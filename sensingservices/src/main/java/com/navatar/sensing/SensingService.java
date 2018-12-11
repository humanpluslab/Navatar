package com.navatar.sensing;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.SensorManager;
import android.os.Binder;
import android.os.IBinder;
import android.util.SparseArray;

import com.navatar.sensing.compositesensors.GyroCompass;
import com.navatar.sensing.compositesensors.StepDetector;

public class SensingService extends Service {
    private final IBinder binder = new SensingBinder();
    private SensorManager mgr;
    private SparseArray<NavatarSensor> sensors;

    @Override
    public void onCreate() {
        // Activates the sensors for reading data and initializes the listeners
        this.mgr = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        this.sensors = new SparseArray<NavatarSensor>();

        this.sensors.append(NavatarSensor.ACCELEROMETER, new Accelerometer(mgr));
        this.sensors.append(NavatarSensor.COMPASS, new Compass(mgr));
        this.sensors.append(NavatarSensor.GYRO_COMPASS, new GyroCompass(mgr));
        this.sensors.append(NavatarSensor.GYROSCOPE, new Gyroscope(mgr));
        this.sensors.append(NavatarSensor.PEDOMETER, new StepDetector(mgr));
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public void registerListener(NavatarSensorListener listener, int[] sensorRequests) {
        for (int sensorType : sensorRequests) {
            NavatarSensor sensor = sensors.get(sensorType);

            if (sensor != null)
                sensor.registerSensorListener(listener);
        }
    }

    public void unregisterListener(NavatarSensorListener listener) {

        int size = sensors.size();
        for (int i = 0; i < size; ++i)
            sensors.valueAt(i).unregisterSensorListener(listener);
    }

    public class SensingBinder extends Binder {
        public SensingService getService() {
            return SensingService.this;
        }
    }
}
