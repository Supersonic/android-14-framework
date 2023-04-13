package com.android.server.appop;

import com.android.internal.util.function.HeptFunction;
/* compiled from: R8$$SyntheticClass */
/* renamed from: com.android.server.appop.AppOpsService$CheckOpsDelegateDispatcher$$ExternalSyntheticLambda3 */
/* loaded from: classes.dex */
public final /* synthetic */ class C0476x34f2e5c8 implements HeptFunction {
    public final /* synthetic */ AppOpsService f$0;

    public /* synthetic */ C0476x34f2e5c8(AppOpsService appOpsService) {
        this.f$0 = appOpsService;
    }

    public final Object apply(Object obj, Object obj2, Object obj3, Object obj4, Object obj5, Object obj6, Object obj7) {
        return AppOpsService.m1618$$Nest$mnoteOperationImpl(this.f$0, ((Integer) obj).intValue(), ((Integer) obj2).intValue(), (String) obj3, (String) obj4, ((Boolean) obj5).booleanValue(), (String) obj6, ((Boolean) obj7).booleanValue());
    }
}
