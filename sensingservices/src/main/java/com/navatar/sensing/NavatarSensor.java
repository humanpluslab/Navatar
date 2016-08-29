package com.navatar.sensing;

import java.util.LinkedList;

import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public abstract class NavatarSensor implements SensorEventListener
{
	public static final int ACCELEROMETER = 0;
	public static final int GYROSCOPE = 1;
	public static final int COMPASS = 2;
	public static final int	PEDOMETER = 3; 
	public static final int	GYRO_COMPASS = 4;

	protected int[] types, delays;
	protected SensorManager mgr;
	protected LinkedList<NavatarSensorListener> listeners;
	
	protected NavatarSensor(SensorManager mgr) {
		listeners = new LinkedList<NavatarSensorListener>();
		this.mgr = mgr;
	}
	
	public void registerSensorListener(NavatarSensorListener listener) {
		if(listeners.size() == 0) {
			for(int i=0; i<types.length; ++i)
				mgr.registerListener(this, mgr.getDefaultSensor(types[i]), delays[i]);
		}

		listeners.add(listener);
	}

	public void unregisterSensorListener(NavatarSensorListener listener) {
		listeners.remove(listener);

		if(listeners.size() == 0) {
			for(int i=0; i<types.length; ++i)
				mgr.unregisterListener(this, mgr.getDefaultSensor(types[i]));
		}
	}
}