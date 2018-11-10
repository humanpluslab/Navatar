package com.navatar.location;

import android.location.Location;
import android.support.annotation.NonNull;

public interface LocationSource {

    interface LocationCallback {

        void onLocationChanged(Location location);
        void onLocationManagerConnected();

    }

    void getLocation(@NonNull LocationCallback callback);

}
