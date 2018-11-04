package com.navatar.maps;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;

import com.navatar.R;
import com.navatar.data.Map;
import com.navatar.di.ActivityScoped;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.android.support.DaggerFragment;

import static com.google.common.base.Preconditions.checkNotNull;

@ActivityScoped
public class MapsFragment extends DaggerFragment implements MapsContract.View {

    @Inject
    MapsContract.Presenter mPresenter;

    /**
     * Listener for clicks on maps in the ListView.
     */
    MapItemListener mItemListener = new MapItemListener() {
        @Override
        public void onMapClick(Map clickedMap) {
          //mPresenter.openMapDetails(clickedMap);
        }

        @Override
        public void onActivateMapClick(Map activatedMap) {
            //mPresenter.activateMap(activatedMap);
        }
    };

    private MapsAdapter mListAdapter;


    @Inject
    public MapsFragment() {
        // Requires empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mListAdapter = new MapsAdapter(new ArrayList<Map>(0), mItemListener);
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        mPresenter.result(requestCode, resultCode);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.map_select_frag, container, false);

        // Auto-locate ui items
        Button autoLocateButton = root.findViewById(R.id.button);
        Button getQrsCodeButton = root.findViewById(R.id.qrButton);
        ProgressBar spinner = root.findViewById(R.id.progressBar);
        Spinner campusSpinner = root.findViewById(R.id.campusSpinner);
        campusSpinner.setAdapter(mListAdapter);
        campusSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mPresenter.selectMap();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }

        });

        return root;
    }

    public interface MapItemListener {

        void onMapClick(Map clickedMap);

        void onActivateMapClick(Map activatedMap);
    }

    private static class MapsAdapter extends BaseAdapter {

        private List<Map> mMaps;
        private MapItemListener mItemListener;

        public MapsAdapter(List<Map> maps, MapItemListener itemListener) {
            setList(maps);
            mItemListener = itemListener;
        }

        public void replaceData(List<Map> maps) {
            setList(maps);
            notifyDataSetChanged();
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
                rowView = inflater.inflate(R.layout.task_item, viewGroup, false);
            }

            final Map map = getItem(i);

            rowView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mItemListener.onMapClick(map);
                }
            });

            return rowView;
        }
    }

    @Override
    public boolean isActive() {
        return isAdded();
    }

}
