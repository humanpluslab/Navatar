package com.navatar.common;

import io.reactivex.Single;

public interface PermissionRequestHandler {
    
    boolean checkHasPermission(String permission);
    
    Single<PermissionRequestResult> requestPermission(String permission, int requestCode);
    
    enum PermissionRequestResult {
        GRANTED,
        DENIED_SOFT,
        DENIED_HARD
    }
}