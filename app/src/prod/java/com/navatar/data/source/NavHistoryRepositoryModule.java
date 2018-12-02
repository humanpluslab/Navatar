package com.navatar.data.source;

import android.app.Application;
import android.arch.persistence.room.Room;

import com.navatar.data.source.local.NavDatabase;
import com.navatar.data.source.local.NavHistoryDao;
import com.navatar.data.source.local.NavHistoryLocalDataSource;

import javax.inject.Singleton;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;

@Module
public class NavHistoryRepositoryModule {

    @Singleton
    @Provides
    @Local
    NavHistoryDataSource provideNavHistoryLocalDataSource() {
        return new NavHistoryLocalDataSource();
    }

    @Singleton
    @Provides
    NavDatabase provideDb(Application context) {
        return Room.databaseBuilder(context.getApplicationContext(), NavDatabase.class, "Nav.db")
                .build();
    }

    @Singleton
    @Provides
    NavHistoryDao provideTasksDao(NavDatabase db) {
        return db.navHistoryDao();
    }
}
