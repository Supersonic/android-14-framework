package com.android.server.biometrics.sensors.face.aidl;

import android.content.Context;
import android.hardware.SensorPrivacyManager;
import android.hardware.biometrics.common.ICancellationSignal;
import android.hardware.face.FaceAuthenticateOptions;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Slog;
import com.android.internal.annotations.VisibleForTesting;
import com.android.server.biometrics.log.BiometricContext;
import com.android.server.biometrics.log.BiometricLogger;
import com.android.server.biometrics.sensors.AcquisitionClient;
import com.android.server.biometrics.sensors.ClientMonitorCallback;
import com.android.server.biometrics.sensors.ClientMonitorCallbackConverter;
import com.android.server.biometrics.sensors.DetectionConsumer;
import java.util.function.Supplier;
/* loaded from: classes.dex */
public class FaceDetectClient extends AcquisitionClient<AidlSession> implements DetectionConsumer {
    public ICancellationSignal mCancellationSignal;
    public final boolean mIsStrongBiometric;
    public final FaceAuthenticateOptions mOptions;
    public SensorPrivacyManager mSensorPrivacyManager;

    @Override // com.android.server.biometrics.sensors.BaseClientMonitor
    public int getProtoEnum() {
        return 13;
    }

    @Override // com.android.server.biometrics.sensors.BaseClientMonitor
    public boolean interruptsPrecedingClients() {
        return true;
    }

    public FaceDetectClient(Context context, Supplier<AidlSession> supplier, IBinder iBinder, long j, ClientMonitorCallbackConverter clientMonitorCallbackConverter, FaceAuthenticateOptions faceAuthenticateOptions, BiometricLogger biometricLogger, BiometricContext biometricContext, boolean z) {
        this(context, supplier, iBinder, j, clientMonitorCallbackConverter, faceAuthenticateOptions, biometricLogger, biometricContext, z, (SensorPrivacyManager) context.getSystemService(SensorPrivacyManager.class));
    }

    @VisibleForTesting
    public FaceDetectClient(Context context, Supplier<AidlSession> supplier, IBinder iBinder, long j, ClientMonitorCallbackConverter clientMonitorCallbackConverter, FaceAuthenticateOptions faceAuthenticateOptions, BiometricLogger biometricLogger, BiometricContext biometricContext, boolean z, SensorPrivacyManager sensorPrivacyManager) {
        super(context, supplier, iBinder, clientMonitorCallbackConverter, faceAuthenticateOptions.getUserId(), faceAuthenticateOptions.getOpPackageName(), 0, faceAuthenticateOptions.getSensorId(), true, biometricLogger, biometricContext);
        setRequestId(j);
        this.mIsStrongBiometric = z;
        this.mSensorPrivacyManager = sensorPrivacyManager;
        this.mOptions = faceAuthenticateOptions;
    }

    @Override // com.android.server.biometrics.sensors.BaseClientMonitor
    public void start(ClientMonitorCallback clientMonitorCallback) {
        super.start(clientMonitorCallback);
        startHalOperation();
    }

    @Override // com.android.server.biometrics.sensors.AcquisitionClient
    public void stopHalOperation() {
        ICancellationSignal iCancellationSignal = this.mCancellationSignal;
        if (iCancellationSignal != null) {
            try {
                iCancellationSignal.cancel();
            } catch (RemoteException e) {
                Slog.e("FaceDetectClient", "Remote exception", e);
                this.mCallback.onClientFinished(this, false);
            }
        }
    }

    @Override // com.android.server.biometrics.sensors.HalClientMonitor
    public void startHalOperation() {
        SensorPrivacyManager sensorPrivacyManager = this.mSensorPrivacyManager;
        if (sensorPrivacyManager != null && sensorPrivacyManager.isSensorPrivacyEnabled(1, 2)) {
            onError(1, 0);
            this.mCallback.onClientFinished(this, false);
            return;
        }
        try {
            this.mCancellationSignal = doDetectInteraction();
        } catch (RemoteException e) {
            Slog.e("FaceDetectClient", "Remote exception when requesting face detect", e);
            this.mCallback.onClientFinished(this, false);
        }
    }

    public final ICancellationSignal doDetectInteraction() throws RemoteException {
        AidlSession freshDaemon = getFreshDaemon();
        if (freshDaemon.hasContextMethods()) {
            return freshDaemon.getSession().detectInteractionWithContext(getOperationContext().toAidlContext(this.mOptions));
        }
        return freshDaemon.getSession().detectInteraction();
    }

    public void onInteractionDetected() {
        vibrateSuccess();
        try {
            getListener().onDetected(getSensorId(), getTargetUserId(), this.mIsStrongBiometric);
            this.mCallback.onClientFinished(this, true);
        } catch (RemoteException e) {
            Slog.e("FaceDetectClient", "Remote exception when sending onDetected", e);
            this.mCallback.onClientFinished(this, false);
        }
    }
}
