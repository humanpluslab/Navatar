package com.navatar.data.source;

import android.app.Application;
import android.arch.persistence.room.Room;

import com.navatar.data.source.local.RoutesDatabase;
import com.navatar.data.source.local.RoutesDao;
import com.navatar.data.source.local.RoutesLocalDataSource;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class RoutesRepositoryModule {

    @Singleton
    @Provides
    @Local
    RoutesDataSource provideNavHistoryLocalDataSource() {
        return new RoutesLocalDataSource();
    }

    @Singleton
    @Provides
    RoutesDatabase provideDb(Application context) {
        return Room.databaseBuilder(context.getApplicationContext(), RoutesDatabase.class, "Routes.db")
                .build();
    }

    @Singleton
    @Provides
    RoutesDao provideTasksDao(RoutesDatabase db) {
        return db.routesDao();
    }
}
