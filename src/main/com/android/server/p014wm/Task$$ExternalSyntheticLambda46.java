package com.android.server.p014wm;

import java.util.ArrayList;
import java.util.function.Consumer;
/* compiled from: R8$$SyntheticClass */
/* renamed from: com.android.server.wm.Task$$ExternalSyntheticLambda46 */
/* loaded from: classes2.dex */
public final /* synthetic */ class Task$$ExternalSyntheticLambda46 implements Consumer {
    public final /* synthetic */ ArrayList f$0;

    public /* synthetic */ Task$$ExternalSyntheticLambda46(ArrayList arrayList) {
        this.f$0 = arrayList;
    }

    @Override // java.util.function.Consumer
    public final void accept(Object obj) {
        this.f$0.add((ActivityRecord) obj);
    }
}
