package com.android.server.biometrics.sensors.fingerprint.hidl;

import android.content.Context;
import android.hardware.biometrics.fingerprint.V2_1.IBiometricsFingerprint;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Slog;
import com.android.server.biometrics.log.BiometricContext;
import com.android.server.biometrics.log.BiometricLogger;
import com.android.server.biometrics.sensors.ClientMonitorCallbackConverter;
import com.android.server.biometrics.sensors.GenerateChallengeClient;
import java.util.function.Supplier;
/* loaded from: classes.dex */
public class FingerprintGenerateChallengeClient extends GenerateChallengeClient<IBiometricsFingerprint> {
    public FingerprintGenerateChallengeClient(Context context, Supplier<IBiometricsFingerprint> supplier, IBinder iBinder, ClientMonitorCallbackConverter clientMonitorCallbackConverter, int i, String str, int i2, BiometricLogger biometricLogger, BiometricContext biometricContext) {
        super(context, supplier, iBinder, clientMonitorCallbackConverter, i, str, i2, biometricLogger, biometricContext);
    }

    @Override // com.android.server.biometrics.sensors.HalClientMonitor
    public void startHalOperation() {
        try {
            try {
                getListener().onChallengeGenerated(getSensorId(), getTargetUserId(), getFreshDaemon().preEnroll());
                this.mCallback.onClientFinished(this, true);
            } catch (RemoteException e) {
                Slog.e("FingerprintGenerateChallengeClient", "Remote exception", e);
                this.mCallback.onClientFinished(this, false);
            }
        } catch (RemoteException e2) {
            Slog.e("FingerprintGenerateChallengeClient", "preEnroll failed", e2);
            this.mCallback.onClientFinished(this, false);
        }
    }
}
