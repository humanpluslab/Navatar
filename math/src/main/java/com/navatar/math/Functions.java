/** Contains Functions class. */
package com.navatar.math;

import java.security.InvalidParameterException;

/** Contains static methods which represent common mathematical functions. */
public class Functions {

  /**
   * Represents the s-shaped mathematical function.
   * 
   * @param x
   *          The input value/x coordinate of the function.
   * @return The corresponding point on the y axis for the input x value.
   */
  public static double sigmoid(double x) {
    return 1 / (1 + Math.exp(-x));
  }

  /**
   * Represents the normalized gaussian mathematical function.
   * 
   * @param x
   *          The input value/x coordinate of the function.
   * @param mean
   *          The mean value of the gaussian distribution.
   * @param stDev
   *          The standard deviation of the gaussian distribution. This value should be positive.
   * @return The y value which represents the probability for the input value x.
   * @throws InvalidParameterException
   *           if standard deviation is not positive.
   */
  public static double gaussian(double x, double mean, double stDev)
      throws InvalidParameterException {
    if (stDev <= 0)
      throw new InvalidParameterException();
    final double denominator = Math.sqrt(2 * Math.PI);
    double numerator = x - mean;
    return Math.exp(-(numerator * numerator) / (2 * stDev * stDev)) / (stDev * denominator);
  }
}
