package com.navatar.data;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.common.base.Objects;
import com.google.common.base.Strings;

import java.util.ArrayList;
import java.util.List;

public class Map {

    public Map(String id, String name) {
        mId = id;
        mName = name;
        mBuildings = new ArrayList<String>();
    }

    @NonNull
    private final String mId;

    @Nullable
    private final String mName;

    @NonNull
    private final List<String> mBuildings;

    @NonNull
    public String getId() {
        return mId;
    }

    @Nullable
    public String getName() {
        return mName;
    }

    public List<String> getBuildings() {
        return mBuildings;
    }

    public boolean isEmpty() {
        return Strings.isNullOrEmpty(mName);
    }

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
