package com.android.internal.org.bouncycastle.operator;
/* loaded from: classes4.dex */
public class OperatorException extends Exception {
    private Throwable cause;

    public OperatorException(String msg, Throwable cause) {
        super(msg);
        this.cause = cause;
    }

    public OperatorException(String msg) {
        super(msg);
    }

    @Override // java.lang.Throwable
    public Throwable getCause() {
        return this.cause;
    }
}
