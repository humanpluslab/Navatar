package com.navatar.location;

import android.Manifest;
import android.content.Context;
import android.content.IntentSender;
import android.os.Looper;
import android.provider.Settings;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.journeyapps.barcodescanner.BarcodeView;
import com.navatar.R;
import com.navatar.common.details.PermissionsRequestResultDispatcher;
import com.navatar.data.Map;
import com.navatar.location.model.Location;
import com.navatar.maps.MapsFragment;
import com.navatar.util.ActivityUtils;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnClick;
import dagger.Lazy;
import dagger.android.AndroidInjection;
import dagger.android.support.DaggerAppCompatActivity;

import static com.google.common.base.Preconditions.checkNotNull;

public class LocationActivity extends DaggerAppCompatActivity
        implements LocationContract.View {

    private static final String TAG = LocationActivity.class.getSimpleName();

    @BindView(R.id.latitudeTextView)
    TextView latitudeTextView;

    @BindView(R.id.mapSpinner)
    Spinner mapSpinner;

    @BindView(R.id.longitudeTextView)
    TextView longitudeTextView;

    @Inject
    LocationContract.Presenter presenter;

    @Inject
    PermissionsRequestResultDispatcher permissionsRequestResultDispatcher;

    @Inject
    @Named("locationReqCode")
    Integer locationRequestCode;

    @Inject
    @Named("cameraReqCode")
    Integer cameraRequestCode;

    private MapListAdapter mListAdapter;


    public static Intent newIntent(Context context) {
        Intent i = new Intent(context, LocationActivity.class);
        Bundle b = new Bundle();

        i.putExtras(b);
        return i;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.location_activity);
        ButterKnife.bind(this);
    }


    @Override
    public void addMaps(List<Map> maps) {

        mListAdapter = new MapListAdapter(maps);

        mapSpinner.setAdapter(mListAdapter);
        mapSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                presenter.onMapSelected(mapSpinner.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        presenter.loadData();
    }

    @Override
    public void showLatitude(String latitude) {
        latitudeTextView.setText(latitude);
    }

    @Override
    public void showLongitude(String longitude) {
        longitudeTextView.setText(longitude);
    }

    @Override
    public void showNoLocationAvailable() {
        Toast.makeText(LocationActivity.this, R.string.error_accessing_location,
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showGenericError() {
        Toast.makeText(LocationActivity.this, R.string.error_generic,
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showSoftDenied() {

    }

    @Override
    public void showHardDenied() {

    }


    @Override
    public void hidePermissionDeniedWarning() {

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.cleanup();
    }


    @Override
    @SuppressWarnings({"MissingPermission"})
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        Log.e(TAG, String.join(" : ", permissions));
        if (requestCode == locationRequestCode || requestCode == cameraRequestCode ) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                permissionsRequestResultDispatcher.dispatchResult(true, permissions[0]);
            } else {
                permissionsRequestResultDispatcher.dispatchResult(false, permissions[0]);
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private static class MapListAdapter extends BaseAdapter {

        private List<Map> mMaps;

        public MapListAdapter(List<Map> maps) {
            setList(maps);
        }

        private void setList(List<Map> maps) {
            mMaps = checkNotNull(maps);
        }

        @Override
        public int getCount() {
            return mMaps.size();
        }

        @Override
        public Map getItem(int i) {
            return mMaps.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {

            View rowView = view;
            if (rowView == null) {
                LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
                rowView = inflater.inflate(android.R.layout.simple_spinner_dropdown_item, viewGroup, false);
            }

            final Map map = getItem(i);

            ((TextView)rowView).setText(map.getName());

            return rowView;

        }
    }

}