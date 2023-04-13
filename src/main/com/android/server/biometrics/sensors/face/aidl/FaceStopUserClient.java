package com.android.server.biometrics.sensors.face.aidl;

import android.content.Context;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Slog;
import com.android.server.biometrics.log.BiometricContext;
import com.android.server.biometrics.log.BiometricLogger;
import com.android.server.biometrics.sensors.ClientMonitorCallback;
import com.android.server.biometrics.sensors.StopUserClient;
import java.util.function.Supplier;
/* loaded from: classes.dex */
public class FaceStopUserClient extends StopUserClient<AidlSession> {
    @Override // com.android.server.biometrics.sensors.HalClientMonitor
    public void unableToStart() {
    }

    public FaceStopUserClient(Context context, Supplier<AidlSession> supplier, IBinder iBinder, int i, int i2, BiometricLogger biometricLogger, BiometricContext biometricContext, StopUserClient.UserStoppedCallback userStoppedCallback) {
        super(context, supplier, iBinder, i, i2, biometricLogger, biometricContext, userStoppedCallback);
    }

    @Override // com.android.server.biometrics.sensors.BaseClientMonitor
    public void start(ClientMonitorCallback clientMonitorCallback) {
        super.start(clientMonitorCallback);
        startHalOperation();
    }

    @Override // com.android.server.biometrics.sensors.HalClientMonitor
    public void startHalOperation() {
        try {
            getFreshDaemon().getSession().close();
        } catch (RemoteException e) {
            Slog.e("FaceStopUserClient", "Remote exception", e);
            getCallback().onClientFinished(this, false);
        }
    }
}
