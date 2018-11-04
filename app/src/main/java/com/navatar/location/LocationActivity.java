package com.navatar.location;

import android.location.Location;
import android.os.Bundle;

import com.navatar.R;
import com.navatar.util.ActivityUtils;

import javax.inject.Inject;

import dagger.Lazy;
import dagger.android.support.DaggerAppCompatActivity;

public class LocationActivity extends DaggerAppCompatActivity {
    @Inject
    LocationPresenter mLocationPresenter;

    @Inject
    Lazy<LocationFragment> locationFragmentProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LocationFragment locationFragment =
                (LocationFragment) getSupportFragmentManager().findFragmentById(R.id.contentFrame);
        if (locationFragment == null) {
            // Get the fragment from dagger
            locationFragment = locationFragmentProvider.get();
            ActivityUtils.addFragmentToActivity(
                    getSupportFragmentManager(), locationFragment, R.id.contentFrame);
        }


        // Load previously saved state, if available.
        if (savedInstanceState != null) { }

    }
