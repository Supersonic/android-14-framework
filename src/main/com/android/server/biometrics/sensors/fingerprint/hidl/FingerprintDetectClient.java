package com.android.server.biometrics.sensors.fingerprint.hidl;

import android.content.Context;
import android.hardware.biometrics.BiometricAuthenticator;
import android.hardware.biometrics.fingerprint.PointerContext;
import android.hardware.biometrics.fingerprint.V2_1.IBiometricsFingerprint;
import android.hardware.fingerprint.FingerprintAuthenticateOptions;
import android.hardware.fingerprint.IUdfpsOverlay;
import android.hardware.fingerprint.IUdfpsOverlayController;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Slog;
import com.android.server.biometrics.log.BiometricContext;
import com.android.server.biometrics.log.BiometricLogger;
import com.android.server.biometrics.sensors.AcquisitionClient;
import com.android.server.biometrics.sensors.AuthenticationConsumer;
import com.android.server.biometrics.sensors.ClientMonitorCallback;
import com.android.server.biometrics.sensors.ClientMonitorCallbackConverter;
import com.android.server.biometrics.sensors.PerformanceTracker;
import com.android.server.biometrics.sensors.SensorOverlays;
import com.android.server.biometrics.sensors.fingerprint.Udfps;
import com.android.server.biometrics.sensors.fingerprint.UdfpsHelper;
import java.util.ArrayList;
import java.util.function.Supplier;
/* loaded from: classes.dex */
public class FingerprintDetectClient extends AcquisitionClient<IBiometricsFingerprint> implements AuthenticationConsumer, Udfps {
    public boolean mIsPointerDown;
    public final boolean mIsStrongBiometric;
    public final SensorOverlays mSensorOverlays;

    @Override // com.android.server.biometrics.sensors.BaseClientMonitor
    public int getProtoEnum() {
        return 13;
    }

    @Override // com.android.server.biometrics.sensors.BaseClientMonitor
    public boolean interruptsPrecedingClients() {
        return true;
    }

    @Override // com.android.server.biometrics.sensors.fingerprint.Udfps
    public void onUiReady() {
    }

    public FingerprintDetectClient(Context context, Supplier<IBiometricsFingerprint> supplier, IBinder iBinder, long j, ClientMonitorCallbackConverter clientMonitorCallbackConverter, FingerprintAuthenticateOptions fingerprintAuthenticateOptions, BiometricLogger biometricLogger, BiometricContext biometricContext, IUdfpsOverlayController iUdfpsOverlayController, IUdfpsOverlay iUdfpsOverlay, boolean z) {
        super(context, supplier, iBinder, clientMonitorCallbackConverter, fingerprintAuthenticateOptions.getUserId(), fingerprintAuthenticateOptions.getOpPackageName(), 0, fingerprintAuthenticateOptions.getSensorId(), true, biometricLogger, biometricContext);
        setRequestId(j);
        this.mSensorOverlays = new SensorOverlays(iUdfpsOverlayController, null, iUdfpsOverlay);
        this.mIsStrongBiometric = z;
    }

    @Override // com.android.server.biometrics.sensors.AcquisitionClient
    public void stopHalOperation() {
        this.mSensorOverlays.hide(getSensorId());
        try {
            getFreshDaemon().cancel();
        } catch (RemoteException e) {
            Slog.e("FingerprintDetectClient", "Remote exception when requesting cancel", e);
            onError(1, 0);
            this.mCallback.onClientFinished(this, false);
        }
    }

    @Override // com.android.server.biometrics.sensors.BaseClientMonitor
    public void start(ClientMonitorCallback clientMonitorCallback) {
        super.start(clientMonitorCallback);
        startHalOperation();
    }

    @Override // com.android.server.biometrics.sensors.HalClientMonitor
    public void startHalOperation() {
        this.mSensorOverlays.show(getSensorId(), 4, this);
        try {
            getFreshDaemon().authenticate(0L, getTargetUserId());
        } catch (RemoteException e) {
            Slog.e("FingerprintDetectClient", "Remote exception when requesting auth", e);
            onError(1, 0);
            this.mSensorOverlays.hide(getSensorId());
            this.mCallback.onClientFinished(this, false);
        }
    }

    @Override // com.android.server.biometrics.sensors.fingerprint.Udfps
    public void onPointerDown(PointerContext pointerContext) {
        this.mIsPointerDown = true;
        UdfpsHelper.onFingerDown(getFreshDaemon(), (int) pointerContext.x, (int) pointerContext.y, pointerContext.minor, pointerContext.major);
    }

    @Override // com.android.server.biometrics.sensors.fingerprint.Udfps
    public void onPointerUp(PointerContext pointerContext) {
        this.mIsPointerDown = false;
        UdfpsHelper.onFingerUp(getFreshDaemon());
    }

    @Override // com.android.server.biometrics.sensors.AuthenticationConsumer
    public void onAuthenticated(BiometricAuthenticator.Identifier identifier, boolean z, ArrayList<Byte> arrayList) {
        getLogger().logOnAuthenticated(getContext(), getOperationContext(), z, false, getTargetUserId(), false);
        vibrateSuccess();
        PerformanceTracker.getInstanceForSensorId(getSensorId()).incrementAuthForUser(getTargetUserId(), z);
        if (getListener() != null) {
            try {
                getListener().onDetected(getSensorId(), getTargetUserId(), this.mIsStrongBiometric);
            } catch (RemoteException e) {
                Slog.e("FingerprintDetectClient", "Remote exception when sending onDetected", e);
            }
        }
    }
}
