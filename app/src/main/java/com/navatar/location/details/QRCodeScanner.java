package com.navatar.location.details;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.ImageView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.DecodeHintType;
import com.google.zxing.ResultPoint;
import com.google.zxing.client.android.BeepManager;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.BarcodeView;
import com.journeyapps.barcodescanner.Decoder;
import com.journeyapps.barcodescanner.DecoderFactory;
import com.journeyapps.barcodescanner.DefaultDecoderFactory;
import com.navatar.location.LocationProvider;
import com.navatar.location.model.Location;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import io.reactivex.Single;

public class QRCodeScanner implements LocationProvider {

    private static final String TAG = QRCodeScanner.class.getSimpleName();

    private String lastText;

    private BarcodeView barcodeView;

    @Inject
    public QRCodeScanner(Context context) {
        barcodeView = new BarcodeView(context);
    }

    private BarcodeCallback callback = new BarcodeCallback() {
        @Override
        public void barcodeResult(BarcodeResult result) {
            if(result.getText() == null || result.getText().equals(lastText)) {
                // Prevent duplicate scans
                return;
            }
            lastText = result.getText();
            Log.e(TAG, lastText);
        }

        @Override
        public void possibleResultPoints(List<ResultPoint> resultPoints) {
        }
    };



    @Override
    @NonNull
    public Single<Location> getLocation() {
        return Single.create(
                emitter -> {
                    barcodeView.decodeContinuous(callback);
                    barcodeView.resume();
                    emitter.onSuccess(Location.create(69, 69));
                }
        );
    }
}