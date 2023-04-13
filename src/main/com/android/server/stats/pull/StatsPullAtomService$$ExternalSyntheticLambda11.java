package com.android.server.stats.pull;

import android.app.AppOpsManager;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
/* compiled from: R8$$SyntheticClass */
/* loaded from: classes2.dex */
public final /* synthetic */ class StatsPullAtomService$$ExternalSyntheticLambda11 implements Consumer {
    public final /* synthetic */ CompletableFuture f$0;

    @Override // java.util.function.Consumer
    public final void accept(Object obj) {
        this.f$0.complete((AppOpsManager.HistoricalOps) obj);
    }
}
