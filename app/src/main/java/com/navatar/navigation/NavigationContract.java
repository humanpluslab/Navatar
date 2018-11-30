package com.navatar.navigation;

import com.navatar.BasePresenter;
import com.navatar.BaseView;

public interface NavigationContract {

    interface View extends BaseView<Presenter> {


    }

    interface Presenter extends BasePresenter<View> {

        void startNavigation();

        void reverseRoute();

        void nextStep();

        void addLandmark();

    }

}
