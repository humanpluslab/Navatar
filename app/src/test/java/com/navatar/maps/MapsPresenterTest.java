package com.navatar.maps;

import com.google.common.collect.Lists;
import com.navatar.data.Map;
import com.navatar.data.source.MapsRepository;
import com.navatar.data.source.RoutesRepository;
import com.navatar.location.GeofencingProvider;
import com.navatar.util.schedulers.BaseSchedulerProvider;
import com.navatar.util.schedulers.ImmediateSchedulerProvider;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.mockito.Mockito.verify;

public class MapsPresenterTest {


    private static List<Map> MAPS;

    @Mock
    private MapsRepository mMapsRepository;

    @Mock
    private GeofencingProvider mGeofencingProvider;

    @Mock
    private MapsContract.View mMapsView;

    @Mock
    private RoutesRepository mRoutesRepository;

    private BaseSchedulerProvider mSchedulerProvider;

    private MapsPresenter mMapsPresenter;

    @Before
    public void setupMapsPresenter() {

        MockitoAnnotations.initMocks(this);

        mSchedulerProvider = new ImmediateSchedulerProvider();

        mMapsPresenter = new MapsPresenter();

        MAPS = Lists.newArrayList(new Map("Test1", "Test 1"), new Map("Test2", "Test 2"));
    }

    @Test
    public void createPresenter_setsThePresenterToView() {

        mMapsPresenter = new MapsPresenter();

        // verify(mMapsView).
    }

}
