package android.security.keystore;

import android.security.KeyStoreException;
import java.security.ProviderException;
/* loaded from: classes3.dex */
public class StrongBoxUnavailableException extends ProviderException {
    public StrongBoxUnavailableException() {
    }

    public StrongBoxUnavailableException(String message) {
        super(message, new KeyStoreException(-68, "No StrongBox available"));
    }

    public StrongBoxUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }

    public StrongBoxUnavailableException(Throwable cause) {
        super(cause);
    }
}
