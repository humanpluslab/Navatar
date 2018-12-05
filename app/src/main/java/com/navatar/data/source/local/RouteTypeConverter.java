package com.navatar.data.source.local;


import android.arch.persistence.room.TypeConverter;

import com.navatar.data.Building;
import com.navatar.data.Landmark;
import com.navatar.data.Map;

public class RouteTypeConverter {

    @TypeConverter
    public static Landmark toLandmark(String id) {
        return new Landmark(null);
    }

    @TypeConverter
    public static String landmarkToString(Landmark landmark) {
        return landmark.getName();
    }

    @TypeConverter
    public static Building toBuilding(String id) {
        return new Building(null);
    }

    @TypeConverter
    public static String buildingToString(Building building) {
        return building.getName();
    }

    @TypeConverter
    public static Map toMap(String name) {
        return new Map(name, name.replace('_', ' '));
    }

    @TypeConverter
    public static String mapToString(Map map) {
        return map.getId();
    }

}
