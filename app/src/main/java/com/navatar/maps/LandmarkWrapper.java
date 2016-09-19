/**
 * Contains the class LandmarlWrapper which is a wrapper for the Landmark protobuf.
 */
package com.navatar.maps;

import android.util.Log;

import com.navatar.math.Constants;
import com.navatar.protobufs.LandmarkProto.Landmark;

import java.lang.reflect.Field;

/**
 * Wrapper for the Tile protobuf.
 * 
 * @author ilias
 *
 */
public class LandmarkWrapper implements Comparable<LandmarkWrapper> {
  /** The original protobuf landmark. */
  private Landmark landmark;
  /** The landmarks weight which is based on its distance from the tile. */
  private double weight;

  public LandmarkWrapper(Landmark landmark) {
    this.landmark = landmark;
  }

  public void setWeight(double weight) {
    this.weight = weight;
  }

  public double getWeight() {
    return weight;
  }

  public Landmark getLandmark() {
    return landmark;
  }

  public void setLandmark(Landmark landmark) {
    this.landmark = landmark;
  }

  @Override
  public int compareTo(LandmarkWrapper o) {
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
      Log.i("NavatarLogs",(String)f.get(landmark));
    }catch(NoSuchFieldException e){

    }catch (IllegalAccessException e){
    }
  }




}