package com.navatar.sensing.compositesensors;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;

import com.navatar.sensing.NavatarSensor;

public class GyroCompass extends NavatarSensor {

	public GyroCompass(SensorManager mgr) {
		super(mgr);
		types = new int[] {Sensor.TYPE_ROTATION_VECTOR, Sensor.TYPE_GYROSCOPE};
		delays = new int[] {SensorManager.SENSOR_DELAY_UI, SensorManager.SENSOR_DELAY_UI};
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {}

	@Override
	public void onSensorChanged(SensorEvent event) {
		// TODO Do calculations here and call listeners
	}
}
