package com.navatar.data;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.navatar.pathplanning.Path;

public class Route {

    @NonNull
    private Building mBuilding;

    @Nullable
    private Landmark mFromLandmark;

    @Nullable
    private Landmark mToLandmark;

    @Nullable
    private Path mPath;

    public Route(Building building) {
        mBuilding = building;
    }

    @NonNull
    public Building getmBuilding() {
        return mBuilding;
    }

    public void setFrom(@NonNull Landmark landmark) {
        mFromLandmark = landmark;
    }

    @Nullable
    public Landmark getFrom() {
        return mFromLandmark;
    }

    public void setTo(@NonNull Landmark landmark) {
        mToLandmark = landmark;
    }

    public void setPath(@NonNull Path path) {
        mPath = path;
    }

    @Nullable
    public Path getPath() {
        return mPath;
    }


}
