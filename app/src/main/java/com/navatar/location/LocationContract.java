package com.navatar.location;

import com.navatar.BasePresenter;
import com.navatar.BaseView;
import com.navatar.data.Map;

import java.util.List;

public interface LocationContract {

    interface View extends BaseView<Presenter> {

        void showLatitude(String latitude);

        void showLongitude(String longitude);

        void showNoLocationAvailable();

        void showGenericError();

        void showSoftDenied();

        void showHardDenied();

        void hidePermissionDeniedWarning();

        void addMaps(List<Map> maps);

    }

    interface Presenter extends BasePresenter<View> {

        /**
         * Signals the presenter to start the process for fetching the location.
         * If permissions are required, requesting them will be handled inside this process
         */
        void loadData();

        void cleanup();

        void onMapSelected(String mapName);

    }

}
