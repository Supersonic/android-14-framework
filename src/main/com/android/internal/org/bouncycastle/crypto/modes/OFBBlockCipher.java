package com.android.internal.org.bouncycastle.crypto.modes;

import com.android.internal.org.bouncycastle.crypto.BlockCipher;
import com.android.internal.org.bouncycastle.crypto.CipherParameters;
import com.android.internal.org.bouncycastle.crypto.DataLengthException;
import com.android.internal.org.bouncycastle.crypto.StreamBlockCipher;
import com.android.internal.org.bouncycastle.crypto.params.ParametersWithIV;
/* loaded from: classes4.dex */
public class OFBBlockCipher extends StreamBlockCipher {

    /* renamed from: IV */
    private byte[] f761IV;
    private final int blockSize;
    private int byteCount;
    private final BlockCipher cipher;
    private byte[] ofbOutV;
    private byte[] ofbV;

    public OFBBlockCipher(BlockCipher cipher, int bitBlockSize) {
        super(cipher);
        if (bitBlockSize > cipher.getBlockSize() * 8 || bitBlockSize < 8 || bitBlockSize % 8 != 0) {
            throw new IllegalArgumentException("0FB" + bitBlockSize + " not supported");
        }
        this.cipher = cipher;
        this.blockSize = bitBlockSize / 8;
        this.f761IV = new byte[cipher.getBlockSize()];
        this.ofbV = new byte[cipher.getBlockSize()];
        this.ofbOutV = new byte[cipher.getBlockSize()];
    }

    @Override // com.android.internal.org.bouncycastle.crypto.BlockCipher
    public void init(boolean encrypting, CipherParameters params) throws IllegalArgumentException {
        if (params instanceof ParametersWithIV) {
            ParametersWithIV ivParam = (ParametersWithIV) params;
            byte[] iv = ivParam.getIV();
            int length = iv.length;
            byte[] bArr = this.f761IV;
            if (length < bArr.length) {
                System.arraycopy(iv, 0, bArr, bArr.length - iv.length, iv.length);
                int i = 0;
                while (true) {
                    byte[] bArr2 = this.f761IV;
                    if (i >= bArr2.length - iv.length) {
                        break;
                    }
                    bArr2[i] = 0;
                    i++;
                }
            } else {
                System.arraycopy(iv, 0, bArr, 0, bArr.length);
            }
            reset();
            if (ivParam.getParameters() != null) {
                this.cipher.init(true, ivParam.getParameters());
                return;
            }
            return;
        }
        reset();
        if (params != null) {
            this.cipher.init(true, params);
        }
    }

    @Override // com.android.internal.org.bouncycastle.crypto.BlockCipher
    public String getAlgorithmName() {
        return this.cipher.getAlgorithmName() + "/OFB" + (this.blockSize * 8);
    }

    @Override // com.android.internal.org.bouncycastle.crypto.BlockCipher
    public int getBlockSize() {
        return this.blockSize;
    }

    @Override // com.android.internal.org.bouncycastle.crypto.BlockCipher
    public int processBlock(byte[] in, int inOff, byte[] out, int outOff) throws DataLengthException, IllegalStateException {
        processBytes(in, inOff, this.blockSize, out, outOff);
        return this.blockSize;
    }

    @Override // com.android.internal.org.bouncycastle.crypto.BlockCipher
    public void reset() {
        byte[] bArr = this.f761IV;
        System.arraycopy(bArr, 0, this.ofbV, 0, bArr.length);
        this.byteCount = 0;
        this.cipher.reset();
    }

    @Override // com.android.internal.org.bouncycastle.crypto.StreamBlockCipher
    protected byte calculateByte(byte in) throws DataLengthException, IllegalStateException {
        if (this.byteCount == 0) {
            this.cipher.processBlock(this.ofbV, 0, this.ofbOutV, 0);
        }
        byte[] bArr = this.ofbOutV;
        int i = this.byteCount;
        int i2 = i + 1;
        this.byteCount = i2;
        byte rv = (byte) (bArr[i] ^ in);
        int i3 = this.blockSize;
        if (i2 == i3) {
            this.byteCount = 0;
            byte[] bArr2 = this.ofbV;
            System.arraycopy(bArr2, i3, bArr2, 0, bArr2.length - i3);
            byte[] bArr3 = this.ofbOutV;
            byte[] bArr4 = this.ofbV;
            int length = bArr4.length;
            int i4 = this.blockSize;
            System.arraycopy(bArr3, 0, bArr4, length - i4, i4);
        }
        return rv;
    }
}
