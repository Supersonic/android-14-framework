package android.companion.virtual.camera;

import android.hardware.camera2.CameraCharacteristics;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.Executor;
/* loaded from: classes.dex */
public final class VirtualCameraDevice implements AutoCloseable {
    private final CameraCharacteristics mCameraCharacteristics;
    private final String mCameraDeviceName;
    private final VirtualCameraOutput mCameraOutput;
    private boolean mCameraRegistered = false;

    public VirtualCameraDevice(int virtualDeviceId, String cameraName, CameraCharacteristics characteristics, VirtualCameraInput virtualCameraInput, Executor executor) {
        Objects.requireNonNull(cameraName);
        this.mCameraCharacteristics = (CameraCharacteristics) Objects.requireNonNull(characteristics);
        this.mCameraDeviceName = generateCameraDeviceName(virtualDeviceId, cameraName);
        this.mCameraOutput = new VirtualCameraOutput(virtualCameraInput, executor);
        registerCamera();
    }

    private static String generateCameraDeviceName(int deviceId, String cameraName) {
        return String.format(Locale.ENGLISH, "%d_%s", Integer.valueOf(deviceId), Objects.requireNonNull(cameraName));
    }

    @Override // java.lang.AutoCloseable
    public void close() {
        if (!this.mCameraRegistered) {
            return;
        }
        this.mCameraOutput.closeStream();
    }

    private void registerCamera() {
        if (this.mCameraRegistered) {
            return;
        }
        this.mCameraRegistered = true;
    }
}
