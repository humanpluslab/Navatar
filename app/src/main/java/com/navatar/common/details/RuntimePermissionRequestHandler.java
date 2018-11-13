package com.navatar.common.details;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.navatar.common.PermissionRequestHandler;

import java.lang.ref.WeakReference;

import io.reactivex.Single;
import io.reactivex.subjects.AsyncSubject;

public class RuntimePermissionRequestHandler implements PermissionRequestHandler {

    private static final String TAG = RuntimePermissionRequestHandler.class.getSimpleName();

    private final WeakReference<Activity> activityWeakReference;
    private AsyncSubject<PermissionRequestResult> subject;
    
    public RuntimePermissionRequestHandler(Activity activity) {
        this.activityWeakReference = new WeakReference<>(activity);
    }
    
    @Override
    public boolean checkHasPermission(String permission) {
        if (activityWeakReference.get() != null) {
            Activity activity = activityWeakReference.get();
            return ContextCompat.checkSelfPermission(activity, permission)
                == PackageManager.PERMISSION_GRANTED;
        }
        
        return false;
    }
    
    @Override
    public Single<PermissionRequestResult> requestPermission(String permission, int requestCode) {
        subject = AsyncSubject.create();
        
        if (activityWeakReference.get() != null) {
            ActivityCompat.requestPermissions(
                activityWeakReference.get(),
                new String[] {permission},
                requestCode);
        }
        
        return subject.firstOrError();
    }
    
    
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void onPermissionRequestResult(boolean granted, String permission) {
        Log.e(TAG, permission + " granted: " + granted);
        if (subject != null) {
            if (granted) {
                subject.onNext(PermissionRequestResult.GRANTED);
            } else {
                Activity activity = activityWeakReference.get();
                if (activity != null) {
                    subject.onNext(
                        activity.shouldShowRequestPermissionRationale(permission)
                            ? PermissionRequestResult.DENIED_SOFT
                            : PermissionRequestResult.DENIED_HARD
                    );
                }
            }
            subject.onComplete();
        }
    }
    
    
}