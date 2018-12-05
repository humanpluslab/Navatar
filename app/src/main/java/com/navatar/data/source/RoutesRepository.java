package com.navatar.data.source;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.navatar.data.Route;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Flowable;

@Singleton
public class RoutesRepository implements RoutesDataSource {


    private final RoutesDataSource mRoutesDataSource;

    @Inject
    RoutesRepository(@Local RoutesDataSource routesLocalDataSource) {
        mRoutesDataSource = routesLocalDataSource;
    }

    @Override
    @Nullable
    public Route getSelectedRoute() {
        return mRoutesDataSource.getSelectedRoute();
    }

    @Override
    public void setSelectedRoute(@NonNull Route route){
        mRoutesDataSource.setSelectedRoute(route);
    }

    @Override
    public Flowable<Route> getRoutes() {
        return mRoutesDataSource.getRoutes();
    }
}
