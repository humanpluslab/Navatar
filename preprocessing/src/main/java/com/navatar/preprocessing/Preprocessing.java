/**
 * Contains the process responsible for preprocessing the protobuf maps and adding the minimap
 * information.
 * 
 * @author ilias
 *
 */

package com.navatar.preprocessing;

import java.awt.geom.Area;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Random;


import com.navatar.math.Distance;
import com.navatar.protobufs.BuildingMapProto.BuildingMap;
import com.navatar.protobufs.CoordinatesProto.Coordinates;
import com.navatar.protobufs.FloorProto.Floor;
import com.navatar.protobufs.LandmarkProto.Landmark;
import com.navatar.protobufs.LandmarkProto.Landmark.LandmarkType;
import com.navatar.protobufs.MinimapProto.Minimap;
import com.navatar.protobufs.MinimapProto.Minimap.Tile;
import com.navatar.protobufs.NavigableSpaceProto.NavigableSpace;
import com.navatar.protobufs.NavigableSpaceProto.Ring;

public class Preprocessing {
  /** The tile size to be used when generating the minimaps. */
  private static final double TILE_SIZE = 1.0;
  /** The number of closest landmarks to include in a minimap tile. */
  private static final int NUM_OF_MINIMAP_TILE_LANDMARKS = 5;
  /** The maximum distance allowed for a landmark to be considered close to a tile. */
  private static final double MAX_LANDMARK_DISTANCE = 20.0;
  /** The number of particles stored in a landmark. */
  private static final int NUM_OF_PARTICLES_PER_LANDMARK = 10;

  /**
   * Processes the protobuf map in order to create its minimap.
   * 
   * @param args
   *          The protobuf map to be processed.
   */
  public static void main(String[] args) {
    if (args.length < 1) {
      System.err.println("Usage: preprocessing map_filename [output_filename]");
      return;
    }
    BuildingMap map;
    try {
      map = BuildingMap.parseFrom(new FileInputStream(args[0]));
    } catch (FileNotFoundException e) {
      System.err.println("File " + args[0] + " was not found.");
      return;
    } catch (IOException e) {
      System.err.println("Could not read file " + args[0] + ".");
      return;
    }
    FileOutputStream output = null;
    String filename = args.length == 2 ? args[1] : args[0];
    try {
      output = new FileOutputStream(filename);
    } catch (FileNotFoundException e) {
      System.err.println("Could not create file: " + filename);
      return;
    }
    try {
      generateMinimap(generateParticles(map), TILE_SIZE).writeTo(output);
    } catch (IOException e) {
      System.err.println("Could not write to map to file: " + filename);
      return;
    }
    System.out.println("Map was successfully written to file: " + filename);
  }

  static BuildingMap generateParticles(BuildingMap map) {
    Random rand = new Random();
    BuildingMap.Builder mapBuilder = BuildingMap.newBuilder(map);
    mapBuilder.clearFloors();
    for (Floor floor : map.getFloorsList()) {
      Floor.Builder floorBuilder = Floor.newBuilder(floor);
      floorBuilder.clearLandmarks();
      Area navigableArea = new Area(createAccessibleArea(floor.getNavigableSpacesList()));
      for (Landmark landmark : floor.getLandmarksList()) {
        Rectangle2D.Double rect = new Rectangle2D.Double();
        double radius = 0.5;
        for (; radius <= 5.0; radius += 0.5) {
          rect.setRect(landmark.getLocation().getX() - radius,
              landmark.getLocation().getY() - radius, 2 * radius, 2 * radius);
          Area intersection = new Area(navigableArea);
          intersection.intersect(new Area(rect));
          if (!intersection.isEmpty())
            break;
        }
        Landmark.Builder landmarkBuilder = Landmark.newBuilder(landmark);
        if (radius > 5.0) {
          System.err.println("Landmark " + landmark.getName() + " of type " + landmark.getType()
              + " in position x:" + landmark.getLocation().getX() + " y:"
              + landmark.getLocation().getY() + " is far from accessible spaces.");
        } else {
          for (int i = 0; i < NUM_OF_PARTICLES_PER_LANDMARK; ++i) {
            Coordinates.Builder coordinatesBuilder = Coordinates.newBuilder();
            double x = 0.0;
            double y = 0.0;
            do {
              x = landmark.getLocation().getX() + 2 * (rand.nextDouble() - 0.5) * radius;
              y = landmark.getLocation().getY() + 2 * (rand.nextDouble() - 0.5) * radius;
            } while (!navigableArea.contains(x, y));
            coordinatesBuilder.setX(x).setY(y);
            landmarkBuilder.addParticles(coordinatesBuilder.build());
          }
        }
        floorBuilder.addLandmarks(landmarkBuilder.build());
      }
      mapBuilder.addFloors(floorBuilder.build());
    }
    return mapBuilder.build();
  }

