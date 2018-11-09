package com.navatar.data.source;

import android.location.Location;
import android.support.annotation.NonNull;

public interface LocationSource {

    interface LocationCallback {

        void onLocationChanged(Location location);
        void onLocationApiManagerConnected();

    }

    void getLocation(@NonNull LocationCallback callback);

}
