package com.android.server.location.settings;

import java.util.concurrent.CountDownLatch;
/* compiled from: R8$$SyntheticClass */
/* loaded from: classes.dex */
public final /* synthetic */ class SettingsStore$$ExternalSyntheticLambda1 implements Runnable {
    public final /* synthetic */ CountDownLatch f$0;

    @Override // java.lang.Runnable
    public final void run() {
        this.f$0.countDown();
    }
}
