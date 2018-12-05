package com.navatar.data.source.local;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.navatar.data.Route;
import com.navatar.data.source.RouteData;

import io.reactivex.Flowable;

@Dao
public interface RoutesDao {

    @Query("SELECT * FROM routes")
    Flowable<RouteData> getRoutes();

    @Query("SELECT * from routes WHERE entryid = :routeid")
    RouteData getRouteById(String routeid);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertRoute(RouteData route);

    @Query("DELETE FROM routes WHERE entryid = :routeid")
    int deleteRouteById(String routeid);
}
