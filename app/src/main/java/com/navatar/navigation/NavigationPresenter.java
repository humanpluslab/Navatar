package com.navatar.navigation;

import android.support.annotation.Nullable;
import android.util.Log;

import com.navatar.common.TextToSpeechProvider;
import com.navatar.data.Landmark;
import com.navatar.data.Route;
import com.navatar.data.source.RoutesRepository;
import com.navatar.di.ActivityScoped;
import com.navatar.location.GeofencingProvider;
import com.navatar.location.LocationInteractor;
import com.navatar.math.Angles;
import com.navatar.pathplanning.Path;

import javax.inject.Inject;

import io.reactivex.disposables.CompositeDisposable;

@ActivityScoped
public final class NavigationPresenter implements NavigationContract.Presenter {

    private final String TAG = NavigationPresenter.class.getSimpleName();

    private final CompositeDisposable disposables = new CompositeDisposable();

    @Inject
    RoutesRepository mRoutesRepository;

    @Inject
    TextToSpeechProvider mTTSProvider;

    @Inject
    GeofencingProvider mGeofencingProvider;

    @Inject
    LocationInteractor mLocationInteractor;

    @Nullable
    private NavigationContract.View mNavView;

    @Nullable
    private Route mRoute;

    private int mPathIndex;

    private Path mPath;

    private int mOrientation;

    @Inject
    public NavigationPresenter() { }

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

        mPathIndex = 0;
        mPath = mRoute.getPath();

        if(mPath == null) {
            mNavView.showDirection("No path found");
            return;
        }

        mNavView.showStepCount(mPathIndex, mPath.getLength());
    }

    @Override
    public void reverseRoute() {
        Landmark lmTo = mRoute.getToLandmark();
        mRoute.setToLandmark(mRoute.getFromLandmark());
        mRoute.setFromLandmark(lmTo);
        startNavigation();
    }

    @Override
    public void nextStep() {

        String navigationCommand = getNextDirection();
        if(!navigationCommand.matches("(?i:Turn.*)")){
            mPathIndex++;
        }

        if (mPathIndex >= mPath.getLength()) {
            mNavView.showReachedDestination();
        } else {
            mNavView.showStepCount(mPathIndex, mPath.getLength());
        }

        mNavView.showDirection(navigationCommand);

    }

    @Override
    public void addLandmark() {

    }

    @Override
    public void loadData() {
        mRoute = mRoutesRepository.getSelectedRoute();

        getLocation();

        if (mRoute != null)
            startNavigation();
        else
            mNavView.showDirection("No route found");
    }

    private String getNextDirection() {

        if (mPathIndex >= mPath.getLength() - 1)
            return mPath.getStep(mPath.getLength() - 1).getDirectionString();

        double x1 = mPath.getStep(mPathIndex).getParticleState().getX();
        double y1 = mPath.getStep(mPathIndex).getParticleState().getY();
        double x2 = mPath.getStep(mPathIndex + 1).getParticleState().getX();
        double y2 = mPath.getStep(mPathIndex + 1).getParticleState().getY();
        Double angle = Math.atan(y2 - y1 / x2 - x1) * 180.0 / Math.PI;
        angle = Angles.polarToCompass(angle);
        angle = angle - mOrientation;

        if (angle > 180.0)
            angle = 360.0 - angle;
        else if (angle < -180.0)
            angle = -360.0 - angle;
        if (angle <= 45.0 && angle >= -45.0) {
            return mPath.getStep(mPathIndex).getDirectionString();
        } else if (angle > 45.0 && angle <= 135.0) {
            return "Turn right";
        } else if (angle < -45.0 && angle >= -135.0) {
            return "Turn left";
        } else {
            return "Turn around";
        }
    }


    private void getLocation() {
        disposables.add(mLocationInteractor.getLocationUpdates()
            .subscribe(
                location -> {
                    Log.e(TAG, "Lat: " + location.latitude() + " Long: " + location.longitude());
                },
                throwable -> {
                    Log.e(TAG, "Error while getting location", throwable);
                }
            )
        );

    }
}
