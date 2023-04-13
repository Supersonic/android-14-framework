package android.security.keystore;

import java.security.InvalidKeyException;
/* loaded from: classes3.dex */
public class KeyPermanentlyInvalidatedException extends InvalidKeyException {
    public KeyPermanentlyInvalidatedException() {
        super("Key permanently invalidated");
    }

    public KeyPermanentlyInvalidatedException(String message) {
        super(message);
    }

    public KeyPermanentlyInvalidatedException(String message, Throwable cause) {
        super(message, cause);
    }
}
