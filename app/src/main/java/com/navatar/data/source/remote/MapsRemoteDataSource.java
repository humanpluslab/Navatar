package com.navatar.data.source.remote;

import android.support.annotation.NonNull;

import com.navatar.data.Map;
import com.navatar.data.source.MapsDataSource;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class MapsRemoteDataSource implements MapsDataSource {

    @Inject
    public MapsRemoteDataSource() {

    }

    /**
     * Note: {@link LoadMapsCallback#onDataNotAvailable()} is fired if the database doesn't exist
     * or the table is empty.
     */
    @Override
    public void getMaps(@NonNull final LoadMapsCallback callback) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {

            }
        };

    }

    /**
     * Note: {@link GetTaskCallback#onDataNotAvailable()} is fired if the {@link Task} isn't
     * found.
     */
    @Override
    public void getMap(@NonNull final String mapId, @NonNull final GetMapCallback callback) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {

            }
        };

    }

    @Override
    public void refreshMaps() {

    }

    @Override
    public void activateMap(@NonNull Map map) {}

    @Override
    public void activateMap(@NonNull String mapId) {}

}