  /**
   * Generates minimaps for each floor of the given map. For each tile in the minimap, if it
   * accessible, the process iterates over the list of landmarks and finds the closest ones for each
   * group. If the tile is not accessible, then it puts null in the specific position. It returns a
   * new map that contains all the generated minimaps.
   * 
   * @param map
   *          The map to create minimaps for.
   * @return A new map containing the generated minimaps.
   */
  static BuildingMap generateMinimap(BuildingMap map, double tileSize) {
    BuildingMap.Builder mapBuilder = BuildingMap.newBuilder(map);
    mapBuilder.clearFloors();
    for (Floor floor : map.getFloorsList()) {
      double minX = Double.MAX_VALUE;
      double minY = Double.MAX_VALUE;
      double maxX = Double.NEGATIVE_INFINITY;
      double maxY = Double.NEGATIVE_INFINITY;
      for (NavigableSpace space : floor.getNavigableSpacesList()) {
        for (Coordinates vertex : space.getOuterBoundaryList()) {
          if (vertex.getX() > maxX)
            maxX = vertex.getX();
          if (vertex.getX() < minX)
            minX = vertex.getX();
          if (vertex.getY() > maxY)
            maxY = vertex.getY();
          if (vertex.getY() < minY)
            minY = vertex.getY();
        }
      }
      int rows = Math.max((int) Math.ceil((maxY - minY) / tileSize), 0);
      int columns = Math.max((int) Math.ceil((maxX - minX) / tileSize), 0);
      List<Landmark> landmarks = floor.getLandmarksList();
      Minimap.Builder minimapBuilder = Minimap.newBuilder();
      minimapBuilder = minimapBuilder.setColumns(columns).setRows(rows);
      minimapBuilder.setSideSize(tileSize);
      minimapBuilder.setMinCoordinates(Coordinates.newBuilder().setX(minX).setY(minY).build());
      Path2D.Double navigableArea = createAccessibleArea(floor.getNavigableSpacesList());
      double currY = minY;
      for (int row = 0; row < rows; ++row, currY += tileSize) {
        double currX = minX;
        for (int column = 0; column < columns; ++column, currX += tileSize) {
          Rectangle2D.Double tile = new Rectangle2D.Double(currX, currY, tileSize, tileSize);
          if (navigableArea.intersects(tile)) {
            Tile.Builder tileBuilder = Tile.newBuilder().setRow(row).setColumn(column);
            for (LandmarkType type : LandmarkType.values()) {
              tileBuilder.addAllLandmarks(findKClosestLandmarks(NUM_OF_MINIMAP_TILE_LANDMARKS,
                  landmarks, navigableArea, type, tile));
            }
            minimapBuilder.addTiles(tileBuilder.build());
          }
        }
      }
      mapBuilder.addFloors(Floor.newBuilder(floor).setMinimap(minimapBuilder.build()));
    }
    return mapBuilder.build();
  }

  /**
   * Checks if a square tile overlaps with any part of the accessible areas provided.
   * 
   * @param navigableSpaces
   *          The navigable spaces to check if the tile overlaps with.
   * 
   * @return true if the tile overlaps with any accessible area.
   */
  static Path2D.Double createAccessibleArea(List<NavigableSpace> navigableSpaces) {
    Path2D.Double navigableSpace = new Path2D.Double(Path2D.WIND_EVEN_ODD);
    for (NavigableSpace space : navigableSpaces) {
      List<Ring> rings = space.getRingsList();
      List<Coordinates> outerBoundary = space.getOuterBoundaryList();
      boolean first = true;
      for (Coordinates vertex : outerBoundary) {
        if (first) {
          navigableSpace.moveTo((float) vertex.getX(), (float) vertex.getY());
          first = false;
        } else {
          navigableSpace.lineTo((float) vertex.getX(), (float) vertex.getY());
        }
      }
      if (navigableSpace.getCurrentPoint() != null)
        navigableSpace.closePath();
      for (Ring ring : rings) {
        first = true;
        for (Coordinates vertex : ring.getPolygonList()) {
          if (first) {
            navigableSpace.moveTo((float) vertex.getX(), (float) vertex.getY());
            first = false;
          } else {
            navigableSpace.lineTo((float) vertex.getX(), (float) vertex.getY());
          }
        }
        navigableSpace.closePath();
      }
    }
    return navigableSpace;
  }

