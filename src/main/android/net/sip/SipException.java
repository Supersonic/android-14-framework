package android.net.sip;
/* loaded from: classes.dex */
public class SipException extends Exception {
    public SipException() {
    }

    public SipException(String message) {
        super(message);
    }

    /* JADX WARN: Illegal instructions before constructor call */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public SipException(String message, Throwable cause) {
        super(message, r0);
        Throwable th;
        if ((cause instanceof javax.sip.SipException) && cause.getCause() != null) {
            th = cause.getCause();
        } else {
            th = cause;
        }
    }
}
