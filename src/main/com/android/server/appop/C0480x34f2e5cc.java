package com.android.server.appop;

import android.content.AttributionSource;
import android.os.IBinder;
import com.android.internal.util.function.QuadFunction;
/* compiled from: R8$$SyntheticClass */
/* renamed from: com.android.server.appop.AppOpsService$CheckOpsDelegateDispatcher$$ExternalSyntheticLambda7 */
/* loaded from: classes.dex */
public final /* synthetic */ class C0480x34f2e5cc implements QuadFunction {
    public final /* synthetic */ AppOpsService f$0;

    public /* synthetic */ C0480x34f2e5cc(AppOpsService appOpsService) {
        this.f$0 = appOpsService;
    }

    public final Object apply(Object obj, Object obj2, Object obj3, Object obj4) {
        return this.f$0.finishProxyOperationImpl((IBinder) obj, ((Integer) obj2).intValue(), (AttributionSource) obj3, ((Boolean) obj4).booleanValue());
    }
}
