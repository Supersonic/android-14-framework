package com.android.server.p014wm;

import java.util.function.BiPredicate;
/* compiled from: R8$$SyntheticClass */
/* renamed from: com.android.server.wm.AppTransition$$ExternalSyntheticLambda2 */
/* loaded from: classes2.dex */
public final /* synthetic */ class AppTransition$$ExternalSyntheticLambda2 implements BiPredicate {
    @Override // java.util.function.BiPredicate
    public final boolean test(Object obj, Object obj2) {
        return ((Task) obj).isTaskId(((Integer) obj2).intValue());
    }
}
