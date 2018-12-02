package com.navatar.data.source.local;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;

import com.navatar.data.Route;

@Database(entities = {Route.class}, version = 1, exportSchema = false)
@TypeConverters({RouteTypeConverter.class})
public abstract class RoutesDatabase extends RoomDatabase {

    public abstract RoutesDao routesDao();

}
