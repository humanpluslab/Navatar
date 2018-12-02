package com.navatar.maps;

import android.support.annotation.Nullable;
import android.util.Log;

import com.navatar.data.Building;
import com.navatar.data.Landmark;
import com.navatar.data.Map;
import com.navatar.data.Route;
import com.navatar.data.source.MapsRepository;
import com.navatar.location.GeofencingProvider;

import javax.inject.Inject;

import io.reactivex.disposables.CompositeDisposable;

public class MapsPresenter implements MapsContract.Presenter {

    private static final String TAG = MapsPresenter.class.getSimpleName();

    private final CompositeDisposable disposables = new CompositeDisposable();
    private final GeofencingProvider geofencingProvider;
    private final MapsRepository mMapRepository;
    private final MapsContract.Navigator mNavigator;


    @Nullable
    private MapsContract.View mMapsView;

    private Route mRoute;


    @Inject
    public MapsPresenter(MapsRepository mapsRepository, GeofencingProvider geofencingProvider, MapsContract.Navigator navigator) {
        this.mMapRepository = mapsRepository;
        this.geofencingProvider = geofencingProvider;
        this.mNavigator = navigator;
    }

    @Override
    public void loadData() {

        disposables.clear();

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
    }

    @Override
    public void onBuildingSelected(Building building) {
        //mRoute = new Route(building);
        mMapsView.showFromLandmark(building.destinations());
    }

    @Override
    public void onFromLandmarkSelected(Landmark landmark) {
        mRoute.setFromLandmark(landmark);
       // mMapsView.showToLandmark();
    }

    @Override
    public void onToLandmarkSelected(Landmark landmark) {
        mRoute.setToLandmark(landmark);
        //mMapsView.showNavigate(landmark);
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
    }
}
