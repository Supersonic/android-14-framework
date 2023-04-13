package com.android.server.biometrics.sensors.face.aidl;

import android.app.NotificationManager;
import android.content.Context;
import android.content.res.Resources;
import android.hardware.SensorPrivacyManager;
import android.hardware.biometrics.BiometricAuthenticator;
import android.hardware.biometrics.BiometricManager;
import android.hardware.biometrics.common.ICancellationSignal;
import android.hardware.face.FaceAuthenticateOptions;
import android.hardware.face.FaceAuthenticationFrame;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Slog;
import com.android.internal.annotations.VisibleForTesting;
import com.android.server.biometrics.Utils;
import com.android.server.biometrics.log.BiometricContext;
import com.android.server.biometrics.log.BiometricLogger;
import com.android.server.biometrics.sensors.AuthSessionCoordinator;
import com.android.server.biometrics.sensors.AuthenticationClient;
import com.android.server.biometrics.sensors.BiometricNotificationUtils;
import com.android.server.biometrics.sensors.ClientMonitorCallback;
import com.android.server.biometrics.sensors.ClientMonitorCallbackConverter;
import com.android.server.biometrics.sensors.ClientMonitorCompositeCallback;
import com.android.server.biometrics.sensors.LockoutCache;
import com.android.server.biometrics.sensors.LockoutConsumer;
import com.android.server.biometrics.sensors.PerformanceTracker;
import com.android.server.biometrics.sensors.face.UsageStats;
import java.util.ArrayList;
import java.util.function.Supplier;
/* loaded from: classes.dex */
public class FaceAuthenticationClient extends AuthenticationClient<AidlSession, FaceAuthenticateOptions> implements LockoutConsumer {
    public final AuthSessionCoordinator mAuthSessionCoordinator;
    public final int[] mBiometricPromptIgnoreList;
    public final int[] mBiometricPromptIgnoreListVendor;
    public ICancellationSignal mCancellationSignal;
    public final int[] mKeyguardIgnoreList;
    public final int[] mKeyguardIgnoreListVendor;
    public int mLastAcquire;
    public final NotificationManager mNotificationManager;
    public SensorPrivacyManager mSensorPrivacyManager;
    public final UsageStats mUsageStats;

    public FaceAuthenticationClient(Context context, Supplier<AidlSession> supplier, IBinder iBinder, long j, ClientMonitorCallbackConverter clientMonitorCallbackConverter, long j2, boolean z, FaceAuthenticateOptions faceAuthenticateOptions, int i, boolean z2, BiometricLogger biometricLogger, BiometricContext biometricContext, boolean z3, UsageStats usageStats, LockoutCache lockoutCache, boolean z4, @BiometricManager.Authenticators.Types int i2) {
        this(context, supplier, iBinder, j, clientMonitorCallbackConverter, j2, z, faceAuthenticateOptions, i, z2, biometricLogger, biometricContext, z3, usageStats, lockoutCache, z4, (SensorPrivacyManager) context.getSystemService(SensorPrivacyManager.class), i2);
    }

    @VisibleForTesting
    public FaceAuthenticationClient(Context context, Supplier<AidlSession> supplier, IBinder iBinder, long j, ClientMonitorCallbackConverter clientMonitorCallbackConverter, long j2, boolean z, FaceAuthenticateOptions faceAuthenticateOptions, int i, boolean z2, BiometricLogger biometricLogger, BiometricContext biometricContext, boolean z3, UsageStats usageStats, LockoutCache lockoutCache, boolean z4, SensorPrivacyManager sensorPrivacyManager, @BiometricManager.Authenticators.Types int i2) {
        super(context, supplier, iBinder, clientMonitorCallbackConverter, j2, z, faceAuthenticateOptions, i, z2, biometricLogger, biometricContext, z3, null, null, z4, false, i2);
        this.mLastAcquire = 23;
        setRequestId(j);
        this.mUsageStats = usageStats;
        this.mNotificationManager = (NotificationManager) context.getSystemService(NotificationManager.class);
        this.mSensorPrivacyManager = sensorPrivacyManager;
        this.mAuthSessionCoordinator = biometricContext.getAuthSessionCoordinator();
        Resources resources = getContext().getResources();
        this.mBiometricPromptIgnoreList = resources.getIntArray(17236065);
        this.mBiometricPromptIgnoreListVendor = resources.getIntArray(17236068);
        this.mKeyguardIgnoreList = resources.getIntArray(17236067);
        this.mKeyguardIgnoreListVendor = resources.getIntArray(17236070);
    }

    @Override // com.android.server.biometrics.sensors.AuthenticationClient, com.android.server.biometrics.sensors.BaseClientMonitor
    public void start(ClientMonitorCallback clientMonitorCallback) {
        super.start(clientMonitorCallback);
        this.mState = 1;
    }

    @Override // com.android.server.biometrics.sensors.BaseClientMonitor
    public ClientMonitorCallback wrapCallbackForStart(ClientMonitorCallback clientMonitorCallback) {
        return new ClientMonitorCompositeCallback(getLogger().getAmbientLightProbe(true), clientMonitorCallback);
    }

    @Override // com.android.server.biometrics.sensors.HalClientMonitor
    public void startHalOperation() {
        try {
            SensorPrivacyManager sensorPrivacyManager = this.mSensorPrivacyManager;
            if (sensorPrivacyManager != null && sensorPrivacyManager.isSensorPrivacyEnabled(1, 2)) {
                onError(1, 0);
                this.mCallback.onClientFinished(this, false);
            } else {
                this.mCancellationSignal = doAuthenticate();
            }
        } catch (RemoteException e) {
            Slog.e("FaceAuthenticationClient", "Remote exception when requesting auth", e);
            onError(1, 0);
            this.mCallback.onClientFinished(this, false);
        }
    }

