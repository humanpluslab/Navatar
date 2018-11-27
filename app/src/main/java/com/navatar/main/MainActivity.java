package com.navatar.main;

import android.os.Bundle;

import com.navatar.R;
import com.navatar.maps.MapsFragment;
import com.navatar.util.ActivityUtils;

import javax.inject.Inject;

import dagger.Lazy;
import dagger.android.AndroidInjection;
import dagger.android.support.DaggerAppCompatActivity;

public class MainActivity extends DaggerAppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    @Inject
    Lazy<MainFragment> mainFragmentProvider;

    @Inject
    Lazy<MapsFragment> mapFragmentProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);

        setTitle(R.string.welcome_to_navatar);
        setContentView(R.layout.main_activity);

        MainFragment mainFragment =
                (MainFragment) getSupportFragmentManager().findFragmentById(R.id.contentFrame);

        MapsFragment mapsFragment =
                (MapsFragment) getSupportFragmentManager().findFragmentById(R.id.spinnerFrame);

        if (mainFragment == null) {
            // Get the fragment from dagger
            mainFragment = mainFragmentProvider.get();
            ActivityUtils.addFragmentToActivity(
                    getSupportFragmentManager(), mainFragment, R.id.contentFrame);
        }

        if (mapsFragment == null) {
            // Get the fragment from dagger
            mapsFragment = mapFragmentProvider.get();
            ActivityUtils.addFragmentToActivity(
                    getSupportFragmentManager(), mapsFragment, R.id.spinnerFrame);
        }

    }



}