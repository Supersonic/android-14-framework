package com.android.server.biometrics.sensors.fingerprint.hidl;

import android.content.Context;
import android.hardware.biometrics.fingerprint.V2_1.IBiometricsFingerprint;
import android.os.Build;
import android.os.Environment;
import android.os.RemoteException;
import android.os.SELinux;
import android.util.Slog;
import com.android.server.biometrics.log.BiometricContext;
import com.android.server.biometrics.log.BiometricLogger;
import com.android.server.biometrics.sensors.ClientMonitorCallback;
import com.android.server.biometrics.sensors.HalClientMonitor;
import java.io.File;
import java.util.Map;
import java.util.function.Supplier;
/* loaded from: classes.dex */
public class FingerprintUpdateActiveUserClient extends HalClientMonitor<IBiometricsFingerprint> {
    public final Map<Integer, Long> mAuthenticatorIds;
    public final Supplier<Integer> mCurrentUserId;
    public File mDirectory;
    public final boolean mForceUpdateAuthenticatorId;
    public final boolean mHasEnrolledBiometrics;

    @Override // com.android.server.biometrics.sensors.BaseClientMonitor
    public int getProtoEnum() {
        return 1;
    }

    @Override // com.android.server.biometrics.sensors.HalClientMonitor
    public void unableToStart() {
    }

    public FingerprintUpdateActiveUserClient(Context context, Supplier<IBiometricsFingerprint> supplier, int i, String str, int i2, BiometricLogger biometricLogger, BiometricContext biometricContext, Supplier<Integer> supplier2, boolean z, Map<Integer, Long> map, boolean z2) {
        super(context, supplier, null, null, i, str, 0, i2, biometricLogger, biometricContext);
        this.mCurrentUserId = supplier2;
        this.mForceUpdateAuthenticatorId = z2;
        this.mHasEnrolledBiometrics = z;
        this.mAuthenticatorIds = map;
    }

    @Override // com.android.server.biometrics.sensors.BaseClientMonitor
    public void start(ClientMonitorCallback clientMonitorCallback) {
        File dataVendorDeDirectory;
        super.start(clientMonitorCallback);
        if (this.mCurrentUserId.get().intValue() == getTargetUserId() && !this.mForceUpdateAuthenticatorId) {
            Slog.d("FingerprintUpdateActiveUserClient", "Already user: " + this.mCurrentUserId + ", returning");
            clientMonitorCallback.onClientFinished(this, true);
            return;
        }
        int i = Build.VERSION.DEVICE_INITIAL_SDK_INT;
        if (i < 1) {
            Slog.e("FingerprintUpdateActiveUserClient", "First SDK version " + i + " is invalid; must be at least VERSION_CODES.BASE");
        }
        if (i <= 27) {
            dataVendorDeDirectory = Environment.getUserSystemDirectory(getTargetUserId());
        } else {
            dataVendorDeDirectory = Environment.getDataVendorDeDirectory(getTargetUserId());
        }
        File file = new File(dataVendorDeDirectory, "fpdata");
        this.mDirectory = file;
        if (!file.exists()) {
            if (!this.mDirectory.mkdir()) {
                Slog.e("FingerprintUpdateActiveUserClient", "Cannot make directory: " + this.mDirectory.getAbsolutePath());
                clientMonitorCallback.onClientFinished(this, false);
                return;
            } else if (!SELinux.restorecon(this.mDirectory)) {
                Slog.e("FingerprintUpdateActiveUserClient", "Restorecons failed. Directory will have wrong label.");
                clientMonitorCallback.onClientFinished(this, false);
                return;
            }
        }
        startHalOperation();
    }

    @Override // com.android.server.biometrics.sensors.HalClientMonitor
    public void startHalOperation() {
        try {
            int targetUserId = getTargetUserId();
            Slog.d("FingerprintUpdateActiveUserClient", "Setting active user: " + targetUserId);
            getFreshDaemon().setActiveGroup(targetUserId, this.mDirectory.getAbsolutePath());
            this.mAuthenticatorIds.put(Integer.valueOf(targetUserId), Long.valueOf(this.mHasEnrolledBiometrics ? getFreshDaemon().getAuthenticatorId() : 0L));
            this.mCallback.onClientFinished(this, true);
        } catch (RemoteException e) {
            Slog.e("FingerprintUpdateActiveUserClient", "Failed to setActiveGroup: " + e);
            this.mCallback.onClientFinished(this, false);
        }
    }
}
