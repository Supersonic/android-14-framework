package android.security;
/* loaded from: classes3.dex */
public class KeyChainException extends Exception {
    public KeyChainException() {
    }

    public KeyChainException(String detailMessage) {
        super(detailMessage);
    }

    public KeyChainException(String message, Throwable cause) {
        super(message, cause);
    }

    public KeyChainException(Throwable cause) {
        super(cause == null ? null : cause.toString(), cause);
    }
}
