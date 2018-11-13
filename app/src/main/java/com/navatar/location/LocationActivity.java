package com.navatar.location;

import android.Manifest;
import android.content.Context;
import android.content.IntentSender;
import android.os.Looper;
import android.provider.Settings;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.navatar.R;
import com.navatar.common.details.PermissionsRequestResultDispatcher;
import com.navatar.location.model.Location;
import com.navatar.util.ActivityUtils;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnClick;
import dagger.Lazy;
import dagger.android.AndroidInjection;
import dagger.android.support.DaggerAppCompatActivity;

public class LocationActivity extends DaggerAppCompatActivity {

    private static final String TAG = LocationActivity.class.getSimpleName();

    @Inject
    LocationPresenter mLocationPresenter;

    @Inject
    Lazy<LocationFragment> locationFragmentProvider;

    @Inject
    @Named("locationReqCode")
    Integer locationRequestCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.location_act);

        LocationFragment locationFragment =
                (LocationFragment) getSupportFragmentManager().findFragmentById(R.id.contentFrame);
        if (locationFragment == null) {
            // Get the fragment from dagger
            locationFragment = locationFragmentProvider.get();
            ActivityUtils.addFragmentToActivity(
                    getSupportFragmentManager(), locationFragment, R.id.contentFrame);
        }

        //locationFragment.onStart();

        // Load previously saved state, if available.
        if (savedInstanceState != null) {

        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        //mLocationPresenter.loadData();
    }

    @Override
    @SuppressWarnings({"MissingPermission"})
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == locationRequestCode) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //permissionsRequestResultDispatcher.dispatchResult(true);
            } else {
                //permissionsRequestResultDispatcher.dispatchResult(false);
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

}