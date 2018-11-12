package com.navatar.location;

import android.support.annotation.NonNull;

import com.navatar.location.LocationSource;

import javax.inject.Inject;

public class FakeLocationSource implements LocationSource {
    @Inject
    public FakeLocationSource () {

    }

    @Override
    public void getLocation(LocationCallback callback) {

    }

}