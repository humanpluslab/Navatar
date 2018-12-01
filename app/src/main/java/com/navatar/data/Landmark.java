package com.navatar.data;

import com.navatar.math.Constants;
import com.navatar.protobufs.LandmarkProto;

import java.lang.reflect.Field;

public class Landmark implements Comparable<Landmark> {

    /** The original protobuf landmark. */
    private LandmarkProto.Landmark landmark;

    /** The landmarks weight which is based on its distance from the tile. */
    private double weight;

    public Landmark(LandmarkProto.Landmark landmark) {
        this.landmark = landmark;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public double getWeight() {
        return weight;
    }

    public LandmarkProto.Landmark getLandmark() {
        return landmark;
    }

    public void setLandmark(LandmarkProto.Landmark landmark) {
        this.landmark = landmark;
    }

    @Override
    public int compareTo(Landmark o) {
        if (Math.abs(this.weight - o.weight) < Constants.DOUBLE_ACCURACY)
            return 0;
        if (this.weight < o.weight)
            return -1;
        return 1;
    }


    @Override
    public String toString() {
        String t_string = "Room ";
        t_string=(landmark.getName().length()>10)?(""):t_string;
        return t_string+ landmark.getName().toString()+"\n";
    }

    public void setName(String newName){

        try{
            Field f = landmark.getClass().getDeclaredField("name_");
            f.setAccessible(true);
            f.set(landmark,newName);
            //Log.i("NavatarLogs",(String)f.get(landmark));
        }catch(NoSuchFieldException e){

        }catch (IllegalAccessException e){
        }
    }

    public String getName() {
        return getLandmark().getName();
    }

}
