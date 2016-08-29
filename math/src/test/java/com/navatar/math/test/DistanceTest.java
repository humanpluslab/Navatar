/** Contains unit tests for the Distance class methods. */
package com.navatar.math.test;

import java.util.Arrays;

import com.navatar.math.Distance;

import junit.framework.TestCase;

/** Holds the unit tests for the static methods of Distance class. */
public class DistanceTest extends TestCase {

  /**
   * Tests correct results of function euclidean with two points that have different x,y
   * coordinates.
   */
  public void testEuclideanDistanceDifferentPoints() {
    Iterable<Object[]> testData =
        Arrays.asList(new Object[][] {
            { "Euclidean distance of points (1,1) and (2,3) is 2.236067977", 2.236067977, 1.0, 1.0,
                2.0, 3.0 },
            { "Euclidean distance of points (-1,-1) and (2,3) is 5", 5.0, -1.0, -1.0, 2.0, 3.0 },
            { "Euclidean distance of points (-1,-1) and (-2,-3) is 2.236067977", 2.236067977, -1.0,
                -1.0, -2.0, -3.0 },
            { "Euclidean distance of points (1,-1) and (-2,3) is 5", 5.0, 1.0, -1.0, -2.0, 3.0 } });
    for (Object[] data : testData) {
      assertEquals((String) data[0], (Double) data[1], Distance.euclidean((Double) data[2],
          (Double) data[3], (Double) data[4], (Double) data[5]), 0.000001);
    }
  }

  /** Tests correct result of function euclidean with two points that have the same y coordinate. */
  public void testEuclideanDistanceSameY() {
    Iterable<Object[]> testData =
        Arrays
            .asList(new Object[][] {
                { "Euclidean distance of points (1,1) and (2,1) is 1", 1.0, 1.0, 1.0, 2.0, 1.0 },
                { "Euclidean distance of points (-1,-1) and (2,-1) is 3", 3.0, -1.0, -1.0, 2.0,
                    -1.0 },
                { "Euclidean distance of points (-1,-1) and (-2,-1) is 1", 1.0, -1.0, -1.0, -2.0,
                    -1.0 },
                { "Euclidean distance of points (1,-1) and (-2,-1) is 3", 3.0, 1.0, -1.0, -2.0,
                    -1.0 } });
    for (Object[] data : testData) {
      assertEquals((String) data[0], (Double) data[1], Distance.euclidean((Double) data[2],
          (Double) data[3], (Double) data[4], (Double) data[5]), 0.000001);
    }
  }

  /** Tests correct result of function euclidean with two points that have the same x coordinate. */
  public void testEuclideanDistanceSameX() {
    Iterable<Object[]> testData =
        Arrays
            .asList(new Object[][] {
                { "Euclidean distance of points (1,1) and (1,3) is 2", 2.0, 1.0, 1.0, 1.0, 3.0 },
                { "Euclidean distance of points (-1,-1) and (-1,3) is 4", 4.0, -1.0, -1.0, -1.0,
                    3.0 },
                { "Euclidean distance of points (-1,-1) and (-1,-3) is 2", 2.0, -1.0, -1.0, -1.0,
                    -3.0 },
                { "Euclidean distance of points (1,-1) and (1,3) is 4", 4.0, 1.0, -1.0, 1.0, 3.0 } });
    for (Object[] data : testData) {
      assertEquals((String) data[0], (Double) data[1], Distance.euclidean((Double) data[2],
          (Double) data[3], (Double) data[4], (Double) data[5]), 0.000001);
    }
  }

  /** Tests correct result of function euclidean with two points that are equal. */
  public void testEuclideanDistanceSamePoint() {
    Iterable<Object[]> testData =
        Arrays
            .asList(new Object[][] {
                { "Euclidean distance of points (1,1) and (1,1) is 0", 0.0, 1.0, 1.0, 1.0, 1.0 },
                { "Euclidean distance of points (-1,3) and (-1,3) is 0", 0.0, -1.0, 3.0, -1.0, 3.0 },
                { "Euclidean distance of points (-1,-3) and (-1,-3) is 0", 0.0, -1.0, -3.0, -1.0,
                    -3.0 },
                { "Euclidean distance of points (0,0) and (0,0) is 0", 0.0, 0.0, 0.0, 0.0, 0.0 },
                { "Euclidean distance of points (2,3) and (2,3) is 0", 0.0, 2.0, 3.0, 2.0, 3.0 } });
    for (Object[] data : testData) {
      assertEquals((String) data[0], (Double) data[1], Distance.euclidean((Double) data[2],
          (Double) data[3], (Double) data[4], (Double) data[5]), 0.000001);
    }
  }

