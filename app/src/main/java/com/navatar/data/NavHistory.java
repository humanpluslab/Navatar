package com.navatar.data;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;


import java.util.Date;
import java.util.UUID;

/**
 * Immutable model class for a Navigation History Record
 */
@Entity(tableName = "nav_history")
public final class NavHistory {

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


    @Ignore
    public NavHistory(@NonNull String time, @NonNull String mapId, @NonNull String buildingId, @NonNull String startId, @NonNull String endId) {
        this(UUID.randomUUID().toString(), time, mapId, buildingId, startId, endId);
    }


    public NavHistory(@NonNull String id, @NonNull String time, @NonNull String mapId, @NonNull String buildingId, @NonNull String startId, @NonNull String endId) {
        mId = id;
        mTime = time;
        mMapId = mapId;
        mBuildingId = buildingId;
        mStartId = startId;
        mEndId = endId;
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
