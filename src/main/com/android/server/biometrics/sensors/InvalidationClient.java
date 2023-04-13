package com.android.server.biometrics.sensors;

import android.content.Context;
import android.hardware.biometrics.BiometricAuthenticator;
import android.hardware.biometrics.BiometricAuthenticator.Identifier;
import android.hardware.biometrics.IInvalidationCallback;
import android.os.RemoteException;
import android.util.Slog;
import com.android.server.biometrics.log.BiometricContext;
import com.android.server.biometrics.log.BiometricLogger;
import java.util.Map;
import java.util.function.Supplier;
/* loaded from: classes.dex */
public abstract class InvalidationClient<S extends BiometricAuthenticator.Identifier, T> extends HalClientMonitor<T> {
    public final Map<Integer, Long> mAuthenticatorIds;
    public final IInvalidationCallback mInvalidationCallback;

    @Override // com.android.server.biometrics.sensors.BaseClientMonitor
    public int getProtoEnum() {
        return 15;
    }

    @Override // com.android.server.biometrics.sensors.HalClientMonitor
    public void unableToStart() {
    }

    public InvalidationClient(Context context, Supplier<T> supplier, int i, int i2, BiometricLogger biometricLogger, BiometricContext biometricContext, Map<Integer, Long> map, IInvalidationCallback iInvalidationCallback) {
        super(context, supplier, null, null, i, context.getOpPackageName(), 0, i2, biometricLogger, biometricContext);
        this.mAuthenticatorIds = map;
        this.mInvalidationCallback = iInvalidationCallback;
    }

    public void onAuthenticatorIdInvalidated(long j) {
        this.mAuthenticatorIds.put(Integer.valueOf(getTargetUserId()), Long.valueOf(j));
        try {
            this.mInvalidationCallback.onCompleted();
        } catch (RemoteException e) {
            Slog.e("InvalidationClient", "Remote exception", e);
        }
        this.mCallback.onClientFinished(this, true);
    }

    @Override // com.android.server.biometrics.sensors.BaseClientMonitor
    public void start(ClientMonitorCallback clientMonitorCallback) {
        super.start(clientMonitorCallback);
        startHalOperation();
    }
}
