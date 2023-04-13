package com.android.server.p014wm;

import com.android.internal.util.function.TriPredicate;
/* compiled from: R8$$SyntheticClass */
/* renamed from: com.android.server.wm.TaskFragment$$ExternalSyntheticLambda1 */
/* loaded from: classes2.dex */
public final /* synthetic */ class TaskFragment$$ExternalSyntheticLambda1 implements TriPredicate {
    public final boolean test(Object obj, Object obj2, Object obj3) {
        boolean isOpaqueActivity;
        isOpaqueActivity = TaskFragment.isOpaqueActivity((ActivityRecord) obj, (ActivityRecord) obj2, ((Boolean) obj3).booleanValue());
        return isOpaqueActivity;
    }
}
