package com.android.server.biometrics.sensors.face.hidl;

import android.content.Context;
import android.hardware.biometrics.face.V1_0.IBiometricsFace;
import android.os.Environment;
import android.os.RemoteException;
import android.util.Slog;
import com.android.server.biometrics.log.BiometricContext;
import com.android.server.biometrics.log.BiometricLogger;
import com.android.server.biometrics.sensors.ClientMonitorCallback;
import com.android.server.biometrics.sensors.HalClientMonitor;
import java.io.File;
import java.util.Map;
import java.util.function.Supplier;
/* loaded from: classes.dex */
public class FaceUpdateActiveUserClient extends HalClientMonitor<IBiometricsFace> {
    public final Map<Integer, Long> mAuthenticatorIds;
    public final boolean mHasEnrolledBiometrics;

    @Override // com.android.server.biometrics.sensors.BaseClientMonitor
    public int getProtoEnum() {
        return 1;
    }

    @Override // com.android.server.biometrics.sensors.HalClientMonitor
    public void unableToStart() {
    }

    public FaceUpdateActiveUserClient(Context context, Supplier<IBiometricsFace> supplier, int i, String str, int i2, BiometricLogger biometricLogger, BiometricContext biometricContext, boolean z, Map<Integer, Long> map) {
        super(context, supplier, null, null, i, str, 0, i2, biometricLogger, biometricContext);
        this.mHasEnrolledBiometrics = z;
        this.mAuthenticatorIds = map;
    }

    @Override // com.android.server.biometrics.sensors.BaseClientMonitor
    public void start(ClientMonitorCallback clientMonitorCallback) {
        super.start(clientMonitorCallback);
        startHalOperation();
    }

    @Override // com.android.server.biometrics.sensors.HalClientMonitor
    public void startHalOperation() {
        File file = new File(Environment.getDataVendorDeDirectory(getTargetUserId()), "facedata");
        if (!file.exists()) {
            Slog.e("FaceUpdateActiveUserClient", "vold has not created the directory?");
            this.mCallback.onClientFinished(this, false);
            return;
        }
        try {
            IBiometricsFace freshDaemon = getFreshDaemon();
            freshDaemon.setActiveUser(getTargetUserId(), file.getAbsolutePath());
            this.mAuthenticatorIds.put(Integer.valueOf(getTargetUserId()), Long.valueOf(this.mHasEnrolledBiometrics ? freshDaemon.getAuthenticatorId().value : 0L));
            this.mCallback.onClientFinished(this, true);
        } catch (RemoteException e) {
            Slog.e("FaceUpdateActiveUserClient", "Failed to setActiveUser: " + e);
            this.mCallback.onClientFinished(this, false);
        }
    }
}
