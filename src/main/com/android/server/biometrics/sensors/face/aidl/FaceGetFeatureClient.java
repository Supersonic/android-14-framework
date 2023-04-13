package com.android.server.biometrics.sensors.face.aidl;

import android.content.Context;
import android.os.IBinder;
import android.os.RemoteException;
import android.provider.Settings;
import android.util.Slog;
import com.android.server.biometrics.log.BiometricContext;
import com.android.server.biometrics.log.BiometricLogger;
import com.android.server.biometrics.sensors.ClientMonitorCallback;
import com.android.server.biometrics.sensors.ClientMonitorCallbackConverter;
import com.android.server.biometrics.sensors.ErrorConsumer;
import com.android.server.biometrics.sensors.HalClientMonitor;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
/* loaded from: classes.dex */
public class FaceGetFeatureClient extends HalClientMonitor<AidlSession> implements ErrorConsumer {
    public final int mUserId;

    @Override // com.android.server.biometrics.sensors.BaseClientMonitor
    public int getProtoEnum() {
        return 9;
    }

    public FaceGetFeatureClient(Context context, Supplier<AidlSession> supplier, IBinder iBinder, ClientMonitorCallbackConverter clientMonitorCallbackConverter, int i, String str, int i2, BiometricLogger biometricLogger, BiometricContext biometricContext) {
        super(context, supplier, iBinder, clientMonitorCallbackConverter, i, str, 0, i2, biometricLogger, biometricContext);
        this.mUserId = i;
    }

    @Override // com.android.server.biometrics.sensors.HalClientMonitor
    public void unableToStart() {
        this.mCallback.onClientFinished(this, false);
    }

    @Override // com.android.server.biometrics.sensors.BaseClientMonitor
    public void start(ClientMonitorCallback clientMonitorCallback) {
        super.start(clientMonitorCallback);
        startHalOperation();
    }

    @Override // com.android.server.biometrics.sensors.HalClientMonitor
    public void startHalOperation() {
        try {
            getFreshDaemon().getSession().getFeatures();
        } catch (RemoteException e) {
            Slog.e("FaceGetFeatureClient", "Unable to getFeature", e);
            this.mCallback.onClientFinished(this, false);
        }
    }

    public void onFeatureGet(boolean z, byte[] bArr) {
        try {
            HashMap<Integer, Boolean> featureMap = getFeatureMap();
            int[] iArr = new int[featureMap.size()];
            boolean[] zArr = new boolean[featureMap.size()];
            for (byte b : bArr) {
                featureMap.put(Integer.valueOf(AidlConversionUtils.convertAidlToFrameworkFeature(b)), Boolean.TRUE);
            }
            int i = 0;
            for (Map.Entry<Integer, Boolean> entry : featureMap.entrySet()) {
                iArr[i] = entry.getKey().intValue();
                zArr[i] = entry.getValue().booleanValue();
                i++;
            }
            boolean booleanValue = featureMap.get(1).booleanValue();
            Slog.d("FaceGetFeatureClient", "Updating attention value for user: " + this.mUserId + " to value: " + booleanValue);
            Settings.Secure.putIntForUser(getContext().getContentResolver(), "face_unlock_attention_required", booleanValue ? 1 : 0, this.mUserId);
            getListener().onFeatureGet(z, iArr, zArr);
            this.mCallback.onClientFinished(this, true);
        } catch (RemoteException | IllegalArgumentException e) {
            Slog.e("FaceGetFeatureClient", "exception", e);
            this.mCallback.onClientFinished(this, false);
        }
    }

    public final HashMap<Integer, Boolean> getFeatureMap() {
        HashMap<Integer, Boolean> hashMap = new HashMap<>();
        hashMap.put(1, Boolean.FALSE);
        return hashMap;
    }

    @Override // com.android.server.biometrics.sensors.ErrorConsumer
    public void onError(int i, int i2) {
        try {
            getListener().onFeatureGet(false, new int[0], new boolean[0]);
        } catch (RemoteException e) {
            Slog.e("FaceGetFeatureClient", "Remote exception", e);
        }
        this.mCallback.onClientFinished(this, false);
    }
}
