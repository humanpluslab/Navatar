package com.navatar.data;

import android.support.annotation.NonNull;

import com.google.common.base.Optional;
import com.navatar.data.source.MapsDataSource;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.Observable;

public class FakeMapsRemoteDataSource implements MapsDataSource {

    @Inject
    public FakeMapsRemoteDataSource() {

    }

    @Override
    public Flowable<List<Map>> getMaps() {
        return Flowable.empty();
    }

    @Override
    public Flowable<Optional<Map>> getMap(@NonNull String mapId) {
        return Flowable.empty();
    }

    @Override
    public void saveMap(Map map) {

    }

    @Override
    public void refreshMaps() {

    }

    @Override
    public Flowable<List<Geofence>> getGeofences() {
        return Flowable.empty();
    }

    @Override
    public void setSelectedMap(@NonNull Map map) {

    }

    @Nullable
    @Override
    public Map getSelectedMap() {
        return null;
    }
}
