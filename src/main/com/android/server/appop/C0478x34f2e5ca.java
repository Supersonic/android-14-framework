package com.android.server.appop;

import android.content.AttributionSource;
import com.android.internal.util.function.HexFunction;
/* compiled from: R8$$SyntheticClass */
/* renamed from: com.android.server.appop.AppOpsService$CheckOpsDelegateDispatcher$$ExternalSyntheticLambda5 */
/* loaded from: classes.dex */
public final /* synthetic */ class C0478x34f2e5ca implements HexFunction {
    public final /* synthetic */ AppOpsService f$0;

    public /* synthetic */ C0478x34f2e5ca(AppOpsService appOpsService) {
        this.f$0 = appOpsService;
    }

    public final Object apply(Object obj, Object obj2, Object obj3, Object obj4, Object obj5, Object obj6) {
        return this.f$0.noteProxyOperationImpl(((Integer) obj).intValue(), (AttributionSource) obj2, ((Boolean) obj3).booleanValue(), (String) obj4, ((Boolean) obj5).booleanValue(), ((Boolean) obj6).booleanValue());
    }
}
