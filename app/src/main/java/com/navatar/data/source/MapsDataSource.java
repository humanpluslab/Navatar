package com.navatar.data.source;

import android.support.annotation.NonNull;

import com.navatar.data.Map;

import java.util.List;

public interface MapsDataSource {

    interface LoadMapsCallback {

        void onMapsLoaded(List<Map> tasks);

        void onDataNotAvailable();
    }

    interface GetMapCallback {

        void onMapLoaded(Map task);

        void onDataNotAvailable();
    }


    void getMaps(@NonNull LoadMapsCallback callback);

    void getMap(@NonNull String mapId, @NonNull GetMapCallback callback);

    void activateMap(@NonNull Map map);

    void activateMap(@NonNull String mapId);

    void refreshMaps();

}
