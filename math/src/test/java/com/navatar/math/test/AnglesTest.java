/** Tests the correct functionality of static methods in the Angles class. */
package com.navatar.math.test;

import java.util.Arrays;

import com.navatar.math.Angles;

import junit.framework.TestCase;

/**
 * @author Ilias Apostolopoulos Contains the unit tests for the Angles class.
 */
public class AnglesTest extends TestCase {

  /** Tests correct results of average static method. */
  public void testCorrectAverage() {
    assertEquals("Average of angles 0 and 90 should be 45", 45.0,
        Angles.average(new double[] { 0.0, 90.0 }), 0.000001);
    assertEquals("Average of angles 90 and 0 should be 45", 45.0,
        Angles.average(new double[] { 90.0, 0.0 }), 0.000001);
    assertEquals("Average of angles 10 and 13 should be 11.5", 11.5,
        Angles.average(new double[] { 13.0, 10.0 }), 0.000001);
    assertEquals("Average of angles 0, 90, and 180 should be 90", 90.0,
        Angles.average(new double[] { 0.0, 90.0, 180.0 }), 0.000001);
    assertEquals("Average of angles 90, 180, and 0 should be 90", 90.0,
        Angles.average(new double[] { 90.0, 180.0, 0.0 }), 0.000001);
    assertEquals("Average of angles -90, 90, and 0 should be 0", 0.0,
        Angles.average(new double[] { 0.0, 90.0, -90.0 }), 0.000001);
    assertEquals("Average of angles 270, 90, and 0 should be 0", 0.0,
        Angles.average(new double[] { 0.0, 90.0, 270.0 }), 0.000001);
    assertEquals("Average of angles 0, 45, and 90 should be 45", 45.0,
        Angles.average(new double[] { 0.0, 45.0, 90.0 }), 0.000001);
    assertEquals("Average of angles 200, 210, and 220 should be 210", 210.0,
        Angles.average(new double[] { 200.0, 210.0, 220.0 }), 0.000001);
  }

  /** Tests correct results of average static method when one angle is provided. */
  public void testOneAngleAverage() {
    assertEquals("Average of angle 90 should be 90", 90.0, Angles.average(new double[] { 90.0 }),
        0.000001);
    assertEquals("Average of angle 180 should be 180", 180.0,
        Angles.average(new double[] { 180.0 }), 0.000001);
    assertEquals("Average of angle 179 should be 179", 179.0,
        Angles.average(new double[] { 179.0 }), 0.000001);
    assertEquals("Average of angle -90 should be 270", 270.0,
        Angles.average(new double[] { -90.0 }), 0.000001);
  }

  /** Tests correct results of average static method when the angles are close to wrapping around. */
  public void testWrapAroundAngleAverage() {
    assertEquals("Average of angle 1 and 359 should be 0", 0.0,
        Angles.average(new double[] { 1.0, 359.0 }), 0.000001);
    assertEquals("Average of angle 179 and 181 should be 180", 180.0,
        Angles.average(new double[] { 179.0, 181.0 }), 0.000001);
  }

  /** Tests correct results of average static method when no angle is provided. */
  public void testSameAngleAverage() {
    assertEquals("Average of angles 90 and 90 should be 90", 90.0,
        Angles.average(new double[] { 90.0, 90.0 }), 0.000001);
    assertEquals("Average of angles 54, 54, and 54 should be 54", 54.0,
        Angles.average(new double[] { 54.0, 54.0, 54.0 }), 0.000001);
    assertEquals("Average of angles 180 and -180 should be 180", 180.0,
        Angles.average(new double[] { 180.0, -180.0 }), 0.000001);
  }

  /** Tests correct results of weightedAverage static method for two angles. */
  public void testTwoAnglesWeightedAverage() {
    assertEquals("Weighted average of angles 0 and 90 should be 45", 45.0,
        Angles.weightedAverage(new double[] { 0.0, 90.0 }, new double[] { 1.0, 1.0 }), 0.000001);
    assertEquals("Weighted average of angles 0 and 90 should be 60", 60.0,
        Angles.weightedAverage(new double[] { 0.0, 90.0 }, new double[] { 1.0, 2.0 }), 0.000001);
    assertEquals("Weighted average of angles 90 and 0 should be 60", 60.0,
        Angles.weightedAverage(new double[] { 90.0, 0.0 }, new double[] { 2.0, 1.0 }), 0.000001);
    assertEquals("Weighted average of angles 0 and 90 should be 90", 90.0,
        Angles.weightedAverage(new double[] { 0.0, 90.0 }, new double[] { 0.0, 1.0 }), 0.000001);
    assertEquals("Weighted average of angles 0 and 90 should be 30", 30.0,
        Angles.weightedAverage(new double[] { 0.0, 90.0 }, new double[] { 0.66, 0.33 }), 0.000001);
  }

  /** Tests correct results of weightedAverage static method for three angles. */
  public void testThreeAnglesWeightedAverage() {
    assertEquals("Weighted average of angles 0, 45, and 90 should be 45", 45.0,
        Angles.weightedAverage(new double[] { 0.0, 45.0, 90.0 }, new double[] { 1.0, 1.0, 1.0 }),
        0.000001);
    assertEquals("Weighted average of angles 0, 45, and 90 should be 60", 60.0,
        Angles.weightedAverage(new double[] { 0.0, 45.0, 90.0 }, new double[] { 1.0, 2.0, 3.0 }),
        0.000001);
    assertEquals("Weighted average of angles 0, 45, and 90 should be 60", 52.5,
        Angles.weightedAverage(new double[] { 0.0, 45.0, 90.0 }, new double[] { 1.0, 3.0, 2.0 }),
        0.000001);
    assertEquals("Weighted average of angles 10, -10, and 20 should be 360", 360.0,
        Angles.weightedAverage(new double[] { 10.0, -10.0, 20.0 }, new double[] { 1.0, 5.0, 2.0 }),
        0.000001);
    assertEquals("Weighted average of angles 1, 90, and 359 should be 0", 0.0,
        Angles.weightedAverage(new double[] { 1.0, 90.0, 359.0 }, new double[] { 1.0, 0.0, 1.0 }),
        0.000001);
  }

