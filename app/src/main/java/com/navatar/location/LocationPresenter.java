package com.navatar.location;

import android.support.annotation.Nullable;
import android.util.Log;

import java.lang.ref.WeakReference;

import javax.inject.Inject;

import io.reactivex.disposables.CompositeDisposable;
import com.navatar.common.PermissionRequestHandler;
import com.navatar.di.ActivityScoped;
import com.navatar.location.LocationInteractor;
import com.navatar.location.model.Location;
import com.navatar.location.model.NoLocationAvailableException;

public class LocationPresenter implements LocationContract.Presenter {

    private static final String TAG = "LOCATION";

    private final WeakReference<LocationContract.View> viewWeakReference;
    private final LocationInteractor interactor;
    private PermissionRequestHandler permissionRequestHandler;
    private final CompositeDisposable disposables = new CompositeDisposable();

    private LocationContract.View mLocationView;

    @Inject
    public LocationPresenter(LocationContract.View view,
                             LocationInteractor interactor,
                             PermissionRequestHandler permissionRequestHandler) {
        this.viewWeakReference = new WeakReference<>(view);
        this.interactor = interactor;
        this.permissionRequestHandler = permissionRequestHandler;
    }

    @Override
    public void loadData() {
        if (!permissionRequestHandler.checkHasPermission()) {
            disposables.clear();
            disposables.add(permissionRequestHandler.requestPermission()
                    .subscribe(
                            this::handleResult,
                            throwable -> Log.e(TAG, "An error occurred on the permission request " +
                                    "result stream", throwable)
                    )
            );
        } else {
            getLocation();
        }
    }

    private void handleResult(PermissionRequestHandler.PermissionRequestResult result) {
        LocationContract.View view = viewWeakReference.get();
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
                interactor.getLocation()
                        .subscribe(
                                location -> {
                                    LocationContract.View view = viewWeakReference.get();
                                    if (view != null) {
                                        view.hidePermissionDeniedWarning();
                                        view.showLatitude(String.valueOf(location.latitude()));
                                        view.showLongitude(String.valueOf(location.longitude()));
                                    }
                                },
                                throwable -> {
                                    LocationContract.View view = viewWeakReference.get();
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
    public void takeView(LocationContract.View view) {
        mLocationView = view;
    }

    @Override
    public void dropView() {
        mLocationView = null;
    }


}
