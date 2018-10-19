/** Contains the A* algorithm for finding the shortest path in a 2D environment. **/
package com.navatar.pathplanning;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Stack;

import com.navatar.maps.BuildingMapWrapper;
import com.navatar.maps.LandmarkWrapper;
import com.navatar.maps.particles.ParticleState;
import com.navatar.math.Distance;
import com.navatar.protobufs.LandmarkProto.Landmark;
import com.navatar.protobufs.LandmarkProto.Landmark.LandmarkType;

/** Implements the A* algorithm for finding the optimum path in a 2D space. **/
public class AStar {
  /** The map of the environment to search for a path. **/
  private BuildingMapWrapper map;

  public AStar(BuildingMapWrapper map) {
    this.map = map;
  }

  /**
   * A single node in the search graph.
   */
  public class Node implements Comparable<Node> {
    /** The landmark of the node */
    private Landmark landmark;
    /** The state of the node */
    private ParticleState state;
    /** The path cost for this node */
    private double g;
    /** The heuristic cost of this node */
    private double h;
    /** The node from which the algorithm reached this one */
    private Node cameFrom;

    public Node(ParticleState state, Landmark landmark) {
      this.state = state;
      this.landmark = landmark;
    }

    /**
     * Set the parent of this node
     * 
     * @param cameFrom
     *          The parent node which lead us to this node.
     * @return The depth we have no reached in searching.
     */
    public void setCameFrom(Node cameFrom) {
      this.cameFrom = cameFrom;
    }

    public Node getCameFrom() {
      return cameFrom;
    }

    /**
     * @see Comparable#compareTo(Object)
     */
    public int compareTo(Node other) {
      double f = h + g;
      double of = other.h + other.g;
      if (f < of)
        return -1;
      else if (f > of)
        return 1;
      else
        return 0;
    }
  }

  public Path findPath(ParticleState startState, Landmark start, ParticleState endState,
      Landmark goal) {
    Map<Landmark, Node> closed = new HashMap<Landmark, Node>();
    Map<Landmark, Node> openMap = new HashMap<Landmark, Node>();
    PriorityQueue<Node> openQueue = new PriorityQueue<Node>();
    Node startNode = new Node(startState, start);
    startNode.g = 0;
    startNode.cameFrom = null;
    openMap.put(startNode.landmark, startNode);
    openQueue.add(startNode);

    Node current;
    while (!openMap.isEmpty()) {
      current = openQueue.poll();
      if (current.landmark.getName().equals(goal.getName()))
        return reconstructPath(current);
      openMap.remove(current.landmark);
      closed.put(current.landmark, current);
      for (Node neighbor : neighbors(current)) {
        if (closed.get(neighbor.landmark) == null) {
          double nextStepCost = current.g + Distance.euclidean(current.state.getX(),
              current.state.getY(), neighbor.state.getX(), neighbor.state.getY());
          if (openMap.get(neighbor.landmark) == null || nextStepCost < neighbor.g) {
            neighbor.setCameFrom(current);
            neighbor.g = nextStepCost;
            neighbor.h = neighbor.g + heuristicCost(neighbor.state, endState);
            if (openMap.get(neighbor.landmark) == null) {
              openMap.put(neighbor.landmark, neighbor);
              openQueue.add(neighbor);
            }
          }
        }
      }
    }
    return null;
  }

  /**
   * Get the heuristic cost for the given location. This determines in which order the locations are
   * processed.
   * 
   * @param currentState
   *          The current state to use for calculating the heuristic.
   * @param endState
   *          The goal state we want to reach.
   * 
   * @return The heuristic cost assigned to the tile
   */
  private double heuristicCost(ParticleState currentState, ParticleState endState) {
    return Distance.euclidean(currentState.getX(), currentState.getY(), endState.getX(),
        endState.getY());
  }

  /**
   * Returns all the neighbors from the given node.
   * 
   * @param node
   * @return
   */
  private LinkedList<Node> neighbors(Node node) {
    LinkedList<Node> neighbors = new LinkedList<Node>();
    Map<LandmarkType, List<LandmarkWrapper>> landmarks = map.getLandmarks(node.state);
    for (List<LandmarkWrapper> landmarkGroup : landmarks.values()) {
      for (LandmarkWrapper landmark : landmarkGroup) {
        Node neighbor = new Node(
            new ParticleState(0, landmark.getLandmark().getLocation().getX(),
                landmark.getLandmark().getLocation().getY(), node.state.getFloor()),
            landmark.getLandmark());
        neighbors.add(neighbor);
      }
    }
    return neighbors;
  }

  /**
   * Reconstructs the path from the goal node. The algorithm creates first an inverted path by
   * backtracking from the goal until it reaches the starting node. It then inverts this path to
   * create the normal path which will be used for navigation.
   * 
   * @param goal
   *          The goal node to use in order to recalculate the path.
   * @return The reconstructed path found by A*.
   */
  private Path reconstructPath(Node goal) {
    Stack<Node> reversePath;
    Node current = goal;
    reversePath = new Stack<Node>();
    while (current != null) {
      reversePath.add(current);
      current = current.cameFrom;
    }
    Path path = new Path();
    while (!reversePath.empty()) {
      current = reversePath.pop();
      path.add(new Step(current.landmark, current.state));
    }
    return path;
  }
}
