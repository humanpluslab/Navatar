package com.navatar.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.navatar.R;
import com.navatar.common.details.PermissionsRequestResultDispatcher;
import com.navatar.data.Map;
import com.navatar.di.ActivityScoped;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import butterknife.BindView;
import butterknife.ButterKnife;
import dagger.android.support.DaggerFragment;

import static com.google.common.base.Preconditions.checkNotNull;

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

        });

        getQrsCodeButton.setOnClickListener(v -> {
            //new IntentIntegrator(self).initiateScan();
        });


        return root;
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

            if (i == 0) {
                ((TextView)rowView).setText(R.string.mapSpinnerLabel);
            } else {
                final Map map = getItem(i - 1);
                ((TextView) rowView).setText(map.getName());
            }
            return rowView;
        }
    }

}
