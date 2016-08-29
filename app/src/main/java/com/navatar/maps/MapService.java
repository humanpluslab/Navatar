package com.navatar.maps;

import java.io.IOException;
import java.util.ArrayList;

import com.navatar.maps.particles.ParticleState;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Environment;
import android.os.IBinder;
import android.widget.Toast;

public class MapService extends Service {
  private String navatarPath = Environment.getExternalStorageDirectory().getPath() + "/Navatar";
  private final IBinder binder = new MapBinder();
  private ArrayList<BuildingMapWrapper> maps;
  private BuildingMapWrapper activeMap;

  @Override
  public void onCreate() {
    maps = new ArrayList<BuildingMapWrapper>();
    String filename = "scrugham_engineering_mines_minimap.nvm";
    try {
      maps.add(BuildingMapWrapper.readFrom(navatarPath + "/" + "maps/" + filename));
    } catch (IOException e) {
      Toast.makeText(getApplicationContext(), "Could not load map " + filename, Toast.LENGTH_LONG)
          .show();
    }
  }

  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    return START_STICKY;
  }

  @Override
  public IBinder onBind(Intent arg0) {
    return binder;
  }

  public class MapBinder extends Binder {
    public MapService getService() {
      return MapService.this;
    }
  }

  public ArrayList<BuildingMapWrapper> maps() {
    return maps;
  }

  public void setActiveMap(int position) {
    activeMap = maps.get(position);
  }

  public BuildingMapWrapper getActiveMap() {
    return activeMap;
  }

  public void setActiveMap(String room) {
    this.activeMap = findMapFromRoom(room);
  }

  public ParticleState roomLocation(String room) {
    return activeMap.getRoomLocation(room);
  }

  private BuildingMapWrapper findMapFromRoom(String room) {
    for (BuildingMapWrapper map : maps) {
      if (map.getRoomLocation(room) != null)
        return map;
    }
    return null;
  }
}
