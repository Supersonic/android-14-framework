package com.android.server.biometrics.sensors.face.hidl;

import android.content.Context;
import android.hardware.biometrics.face.V1_0.IBiometricsFace;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Slog;
import com.android.server.biometrics.log.BiometricContext;
import com.android.server.biometrics.log.BiometricLogger;
import com.android.server.biometrics.sensors.ClientMonitorCallback;
import com.android.server.biometrics.sensors.ClientMonitorCallbackConverter;
import com.android.server.biometrics.sensors.HalClientMonitor;
import java.util.ArrayList;
import java.util.function.Supplier;
/* loaded from: classes.dex */
public class FaceSetFeatureClient extends HalClientMonitor<IBiometricsFace> {
    public final boolean mEnabled;
    public final int mFaceId;
    public final int mFeature;
    public final ArrayList<Byte> mHardwareAuthToken;

    @Override // com.android.server.biometrics.sensors.BaseClientMonitor
    public int getProtoEnum() {
        return 8;
    }

    public FaceSetFeatureClient(Context context, Supplier<IBiometricsFace> supplier, IBinder iBinder, ClientMonitorCallbackConverter clientMonitorCallbackConverter, int i, String str, int i2, BiometricLogger biometricLogger, BiometricContext biometricContext, int i3, boolean z, byte[] bArr, int i4) {
        super(context, supplier, iBinder, clientMonitorCallbackConverter, i, str, 0, i2, biometricLogger, biometricContext);
        this.mFeature = i3;
        this.mEnabled = z;
        this.mFaceId = i4;
        this.mHardwareAuthToken = new ArrayList<>();
        for (byte b : bArr) {
            this.mHardwareAuthToken.add(Byte.valueOf(b));
        }
    }

    @Override // com.android.server.biometrics.sensors.HalClientMonitor
    public void unableToStart() {
        try {
            getListener().onFeatureSet(false, this.mFeature);
        } catch (RemoteException e) {
            Slog.e("FaceSetFeatureClient", "Unable to send error", e);
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
            getListener().onFeatureSet(getFreshDaemon().setFeature(this.mFeature, this.mEnabled, this.mHardwareAuthToken, this.mFaceId) == 0, this.mFeature);
            this.mCallback.onClientFinished(this, true);
        } catch (RemoteException e) {
            Slog.e("FaceSetFeatureClient", "Unable to set feature: " + this.mFeature + " to enabled: " + this.mEnabled, e);
            this.mCallback.onClientFinished(this, false);
        }
    }
}
