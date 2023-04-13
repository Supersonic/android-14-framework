package com.android.server.biometrics.sensors;

import android.content.Context;
import android.os.IBinder;
import com.android.internal.annotations.VisibleForTesting;
import com.android.server.biometrics.log.BiometricContext;
import com.android.server.biometrics.log.BiometricLogger;
import java.util.function.Supplier;
/* loaded from: classes.dex */
public abstract class StartUserClient<T, U> extends HalClientMonitor<T> {
    @VisibleForTesting
    protected final UserStartedCallback<U> mUserStartedCallback;

    /* loaded from: classes.dex */
    public interface UserStartedCallback<U> {
        void onUserStarted(int i, U u, int i2);
    }

    @Override // com.android.server.biometrics.sensors.BaseClientMonitor
    public int getProtoEnum() {
        return 17;
    }

    public StartUserClient(Context context, Supplier<T> supplier, IBinder iBinder, int i, int i2, BiometricLogger biometricLogger, BiometricContext biometricContext, UserStartedCallback<U> userStartedCallback) {
        super(context, supplier, iBinder, null, i, context.getOpPackageName(), 0, i2, biometricLogger, biometricContext);
        this.mUserStartedCallback = userStartedCallback;
    }
}
