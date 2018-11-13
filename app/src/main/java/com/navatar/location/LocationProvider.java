package com.navatar.location;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import io.reactivex.Single;
import com.navatar.location.model.Location;

public interface LocationProvider {

    @NonNull
    Single<Location> getLocation();

}
