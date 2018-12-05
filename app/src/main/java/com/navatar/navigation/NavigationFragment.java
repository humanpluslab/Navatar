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

        reverseRouteButton.setOnClickListener(v -> { mPresenter.reverseRoute(); });


        mGestureDetector = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapUp(MotionEvent motionEvent) {
                gestureBox.setText("onSingleTap");
                return false;
            }

            @Override
            public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
                gestureBox.setText("onScroll");
                return false;
            }

            @Override
            public boolean onFling(MotionEvent downEvent, MotionEvent moveEvent, float velocityX, float velocityY) {
                gestureBox.setText("onFling");
                return false;
            }

            @Override
            public boolean onSingleTapConfirmed(MotionEvent event) {
                gestureBox.setText("onSingleTapConfirmed");
                mPresenter.nextStep();
                return true;
            }

            @Override
            public void onLongPress(MotionEvent arg0) {
                gestureBox.setText("onLongPress");
                mPresenter.addLandmark();
            }
        });

        root.setOnTouchListener((v,e) -> {
            if(mGestureDetector.onTouchEvent(e)) {
                return true;
            }
            return true;
        });

        return root;
    }


    @Override
    public void setStepCount(int stepCount) {
        viewStepCount.setText(Integer.toString(stepCount));
    }
}
