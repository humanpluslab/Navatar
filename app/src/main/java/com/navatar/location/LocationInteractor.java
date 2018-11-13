package com.navatar.location;

import javax.inject.Inject;

import io.reactivex.Flowable;
import io.reactivex.Single;
import io.reactivex.Observable;
import com.navatar.location.model.Location;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

import io.reactivex.Observable;

public class LocationInteractor {

    private final Set<LocationProvider> locationProviders;

    @Inject
    public LocationInteractor(Set<LocationProvider> locationProviders) {
        this.locationProviders = locationProviders;
    }


    public Observable<Location> getLocation() {
        Observable<Location> wrapper = null;
        for (LocationProvider loc : locationProviders){
            if (wrapper == null) {
                wrapper = loc.getLocation().toObservable();
            } else {
                wrapper = Observable.merge(wrapper, loc.getLocation().toObservable());
            }
        }
        return wrapper;
    }
}
