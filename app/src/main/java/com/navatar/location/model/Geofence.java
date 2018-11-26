package com.navatar.location.model;

public class Geofence<T> {

    private String name;

    private T data;

    public Geofence(T data, String name) {
        this.data = data;
        this.name = name;
    }


    public String getName() { return name; }

    public T get() {
        return data;
    }




}
