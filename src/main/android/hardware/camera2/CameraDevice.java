package android.hardware.camera2;

import android.annotation.SystemApi;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.ExtensionSessionConfiguration;
import android.hardware.camera2.params.InputConfiguration;
import android.hardware.camera2.params.OutputConfiguration;
import android.hardware.camera2.params.SessionConfiguration;
import android.p008os.Handler;
import android.view.Surface;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;
import java.util.Set;
/* loaded from: classes.dex */
public abstract class CameraDevice implements AutoCloseable {
    public static final int AUDIO_RESTRICTION_NONE = 0;
    public static final int AUDIO_RESTRICTION_VIBRATION = 1;
    public static final int AUDIO_RESTRICTION_VIBRATION_SOUND = 3;
    @SystemApi
    public static final int SESSION_OPERATION_MODE_CONSTRAINED_HIGH_SPEED = 1;
    @SystemApi
    public static final int SESSION_OPERATION_MODE_NORMAL = 0;
    @SystemApi
    public static final int SESSION_OPERATION_MODE_VENDOR_START = 32768;
    public static final int TEMPLATE_MANUAL = 6;
    public static final int TEMPLATE_PREVIEW = 1;
    public static final int TEMPLATE_RECORD = 3;
    public static final int TEMPLATE_STILL_CAPTURE = 2;
    public static final int TEMPLATE_VIDEO_SNAPSHOT = 4;
    public static final int TEMPLATE_ZERO_SHUTTER_LAG = 5;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    public @interface CAMERA_AUDIO_RESTRICTION {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    public @interface RequestTemplate {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    public @interface SessionOperatingMode {
    }

    @Override // java.lang.AutoCloseable
    public abstract void close();

    public abstract CaptureRequest.Builder createCaptureRequest(int i) throws CameraAccessException;

    @Deprecated
    public abstract void createCaptureSession(List<Surface> list, CameraCaptureSession.StateCallback stateCallback, Handler handler) throws CameraAccessException;

    @Deprecated
    public abstract void createCaptureSessionByOutputConfigurations(List<OutputConfiguration> list, CameraCaptureSession.StateCallback stateCallback, Handler handler) throws CameraAccessException;

    @Deprecated
    public abstract void createConstrainedHighSpeedCaptureSession(List<Surface> list, CameraCaptureSession.StateCallback stateCallback, Handler handler) throws CameraAccessException;

    @SystemApi
    @Deprecated
    public abstract void createCustomCaptureSession(InputConfiguration inputConfiguration, List<OutputConfiguration> list, int i, CameraCaptureSession.StateCallback stateCallback, Handler handler) throws CameraAccessException;

    public abstract CaptureRequest.Builder createReprocessCaptureRequest(TotalCaptureResult totalCaptureResult) throws CameraAccessException;

    @Deprecated
    public abstract void createReprocessableCaptureSession(InputConfiguration inputConfiguration, List<Surface> list, CameraCaptureSession.StateCallback stateCallback, Handler handler) throws CameraAccessException;

    @Deprecated
    public abstract void createReprocessableCaptureSessionByConfigurations(InputConfiguration inputConfiguration, List<OutputConfiguration> list, CameraCaptureSession.StateCallback stateCallback, Handler handler) throws CameraAccessException;

    public abstract String getId();

    public void createExtensionSession(ExtensionSessionConfiguration extensionConfiguration) throws CameraAccessException {
        throw new UnsupportedOperationException("No default implementation");
    }

    public void createCaptureSession(SessionConfiguration config) throws CameraAccessException {
        throw new UnsupportedOperationException("No default implementation");
    }

    public CaptureRequest.Builder createCaptureRequest(int templateType, Set<String> physicalCameraIdSet) throws CameraAccessException {
        throw new UnsupportedOperationException("Subclasses must override this method");
    }

    public boolean isSessionConfigurationSupported(SessionConfiguration sessionConfig) throws CameraAccessException {
        throw new UnsupportedOperationException("Subclasses must override this method");
    }

    /* loaded from: classes.dex */
    public static abstract class StateCallback {
        public static final int ERROR_CAMERA_DEVICE = 4;
        public static final int ERROR_CAMERA_DISABLED = 3;
        public static final int ERROR_CAMERA_IN_USE = 1;
        public static final int ERROR_CAMERA_SERVICE = 5;
        public static final int ERROR_MAX_CAMERAS_IN_USE = 2;

        @Retention(RetentionPolicy.SOURCE)
        /* loaded from: classes.dex */
        public @interface ErrorCode {
        }

        public abstract void onDisconnected(CameraDevice cameraDevice);

        public abstract void onError(CameraDevice cameraDevice, int i);

        public abstract void onOpened(CameraDevice cameraDevice);

        public void onClosed(CameraDevice camera) {
        }
    }

    public void setCameraAudioRestriction(int mode) throws CameraAccessException {
        throw new UnsupportedOperationException("Subclasses must override this method");
    }

    public int getCameraAudioRestriction() throws CameraAccessException {
        throw new UnsupportedOperationException("Subclasses must override this method");
    }
}
