package com.navatar.location;

import javax.inject.Singleton;

import dagger.Binds;
import dagger.Module;

@Module
public abstract class LocationManagerModule {

    private static final int THREAD_COUNT = 3;

    @Singleton
    @Binds
    abstract LocationSource provideLocation(GoogleLocationSource dataSource);

}