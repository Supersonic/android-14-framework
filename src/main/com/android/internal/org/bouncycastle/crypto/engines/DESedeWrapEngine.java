package com.android.internal.org.bouncycastle.crypto.engines;

import android.security.keystore.KeyProperties;
import com.android.internal.org.bouncycastle.crypto.CipherParameters;
import com.android.internal.org.bouncycastle.crypto.CryptoServicesRegistrar;
import com.android.internal.org.bouncycastle.crypto.Digest;
import com.android.internal.org.bouncycastle.crypto.InvalidCipherTextException;
import com.android.internal.org.bouncycastle.crypto.Wrapper;
import com.android.internal.org.bouncycastle.crypto.digests.AndroidDigestFactory;
import com.android.internal.org.bouncycastle.crypto.modes.CBCBlockCipher;
import com.android.internal.org.bouncycastle.crypto.params.KeyParameter;
import com.android.internal.org.bouncycastle.crypto.params.ParametersWithIV;
import com.android.internal.org.bouncycastle.crypto.params.ParametersWithRandom;
import com.android.internal.org.bouncycastle.util.Arrays;
import java.security.SecureRandom;
/* loaded from: classes4.dex */
public class DESedeWrapEngine implements Wrapper {
    private static final byte[] IV2 = {74, -35, -94, 44, 121, -24, 33, 5};
    private CBCBlockCipher engine;
    private boolean forWrapping;

    /* renamed from: iv */
    private byte[] f747iv;
    private KeyParameter param;
    private ParametersWithIV paramPlusIV;
    Digest sha1 = AndroidDigestFactory.getSHA1();
    byte[] digest = new byte[20];

    @Override // com.android.internal.org.bouncycastle.crypto.Wrapper
    public void init(boolean forWrapping, CipherParameters param) {
        SecureRandom sr;
        this.forWrapping = forWrapping;
        this.engine = new CBCBlockCipher(new DESedeEngine());
        if (param instanceof ParametersWithRandom) {
            ParametersWithRandom pr = (ParametersWithRandom) param;
            param = pr.getParameters();
            sr = pr.getRandom();
        } else {
            sr = CryptoServicesRegistrar.getSecureRandom();
        }
        if (param instanceof KeyParameter) {
            this.param = (KeyParameter) param;
            if (this.forWrapping) {
                byte[] bArr = new byte[8];
                this.f747iv = bArr;
                sr.nextBytes(bArr);
                this.paramPlusIV = new ParametersWithIV(this.param, this.f747iv);
            }
        } else if (param instanceof ParametersWithIV) {
            ParametersWithIV parametersWithIV = (ParametersWithIV) param;
            this.paramPlusIV = parametersWithIV;
            this.f747iv = parametersWithIV.getIV();
            this.param = (KeyParameter) this.paramPlusIV.getParameters();
            if (this.forWrapping) {
                byte[] bArr2 = this.f747iv;
                if (bArr2 == null || bArr2.length != 8) {
                    throw new IllegalArgumentException("IV is not 8 octets");
                }
                return;
            }
            throw new IllegalArgumentException("You should not supply an IV for unwrapping");
        }
    }

    @Override // com.android.internal.org.bouncycastle.crypto.Wrapper
    public String getAlgorithmName() {
        return KeyProperties.KEY_ALGORITHM_3DES;
    }

