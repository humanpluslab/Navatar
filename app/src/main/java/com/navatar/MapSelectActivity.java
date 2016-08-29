package com.navatar;

import java.util.ArrayList;

import com.navatar.maps.BuildingMapWrapper;
import com.navatar.maps.MapService;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class MapSelectActivity extends Activity {
  private Spinner mapSpinner;
  private ArrayAdapter<String> mapArrayAdapter;
  private ArrayList<String> maplist;
  private MapService mapService;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setTitle("Map_Selection");
    setContentView(R.layout.map_select);
    Intent mapIntent = new Intent(this, MapService.class);
    startService(mapIntent);
    bindService(mapIntent, mMapConnection, BIND_AUTO_CREATE);

    mapSpinner = (Spinner) findViewById(R.id.mapSpinner);
    mapArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item,
        new ArrayList<String>());
    mapArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

    mapSpinner.setAdapter(mapArrayAdapter);
    mapSpinner.setOnItemSelectedListener(mapSpinnerItemSelected);

  }

  OnItemSelectedListener mapSpinnerItemSelected = new OnItemSelectedListener() {
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
      if (position != 0) {
        mapService.setActiveMap(position - 1);
        Intent intent = new Intent(MapSelectActivity.this, NavigationSelectionActivity.class);
        startActivity(intent);
      }
    }

    public void onNothingSelected(AdapterView<?> arg0) {}
  };

  /** Defines callback for service binding, passed to bindService() */
  private ServiceConnection mMapConnection = new ServiceConnection() {
    @Override
    public void onServiceConnected(ComponentName className, IBinder service) {
      MapService.MapBinder binder = (MapService.MapBinder) service;
      mapService = binder.getService();
      maplist = new ArrayList<String>();
      maplist.add("Select a map");
      for (BuildingMapWrapper map : mapService.maps())
        maplist.add(map.getName());
      mapArrayAdapter.clear();
      mapArrayAdapter.addAll(maplist);
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
      mapService = null;
    }
  };
}
