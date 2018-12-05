package com.navatar.routes;

import com.navatar.di.ActivityScoped;
import com.navatar.di.FragmentScoped;

import dagger.Binds;
import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class RoutesModule {


    @FragmentScoped
    @ContributesAndroidInjector
    abstract RoutesFragment routesFragment();

    @ActivityScoped
    @Binds
    abstract RoutesContract.Presenter routesPresenter(RoutesPresenter presenter);


}
