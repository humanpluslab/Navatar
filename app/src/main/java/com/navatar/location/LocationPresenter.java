package com.navatar.location;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.support.annotation.Nullable;

import javax.inject.Inject;

public final class LocationPresenter implements LocationContract.Presenter, LocationListener {


    @Nullable
    private LocationContract.View mLocationView;

    @Inject
    LocationPresenter() {
    }

    @Override
    public void result(int requestCode, int resultCode) {

    }

    @Override
    public void getLocation() {
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
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {
    }

    @Override
    public void onProviderDisabled(String provider) {
        // GPS needs to be enabled
        if (provider.equals("gps")) {

        }
    }

    @Override
    public void onLocationChanged(Location location) {

    }

}