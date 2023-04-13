package com.android.server.biometrics.sensors.face.hidl;

import android.content.Context;
import android.hardware.biometrics.face.V1_0.IBiometricsFace;
import android.hardware.face.Face;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Slog;
import com.android.server.biometrics.log.BiometricContext;
import com.android.server.biometrics.log.BiometricLogger;
import com.android.server.biometrics.sensors.BiometricUtils;
import com.android.server.biometrics.sensors.InternalEnumerateClient;
import java.util.List;
import java.util.function.Supplier;
/* loaded from: classes.dex */
public class FaceInternalEnumerateClient extends InternalEnumerateClient<IBiometricsFace> {
    public FaceInternalEnumerateClient(Context context, Supplier<IBiometricsFace> supplier, IBinder iBinder, int i, String str, List<Face> list, BiometricUtils<Face> biometricUtils, int i2, BiometricLogger biometricLogger, BiometricContext biometricContext) {
        super(context, supplier, iBinder, i, str, list, biometricUtils, i2, biometricLogger, biometricContext);
    }

    @Override // com.android.server.biometrics.sensors.HalClientMonitor
    public void startHalOperation() {
        try {
            getFreshDaemon().enumerate();
        } catch (RemoteException e) {
            Slog.e("FaceInternalEnumerateClient", "Remote exception when requesting enumerate", e);
            this.mCallback.onClientFinished(this, false);
        }
    }
}
