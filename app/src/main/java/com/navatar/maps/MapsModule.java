package com.navatar.maps;

import com.navatar.di.ActivityScoped;
import com.navatar.di.FragmentScoped;

import dagger.Binds;
import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class MapsModule {

    @FragmentScoped
    @ContributesAndroidInjector
    abstract MapsFragment mapsFragment();

    @ActivityScoped
    @Binds
    abstract MapsContract.Presenter providePresenter(MapsPresenter presenter);

    @ActivityScoped
    @Binds
    abstract MapsContract.Navigator provideNavigator(MapsNavigator navigator);

}
