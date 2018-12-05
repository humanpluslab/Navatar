package com.navatar.navigation;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.MotionEvent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.navatar.R;
import com.navatar.di.ActivityScoped;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import dagger.android.support.DaggerFragment;

@ActivityScoped
public class NavigationFragment extends DaggerFragment implements NavigationContract.View {

    @BindView(R.id.viewStepCount)
    EditText viewStepCount;

    @BindView(R.id.viewDirection)
    TextView viewDirection;

    @BindView(R.id.reverseRouteButton)
    Button reverseRouteButton;

    @BindView(R.id.gestureBox)
    TextView gestureBox;

    @Inject
    NavigationContract.Presenter mPresenter;

    @Inject
    public NavigationFragment() {}

    private GestureDetector mGestureDetector;

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

        View root = inflater.inflate(R.layout.navigation_fragment, container, false);

        ButterKnife.bind(this, root);

        return root;
    }


    public boolean onTouchEvent(@NonNull MotionEvent event) {
        return true;
    }

}
