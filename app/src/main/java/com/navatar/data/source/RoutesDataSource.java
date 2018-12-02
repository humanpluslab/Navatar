package com.navatar.data.source;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.navatar.data.Route;

public interface RoutesDataSource {

    @Nullable
    Route getSelectedRoute();

    void setSelectedRoute(@NonNull Route route);

}
