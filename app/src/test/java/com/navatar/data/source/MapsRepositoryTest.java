package com.navatar.data.source;

import android.content.Context;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.navatar.data.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.List;


import io.reactivex.Flowable;
import io.reactivex.subscribers.TestSubscriber;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class MapsRepositoryTest {

    private final static String TASK_TITLE = "title";

    private final static String TASK_TITLE2 = "title2";

    private final static String TASK_TITLE3 = "title3";

    private static List<Map> MAPS = Lists.newArrayList(new Map("Title1", "Description1"),
            new Map("Title2", "Description2"));


    @Mock
    private MapsDataSource mMapsRemoteDataSource;

    @Mock
    private MapsDataSource mMapsLocalDataSource;

    @Mock
    private Context mContext;

    private MapsRepository mMapsRepository;

    private TestSubscriber<List<Map>> mMapsTestSubscriber;

    @Before
    public void setupMapsRepository() {
        // Mockito has a very convenient way to inject mocks by using the @Mock annotation. To
        // inject the mocks in the test the initMocks method needs to be called.
        MockitoAnnotations.initMocks(this);

        // Get a reference to the class under test
        mMapsRepository = new MapsRepository(
                mMapsRemoteDataSource, mMapsLocalDataSource);

        mMapsTestSubscriber = new TestSubscriber<>();
    }

    @Test
    public void getTasks_requestsAllMapsFromLocalDataSource() {
        // Given that the local data source has data available
        setMapsAvailable(mMapsLocalDataSource, MAPS);
        // And the remote data source does not have any data available
        setMapsNotAvailable(mMapsRemoteDataSource);

        // When maps are requested from the maps repository
        mMapsRepository.getMaps().subscribe(mMapsTestSubscriber);

        // Then maps are loaded from the local data source
        verify(mMapsLocalDataSource).getMaps();
        mMapsTestSubscriber.assertValue(MAPS);
    }

    @Test
    public void getTask_requestsSingleMapFromLocalDataSource() {
        // Given a stub map with id and name in the local repository
        Map map = new Map(TASK_TITLE, "Some Task Description");
        Optional<Map> mapOptional = Optional.of(map);
        setMapAvailable(mMapsLocalDataSource, mapOptional);
        // And the task not available in the remote repository
        setMapNotAvailable(mMapsRemoteDataSource, mapOptional.get().getId());

        // When a map is requested from the maps repository
        TestSubscriber<Optional<Map>> testSubscriber = new TestSubscriber<>();
        mMapsRepository.getMap(map.getId()).subscribe(testSubscriber);

        // Then the map is loaded from the database
        verify(mMapsLocalDataSource).getMap(eq(map.getId()));
        testSubscriber.assertValue(mapOptional);
    }


    private void setMapsNotAvailable(MapsDataSource dataSource) {
        when(dataSource.getMaps()).thenReturn(Flowable.just(Collections.emptyList()));
    }

    private void setMapsAvailable(MapsDataSource dataSource, List<Map> maps) {
        // don't allow the data sources to complete.
        when(dataSource.getMaps()).thenReturn(Flowable.just(maps).concatWith(Flowable.never()));
    }

    private void setMapNotAvailable(MapsDataSource dataSource, String taskId) {
        when(dataSource.getMap(eq(taskId))).thenReturn(Flowable.just(Optional.absent()));
    }

    private void setMapAvailable(MapsDataSource dataSource, Optional<Map> taskOptional) {
        when(dataSource.getMap(eq(taskOptional.get().getId()))).thenReturn(Flowable.just(taskOptional).concatWith(Flowable.never()));
    }

}
