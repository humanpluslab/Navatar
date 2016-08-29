package com.navatar.sensordata;

import java.io.File;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.os.Vibrator;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.util.SparseArray;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.widget.Toast;

import com.navatar.output.file.TextFile;
import com.navatar.sensing.NavatarSensor;
import com.navatar.sensing.NavatarSensorListener;
import com.navatar.sensing.SensingService;
import com.navatar.time.TimeListener;
import com.navatar.time.TimeService;

public class SensorDataActivity extends Activity 
		implements NavatarSensorListener, TextToSpeech.OnInitListener, TimeListener {

	private TextToSpeech 			mSpeech;
	private SensingService 			mSensing;
    private TimeService 			mTime;
	private SparseArray<TextFile>	mSensorDataFiles;
	private State 					mCurrentState;
	private Vibrator 				mVibrator;
	private static String 			sNavatarPath = Environment.getExternalStorageDirectory().getPath()+"/Navatar";
	private long 					mClockOffset;
	
	enum State {
		INITIALIZING,
		RUNNING,
		ENDING
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sensor_data);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		
		mVibrator = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);

		Intent sensingIntent = new Intent(this, SensingService.class);
		startService(sensingIntent);
		bindService(sensingIntent, sensingConnection, BIND_AUTO_CREATE);
		
    	// Connect here with TimeService
    	// Use 0.us.pool.ntp.org for server
		Intent timeIntent = new Intent(this, TimeService.class);
		startService(timeIntent);
		bindService(timeIntent, timeConnection, BIND_AUTO_CREATE);
    	
    	getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    	
        mSpeech = new TextToSpeech(this, this);
        
		File dir = new File(sNavatarPath);
    	if(!dir.exists())
    		dir.mkdir();
		
    	dir = new File(sNavatarPath+"/data");
    	if(!dir.exists())
    		dir.mkdir();

    	
    	mSensorDataFiles = new SparseArray<TextFile>();
    	
    	TextFile accelerometer_data;
		int i = -1;
    		
		do{
			accelerometer_data = new TextFile(sNavatarPath+"/data/accelerometer"+ ++i +".txt");
		}while(accelerometer_data.exists());
		
		mSensorDataFiles.put(NavatarSensor.ACCELEROMETER, accelerometer_data);
    	
    	mCurrentState = State.INITIALIZING;    	
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		if(mSensing != null)
			mSensing.unregisterListener(this);

		int size = mSensorDataFiles.size();
		for (int i = 0; i < size; ++i) {
			try {
				mSensorDataFiles.valueAt(i).writeFile(false);
			} catch (Exception e) {
				Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
			}
		}

		mSpeech.shutdown();
	    unbindService(sensingConnection);
		Intent sensingIntent = new Intent(this, SensingService.class);
	    stopService(sensingIntent);

	    unbindService(timeConnection);
		Intent timeIntent = new Intent(this, TimeService.class);
	    stopService(timeIntent);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.sensor_data, menu);
		return true;
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		switch (keyCode) {
		case KeyEvent.KEYCODE_DPAD_CENTER:
		case KeyEvent.KEYCODE_ENTER:
			handleInput();
		default:
			return super.onKeyDown(keyCode, event);
		}
	}
   
	@Override
	public boolean onTouchEvent(MotionEvent event) {

	    switch (event.getAction()) {
	    case MotionEvent.ACTION_DOWN:
	    	handleInput();
	    default:
	    	return super.onTouchEvent(event);
	    }
	}
	
	private void handleInput() {

		if (mVibrator != null)
			mVibrator.vibrate(100);

		switch(mCurrentState) {
		case INITIALIZING:
			if(mSensing != null) {
				mSensing.registerListener(SensorDataActivity.this, new int[]{NavatarSensor.ACCELEROMETER});
				mSpeech.speak("Ready for data collection", TextToSpeech.QUEUE_FLUSH, null);
				mCurrentState = State.RUNNING;
			}
			break;
		case RUNNING:
    		mSpeech.speak("End of experiment", TextToSpeech.QUEUE_FLUSH, null);
    		finish();
		default:
			break;
		}
	}

	private ServiceConnection sensingConnection = new ServiceConnection()
    {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service)
        {
            SensingService.SensingBinder binder = (SensingService.SensingBinder) service;
            mSensing = binder.getService();

    		Log.i("Navigation Activity", "Sensing service connected");
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0){
    		mSensing.unregisterListener(SensorDataActivity.this);
    		mSensing = null;
        }
    };

	private ServiceConnection timeConnection = new ServiceConnection()
    {

		@Override
        public void onServiceConnected(ComponentName className, IBinder service)
        {
            TimeService.TimeBinder binder = (TimeService.TimeBinder) service;
            mTime = binder.getService();
            mTime.registerTimeListener(SensorDataActivity.this);
            mTime.syncTime("0.us.pool.ntp.org", 10000);

    		Log.i("Navigation Activity", "Time service connected");
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0){
    		mTime = null;
        }
    };

    @Override
	public void onSensorChanged(float[] values, int sensor, long timestamp) {
			mSensorDataFiles.get(sensor).append(""+(System.currentTimeMillis()+mClockOffset)+"\t"+values[0]+"\t"+values[1]+"\t"+values[2]+"\n");
	}

	@Override
	public void onInit(int status) {
	      mSpeech.speak("Tap surface to start experiment", TextToSpeech.QUEUE_FLUSH, null);
	}

	@Override
	public void onClockSynced(long offset) {
		mClockOffset = offset;
	}
}