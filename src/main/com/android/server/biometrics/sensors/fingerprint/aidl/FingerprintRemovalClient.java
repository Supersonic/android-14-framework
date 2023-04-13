package com.android.server.biometrics.sensors.fingerprint.aidl;

import android.content.Context;
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
public class FingerprintRemovalClient extends RemovalClient<Fingerprint, AidlSession> {
    public final int[] mBiometricIds;

    public FingerprintRemovalClient(Context context, Supplier<AidlSession> supplier, IBinder iBinder, ClientMonitorCallbackConverter clientMonitorCallbackConverter, int[] iArr, int i, String str, BiometricUtils<Fingerprint> biometricUtils, int i2, BiometricLogger biometricLogger, BiometricContext biometricContext, Map<Integer, Long> map) {
        super(context, supplier, iBinder, clientMonitorCallbackConverter, i, str, biometricUtils, i2, biometricLogger, biometricContext, map);
        this.mBiometricIds = iArr;
    }

    @Override // com.android.server.biometrics.sensors.HalClientMonitor
    public void startHalOperation() {
        try {
            getFreshDaemon().getSession().removeEnrollments(this.mBiometricIds);
        } catch (RemoteException e) {
            Slog.e("FingerprintRemovalClient", "Remote exception when requesting remove", e);
            this.mCallback.onClientFinished(this, false);
        }
    }
}
