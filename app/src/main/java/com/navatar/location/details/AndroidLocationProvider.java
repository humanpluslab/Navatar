package com.navatar.location.details;

import android.Manifest;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresPermission;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.Single;
import com.navatar.location.LocationProvider;
import com.navatar.location.model.Location;
import com.navatar.location.model.NoLocationAvailableException;

public class AndroidLocationProvider implements LocationProvider, LocationListener {

    private final String TAG = AndroidLocationProvider.class.getSimpleName();

    private final FusedLocationProviderClient fusedLocationProviderClient;

    private android.location.Location lastKnownLocation;


    @Inject
    public AndroidLocationProvider(Context context) {
        this.fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context);
    }

    @RequiresPermission(Manifest.permission.ACCESS_FINE_LOCATION)
    @SuppressWarnings({"MissingPermission"})
    @NonNull
    @Override
    public Single<Location> getLocation() {

        return Single.create(
                emitter -> fusedLocationProviderClient
                        .getLastLocation()
                        .addOnSuccessListener(location -> {
                                    if (location != null) {
                                        emitter.onSuccess(Location.create(location.getLatitude(),
                                                location.getLongitude()));
                                    } else {
                                        Log.w(TAG, "Are you using an emulator? " +
                                                "Make sure you send a dummy location " +
                                                "to the emulator through the emulator settings");
                                        emitter.onError(new NoLocationAvailableException());
                                    }
                                }
                        )
        );

    }

    @NonNull
    @Override
    public Observable<Location> getLocationChanged() {


        return Observable.empty();
    }

    @Override
    public void onLocationChanged(android.location.Location location) {
        Log.d(TAG, "onLocationChanged: hit");
        lastKnownLocation = location;

    }

}