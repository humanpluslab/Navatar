package com.navatar.location.details;

import android.Manifest;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresPermission;
import android.util.Log;

import com.google.android.gms.location.LocationRequest;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import com.navatar.location.GeofencingProvider;
import com.navatar.location.LocationProvider;
import com.navatar.location.model.Geofence;
import com.navatar.location.model.GeofenceRequest;
import com.navatar.location.model.Location;
import com.patloew.rxlocation.RxLocation;

import java.util.concurrent.TimeUnit;

public class AndroidLocationProvider implements LocationProvider, GeofencingProvider {

    private final String TAG = AndroidLocationProvider.class.getSimpleName();

    private final RxLocation rxLocation;
    private final LocationRequest locationRequest;


    @Inject
    public AndroidLocationProvider(Context context) {
        rxLocation = new RxLocation(context);
        rxLocation.setDefaultTimeout(15, TimeUnit.SECONDS);
        locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(5000);
    }

    @NonNull
    @Override
    public Observable<Location> getLocationUpdates() {

        return rxLocation
                .settings()
                .checkAndHandleResolution(locationRequest)
                .flatMapObservable(this::getLocationObservable)
                .observeOn(AndroidSchedulers.mainThread());
    }

    @RequiresPermission(Manifest.permission.ACCESS_FINE_LOCATION)
    @SuppressWarnings({"MissingPermission"})
    private Observable<Location> getLocationObservable(boolean success) {
        if(success) {
            return rxLocation.location().updates(locationRequest)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .flatMap(this::convertLocation);

        } else {
            return rxLocation.location().lastLocation()
                    .flatMapObservable(this::convertLocation);
        }
    }

    private Observable<Location> convertLocation(android.location.Location location) {
        return Observable.just(Location.create(location.getLatitude(), location.getLongitude()));
    }

    @Override
    public Single<GeofencingProvider.Status> addGeoFenceRequest(GeofenceRequest request) {
        return Single.just(new GeofencingProvider.Status());
    }


}