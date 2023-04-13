package android.security.keystore;

import java.security.InvalidKeyException;
/* loaded from: classes3.dex */
public class KeyExpiredException extends InvalidKeyException {
    public KeyExpiredException() {
        super("Key expired");
    }

    public KeyExpiredException(String message) {
        super(message);
    }

    public KeyExpiredException(String message, Throwable cause) {
        super(message, cause);
    }
}
