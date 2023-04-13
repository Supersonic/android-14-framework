package com.android.internal.org.bouncycastle.crypto.engines;

import com.android.internal.org.bouncycastle.crypto.CipherParameters;
import com.android.internal.org.bouncycastle.crypto.DataLengthException;
import com.android.internal.org.bouncycastle.crypto.OutputLengthException;
import com.android.internal.org.bouncycastle.crypto.StreamCipher;
import com.android.internal.org.bouncycastle.crypto.params.KeyParameter;
/* loaded from: classes4.dex */
public class RC4Engine implements StreamCipher {
    private static final int STATE_LENGTH = 256;
    private byte[] engineState = null;

    /* renamed from: x */
    private int f748x = 0;

    /* renamed from: y */
    private int f749y = 0;
    private byte[] workingKey = null;

    @Override // com.android.internal.org.bouncycastle.crypto.StreamCipher
    public void init(boolean forEncryption, CipherParameters params) {
        if (params instanceof KeyParameter) {
            byte[] key = ((KeyParameter) params).getKey();
            this.workingKey = key;
            setKey(key);
            return;
        }
        throw new IllegalArgumentException("invalid parameter passed to RC4 init - " + params.getClass().getName());
    }

    @Override // com.android.internal.org.bouncycastle.crypto.StreamCipher
    public String getAlgorithmName() {
        return "RC4";
    }

    @Override // com.android.internal.org.bouncycastle.crypto.StreamCipher
    public byte returnByte(byte in) {
        int i = (this.f748x + 1) & 255;
        this.f748x = i;
        byte[] bArr = this.engineState;
        int i2 = (bArr[i] + this.f749y) & 255;
        this.f749y = i2;
        byte tmp = bArr[i];
        bArr[i] = bArr[i2];
        bArr[i2] = tmp;
        return (byte) (bArr[(bArr[i] + tmp) & 255] ^ in);
    }

    @Override // com.android.internal.org.bouncycastle.crypto.StreamCipher
    public int processBytes(byte[] in, int inOff, int len, byte[] out, int outOff) {
        if (inOff + len > in.length) {
            throw new DataLengthException("input buffer too short");
        }
        if (outOff + len > out.length) {
            throw new OutputLengthException("output buffer too short");
        }
        for (int i = 0; i < len; i++) {
            int i2 = (this.f748x + 1) & 255;
            this.f748x = i2;
            byte[] bArr = this.engineState;
            int i3 = (bArr[i2] + this.f749y) & 255;
            this.f749y = i3;
            byte tmp = bArr[i2];
            bArr[i2] = bArr[i3];
            bArr[i3] = tmp;
            out[i + outOff] = (byte) (bArr[(bArr[i2] + tmp) & 255] ^ in[i + inOff]);
        }
        return len;
    }

    @Override // com.android.internal.org.bouncycastle.crypto.StreamCipher
    public void reset() {
        setKey(this.workingKey);
    }

    private void setKey(byte[] keyBytes) {
        this.workingKey = keyBytes;
        this.f748x = 0;
        this.f749y = 0;
        if (this.engineState == null) {
            this.engineState = new byte[256];
        }
        for (int i = 0; i < 256; i++) {
            this.engineState[i] = (byte) i;
        }
        int i1 = 0;
        int i2 = 0;
        for (int i3 = 0; i3 < 256; i3++) {
            byte[] bArr = this.engineState;
            i2 = ((keyBytes[i1] & 255) + bArr[i3] + i2) & 255;
            byte tmp = bArr[i3];
            bArr[i3] = bArr[i2];
            bArr[i2] = tmp;
            i1 = (i1 + 1) % keyBytes.length;
        }
    }
}
