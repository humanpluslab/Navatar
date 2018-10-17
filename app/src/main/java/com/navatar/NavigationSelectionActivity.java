package com.navatar;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.accessibility.AccessibilityEvent;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.navatar.maps.BuildingMapWrapper;
import com.navatar.maps.LandmarkWrapper;
import com.navatar.maps.MapService;
import com.navatar.protobufs.LandmarkProto;

public class NavigationSelectionActivity extends Activity {
  private float stepLength = 0.0f;
  private Spinner typeSpinner, modeSpinner, maxDistanceSpinner, fromRoomSpinner, toRoomSpinner;
  private Button startNavigationButton;
  private ArrayAdapter<LandmarkWrapper> roomArrayAdapter;
  private LandmarkWrapper fromRoomItemSelected, toRoomItemSelected;

  private MapService mapService;
  private BuildingMapWrapper map;
  private ArrayList<LandmarkWrapper>rooms;
  private LandmarkWrapper dummyLandmarkWrapper;
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setTitle("Select starting room");
    setContentView(R.layout.navigation_selection_layout);

    Intent mapIntent = new Intent(this, MapService.class);
    startService(mapIntent);
    bindService(mapIntent, mMapConnection, BIND_AUTO_CREATE);
    fromRoomSpinner = (Spinner) findViewById(R.id.fromSpinner);
    rooms = new ArrayList<LandmarkWrapper>();
    LandmarkProto.Landmark dummyLandmark=LandmarkProto.Landmark.getDefaultInstance();
    dummyLandmarkWrapper= new LandmarkWrapper(dummyLandmark);
    dummyLandmarkWrapper.setName("Select Room");
    rooms.add(dummyLandmarkWrapper);
    roomArrayAdapter =
        new ArrayAdapter<LandmarkWrapper>(this, R.layout.room_layout,
            rooms);

    roomArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    fromRoomSpinner.setAdapter(roomArrayAdapter);


    fromRoomSpinner.setOnItemSelectedListener(fromRoomSpinnerItemSelected);


    // ****************
    // Listeners:
    // ****************

    Bundle extras = getIntent().getExtras();
    if (extras != null) {
      stepLength = extras.getFloat("com.Navatar.stepLength");
    }
  }

  OnItemSelectedListener fromRoomSpinnerItemSelected = new OnItemSelectedListener() {
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if(position!=0) {
            fromRoomItemSelected = (LandmarkWrapper) fromRoomSpinner.getItemAtPosition(position);
            setTitle("Select destination room");
            getWindow().getDecorView().sendAccessibilityEvent(AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED);
            setContentView(R.layout.navigation_selection_layout_new);
            toRoomSpinner = (Spinner) findViewById(R.id.toSpinner);
            startNavigationButton = (Button) findViewById(R.id.startNavigationButton);
            startNavigationButton.setVisibility(View.GONE);
            toRoomSpinner.setAdapter(roomArrayAdapter);
            toRoomSpinner.setOnItemSelectedListener(toRoomSpinnerItemSelected);
            startNavigationButton.setOnClickListener(startNavigationButtonClickListener);
        }
    }

    public void onNothingSelected(AdapterView<?> arg0) {}
  };

  OnItemSelectedListener toRoomSpinnerItemSelected = new OnItemSelectedListener() {
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if(position !=0) {
            toRoomItemSelected = (LandmarkWrapper) toRoomSpinner.getItemAtPosition(position);
            startNavigationButton.performClick();
        }
    }

    public void onNothingSelected(AdapterView<?> arg0) {}
  };

  OnClickListener startNavigationButtonClickListener = new OnClickListener() {
    public void onClick(View v) {
      startNavigation();
    }
  };

  @Override
  protected void onDestroy() {
    super.onDestroy();
    if(mapService!=null)
        unbindService(mMapConnection);
  }

  private void startNavigation() {
    Intent myIntent = new Intent(this, NavigationActivity.class);
    myIntent.putExtra("com.Navatar.stepLength", stepLength);
    //myIntent.putExtra("com.Navatar.fromRoom", fromRoomItemSelected.getLandmark());
    //myIntent.putExtra("com.Navatar.toRoom", toRoomItemSelected.getLandmark());
    startActivity(myIntent);
  }

  /** Defines callback for service binding, passed to bindService() */
  private ServiceConnection mMapConnection = new ServiceConnection() {
    @Override
    public void onServiceConnected(ComponentName className, IBinder service) {
      MapService.MapBinder binder = (MapService.MapBinder) service;
      mapService = binder.getService();
      map = mapService.getActiveMap();
      rooms.clear();
      rooms.add(dummyLandmarkWrapper);
      List<LandmarkWrapper> t_rooms=map.destinations();
      Collections.sort(t_rooms, new Comparator<LandmarkWrapper>() {
        public int compare(LandmarkWrapper lhs, LandmarkWrapper rhs) {
          return lhs.toString().compareToIgnoreCase(rhs.toString());
        }
      });
      rooms.addAll(t_rooms);
    }

    @Override
    public void onServiceDisconnected(ComponentName arg0) {
      mapService = null;
    }

  };
}