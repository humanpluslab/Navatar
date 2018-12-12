package com.navatar.data.source;

import com.navatar.data.Landmark;

import io.reactivex.Flowable;

public interface LandmarkProvider {

    Flowable<Landmark> getLandmarks();

}
