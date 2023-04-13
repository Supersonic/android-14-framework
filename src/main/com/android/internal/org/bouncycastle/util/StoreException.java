package com.android.internal.org.bouncycastle.util;
/* loaded from: classes4.dex */
public class StoreException extends RuntimeException {

    /* renamed from: _e */
    private Throwable f901_e;

    public StoreException(String msg, Throwable cause) {
        super(msg);
        this.f901_e = cause;
    }

    @Override // java.lang.Throwable
    public Throwable getCause() {
        return this.f901_e;
    }
}
