package com.navatar.data;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.common.base.Objects;
import com.google.common.base.Strings;

public class Map {

    public Map(String id, String name) {
        mId = id;
        mName = name;
    }

    @NonNull
    private final String mId;

    @Nullable
    private final String mName;


    @NonNull
    public String getId() {
        return mId;
    }

    @Nullable
    public String getName() {
        return mName;
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
        return "Map with name" + mName;
    }

}
