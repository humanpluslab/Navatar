package com.navatar.data.source;

import android.support.annotation.NonNull;

import com.navatar.data.Map;

import java.util.List;
import java.util.Optional;

import io.reactivex.Flowable;

public interface MapsDataSource {

    Flowable<List<Map>> getMaps();

    Flowable<Optional<Map>> getMap(@NonNull String mapId);

    void activateMap(@NonNull Map map);

    void activateMap(@NonNull String mapId);

    void refreshMaps();

}
