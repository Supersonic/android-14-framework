package com.android.server.biometrics.sensors.face.aidl;

import android.annotation.EnforcePermission;
import android.content.Context;
import android.hardware.biometrics.ITestSession;
import android.hardware.biometrics.ITestSessionCallback;
import android.hardware.biometrics.face.AuthenticationFrame;
import android.hardware.biometrics.face.BaseFrame;
import android.hardware.biometrics.face.EnrollmentFrame;
import android.hardware.face.Face;
import android.hardware.face.FaceAuthenticationFrame;
import android.hardware.face.FaceEnrollFrame;
import android.hardware.face.IFaceServiceReceiver;
import android.os.Binder;
import android.os.RemoteException;
import android.util.Slog;
import com.android.server.biometrics.HardwareAuthTokenUtils;
import com.android.server.biometrics.sensors.BaseClientMonitor;
import com.android.server.biometrics.sensors.ClientMonitorCallback;
import com.android.server.biometrics.sensors.EnrollClient;
import com.android.server.biometrics.sensors.face.FaceUtils;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
/* loaded from: classes.dex */
public class BiometricTestSessionImpl extends ITestSession.Stub {
    public final ITestSessionCallback mCallback;
    public final Context mContext;
    public final FaceProvider mProvider;
    public final Sensor mSensor;
    public final int mSensorId;
    public final IFaceServiceReceiver mReceiver = new IFaceServiceReceiver.Stub() { // from class: com.android.server.biometrics.sensors.face.aidl.BiometricTestSessionImpl.1
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

    public BiometricTestSessionImpl(Context context, int i, ITestSessionCallback iTestSessionCallback, FaceProvider faceProvider, Sensor sensor) {
        this.mContext = context;
        this.mSensorId = i;
        this.mCallback = iTestSessionCallback;
        this.mProvider = faceProvider;
        this.mSensor = sensor;
    }

    @EnforcePermission("android.permission.TEST_BIOMETRIC")
    public void setTestHalEnabled(boolean z) {
        super.setTestHalEnabled_enforcePermission();
        this.mProvider.setTestHalEnabled(z);
        this.mSensor.setTestHalEnabled(z);
    }

    @EnforcePermission("android.permission.TEST_BIOMETRIC")
    public void startEnroll(int i) {
        super.startEnroll_enforcePermission();
        this.mProvider.scheduleEnroll(this.mSensorId, new Binder(), new byte[69], i, this.mReceiver, this.mContext.getOpPackageName(), new int[0], null, false);
    }

    @EnforcePermission("android.permission.TEST_BIOMETRIC")
    public void finishEnroll(int i) {
        super.finishEnroll_enforcePermission();
        int nextInt = this.mRandom.nextInt();
        while (this.mEnrollmentIds.contains(Integer.valueOf(nextInt))) {
            nextInt = this.mRandom.nextInt();
        }
        this.mEnrollmentIds.add(Integer.valueOf(nextInt));
        this.mSensor.getSessionForUser(i).getHalSessionCallback().onEnrollmentProgress(nextInt, 0);
    }

    @EnforcePermission("android.permission.TEST_BIOMETRIC")
    public void acceptAuthentication(int i) {
        super.acceptAuthentication_enforcePermission();
        List<Face> biometricsForUser = FaceUtils.getInstance(this.mSensorId).getBiometricsForUser(this.mContext, i);
        if (biometricsForUser.isEmpty()) {
            Slog.w("face/aidl/BiometricTestSessionImpl", "No faces, returning");
            return;
        }
        this.mSensor.getSessionForUser(i).getHalSessionCallback().onAuthenticationSucceeded(biometricsForUser.get(0).getBiometricId(), HardwareAuthTokenUtils.toHardwareAuthToken(new byte[69]));
    }

    @EnforcePermission("android.permission.TEST_BIOMETRIC")
    public void rejectAuthentication(int i) {
        super.rejectAuthentication_enforcePermission();
        this.mSensor.getSessionForUser(i).getHalSessionCallback().onAuthenticationFailed();
    }

    @EnforcePermission("android.permission.TEST_BIOMETRIC")
    public void notifyAcquired(int i, int i2) {
        super.notifyAcquired_enforcePermission();
        BaseFrame baseFrame = new BaseFrame();
        baseFrame.acquiredInfo = (byte) i2;
        if (this.mSensor.getScheduler().getCurrentClient() instanceof EnrollClient) {
            EnrollmentFrame enrollmentFrame = new EnrollmentFrame();
            enrollmentFrame.data = baseFrame;
            this.mSensor.getSessionForUser(i).getHalSessionCallback().onEnrollmentFrame(enrollmentFrame);
            return;
        }
        AuthenticationFrame authenticationFrame = new AuthenticationFrame();
        authenticationFrame.data = baseFrame;
        this.mSensor.getSessionForUser(i).getHalSessionCallback().onAuthenticationFrame(authenticationFrame);
    }

    @EnforcePermission("android.permission.TEST_BIOMETRIC")
    public void notifyError(int i, int i2) {
        super.notifyError_enforcePermission();
        this.mSensor.getSessionForUser(i).getHalSessionCallback().onError((byte) i2, 0);
    }

    @EnforcePermission("android.permission.TEST_BIOMETRIC")
    public void cleanupInternalState(int i) {
        super.cleanupInternalState_enforcePermission();
        Slog.d("face/aidl/BiometricTestSessionImpl", "cleanupInternalState: " + i);
        this.mProvider.scheduleInternalCleanup(this.mSensorId, i, new ClientMonitorCallback() { // from class: com.android.server.biometrics.sensors.face.aidl.BiometricTestSessionImpl.2
            @Override // com.android.server.biometrics.sensors.ClientMonitorCallback
            public void onClientStarted(BaseClientMonitor baseClientMonitor) {
                try {
                    Slog.d("face/aidl/BiometricTestSessionImpl", "onClientStarted: " + baseClientMonitor);
                    BiometricTestSessionImpl.this.mCallback.onCleanupStarted(baseClientMonitor.getTargetUserId());
                } catch (RemoteException e) {
                    Slog.e("face/aidl/BiometricTestSessionImpl", "Remote exception", e);
                }
            }

            @Override // com.android.server.biometrics.sensors.ClientMonitorCallback
            public void onClientFinished(BaseClientMonitor baseClientMonitor, boolean z) {
                try {
                    Slog.d("face/aidl/BiometricTestSessionImpl", "onClientFinished: " + baseClientMonitor);
                    BiometricTestSessionImpl.this.mCallback.onCleanupFinished(baseClientMonitor.getTargetUserId());
                } catch (RemoteException e) {
                    Slog.e("face/aidl/BiometricTestSessionImpl", "Remote exception", e);
                }
            }
        });
    }
}
