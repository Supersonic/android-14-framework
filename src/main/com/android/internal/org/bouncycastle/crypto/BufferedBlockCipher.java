package com.android.internal.org.bouncycastle.crypto;
/* loaded from: classes4.dex */
public class BufferedBlockCipher {
    protected byte[] buf;
    protected int bufOff;
    protected BlockCipher cipher;
    protected boolean forEncryption;
    protected boolean partialBlockOkay;
    protected boolean pgpCFB;

    /* JADX INFO: Access modifiers changed from: protected */
    public BufferedBlockCipher() {
    }

    public BufferedBlockCipher(BlockCipher cipher) {
        this.cipher = cipher;
        this.buf = new byte[cipher.getBlockSize()];
        boolean z = false;
        this.bufOff = 0;
        String name = cipher.getAlgorithmName();
        int idx = name.indexOf(47) + 1;
        boolean z2 = idx > 0 && name.startsWith("PGP", idx);
        this.pgpCFB = z2;
        if (z2 || (cipher instanceof StreamCipher)) {
            this.partialBlockOkay = true;
            return;
        }
        if (idx > 0 && name.startsWith("OpenPGP", idx)) {
            z = true;
        }
        this.partialBlockOkay = z;
    }

    public BlockCipher getUnderlyingCipher() {
        return this.cipher;
    }

    public void init(boolean forEncryption, CipherParameters params) throws IllegalArgumentException {
        this.forEncryption = forEncryption;
        reset();
        this.cipher.init(forEncryption, params);
    }

    public int getBlockSize() {
        return this.cipher.getBlockSize();
    }

    public int getUpdateOutputSize(int len) {
        int leftOver;
        int total = this.bufOff + len;
        if (this.pgpCFB) {
            if (this.forEncryption) {
                leftOver = (total % this.buf.length) - (this.cipher.getBlockSize() + 2);
            } else {
                leftOver = total % this.buf.length;
            }
        } else {
            leftOver = total % this.buf.length;
        }
        return total - leftOver;
    }

    public int getOutputSize(int length) {
        return this.bufOff + length;
    }

    public int processByte(byte in, byte[] out, int outOff) throws DataLengthException, IllegalStateException {
        byte[] bArr = this.buf;
        int i = this.bufOff;
        int i2 = i + 1;
        this.bufOff = i2;
        bArr[i] = in;
        if (i2 != bArr.length) {
            return 0;
        }
        int resultLen = this.cipher.processBlock(bArr, 0, out, outOff);
        this.bufOff = 0;
        return resultLen;
    }

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
        byte[] bArr = this.buf;
        int length2 = bArr.length;
        int i = this.bufOff;
        int gapLen = length2 - i;
        if (len > gapLen) {
            System.arraycopy(in, inOff, bArr, i, gapLen);
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
        int i2 = this.bufOff + len;
        this.bufOff = i2;
        byte[] bArr2 = this.buf;
        if (i2 == bArr2.length) {
            int resultLen2 = resultLen + this.cipher.processBlock(bArr2, 0, out, outOff + resultLen);
            this.bufOff = 0;
            return resultLen2;
        }
        return resultLen;
    }

    public int doFinal(byte[] out, int outOff) throws DataLengthException, IllegalStateException, InvalidCipherTextException {
        int resultLen = 0;
        try {
            int i = this.bufOff;
            if (outOff + i > out.length) {
                throw new OutputLengthException("output buffer too short for doFinal()");
            }
            if (i != 0) {
                if (!this.partialBlockOkay) {
                    throw new DataLengthException("data not block size aligned");
                }
                BlockCipher blockCipher = this.cipher;
                byte[] bArr = this.buf;
                blockCipher.processBlock(bArr, 0, bArr, 0);
                resultLen = this.bufOff;
                this.bufOff = 0;
                System.arraycopy(this.buf, 0, out, outOff, resultLen);
            }
            return resultLen;
        } finally {
            reset();
        }
    }

    public void reset() {
        int i = 0;
        while (true) {
            byte[] bArr = this.buf;
            if (i < bArr.length) {
                bArr[i] = 0;
                i++;
            } else {
                this.bufOff = 0;
                this.cipher.reset();
                return;
            }
        }
    }
}
