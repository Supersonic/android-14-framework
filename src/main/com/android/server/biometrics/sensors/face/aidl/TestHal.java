package com.android.server.biometrics.sensors.face.aidl;

import android.hardware.biometrics.common.ICancellationSignal;
import android.hardware.biometrics.common.OperationContext;
import android.hardware.biometrics.face.EnrollmentStageConfig;
import android.hardware.biometrics.face.IFace;
import android.hardware.biometrics.face.ISession;
import android.hardware.biometrics.face.ISessionCallback;
import android.hardware.biometrics.face.SensorProps;
import android.hardware.common.NativeHandle;
import android.hardware.keymaster.HardwareAuthToken;
import android.os.RemoteException;
import android.util.Slog;
/* loaded from: classes.dex */
public class TestHal extends IFace.Stub {
    @Override // android.hardware.biometrics.face.IFace
    public String getInterfaceHash() {
        return "notfrozen";
    }

    @Override // android.hardware.biometrics.face.IFace
    public int getInterfaceVersion() {
        return 3;
    }

    @Override // android.hardware.biometrics.face.IFace
    public SensorProps[] getSensorProps() {
        Slog.w("face.aidl.TestHal", "getSensorProps");
        return new SensorProps[0];
    }

    @Override // android.hardware.biometrics.face.IFace
    public ISession createSession(int i, int i2, final ISessionCallback iSessionCallback) {
        Slog.w("face.aidl.TestHal", "createSession, sensorId: " + i + " userId: " + i2);
        return new ISession.Stub() { // from class: com.android.server.biometrics.sensors.face.aidl.TestHal.1
            @Override // android.hardware.biometrics.face.ISession
            public EnrollmentStageConfig[] getEnrollmentConfig(byte b) {
                return new EnrollmentStageConfig[0];
            }

            @Override // android.hardware.biometrics.face.ISession
            public String getInterfaceHash() {
                return "notfrozen";
            }

            @Override // android.hardware.biometrics.face.ISession
            public int getInterfaceVersion() {
                return 3;
            }

            @Override // android.hardware.biometrics.face.ISession
            public void generateChallenge() throws RemoteException {
                Slog.w("face.aidl.TestHal", "generateChallenge");
                iSessionCallback.onChallengeGenerated(0L);
            }

            @Override // android.hardware.biometrics.face.ISession
            public void revokeChallenge(long j) throws RemoteException {
                Slog.w("face.aidl.TestHal", "revokeChallenge: " + j);
                iSessionCallback.onChallengeRevoked(j);
            }

            @Override // android.hardware.biometrics.face.ISession
            public ICancellationSignal enroll(HardwareAuthToken hardwareAuthToken, byte b, byte[] bArr, NativeHandle nativeHandle) {
                Slog.w("face.aidl.TestHal", "enroll");
                return new ICancellationSignal.Stub() { // from class: com.android.server.biometrics.sensors.face.aidl.TestHal.1.1
                    public String getInterfaceHash() {
                        return "notfrozen";
                    }

                    public int getInterfaceVersion() {
                        return 3;
                    }

                    public void cancel() throws RemoteException {
                        iSessionCallback.onError((byte) 5, 0);
                    }
                };
            }

            @Override // android.hardware.biometrics.face.ISession
            public ICancellationSignal authenticate(long j) {
                Slog.w("face.aidl.TestHal", "authenticate");
                return new ICancellationSignal.Stub() { // from class: com.android.server.biometrics.sensors.face.aidl.TestHal.1.2
                    public String getInterfaceHash() {
                        return "notfrozen";
                    }

                    public int getInterfaceVersion() {
                        return 3;
                    }

                    public void cancel() throws RemoteException {
                        iSessionCallback.onError((byte) 5, 0);
                    }
                };
            }

            @Override // android.hardware.biometrics.face.ISession
            public ICancellationSignal detectInteraction() {
                Slog.w("face.aidl.TestHal", "detectInteraction");
                return new ICancellationSignal.Stub() { // from class: com.android.server.biometrics.sensors.face.aidl.TestHal.1.3
                    public String getInterfaceHash() {
                        return "notfrozen";
                    }

                    public int getInterfaceVersion() {
                        return 3;
                    }

                    public void cancel() throws RemoteException {
                        iSessionCallback.onError((byte) 5, 0);
                    }
                };
            }

            @Override // android.hardware.biometrics.face.ISession
            public void enumerateEnrollments() throws RemoteException {
                Slog.w("face.aidl.TestHal", "enumerateEnrollments");
                iSessionCallback.onEnrollmentsEnumerated(new int[0]);
            }

            @Override // android.hardware.biometrics.face.ISession
            public void removeEnrollments(int[] iArr) throws RemoteException {
                Slog.w("face.aidl.TestHal", "removeEnrollments");
                iSessionCallback.onEnrollmentsRemoved(iArr);
            }

            @Override // android.hardware.biometrics.face.ISession
            public void getFeatures() throws RemoteException {
                Slog.w("face.aidl.TestHal", "getFeatures");
                iSessionCallback.onFeaturesRetrieved(new byte[0]);
            }

            @Override // android.hardware.biometrics.face.ISession
            public void setFeature(HardwareAuthToken hardwareAuthToken, byte b, boolean z) throws RemoteException {
                Slog.w("face.aidl.TestHal", "setFeature");
                iSessionCallback.onFeatureSet(b);
            }

            @Override // android.hardware.biometrics.face.ISession
            public void getAuthenticatorId() throws RemoteException {
                Slog.w("face.aidl.TestHal", "getAuthenticatorId");
                iSessionCallback.onAuthenticatorIdRetrieved(0L);
            }

            @Override // android.hardware.biometrics.face.ISession
            public void invalidateAuthenticatorId() throws RemoteException {
                Slog.w("face.aidl.TestHal", "invalidateAuthenticatorId");
                iSessionCallback.onAuthenticatorIdInvalidated(0L);
            }

            @Override // android.hardware.biometrics.face.ISession
            public void resetLockout(HardwareAuthToken hardwareAuthToken) throws RemoteException {
                Slog.w("face.aidl.TestHal", "resetLockout");
                iSessionCallback.onLockoutCleared();
            }

            @Override // android.hardware.biometrics.face.ISession
            public void close() throws RemoteException {
                Slog.w("face.aidl.TestHal", "close");
                iSessionCallback.onSessionClosed();
            }

            @Override // android.hardware.biometrics.face.ISession
            public ICancellationSignal authenticateWithContext(long j, OperationContext operationContext) {
                return authenticate(j);
            }

            @Override // android.hardware.biometrics.face.ISession
            public ICancellationSignal enrollWithContext(HardwareAuthToken hardwareAuthToken, byte b, byte[] bArr, NativeHandle nativeHandle, OperationContext operationContext) {
                return enroll(hardwareAuthToken, b, bArr, nativeHandle);
            }

            @Override // android.hardware.biometrics.face.ISession
            public ICancellationSignal detectInteractionWithContext(OperationContext operationContext) {
                return detectInteraction();
            }

            @Override // android.hardware.biometrics.face.ISession
            public void onContextChanged(OperationContext operationContext) {
                Slog.w("face.aidl.TestHal", "onContextChanged");
            }
        };
    }
}
