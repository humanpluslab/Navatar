package com.navatar.sensing;

public interface NavatarSensorListener {

	void onSensorChanged(float[] values, int sensor, long timestamp);
}
