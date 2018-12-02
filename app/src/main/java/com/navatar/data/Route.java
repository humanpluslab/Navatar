package com.navatar.data;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.navatar.pathplanning.Path;

import java.util.UUID;

/**
 * Immutable model class for a Route Record
 */
@Entity(tableName = "routes")
public final class Route {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "entryid")
    private final String mId;

    @NonNull
    @ColumnInfo(name = "time")
    private final String mTime;

    @NonNull
    @ColumnInfo(name = "mapid")
    private final String mMapId;

    @NonNull
    @ColumnInfo(name = "buildingid")
    private final String mBuildingId;

    @NonNull
    @ColumnInfo(name = "startid")
    private final String mStartId;

    @NonNull
    @ColumnInfo(name = "endid")
    private final String mEndId;

    @NonNull
    private Building mBuilding;

    @Nullable
    private Landmark mFromLandmark;

    @Nullable
    private Landmark mToLandmark;

    @Nullable
    @Ignore
    private Path mPath;


    @Ignore
    public Route(@NonNull String time, @NonNull String mapId, @NonNull String buildingId, @NonNull String startId, @NonNull String endId) {
        this(UUID.randomUUID().toString(), time, mapId, buildingId, startId, endId);
    }

    public Route(@NonNull String id, @NonNull String time, @NonNull String mapId, @NonNull String buildingId, @NonNull String startId, @NonNull String endId) {
        mId = id;
        mTime = time;
        mMapId = mapId;
        mBuildingId = buildingId;
        mStartId = startId;
        mEndId = endId;
    }


    //@Ignore
    //public Route(Building building) {
    //    mBuilding = building;
    //}

    @NonNull
    public Building getBuilding() {
        return mBuilding;
    }

    public void setBuilding(Building building) {
        mBuilding = building;
    }

    public void setFromLandmark(@NonNull Landmark landmark) {
        mFromLandmark = landmark;
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
    }

    public void setPath(@NonNull Path path) {
        mPath = path;
    }

    @Nullable
    public Path getPath() {
        return mPath;
    }



    @NonNull
    public String getId() { return mId; }

    @NonNull
    public String getTime() { return mTime; }

    @NonNull
    public String getMapId() { return mMapId; }

    @NonNull
    public String getBuildingId() { return mBuildingId; }

    @NonNull
    public String getStartId() { return mStartId; }

    @NonNull
    public String getEndId() { return mEndId; }

}
