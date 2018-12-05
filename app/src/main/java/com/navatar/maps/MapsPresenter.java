package com.navatar.maps;

import android.support.annotation.Nullable;
import android.util.Log;

import com.navatar.data.Building;
import com.navatar.data.Landmark;
import com.navatar.data.Map;
import com.navatar.data.Route;
import com.navatar.data.source.MapsRepository;
import com.navatar.data.source.RoutesRepository;
import com.navatar.location.GeofencingProvider;
import com.navatar.pathplanning.Path;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.disposables.CompositeDisposable;

public class MapsPresenter implements MapsContract.Presenter {

    private static final String TAG = MapsPresenter.class.getSimpleName();

    private final CompositeDisposable disposables = new CompositeDisposable();
    private final GeofencingProvider mGeofencingProvider;
    private final MapsRepository mMapRepository;
    private final RoutesRepository mRoutesRepository;

    @Nullable
    private MapsContract.View mMapsView;

    @Nullable
    private Route mRoute;

    @Inject
    public MapsPresenter(MapsRepository mapsRepository, GeofencingProvider geofencingProvider, RoutesRepository routes) {
        mMapRepository = mapsRepository;
        mGeofencingProvider = geofencingProvider;
        mRoutesRepository = routes;
    }

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