  /**
   * Tests correct result of function squaredEuclidean with two points that have different x,y
   * coordinates.
   */
  public void testSquaredEuclideanDistanceDifferentPoints() {
    Iterable<Object[]> testData =
        Arrays
            .asList(new Object[][] {
                { "Euclidean distance of points (1,1) and (2,3) is 5", 5.0, 1.0, 1.0, 2.0, 3.0 },
                { "Euclidean distance of points (-1,-1) and (2,3) is 25", 25.0, -1.0, -1.0, 2.0,
                    3.0 },
                { "Euclidean distance of points (-1,-1) and (-2,-3) is 5", 5.0, -1.0, -1.0, -2.0,
                    -3.0 },
                { "Euclidean distance of points (1,-1) and (-2,3) is 25", 25.0, 1.0, -1.0, -2.0,
                    3.0 } });
    for (Object[] data : testData) {
      assertEquals((String) data[0], (Double) data[1], Distance.squareEuclidean((Double) data[2],
          (Double) data[3], (Double) data[4], (Double) data[5]), 0.000001);
    }
  }

  /**
   * Tests correct result of function squaredEuclidean with two points that have the same y
   * coordinate.
   */
  public void testSquaredEuclideanDistanceSameY() {
    Iterable<Object[]> testData =
        Arrays
            .asList(new Object[][] {
                { "Euclidean distance of points (1,1) and (2,1) is 1", 1.0, 1.0, 1.0, 2.0, 1.0 },
                { "Euclidean distance of points (-1,-1) and (2,-1) is 9", 9.0, -1.0, -1.0, 2.0,
                    -1.0 },
                { "Euclidean distance of points (-1,-1) and (-2,-1) is 1", 1.0, -1.0, -1.0, -2.0,
                    -1.0 },
                { "Euclidean distance of points (1,-1) and (-2,-1) is 9", 9.0, 1.0, -1.0, -2.0,
                    -1.0 } });
    for (Object[] data : testData) {
      assertEquals((String) data[0], (Double) data[1], Distance.squareEuclidean((Double) data[2],
          (Double) data[3], (Double) data[4], (Double) data[5]), 0.000001);
    }
  }

  /**
   * Tests correct result of function squaredEuclidean with two points that have the same x
   * coordinate.
   */
  public void testSquaredEuclideanDistanceSameX() {
    Iterable<Object[]> testData =
        Arrays
            .asList(new Object[][] {
                { "Euclidean distance of points (1,1) and (1,3) is 4", 4.0, 1.0, 1.0, 1.0, 3.0 },
                { "Euclidean distance of points (-1,-1) and (-1,3) is 16", 16.0, -1.0, -1.0, -1.0,
                    3.0 },
                { "Euclidean distance of points (-1,-1) and (-1,-3) is 4", 4.0, -1.0, -1.0, -1.0,
                    -3.0 },
                { "Euclidean distance of points (1,-1) and (1,3) is 16", 16.0, 1.0, -1.0, 1.0, 3.0 } });
    for (Object[] data : testData) {
      assertEquals((String) data[0], (Double) data[1], Distance.squareEuclidean((Double) data[2],
          (Double) data[3], (Double) data[4], (Double) data[5]), 0.000001);
    }
  }

  /** Tests correct result of function squaredEuclidean with two points that are equal. */
  public void testSquaredEuclideanDistanceSamePoint() {
    Iterable<Object[]> testData =
        Arrays
            .asList(new Object[][] {
                { "Euclidean distance of points (1,1) and (1,1) is 0", 0.0, 1.0, 1.0, 1.0, 1.0 },
                { "Euclidean distance of points (-1,3) and (-1,3) is 0", 0.0, -1.0, 3.0, -1.0, 3.0 },
                { "Euclidean distance of points (-1,-3) and (-1,-3) is 0", 0.0, -1.0, -3.0, -1.0,
                    -3.0 },
                { "Euclidean distance of points (0,0) and (0,0) is 0", 0.0, 0.0, 0.0, 0.0, 0.0 },
                { "Euclidean distance of points (2,3) and (2,3) is 0", 0.0, 2.0, 3.0, 2.0, 3.0 } });
    for (Object[] data : testData) {
      assertEquals((String) data[0], (Double) data[1], Distance.squareEuclidean((Double) data[2],
          (Double) data[3], (Double) data[4], (Double) data[5]), 0.000001);
    }
  }

