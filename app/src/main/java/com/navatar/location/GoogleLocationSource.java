package com.navatar.location;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.location.Location;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class GoogleLocationSource implements LocationSource, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        ResultCallback<LocationSettingsResult>, LocationListener {

    private static final int LOCATION_REQUEST_INTERVAL = 10000;
    private static final int LOCATION_REQUEST_FASTEST_INTERVAL = 5000;
    private static final int LOCATION_REQUEST_PRIORITY = LocationRequest.PRIORITY_HIGH_ACCURACY;

    @Inject
    public GoogleLocationSource() {

    }

    @Override
    public void getLocation(@NonNull LocationCallback callback) {

    }


    /* Overrides */

    @Override
    public void onConnected(@Nullable Bundle bundle) {


    }

    @Override
    public void onConnectionSuspended(int i) {

    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onResult(@NonNull LocationSettingsResult locationSettingsResult) {

    }

    @Override
    public void onLocationChanged(Location location) {

    }

}
