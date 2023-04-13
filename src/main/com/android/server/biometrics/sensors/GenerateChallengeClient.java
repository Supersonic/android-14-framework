package com.android.server.biometrics.sensors;

import android.content.Context;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Slog;
import com.android.server.biometrics.log.BiometricContext;
import com.android.server.biometrics.log.BiometricLogger;
import java.util.function.Supplier;
/* loaded from: classes.dex */
public abstract class GenerateChallengeClient<T> extends HalClientMonitor<T> {
    @Override // com.android.server.biometrics.sensors.BaseClientMonitor
    public int getProtoEnum() {
        return 10;
    }

    public GenerateChallengeClient(Context context, Supplier<T> supplier, IBinder iBinder, ClientMonitorCallbackConverter clientMonitorCallbackConverter, int i, String str, int i2, BiometricLogger biometricLogger, BiometricContext biometricContext) {
        super(context, supplier, iBinder, clientMonitorCallbackConverter, i, str, 0, i2, biometricLogger, biometricContext);
    }

    @Override // com.android.server.biometrics.sensors.HalClientMonitor
    public void unableToStart() {
        try {
            getListener().onChallengeGenerated(getSensorId(), getTargetUserId(), 0L);
        } catch (RemoteException e) {
            Slog.e("GenerateChallengeClient", "Unable to send error", e);
        }
    }

    @Override // com.android.server.biometrics.sensors.BaseClientMonitor
    public void start(ClientMonitorCallback clientMonitorCallback) {
        super.start(clientMonitorCallback);
        startHalOperation();
    }
}
