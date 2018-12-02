package com.navatar.main;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.navatar.R;
import com.navatar.common.details.RuntimePermissionRequestHandler;
import com.navatar.di.ActivityScoped;
import com.navatar.maps.MapsActivity;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import butterknife.BindView;
import butterknife.ButterKnife;
import dagger.android.support.DaggerFragment;

@ActivityScoped
public class MainFragment extends DaggerFragment implements MainContract.View {

    @BindView(R.id.mapSelectButton)
    Button mapSelectButton;

    @BindView(R.id.button)
    Button autoLocateButton;

    @BindView(R.id.qrButton)
    Button getQrsCodeButton;

    @Inject
    MainContract.Presenter mPresenter;

    @Inject
    @Named("requestCodes")
    List<Integer> requestCodes;


    @Inject
    public MainFragment() {
        // Requires empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter.setPermissionHandler(new RuntimePermissionRequestHandler(this));
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
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
        // case mPresenter is orchestrating a long running task
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.main_frag, container, false);

        ButterKnife.bind(this, root);

        mapSelectButton.setOnClickListener(v -> { mPresenter.openMaps();});

        autoLocateButton.setOnClickListener(v-> { mPresenter.getLocation(); });

        getQrsCodeButton.setOnClickListener(v -> {
            // new IntentIntegrator(self).initiateScan();
        });

        return root;
    }


    @Override
    public void openMapsUI() {
        Intent intent = new Intent(getContext(), MapsActivity.class);
        startActivity(intent);
    }

    @Override
    public void showNoLocationAvailable() {
        Toast.makeText(getActivity(), R.string.error_accessing_location, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showGenericError() {
        Toast.makeText(getActivity(), R.string.error_generic, Toast.LENGTH_SHORT).show();
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


}
