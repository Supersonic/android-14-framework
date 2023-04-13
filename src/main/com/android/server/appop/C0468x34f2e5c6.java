package com.android.server.appop;

import android.content.AttributionSource;
import android.os.IBinder;
import com.android.internal.util.function.UndecFunction;
import com.android.server.appop.AppOpsService;
/* compiled from: R8$$SyntheticClass */
/* renamed from: com.android.server.appop.AppOpsService$CheckOpsDelegateDispatcher$$ExternalSyntheticLambda1 */
/* loaded from: classes.dex */
public final /* synthetic */ class C0468x34f2e5c6 implements UndecFunction {
    public final /* synthetic */ AppOpsService f$0;

    public /* synthetic */ C0468x34f2e5c6(AppOpsService appOpsService) {
        this.f$0 = appOpsService;
    }

    public final Object apply(Object obj, Object obj2, Object obj3, Object obj4, Object obj5, Object obj6, Object obj7, Object obj8, Object obj9, Object obj10, Object obj11) {
        return AppOpsService.CheckOpsDelegateDispatcher.$r8$lambda$kQytUeo6KPcEwU0WRhtZrcrIPs8(this.f$0, (IBinder) obj, ((Integer) obj2).intValue(), (AttributionSource) obj3, ((Boolean) obj4).booleanValue(), ((Boolean) obj5).booleanValue(), (String) obj6, ((Boolean) obj7).booleanValue(), ((Boolean) obj8).booleanValue(), ((Integer) obj9).intValue(), ((Integer) obj10).intValue(), ((Integer) obj11).intValue());
    }
}
