/**
 * 
 */
package com.navatar.particlefilter;

import com.navatar.protobufs.LandmarkProto.Landmark.LandmarkType;

/**
 * @author Ilias Apostolopoulos
 *
 */
public class Transition {
  private LandmarkType landmarkType;
  private int direction;
  private double dirV;
  private long time;
  private boolean left;
  private int step;

  public Transition() {
    this.direction = 0;
    this.step = 0;
    this.time = 0;
    this.dirV = 0;
    this.left = false;
  }

  public Transition(int direction, int step, long time, double dirV, LandmarkType type,
      boolean left) {
    this.direction = direction;
    this.step = step;
    this.time = time;
    this.dirV = dirV;
    this.landmarkType = type;
    this.left = left;
  }

  boolean isLeft() {
    return left;
  }

  void setLeft(boolean left) {
    this.left = left;
  }

  public void setStep(int step) {
    this.step = step;
  }

  public int getStep() {
    return step;
  }

  public void setDirection(int direction) {
    this.direction = direction;
  }

  public int getDirection() {
    return direction;
  }

  public void setTime(long time) {
    this.time = time;
  }

  public long getTime() {
    return time;
  }

  public void setDirV(double dirV) {
    this.dirV = dirV;
  }

  public double getDirV() {
    return dirV;
  }

  public void setLandmarkType(LandmarkType type) {
    this.landmarkType = type;
  }

  public LandmarkType getLandmarkType() {
    return landmarkType;
  }

}