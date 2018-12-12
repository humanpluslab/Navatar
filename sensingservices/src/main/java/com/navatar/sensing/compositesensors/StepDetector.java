package com.navatar.sensing.compositesensors;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;

import com.navatar.sensing.NavatarSensor;
import com.navatar.sensing.NavatarSensorListener;

import java.util.LinkedList;

/**
 * Now uses the built in step detection sensor.
 */
public class StepDetector extends NavatarSensor {

    /**
     * Initializes the type of sensor used and the navatar listeners
     */
    public StepDetector(SensorManager mgr) {
        super(mgr);
        types = new int[]{Sensor.TYPE_STEP_DETECTOR};
        delays = new int[]{SensorManager.SENSOR_DELAY_UI};

        listeners = new LinkedList<NavatarSensorListener>();
    }

    public void onSensorChanged(SensorEvent event) {
        for (NavatarSensorListener listener : listeners)
            listener.onSensorChanged(new float[]{1f}, NavatarSensor.PEDOMETER, event.timestamp);

    }

    public void onAccuracyChanged(Sensor arg0, int arg1) {
    }
}
