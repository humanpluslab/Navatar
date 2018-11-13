package com.navatar;

import android.support.annotation.VisibleForTesting;
import com.navatar.data.source.MapsRepository;
import com.navatar.di.DaggerAppComponent;

import dagger.android.AndroidInjector;
import dagger.android.DaggerApplication;
import javax.inject.Inject;

public class NavatarApplication extends DaggerApplication {
    @Inject
    MapsRepository mapRepository;

    @Override
    protected AndroidInjector<? extends DaggerApplication> applicationInjector() {
        return DaggerAppComponent.builder().application(this).build();
    }

    /**
     * Our Espresso tests need to be able to get an instance of the {@link TasksRepository}
     * so that we can delete all tasks before running each test
     */
    @VisibleForTesting
    public MapsRepository getMapRepository() {
        return mapRepository;
    }

}
