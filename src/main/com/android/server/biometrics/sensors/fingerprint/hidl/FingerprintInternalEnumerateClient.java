package com.android.server.biometrics.sensors.fingerprint.hidl;

import android.content.Context;
import android.hardware.biometrics.fingerprint.V2_1.IBiometricsFingerprint;
import android.hardware.fingerprint.Fingerprint;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Slog;
import com.android.server.biometrics.log.BiometricContext;
import com.android.server.biometrics.log.BiometricLogger;
import com.android.server.biometrics.sensors.BiometricUtils;
import com.android.server.biometrics.sensors.InternalEnumerateClient;
import java.util.List;
import java.util.function.Supplier;
/* loaded from: classes.dex */
public class FingerprintInternalEnumerateClient extends InternalEnumerateClient<IBiometricsFingerprint> {
    public FingerprintInternalEnumerateClient(Context context, Supplier<IBiometricsFingerprint> supplier, IBinder iBinder, int i, String str, List<Fingerprint> list, BiometricUtils<Fingerprint> biometricUtils, int i2, BiometricLogger biometricLogger, BiometricContext biometricContext) {
        super(context, supplier, iBinder, i, str, list, biometricUtils, i2, biometricLogger, biometricContext);
    }

    @Override // com.android.server.biometrics.sensors.HalClientMonitor
    public void startHalOperation() {
        try {
            getFreshDaemon().enumerate();
        } catch (RemoteException e) {
            Slog.e("FingerprintInternalEnumerateClient", "Remote exception when requesting enumerate", e);
            this.mCallback.onClientFinished(this, false);
        }
    }
}
