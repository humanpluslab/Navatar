package com.navatar.location;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.navatar.R;
import com.navatar.di.ActivityScoped;

import javax.inject.Inject;

import dagger.android.support.DaggerFragment;

@ActivityScoped
public class LocationFragment extends DaggerFragment implements LocationContract.View {

    @Inject
    LocationContract.Presenter mPresenter;

    @Inject
    PermissionsRequestResultDispatcher permissionsRequestResultDispatcher;

    @Inject
    @Named("locationReqCode")
    Integer locationRequestCode;

    private ProgressBar progressBar;

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

        progressBar = root.findViewById(R.id.progressBar);

        Button autoLocateButton = root.findViewById(R.id.button);

        autoLocateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { mPresenter.getLocation(); }
        });


        return root;
    }

    @Override
    public void showProgressbar() {
        progressBar.setVisibility(View.VISIBLE);

    }

    @Override
    public void hideProgressbar() {
        progressBar.setVisibility(View.INVISIBLE);

    }


    @Override
    public boolean isActive() {
        return isAdded();
    }


}
