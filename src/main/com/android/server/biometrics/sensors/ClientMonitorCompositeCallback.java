package com.android.server.biometrics.sensors;

import java.util.ArrayList;
import java.util.List;
/* loaded from: classes.dex */
public class ClientMonitorCompositeCallback implements ClientMonitorCallback {
    public final List<ClientMonitorCallback> mCallbacks = new ArrayList();

    public ClientMonitorCompositeCallback(ClientMonitorCallback... clientMonitorCallbackArr) {
        for (ClientMonitorCallback clientMonitorCallback : clientMonitorCallbackArr) {
            if (clientMonitorCallback != null) {
                this.mCallbacks.add(clientMonitorCallback);
            }
        }
    }

    @Override // com.android.server.biometrics.sensors.ClientMonitorCallback
    public final void onClientStarted(BaseClientMonitor baseClientMonitor) {
        for (int i = 0; i < this.mCallbacks.size(); i++) {
            this.mCallbacks.get(i).onClientStarted(baseClientMonitor);
        }
    }

    @Override // com.android.server.biometrics.sensors.ClientMonitorCallback
    public final void onBiometricAction(int i) {
        for (int i2 = 0; i2 < this.mCallbacks.size(); i2++) {
            this.mCallbacks.get(i2).onBiometricAction(i);
        }
    }

    @Override // com.android.server.biometrics.sensors.ClientMonitorCallback
    public final void onClientFinished(BaseClientMonitor baseClientMonitor, boolean z) {
        for (int size = this.mCallbacks.size() - 1; size >= 0; size--) {
            this.mCallbacks.get(size).onClientFinished(baseClientMonitor, z);
        }
    }
}
