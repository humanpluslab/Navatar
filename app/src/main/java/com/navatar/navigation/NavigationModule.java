package com.navatar.navigation;

import com.navatar.di.ActivityScoped;
import com.navatar.di.FragmentScoped;

import dagger.Binds;
import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class NavigationModule {
    @FragmentScoped
    @ContributesAndroidInjector
    abstract NavigationFragment navigationFragment();

    @ActivityScoped
    @Binds
    abstract NavigationContract.Presenter navigationPresenter(NavigationPresenter presenter);
}