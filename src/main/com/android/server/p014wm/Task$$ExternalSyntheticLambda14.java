package com.android.server.p014wm;

import java.util.function.Predicate;
/* compiled from: R8$$SyntheticClass */
/* renamed from: com.android.server.wm.Task$$ExternalSyntheticLambda14 */
/* loaded from: classes2.dex */
public final /* synthetic */ class Task$$ExternalSyntheticLambda14 implements Predicate {
    @Override // java.util.function.Predicate
    public final boolean test(Object obj) {
        return ((ActivityRecord) obj).canBeTopRunning();
    }
}
