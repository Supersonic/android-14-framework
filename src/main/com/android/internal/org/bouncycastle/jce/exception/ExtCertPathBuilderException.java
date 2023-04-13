package com.android.internal.org.bouncycastle.jce.exception;

import java.security.cert.CertPath;
import java.security.cert.CertPathBuilderException;
/* loaded from: classes4.dex */
public class ExtCertPathBuilderException extends CertPathBuilderException implements ExtException {
    private Throwable cause;

    public ExtCertPathBuilderException(String message, Throwable cause) {
        super(message);
        this.cause = cause;
    }

    public ExtCertPathBuilderException(String msg, Throwable cause, CertPath certPath, int index) {
        super(msg, cause);
        this.cause = cause;
    }

    @Override // java.lang.Throwable, com.android.internal.org.bouncycastle.jce.exception.ExtException
    public Throwable getCause() {
        return this.cause;
    }
}
