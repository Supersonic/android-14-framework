package com.android.server.biometrics.sensors;

import android.hardware.biometrics.BiometricAuthenticator;
import android.hardware.biometrics.IBiometricSensorReceiver;
import android.hardware.face.Face;
import android.hardware.face.FaceAuthenticationFrame;
import android.hardware.face.FaceEnrollFrame;
import android.hardware.face.IFaceServiceReceiver;
import android.hardware.fingerprint.Fingerprint;
import android.hardware.fingerprint.IFingerprintServiceReceiver;
import android.os.RemoteException;
/* loaded from: classes.dex */
public class ClientMonitorCallbackConverter {
    public IFaceServiceReceiver mFaceServiceReceiver;
    public IFingerprintServiceReceiver mFingerprintServiceReceiver;
    public IBiometricSensorReceiver mSensorReceiver;

    public ClientMonitorCallbackConverter(IBiometricSensorReceiver iBiometricSensorReceiver) {
        this.mSensorReceiver = iBiometricSensorReceiver;
    }

    public ClientMonitorCallbackConverter(IFaceServiceReceiver iFaceServiceReceiver) {
        this.mFaceServiceReceiver = iFaceServiceReceiver;
    }

    public ClientMonitorCallbackConverter(IFingerprintServiceReceiver iFingerprintServiceReceiver) {
        this.mFingerprintServiceReceiver = iFingerprintServiceReceiver;
    }

    public void onAcquired(int i, int i2, int i3) throws RemoteException {
        IBiometricSensorReceiver iBiometricSensorReceiver = this.mSensorReceiver;
        if (iBiometricSensorReceiver != null) {
            iBiometricSensorReceiver.onAcquired(i, i2, i3);
            return;
        }
        IFaceServiceReceiver iFaceServiceReceiver = this.mFaceServiceReceiver;
        if (iFaceServiceReceiver != null) {
            iFaceServiceReceiver.onAcquired(i2, i3);
            return;
        }
        IFingerprintServiceReceiver iFingerprintServiceReceiver = this.mFingerprintServiceReceiver;
        if (iFingerprintServiceReceiver != null) {
            iFingerprintServiceReceiver.onAcquired(i2, i3);
        }
    }

    public void onAuthenticationSucceeded(int i, BiometricAuthenticator.Identifier identifier, byte[] bArr, int i2, boolean z) throws RemoteException {
        IBiometricSensorReceiver iBiometricSensorReceiver = this.mSensorReceiver;
        if (iBiometricSensorReceiver != null) {
            iBiometricSensorReceiver.onAuthenticationSucceeded(i, bArr);
            return;
        }
        IFaceServiceReceiver iFaceServiceReceiver = this.mFaceServiceReceiver;
        if (iFaceServiceReceiver != null) {
            iFaceServiceReceiver.onAuthenticationSucceeded((Face) identifier, i2, z);
            return;
        }
        IFingerprintServiceReceiver iFingerprintServiceReceiver = this.mFingerprintServiceReceiver;
        if (iFingerprintServiceReceiver != null) {
            iFingerprintServiceReceiver.onAuthenticationSucceeded((Fingerprint) identifier, i2, z);
        }
    }

    public void onAuthenticationFailed(int i) throws RemoteException {
        IBiometricSensorReceiver iBiometricSensorReceiver = this.mSensorReceiver;
        if (iBiometricSensorReceiver != null) {
            iBiometricSensorReceiver.onAuthenticationFailed(i);
            return;
        }
        IFaceServiceReceiver iFaceServiceReceiver = this.mFaceServiceReceiver;
        if (iFaceServiceReceiver != null) {
            iFaceServiceReceiver.onAuthenticationFailed();
            return;
        }
        IFingerprintServiceReceiver iFingerprintServiceReceiver = this.mFingerprintServiceReceiver;
        if (iFingerprintServiceReceiver != null) {
            iFingerprintServiceReceiver.onAuthenticationFailed();
        }
    }

    public void onError(int i, int i2, int i3, int i4) throws RemoteException {
        IBiometricSensorReceiver iBiometricSensorReceiver = this.mSensorReceiver;
        if (iBiometricSensorReceiver != null) {
            iBiometricSensorReceiver.onError(i, i2, i3, i4);
            return;
        }
        IFaceServiceReceiver iFaceServiceReceiver = this.mFaceServiceReceiver;
        if (iFaceServiceReceiver != null) {
            iFaceServiceReceiver.onError(i3, i4);
            return;
        }
        IFingerprintServiceReceiver iFingerprintServiceReceiver = this.mFingerprintServiceReceiver;
        if (iFingerprintServiceReceiver != null) {
            iFingerprintServiceReceiver.onError(i3, i4);
        }
    }

