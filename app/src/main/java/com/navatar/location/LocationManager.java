package com.navatar.location;

import android.support.annotation.NonNull;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class LocationManager implements LocationSource {

    private final LocationSource mLocationSource;

    @Inject
    public LocationManager(LocationSource locationSource) {
        mLocationSource = locationSource;
    }

    @Override
    public void getLocation(@NonNull LocationCallback callback) {
        mLocationSource.getLocation(callback);
    }


}
