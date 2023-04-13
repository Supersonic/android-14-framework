package com.android.internal.org.bouncycastle.cms;
/* loaded from: classes4.dex */
public class CMSRuntimeException extends RuntimeException {

    /* renamed from: e */
    Exception f664e;

    public CMSRuntimeException(String name) {
        super(name);
    }

    public CMSRuntimeException(String name, Exception e) {
        super(name);
        this.f664e = e;
    }

    public Exception getUnderlyingException() {
        return this.f664e;
    }

    @Override // java.lang.Throwable
    public Throwable getCause() {
        return this.f664e;
    }
}
