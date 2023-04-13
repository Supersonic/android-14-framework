package com.android.internal.org.bouncycastle.cert;
/* loaded from: classes4.dex */
public class CertException extends Exception {
    private Throwable cause;

    public CertException(String msg, Throwable cause) {
        super(msg);
        this.cause = cause;
    }

    public CertException(String msg) {
        super(msg);
    }

    @Override // java.lang.Throwable
    public Throwable getCause() {
        return this.cause;
    }
}
