package com.navatar.data.source;

import javax.inject.Singleton;

@Singleton
public class MapsRepository implements MapsDataSource {

    /**
     * Marks the cache as invalid, to force an update the next time data is requested. This variable
     * has package local visibility so it can be accessed from tests.
     */
    boolean mCacheIsDirty = false;


    @Override
    public void refreshMaps() {
        mCacheIsDirty = true;
    }

}
