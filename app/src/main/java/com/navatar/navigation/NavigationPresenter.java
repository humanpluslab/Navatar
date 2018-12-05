package com.navatar.navigation;

import android.support.annotation.Nullable;
import android.util.Log;

import com.navatar.common.TextToSpeechProvider;
import com.navatar.data.Route;
import com.navatar.data.source.RoutesRepository;
import com.navatar.di.ActivityScoped;
import com.navatar.location.GeofencingProvider;
import com.navatar.location.LocationProvider;
import com.navatar.pathplanning.Path;

import javax.inject.Inject;

import io.reactivex.disposables.CompositeDisposable;

@ActivityScoped
public final class NavigationPresenter implements NavigationContract.Presenter {

    private final String TAG = NavigationPresenter.class.getSimpleName();

    private final CompositeDisposable disposables = new CompositeDisposable();

    private final RoutesRepository mRoutesRepository;
    private final TextToSpeechProvider mTTSProvider;
    private final GeofencingProvider mGeofencingProvider;
    private final LocationProvider mLocationProvider;

    @Nullable
    private NavigationContract.View mNavView;

    @Nullable
    private Route mRoute;

    @Inject
    public NavigationPresenter(RoutesRepository navHistoryRepository,
                               TextToSpeechProvider textToSpeechProvider,
                               GeofencingProvider geofencingProvider,
                               LocationProvider locationProvider) {
        mRoutesRepository = navHistoryRepository;
        mTTSProvider = textToSpeechProvider;
        mGeofencingProvider = geofencingProvider;
        mLocationProvider = locationProvider;
    }

    @Override
    public void takeView(NavigationContract.View view) {
        mNavView = view;
        loadData();
    }

    @Override
    public void dropView() {
        mNavView = null;
    }

    @Override
    public void startNavigation() {

        Path path = mRoute.getPath();

        if(path == null) {
            mTTSProvider.speak("No path found");
            Log.e(TAG, "No path found");
            return;
        }

        mNavView.setStepCount(0);
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

        if (mRoute != null)
            startNavigation();
        else
            Log.e(TAG, "No route found");
    }
}
