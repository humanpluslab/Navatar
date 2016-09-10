package com.navatar;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import com.navatar.maps.BuildingMapWrapper;
import com.navatar.maps.MapService;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

public class MapSelectActivity extends Activity {
  private Spinner mapSpinner,campusSpinner;
  private TextView mapSelectTextView;
  private ArrayAdapter<String> mapArrayAdapter,campusArrayAdapter;
  private ArrayList<String> maplist;
  private MapService mapService;
  private Intent mapIntent;

  @Override
  protected void onDestroy() {
      super.onDestroy();
      if(mapService!=null)
        getApplicationContext().unbindService(mMapConnection);
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setTitle("Welcome to Navatar");
    setContentView(R.layout.map_select);

    mapIntent= new Intent(this, MapService.class);

    campusSpinner = (Spinner)findViewById(R.id.campusSpinner);

    campusArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item,
            new ArrayList<String>());
    campusArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

    ArrayList<String> campuslist = new ArrayList<String>();

    try {

      String[] campusNames = getAssets().list("maps");
      campuslist.add("Select a campus");
      for (int i=0;i<campusNames.length;i++){
        campuslist.add(campusNames[i].replaceAll("_"," "));
      }

    } catch (IOException e) {
      e.printStackTrace();
    }

    campusArrayAdapter.addAll(campuslist);
    campusSpinner.setAdapter(campusArrayAdapter);
    campusSpinner.setOnItemSelectedListener(campusSpinnerSelected);
    maplist = new ArrayList<String>();
    maplist.add(0,"Select Building");
    mapArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item,
            maplist);
  }


  public OnItemSelectedListener campusSpinnerSelected = new OnItemSelectedListener() {
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
      if (position != 0) {
        String campusName = campusSpinner.getSelectedItem().toString();
        campusName=campusName.replaceAll(" ","_");
        setContentView(R.layout.map_select_new);
        setTitle("Select the building");
        mapSelectTextView = (TextView)findViewById(R.id.tvmapselect);
        mapSpinner = (Spinner) findViewById(R.id.mapSpinner);


        mapSpinner.setAdapter(mapArrayAdapter);
        mapSpinner.setOnItemSelectedListener(mapSpinnerItemSelected);
        mapArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mapIntent.putExtra("path",campusName);
        Intent defaultIntent = new Intent();
        PendingIntent apr = MapSelectActivity.this.createPendingResult(1,defaultIntent,PendingIntent.FLAG_ONE_SHOT);
        mapIntent.putExtra("pendingIntent",apr);
        startService(mapIntent);
        bindService(mapIntent, mMapConnection, BIND_AUTO_CREATE);
      }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
   };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        maplist.clear();
        maplist.add("Select a building");
        if(data.hasExtra("maps"))
           maplist.addAll((ArrayList<String>) data.getSerializableExtra("maps"));


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
    }
    @Override
    public void onServiceDisconnected(ComponentName name) {
      mapService = null;
    }
  };
}
