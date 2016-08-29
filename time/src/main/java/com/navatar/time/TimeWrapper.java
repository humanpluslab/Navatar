/**
 * Contains the wrapper class TimeWrapper.
 */
package com.navatar.time;

import android.os.SystemClock;

/**
 * Wraps common time functions from java. This class is provided without unit testing because the
 * existence of this class is to simplify unit testing for other classes that use the time
 * functions. By unit testing this class we would need to create another wrapper of this class and,
 * thus, create an endless loop. This class is implemented using the singleton design pattern since
 * there is no reason to have multiple instances.
 * 
 * @author Ilias Apostolopoulos
 *
 */
public class TimeWrapper {

  /** The singleton instance of this class */
  private static TimeWrapper instance = null;

  /** Private constructor to avoid multiple instances of the class */
  private TimeWrapper() {}

  /** Initializes the singleton instance, if it has not been initialized yet, and returns it. */
  public static TimeWrapper getInstance() {
    if (instance == null)
      instance = new TimeWrapper();
    return instance;
  }

  /** See {@link java.lang.System#currentTimeMillis currentTimeMillis} */
  public long currentTimeMillis() {
    return System.currentTimeMillis();
  }

  /** See {@link android.os.SystemClock#elapsedRealtime elapsedRealtime} */
  public long elapsedRealtime() {
    return SystemClock.elapsedRealtime();
  }
}
