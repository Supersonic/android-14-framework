package com.android.server.biometrics.sensors.face.hidl;

import android.content.Context;
import android.hardware.biometrics.face.V1_0.IBiometricsFace;
import android.hardware.face.IFaceServiceReceiver;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Slog;
import com.android.internal.util.Preconditions;
import com.android.server.biometrics.log.BiometricContext;
import com.android.server.biometrics.log.BiometricLogger;
import com.android.server.biometrics.sensors.ClientMonitorCallback;
import com.android.server.biometrics.sensors.ClientMonitorCallbackConverter;
import com.android.server.biometrics.sensors.GenerateChallengeClient;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
/* loaded from: classes.dex */
public class FaceGenerateChallengeClient extends GenerateChallengeClient<IBiometricsFace> {
    public static final ClientMonitorCallback EMPTY_CALLBACK = new ClientMonitorCallback() { // from class: com.android.server.biometrics.sensors.face.hidl.FaceGenerateChallengeClient.1
    };
    public Long mChallengeResult;
    public final long mCreatedAt;
    public List<IFaceServiceReceiver> mWaiting;

    public FaceGenerateChallengeClient(Context context, Supplier<IBiometricsFace> supplier, IBinder iBinder, ClientMonitorCallbackConverter clientMonitorCallbackConverter, int i, String str, int i2, BiometricLogger biometricLogger, BiometricContext biometricContext, long j) {
        super(context, supplier, iBinder, clientMonitorCallbackConverter, i, str, i2, biometricLogger, biometricContext);
        this.mCreatedAt = j;
        this.mWaiting = new ArrayList();
    }

    @Override // com.android.server.biometrics.sensors.HalClientMonitor
    public void startHalOperation() {
        this.mChallengeResult = null;
        try {
            try {
                this.mChallengeResult = Long.valueOf(getFreshDaemon().generateChallenge(600).value);
                sendChallengeResult(getListener(), this.mCallback);
                for (IFaceServiceReceiver iFaceServiceReceiver : this.mWaiting) {
                    sendChallengeResult(new ClientMonitorCallbackConverter(iFaceServiceReceiver), EMPTY_CALLBACK);
                }
            } catch (RemoteException e) {
                Slog.e("FaceGenerateChallengeClient", "generateChallenge failed", e);
                this.mCallback.onClientFinished(this, false);
            }
        } finally {
            this.mWaiting = null;
        }
    }

    public long getCreatedAt() {
        return this.mCreatedAt;
    }

    public void reuseResult(IFaceServiceReceiver iFaceServiceReceiver) {
        List<IFaceServiceReceiver> list = this.mWaiting;
        if (list != null) {
            list.add(iFaceServiceReceiver);
        } else {
            sendChallengeResult(new ClientMonitorCallbackConverter(iFaceServiceReceiver), EMPTY_CALLBACK);
        }
    }

    public final void sendChallengeResult(ClientMonitorCallbackConverter clientMonitorCallbackConverter, ClientMonitorCallback clientMonitorCallback) {
        Preconditions.checkState(this.mChallengeResult != null, "result not available");
        try {
            clientMonitorCallbackConverter.onChallengeGenerated(getSensorId(), getTargetUserId(), this.mChallengeResult.longValue());
            clientMonitorCallback.onClientFinished(this, true);
        } catch (RemoteException e) {
            Slog.e("FaceGenerateChallengeClient", "Remote exception", e);
            clientMonitorCallback.onClientFinished(this, false);
        }
    }
}