  /**
   * Tests correct result of function Manhattan with two points that have different x,y coordinates.
   */
  public void testManhattanDistanceDifferentPoints() {
    Iterable<Object[]> testData =
        Arrays
            .asList(new Object[][] {
                { "Euclidean distance of points (1,1) and (2,3) is 3", 3.0, 1.0, 1.0, 2.0, 3.0 },
                { "Euclidean distance of points (-1,-1) and (2,3) is 7", 7.0, -1.0, -1.0, 2.0, 3.0 },
                { "Euclidean distance of points (-1,-1) and (-2,-3) is 3", 3.0, -1.0, -1.0, -2.0,
                    -3.0 },
                { "Euclidean distance of points (1,-1) and (-2,3) is 7", 7.0, 1.0, -1.0, -2.0, 3.0 } });
    for (Object[] data : testData) {
      assertEquals((String) data[0], (Double) data[1], Distance.manhattan((Double) data[2],
          (Double) data[3], (Double) data[4], (Double) data[5]), 0.000001);
    }
  }

  /**
   * Tests correct result of function Manhattan with two points that have the same y coordinate.
   */
  public void testManhattanDistanceSameY() {
    Iterable<Object[]> testData =
        Arrays
            .asList(new Object[][] {
                { "Euclidean distance of points (1,1) and (2,1) is 1", 1.0, 1.0, 1.0, 2.0, 1.0 },
                { "Euclidean distance of points (-1,-1) and (2,-1) is 3", 3.0, -1.0, -1.0, 2.0,
                    -1.0 },
                { "Euclidean distance of points (-1,-1) and (-2,-1) is 1", 1.0, -1.0, -1.0, -2.0,
                    -1.0 },
                { "Euclidean distance of points (1,-1) and (-2,-1) is 3", 3.0, 1.0, -1.0, -2.0,
                    -1.0 } });
    for (Object[] data : testData) {
      assertEquals((String) data[0], (Double) data[1], Distance.manhattan((Double) data[2],
          (Double) data[3], (Double) data[4], (Double) data[5]), 0.000001);
    }
  }

  /**
   * Tests correct result of function Manhattan with two points that have the same x coordinate.
   */
  public void testManhattanDistanceSameX() {
    Iterable<Object[]> testData =
        Arrays
            .asList(new Object[][] {
                { "Euclidean distance of points (1,1) and (1,3) is 2", 2.0, 1.0, 1.0, 1.0, 3.0 },
                { "Euclidean distance of points (-1,-1) and (-1,3) is 2", 4.0, -1.0, -1.0, -1.0,
                    3.0 },
                { "Euclidean distance of points (-1,-1) and (-1,-3) is 2", 2.0, -1.0, -1.0, -1.0,
                    -3.0 },
                { "Euclidean distance of points (1,-1) and (1,3) is 4", 4.0, 1.0, -1.0, 1.0, 3.0 } });
    for (Object[] data : testData) {
      assertEquals((String) data[0], (Double) data[1], Distance.manhattan((Double) data[2],
          (Double) data[3], (Double) data[4], (Double) data[5]), 0.000001);
    }
  }

  /** Tests correct result of function Manhattan with two points that are equal. */
  public void testManhattanDistanceSamePoint() {
    Iterable<Object[]> testData =
        Arrays
            .asList(new Object[][] {
                { "Euclidean distance of points (1,1) and (1,1) is 0", 0.0, 1.0, 1.0, 1.0, 1.0 },
                { "Euclidean distance of points (-1,3) and (-1,3) is 0", 0.0, -1.0, 3.0, -1.0, 3.0 },
                { "Euclidean distance of points (-1,-3) and (-1,-3) is 0", 0.0, -1.0, -3.0, -1.0,
                    -3.0 },
                { "Euclidean distance of points (0,0) and (0,0) is 0", 0.0, 0.0, 0.0, 0.0, 0.0 },
                { "Euclidean distance of points (2,3) and (2,3) is 0", 0.0, 2.0, 3.0, 2.0, 3.0 } });
    for (Object[] data : testData) {
      assertEquals((String) data[0], (Double) data[1], Distance.manhattan((Double) data[2],
          (Double) data[3], (Double) data[4], (Double) data[5]), 0.000001);
    }
  }
}
