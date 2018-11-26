package com.navatar.location.model;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class Point {

    public abstract double x();
    public abstract double y();
    public abstract double z();

    public static Point create(double x, double y, double z) {
        return new AutoValue_Point(x, y, z);
    }

}
