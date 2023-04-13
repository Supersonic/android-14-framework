package com.android.server.biometrics.sensors.face.hidl;

import android.content.Context;
import android.hardware.biometrics.face.V1_0.IBiometricsFace;
import android.hardware.face.Face;
import android.os.IBinder;
import com.android.server.biometrics.log.BiometricContext;
import com.android.server.biometrics.log.BiometricLogger;
import com.android.server.biometrics.sensors.BiometricUtils;
import com.android.server.biometrics.sensors.InternalCleanupClient;
import com.android.server.biometrics.sensors.InternalEnumerateClient;
import com.android.server.biometrics.sensors.RemovalClient;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
/* loaded from: classes.dex */
public class FaceInternalCleanupClient extends InternalCleanupClient<Face, IBiometricsFace> {
    public FaceInternalCleanupClient(Context context, Supplier<IBiometricsFace> supplier, int i, String str, int i2, BiometricLogger biometricLogger, BiometricContext biometricContext, BiometricUtils<Face> biometricUtils, Map<Integer, Long> map) {
        super(context, supplier, i, str, i2, biometricLogger, biometricContext, biometricUtils, map);
    }

    @Override // com.android.server.biometrics.sensors.InternalCleanupClient
    public InternalEnumerateClient<IBiometricsFace> getEnumerateClient(Context context, Supplier<IBiometricsFace> supplier, IBinder iBinder, int i, String str, List<Face> list, BiometricUtils<Face> biometricUtils, int i2, BiometricLogger biometricLogger, BiometricContext biometricContext) {
        return new FaceInternalEnumerateClient(context, supplier, iBinder, i, str, list, biometricUtils, i2, biometricLogger, biometricContext);
    }

    @Override // com.android.server.biometrics.sensors.InternalCleanupClient
    public RemovalClient<Face, IBiometricsFace> getRemovalClient(Context context, Supplier<IBiometricsFace> supplier, IBinder iBinder, int i, int i2, String str, BiometricUtils<Face> biometricUtils, int i3, BiometricLogger biometricLogger, BiometricContext biometricContext, Map<Integer, Long> map) {
        return new FaceRemovalClient(context, supplier, iBinder, null, i, i2, str, biometricUtils, i3, biometricLogger, biometricContext, map);
    }
}
