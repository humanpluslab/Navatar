/**
 * Contains unit tests for the preprocessing class.
 */
package com.navatar.preprocessing;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.util.List;

import org.junit.Test;

import com.navatar.protobufs.BuildingMapProto.BuildingMap;
import com.navatar.protobufs.CoordinatesProto.Coordinates;
import com.navatar.protobufs.FloorProto.Floor;
import com.navatar.protobufs.LandmarkProto.Landmark;
import com.navatar.protobufs.MinimapProto.Minimap;
import com.navatar.protobufs.MinimapProto.Minimap.Tile;
import com.navatar.protobufs.NavigableSpaceProto.NavigableSpace;
import com.navatar.protobufs.NavigableSpaceProto.Ring;

/**
 * Tests that preprocessing functionality is correct.
 * 
 * @author ilias
 *
 */
public class PreprocessingTest {

  /**
   * Checks that the generated minimap has the correct values stored.
   * 
   * @param map
   *          The map used to generate the minimap.
   * @param data
   *          The correct values to compare with the generated ones.
   */
  private void validateMinimap(BuildingMap map, Object[] data) {
    map = Preprocessing.generateParticles(map);
    map = Preprocessing.generateMinimap(map, (double) data[0]);
    Minimap minimap = map.getFloors(0).getMinimap();
    assertNotNull("The minimap was not generated.", minimap);
    assertEquals("Tile size is incorrect.", (double) data[0], minimap.getSideSize(), 0.00001);
    assertEquals("The number of minimap rows is incorrect.", (int) data[1], minimap.getRows());
    assertEquals("The number of minimap columns is incorrect.", (int) data[2],
        minimap.getColumns());
    List<Tile> tiles = minimap.getTilesList();
    assertEquals("Minimap does not have the correct number of tiles.", (int) data[3], tiles.size());
    assertEquals("The minimum X coordinate is incorrect.", (double) data[4],
        minimap.getMinCoordinates().getX(), 0.00001);
    assertEquals("The minimum Y coordinate is incorrect.", (double) data[5],
        minimap.getMinCoordinates().getY(), 0.00001);
    int i = 6;
    for (Tile tile : tiles) {
      assertEquals("The number of landmarks is not correct.", (int) data[i++],
          tile.getLandmarksCount());
    }
  }

  /**
   * Generates an accessible convex BuildingMap for testing purposes.
   * 
   * @return An accessible convex map.
   */
  private static BuildingMap generateConvexMap() {
    BuildingMap.Builder mapBuilder = BuildingMap.newBuilder();
    Coordinates.Builder coordinatesBuilder = Coordinates.newBuilder();
    Floor.Builder floorBuilder = Floor.newBuilder();
    Landmark.Builder landmarkBuilder = Landmark.newBuilder();
    NavigableSpace.Builder navigableBuilder = NavigableSpace.newBuilder();
    floorBuilder.setNumber(0);
    landmarkBuilder.setLocation(coordinatesBuilder.setX(1.0).setY(2.5));
    landmarkBuilder.setType(Landmark.LandmarkType.DOOR);
    landmarkBuilder.setName("208");
    floorBuilder.addLandmarks(landmarkBuilder.build());
    landmarkBuilder.setLocation(coordinatesBuilder.setX(2.5).setY(2.0));
    landmarkBuilder.setType(Landmark.LandmarkType.HALLWAY_INTERSECTION);
    floorBuilder.addLandmarks(landmarkBuilder.build());
    landmarkBuilder.setLocation(coordinatesBuilder.setX(5.0).setY(2.5));
    landmarkBuilder.setType(Landmark.LandmarkType.STAIRS);
    floorBuilder.addLandmarks(landmarkBuilder.build());
    landmarkBuilder.setLocation(coordinatesBuilder.setX(2.5).setY(4.0));
    landmarkBuilder.setType(Landmark.LandmarkType.ELEVATOR);
    floorBuilder.addLandmarks(landmarkBuilder.build());
    navigableBuilder.addOuterBoundary(coordinatesBuilder.setX(1.0).setY(2.0));
    navigableBuilder.addOuterBoundary(coordinatesBuilder.setX(5.0).setY(2.0));
    navigableBuilder.addOuterBoundary(coordinatesBuilder.setX(5.0).setY(4.0));
    navigableBuilder.addOuterBoundary(coordinatesBuilder.setX(1.0).setY(4.0));
    floorBuilder.addNavigableSpaces(navigableBuilder.build());
    mapBuilder.addFloors(floorBuilder.build());
    return mapBuilder.build();
  }

