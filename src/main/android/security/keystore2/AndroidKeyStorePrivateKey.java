package android.security.keystore2;

import android.security.KeyStoreSecurityLevel;
import android.system.keystore2.Authorization;
import android.system.keystore2.KeyDescriptor;
import java.security.PrivateKey;
/* loaded from: classes3.dex */
public class AndroidKeyStorePrivateKey extends AndroidKeyStoreKey implements PrivateKey {
    public AndroidKeyStorePrivateKey(KeyDescriptor descriptor, long keyId, Authorization[] authorizations, String algorithm, KeyStoreSecurityLevel securityLevel) {
        super(descriptor, keyId, authorizations, algorithm, securityLevel);
    }
}
