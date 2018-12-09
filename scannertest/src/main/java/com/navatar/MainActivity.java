package com.navatar;

import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import com.navatar.barcodescanner.CameraController;
import com.navatar.barcodescanner.CameraRxWrapper;

import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.disposables.CompositeDisposable;

public class MainActivity extends AppCompatActivity {

    private final static String TAG = MainActivity.class.getSimpleName();

    private final CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Button btn = (Button) findViewById(R.id.startCaptureBtn);

        CameraController controller = new CameraController(getApplicationContext());

        btn.setOnClickListener(v -> {

            compositeDisposable.add(controller
                    .openCamera()
                    .subscribe(s -> Log.i(TAG, "Opening camera"),
                            e -> Log.e(TAG, "Error opening camera:" + e)));

        });

    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        compositeDisposable.dispose();
    }
}
