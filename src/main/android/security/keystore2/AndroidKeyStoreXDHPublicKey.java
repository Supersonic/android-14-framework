package android.security.keystore2;

import android.security.KeyStoreSecurityLevel;
import android.security.keystore.KeyProperties;
import android.system.keystore2.KeyDescriptor;
import android.system.keystore2.KeyMetadata;
import java.math.BigInteger;
import java.security.interfaces.XECPublicKey;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.NamedParameterSpec;
import java.util.Arrays;
/* loaded from: classes3.dex */
public class AndroidKeyStoreXDHPublicKey extends AndroidKeyStorePublicKey implements XECPublicKey {
    private static final int X25519_KEY_SIZE_BYTES = 32;
    private static final byte[] X509_PREAMBLE = {48, 42, 48, 5, 6, 3, 43, 101, 110, 3, 33, 0};
    private static final byte[] X509_PREAMBLE_WITH_NULL = {48, 44, 48, 7, 6, 3, 43, 101, 110, 5, 0, 3, 33, 0};
    private final byte[] mEncodedKey;
    private final int mPreambleLength;

    public AndroidKeyStoreXDHPublicKey(KeyDescriptor descriptor, KeyMetadata metadata, String algorithm, KeyStoreSecurityLevel iSecurityLevel, byte[] encodedKey) {
        super(descriptor, metadata, encodedKey, algorithm, iSecurityLevel);
        this.mEncodedKey = encodedKey;
        if (encodedKey == null) {
            throw new IllegalArgumentException("empty encoded key.");
        }
        int matchesPreamble = matchesPreamble(X509_PREAMBLE, encodedKey) | matchesPreamble(X509_PREAMBLE_WITH_NULL, encodedKey);
        this.mPreambleLength = matchesPreamble;
        if (matchesPreamble == 0) {
            throw new IllegalArgumentException("Key size is not correct size");
        }
    }

    private static int matchesPreamble(byte[] preamble, byte[] encoded) {
        if (encoded.length == preamble.length + 32 && Arrays.compare(preamble, 0, preamble.length, encoded, 0, preamble.length) == 0) {
            return preamble.length;
        }
        return 0;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // android.security.keystore2.AndroidKeyStorePublicKey
    public AndroidKeyStorePrivateKey getPrivateKey() {
        return new AndroidKeyStoreXDHPrivateKey(getUserKeyDescriptor(), getKeyIdDescriptor().nspace, getAuthorizations(), KeyProperties.KEY_ALGORITHM_XDH, getSecurityLevel());
    }

    public BigInteger getU() {
        byte[] bArr = this.mEncodedKey;
        return new BigInteger(Arrays.copyOfRange(bArr, this.mPreambleLength, bArr.length));
    }

    @Override // android.security.keystore2.AndroidKeyStorePublicKey, android.security.keystore2.AndroidKeyStoreKey, java.security.Key
    public byte[] getEncoded() {
        return (byte[]) this.mEncodedKey.clone();
    }

    @Override // android.security.keystore2.AndroidKeyStoreKey, java.security.Key
    public String getAlgorithm() {
        return KeyProperties.KEY_ALGORITHM_XDH;
    }

    @Override // android.security.keystore2.AndroidKeyStorePublicKey, android.security.keystore2.AndroidKeyStoreKey, java.security.Key
    public String getFormat() {
        return "x.509";
    }

    public AlgorithmParameterSpec getParams() {
        return NamedParameterSpec.X25519;
    }
}
