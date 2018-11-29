package com.navatar.maps;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.navatar.R;
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

    @BindViews({R.id.mapSpinner, R.id.buildingSpinner})
    List<Spinner> spinnerViews;

    private static final ButterKnife.Action<View> VISIBLE =
            (v, index) -> v.setVisibility(View.VISIBLE);

    private static final ButterKnife.Action<View> GONE =
            (v, index) -> v.setVisibility(View.GONE);


    private MapListAdapter mListAdapter;

    @Inject
    MapsContract.Presenter mPresenter;


    @Inject
    public MapsFragment() {

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

        mListAdapter = new MapListAdapter(getContext(), maps);
        mapSpinner.setAdapter(mListAdapter);
        mapSpinner.setSelection(mListAdapter.getCount());
        mapSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position < mListAdapter.getCount()) {
                    mPresenter.onMapSelected((Map)mapSpinner.getSelectedItem());
                    ButterKnife.apply(buildingSpinner, VISIBLE);
                    ButterKnife.apply(mapSpinner, GONE);

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    public void showMap(Map map) {

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
