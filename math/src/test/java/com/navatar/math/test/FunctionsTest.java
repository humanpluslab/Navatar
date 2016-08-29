/** Contains unit tests for the Functions class methods. */
package com.navatar.math.test;

import java.util.Arrays;

import com.navatar.math.Functions;

import junit.framework.TestCase;

/** Holds the unit tests for the static methods of Distance class. */
public class FunctionsTest extends TestCase {

  /** Tests the correct results of the sigmoid function for various input values. */
  public void testSigmoid() {
    Iterable<Object[]> testData =
        Arrays.asList(new Object[][] {
            { "The value of sigmoid for input 1 is 0.7310585786", 0.7310585786, 1.0 },
            { "The value of sigmoid for input 0 is 0.5", 0.5, 0.0 },
            { "The value of sigmoid for input -6 is 0.002472623", 0.002472623, -6.0 },
            { "The value of sigmoid for input 6 is 0.997527377", 0.997527377, 6.0 } });
    for (Object[] data : testData) {
      assertEquals((String) data[0], (Double) data[1], Functions.sigmoid((Double) data[2]),
          0.000001);
    }
  }

  /**
   * Tests the correct result probability of the gaussian function with mean 0 and standard
   * deviation 1.
   */
  public void testGaussian0Mean1StDev() {
    Iterable<Object[]> testData =
        Arrays
            .asList(new Object[][] {
                {
                    "The value of gaussian with mean 0 and standard deviation 1 for input 0 is 0.39894228",
                    0.39894228, 0.0, 0.0, 1.0 },
                {
                    "The value of gaussian with mean 0 and standard deviation 1 for input 1 is 0.241970725",
                    0.241970725, 1.0, 0.0, 1.0 },
                {
                    "The value of gaussian with mean 0 and standard deviation 1 for input -1 is 0.241970725",
                    0.241970725, -1.0, 0.0, 1.0 },
                {
                    "The value of gaussian with mean 0 and standard deviation 1 for input 2 is 0.053990967",
                    0.053990967, 2.0, 0.0, 1.0 },
                {
                    "The value of gaussian with mean 0 and standard deviation 1 for input -2 is 0.053990967",
                    0.053990967, -2.0, 0.0, 1.0 }, });
    for (Object[] data : testData) {
      assertEquals((String) data[0], (Double) data[1],
          Functions.gaussian((Double) data[2], (Double) data[3], (Double) data[4]), 0.000001);
    }
  }

  /**
   * Tests the correct result probability of the gaussian function with mean 1 and standard
   * deviation 2.
   */
  public void testGaussian1Mean2StDev() {
    Iterable<Object[]> testData =
        Arrays
            .asList(new Object[][] {
                {
                    "The value of gaussian with mean 1 and standard deviation 2 for input 0 is 0.176032663",
                    0.176032663, 0.0, 1.0, 2.0 },
                {
                    "The value of gaussian with mean 1 and standard deviation 2 for input 1 is 0.19947114",
                    0.19947114, 1.0, 1.0, 2.0 },
                {
                    "The value of gaussian with mean 1 and standard deviation 2 for input -1 is 0.120985362",
                    0.120985362, -1.0, 1.0, 2.0 },
                {
                    "The value of gaussian with mean 1 and standard deviation 2 for input 2 is 0.176032663",
                    0.176032663, 2.0, 1.0, 2.0 },
                {
                    "The value of gaussian with mean 1 and standard deviation 2 for input -2 is 0.064758798",
                    0.064758798, -2.0, 1.0, 2.0 }, });
    for (Object[] data : testData) {
      assertEquals((String) data[0], (Double) data[1],
          Functions.gaussian((Double) data[2], (Double) data[3], (Double) data[4]), 0.000001);
    }
  }

}
