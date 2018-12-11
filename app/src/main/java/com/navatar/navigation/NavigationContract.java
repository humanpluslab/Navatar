package com.navatar.navigation;

import com.navatar.BasePresenter;
import com.navatar.BaseView;

public interface NavigationContract {

    interface View extends BaseView<Presenter> {

        void showStepCount(int stepNum, int totalSteps);

        void showDirection(String direction);

        void showReachedDestination();

        void showNoRouteFound();
    }

    interface Presenter extends BasePresenter<View> {

        void onStartNavigation();

        void onReverseRoute();

        void nextStep();

        void addLandmark();

        void loadData();

    }

}
