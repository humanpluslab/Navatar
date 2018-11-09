package com.navatar.navigation;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Environment;
import android.os.IBinder;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import com.navatar.maps.particles.ParticleState;
import com.navatar.math.Angles;
import com.navatar.output.file.XmlFile;
import com.navatar.particlefilter.Transition;
import com.navatar.pathplanning.Step;
import com.navatar.protobufs.LandmarkProto;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class NavigationService extends Service {

    private static final int METERS_FROM_PATH = 5;
    private static final boolean WITH_COMPASS = true;
    private static final int COMPASS_COUNTER_MAX = 10;

    private XmlFile outputPerfect = null;
    private XmlFile xmlOutput;
    private String navigationCommand;
    private TextToSpeech tts;
    private Step lastStep;
    private int stepCounter = 0;
    private int compassAverage = 0;
    private String navatarPath = Environment.getExternalStorageDirectory().getPath() + "/Navatar";
    private LandmarkProto.Landmark fromRoom, toRoom;


    private final IBinder binder = new NavBinder();


    @Override
    public void onCreate() {
        super.onCreate();
    }


    @Override
    public IBinder onBind(Intent arg0) {

        return binder;
    }

    public class NavBinder extends Binder {
        public NavigationService getService() {
            return NavigationService.this;
        }
    }

    /*
    private void writeXml() {
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
    */
}
