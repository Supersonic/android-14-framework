package com.android.internal.org.bouncycastle.asn1;
/* loaded from: classes4.dex */
public class ASN1ParsingException extends IllegalStateException {
    private Throwable cause;

    public ASN1ParsingException(String message) {
        super(message);
    }

    public ASN1ParsingException(String message, Throwable cause) {
        super(message);
        this.cause = cause;
    }

    @Override // java.lang.Throwable
    public Throwable getCause() {
        return this.cause;
    }
}
