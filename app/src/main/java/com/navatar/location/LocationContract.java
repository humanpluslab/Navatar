package com.navatar.location;

import com.navatar.BasePresenter;
import com.navatar.BaseView;

public interface LocationContract {

    interface View extends BaseView<Presenter> {

        boolean isActive();

    }

    interface Presenter extends BasePresenter<View> {

        void result(int requestCode, int resultCode);

        void getLocation();

    }

}
