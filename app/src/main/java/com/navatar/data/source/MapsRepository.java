package com.navatar.data.source;

import android.support.annotation.NonNull;

import com.navatar.data.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class MapsRepository implements MapsDataSource {

    private final MapsDataSource mMapsRemoteDataSource;
    private final MapsDataSource mMapsLocalDataSource;
    /**
     * Marks the cache as invalid, to force an update the next time data is requested. This variable
     * has package local visibility so it can be accessed from tests.
     */
    boolean mCacheIsDirty = false;

    @Inject
    MapsRepository(@Remote MapsDataSource mapsRemoteDataSource,
                    @Local MapsDataSource mapsLocalDataSource) {
        mMapsRemoteDataSource = mapsRemoteDataSource;
        mMapsLocalDataSource = mapsLocalDataSource;
    }

    @Override
    public void refreshMaps() {
        mCacheIsDirty = true;
    }

    @Override
    public void getMaps(@NonNull LoadMapsCallback callback) { }

    @Override
    public void getMap(@NonNull String mapId, @NonNull GetMapCallback callback) {}

    @Override
    public void activateMap(@NonNull Map map) {}

    @Override
    public void activateMap(@NonNull String mapId) {}


}
