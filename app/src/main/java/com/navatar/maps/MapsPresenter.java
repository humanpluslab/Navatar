package com.navatar.maps;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.navatar.data.source.MapsRepository;
import com.navatar.di.ActivityScoped;

import javax.inject.Inject;

@ActivityScoped
final class MapsPresenter implements MapsContract.Presenter {

    private final MapsRepository mMapRepository;

    @Nullable
    private MapsContract.View mMapsView;

    private boolean mFirstLoad = true;

    @Inject
    MapsPresenter(MapsRepository mapsRepository) { mMapRepository = mapsRepository; }

    @Override
    public void result(int requestCode, int resultCode) {

    }


    @Override
    public void takeView(MapsContract.View view) {
        this.mMapsView = view;
      //  loadMaps(false);
    }

    @Override
    public void dropView() {
        mMapsView = null;
    }

}
