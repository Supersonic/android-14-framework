package com.android.server.soundtrigger_middleware;
/* loaded from: classes2.dex */
public class RecoverableException extends RuntimeException {
    public final int errorCode;

    public RecoverableException(int i, String str) {
        super(str);
        this.errorCode = i;
    }

    public RecoverableException(int i) {
        this.errorCode = i;
    }

    @Override // java.lang.Throwable
    public String toString() {
        return super.toString() + " (code " + this.errorCode + ")";
    }
}
