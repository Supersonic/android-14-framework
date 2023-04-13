package com.android.server.biometrics.sensors.face.hidl;

import android.content.Context;
import android.hardware.biometrics.face.V1_0.IBiometricsFace;
import android.hardware.biometrics.face.V1_0.OptionalBool;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Slog;
import com.android.server.biometrics.log.BiometricContext;
import com.android.server.biometrics.log.BiometricLogger;
import com.android.server.biometrics.sensors.ClientMonitorCallback;
import com.android.server.biometrics.sensors.ClientMonitorCallbackConverter;
import com.android.server.biometrics.sensors.HalClientMonitor;
import java.util.function.Supplier;
/* loaded from: classes.dex */
public class FaceGetFeatureClient extends HalClientMonitor<IBiometricsFace> {
    public final int mFaceId;
    public final int mFeature;
    public boolean mValue;

    @Override // com.android.server.biometrics.sensors.BaseClientMonitor
    public int getProtoEnum() {
        return 9;
    }

    public FaceGetFeatureClient(Context context, Supplier<IBiometricsFace> supplier, IBinder iBinder, ClientMonitorCallbackConverter clientMonitorCallbackConverter, int i, String str, int i2, BiometricLogger biometricLogger, BiometricContext biometricContext, int i3, int i4) {
        super(context, supplier, iBinder, clientMonitorCallbackConverter, i, str, 0, i2, biometricLogger, biometricContext);
        this.mFeature = i3;
        this.mFaceId = i4;
    }

    @Override // com.android.server.biometrics.sensors.HalClientMonitor
    public void unableToStart() {
        try {
            if (getListener() != null) {
                getListener().onFeatureGet(false, new int[0], new boolean[0]);
            }
        } catch (RemoteException e) {
            Slog.e("FaceGetFeatureClient", "Unable to send error", e);
        }
    }

    @Override // com.android.server.biometrics.sensors.BaseClientMonitor
    public void start(ClientMonitorCallback clientMonitorCallback) {
        super.start(clientMonitorCallback);
        startHalOperation();
    }

    @Override // com.android.server.biometrics.sensors.HalClientMonitor
    public void startHalOperation() {
        try {
            OptionalBool feature = getFreshDaemon().getFeature(this.mFeature, this.mFaceId);
            int[] iArr = {this.mFeature};
            boolean z = feature.value;
            boolean[] zArr = {z};
            this.mValue = z;
            if (getListener() != null) {
                getListener().onFeatureGet(feature.status == 0, iArr, zArr);
            }
            this.mCallback.onClientFinished(this, true);
        } catch (RemoteException e) {
            Slog.e("FaceGetFeatureClient", "Unable to getFeature", e);
            this.mCallback.onClientFinished(this, false);
        }
    }

    public boolean getValue() {
        return this.mValue;
    }
}
