package com.android.server.appop;

import com.android.internal.util.function.QuintConsumer;
/* compiled from: R8$$SyntheticClass */
/* loaded from: classes.dex */
public final /* synthetic */ class AppOpsService$$ExternalSyntheticLambda4 implements QuintConsumer {
    public final void accept(Object obj, Object obj2, Object obj3, Object obj4, Object obj5) {
        ((AppOpsService) obj).notifyOpChanged((OnOpModeChangedListener) obj2, ((Integer) obj3).intValue(), ((Integer) obj4).intValue(), (String) obj5);
    }
}
