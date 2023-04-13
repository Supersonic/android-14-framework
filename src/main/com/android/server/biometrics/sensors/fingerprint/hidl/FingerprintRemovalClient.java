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
import com.android.server.biometrics.sensors.ClientMonitorCallbackConverter;
import com.android.server.biometrics.sensors.RemovalClient;
import java.util.Map;
import java.util.function.Supplier;
/* loaded from: classes.dex */
public class FingerprintRemovalClient extends RemovalClient<Fingerprint, IBiometricsFingerprint> {
    public final int mBiometricId;

    public FingerprintRemovalClient(Context context, Supplier<IBiometricsFingerprint> supplier, IBinder iBinder, ClientMonitorCallbackConverter clientMonitorCallbackConverter, int i, int i2, String str, BiometricUtils<Fingerprint> biometricUtils, int i3, BiometricLogger biometricLogger, BiometricContext biometricContext, Map<Integer, Long> map) {
        super(context, supplier, iBinder, clientMonitorCallbackConverter, i2, str, biometricUtils, i3, biometricLogger, biometricContext, map);
        this.mBiometricId = i;
    }

    @Override // com.android.server.biometrics.sensors.HalClientMonitor
    public void startHalOperation() {
        try {
            getFreshDaemon().remove(getTargetUserId(), this.mBiometricId);
        } catch (RemoteException e) {
            Slog.e("FingerprintRemovalClient", "Remote exception when requesting remove", e);
            this.mCallback.onClientFinished(this, false);
        }
    }
}
