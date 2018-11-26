package com.navatar.main;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.navatar.R;
import com.navatar.common.details.RuntimePermissionRequestHandler;
import com.navatar.data.Map;
import com.navatar.di.ActivityScoped;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import butterknife.BindView;
import butterknife.ButterKnife;
import dagger.android.support.DaggerFragment;

@ActivityScoped
public class MainFragment extends DaggerFragment implements MainContract.View {

    @BindView(R.id.mapSpinner)
    Spinner mapSpinner;

    @BindView(R.id.button)
    Button autoLocateButton;

    @BindView(R.id.qrButton)
    Button getQrsCodeButton;

    @Inject
    MainContract.Presenter mPresenter;

    @Inject
    @Named("requestCodes")
    List<Integer> requestCodes;

    private MapListAdapter mListAdapter;

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

        View root = inflater.inflate(R.layout.map_select_frag, container, false);

        ButterKnife.bind(this, root);

        autoLocateButton.setOnClickListener(v-> {
            // mPresenter
        });

        getQrsCodeButton.setOnClickListener(v -> {
            // new IntentIntegrator(self).initiateScan();
        });

        return root;
    }


    @Override
    public void addMaps(List<Map> maps) {

        mListAdapter = new MapListAdapter(getContext(), maps);
        mapSpinner.setAdapter(mListAdapter);
        mapSpinner.setSelection(mListAdapter.getCount());
        mapSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position < mListAdapter.getCount()) {
                    mPresenter.onMapSelected(mapSpinner.getSelectedItem().toString());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
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

    private static class MapListAdapter extends ArrayAdapter {

        public MapListAdapter(Context context, List<Map> maps) {
            super(context, android.R.layout.simple_spinner_item, maps);
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        }

        @Override
        public int getCount() {
            return super.getCount();
        }

        @Override
        public boolean isEnabled(int position){
            if(position >= getCount()) {
                return false;
            }
            return true;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View v = null;

            if (position >= getCount()) {
                v = super.getView(0, convertView, parent);
                TextView tv = (TextView) v;
                tv.setText("");
                tv.setHint(R.string.mapSpinnerLabel);
            } else {
                v = super.getView(position, convertView, parent);
            }
            return v;
        }


    }

}
