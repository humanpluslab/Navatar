package com.navatar.location.details;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.zxing.ResultPoint;

import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.navatar.barcodescanner.BarcodeScanner;
import com.navatar.barcodescanner.ScannerService;
import com.navatar.location.LocationProvider;
import com.navatar.location.model.Location;


import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.subjects.PublishSubject;

public class QRCodeScanner implements LocationProvider {

    private static final String TAG = QRCodeScanner.class.getSimpleName();

    private String lastText;

    private ScannerService mScannerService;

    private final CompositeDisposable disposables = new CompositeDisposable();

    PublishSubject<Location> mLocationSource = PublishSubject.create();

    @Inject
    public QRCodeScanner(Context context) {
        mScannerService = BarcodeScanner.getInstance(context);
    }

    public void startCapturing() {

        disposables.add(mScannerService
            .startCapturing()
            .subscribe(
                this::process
            ));

    }

    private void process(String data) {
        if(data.equals(lastText)) // Prevent duplicate scans
            return;
        lastText = data;
        if(lastText.startsWith("geo:")) {
            String[] geoText = lastText.substring(4).split(",");
            if (geoText.length > 2) {
                mLocationSource.onNext(Location.create(Double.parseDouble(geoText[0]), Double.parseDouble(geoText[1])));
            }
        }
    }


    @NonNull
    @Override
    public Observable<Location> getLocationUpdates() {
        return mLocationSource;
    }
}