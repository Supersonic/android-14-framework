package android.security.keystore;

import java.security.InvalidKeyException;
/* loaded from: classes3.dex */
public class KeyNotYetValidException extends InvalidKeyException {
    public KeyNotYetValidException() {
        super("Key not yet valid");
    }

    public KeyNotYetValidException(String message) {
        super(message);
    }

    public KeyNotYetValidException(String message, Throwable cause) {
        super(message, cause);
    }
}
