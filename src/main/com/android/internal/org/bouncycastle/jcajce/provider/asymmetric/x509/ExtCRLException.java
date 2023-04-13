package com.android.internal.org.bouncycastle.jcajce.provider.asymmetric.x509;

import java.security.cert.CRLException;
/* loaded from: classes4.dex */
class ExtCRLException extends CRLException {
    Throwable cause;

    /* JADX INFO: Access modifiers changed from: package-private */
    public ExtCRLException(String message, Throwable cause) {
        super(message);
        this.cause = cause;
    }

    @Override // java.lang.Throwable
    public Throwable getCause() {
        return this.cause;
    }
}
