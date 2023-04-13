package android.security.keystore2;

import android.security.KeyStoreSecurityLevel;
import android.system.keystore2.Authorization;
import android.system.keystore2.KeyDescriptor;
import java.security.interfaces.XECPrivateKey;
import java.security.spec.NamedParameterSpec;
import java.util.Optional;
/* loaded from: classes3.dex */
public class AndroidKeyStoreXDHPrivateKey extends AndroidKeyStorePrivateKey implements XECPrivateKey {
    public AndroidKeyStoreXDHPrivateKey(KeyDescriptor descriptor, long keyId, Authorization[] authorizations, String algorithm, KeyStoreSecurityLevel securityLevel) {
        super(descriptor, keyId, authorizations, algorithm, securityLevel);
    }

    public NamedParameterSpec getParams() {
        return NamedParameterSpec.X25519;
    }

    public Optional<byte[]> getScalar() {
        return Optional.empty();
    }
}
