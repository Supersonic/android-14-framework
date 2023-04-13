package com.android.server;
/* loaded from: classes.dex */
public class AppFuseMountException extends Exception {
    public AppFuseMountException(String str) {
        super(str);
    }

    public AppFuseMountException(String str, Throwable th) {
        super(str, th);
    }

    public IllegalArgumentException rethrowAsParcelableException() {
        throw new IllegalStateException(getMessage(), this);
    }
}
