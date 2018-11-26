package com.navatar.di;

import android.app.Application;
import android.content.Context;

import com.navatar.location.GeofencingProvider;
import com.navatar.location.LocationProvider;
import com.navatar.location.details.AndroidLocationProvider;
import com.navatar.location.details.QRCodeScanner;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import dagger.multibindings.ElementsIntoSet;

/**
 * This is a Dagger module. We use this to bind our Application class as a Context in the AppComponent
 * By using Dagger Android we do not need to pass our Application instance to any module,
 * we simply need to expose our Application as Context.
 * One of the advantages of Dagger.Android is that your
 * Application & Activities are provided into your graph for you.
 * {@link AppComponent}.
 */
@Module
public abstract class ApplicationModule {
    //expose Application as an injectable context
    @Binds
    abstract Context bindContext(Application application);

    @Binds
    abstract LocationProvider provideLocationProvider(AndroidLocationProvider locationProvider);

    @Binds
    abstract GeofencingProvider provideGeofencingProvider(AndroidLocationProvider geofencingProvider);


    @Provides
    @ElementsIntoSet
    static Set<LocationProvider> provideLocationProviders(AndroidLocationProvider alp, QRCodeScanner qrs) {
        return new HashSet<>(Arrays.asList(alp, qrs));
    }

}

