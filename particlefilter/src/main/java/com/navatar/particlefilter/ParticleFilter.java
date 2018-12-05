package com.navatar.particlefilter;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Random;
import java.util.Vector;

import com.navatar.maps.BuildingMapWrapper;
import com.navatar.maps.particles.Cluster;
import com.navatar.maps.particles.KMeans;
import com.navatar.maps.particles.Particle;
import com.navatar.maps.particles.ParticleState;
import com.navatar.math.Angles;
import com.navatar.math.Constants;
import com.navatar.math.Distance;
import com.navatar.output.file.TextFile;
import com.navatar.output.file.XmlFile;

import com.navatar.protobufs.LandmarkProto.Landmark;
import com.navatar.protobufs.LandmarkProto.Landmark.LandmarkType;

public class ParticleFilter {
    private static final int NUM_OF_PARTICLE_FILTERS = 10;
    private static final int NUM_OF_PARTICLES = 1000;
    private static final int NUM_OF_PARTICLES_PER_PF = NUM_OF_PARTICLES / NUM_OF_PARTICLE_FILTERS;
    private static final int NUM_OF_CLUSTERS = 3;

    private static final double KMEANS_CONVERGENCE = 0.25;
    private static final double KMEANS_MAX_DIAMETER = 10.0;
    private static final double LANDMARK_RADIUS = 5.0;

    private int run;
    private float staticStepV = (float) 0.1;
    private boolean printData = false, printEstimation = true;

    private XmlFile xml;
    private TextFile logs;

    private LinkedList<Transition> transitions;
    private Particle[][] particles;
    private BuildingMapWrapper map;
    private static Random rand;
    private float[] stepM;

    private ParticleState locationEstimation;
    private double[][] pfEllipses;

    private DecimalFormat twoDForm;

    private float stepMean;
    private float stepMeanDiff;

    private String groundTruthFile;

    public ParticleFilter(BuildingMapWrapper map, ParticleState startingState) {
        rand = new Random();
        this.transitions = new LinkedList<Transition>();
        this.map = map;
        this.particles = new Particle[NUM_OF_PARTICLE_FILTERS][NUM_OF_PARTICLES_PER_PF];
        this.stepM = new float[NUM_OF_PARTICLE_FILTERS];
        stepMean = 1.0f;
        stepMeanDiff = 0.0933f;
        float currMean = stepMean;
        for (int i = 0; i < NUM_OF_PARTICLE_FILTERS; ++i, currMean += 3 * stepMeanDiff)
            stepM[i] = currMean;

        pfEllipses = new double[NUM_OF_PARTICLE_FILTERS][5];
        twoDForm = new DecimalFormat("#0.00000");

        double newX, newY;
        locationEstimation = startingState;
        for (int j = 0; j < NUM_OF_PARTICLE_FILTERS; ++j) {
            for (int i = 0; i < NUM_OF_PARTICLES_PER_PF; ++i) {
                do {
                    newX = startingState.getX() + 2 * rand.nextGaussian();
                    newY = startingState.getY() + 2 * rand.nextGaussian();
                } while (!map.isAccessible(newX, newY, startingState.getFloor()));

                particles[j][i] = Particle.newInstance(
                        new ParticleState(startingState.getDirection(), newX, newY, startingState.getFloor()),
                        2);
            }
        }

    }

    private Transition integrateTransitions(Vector<Transition> currTransitions) {

        int newDisp = 0;
        double angles[] = new double[currTransitions.size()];
        double weights[] = new double[currTransitions.size()];
        Transition newTransition = new Transition(), currTransition;

        for (int i = 0; i < currTransitions.size(); ++i) {
            currTransition = currTransitions.get(i);
            angles[i] = currTransition.getDirection();
            weights[i] = i + 1;
            newDisp += currTransition.getStep();

            if (currTransition.getLandmarkType() != null) {
                newTransition.setLandmarkType(currTransition.getLandmarkType());
                newTransition.setLeft(currTransition.isLeft());
            }
        }

        newTransition.setStep(newDisp);
        newTransition.setDirection(Angles.discretizeAngle(Angles.weightedAverage(angles, weights)));

        return newTransition;
    }

