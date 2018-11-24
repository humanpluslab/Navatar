package com.navatar.common.details;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.navatar.common.PermissionRequestHandler;
import com.tbruyelle.rxpermissions2.Permission;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.lang.ref.WeakReference;

import io.reactivex.Observable;
import io.reactivex.subjects.AsyncSubject;

public class RuntimePermissionRequestHandler implements PermissionRequestHandler {

    private static final String TAG = RuntimePermissionRequestHandler.class.getSimpleName();

    private final WeakReference<Fragment> fragmentWeakReference;
    private AsyncSubject<PermissionRequestResult> subject;

    private final RxPermissions rxPermissions;

    public RuntimePermissionRequestHandler(Fragment fragment) {
        this.fragmentWeakReference = new WeakReference<>(fragment);
        this.rxPermissions = new RxPermissions(fragment);
    }
    
    @Override
    public boolean checkHasPermission(String permission) {
        if (fragmentWeakReference.get() != null) {
            Fragment activity = fragmentWeakReference.get();
            return ContextCompat.checkSelfPermission(activity.getActivity(), permission)
                == PackageManager.PERMISSION_GRANTED;
        }
        
        return false;
    }

    @Override
    public Observable<PermissionRequestResult> requestPermissions(String... permissions) {
        return rxPermissions
                .requestEach(permissions)
                .map(permission -> getPermission(permission));
    }

    private PermissionRequestResult getPermission(Permission permission) {
        if (permission.granted) {
            // `permission.name` is granted !
            return PermissionRequestResult.GRANTED;
        } else if (permission.shouldShowRequestPermissionRationale) {
            // Denied permission without ask never again
            return PermissionRequestResult.DENIED_SOFT;
        } else {
            // Denied permission with ask never again
            return PermissionRequestResult.DENIED_HARD;
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    public void onPermissionRequestResult(boolean granted, String permission) {
        Log.e(TAG, permission + " granted: " + granted);
        if (subject != null) {
            if (granted) {
                subject.onNext(PermissionRequestResult.GRANTED);
            } else {
                Fragment activity = fragmentWeakReference.get();
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