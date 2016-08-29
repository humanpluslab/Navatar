package com.navatar.pathplanning;

import java.util.ArrayList;

import com.navatar.maps.particles.ParticleState;

/**
 * A path determined by some path finding algorithm. A series of steps from the starting location to
 * the target location. This includes a landmark for the initial location.
 */
public class Path {
  /** The list of steps building up this path, Steps[0] is where you are at right now. */

  private ArrayList<Step> steps;

  private int floor;

  public int getFloor() {
    return floor;
  }

  public void setFloor(int floor) {
    this.floor = floor;
  }

  /**
   * Create an empty path
   */
  public Path() {
    steps = new ArrayList<Step>();
  }

  /**
   * Get the length of the path, i.e. the number of steps
   * 
   * @return The number of steps in this path
   */
  public int getLength() {
    return steps.size();
  }

  /**
   * Get the landmark at a given index in the path
   * 
   * @param index
   *          The index of the landmark to retrieve. Note this should be >= 0 and < getLength();
   * @return The landmark information, the position on the map.
   */
  public Step getStep(int index) {
    return (index < steps.size()) ? steps.get(index) : null;
  }

  /*
   * 
   * Add a step to the path.
   * 
   */
  public boolean add(Step step) {
    return steps.add(step);
  }

  public double distance(ParticleState particlestate) {
    return distance(particlestate.getX(), particlestate.getY());
  }

  public double distance(double x, double y) {

    double tempDistance = 0;
    double minDistance = Double.MAX_VALUE;
    for (int i = 0; i < steps.size() - 1; ++i) {
      tempDistance = ptLineDist(steps.get(i).getParticleState().getX(),
          steps.get(i).getParticleState().getY(), steps.get(i + 1).getParticleState().getX(),
          steps.get(i + 1).getParticleState().getX(), x, y);
      if (tempDistance < minDistance)
        minDistance = tempDistance;
    }
    return Math.sqrt(minDistance);
  }

  // copied over this function from java.awt.geom.Line2D class
  private double ptLineDist(double x1, double y1, double x2, double y2, double px, double py) {
    // Adjust vectors relative to x1,y1
    // x2,y2 becomes relative vector from x1,y1 to end of segment
    x2 -= x1;
    y2 -= y1;
    // px,py becomes relative vector from x1,y1 to test point
    px -= x1;
    py -= y1;
    double dotprod = px * x2 + py * y2;
    // dotprod is the length of the px,py vector
    // projected on the x1,y1=>x2,y2 vector times the
    // length of the x1,y1=>x2,y2 vector
    double projlenSq = dotprod * dotprod / (x2 * x2 + y2 * y2);
    // Distance to line is now the length of the relative point
    // vector minus the length of its projection onto the line
    double lenSq = px * px + py * py - projlenSq;
    if (lenSq < 0) {
      lenSq = 0;
    }
    return Math.sqrt(lenSq);

  }

}