    public void execute() throws IOException {
        Vector<Transition> currTransitions = new Vector<Transition>();
        Transition currTransition = null, integratedTransition;
        ParticleState newState;
        long currTime;
        int i;
        LandmarkType currentLandmark;

        synchronized (particles) {

            while (!transitions.isEmpty()) {
                currTransition = transitions.remove();
                currTransitions.add(currTransition);
                currTime = currTransition.getTime();
                currentLandmark = currTransition.getLandmarkType();

                // Gather all transitions within a second from the first one
                while (!transitions.isEmpty() && transitions.element().getTime() - currTime < 1000000000L) {
                    currTransition = transitions.remove();
                    currTransitions.add(currTransition);
                    if (currTransition.getLandmarkType() != null)
                        currentLandmark = currTransition.getLandmarkType();
                }

                integratedTransition = integrateTransitions(currTransitions);

                for (i = 0; i < NUM_OF_PARTICLE_FILTERS; ++i) {
                    for (Particle particle : particles[i]) {
                        newState = transitionModel(particle, integratedTransition, stepM[i]);
                        if (map.pathBlocked(particle.getLastState(), newState))
                            particle.setWeight(0.0);
                        else
                            particle.setWeight(observationModel(particle, newState,
                                    integratedTransition.getLandmarkType(), integratedTransition.isLeft()));
                        particle.addState(newState);
                    }
                }
                currTransitions.clear();
                sampling(currentLandmark);

                // locationEstimation = kmeans.greatestCluster(particles);

                if (printData) {
                    xml.append(" <step time=\"" + currTime + "\">\n");
                    for (i = 0; i < NUM_OF_PARTICLE_FILTERS; ++i) {
                        xml.append("  <pf step=\"" + twoDForm.format(stepM[i]) + "\" >\n");
                        for (Particle particle : particles[i])
                            xml.append("   <particle row=\"" + twoDForm.format(particle.getLastState().getY())
                                    + "\" col=\"" + twoDForm.format(particle.getLastState().getX()) + "\" />\n");
                        xml.append("  </pf>\n");
                    }

                    xml.append("\t</step>\n");
                    xml.writeFile(true);
                } else if (printEstimation) {
                    pfEllipses = getPFLocations();
                    xml.append(" <step time=\"" + currTime + "\" meanX=\"" + locationEstimation.getX()
                            + "\" meanY=\"" + locationEstimation.getY() + "\" >\n");
                    for (i = 0; i < NUM_OF_PARTICLE_FILTERS; ++i)
                        xml.append("  <pf step=\"" + twoDForm.format(stepM[i]) + "\" meanX=\""
                                + twoDForm.format(pfEllipses[i][0]) + "\" meanY=\""
                                + twoDForm.format(pfEllipses[i][1]) + "\" covX=\""
                                + twoDForm.format(pfEllipses[i][2]) + "\" covXY=\""
                                + twoDForm.format(pfEllipses[i][3]) + "\" covY=\""
                                + twoDForm.format(pfEllipses[i][4]) + "\" />\n");
                    xml.append(" </step>\n");
                    xml.writeFile(true);
                }
            }
        }
    }

    private ParticleState transitionModel(Particle particle, Transition integratedTransition,
                                          float stepM) {
        int steps = integratedTransition.getStep();
        ParticleState prevState = particle.getLastState();
        double choice = rand.nextDouble(), newDisp = 0, toRadians;
        int newDir;
        if (steps > 0) {
            for (int i = 0; i < steps; ++i) {
                if (choice > 0.01)
                    newDisp += rand.nextGaussian() * stepMeanDiff + integratedTransition.getStep() * stepM;
                else
                    newDisp += rand.nextGaussian() * staticStepV;
            }
        } else {
            if (choice > 0.2)
                newDisp = (float) rand.nextGaussian() * staticStepV;
            else
                newDisp =
                        (float) rand.nextGaussian() * stepMeanDiff + integratedTransition.getStep() * stepM;
        }

        if (newDisp < 0)
            newDisp = 0;
        // newDir = rand.nextGaussian()*dirV+integratedTransition.getDirection();
        newDir = pickBiasedOrientation(integratedTransition.getDirection());
        toRadians = Math.toRadians(newDir);
        return new ParticleState(newDir, prevState.getX() + newDisp * Math.cos(toRadians),
                prevState.getY() + newDisp * Math.sin(toRadians), prevState.getFloor());
    }

    private static int pickBiasedOrientation(int direction) {

        int i;
        double choice = rand.nextDouble(), distr = 0;
        final double[] dirProb = {0.0942998415, 0.811400318, 0.0942998414};

        for (i = 0; i < dirProb.length && choice > (distr += dirProb[i]); ++i)
            ;
        return direction + (i - 1) * 45;
    }

