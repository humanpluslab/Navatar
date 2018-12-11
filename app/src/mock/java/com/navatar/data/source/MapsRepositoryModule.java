package com.navatar.data.source;

import com.navatar.data.FakeMapsRemoteDataSource;
import com.navatar.data.source.local.MapsLocalDataSource;

import javax.inject.Singleton;

import dagger.Binds;
import dagger.Module;

@Module
public abstract class MapsRepositoryModule {

    @Singleton
    @Binds
    @Local
    abstract MapsDataSource provideMapsLocalDataSource(MapsLocalDataSource dataSource);

    @Singleton
    @Binds
    @Remote
    abstract MapsDataSource provideMapsRemoteDataSource(FakeMapsRemoteDataSource dataSource);

}
