package com.android.server.biometrics.sensors;
/* loaded from: classes.dex */
public interface ClientMonitorCallback {
    default void onBiometricAction(int i) {
    }

    default void onClientFinished(BaseClientMonitor baseClientMonitor, boolean z) {
    }

    default void onClientStarted(BaseClientMonitor baseClientMonitor) {
    }
}
