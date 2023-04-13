package com.android.internal.org.bouncycastle.jcajce.provider.util;

import javax.crypto.BadPaddingException;
/* loaded from: classes4.dex */
public class BadBlockException extends BadPaddingException {
    private final Throwable cause;

    public BadBlockException(String msg, Throwable cause) {
        super(msg);
        this.cause = cause;
    }

    @Override // java.lang.Throwable
    public Throwable getCause() {
        return this.cause;
    }
}
