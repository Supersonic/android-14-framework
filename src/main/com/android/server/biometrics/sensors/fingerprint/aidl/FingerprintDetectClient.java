package com.android.server.biometrics.sensors.fingerprint.aidl;

import android.content.Context;
import android.hardware.biometrics.common.ICancellationSignal;
import android.hardware.fingerprint.FingerprintAuthenticateOptions;
import android.hardware.fingerprint.IUdfpsOverlay;
import android.hardware.fingerprint.IUdfpsOverlayController;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Slog;
import com.android.server.biometrics.log.BiometricContext;
import com.android.server.biometrics.log.BiometricLogger;
import com.android.server.biometrics.sensors.AcquisitionClient;
import com.android.server.biometrics.sensors.ClientMonitorCallback;
import com.android.server.biometrics.sensors.ClientMonitorCallbackConverter;
import com.android.server.biometrics.sensors.DetectionConsumer;
import com.android.server.biometrics.sensors.SensorOverlays;
import java.util.function.Supplier;
/* loaded from: classes.dex */
public class FingerprintDetectClient extends AcquisitionClient<AidlSession> implements DetectionConsumer {
    public ICancellationSignal mCancellationSignal;
    public final boolean mIsStrongBiometric;
    public final FingerprintAuthenticateOptions mOptions;
    public final SensorOverlays mSensorOverlays;

    @Override // com.android.server.biometrics.sensors.BaseClientMonitor
    public int getProtoEnum() {
        return 13;
    }

    @Override // com.android.server.biometrics.sensors.BaseClientMonitor
    public boolean interruptsPrecedingClients() {
        return true;
    }

    public FingerprintDetectClient(Context context, Supplier<AidlSession> supplier, IBinder iBinder, long j, ClientMonitorCallbackConverter clientMonitorCallbackConverter, FingerprintAuthenticateOptions fingerprintAuthenticateOptions, BiometricLogger biometricLogger, BiometricContext biometricContext, IUdfpsOverlayController iUdfpsOverlayController, IUdfpsOverlay iUdfpsOverlay, boolean z) {
        super(context, supplier, iBinder, clientMonitorCallbackConverter, fingerprintAuthenticateOptions.getUserId(), fingerprintAuthenticateOptions.getOpPackageName(), 0, fingerprintAuthenticateOptions.getSensorId(), true, biometricLogger, biometricContext);
        setRequestId(j);
        this.mIsStrongBiometric = z;
        this.mSensorOverlays = new SensorOverlays(iUdfpsOverlayController, null, iUdfpsOverlay);
        this.mOptions = fingerprintAuthenticateOptions;
    }

    @Override // com.android.server.biometrics.sensors.BaseClientMonitor
    public void start(ClientMonitorCallback clientMonitorCallback) {
        super.start(clientMonitorCallback);
        startHalOperation();
    }

    @Override // com.android.server.biometrics.sensors.AcquisitionClient
    public void stopHalOperation() {
        this.mSensorOverlays.hide(getSensorId());
        try {
            this.mCancellationSignal.cancel();
        } catch (RemoteException e) {
            Slog.e("FingerprintDetectClient", "Remote exception", e);
            this.mCallback.onClientFinished(this, false);
        }
    }

    @Override // com.android.server.biometrics.sensors.HalClientMonitor
    public void startHalOperation() {
        this.mSensorOverlays.show(getSensorId(), 4, this);
        try {
            this.mCancellationSignal = doDetectInteraction();
        } catch (RemoteException e) {
            Slog.e("FingerprintDetectClient", "Remote exception when requesting finger detect", e);
            this.mSensorOverlays.hide(getSensorId());
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
            Slog.e("FingerprintDetectClient", "Remote exception when sending onDetected", e);
            this.mCallback.onClientFinished(this, false);
        }
    }
}
