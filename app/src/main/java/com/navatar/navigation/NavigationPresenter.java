package com.navatar.navigation;

import android.support.annotation.Nullable;

import com.navatar.common.TextToSpeechProvider;
import com.navatar.data.Route;
import com.navatar.data.source.RoutesRepository;
import com.navatar.di.ActivityScoped;
import com.navatar.pathplanning.Path;

import javax.inject.Inject;

import io.reactivex.disposables.CompositeDisposable;

@ActivityScoped
public final class NavigationPresenter implements NavigationContract.Presenter {

    private final CompositeDisposable disposables = new CompositeDisposable();

    private final RoutesRepository mRoutesRepository;
    private final TextToSpeechProvider mTTSProvider;

    @Nullable
    private NavigationContract.View mNavView;

    @Nullable
    private Route mRoute;

    @Inject
    public NavigationPresenter(RoutesRepository navHistoryRepository,
                               TextToSpeechProvider textToSpeechProvider) {
        mRoutesRepository = navHistoryRepository;
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

        Path path = route.getPath();

        if(path == null) {
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

    @Override
    public void loadData() {
        mRoute = mRoutesRepository.getSelectedRoute();

    }
}
