package com.android.server.biometrics.sensors.iris;

import android.hardware.biometrics.IBiometricAuthenticator;
import android.hardware.biometrics.IBiometricSensorReceiver;
import android.hardware.biometrics.IInvalidationCallback;
import android.hardware.biometrics.ITestSession;
import android.hardware.biometrics.ITestSessionCallback;
import android.hardware.biometrics.SensorPropertiesInternal;
import android.hardware.iris.IIrisService;
import android.os.IBinder;
import android.os.RemoteException;
/* loaded from: classes.dex */
public final class IrisAuthenticator extends IBiometricAuthenticator.Stub {
    public final IIrisService mIrisService;

    public void cancelAuthenticationFromService(IBinder iBinder, String str, long j) throws RemoteException {
    }

    public ITestSession createTestSession(ITestSessionCallback iTestSessionCallback, String str) throws RemoteException {
        return null;
    }

    public byte[] dumpSensorServiceStateProto(boolean z) throws RemoteException {
        return null;
    }

    public long getAuthenticatorId(int i) throws RemoteException {
        return 0L;
    }

    public int getLockoutModeForUser(int i) throws RemoteException {
        return 0;
    }

    public SensorPropertiesInternal getSensorProperties(String str) throws RemoteException {
        return null;
    }

    public boolean hasEnrolledTemplates(int i, String str) throws RemoteException {
        return false;
    }

    public void invalidateAuthenticatorId(int i, IInvalidationCallback iInvalidationCallback) {
    }

    public boolean isHardwareDetected(String str) throws RemoteException {
        return false;
    }

    public void prepareForAuthentication(boolean z, IBinder iBinder, long j, int i, IBiometricSensorReceiver iBiometricSensorReceiver, String str, long j2, int i2, boolean z2) throws RemoteException {
    }

    public void resetLockout(IBinder iBinder, String str, int i, byte[] bArr) throws RemoteException {
    }

    public void startPreparedClient(int i) throws RemoteException {
    }

    public IrisAuthenticator(IIrisService iIrisService, int i) {
        this.mIrisService = iIrisService;
    }
}
