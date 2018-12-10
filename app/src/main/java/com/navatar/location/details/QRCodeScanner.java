package com.navatar.location.details;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.ResultPoint;

import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;
import com.journeyapps.barcodescanner.DefaultDecoderFactory;
import com.navatar.location.LocationProvider;
import com.navatar.location.model.Location;


import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.subjects.PublishSubject;

@Singleton
public class QRCodeScanner implements LocationProvider {

    private static final String TAG = QRCodeScanner.class.getSimpleName();

    private String lastText;

    private DecoratedBarcodeView mBarcodeView;

    private DefaultDecoderFactory defaultDecoderFactory;

    private final CompositeDisposable disposables = new CompositeDisposable();

    PublishSubject<Location> mLocationSource = PublishSubject.create();

    @Inject
    public QRCodeScanner() {
        Collection<BarcodeFormat> formats = Arrays.asList(BarcodeFormat.QR_CODE, BarcodeFormat.CODE_39);
        defaultDecoderFactory = new DefaultDecoderFactory(formats);
    }

    private BarcodeCallback callback = new BarcodeCallback() {
        @Override
        public void barcodeResult(BarcodeResult result) {
            if(result.getText() == null || result.getText().equals(lastText)) {
                // Prevent duplicate scans
                return;
            }
            process(result.getText());
            mBarcodeView.setStatusText(result.getText());
        }

        @Override
        public void possibleResultPoints(List<ResultPoint> resultPoints) { }
    };

    public void setView(DecoratedBarcodeView barcodeView, Intent intent) {
        mBarcodeView = barcodeView;
        mBarcodeView.getBarcodeView().setDecoderFactory(defaultDecoderFactory);
        mBarcodeView.initializeFromIntent(intent);
        barcodeView.decodeContinuous(callback);
    }

    private void process(String data) {
        lastText = data;
        if(lastText.startsWith("geo:")) {
            Pattern pattern = Pattern.compile("geo:([\\d.-]+),([\\d-.]+)[?]*(.*)");
            Matcher matcher = pattern.matcher(lastText);

            while (matcher.find()) {
                Location location = Location.create(Double.parseDouble(matcher.group(1)), Double.parseDouble(matcher.group(2)));
                mLocationSource.onNext(location);
            }
        } else if (lastText.startsWith("BEGIN:VCARD")) {

        } else if (lastText.startsWith("BEGIN:VEVENT")) {

        }
    }


    @NonNull
    @Override
    public Observable<Location> getLocationUpdates() {
        return mLocationSource;
    }
}