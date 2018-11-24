package com.navatar.common;

import io.reactivex.Observable;
import io.reactivex.Single;

public interface PermissionRequestHandler {
    
    boolean checkHasPermission(String permission);

    Observable<PermissionRequestResult> requestPermissions(String... permissions);

    enum PermissionRequestResult {
        GRANTED,
        DENIED_SOFT,
        DENIED_HARD
    }
}