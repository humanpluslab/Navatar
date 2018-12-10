package com.navatar.maps;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.navatar.R;
import com.navatar.util.ActivityUtils;

import javax.inject.Inject;

import dagger.Lazy;
import dagger.android.AndroidInjection;
import dagger.android.support.DaggerAppCompatActivity;

public class MapsActivity extends DaggerAppCompatActivity {

    @Inject
    Lazy<MapsFragment> mapsFragmentProvider;

    MapsFragment mapsFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);

        setTitle(R.string.selectMapLabel);
        setContentView(R.layout.maps_activity);

        mapsFragment =
                (MapsFragment) getSupportFragmentManager().findFragmentById(R.id.contentFrame);

        if (mapsFragment == null) {
            mapsFragment = mapsFragmentProvider.get();
            ActivityUtils.addFragmentToActivity(
                    getSupportFragmentManager(), mapsFragment, R.id.contentFrame);
        }

    }

    @Override
    public void onBackPressed() {
        if (mapsFragment == null || !mapsFragment.onBackPressed()) {
            super.onBackPressed();
        }
    }

}
