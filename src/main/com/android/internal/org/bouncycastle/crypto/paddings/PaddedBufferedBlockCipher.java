package com.android.internal.org.bouncycastle.crypto.paddings;

import com.android.internal.org.bouncycastle.crypto.BlockCipher;
import com.android.internal.org.bouncycastle.crypto.BufferedBlockCipher;
import com.android.internal.org.bouncycastle.crypto.CipherParameters;
import com.android.internal.org.bouncycastle.crypto.DataLengthException;
import com.android.internal.org.bouncycastle.crypto.InvalidCipherTextException;
import com.android.internal.org.bouncycastle.crypto.OutputLengthException;
import com.android.internal.org.bouncycastle.crypto.params.ParametersWithRandom;
/* loaded from: classes4.dex */
public class PaddedBufferedBlockCipher extends BufferedBlockCipher {
    BlockCipherPadding padding;

    public PaddedBufferedBlockCipher(BlockCipher cipher, BlockCipherPadding padding) {
        this.cipher = cipher;
        this.padding = padding;
        this.buf = new byte[cipher.getBlockSize()];
        this.bufOff = 0;
    }

    public PaddedBufferedBlockCipher(BlockCipher cipher) {
        this(cipher, new PKCS7Padding());
    }

    @Override // com.android.internal.org.bouncycastle.crypto.BufferedBlockCipher
    public void init(boolean forEncryption, CipherParameters params) throws IllegalArgumentException {
        this.forEncryption = forEncryption;
        reset();
        if (params instanceof ParametersWithRandom) {
            ParametersWithRandom p = (ParametersWithRandom) params;
            this.padding.init(p.getRandom());
            this.cipher.init(forEncryption, p.getParameters());
            return;
        }
        this.padding.init(null);
        this.cipher.init(forEncryption, params);
    }

    @Override // com.android.internal.org.bouncycastle.crypto.BufferedBlockCipher
    public int getOutputSize(int len) {
        int total = this.bufOff + len;
        int leftOver = total % this.buf.length;
        if (leftOver == 0) {
            if (this.forEncryption) {
                return this.buf.length + total;
            }
            return total;
        }
        return (total - leftOver) + this.buf.length;
    }

    @Override // com.android.internal.org.bouncycastle.crypto.BufferedBlockCipher
    public int getUpdateOutputSize(int len) {
        int total = this.bufOff + len;
        int leftOver = total % this.buf.length;
        if (leftOver == 0) {
            return Math.max(0, total - this.buf.length);
        }
        return total - leftOver;
    }

    @Override // com.android.internal.org.bouncycastle.crypto.BufferedBlockCipher
    public int processByte(byte in, byte[] out, int outOff) throws DataLengthException, IllegalStateException {
        int resultLen = 0;
        if (this.bufOff == this.buf.length) {
            resultLen = this.cipher.processBlock(this.buf, 0, out, outOff);
            this.bufOff = 0;
        }
        byte[] bArr = this.buf;
        int i = this.bufOff;
        this.bufOff = i + 1;
        bArr[i] = in;
        return resultLen;
    }

    @Override // com.android.internal.org.bouncycastle.crypto.BufferedBlockCipher
    public int processBytes(byte[] in, int inOff, int len, byte[] out, int outOff) throws DataLengthException, IllegalStateException {
        if (len < 0) {
            throw new IllegalArgumentException("Can't have a negative input length!");
        }
        int blockSize = getBlockSize();
        int length = getUpdateOutputSize(len);
        if (length > 0 && outOff + length > out.length) {
            throw new OutputLengthException("output buffer too short");
        }
        int resultLen = 0;
        int gapLen = this.buf.length - this.bufOff;
        if (len > gapLen) {
            System.arraycopy(in, inOff, this.buf, this.bufOff, gapLen);
            resultLen = 0 + this.cipher.processBlock(this.buf, 0, out, outOff);
            this.bufOff = 0;
            len -= gapLen;
            inOff += gapLen;
            while (len > this.buf.length) {
                resultLen += this.cipher.processBlock(in, inOff, out, outOff + resultLen);
                len -= blockSize;
                inOff += blockSize;
            }
        }
        System.arraycopy(in, inOff, this.buf, this.bufOff, len);
        this.bufOff += len;
        return resultLen;
    }

    @Override // com.android.internal.org.bouncycastle.crypto.BufferedBlockCipher
    public int doFinal(byte[] out, int outOff) throws DataLengthException, IllegalStateException, InvalidCipherTextException {
        int blockSize = this.cipher.getBlockSize();
        int resultLen = 0;
        if (this.forEncryption) {
            if (this.bufOff == blockSize) {
                if ((blockSize * 2) + outOff > out.length) {
                    reset();
                    throw new OutputLengthException("output buffer too short");
                }
                resultLen = this.cipher.processBlock(this.buf, 0, out, outOff);
                this.bufOff = 0;
            }
            this.padding.addPadding(this.buf, this.bufOff);
            return resultLen + this.cipher.processBlock(this.buf, 0, out, outOff + resultLen);
        } else if (this.bufOff == blockSize) {
            int resultLen2 = this.cipher.processBlock(this.buf, 0, this.buf, 0);
            this.bufOff = 0;
            try {
                int resultLen3 = resultLen2 - this.padding.padCount(this.buf);
                System.arraycopy(this.buf, 0, out, outOff, resultLen3);
                return resultLen3;
            } finally {
                reset();
            }
        } else {
            reset();
            throw new DataLengthException("last block incomplete in decryption");
        }
    }
}
