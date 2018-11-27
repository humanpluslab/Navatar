package com.navatar.main;

import android.Manifest;
import android.util.Log;
import android.support.annotation.Nullable;

import com.google.common.base.Optional;
import com.navatar.common.PermissionRequestHandler;
import com.navatar.data.Map;
import com.navatar.data.source.MapsRepository;
import com.navatar.location.GeofencingProvider;
import com.navatar.location.LocationInteractor;
import com.navatar.location.model.NoLocationAvailableException;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import io.reactivex.disposables.CompositeDisposable;

public class MainPresenter implements MainContract.Presenter {

    private static final String TAG = MainPresenter.class.getSimpleName();

    private final LocationInteractor interactor;
    private PermissionRequestHandler permissionRequestHandler;
    private final CompositeDisposable disposables = new CompositeDisposable();

    private final MainContract.Navigator mNavigator;

    @Nullable
    private MainContract.View mMainView;

    @Inject
    @Named("locationReqCode")
    Integer locationRequestCode;

    @Inject
    @Named("cameraReqCode")
    Integer cameraRequestCode;

    @Inject
    public MainPresenter(LocationInteractor interactor, MainContract.Navigator navigator) {
        this.interactor = interactor;
        this.mNavigator = navigator;
    }

    @Override
    public void setPermissionHandler(PermissionRequestHandler handler) {
        permissionRequestHandler = handler;
    }


    @Override
    public void loadData() {

        disposables.clear();

        disposables.add(permissionRequestHandler.requestPermissions(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.CAMERA)
            .subscribe(
                this::handlePermissionsResult,
                throwable -> Log.e(TAG, "An error occurred getting permissions", throwable)
            ));

    }




    private void handlePermissionsResult(PermissionRequestHandler.PermissionRequestResult result) {
        if (mMainView != null) {
            switch (result) {
                case GRANTED:
                    getLocation();
                    break;
                case DENIED_SOFT:
                    mMainView.showSoftDenied();
                    break;
                case DENIED_HARD:
                    mMainView.showHardDenied();
                    break;
            }
        }
    }


    private void getLocation() {
        disposables.add(
            interactor.getLocationUpdates()
            .subscribe(
                location -> {
                    if (mMainView!= null) {
                        Log.e(TAG, "Lat: " + location.latitude() + " Long: " + location.longitude());
                        mMainView.hidePermissionDeniedWarning();
                        //view.showLatitude(String.valueOf(location.latitude()));
                        //view.showLongitude(String.valueOf(location.longitude()));
                    }
                },
                throwable -> {
                    if (mMainView != null) {
                        Log.e(TAG, "Error while getting location", throwable);
                        if (throwable instanceof NoLocationAvailableException) {
                            mMainView.showNoLocationAvailable();
                        } else {
                            mMainView.showGenericError();
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
       mMainView = view;
       loadData();
    }

    @Override
    public void dropView() {
        mMainView = null;
    }


}
