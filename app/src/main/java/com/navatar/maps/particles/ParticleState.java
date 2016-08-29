package com.navatar.maps.particles;

import java.security.InvalidParameterException;

import com.navatar.math.Constants;
import com.navatar.math.Distance;

/* TODO(ilapost): Remove all these constructors and instead provide a builder. */
public class ParticleState implements State {
  private double x, y;
  private int floor;
  private int direction;
  private String time;

  public ParticleState() {
    this.floor = 0;
    this.direction = 0;
    this.x = 0;
    this.y = 0;
    this.time = "";
  }

  public ParticleState(double x, double y) {
    this();
    this.x = x;
    this.y = y;
  }

  public ParticleState(int direction, double x, double y) {
    this(x, y);
    this.direction = direction;
  }

  public ParticleState(int direction, double x, double y, int floor) {
    this(direction, x, y);
    this.floor = floor;
  }

  public ParticleState(int direction, double x, double y, String time) {
    this(direction, x, y);
    this.time = time;
  }

  public ParticleState(int direction, double x, double y, int floor, String time) {
    this(direction, x, y, floor);
    this.time = time;
  }

  public ParticleState(ParticleState state) {
    this(state.direction, state.x, state.y, state.floor, state.time);
  }

  public void setDirection(int direction) {
    this.direction = direction;
  }

  public int getDirection() {
    return direction;
  }

  public void setX(double x) {
    this.x = x;
  }

  public double getX() {
    return x;
  }

  public void setY(double y) {
    this.y = y;
  }

  public double getY() {
    return y;
  }

  public ParticleState clone() {
    return new ParticleState(this.direction, this.x, this.y, this.floor);
  }

  public void setFloor(int floor) {
    this.floor = floor;
  }

  public int getFloor() {
    return floor;
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.navatar.maps.particles.State#distance(com.navatar.maps.particles.State)
   */
  @Override
  public double distance(State state) {
    if (!(state instanceof ParticleState))
      return Double.MAX_VALUE;
    ParticleState particleState = (ParticleState) state;
    return Distance.euclidean(x, y, particleState.x, particleState.y);
  }

  public double distance(double x, double y) {
    return Distance.euclidean(this.x, this.y, x, y);
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.navatar.maps.particles.State#squareDistance(com.navatar.maps.particles.State)
   */
  @Override
  public double squareDistance(State state) {
    if (!(state instanceof ParticleState))
      return Double.MAX_VALUE;
    ParticleState particleState = (ParticleState) state;
    return Distance.squareEuclidean(x, y, particleState.x, particleState.y);
  }

  public void copyTo(ParticleState state) {
    state.direction = this.direction;
    state.x = this.x;
    state.y = this.y;
    state.floor = this.floor;
  }

  /**
   * @return the time
   */
  public String getTime() {
    return time;
  }

  /**
   * @param time
   *          the time to set
   */
  public void setTime(String time) {
    this.time = time;
  }

  /**
   * Adds the members x and y of this ParticleState and the parameter state only if the parameter is
   * a ParticleState instance. If the parameter is not an instance of ParticleState, it throws
   * InvalidParameterException.
   * 
   * @throws InvalidParameterException
   *           If the parameter is not an instance of ParticleState.
   * 
   * @see com.navatar.maps.particles.State#add(com.navatar.maps.particles.State)
   */
  @Override
  public void add(State state) {
    if (!(state instanceof ParticleState))
      throw new InvalidParameterException("Parameter is not an instance of ParticleState.");
    ParticleState particleState = (ParticleState) state;
    this.x += particleState.x;
    this.y += particleState.y;
  }

  /**
   * Divides the members x and y by the value denominator.
   * 
   * @see com.navatar.maps.particles.State#divideBy(int)
   */
  @Override
  public void divideBy(int value) {
    this.x /= value;
    this.y /= value;
  }

  @Override
  public boolean equals(Object state) {
    if (state == null || !(state instanceof ParticleState))
      return false;
    ParticleState s = (ParticleState) state;
    return Math.abs(s.x - this.x) <= Constants.DOUBLE_ACCURACY
        && Math.abs(s.y - this.y) <= Constants.DOUBLE_ACCURACY && s.floor == this.floor;
  }
}