package com.navatar.location;

import android.location.Location;

import com.navatar.di.ActivityScoped;

import android.support.annotation.Nullable;
import android.util.Log;

import javax.inject.Inject;

@ActivityScoped
public final class LocationPresenter implements LocationContract.Presenter, LocationSource.LocationCallback {

    private final LocationProvider mLocationProvider;

    @Nullable
    private LocationContract.View mLocationView;

    @Inject
    LocationPresenter(LocationProvider locationProvider) {
        mLocationProvider = locationProvider;
    }

    @Override
    public void getLocation() {
        Log.d("MainActivity", "Getting Location");

        mLocationProvider.getLocation(this);

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