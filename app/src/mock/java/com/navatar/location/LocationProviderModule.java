package com.navatar.location;

import javax.inject.Singleton;

import dagger.Binds;
import dagger.Module;

@Module
public abstract class LocationProviderModule {

    @Singleton
    @Binds
    abstract LocationSource provideLocation(FusedLocationProvider dataSource);

}