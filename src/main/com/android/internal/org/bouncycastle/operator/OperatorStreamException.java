package com.android.internal.org.bouncycastle.operator;

import java.io.IOException;
/* loaded from: classes4.dex */
public class OperatorStreamException extends IOException {
    private Throwable cause;

    public OperatorStreamException(String msg, Throwable cause) {
        super(msg);
        this.cause = cause;
    }

    @Override // java.lang.Throwable
    public Throwable getCause() {
        return this.cause;
    }
}
