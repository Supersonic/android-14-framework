package com.android.server.biometrics.sensors.fingerprint.aidl;

import android.content.Context;
import android.os.RemoteException;
import android.util.Slog;
import com.android.server.biometrics.log.BiometricContext;
import com.android.server.biometrics.log.BiometricLogger;
import com.android.server.biometrics.sensors.ClientMonitorCallback;
import com.android.server.biometrics.sensors.HalClientMonitor;
import java.util.Map;
import java.util.function.Supplier;
/* loaded from: classes.dex */
public class FingerprintGetAuthenticatorIdClient extends HalClientMonitor<AidlSession> {
    public final Map<Integer, Long> mAuthenticatorIds;

    @Override // com.android.server.biometrics.sensors.BaseClientMonitor
    public int getProtoEnum() {
        return 5;
    }

    @Override // com.android.server.biometrics.sensors.HalClientMonitor
    public void unableToStart() {
    }

    public FingerprintGetAuthenticatorIdClient(Context context, Supplier<AidlSession> supplier, int i, String str, int i2, BiometricLogger biometricLogger, BiometricContext biometricContext, Map<Integer, Long> map) {
        super(context, supplier, null, null, i, str, 0, i2, biometricLogger, biometricContext);
        this.mAuthenticatorIds = map;
    }

    @Override // com.android.server.biometrics.sensors.BaseClientMonitor
    public void start(ClientMonitorCallback clientMonitorCallback) {
        super.start(clientMonitorCallback);
        startHalOperation();
    }

    @Override // com.android.server.biometrics.sensors.HalClientMonitor
    public void startHalOperation() {
        try {
            getFreshDaemon().getSession().getAuthenticatorId();
        } catch (RemoteException e) {
            Slog.e("FingerprintGetAuthenticatorIdClient", "Remote exception", e);
        }
    }

    public void onAuthenticatorIdRetrieved(long j) {
        this.mAuthenticatorIds.put(Integer.valueOf(getTargetUserId()), Long.valueOf(j));
        this.mCallback.onClientFinished(this, true);
    }
}
