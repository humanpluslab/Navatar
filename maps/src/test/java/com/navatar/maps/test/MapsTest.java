package com.navatar.maps.test;

import junit.framework.TestCase;

import com.navatar.maps.BuildingMapWrapper;
import com.navatar.maps.particles.ParticleState;
import com.navatar.protobufs.BuildingMapProto;
import com.navatar.protobufs.BuildingMapProto.BuildingMap;
import com.navatar.protobufs.CoordinatesProto;
import com.navatar.protobufs.FloorProto;
import com.navatar.protobufs.LandmarkProto;

import java.io.File;
import java.io.IOException;

public class MapsTest extends TestCase {

    public void testNewBuildingMapWrapper () {
        BuildingMap instance = BuildingMap.getDefaultInstance();

        BuildingMapWrapper wrapper = new BuildingMapWrapper(instance);

        assertNotNull(wrapper);
    }

    public void testLoadMap() {
        ClassLoader classLoader = getClass().getClassLoader();

        try {
            BuildingMap map = BuildingMap.parseFrom(classLoader.getResourceAsStream("scrugham_engineering_mines_minimap.nvm"));
            BuildingMapWrapper wrapper = new BuildingMapWrapper(map);

            assertNotNull(wrapper);

            ParticleState state = wrapper.getRoomLocation("102");

            assertNotNull(state);

        } catch (IOException e) {
            assertTrue(false);
        }
    }

    public void testGetRoomLocation () {

        BuildingMap instance = BuildingMap.getDefaultInstance();
        BuildingMapWrapper wrapper = new BuildingMapWrapper(instance);

        ParticleState state = wrapper.getRoomLocation("Test");

        assertNotNull(state);
    }
}
