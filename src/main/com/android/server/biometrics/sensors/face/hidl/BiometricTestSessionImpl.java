package com.android.server.biometrics.sensors.face.hidl;

import android.annotation.EnforcePermission;
import android.content.Context;
import android.hardware.biometrics.ITestSession;
import android.hardware.biometrics.ITestSessionCallback;
import android.hardware.face.Face;
import android.hardware.face.FaceAuthenticationFrame;
import android.hardware.face.FaceEnrollFrame;
import android.hardware.face.IFaceServiceReceiver;
import android.os.Binder;
import android.os.RemoteException;
import android.util.Slog;
import com.android.server.biometrics.sensors.BaseClientMonitor;
import com.android.server.biometrics.sensors.ClientMonitorCallback;
import com.android.server.biometrics.sensors.face.FaceUtils;
import com.android.server.biometrics.sensors.face.hidl.Face10;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
/* loaded from: classes.dex */
public class BiometricTestSessionImpl extends ITestSession.Stub {
    public final ITestSessionCallback mCallback;
    public final Context mContext;
    public final Face10 mFace10;
    public final Face10.HalResultController mHalResultController;
    public final int mSensorId;
    public final IFaceServiceReceiver mReceiver = new IFaceServiceReceiver.Stub() { // from class: com.android.server.biometrics.sensors.face.hidl.BiometricTestSessionImpl.1
        public void onAcquired(int i, int i2) {
        }

        public void onAuthenticationFailed() {
        }

        public void onAuthenticationFrame(FaceAuthenticationFrame faceAuthenticationFrame) {
        }

        public void onAuthenticationSucceeded(Face face, int i, boolean z) {
        }

        public void onChallengeGenerated(int i, int i2, long j) {
        }

        public void onEnrollResult(Face face, int i) {
        }

        public void onEnrollmentFrame(FaceEnrollFrame faceEnrollFrame) {
        }

        public void onError(int i, int i2) {
        }

        public void onFaceDetected(int i, int i2, boolean z) {
        }

        public void onFeatureGet(boolean z, int[] iArr, boolean[] zArr) {
        }

        public void onFeatureSet(boolean z, int i) {
        }

        public void onRemoved(Face face, int i) {
        }
    };
    public final Set<Integer> mEnrollmentIds = new HashSet();
    public final Random mRandom = new Random();

    public BiometricTestSessionImpl(Context context, int i, ITestSessionCallback iTestSessionCallback, Face10 face10, Face10.HalResultController halResultController) {
        this.mContext = context;
        this.mSensorId = i;
        this.mCallback = iTestSessionCallback;
        this.mFace10 = face10;
        this.mHalResultController = halResultController;
    }

    @EnforcePermission("android.permission.TEST_BIOMETRIC")
    public void setTestHalEnabled(boolean z) {
        super.setTestHalEnabled_enforcePermission();
        this.mFace10.setTestHalEnabled(z);
    }

    @EnforcePermission("android.permission.TEST_BIOMETRIC")
    public void startEnroll(int i) {
        super.startEnroll_enforcePermission();
        this.mFace10.scheduleEnroll(this.mSensorId, new Binder(), new byte[69], i, this.mReceiver, this.mContext.getOpPackageName(), new int[0], null, false);
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
        List<Face> biometricsForUser = FaceUtils.getLegacyInstance(this.mSensorId).getBiometricsForUser(this.mContext, i);
        if (biometricsForUser.isEmpty()) {
            Slog.w("BiometricTestSessionImpl", "No faces, returning");
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
        this.mHalResultController.onAcquired(0L, i, i2, 0);
    }

    @EnforcePermission("android.permission.TEST_BIOMETRIC")
    public void notifyError(int i, int i2) {
        super.notifyError_enforcePermission();
        this.mHalResultController.onError(0L, i, i2, 0);
    }

    @EnforcePermission("android.permission.TEST_BIOMETRIC")
    public void cleanupInternalState(int i) {
        super.cleanupInternalState_enforcePermission();
        this.mFace10.scheduleInternalCleanup(this.mSensorId, i, new ClientMonitorCallback() { // from class: com.android.server.biometrics.sensors.face.hidl.BiometricTestSessionImpl.2
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
