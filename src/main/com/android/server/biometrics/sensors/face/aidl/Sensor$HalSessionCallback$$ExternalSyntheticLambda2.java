package com.android.server.biometrics.sensors.face.aidl;

import com.android.server.biometrics.sensors.UserAwareBiometricScheduler;
/* compiled from: R8$$SyntheticClass */
/* loaded from: classes.dex */
public final /* synthetic */ class Sensor$HalSessionCallback$$ExternalSyntheticLambda2 implements Runnable {
    public final /* synthetic */ UserAwareBiometricScheduler f$0;

    @Override // java.lang.Runnable
    public final void run() {
        this.f$0.onUserStopped();
    }
}
