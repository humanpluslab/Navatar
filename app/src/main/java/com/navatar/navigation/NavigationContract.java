package com.navatar.navigation;

import com.navatar.BasePresenter;
import com.navatar.BaseView;
import com.navatar.data.Route;

public interface NavigationContract {

    interface View extends BaseView<Presenter> {


    }

    interface Presenter extends BasePresenter<View> {

        void startNavigation(Route route);

        void reverseRoute();

        void nextStep();

        void addLandmark();

        void loadData();

    }

}
