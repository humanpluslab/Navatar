package com.navatar.main;

import com.navatar.common.PermissionRequestHandler;
import com.navatar.common.details.RuntimePermissionRequestHandler;
import com.navatar.di.ActivityScoped;
import com.navatar.di.FragmentScoped;
import com.navatar.maps.MapsContract;
import com.navatar.maps.MapsFragment;
import com.navatar.maps.MapsPresenter;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Named;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import dagger.android.ContributesAndroidInjector;

/**
 * This is a Dagger module. We use this to pass in the View dependency to the
 * {@link MainPresenter}.
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

    @FragmentScoped
    @ContributesAndroidInjector
    abstract MainFragment mainFragment();

    @FragmentScoped
    @ContributesAndroidInjector
    abstract MapsFragment mapsFragment();

    @ActivityScoped
    @Binds
    abstract MainContract.Presenter providePresenter(MainPresenter presenter);

    @Binds
    abstract MainContract.Navigator provideNavigator(MainNavigator navigator);

    @ActivityScoped
    @Binds
    abstract MapsContract.Presenter provideMapsPresenter(MapsPresenter presenter);


}
