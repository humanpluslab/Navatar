package com.navatar.barcodescanner;

import com.navatar.barcodescanner.CameraRxWrapper.CaptureSessionData;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.ImageFormat;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.media.Image;
import android.media.ImageReader;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.util.Pair;
import android.util.Size;
import android.view.Surface;
import android.view.WindowManager;

import java.util.Arrays;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;


@TargetApi(21)
public class CameraController {

    static final String TAG = CameraController.class.getName();

    @NonNull
    private final Context mContext;
    @NonNull
    private final WindowManager mWindowManager;
    @NonNull
    private final CameraManager mCameraManager;

    private ImageReader mImageReader;

    private class CameraParams {
        @NonNull
        private final String cameraId;
        @NonNull
        private final CameraCharacteristics cameraCharacteristics;
        @NonNull
        private final Size previewSize;

        private CameraParams(@NonNull String cameraId, @NonNull CameraCharacteristics cameraCharacteristics, @NonNull Size previewSize) {
            this.cameraId = cameraId;
            this.cameraCharacteristics = cameraCharacteristics;
            this.previewSize = previewSize;
        }
    }

    private final CompositeDisposable mCompositeDisposable = new CompositeDisposable();

    private final PublishSubject<Object> mOnPauseSubject = PublishSubject.create();
    private final ConvergeWaiter mAutoFocusConvergeWaiter = ConvergeWaiter.Factory.createAutoFocusConvergeWaiter();
    private final ConvergeWaiter mAutoExposureConvergeWaiter = ConvergeWaiter.Factory.createAutoExposureConvergeWaiter();

    private final PublishSubject<Image> mImageSubject = PublishSubject.create();
    private final PublishSubject<Surface> mSurfaceSubject = PublishSubject.create();
    private final PublishSubject<Pair<CameraRxWrapper.DeviceStateEvents, CameraDevice>> mCameraSubject = PublishSubject.create();

    private CameraParams mCameraParams;

    private Surface mSurface;

    public CameraController(@NonNull Context context, @NonNull ImageReader imageReader) {
        this(context);
        mImageReader = imageReader;
        mSurface = imageReader.getSurface();
    }

    public CameraController(@NonNull Context context) {
        mContext = context;
        mWindowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        mCameraManager = (CameraManager) mContext.getSystemService(Context.CAMERA_SERVICE);
    }

    public Flowable<Image> getImages() {
        return mImageSubject.toFlowable(BackpressureStrategy.LATEST);
    }

    public Observable<Pair<CameraRxWrapper.DeviceStateEvents, CameraDevice>> openCamera() {

        try {
            String cameraId = CameraStrategy.chooseDefaultCamera(mCameraManager);
            if (cameraId == null) {
                mCameraSubject.onError(new IllegalStateException("Can't find any cameras"));
            } else {
                mCameraParams = getCameraParams(cameraId);
                subscribe();
                initImageReader();
                mSurfaceSubject.onNext(mSurface);
            }
        } catch (CameraAccessException err) {
            mCameraSubject.onError(err);
        }

        return mCameraSubject;
    }

    private void initImageReader() {
        if (mImageReader == null) {
            Size sizeForImageReader = CameraStrategy.getStillImageSize(mCameraParams.cameraCharacteristics, mCameraParams.previewSize);
            mImageReader = ImageReader.newInstance(sizeForImageReader.getWidth(), sizeForImageReader.getHeight(), ImageFormat.JPEG, 1);
            mSurface = mImageReader.getSurface();
        }
        mCompositeDisposable.add(
                ImageSaverRxWrapper.createOnImageAvailableObservable(mImageReader)
                        .observeOn(Schedulers.io())
                        .subscribe(this::fromImageReader)
        );
    }


    private CameraParams getCameraParams(@NonNull String cameraId) throws CameraAccessException {
        CameraCharacteristics cameraCharacteristics = mCameraManager.getCameraCharacteristics(cameraId);
        Size previewSize = CameraStrategy.getPreviewSize(cameraCharacteristics);
        return new CameraParams(cameraId, cameraCharacteristics, previewSize);
    }

    private void fromImageReader(ImageReader reader) {
        mImageSubject.onNext(reader.acquireLatestImage());
    }


