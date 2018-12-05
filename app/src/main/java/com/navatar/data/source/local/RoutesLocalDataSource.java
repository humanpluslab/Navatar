package com.navatar.data.source.local;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.navatar.data.Route;
import com.navatar.data.source.MapsRepository;
import com.navatar.data.source.RouteData;
import com.navatar.data.source.RoutesDataSource;

import com.google.common.base.Optional;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Flowable;

@Singleton
public class RoutesLocalDataSource implements RoutesDataSource {

    private MapsRepository mMapsRepository;

    @Nullable
    private Route mSelectedRoute;

    private final RoutesDao mRoutesDao;

    @Inject
    public RoutesLocalDataSource(RoutesDao dao, MapsRepository mapsRepository) {
        mRoutesDao = dao;
        mMapsRepository = mapsRepository;
    }

    @Override
    public Flowable<Route> getRoutes() {
        return mRoutesDao
                .getRoutes()
                .flatMap(this::fromRouteData);
    }

    private Flowable<Route> fromRouteData(RouteData data) {
        return mMapsRepository.getMap(data.getMapId())
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(m -> new Route(m, data));
    }


    @Override
    @Nullable
    public Route getSelectedRoute() {
        return mSelectedRoute;
    }

    @Override
    public void setSelectedRoute(@NonNull Route route){
        mSelectedRoute = route;
    }
}