  /**
   * Generates a BuildingMap with two disconnected accessible areas for testing purposes.
   * 
   * @return An accessible convex map.
   */
  private static BuildingMap generateMapTwoAccessibleAreas() {
    BuildingMap.Builder mapBuilder = BuildingMap.newBuilder();
    Coordinates.Builder coordinatesBuilder = Coordinates.newBuilder();
    Floor.Builder floorBuilder = Floor.newBuilder();
    Landmark.Builder landmarkBuilder = Landmark.newBuilder();
    NavigableSpace.Builder navigableBuilder = NavigableSpace.newBuilder();
    floorBuilder.setNumber(0);
    landmarkBuilder.setLocation(coordinatesBuilder.setX(0.0).setY(2.5));
    landmarkBuilder.setType(Landmark.LandmarkType.DOOR);
    floorBuilder.addLandmarks(landmarkBuilder.build());
    landmarkBuilder.setLocation(coordinatesBuilder.setX(2.5).setY(0.0));
    landmarkBuilder.setType(Landmark.LandmarkType.HALLWAY_INTERSECTION);
    floorBuilder.addLandmarks(landmarkBuilder.build());
    landmarkBuilder.setLocation(coordinatesBuilder.setX(5.0).setY(2.5));
    landmarkBuilder.setType(Landmark.LandmarkType.STAIRS);
    floorBuilder.addLandmarks(landmarkBuilder.build());
    landmarkBuilder.setLocation(coordinatesBuilder.setX(2.5).setY(4.0));
    landmarkBuilder.setType(Landmark.LandmarkType.ELEVATOR);
    floorBuilder.addLandmarks(landmarkBuilder.build());
    navigableBuilder.addOuterBoundary(coordinatesBuilder.setX(0.0).setY(0.0));
    navigableBuilder.addOuterBoundary(coordinatesBuilder.setX(5.0).setY(0.0));
    navigableBuilder.addOuterBoundary(coordinatesBuilder.setX(5.0).setY(4.0));
    navigableBuilder.addOuterBoundary(coordinatesBuilder.setX(0.0).setY(4.0));
    floorBuilder.addNavigableSpaces(navigableBuilder.build());
    navigableBuilder.clear();
    navigableBuilder.addOuterBoundary(coordinatesBuilder.setX(6.0).setY(0.0));
    navigableBuilder.addOuterBoundary(coordinatesBuilder.setX(10.0).setY(0.0));
    navigableBuilder.addOuterBoundary(coordinatesBuilder.setX(10.0).setY(6.0));
    navigableBuilder.addOuterBoundary(coordinatesBuilder.setX(6.0).setY(6.0));
    floorBuilder.addNavigableSpaces(navigableBuilder.build());
    mapBuilder.addFloors(floorBuilder.build());
    return mapBuilder.build();
  }

