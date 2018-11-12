package com.navatar.location;

import com.navatar.di.ActivityScoped;
import com.navatar.di.FragmentScoped;

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

    @Provides
    @Named("locationReqCode")
    static Integer provideLocationReqCode() {
        return LOCATION_PERMISSION_REQUEST_CODE;
    }

    @FragmentScoped
    @ContributesAndroidInjector
    abstract LocationFragment locationFragment();

    @ActivityScoped
    @Binds abstract LocationContract.Presenter locationPresenter(LocationPresenter presenter);

}
