package com.android.server.biometrics.sensors.fingerprint.aidl;

import android.content.Context;
import android.hardware.biometrics.BiometricAuthenticator;
import android.hardware.biometrics.common.ICancellationSignal;
import android.hardware.biometrics.common.OperationContext;
import android.hardware.biometrics.fingerprint.PointerContext;
import android.hardware.fingerprint.Fingerprint;
import android.hardware.fingerprint.FingerprintSensorPropertiesInternal;
import android.hardware.fingerprint.ISidefpsController;
import android.hardware.fingerprint.IUdfpsOverlay;
import android.hardware.fingerprint.IUdfpsOverlayController;
import android.hardware.keymaster.HardwareAuthToken;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Slog;
import android.view.accessibility.AccessibilityManager;
import com.android.server.biometrics.HardwareAuthTokenUtils;
import com.android.server.biometrics.log.BiometricContext;
import com.android.server.biometrics.log.BiometricLogger;
import com.android.server.biometrics.log.CallbackWithProbe;
import com.android.server.biometrics.log.OperationContextExt;
import com.android.server.biometrics.log.Probe;
import com.android.server.biometrics.sensors.BiometricNotificationUtils;
import com.android.server.biometrics.sensors.BiometricUtils;
import com.android.server.biometrics.sensors.ClientMonitorCallback;
import com.android.server.biometrics.sensors.ClientMonitorCallbackConverter;
import com.android.server.biometrics.sensors.ClientMonitorCompositeCallback;
import com.android.server.biometrics.sensors.EnrollClient;
import com.android.server.biometrics.sensors.SensorOverlays;
import com.android.server.biometrics.sensors.fingerprint.FingerprintUtils;
import com.android.server.biometrics.sensors.fingerprint.PowerPressHandler;
import com.android.server.biometrics.sensors.fingerprint.Udfps;
import com.android.server.biometrics.sensors.fingerprint.UdfpsHelper;
import java.util.function.Consumer;
import java.util.function.Supplier;
/* loaded from: classes.dex */
public class FingerprintEnrollClient extends EnrollClient<AidlSession> implements Udfps, PowerPressHandler {
    public final CallbackWithProbe<Probe> mALSProbeCallback;
    public ICancellationSignal mCancellationSignal;
    public final int mEnrollReason;
    public boolean mIsPointerDown;
    public final int mMaxTemplatesPerUser;
    public final SensorOverlays mSensorOverlays;
    public final FingerprintSensorPropertiesInternal mSensorProps;

    @Override // com.android.server.biometrics.sensors.fingerprint.PowerPressHandler
    public void onPowerPressed() {
    }

    public static boolean shouldVibrateFor(Context context, FingerprintSensorPropertiesInternal fingerprintSensorPropertiesInternal) {
        return !fingerprintSensorPropertiesInternal.isAnyUdfpsType() || ((AccessibilityManager) context.getSystemService(AccessibilityManager.class)).isTouchExplorationEnabled();
    }

    public FingerprintEnrollClient(Context context, Supplier<AidlSession> supplier, IBinder iBinder, long j, ClientMonitorCallbackConverter clientMonitorCallbackConverter, int i, byte[] bArr, String str, BiometricUtils<Fingerprint> biometricUtils, int i2, BiometricLogger biometricLogger, BiometricContext biometricContext, FingerprintSensorPropertiesInternal fingerprintSensorPropertiesInternal, IUdfpsOverlayController iUdfpsOverlayController, ISidefpsController iSidefpsController, IUdfpsOverlay iUdfpsOverlay, int i3, int i4) {
        super(context, supplier, iBinder, clientMonitorCallbackConverter, i, bArr, str, biometricUtils, 0, i2, shouldVibrateFor(context, fingerprintSensorPropertiesInternal), biometricLogger, biometricContext);
        setRequestId(j);
        this.mSensorProps = fingerprintSensorPropertiesInternal;
        this.mSensorOverlays = new SensorOverlays(iUdfpsOverlayController, iSidefpsController, iUdfpsOverlay);
        this.mMaxTemplatesPerUser = i3;
        this.mALSProbeCallback = getLogger().getAmbientLightProbe(true);
        this.mEnrollReason = i4;
        if (i4 == 1) {
            getLogger().disableMetrics();
        }
    }

