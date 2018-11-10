package com.navatar.location;

import android.location.Location;
import com.navatar.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.Nullable;

import javax.inject.Inject;

public final class LocationPresenter implements LocationContract.Presenter, LocationSource.LocationCallback {

    private final LocationManager mLocationManager;

    @Nullable
    private LocationContract.View mLocationView;

    @Inject
    LocationPresenter(LocationManager locationManager) {
        mLocationManager = locationManager;
    }

    @Override
    public void getLocation() {

        mLocationManager.getLocation(this);

        mLocationView.showProgressbar();
    }

    @Override
    public void takeView(LocationContract.View view) {
        this.mLocationView = view;
    }

    @Override
    public void dropView() {
        mLocationView = null;
    }

    @Override
    public void onLocationChanged(Location location) {

        mLocationView.hideProgressbar();

    }

    @Override
    public void onLocationManagerConnected() { }


}