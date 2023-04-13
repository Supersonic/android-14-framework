package com.android.internal.org.bouncycastle.crypto.encodings;

import com.android.internal.org.bouncycastle.crypto.AsymmetricBlockCipher;
import com.android.internal.org.bouncycastle.crypto.CipherParameters;
import com.android.internal.org.bouncycastle.crypto.CryptoServicesRegistrar;
import com.android.internal.org.bouncycastle.crypto.DataLengthException;
import com.android.internal.org.bouncycastle.crypto.Digest;
import com.android.internal.org.bouncycastle.crypto.InvalidCipherTextException;
import com.android.internal.org.bouncycastle.crypto.digests.AndroidDigestFactory;
import com.android.internal.org.bouncycastle.crypto.params.ParametersWithRandom;
import com.android.internal.org.bouncycastle.util.Arrays;
import java.security.SecureRandom;
/* loaded from: classes4.dex */
public class OAEPEncoding implements AsymmetricBlockCipher {
    private byte[] defHash;
    private AsymmetricBlockCipher engine;
    private boolean forEncryption;
    private Digest mgf1Hash;
    private SecureRandom random;

    public OAEPEncoding(AsymmetricBlockCipher cipher) {
        this(cipher, AndroidDigestFactory.getSHA1(), null);
    }

    public OAEPEncoding(AsymmetricBlockCipher cipher, Digest hash) {
        this(cipher, hash, null);
    }

    public OAEPEncoding(AsymmetricBlockCipher cipher, Digest hash, byte[] encodingParams) {
        this(cipher, hash, hash, encodingParams);
    }

    public OAEPEncoding(AsymmetricBlockCipher cipher, Digest hash, Digest mgf1Hash, byte[] encodingParams) {
        this.engine = cipher;
        this.mgf1Hash = mgf1Hash;
        this.defHash = new byte[hash.getDigestSize()];
        hash.reset();
        if (encodingParams != null) {
            hash.update(encodingParams, 0, encodingParams.length);
        }
        hash.doFinal(this.defHash, 0);
    }

    public AsymmetricBlockCipher getUnderlyingCipher() {
        return this.engine;
    }

    @Override // com.android.internal.org.bouncycastle.crypto.AsymmetricBlockCipher
    public void init(boolean forEncryption, CipherParameters param) {
        if (param instanceof ParametersWithRandom) {
            ParametersWithRandom rParam = (ParametersWithRandom) param;
            this.random = rParam.getRandom();
        } else {
            this.random = CryptoServicesRegistrar.getSecureRandom();
        }
        this.engine.init(forEncryption, param);
        this.forEncryption = forEncryption;
    }

    @Override // com.android.internal.org.bouncycastle.crypto.AsymmetricBlockCipher
    public int getInputBlockSize() {
        int baseBlockSize = this.engine.getInputBlockSize();
        if (this.forEncryption) {
            return (baseBlockSize - 1) - (this.defHash.length * 2);
        }
        return baseBlockSize;
    }

    @Override // com.android.internal.org.bouncycastle.crypto.AsymmetricBlockCipher
    public int getOutputBlockSize() {
        int baseBlockSize = this.engine.getOutputBlockSize();
        if (this.forEncryption) {
            return baseBlockSize;
        }
        return (baseBlockSize - 1) - (this.defHash.length * 2);
    }

    @Override // com.android.internal.org.bouncycastle.crypto.AsymmetricBlockCipher
    public byte[] processBlock(byte[] in, int inOff, int inLen) throws InvalidCipherTextException {
        if (this.forEncryption) {
            return encodeBlock(in, inOff, inLen);
        }
        return decodeBlock(in, inOff, inLen);
    }

