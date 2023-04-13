package com.android.server.biometrics.sensors.fingerprint.hidl;

import android.content.Context;
import android.hardware.biometrics.fingerprint.V2_1.IBiometricsFingerprint;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Slog;
import com.android.server.biometrics.log.BiometricContext;
import com.android.server.biometrics.log.BiometricLogger;
import com.android.server.biometrics.sensors.RevokeChallengeClient;
import java.util.function.Supplier;
/* loaded from: classes.dex */
public class FingerprintRevokeChallengeClient extends RevokeChallengeClient<IBiometricsFingerprint> {
    public FingerprintRevokeChallengeClient(Context context, Supplier<IBiometricsFingerprint> supplier, IBinder iBinder, int i, String str, int i2, BiometricLogger biometricLogger, BiometricContext biometricContext) {
        super(context, supplier, iBinder, i, str, i2, biometricLogger, biometricContext);
    }

    @Override // com.android.server.biometrics.sensors.HalClientMonitor
    public void startHalOperation() {
        try {
            getFreshDaemon().postEnroll();
            this.mCallback.onClientFinished(this, true);
        } catch (RemoteException e) {
            Slog.e("FingerprintRevokeChallengeClient", "revokeChallenge/postEnroll failed", e);
            this.mCallback.onClientFinished(this, false);
        }
    }
}
