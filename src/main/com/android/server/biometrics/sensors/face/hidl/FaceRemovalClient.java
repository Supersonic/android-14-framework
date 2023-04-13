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
import com.android.server.biometrics.sensors.ClientMonitorCallbackConverter;
import com.android.server.biometrics.sensors.RemovalClient;
import java.util.Map;
import java.util.function.Supplier;
/* loaded from: classes.dex */
public class FaceRemovalClient extends RemovalClient<Face, IBiometricsFace> {
    public final int mBiometricId;

    public FaceRemovalClient(Context context, Supplier<IBiometricsFace> supplier, IBinder iBinder, ClientMonitorCallbackConverter clientMonitorCallbackConverter, int i, int i2, String str, BiometricUtils<Face> biometricUtils, int i3, BiometricLogger biometricLogger, BiometricContext biometricContext, Map<Integer, Long> map) {
        super(context, supplier, iBinder, clientMonitorCallbackConverter, i2, str, biometricUtils, i3, biometricLogger, biometricContext, map);
        this.mBiometricId = i;
    }

    @Override // com.android.server.biometrics.sensors.HalClientMonitor
    public void startHalOperation() {
        try {
            getFreshDaemon().remove(this.mBiometricId);
        } catch (RemoteException e) {
            Slog.e("FaceRemovalClient", "Remote exception when requesting remove", e);
            this.mCallback.onClientFinished(this, false);
        }
    }
}
