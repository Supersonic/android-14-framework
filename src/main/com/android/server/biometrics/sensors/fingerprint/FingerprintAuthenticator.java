package com.android.server.biometrics.sensors.fingerprint;

import android.hardware.biometrics.IBiometricAuthenticator;
import android.hardware.biometrics.IBiometricSensorReceiver;
import android.hardware.biometrics.IInvalidationCallback;
import android.hardware.biometrics.ITestSession;
import android.hardware.biometrics.ITestSessionCallback;
import android.hardware.biometrics.SensorPropertiesInternal;
import android.hardware.fingerprint.FingerprintAuthenticateOptions;
import android.hardware.fingerprint.IFingerprintService;
import android.os.IBinder;
import android.os.RemoteException;
/* loaded from: classes.dex */
public final class FingerprintAuthenticator extends IBiometricAuthenticator.Stub {
    public final IFingerprintService mFingerprintService;
    public final int mSensorId;

    public FingerprintAuthenticator(IFingerprintService iFingerprintService, int i) {
        this.mFingerprintService = iFingerprintService;
        this.mSensorId = i;
    }

    public ITestSession createTestSession(ITestSessionCallback iTestSessionCallback, String str) throws RemoteException {
        return this.mFingerprintService.createTestSession(this.mSensorId, iTestSessionCallback, str);
    }

    public SensorPropertiesInternal getSensorProperties(String str) throws RemoteException {
        return this.mFingerprintService.getSensorProperties(this.mSensorId, str);
    }

    public byte[] dumpSensorServiceStateProto(boolean z) throws RemoteException {
        return this.mFingerprintService.dumpSensorServiceStateProto(this.mSensorId, z);
    }

    public void prepareForAuthentication(boolean z, IBinder iBinder, long j, int i, IBiometricSensorReceiver iBiometricSensorReceiver, String str, long j2, int i2, boolean z2) throws RemoteException {
        this.mFingerprintService.prepareForAuthentication(iBinder, j, iBiometricSensorReceiver, new FingerprintAuthenticateOptions.Builder().setSensorId(this.mSensorId).setUserId(i).setOpPackageName(str).build(), j2, i2, z2);
    }

    public void startPreparedClient(int i) throws RemoteException {
        this.mFingerprintService.startPreparedClient(this.mSensorId, i);
    }

    public void cancelAuthenticationFromService(IBinder iBinder, String str, long j) throws RemoteException {
        this.mFingerprintService.cancelAuthenticationFromService(this.mSensorId, iBinder, str, j);
    }

    public boolean isHardwareDetected(String str) throws RemoteException {
        return this.mFingerprintService.isHardwareDetected(this.mSensorId, str);
    }

    public boolean hasEnrolledTemplates(int i, String str) throws RemoteException {
        return this.mFingerprintService.hasEnrolledFingerprints(this.mSensorId, i, str);
    }

    public int getLockoutModeForUser(int i) throws RemoteException {
        return this.mFingerprintService.getLockoutModeForUser(this.mSensorId, i);
    }

    public void invalidateAuthenticatorId(int i, IInvalidationCallback iInvalidationCallback) throws RemoteException {
        this.mFingerprintService.invalidateAuthenticatorId(this.mSensorId, i, iInvalidationCallback);
    }

    public long getAuthenticatorId(int i) throws RemoteException {
        return this.mFingerprintService.getAuthenticatorId(this.mSensorId, i);
    }

    public void resetLockout(IBinder iBinder, String str, int i, byte[] bArr) throws RemoteException {
        this.mFingerprintService.resetLockout(iBinder, this.mSensorId, i, bArr, str);
    }
}
