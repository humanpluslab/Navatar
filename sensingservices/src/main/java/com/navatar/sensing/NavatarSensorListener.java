package com.navatar.sensing;

public interface NavatarSensorListener {

	public void onSensorChanged(float []values, int sensor, long timestamp);
}
