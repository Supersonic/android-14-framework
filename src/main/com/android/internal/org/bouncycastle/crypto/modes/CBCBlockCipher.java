package com.android.internal.org.bouncycastle.crypto.modes;

import com.android.internal.org.bouncycastle.crypto.BlockCipher;
import com.android.internal.org.bouncycastle.crypto.CipherParameters;
import com.android.internal.org.bouncycastle.crypto.DataLengthException;
import com.android.internal.org.bouncycastle.crypto.params.ParametersWithIV;
import com.android.internal.org.bouncycastle.util.Arrays;
/* loaded from: classes4.dex */
public class CBCBlockCipher implements BlockCipher {

    /* renamed from: IV */
    private byte[] f756IV;
    private int blockSize;
    private byte[] cbcNextV;
    private byte[] cbcV;
    private BlockCipher cipher;
    private boolean encrypting;

    public CBCBlockCipher(BlockCipher cipher) {
        this.cipher = null;
        this.cipher = cipher;
        int blockSize = cipher.getBlockSize();
        this.blockSize = blockSize;
        this.f756IV = new byte[blockSize];
        this.cbcV = new byte[blockSize];
        this.cbcNextV = new byte[blockSize];
    }

    public BlockCipher getUnderlyingCipher() {
        return this.cipher;
    }

    @Override // com.android.internal.org.bouncycastle.crypto.BlockCipher
    public void init(boolean encrypting, CipherParameters params) throws IllegalArgumentException {
        boolean oldEncrypting = this.encrypting;
        this.encrypting = encrypting;
        if (params instanceof ParametersWithIV) {
            ParametersWithIV ivParam = (ParametersWithIV) params;
            byte[] iv = ivParam.getIV();
            if (iv.length != this.blockSize) {
                throw new IllegalArgumentException("initialisation vector must be the same length as block size");
            }
            System.arraycopy(iv, 0, this.f756IV, 0, iv.length);
            reset();
            if (ivParam.getParameters() != null) {
                this.cipher.init(encrypting, ivParam.getParameters());
                return;
            } else if (oldEncrypting != encrypting) {
                throw new IllegalArgumentException("cannot change encrypting state without providing key.");
            } else {
                return;
            }
        }
        reset();
        if (params != null) {
            this.cipher.init(encrypting, params);
        } else if (oldEncrypting != encrypting) {
            throw new IllegalArgumentException("cannot change encrypting state without providing key.");
        }
    }

    @Override // com.android.internal.org.bouncycastle.crypto.BlockCipher
    public String getAlgorithmName() {
        return this.cipher.getAlgorithmName() + "/CBC";
    }

    @Override // com.android.internal.org.bouncycastle.crypto.BlockCipher
    public int getBlockSize() {
        return this.cipher.getBlockSize();
    }

    @Override // com.android.internal.org.bouncycastle.crypto.BlockCipher
    public int processBlock(byte[] in, int inOff, byte[] out, int outOff) throws DataLengthException, IllegalStateException {
        return this.encrypting ? encryptBlock(in, inOff, out, outOff) : decryptBlock(in, inOff, out, outOff);
    }

    @Override // com.android.internal.org.bouncycastle.crypto.BlockCipher
    public void reset() {
        byte[] bArr = this.f756IV;
        System.arraycopy(bArr, 0, this.cbcV, 0, bArr.length);
        Arrays.fill(this.cbcNextV, (byte) 0);
        this.cipher.reset();
    }

    private int encryptBlock(byte[] in, int inOff, byte[] out, int outOff) throws DataLengthException, IllegalStateException {
        if (this.blockSize + inOff > in.length) {
            throw new DataLengthException("input buffer too short");
        }
        for (int i = 0; i < this.blockSize; i++) {
            byte[] bArr = this.cbcV;
            bArr[i] = (byte) (bArr[i] ^ in[inOff + i]);
        }
        int length = this.cipher.processBlock(this.cbcV, 0, out, outOff);
        byte[] bArr2 = this.cbcV;
        System.arraycopy(out, outOff, bArr2, 0, bArr2.length);
        return length;
    }

    private int decryptBlock(byte[] in, int inOff, byte[] out, int outOff) throws DataLengthException, IllegalStateException {
        int i = this.blockSize;
        if (inOff + i > in.length) {
            throw new DataLengthException("input buffer too short");
        }
        System.arraycopy(in, inOff, this.cbcNextV, 0, i);
        int length = this.cipher.processBlock(in, inOff, out, outOff);
        for (int i2 = 0; i2 < this.blockSize; i2++) {
            int i3 = outOff + i2;
            out[i3] = (byte) (out[i3] ^ this.cbcV[i2]);
        }
        byte[] tmp = this.cbcV;
        this.cbcV = this.cbcNextV;
        this.cbcNextV = tmp;
        return length;
    }
}
