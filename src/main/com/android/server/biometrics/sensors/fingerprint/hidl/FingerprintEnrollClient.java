package com.android.server.biometrics.sensors.fingerprint.hidl;

import android.content.Context;
import android.hardware.biometrics.BiometricAuthenticator;
import android.hardware.biometrics.fingerprint.PointerContext;
import android.hardware.biometrics.fingerprint.V2_1.IBiometricsFingerprint;
import android.hardware.fingerprint.Fingerprint;
import android.hardware.fingerprint.ISidefpsController;
import android.hardware.fingerprint.IUdfpsOverlay;
import android.hardware.fingerprint.IUdfpsOverlayController;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Slog;
import com.android.server.biometrics.log.BiometricContext;
import com.android.server.biometrics.log.BiometricLogger;
import com.android.server.biometrics.sensors.BiometricNotificationUtils;
import com.android.server.biometrics.sensors.BiometricUtils;
import com.android.server.biometrics.sensors.ClientMonitorCallback;
import com.android.server.biometrics.sensors.ClientMonitorCallbackConverter;
import com.android.server.biometrics.sensors.ClientMonitorCompositeCallback;
import com.android.server.biometrics.sensors.EnrollClient;
import com.android.server.biometrics.sensors.SensorOverlays;
import com.android.server.biometrics.sensors.fingerprint.Udfps;
import com.android.server.biometrics.sensors.fingerprint.UdfpsHelper;
import java.util.function.Supplier;
/* loaded from: classes.dex */
public class FingerprintEnrollClient extends EnrollClient<IBiometricsFingerprint> implements Udfps {
    public final int mEnrollReason;
    public boolean mIsPointerDown;
    public final SensorOverlays mSensorOverlays;

    @Override // com.android.server.biometrics.sensors.fingerprint.Udfps
    public void onUiReady() {
    }

    public FingerprintEnrollClient(Context context, Supplier<IBiometricsFingerprint> supplier, IBinder iBinder, long j, ClientMonitorCallbackConverter clientMonitorCallbackConverter, int i, byte[] bArr, String str, BiometricUtils<Fingerprint> biometricUtils, int i2, int i3, BiometricLogger biometricLogger, BiometricContext biometricContext, IUdfpsOverlayController iUdfpsOverlayController, ISidefpsController iSidefpsController, IUdfpsOverlay iUdfpsOverlay, int i4) {
        super(context, supplier, iBinder, clientMonitorCallbackConverter, i, bArr, str, biometricUtils, i2, i3, true, biometricLogger, biometricContext);
        setRequestId(j);
        this.mSensorOverlays = new SensorOverlays(iUdfpsOverlayController, iSidefpsController, iUdfpsOverlay);
        this.mEnrollReason = i4;
        if (i4 == 1) {
            getLogger().disableMetrics();
        }
    }

    @Override // com.android.server.biometrics.sensors.BaseClientMonitor
    public ClientMonitorCallback wrapCallbackForStart(ClientMonitorCallback clientMonitorCallback) {
        return new ClientMonitorCompositeCallback(getLogger().getAmbientLightProbe(true), clientMonitorCallback);
    }

    @Override // com.android.server.biometrics.sensors.EnrollClient
    public boolean hasReachedEnrollmentLimit() {
        if (this.mBiometricUtils.getBiometricsForUser(getContext(), getTargetUserId()).size() >= getContext().getResources().getInteger(17694845)) {
            Slog.w("FingerprintEnrollClient", "Too many fingerprints registered, user: " + getTargetUserId());
            return true;
        }
        return false;
    }

    @Override // com.android.server.biometrics.sensors.HalClientMonitor
    public void startHalOperation() {
        this.mSensorOverlays.show(getSensorId(), getOverlayReasonFromEnrollReason(this.mEnrollReason), this);
        BiometricNotificationUtils.cancelBadCalibrationNotification(getContext());
        try {
            getFreshDaemon().enroll(this.mHardwareAuthToken, getTargetUserId(), this.mTimeoutSec);
        } catch (RemoteException e) {
            Slog.e("FingerprintEnrollClient", "Remote exception when requesting enroll", e);
            onError(1, 0);
            this.mSensorOverlays.hide(getSensorId());
            this.mCallback.onClientFinished(this, false);
        }
    }

    @Override // com.android.server.biometrics.sensors.AcquisitionClient
    public void stopHalOperation() {
        this.mSensorOverlays.hide(getSensorId());
        try {
            getFreshDaemon().cancel();
        } catch (RemoteException e) {
            Slog.e("FingerprintEnrollClient", "Remote exception when requesting cancel", e);
            onError(1, 0);
            this.mCallback.onClientFinished(this, false);
        }
    }

    @Override // com.android.server.biometrics.sensors.EnrollClient
    public void onEnrollResult(BiometricAuthenticator.Identifier identifier, final int i) {
        super.onEnrollResult(identifier, i);
        this.mSensorOverlays.ifUdfps(new SensorOverlays.OverlayControllerConsumer() { // from class: com.android.server.biometrics.sensors.fingerprint.hidl.FingerprintEnrollClient$$ExternalSyntheticLambda1
            @Override // com.android.server.biometrics.sensors.SensorOverlays.OverlayControllerConsumer
            public final void accept(Object obj) {
                FingerprintEnrollClient.this.lambda$onEnrollResult$0(i, (IUdfpsOverlayController) obj);
            }
        });
        if (i == 0) {
            this.mSensorOverlays.hide(getSensorId());
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onEnrollResult$0(int i, IUdfpsOverlayController iUdfpsOverlayController) throws RemoteException {
        iUdfpsOverlayController.onEnrollmentProgress(getSensorId(), i);
    }

    @Override // com.android.server.biometrics.sensors.AcquisitionClient
    public void onAcquired(final int i, final int i2) {
        super.onAcquired(i, i2);
        this.mSensorOverlays.ifUdfps(new SensorOverlays.OverlayControllerConsumer() { // from class: com.android.server.biometrics.sensors.fingerprint.hidl.FingerprintEnrollClient$$ExternalSyntheticLambda0
            @Override // com.android.server.biometrics.sensors.SensorOverlays.OverlayControllerConsumer
            public final void accept(Object obj) {
                FingerprintEnrollClient.this.lambda$onAcquired$1(i, i2, (IUdfpsOverlayController) obj);
            }
        });
        this.mCallback.onBiometricAction(0);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onAcquired$1(int i, int i2, IUdfpsOverlayController iUdfpsOverlayController) throws RemoteException {
        if (UdfpsHelper.isValidAcquisitionMessage(getContext(), i, i2)) {
            iUdfpsOverlayController.onEnrollmentHelp(getSensorId());
        }
    }

    @Override // com.android.server.biometrics.sensors.EnrollClient, com.android.server.biometrics.sensors.AcquisitionClient, com.android.server.biometrics.sensors.ErrorConsumer
    public void onError(int i, int i2) {
        super.onError(i, i2);
        this.mSensorOverlays.hide(getSensorId());
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
}
