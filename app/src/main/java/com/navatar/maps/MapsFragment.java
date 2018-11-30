package com.navatar.maps;

import android.content.Context;
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

import com.navatar.R;
import com.navatar.data.Building;
import com.navatar.data.Landmark;
import com.navatar.data.Map;
import com.navatar.di.ActivityScoped;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import dagger.android.support.DaggerFragment;

@ActivityScoped
public class MapsFragment extends DaggerFragment implements MapsContract.View {

    @BindView(R.id.mapSpinner)
    Spinner mapSpinner;

    @BindView(R.id.buildingSpinner)
    Spinner buildingSpinner;

    @BindView(R.id.fromSpinner)
    Spinner fromSpinner;

    @BindView(R.id.toSpinner)
    Spinner toSpinner;

    @BindView(R.id.startNavigationButton)
    Button startNavigationButton;

    @BindView(R.id.showNavigationButton)
    Button showNavigationButton;

    @BindViews({R.id.startNavigationButton, R.id.showNavigationButton})
    List<Button> showButtons;

    private static final ButterKnife.Action<View> VISIBLE =
            (v, index) -> v.setVisibility(View.VISIBLE);

    private static final ButterKnife.Action<View> GONE =
            (v, index) -> v.setVisibility(View.GONE);

    @Inject
    MapsContract.Presenter mPresenter;

    @Inject
    public MapsFragment() { }

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
        mPresenter.dropView();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.map_select_frag, container, false);
        ButterKnife.bind(this, root);
        return root;
    }

    @Override
    public void addMaps(List<Map> maps) {

        MapListAdapter<Map> mListAdapter = new MapListAdapter<>(getContext(), maps, getResources().getString(R.string.mapSpinnerLabel));
        mapSpinner.setAdapter(mListAdapter);
        mapSpinner.setSelection(mListAdapter.getCount());
        mapSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position < mListAdapter.getCount()) {
                    mPresenter.onMapSelected((Map)mapSpinner.getSelectedItem());

                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    @Override
    public void showMap(Map map) {

        List<Building> buildings = map.getBuildings();
        MapListAdapter<Building> mListAdapter = new MapListAdapter<>(getContext(), buildings, getResources().getString(R.string.mapSpinnerLabel));

        buildingSpinner.setAdapter(mListAdapter);
        buildingSpinner.setSelection(mListAdapter.getCount());
        buildingSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mPresenter.onBuildingSelected((Building)buildingSpinner.getSelectedItem());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        ButterKnife.apply(mapSpinner, GONE);
        ButterKnife.apply(buildingSpinner, VISIBLE);
    }

    @Override
    public void showFromLandmark(List<Landmark> landmark) {
        setupLandmarkSpinner(fromSpinner, landmark, getResources().getString(R.string.mapSpinnerLabel));
        fromSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mPresenter.onLandmarkSelected((Landmark)fromSpinner.getSelectedItem());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        ButterKnife.apply(buildingSpinner, GONE);
        ButterKnife.apply(fromSpinner, VISIBLE);
    }

    @Override
    public void showToLandmark(List<Landmark> landmark) {
        setupLandmarkSpinner(toSpinner, landmark, getResources().getString(R.string.mapSpinnerLabel));
        ButterKnife.apply(buildingSpinner, GONE);
        ButterKnife.apply(fromSpinner, VISIBLE);
    }

    private void setupLandmarkSpinner(Spinner spinner, List<Landmark> landmark, String hint) {
        MapListAdapter<Landmark> mListAdapter = new MapListAdapter<>(getContext(), landmark, hint);
        spinner.setAdapter(mListAdapter);
        spinner.setSelection(mListAdapter.getCount());
    }

    private static class MapListAdapter<T> extends ArrayAdapter {

        private final String hint;

        public MapListAdapter(Context context, List<T> items, String hint) {
            super(context, android.R.layout.simple_spinner_item, items);
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            this.hint = hint;
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
                tv.setHint(hint);
            } else {
                v = super.getView(position, convertView, parent);
            }
            return v;
        }
    }
}
