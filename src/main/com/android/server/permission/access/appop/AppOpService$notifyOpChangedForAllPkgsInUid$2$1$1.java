package com.android.server.permission.access.appop;

import com.android.internal.util.function.QuintConsumer;
import com.android.server.appop.OnOpModeChangedListener;
/* compiled from: AppOpService.kt */
/* loaded from: classes2.dex */
public /* synthetic */ class AppOpService$notifyOpChangedForAllPkgsInUid$2$1$1 implements QuintConsumer {
    public static final AppOpService$notifyOpChangedForAllPkgsInUid$2$1$1 INSTANCE = new AppOpService$notifyOpChangedForAllPkgsInUid$2$1$1();

    public final void accept(AppOpService appOpService, OnOpModeChangedListener onOpModeChangedListener, int i, int i2, String str) {
        appOpService.notifyOpChanged(onOpModeChangedListener, i, i2, str);
    }

    public /* bridge */ /* synthetic */ void accept(Object obj, Object obj2, Object obj3, Object obj4, Object obj5) {
        accept((AppOpService) obj, (OnOpModeChangedListener) obj2, ((Number) obj3).intValue(), ((Number) obj4).intValue(), (String) obj5);
    }
}
