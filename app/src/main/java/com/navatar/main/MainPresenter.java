package com.navatar.main;

import android.Manifest;
import android.util.Log;

import com.google.common.base.Optional;
import com.navatar.common.PermissionRequestHandler;
import com.navatar.data.Map;
import com.navatar.data.source.MapsRepository;
import com.navatar.location.LocationContract;
import com.navatar.location.LocationInteractor;
import com.navatar.location.model.NoLocationAvailableException;

import java.lang.ref.WeakReference;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import io.reactivex.disposables.CompositeDisposable;

public class MainPresenter implements MainContract.Presenter {

    private static final String TAG = MainPresenter.class.getSimpleName();

    private final WeakReference<MainContract.View> viewWeakReference;
    private final LocationInteractor interactor;
    private PermissionRequestHandler permissionRequestHandler;
    private final CompositeDisposable disposables = new CompositeDisposable();

    private final MapsRepository mMapRepository;

    @Inject
    @Named("locationReqCode")
    Integer locationRequestCode;

    @Inject
    @Named("cameraReqCode")
    Integer cameraRequestCode;

    @Inject
    public MainPresenter(MainContract.View view,
                         LocationInteractor interactor,
                         PermissionRequestHandler permissionRequestHandler,
                         MapsRepository mapsRepository) {
        this.viewWeakReference = new WeakReference<>(view);
        this.interactor = interactor;
        this.permissionRequestHandler = permissionRequestHandler;
        this.mMapRepository = mapsRepository;
    }

    @Override
    public void loadData() {

        disposables.clear();

        disposables.add(permissionRequestHandler.requestPermissions(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.CAMERA)
                .subscribe(
                        this::handlePermissionsResult,
                        throwable -> Log.e(TAG, "An error occurred getting permissions", throwable)
                ));

        disposables.add(mMapRepository.getMaps()
                .subscribe(
                        this::handleMapsResult,
                        throwable -> Log.e(TAG, "An error occurred map provider stream", throwable)
                ));

    }

    @Override
    public void onMapSelected(String mapName) {
        disposables.add(mMapRepository.getMap(mapName)
                .subscribe(
                    this::handleBuildingResult,
                        throwable -> Log.e(TAG, "An error occurred map provider stream", throwable)
                ));
    }

    private void handleBuildingResult(Optional<Map> map) {
        MainContract.View view = viewWeakReference.get();
        if (view != null && map.isPresent()){
            Map nMap = map.get();
            //view.addBuildingList(nMap.getBuildings());
        }
    }


    private void handleMapsResult(List<Map> maps) {
        MainContract.View view = viewWeakReference.get();
        if (view != null) {
            view.addMaps(maps);
        }
    }


    private void handlePermissionsResult(PermissionRequestHandler.PermissionRequestResult result) {
        MainContract.View view = viewWeakReference.get();
        if (view != null) {
            switch (result) {
                case GRANTED:
                    getLocation();
                    break;
                case DENIED_SOFT:
                    view.showSoftDenied();
                    break;
                case DENIED_HARD:
                    view.showHardDenied();
                    break;
            }
        }
    }


    private void getLocation() {
        disposables.add(
            interactor.getLocationUpdates()
            .subscribe(
                location -> {
                    MainContract.View view = viewWeakReference.get();
                    if (view != null) {
                        Log.e(TAG, "Lat: " + location.latitude() + " Long: " + location.longitude());
                        view.hidePermissionDeniedWarning();
                        //view.showLatitude(String.valueOf(location.latitude()));
                        //view.showLongitude(String.valueOf(location.longitude()));
                    }
                },
                throwable -> {
                    MainContract.View view = viewWeakReference.get();
                    if (view != null) {
                        Log.e(TAG, "Error while getting location", throwable);
                        if (throwable instanceof NoLocationAvailableException) {
                            view.showNoLocationAvailable();
                        } else {
                            view.showGenericError();
                        }

                    }
                }
            )
        );
    }


    @Override
    public void cleanup() {
        disposables.clear();
    }


    @Override
    public void takeView(MainContract.View view) {
       // mLocationView = view;
    }

    @Override
    public void dropView() {
        //mLocationView = null;
    }


}
