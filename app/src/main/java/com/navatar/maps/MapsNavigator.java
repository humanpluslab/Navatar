package com.navatar.maps;

import android.content.Context;
import android.content.Intent;

import com.navatar.data.Map;
import com.navatar.navigation.NavigationActivity;

import java.lang.ref.WeakReference;

import javax.inject.Inject;

public class MapsNavigator implements MapsContract.Navigator {


    private final WeakReference<Context> mContext;

    @Inject
    public MapsNavigator(Context context) {
        mContext = new WeakReference<>(context);
    }

    @Override
    public void navigate(Map map) {
        Context context = mContext.get();

        if (context != null) {
            Intent intent = new Intent(context, NavigationActivity.class);
            intent.putExtra("com.navatar.map", map);
            context.startActivity(intent);
        }
    }

}
