package com.navatar.location.details;

import android.Manifest;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresPermission;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import javax.inject.Inject;

import io.reactivex.Single;
import com.navatar.location.LocationProvider;
import com.navatar.location.model.Location;
import com.navatar.location.model.NoLocationAvailableException;

public class AndroidLocationProvider implements LocationProvider {

    private final FusedLocationProviderClient fusedLocationProviderClient;

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
                                        Log.w("LOCATION", "Are you using an emulator? " +
                                                "Make sure you send a dummy location " +
                                                "to the emulator through the emulator settings");
                                        emitter.onError(new NoLocationAvailableException());
                                    }
                                }
                        )
        );

    }
}