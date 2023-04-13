package com.android.internal.org.bouncycastle.util.p027io.pem;

import java.io.IOException;
/* renamed from: com.android.internal.org.bouncycastle.util.io.pem.PemGenerationException */
/* loaded from: classes4.dex */
public class PemGenerationException extends IOException {
    private Throwable cause;

    public PemGenerationException(String message, Throwable cause) {
        super(message);
        this.cause = cause;
    }

    public PemGenerationException(String message) {
        super(message);
    }

    @Override // java.lang.Throwable
    public Throwable getCause() {
        return this.cause;
    }
}
