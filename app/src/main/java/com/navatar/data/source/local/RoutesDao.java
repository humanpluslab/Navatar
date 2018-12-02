package com.navatar.data.source.local;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.navatar.data.Route;

import java.util.List;

@Dao
public interface RoutesDao {

    @Query("SELECT * FROM routes")
    List<Route> getRoutes();

    @Query("SELECT * from routes WHERE entryid = :routeid")
    Route getRouteById(String routeid);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertRoute(Route route);

    @Query("DELETE FROM routes WHERE entryid = :routeid")
    int deleteRouteById(String routeid);
}
