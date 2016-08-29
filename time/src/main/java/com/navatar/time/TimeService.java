/** 
 * Contains the Time service, responsible for synchronizing time.
 */
package com.navatar.time;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.LinkedList;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

/**
 * Responsible for synchronizing the time on android devices based on an NTP server.
 * 
 * @author Ilias Apostolopoulos
 *
 */
public class TimeService extends Service {

  /**
   * The position in the package sent to or received from the server where the transmit time is
   * written.
   */
  private static final int TRANSMIT_TIME_OFFSET = 40;
  /** The position in the package received from the server where the originate time is written. */
  private static final int ORIGINATE_TIME_OFFSET = 24;
  /** The position in the package received from the server where the receive time is written. */
  private static final int RECEIVE_TIME_OFFSET = 32;
  /** The constant size of the NTP package. */
  private static final int NTP_PACKET_SIZE = 48;
  /** The standard NTP port */
  private static final int NTP_PORT = 123;
  /** The mode of the package sent. */
  private static final int NTP_MODE_CLIENT = 3;
  /** The NTP version. */
  private static final int NTP_VERSION = 3;
  /** The offset in seconds from 1900 to 1970. */
  private static final long OFFSET_1900_TO_1970 = ((365L * 70L) + 17L) * 24L * 60L * 60L;
  /** The binder used by connected activities to communicate with the TimeService. */
  private final IBinder binder = new TimeBinder();
  /** The list of listeners that will be notified when the time sync finishes. */
  private LinkedList<TimeListener> listeners;
  /** The offset between the device's clock and the time server. */
  private long clockOffset;

  /**
   * Called when the service is created. It is responsible for initializing the list with the
   * listeners.
   */
  @Override
  public void onCreate() {
    listeners = new LinkedList<TimeListener>();
  }

