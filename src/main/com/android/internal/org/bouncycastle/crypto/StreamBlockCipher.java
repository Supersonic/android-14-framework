package com.android.internal.org.bouncycastle.crypto;
/* loaded from: classes4.dex */
public abstract class StreamBlockCipher implements BlockCipher, StreamCipher {
    private final BlockCipher cipher;

    protected abstract byte calculateByte(byte b);

    /* JADX INFO: Access modifiers changed from: protected */
    public StreamBlockCipher(BlockCipher cipher) {
        this.cipher = cipher;
    }

    public BlockCipher getUnderlyingCipher() {
        return this.cipher;
    }

    @Override // com.android.internal.org.bouncycastle.crypto.StreamCipher
    public final byte returnByte(byte in) {
        return calculateByte(in);
    }

    @Override // com.android.internal.org.bouncycastle.crypto.StreamCipher
    public int processBytes(byte[] in, int inOff, int len, byte[] out, int outOff) throws DataLengthException {
        if (inOff + len > in.length) {
            throw new DataLengthException("input buffer too small");
        }
        if (outOff + len > out.length) {
            throw new OutputLengthException("output buffer too short");
        }
        int inEnd = inOff + len;
        int outStart = outOff;
        for (int inStart = inOff; inStart < inEnd; inStart++) {
            out[outStart] = calculateByte(in[inStart]);
            outStart++;
        }
        return len;
    }
}
