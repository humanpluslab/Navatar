package com.navatar.maps.particles;

import java.util.Random;
import java.util.Vector;

public class KMeans<T extends State> {
  // TODO(ilapost): Consider passing these constants as parameters to the KMeans constructor.
  private double maxDiameter;
  private double convergenceThreshold;
  private Vector<Cluster<T>> clusters;

  public KMeans(int numberOfClusters, double convergenceThreshold, double maxDiameter) {
    this.convergenceThreshold = convergenceThreshold;
    this.maxDiameter = maxDiameter;
    clusters = new Vector<Cluster<T>>();
    for (int i = 0; i < numberOfClusters; ++i)
      clusters.add(new Cluster<T>());
  }

  public Vector<Cluster<T>> calculateClusters(Vector<T> states) {
    int numOfStates = states.size();
    Random rand = new Random();
    for (Cluster<T> cluster : clusters)
      cluster.setMean(states.get(rand.nextInt(numOfStates)));
    for (int converged = 0; converged < clusters.size();) {
      converged = 0;
      Cluster<T> closestCluster = null;
      for (Cluster<T> cluster : clusters)
        cluster.clear();
      for (T state : states) {
        double minDistance = Double.MAX_VALUE;
        for (Cluster<T> cluster : clusters) {
          double currentDistance = state.squareDistance(cluster.getMean());
          if (minDistance > currentDistance) {
            minDistance = currentDistance;
            closestCluster = cluster;
          }
        }
        closestCluster.addState(state);
      }
      for (Cluster<T> cluster : clusters) {
        if (cluster.size() > 0) {
          if (cluster.calculateMean() < convergenceThreshold) {
            double currentRadius = cluster.radius();
            closestCluster = closestCluster(cluster);
            // This means that the cluster's diameter is larger than MAX_DIAMETER and needs to be
            // split in two clusters.
            if (currentRadius > maxDiameter / 2) {
              clusters.add(new Cluster<T>(cluster.farthestState()));
              break;
              // This means that the 2 clusters can fit in one with diameter not larger than
              // MAX_DIAMETER
            } else if (closestCluster != null
                && closestCluster.hasConverged()
                && (closestCluster.radius() + currentRadius + cluster.distance(closestCluster) <= maxDiameter)) {
              clusters.remove(closestCluster);
              if (closestCluster.hasConverged())
                --converged;
              break;
            } else {
              cluster.setConverged(true);
              ++converged;
            }
          }
        } else {
          clusters.remove(cluster);
          break;
        }
      }
    }
    return clusters;
  }

  /**
   * Returns the cluster which mean is closer to the mean of the cluster passed as a parameter. This
   * function does not return the same cluster. If there is no other cluster in the KMeans class,
   * this method returns null.
   * 
   * @param cluster
   *          The cluster for which we search the closest other cluster.
   * @return The closest cluster to the cluster parameter. The parameter and return value is never
   *         the same. If there is no other cluster, it returns null.
   */
  private Cluster<T> closestCluster(Cluster<T> cluster) {
    double shortestDistance = Double.MAX_VALUE, distance;
    Cluster<T> closestCluster = null;
    if (clusters.size() <= 1)
      return null;
    for (Cluster<T> currentCluster : clusters) {
      distance = cluster.squareDistance(currentCluster);
      if (shortestDistance > distance && distance > 0.00001) {
        shortestDistance = distance;
        closestCluster = currentCluster;
      }
    }
    return closestCluster;
  }
}
