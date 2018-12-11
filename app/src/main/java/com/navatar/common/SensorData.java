package com.navatar.common;

import android.support.annotation.NonNull;

public final class SensorData {

    public enum SensorType {
        ACCELEROMETER,
        GYROSCOPE,
        COMPASS,
        PEDOMETER,
        GYRO_COMPASS
    }

    private final float[] mValues;

    private final long mTimeStamp;

    private final SensorType mSensorType;

    public SensorData(SensorType type, float[] values, long timeStamp) {
        mSensorType = type;
        mValues = values;
        mTimeStamp = timeStamp;
    }

    public float[] getValues() {
        return mValues;
    }

    public long getTimeStamp() {
        return mTimeStamp;
    }

    public SensorType getSensorType() {
        return mSensorType;
    }

    @Override
    public String toString() {
        return mSensorType.toString();
    }
}
