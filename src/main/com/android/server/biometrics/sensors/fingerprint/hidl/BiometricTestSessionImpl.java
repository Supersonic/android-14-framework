package com.android.server.biometrics.sensors.fingerprint.hidl;

import android.annotation.EnforcePermission;
import android.content.Context;
import android.hardware.biometrics.ITestSession;
import android.hardware.biometrics.ITestSessionCallback;
import android.hardware.fingerprint.Fingerprint;
import android.hardware.fingerprint.IFingerprintServiceReceiver;
import android.os.Binder;
import android.os.RemoteException;
import android.util.Slog;
import com.android.server.biometrics.sensors.BaseClientMonitor;
import com.android.server.biometrics.sensors.BiometricStateCallback;
import com.android.server.biometrics.sensors.ClientMonitorCallback;
import com.android.server.biometrics.sensors.fingerprint.FingerprintUtils;
import com.android.server.biometrics.sensors.fingerprint.hidl.Fingerprint21;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
/* loaded from: classes.dex */
public class BiometricTestSessionImpl extends ITestSession.Stub {
    public final BiometricStateCallback mBiometricStateCallback;
    public final ITestSessionCallback mCallback;
    public final Context mContext;
    public final Fingerprint21 mFingerprint21;
    public final Fingerprint21.HalResultController mHalResultController;
    public final int mSensorId;
    public final IFingerprintServiceReceiver mReceiver = new IFingerprintServiceReceiver.Stub() { // from class: com.android.server.biometrics.sensors.fingerprint.hidl.BiometricTestSessionImpl.1
        public void onAcquired(int i, int i2) {
        }

        public void onAuthenticationFailed() {
        }

        public void onAuthenticationSucceeded(Fingerprint fingerprint, int i, boolean z) {
        }

        public void onChallengeGenerated(int i, int i2, long j) {
        }

        public void onEnrollResult(Fingerprint fingerprint, int i) {
        }

        public void onError(int i, int i2) {
        }

        public void onFingerprintDetected(int i, int i2, boolean z) {
        }

        public void onRemoved(Fingerprint fingerprint, int i) {
        }

        public void onUdfpsPointerDown(int i) {
        }

        public void onUdfpsPointerUp(int i) {
        }
    };
    public final Set<Integer> mEnrollmentIds = new HashSet();
    public final Random mRandom = new Random();

    public BiometricTestSessionImpl(Context context, int i, ITestSessionCallback iTestSessionCallback, BiometricStateCallback biometricStateCallback, Fingerprint21 fingerprint21, Fingerprint21.HalResultController halResultController) {
        this.mContext = context;
        this.mSensorId = i;
        this.mCallback = iTestSessionCallback;
        this.mFingerprint21 = fingerprint21;
        this.mBiometricStateCallback = biometricStateCallback;
        this.mHalResultController = halResultController;
    }

    @EnforcePermission("android.permission.TEST_BIOMETRIC")
    public void setTestHalEnabled(boolean z) {
        super.setTestHalEnabled_enforcePermission();
        this.mFingerprint21.setTestHalEnabled(z);
    }

    @EnforcePermission("android.permission.TEST_BIOMETRIC")
    public void startEnroll(int i) {
        super.startEnroll_enforcePermission();
        this.mFingerprint21.scheduleEnroll(this.mSensorId, new Binder(), new byte[69], i, this.mReceiver, this.mContext.getOpPackageName(), 2);
    }

    @EnforcePermission("android.permission.TEST_BIOMETRIC")
    public void finishEnroll(int i) {
        super.finishEnroll_enforcePermission();
        int nextInt = this.mRandom.nextInt();
        while (this.mEnrollmentIds.contains(Integer.valueOf(nextInt))) {
            nextInt = this.mRandom.nextInt();
        }
        this.mEnrollmentIds.add(Integer.valueOf(nextInt));
        this.mHalResultController.onEnrollResult(0L, nextInt, i, 0);
    }

    @EnforcePermission("android.permission.TEST_BIOMETRIC")
    public void acceptAuthentication(int i) {
        super.acceptAuthentication_enforcePermission();
        List<Fingerprint> biometricsForUser = FingerprintUtils.getLegacyInstance(this.mSensorId).getBiometricsForUser(this.mContext, i);
        if (biometricsForUser.isEmpty()) {
            Slog.w("BiometricTestSessionImpl", "No fingerprints, returning");
            return;
        }
        this.mHalResultController.onAuthenticated(0L, biometricsForUser.get(0).getBiometricId(), i, new ArrayList<>(Collections.nCopies(69, (byte) 0)));
    }

    @EnforcePermission("android.permission.TEST_BIOMETRIC")
    public void rejectAuthentication(int i) {
        super.rejectAuthentication_enforcePermission();
        this.mHalResultController.onAuthenticated(0L, 0, i, null);
    }

    @EnforcePermission("android.permission.TEST_BIOMETRIC")
    public void notifyAcquired(int i, int i2) {
        super.notifyAcquired_enforcePermission();
        this.mHalResultController.onAcquired(0L, i2, 0);
    }

    @EnforcePermission("android.permission.TEST_BIOMETRIC")
    public void notifyError(int i, int i2) {
        super.notifyError_enforcePermission();
        this.mHalResultController.onError(0L, i2, 0);
    }

    @EnforcePermission("android.permission.TEST_BIOMETRIC")
    public void cleanupInternalState(int i) {
        super.cleanupInternalState_enforcePermission();
        this.mFingerprint21.scheduleInternalCleanup(this.mSensorId, i, new ClientMonitorCallback() { // from class: com.android.server.biometrics.sensors.fingerprint.hidl.BiometricTestSessionImpl.2
            @Override // com.android.server.biometrics.sensors.ClientMonitorCallback
            public void onClientStarted(BaseClientMonitor baseClientMonitor) {
                try {
                    BiometricTestSessionImpl.this.mCallback.onCleanupStarted(baseClientMonitor.getTargetUserId());
                } catch (RemoteException e) {
                    Slog.e("BiometricTestSessionImpl", "Remote exception", e);
                }
            }

            @Override // com.android.server.biometrics.sensors.ClientMonitorCallback
            public void onClientFinished(BaseClientMonitor baseClientMonitor, boolean z) {
                try {
                    BiometricTestSessionImpl.this.mCallback.onCleanupFinished(baseClientMonitor.getTargetUserId());
                } catch (RemoteException e) {
                    Slog.e("BiometricTestSessionImpl", "Remote exception", e);
                }
            }
        });
    }
}
