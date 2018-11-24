package com.navatar.main;

import com.navatar.common.PermissionRequestHandler;
import com.navatar.common.details.RuntimePermissionRequestHandler;
import com.navatar.di.ActivityScoped;
import com.navatar.location.GeofencingProvider;
import com.navatar.location.LocationActivity;
import com.navatar.location.LocationContract;
import com.navatar.location.LocationPresenter;
import com.navatar.location.LocationProvider;
import com.navatar.location.details.AndroidGeofencingProvider;
import com.navatar.location.details.AndroidLocationProvider;
import com.navatar.location.details.QRCodeScanner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Named;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import dagger.multibindings.ElementsIntoSet;

/**
 * This is a Dagger module. We use this to pass in the View dependency to the
 * {@link LocationPresenter}.
 */
@Module
public abstract class MainModule {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 144;
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 250;

    @Provides
    @Named("locationReqCode")
    static Integer provideLocationReqCode() {
        return LOCATION_PERMISSION_REQUEST_CODE;
    }

    @Provides
    @Named("cameraReqCode")
    static Integer provideCameraReqCode() { return CAMERA_PERMISSION_REQUEST_CODE; }

    @Provides
    @Named("requestCodes")
    static List<Integer> provideRequestCodes() {
        return new ArrayList<Integer>(
                Arrays.asList(
                    LOCATION_PERMISSION_REQUEST_CODE,
                    CAMERA_PERMISSION_REQUEST_CODE
                ));
    }

    @Binds
    abstract MainContract.View provideView(MainFragment fragment);

    @Binds
    abstract MainContract.Presenter providePresenter(MainPresenter presenter);

    @Binds
    abstract LocationProvider provideLocationProvider(AndroidLocationProvider locationProvider);

    @Binds
    abstract GeofencingProvider provideGeofencingProvider(AndroidGeofencingProvider geofencingProvider);

    @ActivityScoped
    @Binds
    abstract PermissionRequestHandler bindPermissionRequestHandler(RuntimePermissionRequestHandler runtimePermissionRequestHandler);

    @ActivityScoped
    @Provides
    static RuntimePermissionRequestHandler providePermissionRequestHandler(MainFragment fragment) {
        return new RuntimePermissionRequestHandler(fragment);
    }

    @Provides
    @ElementsIntoSet
    static Set<LocationProvider> provideLocationProviders(AndroidLocationProvider alp, QRCodeScanner qrs) {
        return new HashSet<LocationProvider>(Arrays.asList(alp, qrs));
    }

}
