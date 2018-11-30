package com.navatar.data;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
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
    private final Date mTime;

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


    public NavHistory(@NonNull Date time, @NonNull String mapId, @NonNull String buildingId, @NonNull String fromId, @NonNull String endId) {
        mId = UUID.randomUUID().toString();
        mTime = time;
        mMapId = mapId;
        mBuildingId = buildingId;
        mStartId = fromId;
        mEndId = endId;
    }


    @NonNull
    public String getId() { return mId; }

    @NonNull
    public Date getTime() { return mTime; }

    @NonNull
    public String getMapId() { return mMapId; }

    @NonNull
    public String getBuildingId() { return mBuildingId; }

    @NonNull
    public String getStartId() { return mStartId; }

    @NonNull
    public String getEndId() { return mEndId; }



}
