package com.android.internal.org.bouncycastle.jce.provider;

import com.android.internal.org.bouncycastle.jce.exception.ExtException;
/* loaded from: classes4.dex */
public class AnnotatedException extends Exception implements ExtException {
    private Throwable _underlyingException;

    public AnnotatedException(String string, Throwable e) {
        super(string);
        this._underlyingException = e;
    }

    public AnnotatedException(String string) {
        this(string, null);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Throwable getUnderlyingException() {
        return this._underlyingException;
    }

    @Override // java.lang.Throwable, com.android.internal.org.bouncycastle.jce.exception.ExtException
    public Throwable getCause() {
        return this._underlyingException;
    }
}
