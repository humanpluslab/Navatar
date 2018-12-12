package com.navatar.data;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.common.base.Objects;
import com.google.common.base.Strings;

import java.util.ArrayList;
import java.util.List;

public class Map implements Parcelable {

    @NonNull
    private final String mId;

    @Nullable
    private final String mName;

    @NonNull
    private final List<Building> mBuildings;


    public Map(String id, String name, List<Building> buildings) {
        this(id, name);
        mBuildings.addAll(buildings);
    }

    public Map(String id, String name) {
        mId = id;
        mName = name;
        mBuildings = new ArrayList<>();
    }

    private Map(Parcel in) {
        mId = in.readString();
        mName = in.readString();
        mBuildings = new ArrayList<>();
    }

    @NonNull
    public String getId() {
        return mId;
    }

    @Nullable
    public String getName() {
        return mName;
    }

    public List<Building> getBuildings() {
        return mBuildings;
    }

    @Nullable
    public Building getBuilding(String buildingId) {
        for (Building building : mBuildings) {
            if (building.getName().equals(buildingId)) {
                return building;
            }
        }
        return  null;
    }

    public boolean isEmpty() {
        return Strings.isNullOrEmpty(mName);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(mId);
        out.writeString(mName);
    }

    public static final Parcelable.Creator<Map> CREATOR
            = new Parcelable.Creator<Map>() {
        public Map createFromParcel(Parcel in) {
            return new Map(in);
        }

        public Map[] newArray(int size) {
            return new Map[size];
        }
    };


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Map map = (Map) o;
        return Objects.equal(mId, map.mId) &&
                Objects.equal(mName, map.mName);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(mId, mName);
    }

    @Override
    public String toString() {
        return mName;
    }



}
