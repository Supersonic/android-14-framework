package com.android.server.people.data;
/* compiled from: R8$$SyntheticClass */
/* loaded from: classes2.dex */
public final /* synthetic */ class AbstractProtoDiskReadWriter$$ExternalSyntheticLambda0 implements Runnable {
    public final /* synthetic */ AbstractProtoDiskReadWriter f$0;

    public /* synthetic */ AbstractProtoDiskReadWriter$$ExternalSyntheticLambda0(AbstractProtoDiskReadWriter abstractProtoDiskReadWriter) {
        this.f$0 = abstractProtoDiskReadWriter;
    }

    @Override // java.lang.Runnable
    public final void run() {
        this.f$0.flushScheduledData();
    }
}
