package com.android.server.appop;

import com.android.internal.util.function.QuintFunction;
/* compiled from: R8$$SyntheticClass */
/* renamed from: com.android.server.appop.AppOpsService$CheckOpsDelegateDispatcher$$ExternalSyntheticLambda9 */
/* loaded from: classes.dex */
public final /* synthetic */ class C0482x34f2e5ce implements QuintFunction {
    public final /* synthetic */ AppOpsService f$0;

    public /* synthetic */ C0482x34f2e5ce(AppOpsService appOpsService) {
        this.f$0 = appOpsService;
    }

    public final Object apply(Object obj, Object obj2, Object obj3, Object obj4, Object obj5) {
        return Integer.valueOf(this.f$0.checkOperationImpl(((Integer) obj).intValue(), ((Integer) obj2).intValue(), (String) obj3, (String) obj4, ((Boolean) obj5).booleanValue()));
    }
}
