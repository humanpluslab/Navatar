package com.navatar.main;

import com.navatar.R;

import android.support.v7.app.AppCompatActivity;

import java.lang.ref.WeakReference;

import javax.inject.Inject;

public class MainNavigator implements MainContract.Navigator {

    private final WeakReference<AppCompatActivity> activityWeakReference;

    @Inject
    public MainNavigator(MainActivity activity) {
        activityWeakReference = new WeakReference<>(activity);
    }

    @Override
    public void navigate() {
        AppCompatActivity activity = activityWeakReference.get();
        if (activity != null) {
            activity.getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.contentFrame, null , null)
                    .commit();
        }
    }
}
