package com.android.internal.org.bouncycastle.x509;

import java.security.cert.CertificateEncodingException;
/* loaded from: classes4.dex */
class ExtCertificateEncodingException extends CertificateEncodingException {
    Throwable cause;

    /* JADX INFO: Access modifiers changed from: package-private */
    public ExtCertificateEncodingException(String message, Throwable cause) {
        super(message);
        this.cause = cause;
    }

    @Override // java.lang.Throwable
    public Throwable getCause() {
        return this.cause;
    }
}