  /**
   * Generates an accessible concave BuildingMap for testing purposes.
   * 
   * @return An accessible convex map.
   */
  private static BuildingMap generateConcaveMap() {
    BuildingMap.Builder mapBuilder = BuildingMap.newBuilder();
    Coordinates.Builder coordinatesBuilder = Coordinates.newBuilder();
    Floor.Builder floorBuilder = Floor.newBuilder();
    Landmark.Builder landmarkBuilder = Landmark.newBuilder();
    NavigableSpace.Builder navigableBuilder = NavigableSpace.newBuilder();
    floorBuilder.setNumber(0);
    landmarkBuilder.setLocation(coordinatesBuilder.setX(0.0).setY(2.5));
    landmarkBuilder.setType(Landmark.LandmarkType.DOOR);
    floorBuilder.addLandmarks(landmarkBuilder.build());
    landmarkBuilder.setLocation(coordinatesBuilder.setX(4.0).setY(7.0));
    landmarkBuilder.setType(Landmark.LandmarkType.DOOR);
    floorBuilder.addLandmarks(landmarkBuilder.build());
    landmarkBuilder.setLocation(coordinatesBuilder.setX(2.5).setY(0.0));
    landmarkBuilder.setType(Landmark.LandmarkType.HALLWAY_INTERSECTION);
    floorBuilder.addLandmarks(landmarkBuilder.build());
    landmarkBuilder.setLocation(coordinatesBuilder.setX(5.0).setY(2.5));
    landmarkBuilder.setType(Landmark.LandmarkType.STAIRS);
    floorBuilder.addLandmarks(landmarkBuilder.build());
    landmarkBuilder.setLocation(coordinatesBuilder.setX(2.5).setY(4.0));
    landmarkBuilder.setType(Landmark.LandmarkType.ELEVATOR);
    floorBuilder.addLandmarks(landmarkBuilder.build());
    navigableBuilder.addOuterBoundary(coordinatesBuilder.setX(0.0).setY(0.0));
    navigableBuilder.addOuterBoundary(coordinatesBuilder.setX(8.0).setY(0.0));
    navigableBuilder.addOuterBoundary(coordinatesBuilder.setX(8.0).setY(2.0));
    navigableBuilder.addOuterBoundary(coordinatesBuilder.setX(5.0).setY(2.0));
    navigableBuilder.addOuterBoundary(coordinatesBuilder.setX(5.0).setY(6.0));
    navigableBuilder.addOuterBoundary(coordinatesBuilder.setX(8.0).setY(6.0));
    navigableBuilder.addOuterBoundary(coordinatesBuilder.setX(8.0).setY(7.0));
    navigableBuilder.addOuterBoundary(coordinatesBuilder.setX(0.0).setY(7.0));
    floorBuilder.addNavigableSpaces(navigableBuilder.build());
    mapBuilder.addFloors(floorBuilder.build());
    return mapBuilder.build();
  }

  /**
   * Generates an accessible BuildingMap with inaccessible rings in it for testing purposes.
   * 
   * @return An accessible convex map.
   */
  private static BuildingMap generateMapWithRings() {
    BuildingMap.Builder mapBuilder = BuildingMap.newBuilder();
    Coordinates.Builder coordinatesBuilder = Coordinates.newBuilder();
    Floor.Builder floorBuilder = Floor.newBuilder();
    Landmark.Builder landmarkBuilder = Landmark.newBuilder();
    NavigableSpace.Builder navigableBuilder = NavigableSpace.newBuilder();
    floorBuilder.setNumber(0);
    landmarkBuilder.setLocation(coordinatesBuilder.setX(0.0).setY(2.5));
    landmarkBuilder.setType(Landmark.LandmarkType.DOOR);
    floorBuilder.addLandmarks(landmarkBuilder.build());
    landmarkBuilder.setLocation(coordinatesBuilder.setX(4.0).setY(7.0));
    landmarkBuilder.setType(Landmark.LandmarkType.DOOR);
    floorBuilder.addLandmarks(landmarkBuilder.build());
    landmarkBuilder.setLocation(coordinatesBuilder.setX(2.5).setY(0.0));
    landmarkBuilder.setType(Landmark.LandmarkType.HALLWAY_INTERSECTION);
    floorBuilder.addLandmarks(landmarkBuilder.build());
    landmarkBuilder.setLocation(coordinatesBuilder.setX(5.0).setY(2.5));
    landmarkBuilder.setType(Landmark.LandmarkType.STAIRS);
    floorBuilder.addLandmarks(landmarkBuilder.build());
    landmarkBuilder.setLocation(coordinatesBuilder.setX(2.5).setY(4.0));
    landmarkBuilder.setType(Landmark.LandmarkType.ELEVATOR);
    floorBuilder.addLandmarks(landmarkBuilder.build());
    navigableBuilder.addOuterBoundary(coordinatesBuilder.setX(0.0).setY(0.0));
    navigableBuilder.addOuterBoundary(coordinatesBuilder.setX(18.0).setY(0.0));
    navigableBuilder.addOuterBoundary(coordinatesBuilder.setX(18.0).setY(15.0));
    navigableBuilder.addOuterBoundary(coordinatesBuilder.setX(0.0).setY(15.0));
    Ring.Builder ringBuilder = Ring.newBuilder();
    ringBuilder.addPolygon(coordinatesBuilder.setX(1.0).setY(1.0));
    ringBuilder.addPolygon(coordinatesBuilder.setX(6.0).setY(1.0));
    ringBuilder.addPolygon(coordinatesBuilder.setX(6.0).setY(6.0));
    ringBuilder.addPolygon(coordinatesBuilder.setX(1.0).setY(6.0));
    navigableBuilder.addRings(ringBuilder.build());
    ringBuilder.clear();
    ringBuilder.addPolygon(coordinatesBuilder.setX(11.0).setY(8.0));
    ringBuilder.addPolygon(coordinatesBuilder.setX(16.0).setY(8.0));
    ringBuilder.addPolygon(coordinatesBuilder.setX(16.0).setY(13.0));
    ringBuilder.addPolygon(coordinatesBuilder.setX(11.0).setY(13.0));
    navigableBuilder.addRings(ringBuilder.build());
    floorBuilder.addNavigableSpaces(navigableBuilder.build());
    mapBuilder.addFloors(floorBuilder.build());
    return mapBuilder.build();
  }

