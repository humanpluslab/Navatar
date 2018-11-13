package com.navatar.location;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.navatar.R;
import com.navatar.common.details.PermissionsRequestResultDispatcher;
import com.navatar.di.ActivityScoped;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;

import butterknife.OnClick;
import dagger.android.support.DaggerFragment;

@ActivityScoped
public class LocationFragment extends DaggerFragment implements LocationContract.View {

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

    private static final ButterKnife.Action<View> VISIBLE =
            (v, index) -> v.setVisibility(View.VISIBLE);
    private static final ButterKnife.Action<View> GONE =
            (v, index) -> v.setVisibility(View.GONE);

    @Inject
    LocationContract.Presenter mPresenter;

    @Inject
    PermissionsRequestResultDispatcher permissionsRequestResultDispatcher;

    @Inject
    @Named("locationReqCode")
    Integer locationRequestCode;

    @Inject
    public LocationFragment() {}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.takeView(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mPresenter.dropView();  //prevent leaking activity in
        // case presenter is orchestrating a long running task
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.location_frag, container, false);

        return root;
    }

    @Override
    public void onStart() {
        super.onStart();
        mPresenter.loadData();
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
        Toast.makeText(getActivity(), R.string.error_accessing_location,
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showGenericError() {
        Toast.makeText(getActivity(), R.string.error_generic,
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
        mPresenter.loadData();
    }

    @OnClick(R.id.hardDenyTextView)
    void hardDenyTextViewClicked(View view) {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getActivity().getPackageName(), null);
        intent.setData(uri);
        startActivity(intent);
    }

}
