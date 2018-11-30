/**
 * Contains the BuildingMap wrapper class for the protobuf class.
 */
package com.navatar.maps;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.navatar.maps.particles.ParticleState;
import com.navatar.protobufs.BuildingMapProto.BuildingMap;
import com.navatar.protobufs.CoordinatesProto.Coordinates;
import com.navatar.protobufs.FloorProto.Floor;
import com.navatar.protobufs.LandmarkProto;
import com.navatar.protobufs.LandmarkProto.Landmark;
import com.navatar.protobufs.LandmarkProto.Landmark.LandmarkType;
import com.navatar.protobufs.MinimapProto.Minimap;
import com.navatar.protobufs.MinimapProto.Minimap.Tile;

public class BuildingMapWrapper {
    /**
     * Stores the protobuf map instance.
     */
    private BuildingMap protoMap;
    /**
     * The minimap grid that stores the tiles.
     */
    private HashMap<Integer, TileWrapper[][]> minimaps;
    /** The accessible spaces of the map divided by floor. */
    //private HashMap<Integer, Path> accessibleSpaces;
    /**
     * Dispatches floor indices from floor numbers.
     */
    private HashMap<Integer, Integer> floorsToIndices;

    public BuildingMapWrapper(BuildingMap map) {
        this.protoMap = map;
        minimaps = new HashMap<>();
        //accessibleSpaces = new HashMap<>();
        floorsToIndices = new HashMap<>();
        int i = 0;
        for (Floor floor : map.getFloorsList()) {
            Minimap minimap = floor.getMinimap();
            TileWrapper[][] floorTiles = new TileWrapper[minimap.getRows()][minimap.getColumns()];
            for (Tile tile : minimap.getTilesList()) {
                floorTiles[tile.getRow()][tile.getColumn()] = new TileWrapper(tile,
                        floor.getLandmarksList(), minimap.getSideSize(), minimap.getMinCoordinates());
            }
            minimaps.put(floor.getNumber(), floorTiles);
            //   accessibleSpaces.put(floor.getNumber(), createAccessibleArea(floor.getNavigableSpacesList()));
            floorsToIndices.put(floor.getNumber(), i++);
        }
    }

    /**
     * Checks if a square tile overlaps with any part of the accessible areas provided.
     *
     * @param navigableSpaces
     *          The navigable spaces to check if the tile overlaps with.
     *
     * @return true if the tile overlaps with any accessible area.

    private static Path createAccessibleArea(List<NavigableSpace> navigableSpaces) {
    Path navigableSpace = new Path();
    navigableSpace.setFillType(Path.FillType.EVEN_ODD);
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
    if (!navigableSpace.isEmpty())
    navigableSpace.close();
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
    navigableSpace.close();
    }
    }
    return navigableSpace;
    }
     */
    /**
     * Finds and returns the location of the room with the specific name given as a parameter.
     *
     * @param room The room name to use in order to find its location.
     * @return The location of the room with the given name, or null if there is no door with the
     * specific name.
     */
    public ParticleState getRoomLocation(String room) {
        for (Floor floor : protoMap.getFloorsList()) {
            for (Landmark landmark : floor.getLandmarksList()) {
                if (landmark.getName().equals(room)) {
                    // Can throw an IndexOutOfBounds error
                    Coordinates particle = landmark.getParticles(0);

                    return new ParticleState(0, particle.getX(), particle.getY(), floor.getNumber());
                }
            }
        }
        return null;
    }

    /**
     * Returns all the rooms in the map.
     *
     * @return All the rooms contained in the map.
     */
    public List<LandmarkWrapper> destinations() {
        ArrayList<LandmarkWrapper> rooms = new ArrayList<LandmarkWrapper>();
        for (Floor floor : protoMap.getFloorsList()) {
            for (Landmark landmark : floor.getLandmarksList()) {
                if (landmark.getType() == LandmarkType.DOOR)
                    rooms.add(new LandmarkWrapper(landmark));
            }
        }
        return rooms;
    }

    /**
     * Reads a protobuf map file and returns a BuildingMapWrapper that contains the protobuf.
     *
     * @param filename The filename of the protobuf map.
     * @return A new BuildingMapWrapper instance containg the protobuf map.
     * @throws IOException If the method cannot read the file.
     */
    public static BuildingMapWrapper readFrom(String filename) throws IOException {
        return new BuildingMapWrapper(BuildingMap.parseFrom(new FileInputStream(filename)));
    }

