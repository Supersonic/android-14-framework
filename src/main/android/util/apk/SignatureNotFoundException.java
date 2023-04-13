package android.util.apk;
/* loaded from: classes3.dex */
public class SignatureNotFoundException extends Exception {
    private static final long serialVersionUID = 1;

    public SignatureNotFoundException(String message) {
        super(message);
    }

    public SignatureNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
