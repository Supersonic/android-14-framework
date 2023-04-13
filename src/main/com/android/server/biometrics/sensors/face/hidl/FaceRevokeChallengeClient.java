package com.android.server.biometrics.sensors.face.hidl;

import android.content.Context;
import android.hardware.biometrics.face.V1_0.IBiometricsFace;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Slog;
import com.android.server.biometrics.log.BiometricContext;
import com.android.server.biometrics.log.BiometricLogger;
import com.android.server.biometrics.sensors.RevokeChallengeClient;
import java.util.function.Supplier;
/* loaded from: classes.dex */
public class FaceRevokeChallengeClient extends RevokeChallengeClient<IBiometricsFace> {
    public FaceRevokeChallengeClient(Context context, Supplier<IBiometricsFace> supplier, IBinder iBinder, int i, String str, int i2, BiometricLogger biometricLogger, BiometricContext biometricContext) {
        super(context, supplier, iBinder, i, str, i2, biometricLogger, biometricContext);
    }

    @Override // com.android.server.biometrics.sensors.HalClientMonitor
    public void startHalOperation() {
        try {
            getFreshDaemon().revokeChallenge();
            this.mCallback.onClientFinished(this, true);
        } catch (RemoteException e) {
            Slog.e("FaceRevokeChallengeClient", "revokeChallenge failed", e);
            this.mCallback.onClientFinished(this, false);
        }
    }
}