  /**
   * Called when the service is started.
   */
  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    return START_STICKY;
  }

  /**
   * Called when an activity tries to connect with the service. It returns the binder that is used
   * to bridge the communication.
   */
  @Override
  public IBinder onBind(Intent intent) {
    return binder;
  }

  /**
   * Responsible for synchronizing the device time with the server time.
   * 
   * @param host
   *          The name of the time server to be contacted.
   * @param timeout
   *          The amount of timeout before the server reply.
   */
  public void syncTime(String host, int timeout) {
    new SyncTime().execute(host, "" + timeout);
  }

  /**
   * Creates the necessary socket for communicating with the NTP server.
   * 
   * @return The created socket.
   * @throws SocketException
   *           If the new socket could not be created.
   */
  protected DatagramSocket createSocket() throws SocketException {
    return new DatagramSocket();
  }

  /**
   * Returns the TimeWrapper instance necessary for time calls.
   * 
   * @return The TimeWrapper instance.
   */
  protected TimeWrapper getTime() {
    return TimeWrapper.getInstance();
  }

  /**
   * Responsible for actually performing the synchronization as an asynchronous task.
   * 
   * @author ilias
   * 
   */
  private class SyncTime extends AsyncTask<String, Void, Long> {

    /**
     * Connects to the time server and calculates the offset between the device and the server. The
     * parameters passed to the asynchronous task are the server name and timeout.
     */
    @Override
    protected Long doInBackground(String... params) {
      long responseTime = 0;
      DatagramSocket socket = null;
      TimeWrapper time = getTime();

      try {
        socket = createSocket();
      } catch (SocketException e) {
        Log.e("Navatar Time", "Failed to create socket: " + e.getLocalizedMessage());
        e.printStackTrace();
        return clockOffset;
      }
      int timeout;
      try {
        timeout = Integer.parseInt(params[1]);
      } catch (NumberFormatException e) {
        Log.e("Navatar Time",
            "Failed to parse timeout value. Invalid parameter: " + e.getLocalizedMessage());
        e.printStackTrace();
        // If the parameter is invalid then timeout will get a default value of 10 seconds.
        timeout = 10000;
      }
      try {
        socket.setSoTimeout(timeout);
      } catch (SocketException e) {
        Log.e("Navatar Time", "Failed to set timeout value: " + e.getLocalizedMessage());
        e.printStackTrace();
        socket.close();
        return clockOffset;
      }
      InetAddress address;
      try {
        address = InetAddress.getByName(params[0]);
      } catch (UnknownHostException e) {
        Log.e("Navatar Time", "Invalid server name: " + e.getLocalizedMessage());
        e.printStackTrace();
        socket.close();
        return clockOffset;
      }
      byte[] buffer = new byte[NTP_PACKET_SIZE];
      DatagramPacket request = new DatagramPacket(buffer, buffer.length, address, NTP_PORT);
      // Sets mode = 3 (client) and version = 3. Mode is written in the low 3 bits of the first
      // byte. Version is written in the bits 3-5 of the first byte.
      buffer[0] = NTP_MODE_CLIENT | (NTP_VERSION << 3);
      // Gets the current time and writes it to the request packet.
      long requestTime = time.currentTimeMillis();
      long requestTicks = time.elapsedRealtime();
      writeTimeStamp(buffer, TRANSMIT_TIME_OFFSET, requestTime);
      try {
        socket.send(request);
      } catch (IOException e) {
        Log.e("Navatar Time", "Socket failed to send the request: " + e.getLocalizedMessage());
        e.printStackTrace();
        socket.close();
        return clockOffset;
      }
      // Reads the response.
      DatagramPacket response = new DatagramPacket(buffer, buffer.length);
      try {
        socket.receive(response);
      } catch (IOException e) {
        Log.e("Navatar Time", "Socket failed to receive the response: " + e.getLocalizedMessage());
        e.printStackTrace();
        socket.close();
        return clockOffset;
      }
      long responseTicks = time.elapsedRealtime();
      responseTime = requestTime + (responseTicks - requestTicks);
      // Extracts the results.
      long originateTime = readTimeStamp(buffer, ORIGINATE_TIME_OFFSET);
      long receiveTime = readTimeStamp(buffer, RECEIVE_TIME_OFFSET);
      long transmitTime = readTimeStamp(buffer, TRANSMIT_TIME_OFFSET);
      clockOffset = ((receiveTime - originateTime) + (transmitTime - responseTime)) / 2;
      socket.close();

      return clockOffset;
    }

    /**
     * Called immediately after doInBackground finishes. It is responsible for notifying all
     * listeners about the clock offset.
     * 
     * @param offset
     *          The offset to send to all listeners.
     */
    @Override
    protected void onPostExecute(Long offset) {
      super.onPostExecute(offset);
      for (TimeListener listener : listeners)
        listener.onClockSynced(offset);
    }
  }

  /**
   * Adds a listener to the class' list.
   * 
   * @param listener
   *          The listener to be added to the list.
   * @return It always returns true.
   */
  public boolean registerTimeListener(TimeListener listener) {
    return listeners.add(listener);
  }

  /**
   * Removes a listener from the class' list.
   * 
   * @param listener
   *          The listener to be removed.
   * @return False if the list was not changed.
   */
  public boolean unregisterTimeListener(TimeListener listener) {
    return listeners.remove(listener);
  }

  /**
   * Reads 32 bits from the buffer and converts it to a long number.
   * 
   * @param buffer
   *          The buffer to read from.
   * @param offset
   *          The position in the buffer to start reading from.
   * @return The value read converted in long.
   */
  private long read32(byte[] buffer, int offset) {
    byte b0 = buffer[offset];
    byte b1 = buffer[offset + 1];
    byte b2 = buffer[offset + 2];
    byte b3 = buffer[offset + 3];
    // Converts signed bytes to unsigned values.
    int i0 = ((b0 & 0x80) == 0x80 ? (b0 & 0x7F) + 0x80 : b0);
    int i1 = ((b1 & 0x80) == 0x80 ? (b1 & 0x7F) + 0x80 : b1);
    int i2 = ((b2 & 0x80) == 0x80 ? (b2 & 0x7F) + 0x80 : b2);
    int i3 = ((b3 & 0x80) == 0x80 ? (b3 & 0x7F) + 0x80 : b3);

    return ((long) i0 << 24) + ((long) i1 << 16) + ((long) i2 << 8) + (long) i3;
  }

  /**
   * Reads the timestamp from the buffer.
   * 
   * @param buffer
   *          The buffer to read from.
   * @param offset
   *          The location in the buffer to start reading from.
   * @return The timestamp expressed in milliseconds.
   */
  private long readTimeStamp(byte[] buffer, int offset) {
    long seconds = read32(buffer, offset);
    long fraction = read32(buffer, offset + 4);
    return ((seconds - OFFSET_1900_TO_1970) * 1000) + ((fraction * 1000L) / 0x100000000L);
  }

  /**
   * Writes the time passed as argument to the buffer provided in the location defined by the offset
   * parameter.
   * 
   * @param buffer
   *          The buffer to be written upon.
   * @param offset
   *          The position in the buffer to be written.
   * @param time
   *          The time to be written in the buffer. The parameter should be expressed in
   *          milliseconds since 1/1/1970 00:00:00 UTC.
   */
  private void writeTimeStamp(byte[] buffer, int offset, long time) {
    long seconds = time / 1000L;
    long milliseconds = time - seconds * 1000L;
    seconds += OFFSET_1900_TO_1970;
    // Writes seconds in big endian format.
    buffer[offset] = (byte) (seconds >> 24);
    buffer[++offset] = (byte) (seconds >> 16);
    buffer[++offset] = (byte) (seconds >> 8);
    buffer[++offset] = (byte) (seconds >> 0);
    // Writes fraction in big endian format.
    long fraction = milliseconds * 0x100000000L / 1000L;
    buffer[++offset] = (byte) (fraction >> 24);
    buffer[++offset] = (byte) (fraction >> 16);
    buffer[++offset] = (byte) (fraction >> 8);
    // Low order bits should be random data.
    buffer[++offset] = (byte) (Math.random() * 255.0);
  }

  /**
   * Bridges the communication between the service and the connected activities.
   * 
   * @author ilias
   *
   */
  public class TimeBinder extends Binder {
    /**
     * Returns the active instance of the TimeService.
     * 
     * @return The active TimeService service.
     */
    public TimeService getService() {
      return TimeService.this;
    }
  }
}
