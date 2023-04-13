package com.android.internal.org.bouncycastle.asn1;

import java.io.IOException;
/* loaded from: classes4.dex */
public class ASN1Exception extends IOException {
    private Throwable cause;

    /* JADX INFO: Access modifiers changed from: package-private */
    public ASN1Exception(String message) {
        super(message);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ASN1Exception(String message, Throwable cause) {
        super(message);
        this.cause = cause;
    }

    @Override // java.lang.Throwable
    public Throwable getCause() {
        return this.cause;
    }
}
