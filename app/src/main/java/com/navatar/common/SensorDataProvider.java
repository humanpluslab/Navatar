package com.navatar.common;

import io.reactivex.Flowable;

public interface SensorDataProvider {

        Flowable<SensorData> onSensorChanged();

}
