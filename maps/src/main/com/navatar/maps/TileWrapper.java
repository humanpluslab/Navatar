/**
 * Contains the class TileWrapper which is a wrapper for the Tile protobuf.
 */
package com.navatar.maps;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.navatar.math.Distance;
import com.navatar.protobufs.CoordinatesProto.Coordinates;
import com.navatar.protobufs.LandmarkProto.Landmark;
import com.navatar.protobufs.LandmarkProto.Landmark.LandmarkType;
import com.navatar.protobufs.MinimapProto.Minimap.Tile;

/**
 * Wrapper for the Tile protobuf.
 * 
 * @author ilias
 *
 */
public class TileWrapper {
  /** The original protobuf tile. */
  private Tile tile;
  /** The tile's closest landmarks. */
  private Map<LandmarkType, List<LandmarkWrapper>> landmarks;
  private static final Double MAX_DISTANCE = 20.0;
  private static final Double MAX_DISTANCE_SQUARED = MAX_DISTANCE * MAX_DISTANCE;

  /**
   * Constructs a TileWrapper based on a protobuf tile. The constructor also calculates the weights
   * of the landmarks assigned to the tile. The reason the weights are calculated inversely
   * proportional to the distance from the tile, is because the closest landmarks are more likely to
   * be discovered by a user, and thus, should have a higher weight.
   * 
   * @param tile
   *          The protobuf tile used to construct the TileWrapper.
   * @param landmarks
   *          A list of all landmarks in the specific floor.
   * @param tileSize
   *          The size of the side of the square that constitutes the tile.
   * @param minCoordinates
   *          The origin point of the specific floor the tile is on.
   */
  public TileWrapper(Tile tile, List<Landmark> landmarks, double tileSize,
      Coordinates minCoordinates) {
    this.tile = tile;
    this.landmarks = new HashMap<LandmarkType, List<LandmarkWrapper>>(LandmarkType.values().length);
    for (LandmarkType type : LandmarkType.values())
      this.landmarks.put(type, new LinkedList<LandmarkWrapper>());
    for (int landmarkIndex : tile.getLandmarksList()) {
      Landmark landmark = landmarks.get(landmarkIndex);
      this.landmarks.get(landmark.getType()).add(new LandmarkWrapper(landmark));
    }
    for (List<LandmarkWrapper> landmarkGroup : this.landmarks.values()) {
      double totalDistance = 0.0;
      double[] distances = new double[landmarkGroup.size()];
      int i = 0;
      for (LandmarkWrapper landmark : landmarkGroup) {
        distances[i] = Distance.squareEuclidean(landmark.getLandmark().getLocation().getX(),
            landmark.getLandmark().getLocation().getY(),
            (tile.getColumn() + 0.5) * tileSize + minCoordinates.getX(),
            (tile.getRow() + 0.5) * tileSize + minCoordinates.getY());
        if (distances[i] <= MAX_DISTANCE_SQUARED)
          totalDistance += distances[i];
        ++i;
      }
      double totalWeight = 0.0;
      i = 0;
      for (LandmarkWrapper landmark : landmarkGroup) {
        if (distances[i] <= MAX_DISTANCE_SQUARED) {
          double newWeight = totalDistance / distances[i];
          landmark.setWeight(newWeight);
          totalWeight += newWeight;
        }
        ++i;
      }
      for (LandmarkWrapper landmark : landmarkGroup) {
        if (landmark.getWeight() > 0.0)
          landmark.setWeight(landmark.getWeight() / totalWeight);
      }
      Collections.sort(landmarkGroup, Collections.reverseOrder());
    }
  }

  /**
   * Returns all the landmarks close to this tile that are of a specific type.
   * 
   * @param type
   *          The type of landmarks to return.
   * @return All the landmarks close to this tile which are of a specific type.
   */
  public List<LandmarkWrapper> getLandmarkGroup(LandmarkType type) {
    return landmarks.get(type);
  }

  /**
   * Returns all the landmarks close to this tile.
   * 
   * @return All the landmarks close to this tile.
   */
  public Map<LandmarkType, List<LandmarkWrapper>> getLandmarks() {
    return landmarks;
  }
}