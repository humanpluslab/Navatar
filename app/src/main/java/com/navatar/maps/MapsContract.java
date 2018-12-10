package com.navatar.maps;

import android.support.annotation.Nullable;

import com.navatar.BaseNavigator;
import com.navatar.BasePresenter;
import com.navatar.BaseView;
 import com.navatar.data.Building;
import com.navatar.data.Landmark;
import com.navatar.data.Map;
import com.navatar.data.Route;

import java.util.List;

public interface MapsContract {

    interface View extends BaseView<Presenter> {

        void showMaps(List<Map> maps);

        void showMap(Map map);

        void showFromLandmark(List<Landmark> landmark);

        void showToLandmark(List<Landmark> landmark);

        void showNavigation(@Nullable Route route);

        void noRouteFound();

        boolean onBackPressed();

    }

    interface Presenter extends BasePresenter<View> {

        void loadData();

        void onMapSelected(Map map);

        void onBuildingSelected(Building building);

        void onFromLandmarkSelected(Landmark landmark);

        void onToLandmarkSelected(Landmark landmark);

        void cleanup();
    }
}