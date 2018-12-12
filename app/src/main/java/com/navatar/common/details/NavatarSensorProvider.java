package com.navatar.common.details;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import com.navatar.common.SensorData;
import com.navatar.common.SensorDataProvider;
import com.navatar.sensing.NavatarSensor;
import com.navatar.sensing.NavatarSensorListener;
import com.navatar.sensing.SensingService;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.subjects.PublishSubject;

import static android.content.Context.BIND_AUTO_CREATE;

@Singleton
public final class NavatarSensorProvider implements SensorDataProvider, NavatarSensorListener {

    private final PublishSubject<SensorData> mSensorData = PublishSubject.create();

    private SensingService mSensingService;

    private ServiceConnection mSensingConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            SensingService.SensingBinder binder = (SensingService.SensingBinder) service;
            mSensingService = binder.getService();
            mSensingService.registerListener(NavatarSensorProvider.this,
                    new int[] { NavatarSensor.COMPASS, NavatarSensor.PEDOMETER });
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mSensingService = null;
        }
    };

    @Inject
    public NavatarSensorProvider(Context context) {
        Intent sensingIntent = new Intent(context, SensingService.class);
        context.startService(sensingIntent);
        context.bindService(sensingIntent, mSensingConnection, BIND_AUTO_CREATE);
    }

    @Override
    public Flowable<SensorData> onSensorChanged() {
        return mSensorData.toFlowable(BackpressureStrategy.LATEST);
    }


    @Override
    public void onSensorChanged(float[] values, int sensor, long timestamp) {
        mSensorData.onNext(new SensorData(SensorData.SensorType.values()[sensor], values, timestamp));
    }
}