  /**
   * Tests if the static function generateMinimap correctly generates the minimap of an accessible
   * convex map.
   */
  @Test
  public void testGenerateMinimapAccessibleMapWithoutRings() {
    BuildingMap map = generateConvexMap();
    Object[] data = { 3.0, 1, 2, 2, 1.0, 2.0, 4 };
    validateMinimap(map, data);
  }

  /**
   * Tests if the static function generateMinimap correctly generates the minimap of a map with two
   * disconnected accessible areas.
   */
  @Test
  public void testGenerateMinimapMapWithTwoAccessibleAreas() {
    BuildingMap map = generateMapTwoAccessibleAreas();
    Object[] data = { 3.0, 2, 4, 8, 0.0, 0.0, 4 };
    validateMinimap(map, data);
  }

  /**
   * Tests if the static function generateMinimap correctly generates the minimap of an accessible
   * concave map.
   */
  @Test
  public void testGenerateMinimapAccessibleConcaveMap() {
    BuildingMap map = generateConcaveMap();
    Object[] data = { 3.0, 3, 3, 8, 0.0, 0.0, 5, 5, 3, 5, 5, 5, 5, 1 };
    validateMinimap(map, data);
  }

  /**
   * Tests if the static function generateMinimap correctly generates the minimap of an accessible
   * map with inaccessible inner rings.
   */
  @Test
  public void testGenerateMinimapAccessibleMapWithRings() {
    BuildingMap map = generateMapWithRings();
    Object[] data = { 3.0, 5, 6, 28, 0.0, 0.0, 3, 2, 2, 2, 2, 2 };
    validateMinimap(map, data);
  }

  /**
   * Tests if the static function generateMinimap correctly generates the minimap of a
   * non-accessible map.
   */
  @Test
  public void testGenerateMinimapAccessibleMapWithoutAccessibleSpaces() {
    Floor floor = Floor.newBuilder().addNavigableSpaces(NavigableSpace.newBuilder()).build();
    BuildingMap map = BuildingMap.newBuilder().addFloors(floor).build();
    Object[] data = { 3.0, 0, 0, 0, Double.MAX_VALUE, Double.MAX_VALUE, 0 };
    validateMinimap(map, data);
  }

  /**
   * Tests if the static function generateParticles correctly generates particles for every
   * landmark.
   */
  @Test
  public void testGenerateParticles() {
    BuildingMap map = generateConvexMap();
    map = Preprocessing.generateParticles(map);
    for (Landmark landmark : map.getFloorsList().get(0).getLandmarksList())
      assertTrue("Landmark does not have 10 particles.", landmark.getParticlesCount() == 10);
  }

  /**
   * Tests if the static function isAccessible correctly identifies if a path is accessible.
   */
  @Test
  public void testAccessible() {
    BuildingMap map = generateConvexMap();
    Path2D.Double space =
        Preprocessing.createAccessibleArea(map.getFloors(0).getNavigableSpacesList());
    assertTrue(Preprocessing.isAccessible(new Line2D.Double(1.1, 3.0, 4.0, 3.0), space));
    assertTrue(Preprocessing.isAccessible(new Line2D.Double(1.0, 3.0, 4.0, 3.0), space));
    assertFalse(Preprocessing.isAccessible(new Line2D.Double(4.0, 4.0, 4.0, 3.0), space));
    assertFalse(Preprocessing.isAccessible(new Line2D.Double(0.0, 0.0, 4.0, 3.0), space));
    assertFalse(Preprocessing.isAccessible(new Line2D.Double(2.0, 3.0, 7.0, 7.0), space));
    assertFalse(Preprocessing.isAccessible(new Line2D.Double(0.0, 0.0, 7.0, 7.0), space));
  }
}
