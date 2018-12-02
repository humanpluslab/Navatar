package com.navatar.navigation;

import com.navatar.common.TextToSpeechProvider;
import com.navatar.data.source.NavHistoryRepository;
import com.navatar.util.schedulers.BaseSchedulerProvider;
import com.navatar.util.schedulers.ImmediateSchedulerProvider;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class NavigationPresenterTest {


    @Mock
    private final TextToSpeechProvider mTextToSpeechProvider;

    //@Mock
    //private final NavHistoryRepository mNavHistoryRepository;

    @Mock
    private final NavigationContract.View mNavView;


    private BaseSchedulerProvider mSchedulerProvider;

    private NavigationPresenter mNavigationPresenter;

    @Before
    public void setupNavigationPresenter() {

        MockitoAnnotations.initMocks(this);

        mSchedulerProvider = new ImmediateSchedulerProvider();

        mNavigationPresenter = new NavigationPresenter(mTextToSpeechProvider);

    }


    @Test
    public void createPresenter() {



    }
}