  /** Tests correct results of weightedAverage static method when all weights are zero. */
  public void testZeroWeightsWeightedAverage() {
    assertEquals("Weighted average of angles 0, 45, and 90 should be 0", 0.0,
        Angles.weightedAverage(new double[] { 0.0, 45.0, 90.0 }, new double[] { 0.0, 0.0, 0.0 }),
        0.000001);
  }

  public void testDiscretizeAngle() {
    Iterable<Object[]> testData =
        Arrays.asList(new Object[][] { { "Discrete angle of 0 is 0", 0, 0.0 },
            { "Discrete angle of 22.5 is 0", 0, 22.5 }, { "Discrete angle of 45 is 45", 45, 45.0 },
            { "Discrete angle of 67.5 is 45", 45, 67.5 },
            { "Discrete angle of 90 is 90", 90, 90.0 },
            { "Discrete angle of 112.5 is 90", 90, 112.5 },
            { "Discrete angle of 135 is 135", 135, 135.0 },
            { "Discrete angle of 157.5 is 135", 135, 157.5 },
            { "Discrete angle of 180 is 180", 180, 180.0 },
            { "Discrete angle of 202.5 is 180", 180, 202.5 },
            { "Discrete angle of 225 is 225", 225, 225.0 },
            { "Discrete angle of 247.5 is 225", 225, 247.5 },
            { "Discrete angle of 270 is 270", 270, 270.0 },
            { "Discrete angle of 292.5 is 270", 270, 292.5 },
            { "Discrete angle of 315 is 315", 315, 315.0 },
            { "Discrete angle of 337.5 is 315", 315, 337.5 },
            { "Discrete angle of 360 is 0", 0, 360.0 } });
    for (Object[] data : testData) {
      assertEquals((String) data[0], (Integer) data[1],
          (Integer) Angles.discretizeAngle((Double) data[2]));
    }
  }

  /** Tests that normalizeAngle function returns correct results for in range values. */
  public void testInRangeNormalizeAngle() {
    assertEquals("Normalized angle of 0 is 0", 0.0, Angles.normalizeAngle(0.0), 0.000001);
    assertEquals("Normalized angle of 90 is 90", 90.0, Angles.normalizeAngle(90.0), 0.000001);
    assertEquals("Normalized angle of 359 is 359", 359.0, Angles.normalizeAngle(359.0), 0.000001);
  }

  /** Tests that normalizeAngle function returns correct results for negative values. */
  public void testNegativeNormalizeAngle() {
    assertEquals("Normalized angle of -1 is 359", 359.0, Angles.normalizeAngle(-1.0), 0.000001);
    assertEquals("Normalized angle of -90 is 270", 270.0, Angles.normalizeAngle(-90.0), 0.000001);
    assertEquals("Normalized angle of -360 is 0", 0.0, Angles.normalizeAngle(-360.0), 0.000001);
  }

  /** Tests that normalizeAngle function returns correct results for values greater or equal to 360. */
  public void testGreaterThanRangeNormalizeAngle() {
    assertEquals("Normalized angle of 360 is 0", 0.0, Angles.normalizeAngle(360.0), 0.000001);
    assertEquals("Normalized angle of 540 is 180", 180.0, Angles.normalizeAngle(180.0), 0.000001);
    assertEquals("Normalized angle of 720 is 0", 0.0, Angles.normalizeAngle(720.0), 0.000001);
  }

  /** Tests that compassToPolar transforms compass angles correctly to polar */
  public void testCompassToPolar() {
    assertEquals("Compass angle 0 is 90 in polar", 90.0, Angles.compassToPolar(0.0), 0.000001);
    assertEquals("Compass angle 90 is 0 in polar", 0.0, Angles.compassToPolar(90.0), 0.000001);
    assertEquals("Compass angle 120 is 330 in polar", 330.0, Angles.compassToPolar(120.0), 0.000001);
  }

  /** Tests that polarToScreen transforms polar angles correctly to screen */
  public void testPolarToScreen() {
    assertEquals("Polar angle 0 is 0 in screen", 0.0, Angles.polarToScreen(0.0), 0.000001);
    assertEquals("Polar angle 90 is 270 in screen", 270.0, Angles.polarToScreen(90.0), 0.000001);
    assertEquals("Polar angle 270 is 90 in screen", 90.0, Angles.polarToScreen(270.0), 0.000001);
    assertEquals("Polar angle 180 is 180 in screen", 180.0, Angles.polarToScreen(180.0), 0.000001);
  }

  /** Tests that compassToScreen transforms compass angles correctly to screen */
  public void testCompassToScreen() {
    assertEquals("Compass angle 0 is 270 in screen", 270.0, Angles.compassToScreen(0.0), 0.000001);
    assertEquals("Compass angle 90 is 0 in screen", 0.0, Angles.compassToScreen(90.0), 0.000001);
    assertEquals("Compass angle 180 is 90 in screen", 90.0, Angles.compassToScreen(180.0), 0.000001);
    assertEquals("Compass angle 270 is 180 in screen", 180.0, Angles.compassToScreen(270.0),
        0.000001);
  }

}
