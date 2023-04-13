package com.android.internal.org.bouncycastle.cms;
/* loaded from: classes4.dex */
public class CMSException extends Exception {

    /* renamed from: e */
    Exception f663e;

    public CMSException(String msg) {
        super(msg);
    }

    public CMSException(String msg, Exception e) {
        super(msg);
        this.f663e = e;
    }

    public Exception getUnderlyingException() {
        return this.f663e;
    }

    @Override // java.lang.Throwable
    public Throwable getCause() {
        return this.f663e;
    }
}
