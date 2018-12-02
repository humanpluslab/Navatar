package com.navatar.data.source.local;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.navatar.data.Route;
import com.navatar.data.source.RoutesDataSource;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class RoutesLocalDataSource implements RoutesDataSource {


    @Nullable
    private Route mSelectedRoute;

    @Inject
    public RoutesLocalDataSource() {

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
