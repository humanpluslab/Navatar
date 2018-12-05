package com.navatar;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.navatar.maps.MapService;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MapSelectActivity extends Activity {
  // Manual selection variables
  private Spinner mapSpinner,campusSpinner;
  private ArrayAdapter<String> mapArrayAdapter;
  private ArrayList<String> maplist;
  private MapService mapService;
  private Intent mapIntent;
  private PendingIntent pendingIntent;
  //public static boolean ActivityDestryoed;
  private ArrayList<String> campusNames;

  // Auto-location variables
  private static final int MAX_LOCATION_SAMPLES = 8;
  private static final int MAX_LOCATION_ACCURACY_DIST = 26; // Buildings are typically at least 40m apart
  private String CAMPUS_GEOFENCES_JSON_FILENAME = "Campus_Geofences.json";
  private JSONArray campusGeofences;
  private Button autoLocateButton;
  private Button getQrsCodeButton;
  private ProgressBar spinner;
  private int locationSamples;
  private LocationManager locationManager;
  private LocationListener listener;
  private float accuracy;
  private double LocLat;
  private double LocLong;
  private boolean CampusAutoSelected;

  // Storage Permissions for route history
  private static final int REQUEST_EXTERNAL_STORAGE = 1;
  private static String[] PERMISSIONS_STORAGE = {
          Manifest.permission.READ_EXTERNAL_STORAGE,
          Manifest.permission.WRITE_EXTERNAL_STORAGE
  };

  @Override
  protected void onDestroy() {

      super.onDestroy();
      if(mapService!=null)
       unbindService(mMapConnection);
      if(pendingIntent!=null)
          pendingIntent.cancel();
  }

  @Override
  protected void onResume(){
    super.onResume();

  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setTitle(R.string.welcomeToNavatar);
    setContentView(R.layout.maps_activity);

    // Auto-locate ui items
    autoLocateButton = (Button) findViewById(R.id.button);
    getQrsCodeButton = (Button) findViewById(R.id.qrButton);
    //spinner = (ProgressBar)findViewById(R.id.progressBar);

    // Verify storage for route history
    verifyStoragePermissions(this);

    setupCampuses();

    setupMapService();

    configureQrCodeButton();

    // Setup location manager for auto-location
    configureLocationManager();

    // Setup autoLocateButton
    configureAutoLocateButton();

  }

  @Override
  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    switch (requestCode) {
      // Location permission
      case 10:
        // Accepted
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
          // Click autoLocateButton again now that we have location permission
          autoLocateButton.performClick();
        }
        // Denied
        else {
          // Inform user we need permissions
          Toast.makeText(getBaseContext(), "Location permission is required for auto-locating.",
                  Toast.LENGTH_LONG).show();
        }

        break;
    }
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    try {

      // Get the results of the QR Code scanner
      IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

      if(result != null) {
        if(result.getContents() == null) {
          Toast.makeText(this, R.string.cancelled, Toast.LENGTH_LONG).show();
        } else {
          Toast.makeText(this, "Scanned: " + result.getContents(), Toast.LENGTH_LONG).show();
        }
      } else {
        super.onActivityResult(requestCode, resultCode, data);
      }

      maplist.clear();
      maplist.add("Select a building");

      // If maps loaded for selected campus
      if(data.hasExtra("maps")) {
        // Add maps to spinner
        maplist.addAll((ArrayList<String>) data.getSerializableExtra("maps"));

        // If we have a location with high enough accuracy (campus was auto selected)
        //   and building geofences were loaded
        if (CampusAutoSelected && data.hasExtra("geofences")) {
          // Get building geofences and convert back to json array
          String geofencesString = data.getStringExtra("geofences");
          JSONArray buildingGeofences = new JSONArray(geofencesString);

          // Send in location, get out name of building if supported or null
          String foundBuilding = checkIfLocationIsInsideJSONGeofence(buildingGeofences);

          // If location in supported building
          if (foundBuilding != null) {
            // Replace underscores with spaces, MapService does this for mapList
            foundBuilding = foundBuilding.replaceAll("_"," ");

            // Get index of located building name for spinner selection
            for (int i = 1; i < maplist.size(); i++) { // skip building label at 0
              if (maplist.get(i).equals(foundBuilding)) {
                // Select building
                mapSpinner.setSelection(i);
              }
            }
          }
          // Not found in supported building
          else {
            // Assuming location is inaccurate
            Toast.makeText(getBaseContext(), "Location is not accurate enough to auto-select building.",
                    Toast.LENGTH_LONG).show();
          }
        }
      }
      pendingIntent = null;
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void setupMapService() {
    mapIntent= new Intent(this, MapService.class);
    maplist = new ArrayList<String>();
    maplist.add(0,"Select Building");
    mapArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item,
            maplist);
    startService(mapIntent);
    bindService(mapIntent, mMapConnection, BIND_AUTO_CREATE);
  }

  private void setupCampuses() {
    campusNames = new ArrayList<String>();
    campusSpinner = (Spinner)findViewById(R.id.mapSpinner);
    ArrayAdapter<String> campusArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item,
            new ArrayList<String>());
    campusArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    ArrayList<String> campuslist = new ArrayList<String>();

    CampusAutoSelected = false;

    try {
      // Get campus files
      String[] campusFiles = getAssets().list("maps");

      // Add campuses to spinner
      campuslist.add("Select a campus");
      for (String campusFile : campusFiles) {
        // If file is not campus geofences
        if (!campusFile.equals(CAMPUS_GEOFENCES_JSON_FILENAME)) {
          // Add campus to spinner
          campuslist.add(campusFile.replaceAll("_", " "));

          // Add to campusNames arrayList
          campusNames.add(campusFile);
        }
      }
      // Load campus geofences
      loadCampusGeofencesJSONFromAsset();

      campusArrayAdapter.addAll(campuslist);
      campusSpinner.setAdapter(campusArrayAdapter);
      campusSpinner.setOnItemSelectedListener(campusSpinnerSelected);
    }
    catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void loadCampusGeofencesJSONFromAsset() {
    String json = null;
    try {
      InputStream is = this.getAssets().open("maps/"+ CAMPUS_GEOFENCES_JSON_FILENAME);
      int size = is.available();
      byte[] buffer = new byte[size];
      is.read(buffer);
      is.close();
      json = new String(buffer, "UTF-8");

      try {
        JSONObject obj = new JSONObject(json);
        campusGeofences = obj.getJSONArray("campuses");
      } catch (JSONException e) {
        e.printStackTrace();
      }
    } catch (IOException ex) {
      ex.printStackTrace();
    }
  }

  // Returns name of found area, or null
  private String checkIfLocationIsInsideJSONGeofence( JSONArray geofences ) {
    try {
      // Loop through input geofences
      for (int i = 0; i < geofences.length(); i++) {
        JSONObject jo_inside = geofences.getJSONObject(i);

        JSONObject geofence = jo_inside.getJSONObject("geofence");
        double minLatitude = geofence.getDouble("minLatitude");
        double minLongitude = geofence.getDouble("minLongitude");
        double maxLatitude = geofence.getDouble("maxLatitude");
        double maxLongitude = geofence.getDouble("maxLongitude");

        // If location is inside geofence
        if (LocLat >= minLatitude && LocLat <= maxLatitude &&
                LocLong >= minLongitude && LocLong <= maxLongitude) {
          // Return the name of the geofence
          return jo_inside.getString("name");
        }
      }

      // Finished search and didn't find a match
      return null;
    } catch (JSONException e) {
      e.printStackTrace();
      return null;
    }
  }

  void configureLocationManager() {
    locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
    listener = new LocationListener() {
      @Override
      public void onLocationChanged(Location location) {
        locationSamples++;

        // If you draw a circle centered at this location's latitude and longitude,
        // and with a radius equal to the accuracy(meters), then there is a 68%
        // probability that the true location is inside the circle
        accuracy = location.getAccuracy();

        LocLat = location.getLatitude();
        LocLong = location.getLongitude();

        // If location sample is accurate enough to use
        if(accuracy <= MAX_LOCATION_ACCURACY_DIST) {
          // Stop requesting locations, hide busy spinner
          locationManager.removeUpdates(listener);
          spinner.setVisibility(View.INVISIBLE);

          // Send in location, get out name of campus if supported or null
          String foundCampus = checkIfLocationIsInsideJSONGeofence(campusGeofences);

          // If location is on supported campus
          if (foundCampus != null){
            // Get index of located campusName for spinner selection
            for (int i=0;i<campusNames.size();i++){
              if (campusNames.get(i).equals(foundCampus)){
                // Set flag for building auto locate to be attempted
                CampusAutoSelected = true;

                // Select campus
                campusSpinner.setSelection(i+1); // +1 for select campus label at [0]
              }
            }
          }
          // Not found on supported campuses
          else {
            Toast.makeText(getBaseContext(), "Location is not on a supported campus.",
                    Toast.LENGTH_LONG).show();
          }
        }
        // Otherwise, continue getting location if max location samples has not been reached
        else if(locationSamples < MAX_LOCATION_SAMPLES) {
          // Clear lat and long
          LocLat = Double.NaN;
          LocLong = Double.NaN;
        }

        // Failed to locate user
        else {
          Toast.makeText(getBaseContext(), "Unable to find an accurate location.",
                  Toast.LENGTH_LONG).show();

          // Stop requesting locations, hide busy spinner
          locationManager.removeUpdates(listener);
          spinner.setVisibility(View.INVISIBLE);

          // Clear lat and long
          LocLat = Double.NaN;
          LocLong = Double.NaN;
        }
      }

      @Override
      public void onStatusChanged(String s, int i, Bundle bundle) {
      }

      @Override
      public void onProviderEnabled(String s) {
      }

      @Override
      public void onProviderDisabled(String provider) {
        // GPS needs to be enabled
        if(provider.equals("gps")) {
          Toast.makeText(getApplicationContext(), "GPS must be enabled for Navatar to find your location."
                  + "The location settings menu has been opened for you.", Toast.LENGTH_LONG).show();
          Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
          startActivity(i);
        }
      }
    };
  }

  private void configureAutoLocateButton(){
    // Setup autoLocateButton to request location
    autoLocateButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        // If we don't have permission for location
        if (ActivityCompat.checkSelfPermission(getBaseContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getBaseContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
          // If android 6.0 or above
          if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Request permission popup, will click autoLocateButton again if permissions granted
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.INTERNET}, 10);
          }
          return;
        }
        // All below only executes if permissions granted

        // Listen for location updates
        //  WiFi can get locations within ~20-26m accuracy (NETWORK_PROVIDER)
        //  Cellular locations are accurate to 2000m, could be used for campus selection (NETWORK_PROVIDER)
        //  GPS can get down to 3m with enough time but is useless in some buildings (GPS_PROVIDER)
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 0, listener);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, listener);

        Toast.makeText(getBaseContext(), "Finding your location.",
                Toast.LENGTH_SHORT).show();

        // Show busy spinner
        spinner.setVisibility(View.VISIBLE);

        // Init sample counter
        locationSamples = 0;
      }
    });
  }

  private void configureQrCodeButton() {

    final MapSelectActivity self = this;

    getQrsCodeButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        new IntentIntegrator(self).initiateScan();

      }
    });
  }

  public OnItemSelectedListener campusSpinnerSelected = new OnItemSelectedListener() {
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
      // If a campus is selected
      if (position != 0) {
        // Stop requesting locations, hide busy spinner
        //  For when user manually selects campus during auto-locate
        locationManager.removeUpdates(listener);
        spinner.setVisibility(View.INVISIBLE); // For if we allow going back to campus selection in future

        String campusName = campusSpinner.getSelectedItem().toString();
        campusName=campusName.replaceAll(" ","_");
        setContentView(R.layout.map_select_new);
        setTitle("Select the building");

        mapSpinner = (Spinner) findViewById(R.id.mapSpinner);
        mapSpinner.setAdapter(mapArrayAdapter);
        mapSpinner.setOnItemSelectedListener(mapSpinnerItemSelected);

        mapArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        mapIntent.putExtra("path",campusName);

        Intent defaultIntent = new Intent();
        pendingIntent = MapSelectActivity.this.createPendingResult(1,defaultIntent,PendingIntent.FLAG_ONE_SHOT);
        mapIntent.putExtra("pendingIntent",pendingIntent);
        startService(mapIntent);
        bindService(mapIntent, mMapConnection, BIND_AUTO_CREATE);
      }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
   };

  public OnItemSelectedListener mapSpinnerItemSelected = new OnItemSelectedListener() {
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
      // If building is selected
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

  public void navHistory(View view) {
    Intent intent = new Intent(MapSelectActivity.this, NavigationHistoryActivity.class);
    startActivity(intent);
  }

  public static void verifyStoragePermissions(Activity activity) {
      // Check if we have write permission
      int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

      if (permission != PackageManager.PERMISSION_GRANTED) {
          // We don't have permission so prompt the user
          ActivityCompat.requestPermissions(
                  activity,
                  PERMISSIONS_STORAGE,
                  REQUEST_EXTERNAL_STORAGE
          );
      }
  }
}