    // TODO(ilapost): Unit test these to make sure they work properly.
    // TODO(ilapost): Consider creating a shape and use that to test for accessibility since it will
    // give more accurate results.

    /**
     * Returns true if a point belongs to the accessible space in the map.
     *
     * @param x     The x coordinate of the point.
     * @param y     The y coordinate of the point.
     * @param floor The floor to check.
     * @return True it the point is accessible.
     */
    public boolean isAccessible(double x, double y, int floor) {
        return getTile(x, y, floor) != null;
    }

    /**
     * Same as {@link #isAccessible(double, double, int)}.
     *
     * @param state The state to check if it is accessible.
     * @return True if the state is accessible.
     */
    public boolean isAccessible(ParticleState state) {
        return isAccessible(state.getX(), state.getY(), state.getFloor());
    }

    /**
     * Returns the closest landmark to the given location (x,y) which is of a specific landmark type.
     *
     * @param x    The x coordinate of the point to search for the closest landmark.
     * @param y    The y coordinate of the point to search for the closest landmark.
     * @param type The type of the landmark to search for.
     * @return The closest landmark of a specific type.
     */
    public Landmark getClosestLandmark(ParticleState state, LandmarkType type) {
        List<LandmarkWrapper> landmarkGroup = getTile(state).getLandmarkGroup(type);
        if (landmarkGroup.isEmpty())
            return null;
        return landmarkGroup.get(0).getLandmark();
    }

    /**
     * Returns a landmark of specific type which is close to the given state. Keep in mind that the
     * landmark does not have to be the closest one.
     *
     * @param state The position to search around for a close landmark.
     * @param type  The type of landmark to search for.
     * @return A landmark close to the given position which is of the given type.
     */
    public Landmark getCloseLandmark(ParticleState state, LandmarkType type) {
        List<LandmarkWrapper> landmarkGroup = getTile(state).getLandmarkGroup(type);
        if (landmarkGroup.isEmpty())
            return null;
        Double choice = Math.random(), weightSum = 0.0;
        for (LandmarkWrapper landmark : landmarkGroup) {
            weightSum += landmark.getWeight();
            if (weightSum > choice)
                return landmark.getLandmark();
        }
        return landmarkGroup.get(0).getLandmark();
    }

    /**
     * Returns all the landmarks close to a specific location.
     *
     * @param state The location to search for landmarks.
     */
    public Map<LandmarkType, List<LandmarkWrapper>> getLandmarks(ParticleState state) {
        TileWrapper tile = getTile(state);
        if (tile != null)
            return tile.getLandmarks();
        return new HashMap<LandmarkProto.Landmark.LandmarkType, List<LandmarkWrapper>>();
    }

    TileWrapper getTile(ParticleState state) {
        return getTile(state.getX(), state.getY(), state.getFloor());
    }

    /**
     * Returns the tile that contains the given point.
     *
     * @param state
     * @return
     */
    TileWrapper getTile(double x, double y, int floor) {
        TileWrapper[][] floorTiles = minimaps.get(floor);
        if (floorTiles == null)
            return null;
        double tileSize = protoMap.getFloors(floorsToIndices.get(floor)).getMinimap().getSideSize();
        Coordinates origin =
                protoMap.getFloors(floorsToIndices.get(floor)).getMinimap().getMinCoordinates();
        int row = (int) ((y - origin.getY()) / tileSize);
        int column = (int) ((x - origin.getX()) / tileSize);
        if (row < 0 || row >= floorTiles.length || column < 0 || column >= floorTiles[0].length)
            return null;
        return floorTiles[row][column];
    }

    public BuildingMap getProtobufMap() {
        return protoMap;
    }

    // TODO(ilapost): This function only checks the starting and ending points. It should also check
    // in between in case the path goes through an inaccessible area.

    /**
     * Checks if a given path goes through inaccessible areas.
     *
     * @param start The start of the path.
     * @param end   The end of the path.
     * @return True if the path goes through inaccessible areas.
     */
    public boolean pathBlocked(ParticleState start, ParticleState end) {
        return !(isAccessible(start) && isAccessible(end));
    }

    public String getName() {
        return protoMap.getName();
    }
}
