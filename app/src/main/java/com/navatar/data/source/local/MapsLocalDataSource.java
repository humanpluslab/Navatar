package com.navatar.data.source.local;

import android.support.annotation.NonNull;

import com.navatar.data.Map;
import com.navatar.data.source.MapsDataSource;
import com.navatar.util.AppExecutors;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import static com.google.common.base.Preconditions.checkNotNull;


@Singleton
public class MapsLocalDataSource implements MapsDataSource {

    private final AppExecutors mAppExecutors;

    @Inject
    public MapsLocalDataSource(@NonNull AppExecutors executors) {
        mAppExecutors = executors;
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



                final List<Map> maps = mTasksDao.getTasks();

                mAppExecutors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        if (maps.isEmpty()) {
                            // This will be called if the table is new or just empty.
                            callback.onDataNotAvailable();
                        } else {
                            callback.onMapsLoaded(maps);
                        }
                    }
                });
            }
        };

        mAppExecutors.diskIO().execute(runnable);
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
                final Map map = mTasksDao.getTaskById(mapId);

                mAppExecutors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        if (map != null) {
                            callback.onMapLoaded(map);
                        } else {
                            callback.onDataNotAvailable();
                        }
                    }
                });
            }
        };

        mAppExecutors.diskIO().execute(runnable);
    }


}
