package com.navatar.maps;

import com.navatar.BasePresenter;
import com.navatar.BaseView;

public interface MapsContract {


    interface View extends BaseView<Presenter> {

    }

    interface Presenter extends BasePresenter<View> {

        void result(int requestCode, int resultCode);

    }

}
