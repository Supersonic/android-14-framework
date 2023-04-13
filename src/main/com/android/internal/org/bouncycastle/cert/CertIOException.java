package com.android.internal.org.bouncycastle.cert;

import java.io.IOException;
/* loaded from: classes4.dex */
public class CertIOException extends IOException {
    private Throwable cause;

    public CertIOException(String msg, Throwable cause) {
        super(msg);
        this.cause = cause;
    }

    public CertIOException(String msg) {
        super(msg);
    }

    @Override // java.lang.Throwable
    public Throwable getCause() {
        return this.cause;
    }
}
