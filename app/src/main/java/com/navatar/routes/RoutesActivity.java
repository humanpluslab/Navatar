package com.navatar.routes;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.navatar.R;
import com.navatar.util.ActivityUtils;

import javax.inject.Inject;

import dagger.Lazy;
import dagger.android.AndroidInjection;
import dagger.android.support.DaggerAppCompatActivity;

public class RoutesActivity extends DaggerAppCompatActivity {

    @Inject
    Lazy<RoutesFragment> routesFragmentProvider;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.routes_activity);

        RoutesFragment routesFragment =
                (RoutesFragment) getSupportFragmentManager().findFragmentById(R.id.contentFrame);
        if(routesFragment == null) {
            routesFragment = routesFragmentProvider.get();
            ActivityUtils.addFragmentToActivity(
                    getSupportFragmentManager(), routesFragment, R.id.contentFrame);
        }
    }
}
