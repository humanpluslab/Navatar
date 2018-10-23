package com.navatar.maps.test;

import com.navatar.maps.BuildingMapWrapper;
import com.navatar.maps.particles.ParticleState;
import com.navatar.protobufs.BuildingMapProto;

import junit.framework.TestCase;

import java.io.IOException;

public class BuildingMapWrapperTests extends TestCase {

    public void testNewBuildingMapWrapper () {
        ClassLoader classLoader = getClass().getClassLoader();

        try {
            BuildingMapProto.BuildingMap map = BuildingMapProto.BuildingMap.parseFrom(classLoader.getResourceAsStream("test.pb"));
            BuildingMapWrapper wrapper = new BuildingMapWrapper(map);

            assertNotNull(wrapper);

        } catch (IOException e) {
            e.printStackTrace();
            assertTrue(false);
        }
    }

    public void testGetRoomLocation() {
        ClassLoader classLoader = getClass().getClassLoader();

        try {
            BuildingMapProto.BuildingMap map = BuildingMapProto.BuildingMap.parseFrom(classLoader.getResourceAsStream("test.pb"));
            BuildingMapWrapper wrapper = new BuildingMapWrapper(map);

            ParticleState state = wrapper.getRoomLocation("401");

            assertNotNull(state);

        } catch (IOException e) {
            e.printStackTrace();
            assertTrue(false);
        }
    }
}
