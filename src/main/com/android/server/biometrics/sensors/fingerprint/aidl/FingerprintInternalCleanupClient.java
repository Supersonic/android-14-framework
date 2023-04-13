package com.android.server.biometrics.sensors.fingerprint.aidl;

import android.content.Context;
import android.hardware.biometrics.BiometricAuthenticator;
import android.hardware.fingerprint.Fingerprint;
import android.os.IBinder;
import com.android.server.biometrics.log.BiometricContext;
import com.android.server.biometrics.log.BiometricLogger;
import com.android.server.biometrics.sensors.BiometricUtils;
import com.android.server.biometrics.sensors.InternalCleanupClient;
import com.android.server.biometrics.sensors.InternalEnumerateClient;
import com.android.server.biometrics.sensors.RemovalClient;
import com.android.server.biometrics.sensors.fingerprint.FingerprintUtils;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
/* loaded from: classes.dex */
public class FingerprintInternalCleanupClient extends InternalCleanupClient<Fingerprint, AidlSession> {
    public FingerprintInternalCleanupClient(Context context, Supplier<AidlSession> supplier, int i, String str, int i2, BiometricLogger biometricLogger, BiometricContext biometricContext, FingerprintUtils fingerprintUtils, Map<Integer, Long> map) {
        super(context, supplier, i, str, i2, biometricLogger, biometricContext, fingerprintUtils, map);
    }

    @Override // com.android.server.biometrics.sensors.InternalCleanupClient
    public InternalEnumerateClient<AidlSession> getEnumerateClient(Context context, Supplier<AidlSession> supplier, IBinder iBinder, int i, String str, List<Fingerprint> list, BiometricUtils<Fingerprint> biometricUtils, int i2, BiometricLogger biometricLogger, BiometricContext biometricContext) {
        return new FingerprintInternalEnumerateClient(context, supplier, iBinder, i, str, list, biometricUtils, i2, biometricLogger.swapAction(context, 3), biometricContext);
    }

    @Override // com.android.server.biometrics.sensors.InternalCleanupClient
    public RemovalClient<Fingerprint, AidlSession> getRemovalClient(Context context, Supplier<AidlSession> supplier, IBinder iBinder, int i, int i2, String str, BiometricUtils<Fingerprint> biometricUtils, int i3, BiometricLogger biometricLogger, BiometricContext biometricContext, Map<Integer, Long> map) {
        return new FingerprintRemovalClient(context, supplier, iBinder, null, new int[]{i}, i2, str, biometricUtils, i3, biometricLogger.swapAction(context, 4), biometricContext, map);
    }

    @Override // com.android.server.biometrics.sensors.InternalCleanupClient
    public void onAddUnknownTemplate(int i, BiometricAuthenticator.Identifier identifier) {
        FingerprintUtils.getInstance(getSensorId()).addBiometricForUser(getContext(), getTargetUserId(), (Fingerprint) identifier);
    }
}
