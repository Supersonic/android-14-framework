package com.android.server.permission.jarjar.kotlin.jdk7;

import com.android.server.permission.jarjar.kotlin.ExceptionsKt__ExceptionsKt;
/* compiled from: AutoCloseable.kt */
/* loaded from: classes2.dex */
public final class AutoCloseableKt {
    public static final void closeFinally(AutoCloseable autoCloseable, Throwable th) {
        if (autoCloseable != null) {
            if (th == null) {
                autoCloseable.close();
                return;
            }
            try {
                autoCloseable.close();
            } catch (Throwable th2) {
                ExceptionsKt__ExceptionsKt.addSuppressed(th, th2);
            }
        }
    }
}
