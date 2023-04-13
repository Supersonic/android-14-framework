package android.security.keystore2;

import android.security.KeyStoreSecurityLevel;
import android.system.keystore2.KeyDescriptor;
import android.system.keystore2.KeyMetadata;
import java.math.BigInteger;
import java.security.interfaces.EdECPublicKey;
import java.security.spec.EdECPoint;
import java.security.spec.NamedParameterSpec;
import java.util.Arrays;
import java.util.Objects;
/* loaded from: classes3.dex */
public class AndroidKeyStoreEdECPublicKey extends AndroidKeyStorePublicKey implements EdECPublicKey {
    private static final byte[] DER_KEY_PREFIX = {48, 42, 48, 5, 6, 3, 43, 101, 112, 3, 33, 0};
    private static final int ED25519_KEY_SIZE_BYTES = 32;
    private byte[] mEncodedKey;
    private EdECPoint mPoint;

    public AndroidKeyStoreEdECPublicKey(KeyDescriptor descriptor, KeyMetadata metadata, String algorithm, KeyStoreSecurityLevel iSecurityLevel, byte[] encodedKey) {
        super(descriptor, metadata, encodedKey, algorithm, iSecurityLevel);
        this.mEncodedKey = encodedKey;
        int preambleLength = matchesPreamble(DER_KEY_PREFIX, encodedKey);
        if (preambleLength == 0) {
            throw new IllegalArgumentException("Key size is not correct size");
        }
        this.mPoint = pointFromKeyByteArray(Arrays.copyOfRange(encodedKey, preambleLength, encodedKey.length));
    }

    @Override // android.security.keystore2.AndroidKeyStorePublicKey
    AndroidKeyStorePrivateKey getPrivateKey() {
        return new AndroidKeyStoreEdECPrivateKey(getUserKeyDescriptor(), getKeyIdDescriptor().nspace, getAuthorizations(), "EdDSA", getSecurityLevel());
    }

    public NamedParameterSpec getParams() {
        return NamedParameterSpec.ED25519;
    }

    public EdECPoint getPoint() {
        return this.mPoint;
    }

    private static int matchesPreamble(byte[] preamble, byte[] encoded) {
        if (encoded.length == preamble.length + 32 && Arrays.compare(preamble, Arrays.copyOf(encoded, preamble.length)) == 0) {
            return preamble.length;
        }
        return 0;
    }

    private static EdECPoint pointFromKeyByteArray(byte[] coordinates) {
        Objects.requireNonNull(coordinates);
        boolean isOdd = (coordinates[coordinates.length - 1] & 128) != 0;
        int length = coordinates.length - 1;
        coordinates[length] = (byte) (coordinates[length] & Byte.MAX_VALUE);
        reverse(coordinates);
        BigInteger y = new BigInteger(1, coordinates);
        return new EdECPoint(isOdd, y);
    }

    private static void reverse(byte[] coordinateArray) {
        int start = 0;
        for (int end = coordinateArray.length - 1; start < end; end--) {
            byte tmp = coordinateArray[start];
            coordinateArray[start] = coordinateArray[end];
            coordinateArray[end] = tmp;
            start++;
        }
    }

    @Override // android.security.keystore2.AndroidKeyStorePublicKey, android.security.keystore2.AndroidKeyStoreKey, java.security.Key
    public byte[] getEncoded() {
        return (byte[]) this.mEncodedKey.clone();
    }
}
