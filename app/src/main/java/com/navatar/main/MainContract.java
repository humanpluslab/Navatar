package com.navatar.main;

import android.support.annotation.NonNull;

import com.navatar.BaseNavigator;
import com.navatar.BasePresenter;
import com.navatar.BaseView;
import com.navatar.common.PermissionRequestHandler;
import com.navatar.data.Map;

import java.util.List;

public interface MainContract {

    interface View extends BaseView<Presenter> {

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

        void setPermissionHandler(PermissionRequestHandler handler);

    }

    interface Navigator extends BaseNavigator {
        void navigate();
    }


}
