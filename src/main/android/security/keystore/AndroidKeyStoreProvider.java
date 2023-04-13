package android.security.keystore;

import android.annotation.SystemApi;
import android.security.keystore2.AndroidKeyStoreLoadStoreParameter;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Provider;
import java.security.cert.CertificateException;
@SystemApi
/* loaded from: classes3.dex */
public class AndroidKeyStoreProvider extends Provider {
    private static final String PROVIDER_NAME = "AndroidKeyStore";

    public AndroidKeyStoreProvider(String name) {
        super(name, 1.0d, "Android KeyStore security provider");
        throw new IllegalStateException("Should not be instantiated.");
    }

    public static long getKeyStoreOperationHandle(Object cryptoPrimitive) {
        return android.security.keystore2.AndroidKeyStoreProvider.getKeyStoreOperationHandle(cryptoPrimitive);
    }

    @SystemApi
    public static KeyStore getKeyStoreForUid(int uid) throws KeyStoreException, NoSuchProviderException {
        KeyStore.LoadStoreParameter loadParameter = new AndroidKeyStoreLoadStoreParameter(KeyProperties.legacyUidToNamespace(uid));
        KeyStore result = KeyStore.getInstance("AndroidKeyStore");
        try {
            result.load(loadParameter);
            return result;
        } catch (IOException | NoSuchAlgorithmException | CertificateException e) {
            throw new KeyStoreException("Failed to load AndroidKeyStore KeyStore for UID " + uid, e);
        }
    }
}
