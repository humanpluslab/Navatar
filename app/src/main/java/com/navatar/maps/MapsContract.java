package com.navatar.maps;

import com.navatar.BasePresenter;
import com.navatar.BaseView;
import com.navatar.data.Map;

import java.util.List;

public interface MapsContract {

    interface View extends BaseView<Presenter> {

        void addMaps(List<Map> maps);

        void showBuildings(Map map);
    }

    interface Presenter extends BasePresenter<View> {

        void loadData();

        void onMapSelected(Map map);

        void cleanup();
    }
}