    @Override // com.android.server.biometrics.sensors.BaseClientMonitor
    public ClientMonitorCallback wrapCallbackForStart(ClientMonitorCallback clientMonitorCallback) {
        return new ClientMonitorCompositeCallback(this.mALSProbeCallback, getBiometricContextUnsubscriber(), clientMonitorCallback);
    }

    @Override // com.android.server.biometrics.sensors.EnrollClient
    public void onEnrollResult(BiometricAuthenticator.Identifier identifier, final int i) {
        super.onEnrollResult(identifier, i);
        this.mSensorOverlays.ifUdfps(new SensorOverlays.OverlayControllerConsumer() { // from class: com.android.server.biometrics.sensors.fingerprint.aidl.FingerprintEnrollClient$$ExternalSyntheticLambda3
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
        boolean z = i == 0;
        if (this.mSensorProps.isAnyUdfpsType()) {
            if (z && this.mShouldVibrate) {
                vibrateSuccess();
            }
            this.mSensorOverlays.ifUdfps(new SensorOverlays.OverlayControllerConsumer() { // from class: com.android.server.biometrics.sensors.fingerprint.aidl.FingerprintEnrollClient$$ExternalSyntheticLambda0
                @Override // com.android.server.biometrics.sensors.SensorOverlays.OverlayControllerConsumer
                public final void accept(Object obj) {
                    FingerprintEnrollClient.this.lambda$onAcquired$1(i, (IUdfpsOverlayController) obj);
                }
            });
        }
        this.mSensorOverlays.ifUdfps(new SensorOverlays.OverlayControllerConsumer() { // from class: com.android.server.biometrics.sensors.fingerprint.aidl.FingerprintEnrollClient$$ExternalSyntheticLambda1
            @Override // com.android.server.biometrics.sensors.SensorOverlays.OverlayControllerConsumer
            public final void accept(Object obj) {
                FingerprintEnrollClient.this.lambda$onAcquired$2(i, i2, (IUdfpsOverlayController) obj);
            }
        });
        this.mCallback.onBiometricAction(0);
        super.onAcquired(i, i2);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onAcquired$1(int i, IUdfpsOverlayController iUdfpsOverlayController) throws RemoteException {
        iUdfpsOverlayController.onAcquired(getSensorId(), i);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onAcquired$2(int i, int i2, IUdfpsOverlayController iUdfpsOverlayController) throws RemoteException {
        if (UdfpsHelper.isValidAcquisitionMessage(getContext(), i, i2)) {
            iUdfpsOverlayController.onEnrollmentHelp(getSensorId());
        }
    }

    @Override // com.android.server.biometrics.sensors.EnrollClient, com.android.server.biometrics.sensors.AcquisitionClient, com.android.server.biometrics.sensors.ErrorConsumer
    public void onError(int i, int i2) {
        super.onError(i, i2);
        this.mSensorOverlays.hide(getSensorId());
    }

    @Override // com.android.server.biometrics.sensors.EnrollClient
    public boolean hasReachedEnrollmentLimit() {
        return FingerprintUtils.getInstance(getSensorId()).getBiometricsForUser(getContext(), getTargetUserId()).size() >= this.mMaxTemplatesPerUser;
    }

    @Override // com.android.server.biometrics.sensors.HalClientMonitor
    public void startHalOperation() {
        this.mSensorOverlays.show(getSensorId(), getOverlayReasonFromEnrollReason(this.mEnrollReason), this);
        BiometricNotificationUtils.cancelBadCalibrationNotification(getContext());
        try {
            this.mCancellationSignal = doEnroll();
        } catch (RemoteException e) {
            Slog.e("FingerprintEnrollClient", "Remote exception when requesting enroll", e);
            onError(2, 0);
            this.mCallback.onClientFinished(this, false);
        }
    }

    public final ICancellationSignal doEnroll() throws RemoteException {
        final AidlSession freshDaemon = getFreshDaemon();
        HardwareAuthToken hardwareAuthToken = HardwareAuthTokenUtils.toHardwareAuthToken(this.mHardwareAuthToken);
        if (freshDaemon.hasContextMethods()) {
            OperationContextExt operationContext = getOperationContext();
            ICancellationSignal enrollWithContext = freshDaemon.getSession().enrollWithContext(hardwareAuthToken, operationContext.toAidlContext());
            getBiometricContext().subscribe(operationContext, new Consumer() { // from class: com.android.server.biometrics.sensors.fingerprint.aidl.FingerprintEnrollClient$$ExternalSyntheticLambda2
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    FingerprintEnrollClient.lambda$doEnroll$3(AidlSession.this, (OperationContext) obj);
                }
            });
            return enrollWithContext;
        }
        return freshDaemon.getSession().enroll(hardwareAuthToken);
    }

    public static /* synthetic */ void lambda$doEnroll$3(AidlSession aidlSession, OperationContext operationContext) {
        try {
            aidlSession.getSession().onContextChanged(operationContext);
        } catch (RemoteException e) {
            Slog.e("FingerprintEnrollClient", "Unable to notify context changed", e);
        }
    }

    @Override // com.android.server.biometrics.sensors.AcquisitionClient
    public void stopHalOperation() {
        this.mSensorOverlays.hide(getSensorId());
        unsubscribeBiometricContext();
        ICancellationSignal iCancellationSignal = this.mCancellationSignal;
        if (iCancellationSignal != null) {
            try {
                iCancellationSignal.cancel();
            } catch (RemoteException e) {
                Slog.e("FingerprintEnrollClient", "Remote exception when requesting cancel", e);
                onError(1, 0);
                this.mCallback.onClientFinished(this, false);
            }
        }
    }

    @Override // com.android.server.biometrics.sensors.fingerprint.Udfps
    public void onPointerDown(PointerContext pointerContext) {
        try {
            this.mIsPointerDown = true;
            AidlSession freshDaemon = getFreshDaemon();
            if (freshDaemon.hasContextMethods()) {
                freshDaemon.getSession().onPointerDownWithContext(pointerContext);
            } else {
                freshDaemon.getSession().onPointerDown(pointerContext.pointerId, (int) pointerContext.x, (int) pointerContext.y, pointerContext.minor, pointerContext.major);
            }
            if (getListener() != null) {
                getListener().onUdfpsPointerDown(getSensorId());
            }
        } catch (RemoteException e) {
            Slog.e("FingerprintEnrollClient", "Unable to send pointer down", e);
        }
    }

    @Override // com.android.server.biometrics.sensors.fingerprint.Udfps
    public void onPointerUp(PointerContext pointerContext) {
        try {
            this.mIsPointerDown = false;
            AidlSession freshDaemon = getFreshDaemon();
            if (freshDaemon.hasContextMethods()) {
                freshDaemon.getSession().onPointerUpWithContext(pointerContext);
            } else {
                freshDaemon.getSession().onPointerUp(pointerContext.pointerId);
            }
            if (getListener() != null) {
                getListener().onUdfpsPointerUp(getSensorId());
            }
        } catch (RemoteException e) {
            Slog.e("FingerprintEnrollClient", "Unable to send pointer up", e);
        }
    }

    @Override // com.android.server.biometrics.sensors.fingerprint.Udfps
    public void onUiReady() {
        try {
            getFreshDaemon().getSession().onUiReady();
        } catch (RemoteException e) {
            Slog.e("FingerprintEnrollClient", "Unable to send UI ready", e);
        }
    }
}
