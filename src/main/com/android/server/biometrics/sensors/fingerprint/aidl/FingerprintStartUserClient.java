package com.android.server.biometrics.sensors.fingerprint.aidl;

import android.content.Context;
import android.hardware.biometrics.fingerprint.IFingerprint;
import android.hardware.biometrics.fingerprint.ISession;
import android.hardware.biometrics.fingerprint.ISessionCallback;
import android.os.Binder;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Slog;
import com.android.server.biometrics.log.BiometricContext;
import com.android.server.biometrics.log.BiometricLogger;
import com.android.server.biometrics.sensors.ClientMonitorCallback;
import com.android.server.biometrics.sensors.StartUserClient;
import java.util.function.Supplier;
/* loaded from: classes.dex */
public class FingerprintStartUserClient extends StartUserClient<IFingerprint, ISession> {
    public final ISessionCallback mSessionCallback;

    @Override // com.android.server.biometrics.sensors.HalClientMonitor
    public void unableToStart() {
    }

    public FingerprintStartUserClient(Context context, Supplier<IFingerprint> supplier, IBinder iBinder, int i, int i2, BiometricLogger biometricLogger, BiometricContext biometricContext, ISessionCallback iSessionCallback, StartUserClient.UserStartedCallback<ISession> userStartedCallback) {
        super(context, supplier, iBinder, i, i2, biometricLogger, biometricContext, userStartedCallback);
        this.mSessionCallback = iSessionCallback;
    }

    @Override // com.android.server.biometrics.sensors.BaseClientMonitor
    public void start(ClientMonitorCallback clientMonitorCallback) {
        super.start(clientMonitorCallback);
        startHalOperation();
    }

    @Override // com.android.server.biometrics.sensors.HalClientMonitor
    public void startHalOperation() {
        try {
            IFingerprint freshDaemon = getFreshDaemon();
            int interfaceVersion = freshDaemon.getInterfaceVersion();
            ISession createSession = freshDaemon.createSession(getSensorId(), getTargetUserId(), this.mSessionCallback);
            Binder.allowBlocking(createSession.asBinder());
            this.mUserStartedCallback.onUserStarted(getTargetUserId(), createSession, interfaceVersion);
            getCallback().onClientFinished(this, true);
        } catch (RemoteException e) {
            Slog.e("FingerprintStartUserClient", "Remote exception", e);
            getCallback().onClientFinished(this, false);
        }
    }
}
