package com.navatar.data.source.local;


import com.navatar.data.Building;
import com.navatar.data.Landmark;
import com.navatar.pathplanning.Path;

public class TypeConverter {

    @android.arch.persistence.room.TypeConverter
    public static Landmark toLandmark(String id) {
        return new Landmark(null);
    }

    @android.arch.persistence.room.TypeConverter
    public static String landmarkToString(Landmark landmark) {
        return landmark.getName();
    }

    @android.arch.persistence.room.TypeConverter
    public static Building toBuilding(String id) {
        return new Building(null);
    }

    @android.arch.persistence.room.TypeConverter
    public static String buildingToString(Building building) {
        return building.getName();
    }


}
