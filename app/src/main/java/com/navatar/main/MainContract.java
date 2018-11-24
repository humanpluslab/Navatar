package com.navatar.main;

import com.navatar.BasePresenter;
import com.navatar.BaseView;
import com.navatar.data.Map;

import java.util.List;

public interface MainContract {

    interface View extends BaseView<Presenter> {

        void addMaps(List<Map> maps);



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

        void onMapSelected(String mapName);

    }

}
