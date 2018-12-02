package com.navatar.data.source;

import com.navatar.data.source.remote.MapsRemoteDataSource;
import com.navatar.data.source.local.MapsLocalDataSource;
import com.navatar.util.schedulers.BaseSchedulerProvider;
import com.navatar.util.schedulers.ImmediateSchedulerProvider;

import javax.inject.Singleton;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;

@Module
public abstract class MapsRepositoryModule {

    @Singleton
    @Binds
    @Local
    abstract MapsDataSource provideMapsLocalDataSource(MapsLocalDataSource dataSource);

    @Singleton
    @Binds
    @Remote
    abstract MapsDataSource provideMapsRemoteDataSource(MapsRemoteDataSource dataSource);



}