    private double observationModel(Particle particle, ParticleState newState, LandmarkType type,
                                    boolean isLeft) {
        double landmarkAngle, newDirection;
        boolean left;
        if (particle.getWeight() < Constants.DOUBLE_ACCURACY || !map.isAccessible(newState))
            return 0.0;
        if (type == null)
            return 1.0 * particle.getWeight();
        Landmark closestLandmark = map.getClosestLandmark(newState, type);

        if (Distance.euclidean(closestLandmark.getLocation().getX(),
                closestLandmark.getLocation().getY(), newState.getX(),
                newState.getY()) <= LANDMARK_RADIUS) {
            if (type == LandmarkType.HALLWAY_INTERSECTION || type == LandmarkType.STAIRS)
                return 1.0 * particle.getWeight();

            landmarkAngle =
                    Math.toDegrees(Math.atan2(closestLandmark.getLocation().getY() - newState.getY(),
                            closestLandmark.getLocation().getX() - newState.getX()));

            if (landmarkAngle < 0)
                landmarkAngle += 360.0;

            newDirection = newState.getDirection();

            if ((newDirection + 180 < 360) && (landmarkAngle > newDirection)
                    && (landmarkAngle < newDirection + 180))
                left = false;
            else left = !((newDirection + 180 >= 360)
                    && ((landmarkAngle > newDirection) && (landmarkAngle < 360))
                    || ((landmarkAngle >= 0) && (landmarkAngle < newDirection - 180)));

            if (left == isLeft)
                return 1.0 * particle.getWeight();
        }

        return 0.0;
    }

    private void sampling(LandmarkType type) throws IOException {
        ParticleState currState, newState, oldState;
        com.navatar.protobufs.LandmarkProto.Landmark closestLandmark;
        double selection;
        double pdf[] = new double[NUM_OF_PARTICLES_PER_PF];
        double weights[] = new double[NUM_OF_PARTICLE_FILTERS];
        double bestPF = 0.0;
        int i, j, l, bestPFindex = -1;

        // Calculate sums of all particle filters
        for (l = 0; l < NUM_OF_PARTICLE_FILTERS; ++l) {
            for (i = 0; i < NUM_OF_PARTICLES_PER_PF; ++i)
                weights[l] += particles[l][i].getWeight();
            // TODO(ilapost): Make sure it works if we have distance based weights.
            if (weights[l] > bestPF && weights[l] > 0.0001) {
                bestPF = weights[l];
                bestPFindex = l;
            }
        }
        // Sample for each particle filter
        for (l = 0; l < NUM_OF_PARTICLE_FILTERS; ++l) {
            // If all particle filters are dead reset them
            if (bestPFindex == -1) {
                logs.append("\nAll particles died!!!\nType of landmark: " + type);
                logs.writeFile(true);
                for (i = 0; i < NUM_OF_PARTICLES_PER_PF; ++i) {
                    currState = particles[l][i].getLastState();
                    if (!map.isAccessible(currState))
                        currState = getRandomAccessibleState(particles[l]);
                    // closestLandmark = map.closestVisibleLandmark(currState.getY(),
                    // currState.getX(), landmark);
                    if (type != null) {
                        closestLandmark = map.getCloseLandmark(currState, type);
                        Particle newParticle = Particle
                                .newInstance(new ParticleState(particles[l][i].getLastState().getDirection(),
                                        closestLandmark.getLocation().getX(), closestLandmark.getLocation().getY(),
                                        particles[l][i].getLastState().getFloor()), 2);
                        // currState.setX(newState.getX());
                        // currState.setY(newState.getY());

                        oldState = particles[l][i].getLastState();
                        newState = newParticle.getLastState();
                        oldState.setDirection(newState.getDirection());
                        oldState.setX(newState.getX());
                        oldState.setY(newState.getY());
                        particles[l][i].setWeight(newParticle.getWeight());
                    } else {
                        oldState = particles[l][i].getLastState();
                        oldState.setDirection(currState.getDirection());
                        oldState.setX(currState.getX());
                        oldState.setY(currState.getY());
                        particles[l][i].setWeight(1.0);
                    }
                }
            }
            // If the current particle filter is dead resample with the best one
            else if (weights[l] < 0.0001) {
                logs.append("\nParticle filter with step " + stepM[l] + " died!!!\n");
                logs.writeFile(true);
                for (i = 0; i < NUM_OF_PARTICLES_PER_PF; ++i)
                    particles[bestPFindex][i].copyTo(particles[l][i]);
                stepM[l] = stepM[bestPFindex] + (float) rand.nextGaussian() * stepMeanDiff;
            }
            // Calculate new pdf
            pdf[0] = particles[l][0].getWeight();
            for (i = 1; i < NUM_OF_PARTICLES_PER_PF; ++i)
                pdf[i] = particles[l][i].getWeight() + pdf[i - 1];
            for (i = 0, j = 0; i < NUM_OF_PARTICLES_PER_PF; ++i) {
                if (particles[l][i].getWeight() < 0.0001) {
                    selection = rand.nextDouble() * pdf[NUM_OF_PARTICLES_PER_PF - 1];
                    for (j = 0; j < NUM_OF_PARTICLES_PER_PF && pdf[j] < selection; ++j)
                        ;
                    particles[l][j].copyTo(particles[l][i]);
                }
            }
        }
        if (bestPFindex == -1) {
            float currMean = stepMean;
            for (i = 0; i < NUM_OF_PARTICLE_FILTERS; ++i, currMean += 3 * stepMeanDiff)
                stepM[i] = currMean;
            logs.append("\n");
            logs.writeFile(true);
        }
    }

