package com.android.server.biometrics.sensors.face.aidl;

import android.content.Context;
import android.hardware.keymaster.HardwareAuthToken;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Slog;
import com.android.server.biometrics.HardwareAuthTokenUtils;
import com.android.server.biometrics.log.BiometricContext;
import com.android.server.biometrics.log.BiometricLogger;
import com.android.server.biometrics.sensors.ClientMonitorCallback;
import com.android.server.biometrics.sensors.ClientMonitorCallbackConverter;
import com.android.server.biometrics.sensors.ErrorConsumer;
import com.android.server.biometrics.sensors.HalClientMonitor;
import java.util.function.Supplier;
/* loaded from: classes.dex */
public class FaceSetFeatureClient extends HalClientMonitor<AidlSession> implements ErrorConsumer {
    public final boolean mEnabled;
    public final int mFeature;
    public final HardwareAuthToken mHardwareAuthToken;

    @Override // com.android.server.biometrics.sensors.BaseClientMonitor
    public int getProtoEnum() {
        return 8;
    }

    public FaceSetFeatureClient(Context context, Supplier<AidlSession> supplier, IBinder iBinder, ClientMonitorCallbackConverter clientMonitorCallbackConverter, int i, String str, int i2, BiometricLogger biometricLogger, BiometricContext biometricContext, int i3, boolean z, byte[] bArr) {
        super(context, supplier, iBinder, clientMonitorCallbackConverter, i, str, 0, i2, biometricLogger, biometricContext);
        this.mFeature = i3;
        this.mEnabled = z;
        this.mHardwareAuthToken = HardwareAuthTokenUtils.toHardwareAuthToken(bArr);
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
            getFreshDaemon().getSession().setFeature(this.mHardwareAuthToken, AidlConversionUtils.convertFrameworkToAidlFeature(this.mFeature), this.mEnabled);
        } catch (RemoteException | IllegalArgumentException e) {
            Slog.e("FaceSetFeatureClient", "Unable to set feature: " + this.mFeature + " to enabled: " + this.mEnabled, e);
            this.mCallback.onClientFinished(this, false);
        }
    }

    public void onFeatureSet(boolean z) {
        try {
            getListener().onFeatureSet(z, this.mFeature);
        } catch (RemoteException e) {
            Slog.e("FaceSetFeatureClient", "Remote exception", e);
        }
        this.mCallback.onClientFinished(this, true);
    }

    @Override // com.android.server.biometrics.sensors.ErrorConsumer
    public void onError(int i, int i2) {
        try {
            getListener().onFeatureSet(false, this.mFeature);
        } catch (RemoteException e) {
            Slog.e("FaceSetFeatureClient", "Remote exception", e);
        }
        this.mCallback.onClientFinished(this, false);
    }
}
