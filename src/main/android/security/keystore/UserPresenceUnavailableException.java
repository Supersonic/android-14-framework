package android.security.keystore;

import java.security.InvalidKeyException;
/* loaded from: classes3.dex */
public class UserPresenceUnavailableException extends InvalidKeyException {
    public UserPresenceUnavailableException() {
        super("No Strong Box available.");
    }

    public UserPresenceUnavailableException(String message) {
        super(message);
    }

    public UserPresenceUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }
}
