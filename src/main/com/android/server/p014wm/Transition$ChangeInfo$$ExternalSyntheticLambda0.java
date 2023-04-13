package com.android.server.p014wm;

import java.util.function.Predicate;
/* compiled from: R8$$SyntheticClass */
/* renamed from: com.android.server.wm.Transition$ChangeInfo$$ExternalSyntheticLambda0 */
/* loaded from: classes2.dex */
public final /* synthetic */ class Transition$ChangeInfo$$ExternalSyntheticLambda0 implements Predicate {
    @Override // java.util.function.Predicate
    public final boolean test(Object obj) {
        return ((ActivityRecord) obj).hasStartingWindow();
    }
}
