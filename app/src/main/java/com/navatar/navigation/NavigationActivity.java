package com.navatar.navigation;

import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.navatar.R;
import com.navatar.util.ActivityUtils;

import javax.inject.Inject;

import dagger.Lazy;
import dagger.android.AndroidInjection;
import dagger.android.support.DaggerAppCompatActivity;

public class NavigationActivity extends DaggerAppCompatActivity {

    @Inject
    Lazy<NavigationFragment> navFragmentProvider;

    public void onCreate(Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.navigation_activity);

        NavigationFragment mNavigationFragment =
                (NavigationFragment) getSupportFragmentManager().findFragmentById(R.id.contentFrame);

        if(mNavigationFragment == null) {
            mNavigationFragment = navFragmentProvider.get();
            ActivityUtils.addFragmentToActivity(
                    getSupportFragmentManager(), mNavigationFragment, R.id.contentFrame);
        }
    }


}
