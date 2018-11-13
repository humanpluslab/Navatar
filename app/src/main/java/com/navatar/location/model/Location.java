package com.navatar.location.model;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class Location {

    public abstract double latitude();
    public abstract double longitude();

    public static Location create(double latitude, double longitude) {
        return new AutoValue_Location(latitude, longitude);
    }

}
