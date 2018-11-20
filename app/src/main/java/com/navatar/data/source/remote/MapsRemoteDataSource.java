package com.navatar.data.source.remote;

import android.support.annotation.NonNull;

import com.google.common.base.Optional;
import com.navatar.data.Map;
import com.navatar.data.source.MapsDataSource;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Flowable;

@Singleton
public class MapsRemoteDataSource implements MapsDataSource {

    @Inject
    public MapsRemoteDataSource() {

    }

    @Override
    public Flowable<List<Map>> getMaps() {
        return Flowable.empty();
    }

    @Override
    public Flowable<Optional<Map>> getMap(@NonNull String mapId) {
        final Map map = new Map(mapId, mapId);
        if (map != null) {
            return Flowable.just(Optional.of(map));
        } else {
            return Flowable.empty();
        }
    }

    @Override
    public void refreshMaps() {

    }

    @Override
    public void saveMap(Map map) {

    }

    @Override
    public void activateMap(@NonNull Map map) {

    }

    @Override
    public void activateMap(@NonNull String mapId) {

    }

}
