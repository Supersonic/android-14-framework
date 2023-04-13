package android.security.keystore;

import java.security.ProviderException;
/* loaded from: classes3.dex */
public class KeyStoreConnectException extends ProviderException {
    public KeyStoreConnectException() {
        super("Failed to communicate with keystore service");
    }
}
