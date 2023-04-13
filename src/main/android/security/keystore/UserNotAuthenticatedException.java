package android.security.keystore;

import java.security.InvalidKeyException;
/* loaded from: classes3.dex */
public class UserNotAuthenticatedException extends InvalidKeyException {
    public UserNotAuthenticatedException() {
        super("User not authenticated");
    }

    public UserNotAuthenticatedException(String message) {
        super(message);
    }

    public UserNotAuthenticatedException(String message, Throwable cause) {
        super(message, cause);
    }
}
