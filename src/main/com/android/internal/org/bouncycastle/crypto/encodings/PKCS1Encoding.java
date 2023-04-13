package com.android.internal.org.bouncycastle.crypto.encodings;

import com.android.internal.org.bouncycastle.crypto.AsymmetricBlockCipher;
import com.android.internal.org.bouncycastle.crypto.CipherParameters;
import com.android.internal.org.bouncycastle.crypto.CryptoServicesRegistrar;
import com.android.internal.org.bouncycastle.crypto.InvalidCipherTextException;
import com.android.internal.org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import com.android.internal.org.bouncycastle.crypto.params.ParametersWithRandom;
import com.android.internal.org.bouncycastle.util.Arrays;
import com.android.internal.org.bouncycastle.util.Properties;
import java.security.SecureRandom;
/* loaded from: classes4.dex */
public class PKCS1Encoding implements AsymmetricBlockCipher {
    private static final int HEADER_LENGTH = 10;
    public static final String NOT_STRICT_LENGTH_ENABLED_PROPERTY = "com.android.internal.org.bouncycastle.pkcs1.not_strict";
    public static final String STRICT_LENGTH_ENABLED_PROPERTY = "com.android.internal.org.bouncycastle.pkcs1.strict";
    private byte[] blockBuffer;
    private AsymmetricBlockCipher engine;
    private byte[] fallback;
    private boolean forEncryption;
    private boolean forPrivateKey;
    private int pLen;
    private SecureRandom random;
    private boolean useStrictLength;

    public PKCS1Encoding(AsymmetricBlockCipher cipher) {
        this.pLen = -1;
        this.fallback = null;
        this.engine = cipher;
        this.useStrictLength = useStrict();
    }

    public PKCS1Encoding(AsymmetricBlockCipher cipher, int pLen) {
        this.pLen = -1;
        this.fallback = null;
        this.engine = cipher;
        this.useStrictLength = useStrict();
        this.pLen = pLen;
    }

    public PKCS1Encoding(AsymmetricBlockCipher cipher, byte[] fallback) {
        this.pLen = -1;
        this.fallback = null;
        this.engine = cipher;
        this.useStrictLength = useStrict();
        this.fallback = fallback;
        this.pLen = fallback.length;
    }

    private boolean useStrict() {
        if (Properties.isOverrideSetTo(NOT_STRICT_LENGTH_ENABLED_PROPERTY, true)) {
            return false;
        }
        return !Properties.isOverrideSetTo(STRICT_LENGTH_ENABLED_PROPERTY, false);
    }

    public AsymmetricBlockCipher getUnderlyingCipher() {
        return this.engine;
    }

    @Override // com.android.internal.org.bouncycastle.crypto.AsymmetricBlockCipher
    public void init(boolean forEncryption, CipherParameters param) {
        AsymmetricKeyParameter kParam;
        if (param instanceof ParametersWithRandom) {
            ParametersWithRandom rParam = (ParametersWithRandom) param;
            this.random = rParam.getRandom();
            kParam = (AsymmetricKeyParameter) rParam.getParameters();
        } else {
            kParam = (AsymmetricKeyParameter) param;
            if (!kParam.isPrivate() && forEncryption) {
                this.random = CryptoServicesRegistrar.getSecureRandom();
            }
        }
        this.engine.init(forEncryption, param);
        this.forPrivateKey = kParam.isPrivate();
        this.forEncryption = forEncryption;
        this.blockBuffer = new byte[this.engine.getOutputBlockSize()];
        if (this.pLen > 0 && this.fallback == null && this.random == null) {
            throw new IllegalArgumentException("encoder requires random");
        }
    }

    @Override // com.android.internal.org.bouncycastle.crypto.AsymmetricBlockCipher
    public int getInputBlockSize() {
        int baseBlockSize = this.engine.getInputBlockSize();
        if (this.forEncryption) {
            return baseBlockSize - 10;
        }
        return baseBlockSize;
    }

    @Override // com.android.internal.org.bouncycastle.crypto.AsymmetricBlockCipher
    public int getOutputBlockSize() {
        int baseBlockSize = this.engine.getOutputBlockSize();
        if (this.forEncryption) {
            return baseBlockSize;
        }
        return baseBlockSize - 10;
    }

    @Override // com.android.internal.org.bouncycastle.crypto.AsymmetricBlockCipher
    public byte[] processBlock(byte[] in, int inOff, int inLen) throws InvalidCipherTextException {
        if (this.forEncryption) {
            return encodeBlock(in, inOff, inLen);
        }
        return decodeBlock(in, inOff, inLen);
    }

