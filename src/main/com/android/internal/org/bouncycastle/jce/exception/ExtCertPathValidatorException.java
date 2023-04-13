package com.android.internal.org.bouncycastle.jce.exception;

import java.security.cert.CertPath;
import java.security.cert.CertPathValidatorException;
/* loaded from: classes4.dex */
public class ExtCertPathValidatorException extends CertPathValidatorException implements ExtException {
    private Throwable cause;

    public ExtCertPathValidatorException(String message) {
        super(message);
    }

    public ExtCertPathValidatorException(String message, Throwable cause) {
        super(message);
        this.cause = cause;
    }

    public ExtCertPathValidatorException(String msg, Throwable cause, CertPath certPath, int index) {
        super(msg, cause, certPath, index);
        this.cause = cause;
    }

    @Override // java.lang.Throwable, com.android.internal.org.bouncycastle.jce.exception.ExtException
    public Throwable getCause() {
        return this.cause;
    }
}