  /**
   * Finds and returns the k closest landmarks of a specific type to the point (x, y).
   * 
   * @param k
   *          The number of landmarks to return.
   * @param landmarks
   *          The landmarks to compare for the closest ones.
   * @param type
   *          The type of landmarks to search.
   * @param x
   *          The x coordinate of the point.
   * @param y
   *          The y coordinate of the point.
   * @return The k closest landmarks to the point (x, y).
   */
  private static Iterable<Integer> findKClosestLandmarks(int k, List<Landmark> landmarks,
      Path2D.Double navigableArea, Landmark.LandmarkType type, Rectangle2D.Double tile) {
    LandmarkComparator comparator = new LandmarkComparator();
    PriorityQueue<Object[]> orderedLandmarks =
        new PriorityQueue<Object[]>(NUM_OF_MINIMAP_TILE_LANDMARKS, comparator);
    int index = 0;
    Area tileArea = new Area(tile);
    tileArea.intersect(new Area(navigableArea));
    for (Landmark landmark : landmarks) {
      if (landmark.getType() == type) {
        double distance = Distance.euclidean(tile.getCenterX(), tile.getCenterY(),
            landmark.getLocation().getX(), landmark.getLocation().getY());
        for (PathIterator pi = tileArea.getPathIterator(null); !pi.isDone(); pi.next()) {
          double[] coordinates = new double[6];
          if (pi.currentSegment(coordinates) != PathIterator.SEG_CLOSE && isAccessible(
              new Line2D.Double(coordinates[0], coordinates[1], landmark.getParticles(0).getX(),
                  landmark.getParticles(0).getY()),
              navigableArea)) {
            Object[] orderedLandmark = { distance, index };
            orderedLandmarks.add(orderedLandmark);
            break;
          }
        }
      }
      ++index;
    }
    int size = Math.min(orderedLandmarks.size(), k);
    List<Integer> kClosestLandmarks = new LinkedList<>();
    for (int i = 0; i < size; ++i)
      kClosestLandmarks.add((Integer) orderedLandmarks.poll()[1]);
    return kClosestLandmarks;
  }

  static boolean isAccessible(Line2D.Double line, Path2D.Double navigableArea) {
    if (!navigableArea.contains(line.getP1()) || !navigableArea.contains(line.getP2()))
      return false;
    Point2D.Double start = null;
    Point2D.Double point1 = null;
    Point2D.Double point2 = null;
    for (PathIterator pi = navigableArea.getPathIterator(null); !pi.isDone(); pi.next()) {
      double[] coordinates = new double[6];
      switch (pi.currentSegment(coordinates)) {
      case PathIterator.SEG_MOVETO:
        point2 = new Point2D.Double(coordinates[0], coordinates[1]);
        point1 = null;
        start = (Point2D.Double) point2.clone();
        break;
      case PathIterator.SEG_LINETO:
        point1 = point2;
        point2 = new Point2D.Double(coordinates[0], coordinates[1]);
        break;
      case PathIterator.SEG_CLOSE:
        point1 = point2;
        point2 = start;
        break;
      }
      if (point1 != null) {
        Line2D segment = new Line2D.Double(point1, point2);
        if (segment.intersectsLine(line) && segment.ptLineDist(line.getP1()) > 0.0
            && segment.ptLineDist(line.getP2()) > 0.0)
          return false;
      }
    }
    return true;
  }

  /**
   * Used for comparing landmarks.
   * 
   * @author ilias
   *
   */
  private static class LandmarkComparator implements Comparator<Object[]> {

    @Override
    public int compare(Object[] o1, Object[] o2) {
      return (int) Math.ceil((double) o1[0] - (double) o2[0]);
    }
  }
}