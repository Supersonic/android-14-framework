package com.android.server.biometrics.sensors;

import android.content.Context;
import android.os.IBinder;
import com.android.server.biometrics.log.BiometricContext;
import com.android.server.biometrics.log.BiometricLogger;
import com.android.server.biometrics.log.OperationContextExt;
import java.util.function.Supplier;
/* loaded from: classes.dex */
public abstract class HalClientMonitor<T> extends BaseClientMonitor {
    public final Supplier<T> mLazyDaemon;
    public final OperationContextExt mOperationContext;

    public abstract void startHalOperation();

    public abstract void unableToStart();

    public HalClientMonitor(Context context, Supplier<T> supplier, IBinder iBinder, ClientMonitorCallbackConverter clientMonitorCallbackConverter, int i, String str, int i2, int i3, BiometricLogger biometricLogger, BiometricContext biometricContext) {
        super(context, iBinder, clientMonitorCallbackConverter, i, str, i2, i3, biometricLogger, biometricContext);
        this.mOperationContext = new OperationContextExt();
        this.mLazyDaemon = supplier;
    }

    public T getFreshDaemon() {
        return this.mLazyDaemon.get();
    }

    @Override // com.android.server.biometrics.sensors.BaseClientMonitor
    public void destroy() {
        super.destroy();
        unsubscribeBiometricContext();
    }

    public OperationContextExt getOperationContext() {
        return getBiometricContext().updateContext(this.mOperationContext, isCryptoOperation());
    }

    public ClientMonitorCallback getBiometricContextUnsubscriber() {
        return new ClientMonitorCallback() { // from class: com.android.server.biometrics.sensors.HalClientMonitor.1
            @Override // com.android.server.biometrics.sensors.ClientMonitorCallback
            public void onClientFinished(BaseClientMonitor baseClientMonitor, boolean z) {
                HalClientMonitor.this.unsubscribeBiometricContext();
            }
        };
    }

    public void unsubscribeBiometricContext() {
        getBiometricContext().unsubscribe(this.mOperationContext);
    }
}
