package com.navatar.data.source;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;

import com.navatar.data.Map;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import com.google.common.base.Optional;
import com.navatar.location.model.Geofence;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Flowable;

import static com.google.common.base.Preconditions.checkNotNull;

@Singleton
public class MapsRepository implements MapsDataSource {

    private final MapsDataSource mMapsRemoteDataSource;
    private final MapsDataSource mMapsLocalDataSource;

    @VisibleForTesting
    @Nullable
    java.util.Map<String, com.navatar.data.Map> mCachedMaps;

    @VisibleForTesting
    @Nullable
    List<Geofence> mCachedGeofences;

    /**
     * Marks the cache as invalid, to force an update the next time data is requested. This variable
     * has package local visibility so it can be accessed from tests.
     */
    @VisibleForTesting
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
    public Flowable<List<com.navatar.data.Map>> getMaps() {
        // Respond immediately with cache if available and not dirty
        if (mCachedMaps != null && !mCacheIsDirty) {
            return Flowable.fromIterable(mCachedMaps.values()).toList().toFlowable();
        } else if (mCachedMaps == null) {
            mCachedMaps = new LinkedHashMap<>();
        }

        Flowable<List<com.navatar.data.Map>> remoteTasks = getAndSaveRemoteMaps();

        if (mCacheIsDirty) {
            return remoteTasks;
        } else {
            // Query the local storage if available. If not, query the network.
            Flowable<List<com.navatar.data.Map>> localMaps = getAndCacheLocalMaps();
            return Flowable.concat(localMaps, remoteTasks)
                    .filter(maps -> !maps.isEmpty())
                    .firstOrError()
                    .toFlowable();
        }

    }

    private Flowable<List<Map>> getAndCacheLocalMaps() {
        return mMapsLocalDataSource.getMaps()
                .flatMap(tasks -> Flowable.fromIterable(tasks)
                        .doOnNext(task -> mCachedMaps.put(task.getId(), task))
                        .toList()
                        .toFlowable());
    }

    private Flowable<List<Map>> getAndSaveRemoteMaps() {
        return mMapsRemoteDataSource
                .getMaps()
                .flatMap(maps -> Flowable.fromIterable(maps).doOnNext(map -> {
                    mMapsLocalDataSource.saveMap(map);
                    mCachedMaps.put(map.getId(), map);
                }).toList().toFlowable())
                .doOnComplete(() -> mCacheIsDirty = false);
    }


    @Override
    public Flowable<List<Geofence>> getGeofences() {
        if (mCachedGeofences != null && !mCacheIsDirty) {
            return Flowable.fromIterable(mCachedGeofences).toList().toFlowable();
        } else if (mCachedGeofences == null) {
            mCachedGeofences = new ArrayList<>();
        }

        Flowable<List<Geofence>> remoteGeofences = getRemoteGeofences();

        if (mCacheIsDirty) {
            return remoteGeofences;
        } else {
            // Query the local storage if available. If not, query the network.
            Flowable<List<Geofence>> localGeofences = getAndCacheLocalGeofences();
            return Flowable.concat(localGeofences, remoteGeofences)
                    .filter(maps -> !maps.isEmpty())
                    .firstOrError()
                    .toFlowable();
        }

    }


    private Flowable<List<Geofence>> getAndCacheLocalGeofences() {
        return mMapsLocalDataSource.getGeofences()
                .flatMap(gfs -> Flowable.fromIterable(gfs)
                        .doOnNext(gf -> mCachedGeofences.add(gf))
                        .toList()
                        .toFlowable());
    }

    private Flowable<List<Geofence>> getRemoteGeofences() {
        return mMapsRemoteDataSource
                .getGeofences()
                .flatMap(gfs -> Flowable.fromIterable(gfs).doOnNext(gf -> {
                    mCachedGeofences.add(gf);
                }).toList().toFlowable())
                .doOnComplete(() -> mCacheIsDirty = false);
    }

    @Override
    public Flowable<Optional<Map>> getMap(@NonNull String mapId) {
        checkNotNull(mapId);

        final Map cachedMap = getMapWithId(mapId);

        // Respond immediately with cache if available
        if (cachedMap != null) {
            return Flowable.just(Optional.of(cachedMap));
        }

        // Load from server/persisted if needed.

        // Do in memory cache update to keep the app UI up to date
        if (mCachedMaps == null) {
            mCachedMaps = new LinkedHashMap<>();
        }

        // Is the task in the local data source? If not, query the network.
        Flowable<Optional<Map>> localMap = getMapWithIdFromLocalRepository(mapId);
        Flowable<Optional<Map>> remoteTask = mMapsRemoteDataSource
                .getMap(mapId)
                .doOnNext(mapOptional -> {
                    if (mapOptional.isPresent()) {
                        Map map = mapOptional.get();
                        mMapsLocalDataSource.saveMap(map);
                        mCachedMaps.put(map.getId(), map);
                    }
                });

        return Flowable.concat(localMap, remoteTask)
                .firstElement()
                .toFlowable();

    }

    @Override
    public void saveMap(@NonNull Map map) {
        checkNotNull(map);
        mMapsRemoteDataSource.saveMap(map);
        mMapsLocalDataSource.saveMap(map);

        if (mCachedMaps == null) {
            mCachedMaps = new LinkedHashMap<>();
        }
        mCachedMaps.put(map.getId(), map);
    }

    @Nullable
    private Map getMapWithId(@NonNull String id) {
        checkNotNull(id);
        if (mCachedMaps == null || mCachedMaps.isEmpty()) {
            return null;
        } else {
            return mCachedMaps.get(id);
        }
    }

    @NonNull
    Flowable<Optional<Map>> getMapWithIdFromLocalRepository(@NonNull final String mapId) {
        return mMapsLocalDataSource
                .getMap(mapId)
                .doOnNext(taskOptional -> {
                    if (taskOptional.isPresent()) {
                        mCachedMaps.put(mapId, taskOptional.get());
                    }
                })
                .firstElement().toFlowable();
    }
}
