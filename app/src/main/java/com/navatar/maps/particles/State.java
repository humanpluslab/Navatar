/**
 * Contains the interface state, which defines the basic behavior that inherit it.
 */
package com.navatar.maps.particles;

/**
 * @author Ilias Apostolopoulos
 *
 */
public interface State extends Cloneable {

  /**
   * Calculates the distance between this state and the parameter passed to the function.
   * 
   * @param state
   *          The state to which the distance is calculated.
   * @return The distance between the two states.
   */
  public double distance(State state);

  /**
   * Calculates the square distance between this state and the parameter passed to the function.
   * 
   * @param state
   *          The state to which the square distance is calculated.
   * @return The square distance between the two states.
   */
  public double squareDistance(State state);

  /**
   * Adds a state to the current one.
   * 
   * @param state
   *          The state to be added to the current one.
   */
  public void add(State state);

  /**
   * Divides the current state with an integer.
   * 
   * @param value
   *          The integer to be used as a denominator for the division.
   */
  public void divideBy(int value);

  public State clone();
}