package com.android.server.biometrics.sensors;

import android.content.Context;
import android.os.IBinder;
import com.android.internal.annotations.VisibleForTesting;
import com.android.server.biometrics.log.BiometricContext;
import com.android.server.biometrics.log.BiometricLogger;
import java.util.function.Supplier;
/* loaded from: classes.dex */
public abstract class StopUserClient<T> extends HalClientMonitor<T> {
    @VisibleForTesting
    private final UserStoppedCallback mUserStoppedCallback;

    /* loaded from: classes.dex */
    public interface UserStoppedCallback {
        void onUserStopped();
    }

    @Override // com.android.server.biometrics.sensors.BaseClientMonitor
    public int getProtoEnum() {
        return 16;
    }

    public void onUserStopped() {
        this.mUserStoppedCallback.onUserStopped();
        getCallback().onClientFinished(this, true);
    }

    public StopUserClient(Context context, Supplier<T> supplier, IBinder iBinder, int i, int i2, BiometricLogger biometricLogger, BiometricContext biometricContext, UserStoppedCallback userStoppedCallback) {
        super(context, supplier, iBinder, null, i, context.getOpPackageName(), 0, i2, biometricLogger, biometricContext);
        this.mUserStoppedCallback = userStoppedCallback;
    }
}
