package com.navatar.location;

import com.navatar.BasePresenter;
import com.navatar.BaseView;

public interface LocationContract {

    interface View extends BaseView<Presenter> {

        void showLatitude(String latitude);

        void showLongitude(String longitude);

        void showNoLocationAvailable();

        void showGenericError();

        void showSoftDenied();

        void showHardDenied();

        void hidePermissionDeniedWarning();

    }

    interface Presenter extends BasePresenter<View> {

        /**
         * Signals the presenter to start the process for fetching the location.
         * If permissions are required, requesting them will be handled inside this process
         */
        void loadData();

        void cleanup();

    }

}
