package com.navatar.maps.particles;

import java.util.Vector;

public class Particle implements Cloneable {
  private int numOfStates;
  private double weight;
  private Vector<ParticleState> path;
  private int size, index;

  private Particle() {}

  public static Particle newInstance() {
    Particle instance = new Particle();
    instance.numOfStates = Integer.MAX_VALUE;
    instance.path = new Vector<ParticleState>();
    instance.size = 0;
    instance.index = -1;
    instance.weight = 1;
    return instance;
  }

  public static Particle newInstance(ParticleState initialState, int numOfStates) {
    Particle instance = new Particle();
    instance.path = new Vector<ParticleState>();
    instance.path.add(initialState);
    if (numOfStates < 1)
      numOfStates = Integer.MAX_VALUE;
    else {
      for (int i = 1; i < numOfStates; ++i)
        instance.path.add(new ParticleState());
    }
    instance.numOfStates = numOfStates;
    instance.size = 1;
    instance.index = 0;
    instance.weight = 1;
    return instance;
  }

  public void setWeight(double weight) {
    this.weight = weight;
  }

  public double getWeight() {
    return weight;
  }

  public void addState(int direction, float x, float y) {
    index = (index + 1) % numOfStates;
    ParticleState state = path.get(index);
    if (state == null) {
      state = new ParticleState(direction, x, y);
      path.add(state);
    } else {
      state.setDirection(direction);
      state.setX(x);
      state.setY(y);
    }
    size = (size + 1 > numOfStates) ? size : size + 1;
  }

  public void addState(ParticleState state) {
    index = (index + 1) % numOfStates;
    path.set(index, state);
    size = (size + 1 > numOfStates) ? size : size + 1;
  }

  public ParticleState getStateAt(int index) {
    if (index < 0 || index >= size)
      return null;
    return path.get(index);
  }

  public ParticleState getLastState() {
    return path.get(index);
  }

  public ParticleState getNewState() {
    return path.get((index + 1) % numOfStates);
  }

  public void setPath(Vector<ParticleState> path) {
    this.path = path;
  }

  public Vector<ParticleState> getPath() {
    return path;
  }

  public int Size() {
    return size;
  }

  public void copyTo(Particle particle) {
    particle.weight = this.weight;
    particle.size = this.size;
    particle.index = this.index;
    particle.numOfStates = this.numOfStates;
    particle.path.clear();
    for (ParticleState state : path) {
      ParticleState newState = new ParticleState();
      state.copyTo(newState);
      particle.path.add(newState);
    }
  }

  public Object clone() throws CloneNotSupportedException {
    Particle cloneP = (Particle) super.clone();
    cloneP.weight = this.weight;
    cloneP.size = this.size;
    cloneP.index = this.index;
    cloneP.path = new Vector<ParticleState>();
    for (ParticleState state : path)
      cloneP.path.add(state.clone());
    return cloneP;
  }

}
