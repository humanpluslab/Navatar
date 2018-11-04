package com.navatar.location;

import com.navatar.di.ActivityScoped;
import com.navatar.di.FragmentScoped;

import dagger.Binds;
import dagger.Module;
import dagger.android.ContributesAndroidInjector;

/**
 * This is a Dagger module. We use this to pass in the View dependency to the
 * {@link LocationPresenter}.
 */
@Module
public abstract class LocationModule {
    @FragmentScoped
    @ContributesAndroidInjector
    abstract LocationFragment locationFragment();

    @ActivityScoped
    @Binds abstract LocationContract.Presenter locationPresenter(LocationPresenter presenter);

}
