package android.security.keystore2;

import android.security.KeyStoreSecurityLevel;
import android.security.keystore.KeyProperties;
import android.system.keystore2.KeyDescriptor;
import android.system.keystore2.KeyMetadata;
import java.math.BigInteger;
import java.security.interfaces.RSAPublicKey;
/* loaded from: classes3.dex */
public class AndroidKeyStoreRSAPublicKey extends AndroidKeyStorePublicKey implements RSAPublicKey {
    private final BigInteger mModulus;
    private final BigInteger mPublicExponent;

    public AndroidKeyStoreRSAPublicKey(KeyDescriptor descriptor, KeyMetadata metadata, byte[] x509EncodedForm, KeyStoreSecurityLevel securityLevel, BigInteger modulus, BigInteger publicExponent) {
        super(descriptor, metadata, x509EncodedForm, KeyProperties.KEY_ALGORITHM_RSA, securityLevel);
        this.mModulus = modulus;
        this.mPublicExponent = publicExponent;
    }

    public AndroidKeyStoreRSAPublicKey(KeyDescriptor descriptor, KeyMetadata metadata, KeyStoreSecurityLevel securityLevel, RSAPublicKey info) {
        this(descriptor, metadata, info.getEncoded(), securityLevel, info.getModulus(), info.getPublicExponent());
        if (!"X.509".equalsIgnoreCase(info.getFormat())) {
            throw new IllegalArgumentException("Unsupported key export format: " + info.getFormat());
        }
    }

    @Override // android.security.keystore2.AndroidKeyStorePublicKey
    public AndroidKeyStorePrivateKey getPrivateKey() {
        return new AndroidKeyStoreRSAPrivateKey(getUserKeyDescriptor(), getKeyIdDescriptor().nspace, getAuthorizations(), getSecurityLevel(), this.mModulus);
    }

    @Override // java.security.interfaces.RSAKey
    public BigInteger getModulus() {
        return this.mModulus;
    }

    @Override // java.security.interfaces.RSAPublicKey
    public BigInteger getPublicExponent() {
        return this.mPublicExponent;
    }
}
