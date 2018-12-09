package com.navatar.barcodescanner;

import android.content.Context;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.ImageReader;
import android.util.Pair;
import android.util.Size;
import android.view.Surface;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import io.reactivex.observers.TestObserver;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

public class CameraControllerTest {

    private static String ANY_CAMERA_ID = "0";
    private static int ANY_SOME_ERROR = 4;
    private static boolean ANY_BOOLEAN = false;

    @Mock
    CameraManager cameraManager;

    @Mock
    CameraDevice cameraDevice;

    @Mock
    CameraCharacteristics cameraCharacteristics;

    @Mock
    StreamConfigurationMap configurationMap;

    @Mock
    Size size;

    @Mock
    ImageReader imageReader;

    @Mock
    Surface surface;

    @Mock
    Context context;

    ArgumentCaptor<CameraDevice.StateCallback> stateCallback = ArgumentCaptor.forClass(CameraDevice.StateCallback.class);
    ArgumentCaptor<CameraManager.AvailabilityCallback> availabilityCallback = ArgumentCaptor.forClass(CameraManager.AvailabilityCallback.class);
    ArgumentCaptor<CameraManager.TorchCallback> torchCallback = ArgumentCaptor.forClass(CameraManager.TorchCallback.class);


    @Before
    public void setup() throws CameraAccessException {
        MockitoAnnotations.initMocks(this);

        when(context.getSystemService(Context.CAMERA_SERVICE)).thenReturn(cameraManager);
        when(cameraManager.getCameraIdList()).thenReturn(new String[]{"0"});
        when(cameraManager.getCameraCharacteristics(anyString())).thenReturn(cameraCharacteristics);
        when(cameraCharacteristics.get(anyObject())).thenReturn(configurationMap, 1, configurationMap);
        when(configurationMap.getOutputSizes(anyObject())).thenReturn(new Size[] { size });
        when(configurationMap.getOutputSizes(anyInt())).thenReturn(new Size[] { size });
        when(size.getHeight()).thenReturn(600);
        when(size.getWidth()).thenReturn(800);
        when(imageReader.getSurface()).thenReturn(surface);
    }

    @Test
    public void openCamera() {


        CameraController controller = new CameraController(context, imageReader);

        TestObserver<Pair<CameraRxWrapper.DeviceStateEvents, CameraDevice>> cameraTestObserver =
                controller.openCamera().test();

        cameraTestObserver.assertNoErrors().dispose();

    }

    @Test
    public void getImages() {

        CameraController controller = new CameraController(context, imageReader);

        TestObserver<Image> imageTestObserver =
                controller
                    .openCamera()
                    .filter(pair -> pair.first == CameraRxWrapper.DeviceStateEvents.ON_OPENED)
                    .flatMap(__ -> controller.getImages().toObservable())
                    .test();

        imageTestObserver.assertNoErrors().dispose();

    }



}
