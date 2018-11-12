package com.navatar.common.details;


import android.app.Activity;

import javax.inject.Inject;

/**
 * Dispatches the result of the permission request from the Activity to the
 * {@link RuntimePermissionRequestHandler}
 */
public class PermissionsRequestResultDispatcher {
    
    private RuntimePermissionRequestHandler permissionRequestHandler;
    
    @Inject
    public PermissionsRequestResultDispatcher(RuntimePermissionRequestHandler handler) {
        permissionRequestHandler = handler;
    }
    
    /**
     * To be called from {@link Activity#onRequestPermissionsResult(int, String[], int[])}
     */
    public void dispatchResult(boolean granted) {
        permissionRequestHandler.onPermissionRequestResult(granted);
    }
    
}
