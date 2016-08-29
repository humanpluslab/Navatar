/**
 * Contains the class Cluster used in the KMeans algorithm.
 */
package com.navatar.maps.particles;

import java.util.Vector;

/**
 * Cluster contains information about the states held in it, its radius and mean value.
 * 
 * @author Ilias Apostolopoulos
 *
 */
public class Cluster<T extends State> implements Comparable<Cluster<T>> {
  /** The vector storing the states. */
  private Vector<T> states;
  /** The mean(centroid) of the cluster. */
  private T mean;
  /** The radius of the cluster. */
  private double radius;
  /** True if the cluster has converged. */
  private boolean converged;
  /** The farthest state from the current mean. */
  private T farthestState;

  public Cluster() {
    this.states = new Vector<T>();
    mean = null;
    radius = 0.0;
    converged = false;
  }

  /**
   * Class constructor.
   * 
   * @param mean
   *          The mean value of the cluster.
   */
  public Cluster(T mean) {
    this.states = new Vector<T>();
    this.mean = mean;
    this.radius = 0.0;
    this.converged = false;
  }

  public void setMean(T mean) {
    this.mean = mean;
  }

  public T getMean() {
    return this.mean;
  }

  /**
   * Returns the number of states assigned in this cluster.
   * 
   * @return The number of states in this cluster.
   */
  public int size() {
    return this.states.size();
  }

  public Vector<T> states() {
    return this.states;
  }

  public double radius() {
    if (this.radius == 0.0)
      calculateRadius();
    return this.radius;
  }

  public void setConverged(boolean converged) {
    this.converged = converged;
  }

  public boolean hasConverged() {
    return this.converged;
  }

  public T farthestState() {
    return this.farthestState;
  }

  /**
   * Calculates the distance of the parameter state to the mean of this cluster.
   * 
   * @param state
   *          The state to count the distance from.
   * @return The distance between the cluster's mean and the given state.
   */
  public double distance(T state) {
    return this.mean.distance(state);
  }

  /**
   * Calculates the square distance of the mean of the parameter cluster to the mean of this
   * 
   * @param state
   *          The state to count the square distance from.
   * @return The square distance between the cluster's mean and the given state.
   */
  public double squareDistance(T state) {
    return this.mean.distance(state);
  }

  /**
   * Calculates the distance of the mean of the parameter cluster to the mean of this cluster.
   * 
   * @param cluster
   *          The cluster to count the distance from this cluster.
   * @return The distance between the two clusters.
   */
  public double distance(Cluster<T> cluster) {
    return this.mean.distance(cluster.mean);
  }

  /**
   * Calculates the square distance of the parameter state to the mean of the cluster.
   * 
   * @param state
   *          The state to count the square distance from.
   * @return The square distance between the cluster's mean and the given state.
   */
  public double squareDistance(Cluster<T> cluster) {
    return this.mean.distance(cluster.mean);
  }

  /**
   * Adds a new state into the cluster.
   * 
   * @param state
   *          The state to be added.
   */
  public void addState(T state) {
    this.states.add(state);
  }

  /**
   * Clears all the states from the cluster but keeps the old mean.
   */
  public void clear() {
    this.states.clear();
    this.radius = 0.0;
    this.converged = false;
    this.farthestState = null;
  }

  /**
   * Calculates the mean(centroid) of the cluster based on the states that have been assigned to the
   * cluster. After it calculates the mean, it replaces it in the cluster and returns the distance
   * between the previous mean and the new one. Keep in mind that after adding new states, the mean
   * currently stored will not represent the new states. This method has to be called before getting
   * the cluster's mean if you want an updated value.
   * 
   * @return The distance between the new mean and the old one.
   */
  public double calculateMean() {
    @SuppressWarnings("unchecked")
    T newMean = (T) states.firstElement().clone();
    int size = states.size();
    for (int i = 1; i < size; ++i)
      newMean.add(states.get(i));
    newMean.divideBy(size);
    double distance = newMean.distance(mean);
    this.mean = newMean;
    return distance;
  }

  private void calculateRadius() {
    // TODO(ilapost): Create a generic find min/max function and use that instead of writing the
    // same code.
    double maxDistance = 0.0;
    double currentDistance;
    for (T state : states) {
      currentDistance = this.mean.distance(state);
      if (currentDistance > maxDistance) {
        maxDistance = currentDistance;
        this.farthestState = state;
      }
    }
    this.radius = maxDistance;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Comparable#compareTo(java.lang.Object)
   */
  @Override
  public int compareTo(Cluster<T> cluster) {
    return this.states.size() - cluster.states.size();
  }
}
