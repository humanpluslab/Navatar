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
import com.google.zxing.client.android.DecodeFormatManager;
import com.google.zxing.client.android.Intents;
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
import java.util.Set;
import java.util.regex.Pattern;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.subjects.PublishSubject;

public class QRCodeScanner implements LocationProvider {

    private static final String TAG = QRCodeScanner.class.getSimpleName();

    private String lastText;

    private BarcodeView barcodeView;


    PublishSubject<Location> source = PublishSubject.create();

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

            if(lastText.startsWith("geo:")) {
                String[] geoText = lastText.substring(4).split(",");
                if (geoText.length > 2) {
                    source.onNext(Location.create(Double.parseDouble(geoText[0]), Double.parseDouble(geoText[1])));
                }
            }

            Log.e(TAG, lastText);
        }

        @Override
        public void possibleResultPoints(List<ResultPoint> resultPoints) {
        }
    };

    @NonNull
    @Override
    public Observable<Location> getLocationUpdates() {
        barcodeView.decodeContinuous(callback);
        barcodeView.resume();
        return source;
    }
}