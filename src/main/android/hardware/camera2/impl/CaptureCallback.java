package android.hardware.camera2.impl;

import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CaptureFailure;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureResult;
import android.hardware.camera2.TotalCaptureResult;
import android.view.Surface;
import java.util.concurrent.Executor;
/* loaded from: classes.dex */
public abstract class CaptureCallback {
    private CameraCaptureSession.CaptureCallback mCallback;
    private Executor mExecutor;

    public abstract void onCaptureBufferLost(CameraDevice cameraDevice, CaptureRequest captureRequest, Surface surface, long j);

    public abstract void onCaptureCompleted(CameraDevice cameraDevice, CaptureRequest captureRequest, TotalCaptureResult totalCaptureResult);

    public abstract void onCaptureFailed(CameraDevice cameraDevice, CaptureRequest captureRequest, CaptureFailure captureFailure);

    public abstract void onCapturePartial(CameraDevice cameraDevice, CaptureRequest captureRequest, CaptureResult captureResult);

    public abstract void onCaptureProgressed(CameraDevice cameraDevice, CaptureRequest captureRequest, CaptureResult captureResult);

    public abstract void onCaptureSequenceAborted(CameraDevice cameraDevice, int i);

    public abstract void onCaptureSequenceCompleted(CameraDevice cameraDevice, int i, long j);

    public abstract void onCaptureStarted(CameraDevice cameraDevice, CaptureRequest captureRequest, long j, long j2);

    public abstract void onReadoutStarted(CameraDevice cameraDevice, CaptureRequest captureRequest, long j, long j2);

    public CaptureCallback(Executor executor, CameraCaptureSession.CaptureCallback callback) {
        this.mExecutor = executor;
        this.mCallback = callback;
    }

    public Executor getExecutor() {
        return this.mExecutor;
    }

    public CameraCaptureSession.CaptureCallback getSessionCallback() {
        return this.mCallback;
    }
}
