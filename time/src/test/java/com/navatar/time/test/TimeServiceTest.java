/**
 * Contains unit tests for the TimeService service.
 */
package com.navatar.time.test;

import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.test.ServiceTestCase;

import com.navatar.time.TimeService;
import com.navatar.time.TimeWrapper;

/**
 * Tests the correct functionality of the service TimeService.
 * 
 * @author Ilias Apostolopoulos
 * 
 */
public class TimeServiceTest extends ServiceTestCase<TimeService> {

  /** A TimeService used for testing. */
  private TimeService mTimeService;
  /** The intent used to start the service. */
  private Intent mIntent;
  /** A handler to handle the asynchronous calls to the service. */
  private static Handler handler = new Handler();

  public TimeServiceTest() {
    super(TimeService.class);
  }

  /*
   * (non-Javadoc)
   * 
   * @see android.test.ServiceTestCase#setUp()
   */
  protected void setUp() throws Exception {
    super.setUp();
    mIntent = new Intent(getSystemContext(), TimeService.class);
  }

  /*
   * (non-Javadoc)
   * 
   * @see android.test.ServiceTestCase#tearDown()
   */
  protected void tearDown() throws Exception {
    super.tearDown();
  }

  /**
   * Test method for {@link com.navatar.time.TimeService#onCreate()}. Tests that TimeService is
   * created properly.
   */
  public void testOnCreate() {
    this.startService(mIntent);
    mTimeService = this.getService();
    assertNotNull("TimeService was not started properly.", mTimeService);
  }

  /**
   * Test method for {@link com.navatar.time.TimeService#onDestroy()}. Tests that TimeService is
   * destroyed properly.
   */
  public void testOnDestroy() {
    getSystemContext().startService(mIntent);
    getSystemContext().stopService(mIntent);
    mTimeService = this.getService();
    assertNull("TimeService was not stoppped properly.", mTimeService);
  }

  /**
   * Test method for {@link com.navatar.time.TimeService#syncTime(java.lang.String, int)}.
   */
  public void testSyncTime() {
    System.setProperty("dexmaker.dexcache", getContext().getCacheDir().getPath());
    final DatagramSocket socket = mock(DatagramSocket.class);
    final TimeWrapper time = mock(TimeWrapper.class);
    when(time.currentTimeMillis()).thenReturn(50L);
    DatagramPacket request = null;
    try {
      doAnswer(new Answer<Object>() {

        @Override
        public Object answer(InvocationOnMock invocation) throws Throwable {
          /** The constant size of the NTP package. */
          final int NTP_PACKET_SIZE = 48;
          /** The standard NTP port */
          final int NTP_PORT = 123;
          Object[] args = invocation.getArguments();
          final byte[] buffer = new byte[NTP_PACKET_SIZE];
          DatagramPacket request =
              new DatagramPacket(buffer, buffer.length, InetAddress.getByName(null), NTP_PORT);
          args[0] = request;
          return null;
        }
      }).when(socket).receive(request);
    } catch (IOException e) {
      e.printStackTrace();
    }

    mTimeService = new TimeService() {
      protected DatagramSocket createSocket() {
        return socket;
      }

      protected TimeWrapper getTime() {
        return time;
      }
    };
    class LooperThread extends Thread {

      /*
       * (non-Javadoc)
       * 
       * @see java.lang.Thread#run()
       */
      @Override
      public void run() {
        Looper.prepare();
        handler = new Handler();
        Looper.loop();
      }
    }
    LooperThread thread = new LooperThread();
    thread.start();
    handler.post(new Runnable() {

      @Override
      public void run() {
        mTimeService.syncTime("", 0);
      }
    });
    verify(time).currentTimeMillis();
    verify(time, times(2)).currentTimeMillis();
  }

  /**
   * Test method for
   * {@link com.navatar.time.TimeService#registerTimeListener(com.navatar.time.TimeListener)}.
   */
  public void testRegisterTimeListener() {
    fail("Not yet implemented");
  }

  /**
   * Test method for
   * {@link com.navatar.time.TimeService#unregisterTimeListener(com.navatar.time.TimeListener)}.
   */
  public void testUnregisterTimeListener() {
    fail("Not yet implemented");
  }

}
