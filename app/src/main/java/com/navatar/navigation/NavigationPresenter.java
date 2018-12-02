package com.navatar.navigation;

import android.support.annotation.Nullable;

import com.navatar.common.TextToSpeechProvider;
import com.navatar.data.Route;
import com.navatar.data.source.NavHistoryRepository;
import com.navatar.di.ActivityScoped;

import javax.inject.Inject;

import io.reactivex.disposables.CompositeDisposable;

@ActivityScoped
public final class NavigationPresenter implements NavigationContract.Presenter {

    private final CompositeDisposable disposables = new CompositeDisposable();

    //private final NavHistoryRepository mNavHistoryRepository;
    private final TextToSpeechProvider mTTSProvider;

    @Nullable
    private NavigationContract.View mNavView;

    @Inject
    public NavigationPresenter(TextToSpeechProvider textToSpeechProvider) {
        //mNavHistoryRepository = navHistoryRepository;
        mTTSProvider = textToSpeechProvider;
    }

    @Override
    public void takeView(NavigationContract.View view) {
        mNavView = view;
    }

    @Override
    public void dropView() {
        mNavView = null;
    }

    @Override
    public void startNavigation(Route route) {

        if(route.getPath() == null) {
            mTTSProvider.speak("No path found");
            return;
        }

    }

    @Override
    public void reverseRoute() {

    }

    @Override
    public void nextStep() {

    }

    @Override
    public void addLandmark() {

    }
}
