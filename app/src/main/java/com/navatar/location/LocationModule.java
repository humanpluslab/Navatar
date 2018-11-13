package com.navatar.location;

import android.Manifest;

import com.navatar.di.ActivityScoped;
import com.navatar.di.FragmentScoped;
import com.navatar.location.details.AndroidLocationProvider;
import com.navatar.common.PermissionRequestHandler;
import com.navatar.common.details.RuntimePermissionRequestHandler;
import com.navatar.location.model.Location;

import javax.inject.Named;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import dagger.android.ContributesAndroidInjector;

/**
 * This is a Dagger module. We use this to pass in the View dependency to the
 * {@link LocationPresenter}.
 */
@Module
public abstract class LocationModule {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 144;

    @Binds
    abstract LocationContract.View provideView(LocationActivity activity);


    @Binds
    abstract LocationContract.Presenter providePresenter(LocationPresenter presenter);

    @Binds
    abstract LocationProvider providerLocationProvider(AndroidLocationProvider locationProvider);

    @Provides
    @Named("locationReqCode")
    static Integer provideLocationReqCode() {
        return LOCATION_PERMISSION_REQUEST_CODE;
    }


    @ActivityScoped
    @Binds
    abstract PermissionRequestHandler bindPermissionRequestHandler(RuntimePermissionRequestHandler runtimePermissionRequestHandler);

    @ActivityScoped
    @Provides
    static RuntimePermissionRequestHandler providePermissionRequestHandler(LocationActivity activity, @Named("locationReqCode") Integer reqCode) {
        return new RuntimePermissionRequestHandler(activity, Manifest.permission.ACCESS_FINE_LOCATION, reqCode);
    }

}
