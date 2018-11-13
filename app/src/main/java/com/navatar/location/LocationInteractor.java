package com.navatar.location;

import javax.inject.Inject;

import io.reactivex.Single;
import com.navatar.location.model.Location;

public class LocationInteractor {

    private final LocationProvider locationProvider;

    @Inject
    public LocationInteractor(LocationProvider locationProvider) {
        this.locationProvider = locationProvider;
    }


    public Single<Location> getLocation() {
        return locationProvider.getLocation();
    }
}
