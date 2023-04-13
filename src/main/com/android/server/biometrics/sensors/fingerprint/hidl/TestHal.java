package com.android.server.biometrics.sensors.fingerprint.hidl;

import android.content.Context;
import android.hardware.biometrics.fingerprint.V2_1.IBiometricsFingerprintClientCallback;
import android.hardware.biometrics.fingerprint.V2_3.IBiometricsFingerprint;
import android.hardware.fingerprint.Fingerprint;
import android.os.RemoteException;
import android.util.Slog;
import com.android.server.biometrics.sensors.fingerprint.FingerprintUtils;
import java.util.List;
/* loaded from: classes.dex */
public class TestHal extends IBiometricsFingerprint.Stub {
    public IBiometricsFingerprintClientCallback mCallback;
    public final Context mContext;
    public final int mSensorId;

    @Override // android.hardware.biometrics.fingerprint.V2_1.IBiometricsFingerprint
    public long getAuthenticatorId() {
        return 0L;
    }

    @Override // android.hardware.biometrics.fingerprint.V2_3.IBiometricsFingerprint
    public boolean isUdfps(int i) {
        return false;
    }

    @Override // android.hardware.biometrics.fingerprint.V2_3.IBiometricsFingerprint
    public void onFingerDown(int i, int i2, float f, float f2) {
    }

    @Override // android.hardware.biometrics.fingerprint.V2_3.IBiometricsFingerprint
    public void onFingerUp() {
    }

    @Override // android.hardware.biometrics.fingerprint.V2_1.IBiometricsFingerprint
    public int postEnroll() {
        return 0;
    }

    @Override // android.hardware.biometrics.fingerprint.V2_1.IBiometricsFingerprint
    public long preEnroll() {
        return 0L;
    }

    @Override // android.hardware.biometrics.fingerprint.V2_1.IBiometricsFingerprint
    public int setActiveGroup(int i, String str) {
        return 0;
    }

    public TestHal(Context context, int i) {
        this.mContext = context;
        this.mSensorId = i;
    }

    @Override // android.hardware.biometrics.fingerprint.V2_1.IBiometricsFingerprint
    public long setNotify(IBiometricsFingerprintClientCallback iBiometricsFingerprintClientCallback) {
        this.mCallback = iBiometricsFingerprintClientCallback;
        return 0L;
    }

    @Override // android.hardware.biometrics.fingerprint.V2_1.IBiometricsFingerprint
    public int enroll(byte[] bArr, int i, int i2) {
        Slog.w("fingerprint.hidl.TestHal", "enroll");
        return 0;
    }

    @Override // android.hardware.biometrics.fingerprint.V2_1.IBiometricsFingerprint
    public int cancel() throws RemoteException {
        IBiometricsFingerprintClientCallback iBiometricsFingerprintClientCallback = this.mCallback;
        if (iBiometricsFingerprintClientCallback != null) {
            iBiometricsFingerprintClientCallback.onError(0L, 5, 0);
        }
        return 0;
    }

    @Override // android.hardware.biometrics.fingerprint.V2_1.IBiometricsFingerprint
    public int enumerate() throws RemoteException {
        Slog.w("fingerprint.hidl.TestHal", "Enumerate");
        IBiometricsFingerprintClientCallback iBiometricsFingerprintClientCallback = this.mCallback;
        if (iBiometricsFingerprintClientCallback != null) {
            iBiometricsFingerprintClientCallback.onEnumerate(0L, 0, 0, 0);
            return 0;
        }
        return 0;
    }

    @Override // android.hardware.biometrics.fingerprint.V2_1.IBiometricsFingerprint
    public int remove(int i, int i2) throws RemoteException {
        Slog.w("fingerprint.hidl.TestHal", "Remove");
        IBiometricsFingerprintClientCallback iBiometricsFingerprintClientCallback = this.mCallback;
        if (iBiometricsFingerprintClientCallback != null) {
            if (i2 == 0) {
                List<Fingerprint> biometricsForUser = FingerprintUtils.getInstance(this.mSensorId).getBiometricsForUser(this.mContext, i);
                for (int i3 = 0; i3 < biometricsForUser.size(); i3++) {
                    this.mCallback.onRemoved(0L, biometricsForUser.get(i3).getBiometricId(), i, (biometricsForUser.size() - i3) - 1);
                }
            } else {
                iBiometricsFingerprintClientCallback.onRemoved(0L, i2, i, 0);
            }
        }
        return 0;
    }

    @Override // android.hardware.biometrics.fingerprint.V2_1.IBiometricsFingerprint
    public int authenticate(long j, int i) {
        Slog.w("fingerprint.hidl.TestHal", "Authenticate");
        return 0;
    }
}