    @Override // com.android.internal.org.bouncycastle.crypto.Wrapper
    public byte[] wrap(byte[] in, int inOff, int inLen) {
        if (!this.forWrapping) {
            throw new IllegalStateException("Not initialized for wrapping");
        }
        byte[] keyToBeWrapped = new byte[inLen];
        System.arraycopy(in, inOff, keyToBeWrapped, 0, inLen);
        byte[] CKS = calculateCMSKeyChecksum(keyToBeWrapped);
        byte[] WKCKS = new byte[keyToBeWrapped.length + CKS.length];
        System.arraycopy(keyToBeWrapped, 0, WKCKS, 0, keyToBeWrapped.length);
        System.arraycopy(CKS, 0, WKCKS, keyToBeWrapped.length, CKS.length);
        int blockSize = this.engine.getBlockSize();
        if (WKCKS.length % blockSize != 0) {
            throw new IllegalStateException("Not multiple of block length");
        }
        this.engine.init(true, this.paramPlusIV);
        byte[] TEMP1 = new byte[WKCKS.length];
        for (int currentBytePos = 0; currentBytePos != WKCKS.length; currentBytePos += blockSize) {
            this.engine.processBlock(WKCKS, currentBytePos, TEMP1, currentBytePos);
        }
        byte[] bArr = this.f747iv;
        byte[] TEMP2 = new byte[bArr.length + TEMP1.length];
        System.arraycopy(bArr, 0, TEMP2, 0, bArr.length);
        System.arraycopy(TEMP1, 0, TEMP2, this.f747iv.length, TEMP1.length);
        byte[] TEMP3 = reverse(TEMP2);
        ParametersWithIV param2 = new ParametersWithIV(this.param, IV2);
        this.engine.init(true, param2);
        for (int currentBytePos2 = 0; currentBytePos2 != TEMP3.length; currentBytePos2 += blockSize) {
            this.engine.processBlock(TEMP3, currentBytePos2, TEMP3, currentBytePos2);
        }
        return TEMP3;
    }

    @Override // com.android.internal.org.bouncycastle.crypto.Wrapper
    public byte[] unwrap(byte[] in, int inOff, int inLen) throws InvalidCipherTextException {
        if (this.forWrapping) {
            throw new IllegalStateException("Not set for unwrapping");
        }
        if (in == null) {
            throw new InvalidCipherTextException("Null pointer as ciphertext");
        }
        int blockSize = this.engine.getBlockSize();
        if (inLen % blockSize != 0) {
            throw new InvalidCipherTextException("Ciphertext not multiple of " + blockSize);
        }
        ParametersWithIV param2 = new ParametersWithIV(this.param, IV2);
        this.engine.init(false, param2);
        byte[] TEMP3 = new byte[inLen];
        for (int currentBytePos = 0; currentBytePos != inLen; currentBytePos += blockSize) {
            this.engine.processBlock(in, inOff + currentBytePos, TEMP3, currentBytePos);
        }
        byte[] TEMP2 = reverse(TEMP3);
        byte[] bArr = new byte[8];
        this.f747iv = bArr;
        byte[] TEMP1 = new byte[TEMP2.length - 8];
        System.arraycopy(TEMP2, 0, bArr, 0, 8);
        System.arraycopy(TEMP2, 8, TEMP1, 0, TEMP2.length - 8);
        ParametersWithIV parametersWithIV = new ParametersWithIV(this.param, this.f747iv);
        this.paramPlusIV = parametersWithIV;
        this.engine.init(false, parametersWithIV);
        byte[] WKCKS = new byte[TEMP1.length];
        for (int currentBytePos2 = 0; currentBytePos2 != WKCKS.length; currentBytePos2 += blockSize) {
            this.engine.processBlock(TEMP1, currentBytePos2, WKCKS, currentBytePos2);
        }
        int currentBytePos3 = WKCKS.length;
        byte[] result = new byte[currentBytePos3 - 8];
        byte[] CKStoBeVerified = new byte[8];
        System.arraycopy(WKCKS, 0, result, 0, WKCKS.length - 8);
        System.arraycopy(WKCKS, WKCKS.length - 8, CKStoBeVerified, 0, 8);
        if (!checkCMSKeyChecksum(result, CKStoBeVerified)) {
            throw new InvalidCipherTextException("Checksum inside ciphertext is corrupted");
        }
        return result;
    }

    private byte[] calculateCMSKeyChecksum(byte[] key) {
        byte[] result = new byte[8];
        this.sha1.update(key, 0, key.length);
        this.sha1.doFinal(this.digest, 0);
        System.arraycopy(this.digest, 0, result, 0, 8);
        return result;
    }

    private boolean checkCMSKeyChecksum(byte[] key, byte[] checksum) {
        return Arrays.constantTimeAreEqual(calculateCMSKeyChecksum(key), checksum);
    }

    private static byte[] reverse(byte[] bs) {
        byte[] result = new byte[bs.length];
        for (int i = 0; i < bs.length; i++) {
            result[i] = bs[bs.length - (i + 1)];
        }
        return result;
    }
}
