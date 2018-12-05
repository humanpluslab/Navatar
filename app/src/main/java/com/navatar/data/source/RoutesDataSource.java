package com.navatar.data.source;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.navatar.data.Route;

import io.reactivex.Flowable;

public interface RoutesDataSource {

    Flowable<Route> getRoutes();

    @Nullable
    Route getSelectedRoute();

    void setSelectedRoute(@NonNull Route route);

}
