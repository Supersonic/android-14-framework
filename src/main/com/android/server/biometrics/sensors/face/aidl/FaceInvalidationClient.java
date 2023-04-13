package com.android.server.biometrics.sensors.face.aidl;

import android.content.Context;
import android.hardware.biometrics.IInvalidationCallback;
import android.hardware.face.Face;
import android.os.RemoteException;
import android.util.Slog;
import com.android.server.biometrics.log.BiometricContext;
import com.android.server.biometrics.log.BiometricLogger;
import com.android.server.biometrics.sensors.InvalidationClient;
import java.util.Map;
import java.util.function.Supplier;
/* loaded from: classes.dex */
public class FaceInvalidationClient extends InvalidationClient<Face, AidlSession> {
    public FaceInvalidationClient(Context context, Supplier<AidlSession> supplier, int i, int i2, BiometricLogger biometricLogger, BiometricContext biometricContext, Map<Integer, Long> map, IInvalidationCallback iInvalidationCallback) {
        super(context, supplier, i, i2, biometricLogger, biometricContext, map, iInvalidationCallback);
    }

    @Override // com.android.server.biometrics.sensors.HalClientMonitor
    public void startHalOperation() {
        try {
            getFreshDaemon().getSession().invalidateAuthenticatorId();
        } catch (RemoteException e) {
            Slog.e("FaceInvalidationClient", "Remote exception", e);
            this.mCallback.onClientFinished(this, false);
        }
    }
}
