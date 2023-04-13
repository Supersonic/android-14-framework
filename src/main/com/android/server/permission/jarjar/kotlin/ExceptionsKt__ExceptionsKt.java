package com.android.server.permission.jarjar.kotlin;

import com.android.server.permission.jarjar.kotlin.internal.PlatformImplementationsKt;
import com.android.server.permission.jarjar.kotlin.jvm.internal.Intrinsics;
/* compiled from: Exceptions.kt */
/* loaded from: classes2.dex */
public class ExceptionsKt__ExceptionsKt {
    public static final void addSuppressed(Throwable th, Throwable th2) {
        Intrinsics.checkNotNullParameter(th, "<this>");
        Intrinsics.checkNotNullParameter(th2, "exception");
        if (th != th2) {
            PlatformImplementationsKt.IMPLEMENTATIONS.addSuppressed(th, th2);
        }
    }
}
