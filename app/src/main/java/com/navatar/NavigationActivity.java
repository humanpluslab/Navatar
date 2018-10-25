package com.navatar;

import java.io.File;
import java.io.IOException;
import java.io.FileWriter;
import java.util.Date;
import java.text.SimpleDateFormat;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Vibrator;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Button;
import android.util.JsonWriter;
import android.widget.Toast;

import com.google.protobuf.InvalidProtocolBufferException;
import com.navatar.maps.BuildingMapWrapper;
import com.navatar.maps.MapService;
import com.navatar.maps.particles.ParticleState;
import com.navatar.math.Angles;
import com.navatar.output.file.XmlFile;
import com.navatar.particlefilter.ParticleFilter;
import com.navatar.particlefilter.Transition;
import com.navatar.pathplanning.AStar;
import com.navatar.pathplanning.Direction;
import com.navatar.pathplanning.Path;
import com.navatar.pathplanning.Step;
import com.navatar.protobufs.LandmarkProto.Landmark;
import com.navatar.sensing.NavatarSensor;
import com.navatar.sensing.NavatarSensorListener;
import com.navatar.sensing.SensingService;

public class NavigationActivity extends Activity implements NavatarSensorListener {

  private static final int METERS_FROM_PATH = 5;
  private static final boolean WITH_COMPASS = true;
  private static final int COMPASS_COUNTER_MAX = 10;

  private TextToSpeech tts;
  private String userName = "";
  private String navatarPath = Environment.getExternalStorageDirectory().getPath() + "/Navatar";
  private int stepCounter = 0;
  private int orientation;
  private EditText viewUserName, viewStepLength, viewStepCount;

  // path planning variables
  private Step lastStep;
  private boolean isAutomatic = true;
  private String navigationCommand;
  private Landmark fromRoom, toRoom;
  private ParticleState startState, endState;

  private AStar pathFinder = null;

  private XmlFile outputPerfect = null;
  private XmlFile xmlOutput;

  private ParticleFilter pf;
  private Handler handler;
  private TextView viewDirection;
  private InputHandler inputHandler;

  private boolean monitorSteps = false;
  private boolean hasChecked;

  private SensingService sensing;
  private MapService mapService;
  private BuildingMapWrapper map;

  private int compassCounter = 0;
  private double[] compassReadingArray;
  private int compassAverage = 0;

  private Path path;
  private int pathIndex;
  private Direction directionGenerator;

  private Button reverseRouteButton;

  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    String mode = "";

    requestWindowFeature(Window.FEATURE_NO_TITLE);
    getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
        WindowManager.LayoutParams.FLAG_FULLSCREEN);
    setContentView(R.layout.navigation_layout);
    reverseRouteButton = (Button) findViewById(R.id.reverseRouteButton);
    reverseRouteButton.setVisibility(View.GONE);

    tts = new TextToSpeech(this, ttsInitListener);

    Bundle extras = getIntent().getExtras();
    if (extras != null) {
    //  userName = extras.getString("com.Navatar.userName");
    //  stepLength = extras.getFloat("com.Navatar.stepLength");
      mode = extras.getString("com.Navatar.mode");

      byte[] fb = extras.getByteArray("com.Navatar.fromRoom");
      byte[] tb = extras.getByteArray("com.Navatar.toRoom");

      try {
        fromRoom = Landmark.parseFrom(fb);
        toRoom = Landmark.parseFrom(tb);

      } catch (InvalidProtocolBufferException e) {
        e.printStackTrace();
      }

      /*if (mode.equalsIgnoreCase("Automatic"))
        isAutomatic = true;
      else
        isAutomatic = false;*/
      getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }
    //viewUserName = (EditText) findViewById(R.id.viewUsername);
