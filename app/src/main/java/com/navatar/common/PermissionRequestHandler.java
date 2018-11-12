package com.navatar.common;

import io.reactivex.Single;

public interface PermissionRequestHandler {
    
    boolean checkHasPermission();
    
    Single<PermissionRequestResult> requestPermission();
    
    enum PermissionRequestResult {
        GRANTED,
        DENIED_SOFT,
        DENIED_HARD
    }
}