    public void onDetected(int i, int i2, boolean z) throws RemoteException {
        IFaceServiceReceiver iFaceServiceReceiver = this.mFaceServiceReceiver;
        if (iFaceServiceReceiver != null) {
            iFaceServiceReceiver.onFaceDetected(i, i2, z);
            return;
        }
        IFingerprintServiceReceiver iFingerprintServiceReceiver = this.mFingerprintServiceReceiver;
        if (iFingerprintServiceReceiver != null) {
            iFingerprintServiceReceiver.onFingerprintDetected(i, i2, z);
        }
    }

    public void onEnrollResult(BiometricAuthenticator.Identifier identifier, int i) throws RemoteException {
        IFaceServiceReceiver iFaceServiceReceiver = this.mFaceServiceReceiver;
        if (iFaceServiceReceiver != null) {
            iFaceServiceReceiver.onEnrollResult((Face) identifier, i);
            return;
        }
        IFingerprintServiceReceiver iFingerprintServiceReceiver = this.mFingerprintServiceReceiver;
        if (iFingerprintServiceReceiver != null) {
            iFingerprintServiceReceiver.onEnrollResult((Fingerprint) identifier, i);
        }
    }

    public void onRemoved(BiometricAuthenticator.Identifier identifier, int i) throws RemoteException {
        IFaceServiceReceiver iFaceServiceReceiver = this.mFaceServiceReceiver;
        if (iFaceServiceReceiver != null) {
            iFaceServiceReceiver.onRemoved((Face) identifier, i);
            return;
        }
        IFingerprintServiceReceiver iFingerprintServiceReceiver = this.mFingerprintServiceReceiver;
        if (iFingerprintServiceReceiver != null) {
            iFingerprintServiceReceiver.onRemoved((Fingerprint) identifier, i);
        }
    }

    public void onChallengeGenerated(int i, int i2, long j) throws RemoteException {
        IFaceServiceReceiver iFaceServiceReceiver = this.mFaceServiceReceiver;
        if (iFaceServiceReceiver != null) {
            iFaceServiceReceiver.onChallengeGenerated(i, i2, j);
            return;
        }
        IFingerprintServiceReceiver iFingerprintServiceReceiver = this.mFingerprintServiceReceiver;
        if (iFingerprintServiceReceiver != null) {
            iFingerprintServiceReceiver.onChallengeGenerated(i, i2, j);
        }
    }

    public void onFeatureSet(boolean z, int i) throws RemoteException {
        IFaceServiceReceiver iFaceServiceReceiver = this.mFaceServiceReceiver;
        if (iFaceServiceReceiver != null) {
            iFaceServiceReceiver.onFeatureSet(z, i);
        }
    }

    public void onFeatureGet(boolean z, int[] iArr, boolean[] zArr) throws RemoteException {
        IFaceServiceReceiver iFaceServiceReceiver = this.mFaceServiceReceiver;
        if (iFaceServiceReceiver != null) {
            iFaceServiceReceiver.onFeatureGet(z, iArr, zArr);
        }
    }

    public void onUdfpsPointerDown(int i) throws RemoteException {
        IFingerprintServiceReceiver iFingerprintServiceReceiver = this.mFingerprintServiceReceiver;
        if (iFingerprintServiceReceiver != null) {
            iFingerprintServiceReceiver.onUdfpsPointerDown(i);
        }
    }

    public void onUdfpsPointerUp(int i) throws RemoteException {
        IFingerprintServiceReceiver iFingerprintServiceReceiver = this.mFingerprintServiceReceiver;
        if (iFingerprintServiceReceiver != null) {
            iFingerprintServiceReceiver.onUdfpsPointerUp(i);
        }
    }

    public void onAuthenticationFrame(FaceAuthenticationFrame faceAuthenticationFrame) throws RemoteException {
        IFaceServiceReceiver iFaceServiceReceiver = this.mFaceServiceReceiver;
        if (iFaceServiceReceiver != null) {
            iFaceServiceReceiver.onAuthenticationFrame(faceAuthenticationFrame);
        }
    }

    public void onEnrollmentFrame(FaceEnrollFrame faceEnrollFrame) throws RemoteException {
        IFaceServiceReceiver iFaceServiceReceiver = this.mFaceServiceReceiver;
        if (iFaceServiceReceiver != null) {
            iFaceServiceReceiver.onEnrollmentFrame(faceEnrollFrame);
        }
    }
}
