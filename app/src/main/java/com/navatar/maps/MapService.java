package com.navatar.maps;

import java.io.IOException;
import java.util.ArrayList;

import com.navatar.MapSelectActivity;
import com.navatar.maps.particles.ParticleState;
import com.navatar.pathplanning.Path;
import java.io.InputStream;
import com.navatar.protobufs.BuildingMapProto;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MapService extends Service {
  private String navatarPath = Environment.getExternalStorageDirectory().getPath() + "/Navatar";
  private String campusName;
  private final IBinder binder = new MapBinder();
  private ArrayList<BuildingMapWrapper> maps;
  private BuildingMapWrapper activeMap;
  private PendingIntent pendingIntent;

  // Auto-locate
  private String BUILDING_GEOFENCES_JSON_FILENAME = "Building_Geofences.json";
  private JSONArray buildingGeofences;

  @Override
  public void onCreate() {
      maps = new ArrayList<BuildingMapWrapper>();
  }

  private void loadBuildingGeofencesJSONFromPath() {
    String json = null;
    try {
      InputStream is = this.getAssets().open(navatarPath + '/' + BUILDING_GEOFENCES_JSON_FILENAME);
      int size = is.available();
      byte[] buffer = new byte[size];
      is.read(buffer);
      is.close();
      json = new String(buffer, "UTF-8");

      try {
        JSONObject obj = new JSONObject(json);
        buildingGeofences = obj.getJSONArray("buildings");
      } catch (JSONException e) {
        e.printStackTrace();
      }
    } catch (IOException ex) {
      ex.printStackTrace();
    }
  }

  public void loadMapsFromPath(){
      try {

          String[] mapFiles = getAssets().list(navatarPath);
          maps.clear();

          for (int i = 0; i<mapFiles.length; i++) {
            // Ignore building geofences file
            if ( !mapFiles[i].equals(BUILDING_GEOFENCES_JSON_FILENAME)) {
              maps.add(new BuildingMapWrapper(BuildingMapProto.BuildingMap.parseFrom(
                      getAssets().open(navatarPath + "/" + mapFiles[i]))));
            }
          }
      } catch (IOException e) {
          e.printStackTrace();
      }

  }
    @Override
  public int onStartCommand(Intent intent, int flags, int startId) {

    if(intent!=null){
        if(intent.hasExtra("path")){
            campusName = intent.getStringExtra("path");
            navatarPath = "maps/"+intent.getStringExtra("path");
            pendingIntent = intent.getParcelableExtra("pendingIntent");

            loadMapsFromPath();

            loadBuildingGeofencesJSONFromPath();

            try {
                Intent mapListSendbackIntent = new Intent();
                ArrayList<String> mapList = new ArrayList<>();
                for (BuildingMapWrapper map : maps) {
                    mapList.add(map.getName().replaceAll("_"," "));
                }

                // Send maps and locations back
                mapListSendbackIntent.putExtra("maps",mapList);

                // buildingGeofences will be null if they don't exist for failed to load
                if(buildingGeofences != null) {
                  mapListSendbackIntent.putExtra("geofences", buildingGeofences.toString());
                }

                pendingIntent.send(this,100,mapListSendbackIntent);

            } catch (PendingIntent.CanceledException e) {
                e.printStackTrace();
            }

        }
    }
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

  @Override
  public void onDestroy(){

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

  public void setActiveMapByName(String buildingName) {
    this.activeMap = findMapByName(buildingName);
  }

  public String getCampusName() {
    return campusName;
  }

  public void debugMapNames() {
    for (BuildingMapWrapper map : maps) {
        Log.d("MAP NAME:", map.getName());
    }
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

  private BuildingMapWrapper findMapByName(String buildingName) {
    for (BuildingMapWrapper map : maps) {
        if (map.getName().equals(buildingName)){
            Log.d("ACTIVE MAP FOUND BY NAME : ", map.getName());
            return map;
        }
    }

    return null;
  }
}
