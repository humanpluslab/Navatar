package com.navatar.data;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.navatar.data.source.RouteData;
import com.navatar.pathplanning.Path;

import java.text.DateFormat;
import java.util.Date;

public final class Route {

    @NonNull
    private final Map mMap;

    @Nullable
    private Building mBuilding;

    @Nullable
    private Landmark mFromLandmark;

    @Nullable
    private Landmark mToLandmark;

    @Nullable
    private Path mPath;

    public Route(Map map) {
        mMap = map;
    }

    public Route(Map map, RouteData data) {
        this(map);
        for(Building building : map.getBuildings()) {
            if (building.getName().equals(data.getBuildingId())) {
                mBuilding = building;
                break;
            }
        }
        for(Landmark landmark : mBuilding.destinations()) {
            if (landmark.getName().equals(data.getStartId()))
                mFromLandmark = landmark;
            else if (landmark.getName().equals(data.getEndId()))
                mToLandmark = landmark;
            if (mFromLandmark != null && mToLandmark != null)
                break;
        }
        mPath = mBuilding.getRoute(mFromLandmark, mToLandmark);
    }

    @NonNull
    public Building getBuilding() {
        return mBuilding;
    }

    public void setBuilding(Building building) {
        mBuilding = building;
    }

    public void setFromLandmark(@NonNull Landmark landmark) {
        mFromLandmark = landmark;
        mPath = mBuilding.getRoute(mFromLandmark, mToLandmark);
    }

    @Nullable
    public Landmark getFromLandmark() {
        return mFromLandmark;
    }

    @Nullable
    public Landmark getToLandmark() {
        return mToLandmark;
    }

    public void setToLandmark(@NonNull Landmark landmark) {
        mToLandmark = landmark;
        mPath = mBuilding.getRoute(mFromLandmark, mToLandmark);
    }

    public void setPath(@NonNull Path path) {
        mPath = path;
    }

    @Nullable
    public Path getPath() {
        return mPath;
    }

    public RouteData getRouteData() {
        String time = DateFormat.getDateInstance().format(new Date());
        return new RouteData(time, mMap.getId(), mBuilding.getName(), mFromLandmark.getName(), mToLandmark.getName());

    }

}
