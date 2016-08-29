package com.navatar.sensing;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;

public class Compass extends NavatarSensor {

	public Compass(SensorManager mgr) {
		super(mgr);
		types = new int[] {Sensor.TYPE_ROTATION_VECTOR};
		delays = new int[] {SensorManager.SENSOR_DELAY_NORMAL};
	}

	public void onSensorChanged(SensorEvent event)
	{
		float []R = new float[9];
		float []values = new float[3];
		
		SensorManager.getRotationMatrixFromVector(R, event.values);
		SensorManager.getOrientation(R, values);

		for (NavatarSensorListener listener : listeners)
			listener.onSensorChanged(values, NavatarSensor.COMPASS, event.timestamp);
	}

	public void onAccuracyChanged(Sensor sensor, int accuracy){}
}