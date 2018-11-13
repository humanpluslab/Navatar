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
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.journeyapps.barcodescanner.BarcodeView;
import com.navatar.R;
import com.navatar.common.details.PermissionsRequestResultDispatcher;
import com.navatar.location.model.Location;
import com.navatar.util.ActivityUtils;

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

public class LocationActivity extends DaggerAppCompatActivity
        implements LocationContract.View {

    @BindView(R.id.latitudeTextView)
    TextView latitudeTextView;

    @BindView(R.id.longitudeTextView)
    TextView longitudeTextView;

    @BindView(R.id.softDenyTextView)
    TextView softDeniedWarningTextView;

    @BindView(R.id.hardDenyTextView)
    TextView hardDeniedWarningTextView;

    @BindViews({R.id.softDenyTextView, R.id.hardDenyTextView})
    List<TextView> deniedTextViews;

    @BindView(R.id.zxing_barcode_surface)
    BarcodeView barcodeView;

    private static final ButterKnife.Action<View> VISIBLE =
            (v, index) -> v.setVisibility(View.VISIBLE);
    private static final ButterKnife.Action<View> GONE =
            (v, index) -> v.setVisibility(View.GONE);

    @Inject
    LocationContract.Presenter presenter;

    @Inject
    PermissionsRequestResultDispatcher permissionsRequestResultDispatcher;

    @Inject
    @Named("locationReqCode")
    Integer locationRequestCode;

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
        ButterKnife.apply(softDeniedWarningTextView, VISIBLE);
        ButterKnife.apply(hardDeniedWarningTextView, GONE);
    }

    @Override
    public void showHardDenied() {
        ButterKnife.apply(hardDeniedWarningTextView, VISIBLE);
        ButterKnife.apply(softDeniedWarningTextView, GONE);
    }


    @Override
    public void hidePermissionDeniedWarning() {
        ButterKnife.apply(deniedTextViews, GONE);
    }


    @OnClick(R.id.softDenyTextView)
    void softDenyTextViewClicked(View view) {
        presenter.loadData();
    }

    @OnClick(R.id.hardDenyTextView)
    void hardDenyTextViewClicked(View view) {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivity(intent);
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
        if (requestCode == locationRequestCode) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                permissionsRequestResultDispatcher.dispatchResult(true);
            } else {
                permissionsRequestResultDispatcher.dispatchResult(false);
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

}