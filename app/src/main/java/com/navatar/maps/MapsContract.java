package com.navatar.maps;

import android.support.annotation.NonNull;

import com.navatar.BaseNavigator;
import com.navatar.BasePresenter;
import com.navatar.BaseView;
import com.navatar.data.Map;

import java.util.List;

public interface MapsContract {

    interface View extends BaseView<Presenter> {

        void addMaps(List<Map> maps);

        void showMap(Map map);
    }

    interface Presenter extends BasePresenter<View> {

        void loadData();

        void onMapSelected(Map map);

        void cleanup();
    }

    interface Navigator extends BaseNavigator<Presenter> {

        void navigate(Map map);

    }

}