    private ParticleState getRandomAccessibleState(Particle[] particles) {
        ParticleState state;

        do {
            state = particles[rand.nextInt(NUM_OF_PARTICLES_PER_PF)].getNewState();
        } while (!map.isAccessible(state));

        return state;
    }

    public void addTransition(Transition transition) {
        transitions.add(transition);
    }

    public void finalize() {

    }

    private double[][] getPFLocations() {

        int i, j;
        ParticleState currState;
        double varX, varY;
        double[][] values = new double[NUM_OF_PARTICLE_FILTERS][5];

        for (i = 0; i < NUM_OF_PARTICLE_FILTERS; ++i) {

            // Calculate mean
            for (j = 0; j < NUM_OF_PARTICLES_PER_PF; ++j) {
                currState = particles[i][j].getLastState();
                values[i][0] += currState.getX();
                values[i][1] += currState.getY();
            }

            values[i][0] /= NUM_OF_PARTICLES_PER_PF;
            values[i][1] /= NUM_OF_PARTICLES_PER_PF;

            // Calculate covariance matrix
            for (j = 0; j < NUM_OF_PARTICLES_PER_PF; ++j) {
                currState = particles[i][j].getLastState();
                varX = currState.getX() - values[i][0];
                varY = currState.getY() - values[i][1];

                values[i][2] += varX * varX;
                values[i][3] += varX * varY;
                values[i][4] += varY * varY;
            }

            values[i][2] /= NUM_OF_PARTICLES_PER_PF;
            values[i][3] /= NUM_OF_PARTICLES_PER_PF;
            values[i][4] /= NUM_OF_PARTICLES_PER_PF;
        }

        return values;
    }

    public ParticleState getSynchronizedLocationEstimate() {
        synchronized (particles) {
            KMeans<ParticleState> kmeans =
                    new KMeans<ParticleState>(NUM_OF_CLUSTERS, KMEANS_CONVERGENCE, KMEANS_MAX_DIAMETER);
            Vector<ParticleState> states = new Vector<ParticleState>();
            for (Particle[] particleFilter : particles) {
                for (Particle particle : particleFilter)
                    states.add(particle.getLastState());
            }
            Vector<Cluster<ParticleState>> clusters = kmeans.calculateClusters(states);
            Cluster<ParticleState> largestCluster = Collections.max(clusters);
            locationEstimation = largestCluster.getMean();
            locationEstimation.setDirection(calculateAngle(largestCluster.states()));
        }
        return locationEstimation;
    }

    private int calculateAngle(Vector<ParticleState> states) {
        // TODO(ilapost): Change Angles.average parameter so it can accept any Java collections class.
        int statesSize = states.size();
        double[] angles = new double[statesSize];
        for (int i = 0; i < statesSize; ++i) {
            angles[i] = states.get(i).getDirection();
        }
        return Angles.discretizeAngle(Angles.average(angles));
    }

    public void changeFloor(int floor, ParticleState transitionState) {
        synchronized (particles) {
            double newX, newY;
            for (int j = 0; j < NUM_OF_PARTICLE_FILTERS; ++j) {
                for (int i = 0; i < NUM_OF_PARTICLES_PER_PF; ++i) {
                    do {
                        newX = transitionState.getX() + 2 * rand.nextGaussian();
                        newY = transitionState.getY() + 2 * rand.nextGaussian();
                    } while (!map.isAccessible((int) (newX + 0.5), (int) (newY + 0.5), floor));
                    particles[j][i]
                            .addState(new ParticleState(transitionState.getDirection(), newX, newY, floor));
                }
            }
        }
    }

    public ParticleState getLocation() {
        return getSynchronizedLocationEstimate();
    }

    public class ExecutePF extends Thread {

        public void run() {
            try {
                execute();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void interrupt() {
        }
    }
}