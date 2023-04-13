package com.android.internal.org.bouncycastle.crypto.macs;

import com.android.internal.org.bouncycastle.crypto.BlockCipher;
import com.android.internal.org.bouncycastle.crypto.CipherParameters;
import com.android.internal.org.bouncycastle.crypto.Mac;
import com.android.internal.org.bouncycastle.crypto.modes.CBCBlockCipher;
import com.android.internal.org.bouncycastle.crypto.paddings.BlockCipherPadding;
/* loaded from: classes4.dex */
public class CBCBlockCipherMac implements Mac {
    private byte[] buf;
    private int bufOff;
    private BlockCipher cipher;
    private byte[] mac;
    private int macSize;
    private BlockCipherPadding padding;

    public CBCBlockCipherMac(BlockCipher cipher) {
        this(cipher, (cipher.getBlockSize() * 8) / 2, null);
    }

    public CBCBlockCipherMac(BlockCipher cipher, BlockCipherPadding padding) {
        this(cipher, (cipher.getBlockSize() * 8) / 2, padding);
    }

    public CBCBlockCipherMac(BlockCipher cipher, int macSizeInBits) {
        this(cipher, macSizeInBits, null);
    }

    public CBCBlockCipherMac(BlockCipher cipher, int macSizeInBits, BlockCipherPadding padding) {
        if (macSizeInBits % 8 != 0) {
            throw new IllegalArgumentException("MAC size must be multiple of 8");
        }
        this.cipher = new CBCBlockCipher(cipher);
        this.padding = padding;
        this.macSize = macSizeInBits / 8;
        this.mac = new byte[cipher.getBlockSize()];
        this.buf = new byte[cipher.getBlockSize()];
        this.bufOff = 0;
    }

    @Override // com.android.internal.org.bouncycastle.crypto.Mac
    public String getAlgorithmName() {
        return this.cipher.getAlgorithmName();
    }

    @Override // com.android.internal.org.bouncycastle.crypto.Mac
    public void init(CipherParameters params) {
        reset();
        this.cipher.init(true, params);
    }

    @Override // com.android.internal.org.bouncycastle.crypto.Mac
    public int getMacSize() {
        return this.macSize;
    }

    @Override // com.android.internal.org.bouncycastle.crypto.Mac
    public void update(byte in) {
        int i = this.bufOff;
        byte[] bArr = this.buf;
        if (i == bArr.length) {
            this.cipher.processBlock(bArr, 0, this.mac, 0);
            this.bufOff = 0;
        }
        byte[] bArr2 = this.buf;
        int i2 = this.bufOff;
        this.bufOff = i2 + 1;
        bArr2[i2] = in;
    }

    @Override // com.android.internal.org.bouncycastle.crypto.Mac
    public void update(byte[] in, int inOff, int len) {
        if (len < 0) {
            throw new IllegalArgumentException("Can't have a negative input length!");
        }
        int blockSize = this.cipher.getBlockSize();
        int i = this.bufOff;
        int gapLen = blockSize - i;
        if (len > gapLen) {
            System.arraycopy(in, inOff, this.buf, i, gapLen);
            this.cipher.processBlock(this.buf, 0, this.mac, 0);
            this.bufOff = 0;
            len -= gapLen;
            inOff += gapLen;
            while (len > blockSize) {
                this.cipher.processBlock(in, inOff, this.mac, 0);
                len -= blockSize;
                inOff += blockSize;
            }
        }
        System.arraycopy(in, inOff, this.buf, this.bufOff, len);
        this.bufOff += len;
    }

    @Override // com.android.internal.org.bouncycastle.crypto.Mac
    public int doFinal(byte[] out, int outOff) {
        int blockSize = this.cipher.getBlockSize();
        if (this.padding == null) {
            while (true) {
                int i = this.bufOff;
                if (i >= blockSize) {
                    break;
                }
                this.buf[i] = 0;
                this.bufOff = i + 1;
            }
        } else {
            if (this.bufOff == blockSize) {
                this.cipher.processBlock(this.buf, 0, this.mac, 0);
                this.bufOff = 0;
            }
            this.padding.addPadding(this.buf, this.bufOff);
        }
        this.cipher.processBlock(this.buf, 0, this.mac, 0);
        System.arraycopy(this.mac, 0, out, outOff, this.macSize);
        reset();
        return this.macSize;
    }

    @Override // com.android.internal.org.bouncycastle.crypto.Mac
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
