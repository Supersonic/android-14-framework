package com.android.server.biometrics.sensors;

import android.content.Context;
import android.os.IBinder;
import com.android.server.biometrics.log.BiometricContext;
import com.android.server.biometrics.log.BiometricLogger;
import java.util.function.Supplier;
/* loaded from: classes.dex */
public abstract class RevokeChallengeClient<T> extends HalClientMonitor<T> {
    @Override // com.android.server.biometrics.sensors.BaseClientMonitor
    public int getProtoEnum() {
        return 11;
    }

    @Override // com.android.server.biometrics.sensors.HalClientMonitor
    public void unableToStart() {
    }

    public RevokeChallengeClient(Context context, Supplier<T> supplier, IBinder iBinder, int i, String str, int i2, BiometricLogger biometricLogger, BiometricContext biometricContext) {
        super(context, supplier, iBinder, null, i, str, 0, i2, biometricLogger, biometricContext);
    }

    @Override // com.android.server.biometrics.sensors.BaseClientMonitor
    public void start(ClientMonitorCallback clientMonitorCallback) {
        super.start(clientMonitorCallback);
        startHalOperation();
    }
}
