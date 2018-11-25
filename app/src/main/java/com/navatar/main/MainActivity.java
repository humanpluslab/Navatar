package com.navatar.main;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.navatar.R;
import com.navatar.util.ActivityUtils;

import javax.inject.Inject;

import dagger.Lazy;
import dagger.android.AndroidInjection;
import dagger.android.support.DaggerAppCompatActivity;

public class MainActivity extends DaggerAppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    @Inject
    Lazy<MainFragment> mainFragmentProvider;

    public static Intent newIntent(Context context) {
        Intent i = new Intent(context, MainActivity.class);
        Bundle b = new Bundle();

        i.putExtras(b);
        return i;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);

        setTitle(R.string.welcome_to_navatar);
        setContentView(R.layout.map_select);

        MainFragment mainFragment =
                (MainFragment) getSupportFragmentManager().findFragmentById(R.id.contentFrame);

        if (mainFragment == null) {
            // Get the fragment from dagger
            mainFragment = mainFragmentProvider.get();
            ActivityUtils.addFragmentToActivity(
                    getSupportFragmentManager(), mainFragment, R.id.contentFrame);
        }

        // Load previously saved state, if available.
        if (savedInstanceState != null) { }
    }



}