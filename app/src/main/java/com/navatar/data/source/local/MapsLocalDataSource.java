package com.navatar.data.source.local;

import android.content.Context;
import android.content.res.AssetManager;
import android.support.annotation.NonNull;

import com.navatar.data.Map;
import com.navatar.data.source.MapsDataSource;
import com.navatar.util.schedulers.BaseSchedulerProvider;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import com.google.common.base.Optional;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.Observable;

import static com.google.common.base.Preconditions.checkNotNull;


@Singleton
public class MapsLocalDataSource implements MapsDataSource {

    private AssetManager assetManager;


    @Inject
    public MapsLocalDataSource(@NonNull Context context,
                               @NonNull BaseSchedulerProvider schedulerProvider) {
        checkNotNull(context, "context cannot be null");
        checkNotNull(schedulerProvider, "scheduleProvider cannot be null");
        assetManager = context.getAssets();
    }

    @Override
    public Flowable<List<Map>> getMaps() {
        // Get campus files
        try {
            String[] campusFiles = assetManager.list("maps");

            Observable observable = Observable.fromIterable(Arrays.asList(campusFiles));

            return observable
                        .filter(n -> !((String)n).endsWith(".json"))
                        .map(n -> new Map((String)n, ((String)n).replace('_', ' ')))
                        .toList()
                        .toFlowable();

        } catch (IOException e) {
            return Flowable.empty();
        }
    }

    @Override
    public Flowable<Optional<Map>> getMap(@NonNull String mapId) {
        return Observable.just(Optional.of(new Map("",""))).toFlowable(BackpressureStrategy.LATEST);
    }

    @Override
    public void saveMap(Map map) {

    }

    @Override
    public void activateMap(@NonNull Map map) {
        activateMap(map.getId());
    }

    @Override
    public void activateMap(@NonNull String mapId) {

    }

    @Override
    public void refreshMaps() {

    }

}
