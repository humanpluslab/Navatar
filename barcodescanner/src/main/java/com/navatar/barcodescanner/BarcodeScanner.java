package com.navatar.barcodescanner;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.ImageFormat;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.ImageReader;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.util.Size;
import android.view.Surface;

import com.google.zxing.DecodeHintType;
import com.journeyapps.barcodescanner.Decoder;
import com.journeyapps.barcodescanner.DecoderFactory;
import com.journeyapps.barcodescanner.DecoderResultPointCallback;
import com.journeyapps.barcodescanner.DefaultDecoderFactory;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.TreeMap;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.subjects.PublishSubject;

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class BarcodeScanner extends ScannerService {

    private static final String TAG = BarcodeScanner.class.getSimpleName();

    private DecoderFactory mDecoderFactory;

    private CameraDevice cameraDevice;
    private ImageReader imageReader;
    /***
     * camera ids queue.
     */
    private Queue<String> cameraIds;

    private String currentCameraId;
    private boolean cameraClosed;


    private PublishSubject<String> mSource = PublishSubject.create();

    /***
     * private constructor, meant to force the use of {@link #getInstance}  method
     */
    private BarcodeScanner(Context context) {
        super(context);
    }

    /**
     * @param context the Application Context used to get the app's display manager
     * @return a new instance
     */
    public static ScannerService getInstance(Context context) {
        return new BarcodeScanner(context);
    }

    /**
     * Set the DecoderFactory to use. Use this to specify the formats to decode.
     *
     * Call this from UI thread only.
     *
     * @param decoderFactory the DecoderFactory creating Decoders.
     * @see DefaultDecoderFactory
     */
    public void setDecoderFactory(DecoderFactory decoderFactory) {
        mDecoderFactory = decoderFactory;
    }

    private Decoder createDecoder() {
        if (mDecoderFactory == null) {
            mDecoderFactory = createDefaultDecoderFactory();
        }
        DecoderResultPointCallback callback = new DecoderResultPointCallback();
        Map<DecodeHintType, Object> hints = new HashMap<>();
        hints.put(DecodeHintType.NEED_RESULT_POINT_CALLBACK, callback);
        Decoder decoder = this.mDecoderFactory.createDecoder(hints);
        callback.setDecoder(decoder);
        return decoder;
    }

    protected DecoderFactory createDefaultDecoderFactory() {
        return new DefaultDecoderFactory();
    }

    /**
     *
     * @return the current DecoderFactory in use.
     */
    public DecoderFactory getDecoderFactory() {
        return mDecoderFactory;
    }

    /**
     * Starts capturing images and returning any scanned barcodes as Strings.
     * @return a Flowable of String data
     */
    @Override
    public Flowable<String> startCapturing() {



        this.cameraIds = new LinkedList<>();
        try {
            final String[] cameraIds = manager.getCameraIdList();
            if (cameraIds.length > 0) {
                this.cameraIds.addAll(Arrays.asList(cameraIds));
                this.currentCameraId = this.cameraIds.poll();
                openCamera();
            } else {
                //No camera detected!
                //capturingListener.onDoneCapturingAllPhotos(picturesTaken);
            }
        } catch (final CameraAccessException e) {
            Log.e(TAG, "Exception occurred while accessing the list of cameras", e);
        }

        return mSource.toFlowable(BackpressureStrategy.LATEST);
    }

    @SuppressWarnings({"MissingPermission"})
    private void openCamera() {
        Log.d(TAG, "opening camera " + currentCameraId);
        try {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
                    == PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(context,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                manager.openCamera(currentCameraId, stateCallback, null);
            }
        } catch (final CameraAccessException e) {
            Log.e(TAG, " exception occurred while opening camera " + currentCameraId, e);
        }
    }

    private final CameraCaptureSession.CaptureCallback captureListener = new CameraCaptureSession.CaptureCallback() {
        @Override
        public void onCaptureCompleted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request,
                                       @NonNull TotalCaptureResult result) {
            super.onCaptureCompleted(session, request, result);
                //capturingListener.onCaptureDone(picturesTaken.lastEntry().getKey(), picturesTaken.lastEntry().getValue());
            closeCamera();
        }
    };



    private final CameraDevice.StateCallback stateCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(@NonNull CameraDevice camera) {
            cameraClosed = false;
            cameraDevice = camera;
            new Handler().postDelayed(() -> {
                try {
                    takePicture();
                } catch (final CameraAccessException e) {

                }
            }, 500);
        }

        @Override
        public void onDisconnected(@NonNull CameraDevice camera) {
            if (cameraDevice != null && !cameraClosed) {
                cameraClosed = true;
                cameraDevice.close();
            }
        }

        @Override
        public void onClosed(@NonNull CameraDevice camera) {
            cameraClosed = true;
            if (!cameraIds.isEmpty()) {

            } else {
                //capturingListener.onDoneCapturingAllPhotos(picturesTaken);
            }
        }


        @Override
        public void onError(@NonNull CameraDevice camera, int error) {
            Log.e(TAG, "camera in error, int code " + error);
            if (cameraDevice != null && !cameraClosed) {
                cameraDevice.close();
            }
        }
    };


    private void takePicture() throws CameraAccessException {

        if (null == cameraDevice) {
            Log.e(TAG, "cameraDevice is null");
            return;
        }

        final CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraDevice.getId());

        Size[] jpegSizes = null;

        StreamConfigurationMap streamConfigurationMap = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);

        if (streamConfigurationMap != null) {
            jpegSizes = streamConfigurationMap.getOutputSizes(ImageFormat.JPEG);
        }

        final boolean jpegSizesNotEmpty = jpegSizes != null && 0 < jpegSizes.length;
        int width = jpegSizesNotEmpty ? jpegSizes[0].getWidth() : 640;
        int height = jpegSizesNotEmpty ? jpegSizes[0].getHeight() : 480;

        final ImageReader reader = ImageReader.newInstance(width, height, ImageFormat.JPEG, 1);

        final List<Surface> outputSurfaces = new ArrayList<>();
        outputSurfaces.add(reader.getSurface());

        final CaptureRequest.Builder captureBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
        captureBuilder.addTarget(reader.getSurface());
        captureBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);
        captureBuilder.set(CaptureRequest.JPEG_ORIENTATION, getOrientation());

        // reader.setOnImageAvailableListener(onImageAvailableListener, null);

        cameraDevice.createCaptureSession(outputSurfaces, new CameraCaptureSession.StateCallback() {
                    @Override
                    public void onConfigured(@NonNull CameraCaptureSession session) {
                        try {
                            session.capture(captureBuilder.build(), captureListener, null);
                        } catch (final CameraAccessException e) {

                        }
                    }

                    @Override
                    public void onConfigureFailed(@NonNull CameraCaptureSession session) { }
                }, null);
    }

    private void closeCamera() {
        Log.d(TAG, "closing camera " + cameraDevice.getId());
        if (null != cameraDevice && !cameraClosed) {
            cameraDevice.close();
            cameraDevice = null;
        }
        if (null != imageReader) {
            imageReader.close();
            imageReader = null;
        }
    }


}
