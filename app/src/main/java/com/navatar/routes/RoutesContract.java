package com.navatar.routes;

import com.navatar.BasePresenter;
import com.navatar.BaseView;

public interface RoutesContract {

    interface View extends BaseView<Presenter> {

    }

    interface Presenter extends BasePresenter<View> {

    }
}