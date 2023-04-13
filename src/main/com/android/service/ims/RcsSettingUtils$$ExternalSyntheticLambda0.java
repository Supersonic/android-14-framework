package com.android.service.ims;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Consumer;
/* compiled from: D8$$SyntheticClass */
/* loaded from: classes.dex */
public final /* synthetic */ class RcsSettingUtils$$ExternalSyntheticLambda0 implements Consumer {
    public final /* synthetic */ LinkedBlockingQueue f$0;

    public /* synthetic */ RcsSettingUtils$$ExternalSyntheticLambda0(LinkedBlockingQueue linkedBlockingQueue) {
        this.f$0 = linkedBlockingQueue;
    }

    @Override // java.util.function.Consumer
    public final void accept(Object obj) {
        this.f$0.offer((Boolean) obj);
    }
}
