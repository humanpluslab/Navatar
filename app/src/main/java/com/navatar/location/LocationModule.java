package com.navatar.location;

import android.Manifest;

import com.navatar.di.ActivityScoped;
import com.navatar.di.FragmentScoped;
import com.navatar.location.details.AndroidGeofencingProvider;
import com.navatar.location.details.AndroidLocationProvider;
import com.navatar.common.PermissionRequestHandler;
import com.navatar.common.details.RuntimePermissionRequestHandler;
import com.navatar.location.details.QRCodeScanner;
import com.navatar.location.model.Location;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.inject.Named;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import dagger.android.ContributesAndroidInjector;
import dagger.multibindings.ElementsIntoSet;

/**
 * This is a Dagger module. We use this to pass in the View dependency to the
 * {@link LocationPresenter}.
 */
@Module
public abstract class LocationModule {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 144;
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 250;

    @Binds
    abstract LocationContract.View provideView(LocationActivity activity);

    @Binds
    abstract LocationContract.Presenter providePresenter(LocationPresenter presenter);

    @Binds
    abstract LocationProvider provideLocationProvider(AndroidLocationProvider locationProvider);

    @Binds
    abstract GeofencingProvider provideGeofencingProvider(AndroidGeofencingProvider geofencingProvider);

    @Provides
    @Named("locationReqCode")
    static Integer provideLocationReqCode() {
        return LOCATION_PERMISSION_REQUEST_CODE;
    }

    @Provides
    @Named("cameraReqCode")
    static Integer provideCameraReqCode() { return CAMERA_PERMISSION_REQUEST_CODE; }

    @ActivityScoped
    @Binds
    abstract PermissionRequestHandler bindPermissionRequestHandler(RuntimePermissionRequestHandler runtimePermissionRequestHandler);

    @ActivityScoped
    @Provides
    static RuntimePermissionRequestHandler providePermissionRequestHandler(LocationActivity activity) {
        return new RuntimePermissionRequestHandler(activity);
    }

    @Provides
    @ElementsIntoSet
    static Set<LocationProvider> provideLocationProviders(AndroidLocationProvider alp, QRCodeScanner qrs) {
        return new HashSet<LocationProvider>(Arrays.asList(alp, qrs));
    }

}
