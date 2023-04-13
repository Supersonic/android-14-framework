package android.util;
/* loaded from: classes3.dex */
public class AndroidException extends Exception {
    public AndroidException() {
    }

    public AndroidException(String name) {
        super(name);
    }

    public AndroidException(String name, Throwable cause) {
        super(name, cause);
    }

    public AndroidException(Exception cause) {
        super(cause);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public AndroidException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
