package com.android.server.appop;

import com.android.internal.util.function.QuadFunction;
/* compiled from: R8$$SyntheticClass */
/* renamed from: com.android.server.appop.AppOpsService$CheckOpsDelegateDispatcher$$ExternalSyntheticLambda15 */
/* loaded from: classes.dex */
public final /* synthetic */ class C0474x6969d32f implements QuadFunction {
    public final /* synthetic */ AppOpsService f$0;

    public /* synthetic */ C0474x6969d32f(AppOpsService appOpsService) {
        this.f$0 = appOpsService;
    }

    public final Object apply(Object obj, Object obj2, Object obj3, Object obj4) {
        return Integer.valueOf(AppOpsService.m1607$$Nest$mcheckAudioOperationImpl(this.f$0, ((Integer) obj).intValue(), ((Integer) obj2).intValue(), ((Integer) obj3).intValue(), (String) obj4));
    }
}
