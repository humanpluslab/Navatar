package com.navatar.maps;

import android.support.annotation.Nullable;
import android.util.Log;

import com.navatar.common.TextToSpeechProvider;
import com.navatar.data.Building;
import com.navatar.data.Landmark;
import com.navatar.data.Map;
import com.navatar.data.Route;
import com.navatar.data.source.MapsRepository;
import com.navatar.data.source.RoutesRepository;
import com.navatar.location.GeofencingProvider;
import com.navatar.location.LocationInteractor;
import com.navatar.location.model.Location;
import com.navatar.location.model.NoLocationAvailableException;
import com.navatar.pathplanning.Path;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.disposables.CompositeDisposable;

public class MapsPresenter implements MapsContract.Presenter {

    private static final String TAG = MapsPresenter.class.getSimpleName();

    private final CompositeDisposable disposables = new CompositeDisposable();

    @Inject
    GeofencingProvider mGeofencingProvider;

    @Inject
    MapsRepository mMapRepository;

    @Inject
    RoutesRepository mRoutesRepository;

    @Inject
    LocationInteractor mLocationInteractor;

    @Inject
    TextToSpeechProvider mTTSProvider;

    @Nullable
    private MapsContract.View mMapsView;

    @Nullable
    private Route mRoute;

    @Inject
    public MapsPresenter() { }

    @Override
    public void loadData() {

        disposables.add(mMapRepository.getMaps()
            .subscribe(
                mMapsView::showMaps,
                throwable -> Log.e(TAG, "An error occurred map provider stream", throwable)
            ));

        disposables.add(mMapRepository.getGeofences()
            .subscribe(

            ));

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

    @Override
    public void onMapSelected(Map map) {
        mMapsView.showMap(map);
        mRoute = new Route(map);
    }

    @Override
    public void onBuildingSelected(Building building) {
        mRoute.setBuilding(building);
        mMapsView.showFromLandmark(building.destinations());
    }

    @Override
    public void onFromLandmarkSelected(Landmark landmark) {
        mRoute.setFromLandmark(landmark);
        List<Landmark> landmarks = new ArrayList<>(mRoute.getBuilding().destinations());
        landmarks.remove(landmark);
        mMapsView.showToLandmark(landmarks);
    }

    @Override
    public void onToLandmarkSelected(Landmark landmark) {
        mRoute.setToLandmark(landmark);
        Path path = mRoute.getBuilding().getRoute(mRoute.getFromLandmark(), landmark);

        if (path != null) {
            mRoute.setPath(path);
            mRoutesRepository.setSelectedRoute(mRoute);
            mMapsView.showNavigation(mRoute);
        } else {
            mTTSProvider.speak("No path found");
            mMapsView.noRouteFound();
            Log.e(TAG, "No path found");
        }
    }

    @Override
    public void cleanup() {
        disposables.clear();
    }

    @Override
    public void takeView(MapsContract.View view) {
        mMapsView = view;
        loadData();
    }

    @Override
    public void dropView() {
        mMapsView = null;
        disposables.clear();
    }
}