    public final ICancellationSignal doAuthenticate() throws RemoteException {
        AidlSession freshDaemon = getFreshDaemon();
        if (freshDaemon.hasContextMethods()) {
            return freshDaemon.getSession().authenticateWithContext(this.mOperationId, getOperationContext().toAidlContext(getOptions()));
        }
        return freshDaemon.getSession().authenticate(this.mOperationId);
    }

    @Override // com.android.server.biometrics.sensors.AcquisitionClient
    public void stopHalOperation() {
        ICancellationSignal iCancellationSignal = this.mCancellationSignal;
        if (iCancellationSignal != null) {
            try {
                iCancellationSignal.cancel();
            } catch (RemoteException e) {
                Slog.e("FaceAuthenticationClient", "Remote exception when requesting cancel", e);
                onError(1, 0);
                this.mCallback.onClientFinished(this, false);
            }
        }
    }

    @Override // com.android.server.biometrics.sensors.AuthenticationClient
    public void handleLifecycleAfterAuth(boolean z) {
        this.mCallback.onClientFinished(this, true);
    }

    @Override // com.android.server.biometrics.sensors.AuthenticationClient, com.android.server.biometrics.sensors.AuthenticationConsumer
    public void onAuthenticated(BiometricAuthenticator.Identifier identifier, boolean z, ArrayList<Byte> arrayList) {
        super.onAuthenticated(identifier, z, arrayList);
        this.mState = 4;
        this.mUsageStats.addEvent(new UsageStats.AuthenticationEvent(getStartTimeMs(), System.currentTimeMillis() - getStartTimeMs(), z, 0, 0, getTargetUserId()));
    }

    @Override // com.android.server.biometrics.sensors.AuthenticationClient, com.android.server.biometrics.sensors.AcquisitionClient, com.android.server.biometrics.sensors.ErrorConsumer
    public void onError(int i, int i2) {
        this.mUsageStats.addEvent(new UsageStats.AuthenticationEvent(getStartTimeMs(), System.currentTimeMillis() - getStartTimeMs(), false, i, i2, getTargetUserId()));
        if (i == 16) {
            BiometricNotificationUtils.showReEnrollmentNotification(getContext());
        }
        super.onError(i, i2);
    }

    public final int[] getAcquireIgnorelist() {
        return isBiometricPrompt() ? this.mBiometricPromptIgnoreList : this.mKeyguardIgnoreList;
    }

    public final int[] getAcquireVendorIgnorelist() {
        return isBiometricPrompt() ? this.mBiometricPromptIgnoreListVendor : this.mKeyguardIgnoreListVendor;
    }

    public final boolean shouldSendAcquiredMessage(int i, int i2) {
        if (i == 22) {
            if (!Utils.listContains(getAcquireVendorIgnorelist(), i2)) {
                return true;
            }
        } else if (!Utils.listContains(getAcquireIgnorelist(), i)) {
            return true;
        }
        return false;
    }

    @Override // com.android.server.biometrics.sensors.AuthenticationClient, com.android.server.biometrics.sensors.AcquisitionClient
    public void onAcquired(int i, int i2) {
        this.mLastAcquire = i;
        onAcquiredInternal(i, i2, shouldSendAcquiredMessage(i, i2));
        PerformanceTracker.getInstanceForSensorId(getSensorId()).incrementAcquireForUser(getTargetUserId(), isCryptoOperation());
    }

    public void onAuthenticationFrame(FaceAuthenticationFrame faceAuthenticationFrame) {
        int acquiredInfo = faceAuthenticationFrame.getData().getAcquiredInfo();
        int vendorCode = faceAuthenticationFrame.getData().getVendorCode();
        this.mLastAcquire = acquiredInfo;
        onAcquiredInternal(acquiredInfo, vendorCode, false);
        if (!shouldSendAcquiredMessage(acquiredInfo, vendorCode) || getListener() == null) {
            return;
        }
        try {
            getListener().onAuthenticationFrame(faceAuthenticationFrame);
        } catch (RemoteException e) {
            Slog.w("FaceAuthenticationClient", "Failed to send authentication frame", e);
            this.mCallback.onClientFinished(this, false);
        }
    }

    @Override // com.android.server.biometrics.sensors.LockoutConsumer
    public void onLockoutTimed(long j) {
        this.mAuthSessionCoordinator.lockOutTimed(getTargetUserId(), getSensorStrength(), getSensorId(), j, getRequestId());
        getLogger().logOnError(getContext(), getOperationContext(), 7, 0, getTargetUserId());
        PerformanceTracker.getInstanceForSensorId(getSensorId()).incrementTimedLockoutForUser(getTargetUserId());
        try {
            getListener().onError(getSensorId(), getCookie(), 7, 0);
        } catch (RemoteException e) {
            Slog.e("FaceAuthenticationClient", "Remote exception", e);
        }
    }

    @Override // com.android.server.biometrics.sensors.LockoutConsumer
    public void onLockoutPermanent() {
        this.mAuthSessionCoordinator.lockedOutFor(getTargetUserId(), getSensorStrength(), getSensorId(), getRequestId());
        getLogger().logOnError(getContext(), getOperationContext(), 9, 0, getTargetUserId());
        PerformanceTracker.getInstanceForSensorId(getSensorId()).incrementPermanentLockoutForUser(getTargetUserId());
        try {
            getListener().onError(getSensorId(), getCookie(), 9, 0);
        } catch (RemoteException e) {
            Slog.e("FaceAuthenticationClient", "Remote exception", e);
        }
    }
}