    /**
     * Flow is configured in this method
     */
    private void subscribe() {
        mCompositeDisposable.clear();

        // open camera

        Observable<Pair<CameraRxWrapper.DeviceStateEvents, CameraDevice>> cameraDeviceObservable = mSurfaceSubject
            .firstElement()
            .toObservable()
            .flatMap(__ -> CameraRxWrapper.openCamera(mCameraParams.cameraId, mCameraManager))
            .share();

        Observable<CameraDevice> openCameraObservable = mCameraSubject
            .filter(pair -> pair.first == CameraRxWrapper.DeviceStateEvents.ON_OPENED)
            .map(pair -> pair.second)
            .share();

        Observable<CameraDevice> closeCameraObservable = cameraDeviceObservable
            .filter(pair -> pair.first == CameraRxWrapper.DeviceStateEvents.ON_CLOSED)
            .map(pair -> pair.second)
            .share();

        // create capture session

        Observable<Pair<CameraRxWrapper.CaptureSessionStateEvents, CameraCaptureSession>> createCaptureSessionObservable = openCameraObservable
            .flatMap(cameraDevice -> CameraRxWrapper
                .createCaptureSession(cameraDevice, Arrays.asList(mSurface))
            )
            .share();

        Observable<CameraCaptureSession> captureSessionConfiguredObservable = createCaptureSessionObservable
            .filter(pair -> pair.first == CameraRxWrapper.CaptureSessionStateEvents.ON_CONFIGURED)
            .map(pair -> pair.second)
            .share();

        Observable<CameraCaptureSession> captureSessionClosedObservable = createCaptureSessionObservable
            .filter(pair -> pair.first == CameraRxWrapper.CaptureSessionStateEvents.ON_CLOSED)
            .map(pair -> pair.second)
            .share();

        // start preview

        Observable<CaptureSessionData> previewObservable = captureSessionConfiguredObservable
            .flatMap(cameraCaptureSession -> {
                CaptureRequest.Builder previewBuilder = createPreviewBuilder(cameraCaptureSession, mSurface);
                return CameraRxWrapper.fromSetRepeatingRequest(cameraCaptureSession, previewBuilder.build());
            })
            .share();

        // react to onPause event

        mCompositeDisposable.add(Observable.combineLatest(previewObservable, mOnPauseSubject, (state, o) -> state)
            .firstElement().toObservable()
            .doOnNext(captureSessionData ->{
                captureSessionData.session.stopRepeating();
                captureSessionData.session.abortCaptures();
                captureSessionData.session.close();
            } )
            .flatMap(__ -> captureSessionClosedObservable)
            .doOnNext(cameraCaptureSession -> cameraCaptureSession.getDevice().close())
            .flatMap(__ -> closeCameraObservable)
            .doOnNext(__ -> closeImageReader())
            .subscribe(__ -> unsubscribe(), this::onError)
        );
    }

    private void onError(Throwable throwable) {
        unsubscribe();
        if (throwable instanceof CameraAccessException) {
            //mCallback.onCameraAccessException();
        }
        else if (throwable instanceof OpenCameraException) {
            //mCallback.onCameraOpenException(((OpenCameraException) throwable).getReason());
        }
        else {
            //mCallback.onException(throwable);
        }
    }

    private void unsubscribe() {
        mCompositeDisposable.clear();
    }


    @Nullable
    private Integer getLensFacingPhotoType() {
        return mCameraParams.cameraCharacteristics.get(CameraCharacteristics.LENS_FACING);
    }

    private static boolean contains(int[] modes, int mode) {
        if (modes == null) {
            return false;
        }
        for (int i : modes) {
            if (i == mode) {
                return true;
            }
        }
        return false;
    }

    private Observable<CaptureSessionData> waitForAf(@NonNull CaptureSessionData captureResultParams) {
        return Observable
            .fromCallable(() -> createPreviewBuilder(captureResultParams.session, mSurface))
            .flatMap(
                previewBuilder -> mAutoFocusConvergeWaiter
                    .waitForConverge(captureResultParams, previewBuilder)
                    .toObservable()
            );
    }