//    viewStepLength = (EditText) findViewById(R.id.viewStepLength);
    viewStepCount = (EditText) findViewById(R.id.viewStepCount);
    viewDirection = (TextView) findViewById(R.id.viewDirection);
   // viewUserName.setText(userName);
    //viewStepLength.setText(String.valueOf(stepLength));

    outputPerfect =
        new XmlFile(navatarPath + "/" + "From" + fromRoom.getName() + "To" + toRoom.getName()
            + "FeetTraining.xml");
    outputPerfect.append("<rawData navigationFromTo=\"" + fromRoom.getName() + toRoom.getName() + "\" stepLength=\""
         + "\">\r\n");

    File dir = new File(navatarPath + "/Particles");
    if (!dir.exists())
      dir.mkdir();
    Log.i("XmlFile", "logging in");
    xmlOutput =
        new XmlFile(navatarPath + "/Particles/" + fromRoom.getName() + "_" + toRoom.getName()
            + "_position.xml");
    xmlOutput.append("<locations>\n");
    try {
      xmlOutput.writeFile(false);
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    handler = new Handler();

    // Start and bind with sensing service
    Intent sensingIntent = new Intent(this, SensingService.class);
    startService(sensingIntent);
    bindService(sensingIntent, sensingConnection, BIND_AUTO_CREATE);

    // Bind with map service

    Intent mapIntent = new Intent(this, MapService.class);
    startService(mapIntent);
    bindService(mapIntent, mapConnection, BIND_AUTO_CREATE);

    compassReadingArray = new double[COMPASS_COUNTER_MAX];
    inputHandler = new InputHandler();
  }

  @Override
  public boolean onTouchEvent(MotionEvent event) {
    return inputHandler.onTouchEvent(event);
  }

  // TODO Check that works properly
  private void landmarkConfirmed(long timestamp) {
    if (!navigationCommand.startsWith("Turn")) {
      pf.addTransition(new Transition(compassAverage, 0, timestamp, 0.0, lastStep.getlandmark()
          .getType(), lastStep.isFollowLeft()));

        //TODO : commented out by jiwan

      try {
        pf.execute();
      } catch (IOException e1) {
        e1.printStackTrace();
      }

      lastStep = path.getStep(++pathIndex);
    }
    ParticleState locationEstimate = pf.getSynchronizedLocationEstimate();
    navigationCommand = getNextDirection();

    // if(!WITH_COMPASS && navigationCommand.startsWith("Turn"))
    // {
    // if(navigationCommand.endsWith("around"))
    // lastLandmark.objectType = Landmark.turnAround;
    // else
    // lastLandmark.objectType = Landmark.turn;
    // }
    xmlOutput.append("    <location x=\"" + locationEstimate.getX() + "\" y=\""
        + locationEstimate.getY() + "\" floor=\"" + locationEstimate.getFloor() + "\" compass=\""
        + locationEstimate.getDirection() + "\" steps=\"" + stepCounter + "\" landmark=\""
        + lastStep.getlandmark().getType() + "\" command=\"" + navigationCommand + "\" isLeft=\""
        + lastStep.isFollowLeft() + "\" />\n");
    runOnUiThread(new Runnable() {
      @Override
      public void run() {
        viewDirection.setText(navigationCommand);
      }
    });


    try {
      xmlOutput.writeFile(true);
    } catch (IOException e1) {
      // TODO Auto-generated catch block
      e1.printStackTrace();
    }
    tts.speak(navigationCommand, TextToSpeech.QUEUE_ADD, null);
    if (navigationCommand.equalsIgnoreCase("You have reached your destination")) {
      try {
        Thread.sleep(4000);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
      finish();
    }
  }

  /**
   * Initialize text to speech
   */
  private OnInitListener ttsInitListener = new OnInitListener() {
    public void onInit(int status) {
      if (navigationCommand != null)
        tts.speak(navigationCommand, TextToSpeech.QUEUE_ADD, null);
    }
  };

  public void onStop() {
    outputPerfect.append("</rawData>\r\n");
    try {
      outputPerfect.writeFile(true);
    } catch (IOException e) {
      e.printStackTrace();
    }
    tts.shutdown();
    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    super.onStop();
  }

  public void onDestroy() {
    super.onDestroy();
    if(mapService!=null)
      unbindService(mapConnection);

    xmlOutput.append("</locations>");
    pf.finalize();
    try {
      xmlOutput.writeFile(true);
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    outputPerfect.append("</rawData>\r\n");
    try {
      outputPerfect.writeFile(true);
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    tts.shutdown();
    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
  }

  @Override
  public boolean onKeyDown(int keyCode, KeyEvent event) {
    if (keyCode == KeyEvent.KEYCODE_MENU) {
      onSensorChanged(new float[] { 1, 0, 0 }, NavatarSensor.PEDOMETER, event.getDownTime());
      return true;
    }
    return super.onKeyDown(keyCode, event);
  }

  private class ExecutePathCorrection extends Thread {
    public void run() {
      ParticleState locationEstimate = pf.getSynchronizedLocationEstimate();
      double distanceFromPath = path.distance(locationEstimate);

      if (distanceFromPath > METERS_FROM_PATH) {
        tts.speak("Off course!", TextToSpeech.QUEUE_ADD, null);
        ParticleState endState = map.getRoomLocation(toRoom.getName());
        pathFinder.findPath(locationEstimate, null, endState, toRoom);
        // TODO(ilapost): Complete code here.
        tts.speak(navigationCommand, TextToSpeech.QUEUE_ADD, null);
        xmlOutput.append("    <location x=\"" + locationEstimate.getX() + "\" y=\""
            + locationEstimate.getY() + "\" floor=\"" + locationEstimate.getFloor()
            + "\" compass=\"" + locationEstimate.getDirection() + "\" steps=\"" + stepCounter
            + "\" landmark=\"" + lastStep.getlandmark().getType() + "\" command=\""
            + navigationCommand + "\" isLeft=\"" + lastStep.isFollowLeft() + "\" />\n");
        runOnUiThread(new Runnable() {
          public void run() {
            viewDirection.setText(navigationCommand);
          }
        });

        try {
          xmlOutput.writeFile(true);
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }

    public void interrupt() {}
  }

  private String getNextDirection() {
    monitorSteps = false;
    if(path==null){
      return "No Path Found";
    }

    if (pathIndex >= path.getLength() - 1)
      return path.getStep(path.getLength() - 1).getDirectionString();

    double x1 = path.getStep(pathIndex).getParticleState().getX();
    double y1 = path.getStep(pathIndex).getParticleState().getY();
    double x2 = path.getStep(pathIndex + 1).getParticleState().getX();
    double y2 = path.getStep(pathIndex + 1).getParticleState().getY();
    Double angle = Math.atan(y2 - y1 / x2 - x1) * 180.0 / Math.PI;
    angle = Angles.polarToCompass(angle);
    angle = angle - orientation;
    if (angle > 180.0)
      angle = 360.0 - angle;
    else if (angle < -180.0)
      angle = -360.0 - angle;
    if (angle <= 45.0 && angle >= -45.0) {
      //TODO : commented the following line by jiwan
      monitorSteps = true;

      return path.getStep(pathIndex).getDirectionString();
    } else if (angle > 45.0 && angle <= 135.0) {
      return "Turn right";
    } else if (angle < -45.0 && angle >= -135.0) {
      return "Turn left";
    } else {
      return "Turn around";
    }
  }

  private ServiceConnection mapConnection = new ServiceConnection() {

    @Override
    public void onServiceConnected(ComponentName className, IBinder service) {

      MapService.MapBinder binder = (MapService.MapBinder) service;
      mapService = binder.getService();
      map = mapService.getActiveMap();
      pathFinder = new AStar(map);

      Log.i("Navigation Activity", "Map service connected");

      try {
        writeNavHistory();
      } catch (IOException e) {
        e.printStackTrace();
      }

      startState = map.getRoomLocation(fromRoom.getName());
      pf = new ParticleFilter(navatarPath, userName, mapService.getActiveMap(), startState);
      endState = map.getRoomLocation(toRoom.getName());
      path = pathFinder.findPath(startState, fromRoom, endState, toRoom);
      directionGenerator = new Direction(map.getProtobufMap());

      if (path != null) {
        pathIndex = 0;
        path = directionGenerator.generateDirections(path);

        navigationCommand = getNextDirection();
        lastStep = path.getStep(pathIndex);

        xmlOutput.append("    <location x=\"" + startState.getX() + "\" y=\"" + startState.getY()
            + "\" compass=\"" + orientation + "\" steps=\"" + stepCounter + "\" landmark=\""
            + lastStep.getlandmark().getType() + "\" command=\"" + navigationCommand
            + "\" isLeft=\"" + lastStep.isFollowLeft() + "\" />\n");
      } else {
        navigationCommand = "No path found.";
      }
      runOnUiThread(new Runnable() {
        @Override
        public void run() {
          viewDirection.setText(navigationCommand);
        }
      });

      try {
        xmlOutput.writeFile(true);
      } catch (IOException e) {
        e.printStackTrace();
      }

      if (navigationCommand.equalsIgnoreCase("You have reached your destination.")) {
        reverseRouteButton.setVisibility(View.VISIBLE);
      }

      tts.speak(navigationCommand, TextToSpeech.QUEUE_ADD, null);

      Log.d("Navigation Activity", "Particle filter service connected");
    }

    @Override
    public void onServiceDisconnected(ComponentName arg0) {
      mapService = null;
    }
  };

  private void writeNavHistory() throws IOException {
    FileWriter file = null;

    try {
      JSONObject entry = null;
      try {
        entry = new JSONObject();
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
        entry.put("time", dateFormat.format(date));
        entry.put("campus", mapService.getCampusName());
        entry.put("building", map.getName());
        entry.put("start_room", fromRoom.getName());
        entry.put("end_room", toRoom.getName());
      } catch (JSONException e) {
        Log.e("Exception", "Could not create json object for nav history");
      }

      String storagePath = Environment.getExternalStorageDirectory().getPath() + "/Navatar";
      File directories = new File(storagePath);
      directories.mkdirs();
      file = new FileWriter(storagePath + "/nav_history.json", true);
      file.write(entry.toString() + "\n");
    } catch (IOException e) {
      Log.e("Exception", "File write failed: " + e.toString());
    } finally {
      file.flush();
      file.close();
    }
  }

  public void reverseRoute(View view) {
    finish();
    Intent swap = getIntent();
    Bundle b = new Bundle();
    b.putByteArray("com.Navatar.fromRoom", toRoom.toByteArray());
    b.putByteArray("com.Navatar.toRoom", fromRoom.toByteArray());
    swap.putExtras(b);
    startActivity(swap);
  }

  private ServiceConnection sensingConnection = new ServiceConnection() {

    @Override
    public void onServiceConnected(ComponentName className, IBinder service) {
      SensingService.SensingBinder binder = (SensingService.SensingBinder) service;
      sensing = binder.getService();

      sensing.registerListener(NavigationActivity.this, new int[] { NavatarSensor.COMPASS,
          NavatarSensor.PEDOMETER });
      Log.d("Navigation Activity", "Sensing service connected");
    }

    @Override
    public void onServiceDisconnected(ComponentName arg0) {
      // TODO Maybe unregister listener here
      sensing = null;
    }
  };

  @Override
  public void onSensorChanged(float[] values, int sensor, long timestamp) {

    switch (sensor) {
    case NavatarSensor.PEDOMETER:
      if (monitorSteps) {
        hasChecked = false;
        ++stepCounter;
        //TODO see if this is right way to do it by Manju
        runOnUiThread(new Runnable() {
          public void run() {
            viewStepCount.setText(String.valueOf(stepCounter));
          }
        });

        pf.addTransition(new Transition(compassAverage, 1, timestamp, 0.0, null, false));
        Thread execPF = pf.new ExecutePF();
        execPF.setPriority(Thread.MAX_PRIORITY);
        handler.post(execPF);
      }
      break;
    case NavatarSensor.COMPASS:
      orientation = Angles.discretizeAngle(Angles.compassToScreen(Math.toDegrees(values[0])));
      compassReadingArray[compassCounter++] = orientation;

      if (compassCounter >= COMPASS_COUNTER_MAX) {
        compassAverage = Angles.discretizeAngle(Angles.average(compassReadingArray));
        compassCounter = 0;
        if (WITH_COMPASS) {
          // TODO(ilapost): Replace this manual calls every time we have enough data with a Timer
          // call every 1 second.
          pf.addTransition(new Transition(compassAverage, 0, timestamp, 0.0, null, false));
          Thread execPF = pf.new ExecutePF();
          execPF.setPriority(Thread.MAX_PRIORITY);
          handler.post(execPF);
        }
        break;
      }

      //TODO: commented by manju since it was freezing UI during step counting

      // it must be automatic and every 3 step <- we probably need to adjust it after some testing
   /* if (isAutomatic && !hasChecked && (stepCounter % 3 == 2)) {
        Thread execPathCorrection = new ExecutePathCorrection();
        execPathCorrection.setPriority(Thread.MAX_PRIORITY);
        handler.post(execPathCorrection);
        hasChecked = true;
      }*/
    }
  }

  /*  All gestures for the Navigation Activity screen are handled here. */
  private class InputHandler implements GestureDetector.OnGestureListener, GestureDetector.OnDoubleTapListener {

    private GestureDetector gestureDetector;

    public InputHandler() {
      gestureDetector = new GestureDetector(NavigationActivity.this, this);
    }

    public boolean onTouchEvent(MotionEvent event) {
      return gestureDetector.onTouchEvent(event);
    }

    @Override
    public boolean onDown(MotionEvent motionEvent) {
      TextView gestureBox = (TextView) findViewById(R.id.gestureBox);
      gestureBox.setText("onDown");
      return false;
    }

    @Override
    public void onShowPress(MotionEvent motionEvent) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent motionEvent) {
      TextView gestureBox = (TextView) findViewById(R.id.gestureBox);
      gestureBox.setText("onSingleTapUp");
      return false;
    }

    @Override
    public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
      TextView gestureBox = (TextView) findViewById(R.id.gestureBox);
      gestureBox.setText("onScroll");
      return false;
    }

    @Override
    public void onLongPress(MotionEvent arg0) {

      TextView gestureBox = (TextView) findViewById(R.id.gestureBox);
      gestureBox.setText("onLongPress");

      Vibrator vibrator = null;

      try {
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
      } catch (Exception e) {}
      if (vibrator != null) {
        try {
          vibrator.vibrate(100);
        } catch (Exception e) {}
      }

        // TODO: Placeholder for landmark addition until fully implemented.
        // This will likely require extensive testing.
        Toast.makeText(getBaseContext(), "Landmark added!", Toast.LENGTH_LONG).show();
        tts.speak("Landmark added", TextToSpeech.QUEUE_ADD,null);
        //landmarkConfirmed(System.nanoTime());
    }

    @Override
    public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
      TextView gestureBox = (TextView) findViewById(R.id.gestureBox);
      gestureBox.setText("onFling");
      return false;
    }

    /*@Override
    public boolean onSingleTapConfirmed(MotionEvent event) {
      tts.speak("Repeating, " + navigationCommand, TextToSpeech.QUEUE_ADD, null);
      viewStepCount.setText(String.valueOf(stepCounter));
      viewDirection.setText("Repeating " + navigationCommand);
      return true;
    }*/

    @Override
    public boolean onSingleTapConfirmed(MotionEvent event){

      TextView gestureBox = (TextView) findViewById(R.id.gestureBox);
      gestureBox.setText("onSingleTapConfirmed");

      Vibrator vibrator = null;
      try
      {
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
      }
      catch(Exception e){}

      if(vibrator !=null)
      {
        try
        {
          vibrator.vibrate(200);
        }
        catch(Exception exp){}
      }
      navigationCommand = getNextDirection();
      if(!navigationCommand.matches("(?i:Turn.*)")){
        pathIndex++;
      }

      if (navigationCommand.equalsIgnoreCase("You have reached your destination.")) {
        reverseRouteButton.setVisibility(View.VISIBLE);
      }

      tts.speak(navigationCommand,TextToSpeech.QUEUE_ADD,null);
      viewDirection.setText(navigationCommand);
      return true;

    }

    @Override
    public boolean onDoubleTap(MotionEvent motionEvent) {
        TextView gestureBox = (TextView) findViewById(R.id.gestureBox);
        gestureBox.setText("onDoubleTap");
      return false;
    }

    @Override
    public boolean onDoubleTapEvent(MotionEvent motionEvent) {
      return false;
    }

  }
}
