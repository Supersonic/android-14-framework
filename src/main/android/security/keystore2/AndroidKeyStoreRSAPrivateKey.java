package android.security.keystore2;

import android.security.KeyStoreSecurityLevel;
import android.security.keystore.KeyProperties;
import android.system.keystore2.Authorization;
import android.system.keystore2.KeyDescriptor;
import java.math.BigInteger;
import java.security.interfaces.RSAKey;
/* loaded from: classes3.dex */
public class AndroidKeyStoreRSAPrivateKey extends AndroidKeyStorePrivateKey implements RSAKey {
    private final BigInteger mModulus;

    public AndroidKeyStoreRSAPrivateKey(KeyDescriptor descriptor, long keyId, Authorization[] authorizations, KeyStoreSecurityLevel securityLevel, BigInteger modulus) {
        super(descriptor, keyId, authorizations, KeyProperties.KEY_ALGORITHM_RSA, securityLevel);
        this.mModulus = modulus;
    }

    @Override // java.security.interfaces.RSAKey
    public BigInteger getModulus() {
        return this.mModulus;
    }
}
