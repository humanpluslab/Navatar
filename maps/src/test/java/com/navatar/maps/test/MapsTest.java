package com.navatar.maps.test;

import junit.framework.TestCase;

import com.navatar.maps.BuildingMapWrapper;
import com.navatar.maps.particles.ParticleState;
import com.navatar.protobufs.BuildingMapProto.BuildingMap;

import java.io.IOException;

public class MapsTest extends TestCase {

    public void testGetRoomLocation () {

        BuildingMap instance = BuildingMap.getDefaultInstance();
        BuildingMapWrapper wrapper = new BuildingMapWrapper(instance);

        ParticleState state = wrapper.getRoomLocation("Test");

        assertNull(state);
    }
}