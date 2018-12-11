package com.navatar.sensing.compositesensors;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;

import com.navatar.sensing.NavatarSensor;
import com.navatar.sensing.NavatarSensorListener;

import java.util.LinkedList;

/**
 * To detect the steps by analyzing the accelerometer sensor values. The sensor values are compared
 * with an average real time threshold value to detect whether it is a step and steps are filtered
 * from noise by setting up the range for threshold.
 */
public class StepDetector extends NavatarSensor {

    /**
     * Maximum length of dotarray
     */
    private final int dotArrayLength = 10;
    /**
     * Array to save the dot product of sensor values
     */
    private float[] dotArray = new float[dotArrayLength];
    /**
     * Counter variable to check if the array is full
     */
    private int dotArrayCounter = 0;
    /**
     * Count of dot product of sensor values received so far so as to calculate the average
     * efficiently
     */
    private int dotAverageCounter = 0;
    /**
     * Initial average value when the step detection starts
     */
    private float dotAverage = 0.95f;

    private float minDot = Float.MAX_VALUE, maxDot = Float.MIN_VALUE;

    /**
     * Noise limits in the first half of the step
     */
    private static final float STEP_FIRST_HALF_UPPER_NOISE_THRESHOLD = 2f;
    private static final float STEP_FIRST_HALF_LOWER_NOISE_THRESHOLD = -2f;

    /**
     * Noise limits in the second half of the step
     */
    private static final float STEP_SECOND_HALF_UPPER_NOISE_THRESHOLD = 3f;
    private static final float STEP_SECOND_HALF_LOWER_NOISE_THRESHOLD = -3f;

    /**
     * Lower limit of threshold above which is considered as the starting of the next step
     */
    private static final float STEP_START_UPPER_THRESHOLD = 0.05f;

    /**
     * Counter variables to count sensor values above threshold
     */
    private float minCounter = 0, maxCounter = 0;

    /**
     * Count of sensor values which are above threshold. Four sensor values above the threshold should
     * be collected in order to detect it as a half step.
     */
    private static final int MINIMUM_COUNT_OF_SENSOR_VALUES_FOR_STEP_DETECTION = 4;

    /**
     * Detects if the step is the first half or the second half
     */
    private boolean stepFirstHalf = true;

    /**
     * Initializes the type of sensor used and the navatar listeners
     */
    public StepDetector(SensorManager mgr) {
        super(mgr);
        types = new int[]{Sensor.TYPE_ACCELEROMETER};
        delays = new int[]{SensorManager.SENSOR_DELAY_UI};

        listeners = new LinkedList<NavatarSensorListener>();
    }

    /**
     * This function counts the step and notify it to the listener. It initially takes the dot product
     * of x,y, and z axis of accelerometer sensor values. Average of the dot product is calculated by
     * using all the previous sensor values received so far. The amount of change between the current
     * sensor value and the average is stored in an array and again the average is taken to filter out
     * the noise. This average is then compared with threshold values. For the first half of the step,
     * values from 0-2f and 0- (-2f) are considered as noise. So, the if loop checks if it's noise or
     * not and then it increases maxcounter to determine if enough values are received to make sure it
     * is a step. If enough values are not received (i.e if counter is less than 4),it is again
     * considered as noise. Once we determined the first half of the step now we determine the second
     * half of the step. Here 0-(-3f) is considered as noise and if its a noise all the variables are
     * reset to initial value. When the value exceeds a certain threshold limit in second half of the
     * step again it is considered as error value and resets all the variables to initial value. If
     * its not noise or error then it increases mincounter to determine if enough values are received
     * to consider it as second step. once we have enough values to determine the first half and
     * second half of the step, one whole step is determined and notifies the listeners.
     */

    public void onSensorChanged(SensorEvent event) {

        float inX = event.values[0];
        float inY = event.values[1];
        float inZ = event.values[2];

        float dot = (inX * inX + inY * inY + inZ * inZ);

        dotAverage = (dotAverage * dotAverageCounter + dot) / ++dotAverageCounter;

        float sub = dot - dotAverage;

        ++dotArrayCounter;
        if (dotArrayCounter == dotArrayLength)
            dotArrayCounter = 0;

        dotArray[dotArrayCounter] = sub;

        float subAvg = 0;
        for (int i = 0; i < dotArrayLength; i++)
            subAvg += dotArray[i];
        subAvg = subAvg / dotArrayLength;

        if (stepFirstHalf) {
            minCounter = 0;
            if (subAvg > maxDot) {
                maxDot = subAvg;
            }
            if (subAvg > STEP_FIRST_HALF_UPPER_NOISE_THRESHOLD)
                maxCounter++;
            if (subAvg < STEP_FIRST_HALF_LOWER_NOISE_THRESHOLD) {
                if (maxCounter > MINIMUM_COUNT_OF_SENSOR_VALUES_FOR_STEP_DETECTION) {
                    stepFirstHalf = false;
                } else {
                    maxDot = Float.MIN_VALUE;
                    maxCounter = 0;
                }
            }

        } else {
            if (subAvg < minDot)
                minDot = subAvg;
            if (subAvg < STEP_SECOND_HALF_LOWER_NOISE_THRESHOLD)
                ++minCounter;
            if (minCounter > STEP_SECOND_HALF_UPPER_NOISE_THRESHOLD) {
                for (NavatarSensorListener listener : listeners)
                    listener.onSensorChanged(new float[]{1f}, NavatarSensor.PEDOMETER, event.timestamp);

                stepFirstHalf = true;
                minDot = Float.MAX_VALUE;
                maxDot = Float.MIN_VALUE;
                minCounter = 0;
                maxCounter = 0;
            } else {
                if (subAvg > STEP_START_UPPER_THRESHOLD) {
                    stepFirstHalf = true;
                    minCounter = 0;
                    maxCounter = 0;
                    minDot = Float.MAX_VALUE;
                    maxDot = Float.MIN_VALUE;
                }
            }
        }
    }

    public void onAccuracyChanged(Sensor arg0, int arg1) {
    }
}
