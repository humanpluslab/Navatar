package com.navatar.data.source;

import android.app.Application;
import android.arch.persistence.room.Room;

import com.navatar.data.source.local.RoutesDatabase;
import com.navatar.data.source.local.RoutesDao;
import com.navatar.data.source.local.RoutesLocalDataSource;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class RoutesRepositoryModule {

    @Inject
    MapsRepository mapsRepository;

    @Singleton
    @Provides
    @Local
    RoutesDataSource provideNavHistoryLocalDataSource(RoutesDatabase db) {
        return new RoutesLocalDataSource(db.routesDao(), mapsRepository);
    }

    @Singleton
    @Provides
    RoutesDatabase provideDb(Application context) {
        return Room.databaseBuilder(context.getApplicationContext(), RoutesDatabase.class, "Routes.db")
                .build();
    }

    @Singleton
    @Provides
    RoutesDao provideRoutesDao(RoutesDatabase db) {
        return db.routesDao();
    }
}
