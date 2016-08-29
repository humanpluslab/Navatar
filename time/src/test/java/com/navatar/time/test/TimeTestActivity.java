/** Contains the TimeTestActivity class. */
package com.navatar.time.test;

import android.app.Activity;
import android.os.Bundle;

/**
 * Dummy activity used to test TimeService. Android testing requires an activity to be tested upon.
 * When testing android libraries, such as Time, an activity needs to be created in the test project
 * so as to run the tests.
 */
public class TimeTestActivity extends Activity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_time_test);
  }
}
