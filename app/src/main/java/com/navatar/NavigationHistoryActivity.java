package com.navatar;

import android.content.ServiceConnection;
import android.os.Bundle;
import android.app.Activity;
import android.os.Environment;
import android.util.Log;

// these are probably not all needed
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;
import android.app.PendingIntent;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.view.View;

import com.navatar.maps.BuildingMapWrapper;
import com.navatar.maps.LandmarkWrapper;
import com.navatar.maps.MapService;
import com.navatar.protobufs.LandmarkProto;
import com.navatar.protobufs.LandmarkProto.Landmark;

public class NavigationHistoryActivity extends Activity {
    private static final String APP_STORAGE = Environment.getExternalStorageDirectory().getPath() + "/Navatar";

    // TODO(Liam): Step length isnt sent here yet
    private float stepLength = 0.0f;

    private ArrayList<JSONObject> previousNavs;
    private ArrayList<String> displayNames;
    private ArrayAdapter<String> previousNavAdapter;
    private Spinner entrySpinner;

    private MapService mapService;
    private BuildingMapWrapper map;
    private Intent mapIntent;
    private PendingIntent pendingIntent;
    private ArrayList<LandmarkWrapper> rooms;
    private LandmarkWrapper fromRoom, toRoom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation_history);
        setTitle("Repeat previous navigation");

        // initialize array lists
        previousNavs = new ArrayList<JSONObject>();
        displayNames = new ArrayList<String>();
        rooms = new ArrayList<LandmarkWrapper>();

        entrySpinner = (Spinner)findViewById(R.id.navHistorySpinner);

        try {
            readHistory();
        } catch (JSONException e) {
            Log.e("Exception", "Failed to read json file: " + e.toString());
        } finally {
            displayNames.add("Select previous navigation...");
            for (JSONObject entry: previousNavs) {
                try {
                    // TODO: assign unique ids to each json object
                    Log.d("History:", entry.toString(2));

                    String campusAbv = "UNR";
                    String displayName = entry.getString("start_room") + " to " + entry.getString("end_room") + " - " + campusAbv + " - " + entry.getString("building");
                    displayNames.add(displayName);
                }
                catch (JSONException e) {
                    Log.e("Exception", "Failed to read json file: " + e.toString());
                }
            }
        }

        previousNavAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, displayNames);
        entrySpinner.setAdapter(previousNavAdapter);
        entrySpinner.setOnItemSelectedListener(previousNavSelected);

        mapIntent = new Intent(this, MapService.class);
        mapIntent.putExtra("path","University_of_Nevada_Reno");
        Intent defaultIntent = new Intent();
        pendingIntent = NavigationHistoryActivity.this.createPendingResult(1,defaultIntent,PendingIntent.FLAG_ONE_SHOT);
        mapIntent.putExtra("pendingIntent",pendingIntent);
        startService(mapIntent);
        //mapService.setActiveMap("102");
        bindService(mapIntent, mMapConnection, BIND_AUTO_CREATE);
    }

    protected void readHistory() throws JSONException {
        try (BufferedReader br = new BufferedReader(new FileReader(APP_STORAGE + "/nav_history.json"))) {
            String line;
            while ((line = br.readLine()) != null) {
                JSONObject entry = new JSONObject(line);
                previousNavs.add(entry);
            }
        } catch(IOException e) {
            e.printStackTrace();
        }
    }


    private ServiceConnection mMapConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            MapService.MapBinder binder = (MapService.MapBinder) service;
            mapService = binder.getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mapService = null;
        }

    };

    public OnItemSelectedListener previousNavSelected = new OnItemSelectedListener() {
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            if (position != 0) {
                JSONObject selected = previousNavs.get(position - 1);
                try {
                    Log.d("ITEM SELECTED :", selected.getString("start_room") + selected.getString("end_room"));
                }
                catch (JSONException e) {
                    Log.e("Exception", "Bad rooms in json object: " + e.toString());
                }

                try {
                    mapService.setActiveMapByName(selected.getString("building"));
                    map = mapService.getActiveMap();
                    mapService.debugMapNames();
                    List<LandmarkWrapper> t_rooms = map.destinations();
                    rooms.addAll(t_rooms);

                    String fromRoomNumber = selected.getString("start_room");
                    String toRoomNumber = selected.getString("end_room");

                    for (LandmarkWrapper entry : rooms) {
                        Landmark landmarkEntry = entry.getLandmark();
                        if (landmarkEntry.getName().equals(fromRoomNumber)) {
                            Log.d("starting room (HISTORY)", landmarkEntry.getName());
                            fromRoom = entry;
                        }
                        if (landmarkEntry.getName().equals(toRoomNumber)) {
                            Log.d("ending room (HISTORY)", landmarkEntry.getName());
                            toRoom = entry;
                        }
                    }
                }
                catch (JSONException e) {
                    Log.e("Exception", "Failed to get building name: " + e.toString());
                }
                finally {
                    startNavigation();
                }
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    private void startNavigation() {
        Intent myIntent = new Intent(this, NavigationActivity.class);
        myIntent.putExtra("com.Navatar.stepLength", stepLength);
        myIntent.putExtra("com.Navatar.fromRoom", fromRoom.getLandmark());
        myIntent.putExtra("com.Navatar.toRoom", toRoom.getLandmark());
        startActivity(myIntent);
    }

}
