package com.android.server.biometrics.sensors.fingerprint.hidl;

import android.content.Context;
import com.android.server.biometrics.log.BiometricContext;
import com.android.server.biometrics.log.BiometricLogger;
import com.android.server.biometrics.sensors.BaseClientMonitor;
import com.android.server.biometrics.sensors.ClientMonitorCallback;
/* loaded from: classes.dex */
public class FingerprintResetLockoutClient extends BaseClientMonitor {
    public final LockoutFrameworkImpl mLockoutTracker;

    @Override // com.android.server.biometrics.sensors.BaseClientMonitor
    public int getProtoEnum() {
        return 12;
    }

    @Override // com.android.server.biometrics.sensors.BaseClientMonitor
    public boolean interruptsPrecedingClients() {
        return true;
    }

    public FingerprintResetLockoutClient(Context context, int i, String str, int i2, BiometricLogger biometricLogger, BiometricContext biometricContext, LockoutFrameworkImpl lockoutFrameworkImpl) {
        super(context, null, null, i, str, 0, i2, biometricLogger, biometricContext);
        this.mLockoutTracker = lockoutFrameworkImpl;
    }

    @Override // com.android.server.biometrics.sensors.BaseClientMonitor
    public void start(ClientMonitorCallback clientMonitorCallback) {
        super.start(clientMonitorCallback);
        this.mLockoutTracker.resetFailedAttemptsForUser(true, getTargetUserId());
        clientMonitorCallback.onClientFinished(this, true);
    }
}
