package com.navatar.data;

import com.google.common.base.Objects;

public class Location {

    private double mLatitude;

    private double mLongtitude;

    public Location(double latitude, double longtitude) {
        mLatitude = latitude;
        mLongtitude = longtitude;
    }

    public double getLatitude() {
        return mLatitude;
    }

    public double getLongtitude() {
        return mLongtitude;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Location loc = (Location) o;
        return (mLatitude == loc.mLatitude) &&
                (mLongtitude == loc.mLongtitude);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(mLatitude, mLongtitude);
    }

    @Override
    public String toString() {
        return "Location:{" + mLatitude + "," + mLongtitude + "}";
    }

}
