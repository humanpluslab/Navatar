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
import java.util.function.Consumer;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import dagger.android.support.DaggerFragment;
import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.subjects.PublishSubject;

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

    private final CompositeDisposable disposables = new CompositeDisposable();

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
        disposables.clear();
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
    public void showMaps(List<Map> maps) {
        MapListAdapter<Map> listAdapter = new MapListAdapter<>(getContext(), maps, R.string.mapSpinnerLabel);
        listAdapter.setSpinner(mapSpinner);
        disposables.add(listAdapter.getSelected()
                .subscribe(
                        l -> mPresenter.onMapSelected(l)
                ));
    }

    @Override
    public void showMap(Map map) {
        ButterKnife.apply(mapSpinner, GONE);
        ButterKnife.apply(buildingSpinner, VISIBLE);

        List<Building> buildings = map.getBuildings();
        MapListAdapter<Building> listAdapter = new MapListAdapter<>(getContext(), buildings, R.string.mapSpinnerLabel);
        listAdapter.setSpinner(buildingSpinner);
        disposables.add(listAdapter.getSelected()
                .subscribe(
                    l -> mPresenter.onBuildingSelected(l)
                ));
    }

    @Override
    public void showFromLandmark(List<Landmark> landmark) {
        MapListAdapter<Landmark> listAdapter = new MapListAdapter<>(getContext(), landmark, R.string.mapSpinnerLabel);
        listAdapter.setSpinner(fromSpinner);
        disposables.add(listAdapter.getSelected()
                .subscribe(
                        l -> mPresenter.onFromLandmarkSelected(l)
                ));

        ButterKnife.apply(buildingSpinner, GONE);
        ButterKnife.apply(fromSpinner, VISIBLE);
    }

    @Override
    public void showToLandmark(List<Landmark> landmark) {
        MapListAdapter<Landmark> listAdapter = new MapListAdapter<>(getContext(), landmark, R.string.mapSpinnerLabel);
        listAdapter.setSpinner(toSpinner);
        disposables.add(listAdapter.getSelected()
                .subscribe(
                        l -> mPresenter.onToLandmarkSelected(l)
                ));

        ButterKnife.apply(fromSpinner, GONE);
        ButterKnife.apply(toSpinner, VISIBLE);
    }

    private static class MapListAdapter<T> extends ArrayAdapter implements AdapterView.OnItemSelectedListener {

        private final String mHint;

        private PublishSubject<T> mSource = PublishSubject.create();

        public MapListAdapter(Context context, List<T> items, int hint) {
            this(context, items, context.getResources().getString(hint));
        }

        public MapListAdapter(Context context, List<T> items, String hint) {
            super(context, android.R.layout.simple_spinner_item, items);
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            mHint = hint;
        }

        public void setSpinner(Spinner spinner) {
            spinner.setAdapter(this);
            spinner.setOnItemSelectedListener(this);
            spinner.setSelection(getCount());
        }

        public Observable<T> getSelected() {
            return mSource;
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
                tv.setHint(mHint);
            } else {
                v = super.getView(position, convertView, parent);
            }
            return v;
        }

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            if (position < getCount()) {
                mSource.onNext((T)getItem(position));
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {}

    }
}
