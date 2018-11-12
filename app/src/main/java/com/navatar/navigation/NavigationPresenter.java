package com.navatar.navigation;

import android.support.annotation.Nullable;

import com.navatar.di.ActivityScoped;

import javax.inject.Inject;

@ActivityScoped
public final class NavigationPresenter implements NavigationContract.Presenter {

    @Nullable
    private NavigationContract.View mNavView;

    @Inject
    public NavigationPresenter() { }

    @Override
    public void takeView(NavigationContract.View view) {
        mNavView = view;
    }

    @Override
    public void dropView() {
        mNavView = null;
    }


}
