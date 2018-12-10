package com.navatar.navigation;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.journeyapps.barcodescanner.DecoratedBarcodeView;
import com.navatar.R;
import com.navatar.di.ActivityScoped;
import com.navatar.location.details.QRCodeScanner;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import dagger.android.support.DaggerFragment;

@ActivityScoped
public class NavigationFragment extends DaggerFragment implements NavigationContract.View {

    private final static String TAG = NavigationFragment.class.getSimpleName();

    @BindView(R.id.viewStepCount)
    EditText viewStepCount;

    @BindView(R.id.viewDirection)
    TextView viewDirection;

    @BindView(R.id.reverseRouteButton)
    Button reverseRouteButton;

    @BindView(R.id.barcode_scanner)
    DecoratedBarcodeView barcodeView;

    @Inject
    NavigationContract.Presenter mPresenter;

    @Inject
    QRCodeScanner qrCodeScanner;

    private GestureDetector mGestureDetector;

    @Inject
    public NavigationFragment() { }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.takeView(this);
        barcodeView.resume();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mPresenter.dropView();
    }

    @Override
    public void onPause() {
        super.onPause();
        mPresenter.dropView();
        barcodeView.pause();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.navigation_fragment, container, false);

        ButterKnife.bind(this, root);

        reverseRouteButton.setOnClickListener(v -> {
            mPresenter.reverseRoute();
        });

        qrCodeScanner.setView(barcodeView, getActivity().getIntent());

        mGestureDetector = getGestureDetector();

        root.setOnTouchListener((v, e) -> {
            if (mGestureDetector.onTouchEvent(e)) {
                v.performClick();
                return true;
            }
            return false;
        });

        return root;
    }


    private GestureDetector getGestureDetector() {
        return new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener() {

            @Override
            public boolean onDown(MotionEvent e) {
                return true;
            }

            @Override
            public boolean onSingleTapUp(MotionEvent motionEvent) {
                Log.i(TAG,"onSingleTap");
                return false;
            }

            @Override
            public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
                Log.i(TAG,"onScroll");
                return false;
            }

            @Override
            public boolean onFling(MotionEvent downEvent, MotionEvent moveEvent, float velocityX, float velocityY) {
                Log.i(TAG,"onFling");
                return false;
            }

            @Override
            public boolean onSingleTapConfirmed(MotionEvent event) {
                Log.i(TAG,"onSingleTapConfirmed");
                mPresenter.nextStep();
                return true;
            }

            @Override
            public void onLongPress(MotionEvent arg0) {
                Log.i(TAG, "onLongPress");
                mPresenter.addLandmark();
            }
        });
    }

    @Override
    public void setStepCount(int stepCount) {
        viewStepCount.setText(Integer.toString(stepCount));
    }
}