    public byte[] encodeBlock(byte[] in, int inOff, int inLen) throws InvalidCipherTextException {
        if (inLen > getInputBlockSize()) {
            throw new DataLengthException("input data too long");
        }
        byte[] block = new byte[getInputBlockSize() + 1 + (this.defHash.length * 2)];
        System.arraycopy(in, inOff, block, block.length - inLen, inLen);
        block[(block.length - inLen) - 1] = 1;
        byte[] bArr = this.defHash;
        System.arraycopy(bArr, 0, block, bArr.length, bArr.length);
        byte[] seed = new byte[this.defHash.length];
        this.random.nextBytes(seed);
        byte[] mask = maskGeneratorFunction1(seed, 0, seed.length, block.length - this.defHash.length);
        for (int i = this.defHash.length; i != block.length; i++) {
            block[i] = (byte) (block[i] ^ mask[i - this.defHash.length]);
        }
        System.arraycopy(seed, 0, block, 0, this.defHash.length);
        byte[] bArr2 = this.defHash;
        byte[] mask2 = maskGeneratorFunction1(block, bArr2.length, block.length - bArr2.length, bArr2.length);
        for (int i2 = 0; i2 != this.defHash.length; i2++) {
            block[i2] = (byte) (block[i2] ^ mask2[i2]);
        }
        return this.engine.processBlock(block, 0, block.length);
    }

    public byte[] decodeBlock(byte[] in, int inOff, int inLen) throws InvalidCipherTextException {
        byte[] bArr;
        byte[] bArr2;
        byte[] data = this.engine.processBlock(in, inOff, inLen);
        byte[] block = new byte[this.engine.getOutputBlockSize()];
        boolean wrongData = block.length < (this.defHash.length * 2) + 1;
        if (data.length <= block.length) {
            System.arraycopy(data, 0, block, block.length - data.length, data.length);
        } else {
            System.arraycopy(data, 0, block, 0, block.length);
            wrongData = true;
        }
        byte[] bArr3 = this.defHash;
        byte[] mask = maskGeneratorFunction1(block, bArr3.length, block.length - bArr3.length, bArr3.length);
        int i = 0;
        while (true) {
            bArr = this.defHash;
            if (i == bArr.length) {
                break;
            }
            block[i] = (byte) (block[i] ^ mask[i]);
            i++;
        }
        int i2 = bArr.length;
        byte[] mask2 = maskGeneratorFunction1(block, 0, i2, block.length - bArr.length);
        for (int i3 = this.defHash.length; i3 != block.length; i3++) {
            block[i3] = (byte) (block[i3] ^ mask2[i3 - this.defHash.length]);
        }
        boolean defHashWrong = false;
        int i4 = 0;
        while (true) {
            bArr2 = this.defHash;
            if (i4 == bArr2.length) {
                break;
            }
            if (bArr2[i4] != block[bArr2.length + i4]) {
                defHashWrong = true;
            }
            i4++;
        }
        int start = block.length;
        for (int index = bArr2.length * 2; index != block.length; index++) {
            if ((block[index] != 0) & (start == block.length)) {
                start = index;
            }
        }
        int index2 = block.length;
        boolean dataStartWrong = (start > index2 - 1) | (block[start] != 1);
        int start2 = start + 1;
        if (defHashWrong | wrongData | dataStartWrong) {
            Arrays.fill(block, (byte) 0);
            throw new InvalidCipherTextException("data wrong");
        }
        byte[] output = new byte[block.length - start2];
        System.arraycopy(block, start2, output, 0, output.length);
        Arrays.fill(block, (byte) 0);
        return output;
    }

    private void ItoOSP(int i, byte[] sp) {
        sp[0] = (byte) (i >>> 24);
        sp[1] = (byte) (i >>> 16);
        sp[2] = (byte) (i >>> 8);
        sp[3] = (byte) (i >>> 0);
    }

    private byte[] maskGeneratorFunction1(byte[] Z, int zOff, int zLen, int length) {
        byte[] mask = new byte[length];
        byte[] hashBuf = new byte[this.mgf1Hash.getDigestSize()];
        byte[] C = new byte[4];
        int counter = 0;
        this.mgf1Hash.reset();
        while (counter < length / hashBuf.length) {
            ItoOSP(counter, C);
            this.mgf1Hash.update(Z, zOff, zLen);
            this.mgf1Hash.update(C, 0, C.length);
            this.mgf1Hash.doFinal(hashBuf, 0);
            System.arraycopy(hashBuf, 0, mask, hashBuf.length * counter, hashBuf.length);
            counter++;
        }
        if (hashBuf.length * counter < length) {
            ItoOSP(counter, C);
            this.mgf1Hash.update(Z, zOff, zLen);
            this.mgf1Hash.update(C, 0, C.length);
            this.mgf1Hash.doFinal(hashBuf, 0);
            System.arraycopy(hashBuf, 0, mask, hashBuf.length * counter, mask.length - (hashBuf.length * counter));
        }
        return mask;
    }
}
