package com.navatar.barcodescanner;

import android.arch.lifecycle.Lifecycle;
import android.content.Context;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.media.Image;
import android.os.Handler;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import org.junit.Test;

import io.reactivex.observers.TestObserver;

public class CameraControllerTest {

    private static String ANY_CAMERA_ID = "0";
    private static int ANY_SOME_ERROR = 4;
    private static boolean ANY_BOOLEAN = false;

    @Mock
    CameraManager cameraManager;

    @Mock
    CameraDevice cameraDevice;

    @Mock
    Context context;

    @Mock
    Lifecycle lifecycle;

    ArgumentCaptor<CameraDevice.StateCallback> stateCallback = ArgumentCaptor.forClass(CameraDevice.StateCallback.class);
    ArgumentCaptor<CameraManager.AvailabilityCallback> availabilityCallback = ArgumentCaptor.forClass(CameraManager.AvailabilityCallback.class);
    ArgumentCaptor<CameraManager.TorchCallback> torchCallback = ArgumentCaptor.forClass(CameraManager.TorchCallback.class);


    @Before
    public void setup() throws CameraAccessException {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void openCamera() throws  CameraAccessException {


        CameraController controller = new CameraController(context, lifecycle);

        TestObserver<Image> imageTestObserver = controller.getImages().toObservable().test();

        imageTestObserver.assertNoErrors().dispose();

    }

}
