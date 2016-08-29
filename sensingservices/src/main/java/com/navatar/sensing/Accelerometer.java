package com.navatar.sensing;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;

public class Accelerometer extends NavatarSensor {

	public Accelerometer(SensorManager mgr) {
		super(mgr);
		types = new int[] {Sensor.TYPE_ACCELEROMETER};
		delays = new int[] {SensorManager.SENSOR_DELAY_NORMAL};
	}
	
	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {}

	public void onSensorChanged(SensorEvent event) {
		for (NavatarSensorListener listener : listeners)
			listener.onSensorChanged(event.values, NavatarSensor.ACCELEROMETER, event.timestamp);
	}
}