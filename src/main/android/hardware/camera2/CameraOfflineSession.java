package android.hardware.camera2;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
/* loaded from: classes.dex */
public abstract class CameraOfflineSession extends CameraCaptureSession {

    /* loaded from: classes.dex */
    public static abstract class CameraOfflineSessionCallback {
        public static final int STATUS_INTERNAL_ERROR = 0;

        @Retention(RetentionPolicy.SOURCE)
        /* loaded from: classes.dex */
        public @interface StatusCode {
        }

        public abstract void onClosed(CameraOfflineSession cameraOfflineSession);

        public abstract void onError(CameraOfflineSession cameraOfflineSession, int i);

        public abstract void onIdle(CameraOfflineSession cameraOfflineSession);

        public abstract void onReady(CameraOfflineSession cameraOfflineSession);

        public abstract void onSwitchFailed(CameraOfflineSession cameraOfflineSession);
    }

    @Override // android.hardware.camera2.CameraCaptureSession, java.lang.AutoCloseable
    public abstract void close();
}
