package com.navatar.location;

import android.location.Location;
import android.support.annotation.Nullable;

import javax.inject.Inject;

public final class LocationPresenter implements LocationContract.Presenter {


    @Nullable
    private LocationContract.View mLocationView;

    @Inject
    LocationPresenter() { }

    @Override
    public void result(int requestCode, int resultCode) {

    }

    @Override
    public void getLocation() {

    }

    @Override
    public void takeView(LocationContract.View view) {
        this.mLocationView = view;
    }

    @Override
    public void dropView() {
        mLocationView = null;
    }

}

