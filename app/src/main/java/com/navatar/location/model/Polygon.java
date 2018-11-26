package com.navatar.location.model;

import com.google.auto.value.AutoValue;

import java.util.List;

@AutoValue
public abstract class Polygon {

    public abstract List<Point> points();

    public static Polygon create(List<Point> points) {
        return new AutoValue_Polygon(points);
    }

}
