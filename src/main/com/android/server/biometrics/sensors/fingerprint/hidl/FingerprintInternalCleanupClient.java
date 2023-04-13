package com.android.server.biometrics.sensors.fingerprint.hidl;

import android.content.Context;
import android.hardware.biometrics.fingerprint.V2_1.IBiometricsFingerprint;
import android.hardware.fingerprint.Fingerprint;
import android.os.IBinder;
import com.android.server.biometrics.log.BiometricContext;
import com.android.server.biometrics.log.BiometricLogger;
import com.android.server.biometrics.sensors.BiometricUtils;
import com.android.server.biometrics.sensors.InternalCleanupClient;
import com.android.server.biometrics.sensors.InternalEnumerateClient;
import com.android.server.biometrics.sensors.RemovalClient;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
/* loaded from: classes.dex */
public class FingerprintInternalCleanupClient extends InternalCleanupClient<Fingerprint, IBiometricsFingerprint> {
    public FingerprintInternalCleanupClient(Context context, Supplier<IBiometricsFingerprint> supplier, int i, String str, int i2, BiometricLogger biometricLogger, BiometricContext biometricContext, BiometricUtils<Fingerprint> biometricUtils, Map<Integer, Long> map) {
        super(context, supplier, i, str, i2, biometricLogger, biometricContext, biometricUtils, map);
    }

    @Override // com.android.server.biometrics.sensors.InternalCleanupClient
    public InternalEnumerateClient<IBiometricsFingerprint> getEnumerateClient(Context context, Supplier<IBiometricsFingerprint> supplier, IBinder iBinder, int i, String str, List<Fingerprint> list, BiometricUtils<Fingerprint> biometricUtils, int i2, BiometricLogger biometricLogger, BiometricContext biometricContext) {
        return new FingerprintInternalEnumerateClient(context, supplier, iBinder, i, str, list, biometricUtils, i2, biometricLogger, biometricContext);
    }

    @Override // com.android.server.biometrics.sensors.InternalCleanupClient
    public RemovalClient<Fingerprint, IBiometricsFingerprint> getRemovalClient(Context context, Supplier<IBiometricsFingerprint> supplier, IBinder iBinder, int i, int i2, String str, BiometricUtils<Fingerprint> biometricUtils, int i3, BiometricLogger biometricLogger, BiometricContext biometricContext, Map<Integer, Long> map) {
        return new FingerprintRemovalClient(context, supplier, iBinder, null, i, i2, str, biometricUtils, i3, biometricLogger, biometricContext, map);
    }
}