    private byte[] encodeBlock(byte[] in, int inOff, int inLen) throws InvalidCipherTextException {
        if (inLen > getInputBlockSize()) {
            throw new IllegalArgumentException("input data too large");
        }
        byte[] block = new byte[this.engine.getInputBlockSize()];
        if (this.forPrivateKey) {
            block[0] = 1;
            for (int i = 1; i != (block.length - inLen) - 1; i++) {
                block[i] = -1;
            }
        } else {
            this.random.nextBytes(block);
            block[0] = 2;
            for (int i2 = 1; i2 != (block.length - inLen) - 1; i2++) {
                while (block[i2] == 0) {
                    block[i2] = (byte) this.random.nextInt();
                }
            }
        }
        int i3 = block.length;
        block[(i3 - inLen) - 1] = 0;
        System.arraycopy(in, inOff, block, block.length - inLen, inLen);
        return this.engine.processBlock(block, 0, block.length);
    }

    private static int checkPkcs1Encoding(byte[] encoded, int pLen) {
        int correct = 0 | (encoded[0] ^ 2);
        int plen = encoded.length - (pLen + 1);
        for (int i = 1; i < plen; i++) {
            int tmp = encoded[i];
            int tmp2 = tmp | (tmp >> 1);
            int tmp3 = tmp2 | (tmp2 >> 2);
            correct |= ((tmp3 | (tmp3 >> 4)) & 1) - 1;
        }
        int i2 = encoded.length;
        int correct2 = correct | encoded[i2 - (pLen + 1)];
        int correct3 = correct2 | (correct2 >> 1);
        int correct4 = correct3 | (correct3 >> 2);
        return ~(((correct4 | (correct4 >> 4)) & 1) - 1);
    }

    private byte[] decodeBlockOrRandom(byte[] in, int inOff, int inLen) throws InvalidCipherTextException {
        byte[] random;
        if (!this.forPrivateKey) {
            throw new InvalidCipherTextException("sorry, this method is only for decryption, not for signing");
        }
        byte[] block = this.engine.processBlock(in, inOff, inLen);
        if (this.fallback == null) {
            random = new byte[this.pLen];
            this.random.nextBytes(random);
        } else {
            random = this.fallback;
        }
        byte[] data = this.useStrictLength & (block.length != this.engine.getOutputBlockSize()) ? this.blockBuffer : block;
        int correct = checkPkcs1Encoding(data, this.pLen);
        byte[] result = new byte[this.pLen];
        int i = 0;
        while (true) {
            int i2 = this.pLen;
            if (i < i2) {
                result[i] = (byte) ((data[(data.length - i2) + i] & (~correct)) | (random[i] & correct));
                i++;
            } else {
                Arrays.fill(data, (byte) 0);
                return result;
            }
        }
    }

    private byte[] decodeBlock(byte[] in, int inOff, int inLen) throws InvalidCipherTextException {
        byte[] data;
        boolean badType;
        if (this.pLen != -1) {
            return decodeBlockOrRandom(in, inOff, inLen);
        }
        byte[] block = this.engine.processBlock(in, inOff, inLen);
        boolean incorrectLength = this.useStrictLength & (block.length != this.engine.getOutputBlockSize());
        if (block.length < getOutputBlockSize()) {
            data = this.blockBuffer;
        } else {
            data = block;
        }
        byte type = data[0];
        if (this.forPrivateKey) {
            badType = type != 2;
        } else {
            badType = type != 1;
        }
        int start = findStart(type, data) + 1;
        if ((start < 10) | badType) {
            Arrays.fill(data, (byte) 0);
            throw new InvalidCipherTextException("block incorrect");
        } else if (incorrectLength) {
            Arrays.fill(data, (byte) 0);
            throw new InvalidCipherTextException("block incorrect size");
        } else {
            byte[] result = new byte[data.length - start];
            System.arraycopy(data, start, result, 0, result.length);
            return result;
        }
    }

    private int findStart(byte type, byte[] block) throws InvalidCipherTextException {
        int start = -1;
        boolean padErr = false;
        for (int i = 1; i != block.length; i++) {
            byte pad = block[i];
            boolean z = false;
            if ((pad == 0) & (start < 0)) {
                start = i;
            }
            boolean z2 = (type == 1) & (start < 0);
            if (pad != -1) {
                z = true;
            }
            padErr |= z2 & z;
        }
        if (padErr) {
            return -1;
        }
        return start;
    }
}