    @NonNull
    private Observable<CaptureSessionData> waitForAe(@NonNull CaptureSessionData captureResultParams) {
        return Observable
            .fromCallable(() -> createPreviewBuilder(captureResultParams.session, mSurface))
            .flatMap(
                previewBuilder -> mAutoExposureConvergeWaiter
                    .waitForConverge(captureResultParams, previewBuilder)
                    .toObservable()
            );
    }

    @NonNull
    private Observable<CaptureSessionData> captureStillPicture(@NonNull CameraCaptureSession cameraCaptureSession) {
        Log.d(TAG, "\tcaptureStillPicture");
        return Observable
            .fromCallable(() -> createStillPictureBuilder(cameraCaptureSession.getDevice()))
            .flatMap(builder -> CameraRxWrapper.fromCapture(cameraCaptureSession, builder.build()));
    }

    @NonNull
    private CaptureRequest.Builder createStillPictureBuilder(@NonNull CameraDevice cameraDevice) throws CameraAccessException {
        final CaptureRequest.Builder builder;
        builder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
        builder.set(CaptureRequest.CONTROL_CAPTURE_INTENT, CaptureRequest.CONTROL_CAPTURE_INTENT_STILL_CAPTURE);
        builder.set(CaptureRequest.CONTROL_AE_PRECAPTURE_TRIGGER, CameraMetadata.CONTROL_AE_PRECAPTURE_TRIGGER_IDLE);
        builder.addTarget(mImageReader.getSurface());
        setup3Auto(builder);

        int rotation = mWindowManager.getDefaultDisplay().getRotation();
        builder.set(CaptureRequest.JPEG_ORIENTATION, CameraOrientationHelper.getJpegOrientation(mCameraParams.cameraCharacteristics, rotation));
        return builder;
    }

    @NonNull
    CaptureRequest.Builder createPreviewBuilder(CameraCaptureSession captureSession, Surface previewSurface) throws CameraAccessException {
        CaptureRequest.Builder builder = captureSession.getDevice().createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
        builder.addTarget(previewSurface);
        setup3Auto(builder);
        return builder;
    }

    private void setup3Auto(CaptureRequest.Builder builder) {
        // Enable auto-magical 3A run by camera device
        builder.set(CaptureRequest.CONTROL_MODE, CaptureRequest.CONTROL_MODE_AUTO);

        Float minFocusDist = mCameraParams.cameraCharacteristics.get(CameraCharacteristics.LENS_INFO_MINIMUM_FOCUS_DISTANCE);

        // If MINIMUM_FOCUS_DISTANCE is 0, lens is fixed-focus and we need to skip the AF run.
        boolean noAFRun = (minFocusDist == null || minFocusDist == 0);

        if (!noAFRun) {
            // If there is a "continuous picture" mode available, use it, otherwise default to AUTO.
            int[] afModes = mCameraParams.cameraCharacteristics.get(CameraCharacteristics.CONTROL_AF_AVAILABLE_MODES);
            if (contains(afModes, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE)) {
                builder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
            }
            else {
                builder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_AUTO);
            }
        }

        // If there is an auto-magical flash control mode available, use it, otherwise default to
        // the "on" mode, which is guaranteed to always be available.
        int[] aeModes = mCameraParams.cameraCharacteristics.get(CameraCharacteristics.CONTROL_AE_AVAILABLE_MODES);
        if (contains(aeModes, CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH)) {
            builder.set(CaptureRequest.CONTROL_AE_MODE, CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH);
        }
        else {
            builder.set(CaptureRequest.CONTROL_AE_MODE, CaptureRequest.CONTROL_AE_MODE_ON);
        }

        // If there is an auto-magical white balance control mode available, use it.
        int[] awbModes = mCameraParams.cameraCharacteristics.get(CameraCharacteristics.CONTROL_AWB_AVAILABLE_MODES);
        if (contains(awbModes, CaptureRequest.CONTROL_AWB_MODE_AUTO)) {
            // Allow AWB to run auto-magically if this device supports this
            builder.set(CaptureRequest.CONTROL_AWB_MODE, CaptureRequest.CONTROL_AWB_MODE_AUTO);
        }
    }

    private void closeImageReader() {
        if (mImageReader != null) {
            mImageReader.close();
            mImageReader = null;
        }
    }

}
