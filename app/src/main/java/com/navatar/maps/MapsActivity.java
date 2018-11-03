package com.navatar.maps;

import android.content.Intent;
import android.os.Bundle;

import com.navatar.R;
import com.navatar.util.ActivityUtils;

import javax.inject.Inject;

import dagger.Lazy;
import dagger.android.support.DaggerAppCompatActivity;

public class MapsActivity extends DaggerAppCompatActivity {

    private static final String CURRENT_FILTERING_KEY = "CURRENT_FILTERING_KEY";
    @Inject
    MapsPresenter mMapsPresenter;
    @Inject
    Lazy<MapsFragment> mapsFragmentProvider;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.welcome_to_navatar);
        setContentView(R.layout.map_select);

        MapsFragment mapsFragment =
                (MapsFragment) getSupportFragmentManager().findFragmentById(R.id.contentFrame);
        if (mapsFragment == null) {
            // Get the fragment from dagger
            mapsFragment = mapsFragmentProvider.get();
            ActivityUtils.addFragmentToActivity(
                    getSupportFragmentManager(), mapsFragment, R.id.contentFrame);
        }


        // Load previously saved state, if available.
        if (savedInstanceState != null) { }
    }
}
