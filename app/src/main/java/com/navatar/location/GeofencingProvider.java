package com.navatar.location;

import com.navatar.location.model.Geofence;
import com.navatar.location.model.GeofenceRequest;

import io.reactivex.Single;

public interface GeofencingProvider {

    final class Status {



    }

    Single<Status> addGeoFenceRequest(GeofenceRequest request);


}
