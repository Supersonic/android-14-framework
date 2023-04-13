package com.android.server.biometrics.sensors;
/* loaded from: classes.dex */
public interface LockoutConsumer {
    void onLockoutPermanent();

    void onLockoutTimed(long j);
}
