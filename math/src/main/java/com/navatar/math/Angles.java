package com.navatar.math;

import java.security.InvalidParameterException;

public class Angles {

  /**
   * Returns the average angle over the angles provided. It averages over the a number of angles
   * provided by size. Averaging angles does not work like averaging numbers. Angles should average
   * to the smallest arc. For example, angles 1 and 359 should average to 0 and not 180.
   * 
   * @param angles
   *          The angles to be averaged. Values can be over 360 or under 0. If the angles array
   *          contains only one value then the function will return this value normalized in the [0,
   *          360) range. If the array is empty this function will return 0.
   * @return The averaged angle. The returned value is within the range [0, 360). If two or more
   *         angles are provided that are symmetrical (e.g. [90, 270], [0, 120, 240], [0, 90, 180,
   *         270]), this function returns unpredictable results. The reason for this is because no
   *         average angle can be defined by symmetrical angles.
   * @throws NullPointerException
   *           if the angles parameter is null.
   * @throws InvalidParameterException
   *           if the angles array is empty.
   */
  public static double average(double angles[])
      throws NullPointerException, InvalidParameterException {
    if (angles == null)
      throw new NullPointerException("Angles array is null.");
    if (angles.length == 0)
      throw new NullPointerException("Angles array is empty.");
    double average_angle = 0;
    final double normalization_offset = 179.0;
    for (int i = 0; i < angles.length; ++i) {
      double diff =
          normalizeAngle(angles[i] + normalization_offset - average_angle) - normalization_offset;
      average_angle = (average_angle * (i + 1) + diff) / (i + 1);
    }
    return normalizeAngle(average_angle);
  }

  /**
   * Returns the weighted average angle. The calculation is similar to the average function above
   * where each angle now is multiplied but its corresponding weight. The result is then divided by
   * the sum of weights.
   * 
   * @param angles
   *          The angles to be averaged.
   * @param weights
   *          The weights to be used for calculating the average.
   * @return The weighted average angle in the range [0, 360). In the special case that all weights
   *         are 0, then the returned weighted average will be zero.
   * @throws NullPointerException
   *           if the angles array or the weights array is null.
   * @throws InvalidParameterException
   *           if the two arrays do not have the same size or if any of the weights provided is
   *           negative.
   */
  public static double weightedAverage(double angles[], double[] weights)
      throws NullPointerException, InvalidParameterException {
    if (angles == null)
      throw new NullPointerException("Angles array is null.");
    if (weights == null)
      throw new NullPointerException("Weights array is null.");
    if (angles.length != weights.length)
      throw new InvalidParameterException("Angles array has " + angles.length
          + " elements while weights array has " + weights.length + " elements.");
    double average_angle = 0;
    double weight = 0;
    /*
     * Used to transform angles over 180 degrees to their negative equivalent.
     */
    final double normalization_offset = 179.0;
    for (int i = 0; i < angles.length; ++i) {
      if (weights[i] < 0)
        throw new InvalidParameterException("Negative weight was provided");
      double diff =
          (normalizeAngle(angles[i] + normalization_offset - average_angle) - normalization_offset)
              * weights[i];
      weight += weights[i];
      if (weight > 0)
        average_angle = (average_angle * weight + diff) / weight;
    }
    return normalizeAngle(average_angle);
  }

  // TODO(ilapost): Consider if this is necessary.
  // public static double standardDeviation(double angles[], double averageAngle) {
  // double std = 0;
  // for (int i = 0; i < angles.length; ++i) {
  // std += Math.pow(normalizeAngle(angles[i] + 180 - averageAngle) - 180, 2.0d);
  // }
  // std = Math.sqrt(std / angles.length);
  // return std;
  // }

  /**
   * Discretizes an angle to {0, 45, 90, 135, 180, 225, 270, 315}. If an angle is equidistant from
   * two discrete values, the function will select the lower one.
   * 
   * @param angle
   *          The angle to be discretized.
   * @return The discrete angle closer to the parameter.
   */
  public static int discretizeAngle(double angle) {
    /* Represents the size of the intervals we use to discretize the angles. */
    final int interval = 45;
    /*
     * Represents the cutting point in order to distinguish in which interval each angle belongs to.
     */
    final double offset = 22.4;
    return ((int) normalizeAngle(angle + offset) / interval) * interval;
  }

  /**
   * Normalizes an angle in the [0, 360) range.
   * 
   * @param angle
   *          The angle to be normalized
   * @return The normalized angle.
   */
  public static double normalizeAngle(double angle) {
    if (angle < 0)
      return angle - ((int) ((angle + 1) / 360) - 1) * 360;
    if (angle >= 360)
      return angle - (int) (angle / 360) * 360;
    return angle;
  }

  /**
   * Transforms a compass angle to a polar one. Compass angles start counting from north and
   * increase when moving clockwise. Polar angles start counting from east and increase when moving
   * counterclockwise. In order to transform compass angles to polar, the compass angle has to be
   * inverted by changing sign and adding 90 degrees so as the starting point is east.
   * 
   * @param angle
   *          The compass angle to be transformed.
   * @return The polar equivalent of the compass angle.
   */
  public static double compassToPolar(double angle) {
    return normalizeAngle(90 - angle);
  }

  public static double polarToCompass(double angle) {
    return normalizeAngle(angle - 90);
  }

  /**
   * Transforms a polar angle to a screen one. Polar angles start counting from east and increase
   * when moving counterclockwise. Screen angles have the same starting point but move clockwise. In
   * order to transform polar angles to screen, the polar angle has to be inverted by changing sign.
   * 
   * @param angle
   *          The polar angle to be transformed.
   * @return The screen equivalent of the polar angle.
   */
  public static double polarToScreen(double angle) {
    return normalizeAngle(-angle);
  }

  /**
   * Transforms a compass angle to a screen one. Compass angles start counting from north and
   * increase when moving clockwise. Screen angles also increase when moving clockwise but start
   * counting from east. In order to transform compass angles to screen, the compass angle has to be
   * decreased by 90 degrees so that the starting points match.
   * 
   * @param angle
   *          The compass angle to be transformed.
   * @return The screen equivalent of the compass angle.
   */
  public static double compassToScreen(double angle) {
    return normalizeAngle(angle - 90);
  }
}