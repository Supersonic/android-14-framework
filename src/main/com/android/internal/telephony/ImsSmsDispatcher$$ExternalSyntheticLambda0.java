package com.android.internal.telephony;

import java.util.concurrent.Executor;
/* compiled from: R8$$SyntheticClass */
/* loaded from: classes.dex */
public final /* synthetic */ class ImsSmsDispatcher$$ExternalSyntheticLambda0 implements Executor {
    public final /* synthetic */ ImsSmsDispatcher f$0;

    @Override // java.util.concurrent.Executor
    public final void execute(Runnable runnable) {
        this.f$0.post(runnable);
    }
}
