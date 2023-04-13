package com.android.server.appop;

import android.os.IBinder;
import com.android.internal.util.function.QuintConsumer;
/* compiled from: R8$$SyntheticClass */
/* renamed from: com.android.server.appop.AppOpsService$CheckOpsDelegateDispatcher$$ExternalSyntheticLambda13 */
/* loaded from: classes.dex */
public final /* synthetic */ class C0472x6969d32d implements QuintConsumer {
    public final /* synthetic */ AppOpsService f$0;

    public /* synthetic */ C0472x6969d32d(AppOpsService appOpsService) {
        this.f$0 = appOpsService;
    }

    public final void accept(Object obj, Object obj2, Object obj3, Object obj4, Object obj5) {
        AppOpsService.m1609$$Nest$mfinishOperationImpl(this.f$0, (IBinder) obj, ((Integer) obj2).intValue(), ((Integer) obj3).intValue(), (String) obj4, (String) obj5);
    }
}
