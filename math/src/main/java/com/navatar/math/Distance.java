/** Contains static functions that implement various distance metrics. */
package com.navatar.math;

/** Holds all static distance metric functions */
public class Distance {

  /**
   * Calculates the euclidean distance between the 2D points (x1, y1) and (x2, y2).
   * 
   * @param x1
   *          The x coordinate of the first point.
   * @param y1
   *          The y coordinate of the first point.
   * @param x2
   *          The x coordinate of the second point.
   * @param y2
   *          The y coordinate of the second point.
   * @return The euclidean distance between the points.
   */
  public static double euclidean(double x1, double y1, double x2, double y2) {
    double dx = x2 - x1, dy = y2 - y1;
    return Math.sqrt(dx * dx + dy * dy);
  }

  /**
   * Calculates the squared euclidean distance. This function is useful when a large amount of
   * distance calculations need to be executed and time is essential. The square root calculation
   * has been omitted in order to increase speed. This is useful when comparing squared distances
   * and not when an accurate calculation of the distance is needed.
   * 
   * @param x1
   *          The x coordinate of the first point.
   * @param y1
   *          The y coordinate of the first point.
   * @param x2
   *          The x coordinate of the second point.
   * @param y2
   *          The y coordinate of the second point.
   * @return The squared euclidean distance between the points.
   */
  public static double squareEuclidean(double x1, double y1, double x2, double y2) {
    double dx = x2 - x1, dy = y2 - y1;
    return dx * dx + dy * dy;
  }

  /**
   * Calculates the Manhattan distance. Useful for calculating distance in a grid-like environment
   * where only horizontal and vertical movement is allowed.
   * 
   * @param x1
   *          The x coordinate of the first point.
   * @param y1
   *          The y coordinate of the first point.
   * @param x2
   *          The x coordinate of the second point.
   * @param y2
   *          The y coordinate of the second point.
   * @return The Manhattan distance between the points.
   */
  public static double manhattan(double x1, double y1, double x2, double y2) {
    return Math.abs(x2 - x1) + Math.abs(y2 - y1);
  }
}