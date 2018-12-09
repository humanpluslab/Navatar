package com.navatar.barcodescanner;

import android.content.Context;
import android.hardware.camera2.CameraManager;
import android.util.SparseIntArray;
import android.view.Surface;
import android.view.WindowManager;

import io.reactivex.Flowable;

public abstract class ScannerService {

    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();

    static {
        ORIENTATIONS.append(Surface.ROTATION_0, 90);
        ORIENTATIONS.append(Surface.ROTATION_90, 0);
        ORIENTATIONS.append(Surface.ROTATION_180, 270);
        ORIENTATIONS.append(Surface.ROTATION_270, 180);
    }

    final Context context;
    final CameraManager manager;

    /***
     * constructor.
     */
    ScannerService(Context context) {
        this.context = context;
        this.manager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
    }

    /***
     * @return  orientation
     */
    int getOrientation() {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        final int rotation = windowManager.getDefaultDisplay().getRotation();
        return ORIENTATIONS.get(rotation);
    }


    /**
     * starts pictures capturing process.
     *
     */
    public abstract Flowable<String> startCapturing();

}
