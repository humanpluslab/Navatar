package com.navatar.common.details;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import com.navatar.common.PermissionRequestHandler;

import java.lang.ref.WeakReference;

import io.reactivex.Single;
import io.reactivex.subjects.AsyncSubject;

public class RuntimePermissionRequestHandler implements PermissionRequestHandler {
    
    private final WeakReference<Activity> activityWeakReference;
    private final String permission;
    private final int requestCode;
    private AsyncSubject<PermissionRequestResult> subject;
    
    // TODO - think about supporting multiple permissions
    public RuntimePermissionRequestHandler(Activity activity, String permission, int requestCode) {
        this.activityWeakReference = new WeakReference<>(activity);
        this.permission = permission;
        this.requestCode = requestCode;
    }
    
    @Override
    public boolean checkHasPermission() {
        if (activityWeakReference.get() != null) {
            Activity activity = activityWeakReference.get();
            return ContextCompat.checkSelfPermission(activity, permission)
                == PackageManager.PERMISSION_GRANTED;
        }
        
        return false;
    }
    
    @Override
    public Single<PermissionRequestResult> requestPermission() {
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
    public void onPermissionRequestResult(boolean granted) {
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