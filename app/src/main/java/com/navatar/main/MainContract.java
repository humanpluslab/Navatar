package com.navatar.main;


import com.navatar.BasePresenter;
import com.navatar.BaseView;
import com.navatar.common.PermissionRequestHandler;

public interface MainContract {

    interface View extends BaseView<Presenter> {

        void showNoLocationAvailable();

        void showGenericError();

        void showSoftDenied();

        void showHardDenied();

        void hidePermissionDeniedWarning();

        void openMapsUI();

    }

    interface Presenter extends BasePresenter<View> {

        void loadData();

        void cleanup();

        void setPermissionHandler(PermissionRequestHandler handler);

        void getLocation();

        void openMaps();

    }
}
