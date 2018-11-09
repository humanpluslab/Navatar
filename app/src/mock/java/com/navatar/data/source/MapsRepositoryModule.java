package com.navatar.data.source;

import com.navatar.data.FakeMapsRemoteDataSource;
import com.navatar.data.source.local.MapsLocalDataSource;
import com.navatar.util.AppExecutors;
import com.navatar.util.DiskIOThreadExecutor;

import java.util.concurrent.Executors;

import javax.inject.Singleton;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;

@Module
public abstract class MapsRepositoryModule {
    private static final int THREAD_COUNT = 3;

    @Singleton
    @Binds
    @Local
    abstract MapsDataSource provideMapsLocalDataSource(MapsLocalDataSource dataSource);

    @Singleton
    @Binds
    @Remote
    abstract MapsDataSource provideMapsRemoteDataSource(FakeMapsRemoteDataSource dataSource);

    @Singleton
    @Provides
    static AppExecutors provideAppExecutors() {
        return new AppExecutors(new DiskIOThreadExecutor(),
                Executors.newFixedThreadPool(THREAD_COUNT),
                new AppExecutors.MainThreadExecutor());
    }

}
