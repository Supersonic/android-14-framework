package com.android.internal.org.bouncycastle.crypto.engines;

import com.android.internal.org.bouncycastle.crypto.BlockCipher;
import com.android.internal.org.bouncycastle.crypto.CipherParameters;
import com.android.internal.org.bouncycastle.crypto.DataLengthException;
import com.android.internal.org.bouncycastle.crypto.InvalidCipherTextException;
import com.android.internal.org.bouncycastle.crypto.Wrapper;
import com.android.internal.org.bouncycastle.crypto.params.KeyParameter;
import com.android.internal.org.bouncycastle.crypto.params.ParametersWithIV;
import com.android.internal.org.bouncycastle.crypto.params.ParametersWithRandom;
import com.android.internal.org.bouncycastle.util.Arrays;
/* loaded from: classes4.dex */
public class RFC3394WrapEngine implements Wrapper {
    private BlockCipher engine;
    private boolean forWrapping;

    /* renamed from: iv */
    private byte[] f750iv;
    private KeyParameter param;
    private boolean wrapCipherMode;

    public RFC3394WrapEngine(BlockCipher engine) {
        this(engine, false);
    }

    public RFC3394WrapEngine(BlockCipher engine, boolean useReverseDirection) {
        this.f750iv = new byte[]{-90, -90, -90, -90, -90, -90, -90, -90};
        this.engine = engine;
        this.wrapCipherMode = !useReverseDirection;
    }

    @Override // com.android.internal.org.bouncycastle.crypto.Wrapper
    public void init(boolean forWrapping, CipherParameters param) {
        this.forWrapping = forWrapping;
        if (param instanceof ParametersWithRandom) {
            param = ((ParametersWithRandom) param).getParameters();
        }
        if (param instanceof KeyParameter) {
            this.param = (KeyParameter) param;
        } else if (param instanceof ParametersWithIV) {
            this.f750iv = ((ParametersWithIV) param).getIV();
            this.param = (KeyParameter) ((ParametersWithIV) param).getParameters();
            if (this.f750iv.length != 8) {
                throw new IllegalArgumentException("IV not equal to 8");
            }
        }
    }

    @Override // com.android.internal.org.bouncycastle.crypto.Wrapper
    public String getAlgorithmName() {
        return this.engine.getAlgorithmName();
    }

    @Override // com.android.internal.org.bouncycastle.crypto.Wrapper
    public byte[] wrap(byte[] in, int inOff, int inLen) {
        if (!this.forWrapping) {
            throw new IllegalStateException("not set for wrapping");
        }
        int n = inLen / 8;
        if (n * 8 != inLen) {
            throw new DataLengthException("wrap data must be a multiple of 8 bytes");
        }
        byte[] bArr = this.f750iv;
        byte[] block = new byte[bArr.length + inLen];
        byte[] buf = new byte[bArr.length + 8];
        System.arraycopy(bArr, 0, block, 0, bArr.length);
        System.arraycopy(in, inOff, block, this.f750iv.length, inLen);
        this.engine.init(this.wrapCipherMode, this.param);
        for (int j = 0; j != 6; j++) {
            for (int i = 1; i <= n; i++) {
                System.arraycopy(block, 0, buf, 0, this.f750iv.length);
                System.arraycopy(block, i * 8, buf, this.f750iv.length, 8);
                this.engine.processBlock(buf, 0, buf, 0);
                int t = (n * j) + i;
                int k = 1;
                while (t != 0) {
                    byte v = (byte) t;
                    int length = this.f750iv.length - k;
                    buf[length] = (byte) (buf[length] ^ v);
                    t >>>= 8;
                    k++;
                }
                System.arraycopy(buf, 0, block, 0, 8);
                System.arraycopy(buf, 8, block, i * 8, 8);
            }
        }
        return block;
    }

    @Override // com.android.internal.org.bouncycastle.crypto.Wrapper
    public byte[] unwrap(byte[] in, int inOff, int inLen) throws InvalidCipherTextException {
        if (this.forWrapping) {
            throw new IllegalStateException("not set for unwrapping");
        }
        int n = inLen / 8;
        if (n * 8 != inLen) {
            throw new InvalidCipherTextException("unwrap data must be a multiple of 8 bytes");
        }
        byte[] bArr = this.f750iv;
        byte[] block = new byte[inLen - bArr.length];
        byte[] a = new byte[bArr.length];
        int i = 8;
        byte[] buf = new byte[bArr.length + 8];
        System.arraycopy(in, inOff, a, 0, bArr.length);
        byte[] bArr2 = this.f750iv;
        System.arraycopy(in, bArr2.length + inOff, block, 0, inLen - bArr2.length);
        int i2 = 1;
        this.engine.init(!this.wrapCipherMode, this.param);
        int n2 = n - 1;
        int j = 5;
        while (j >= 0) {
            int i3 = n2;
            while (i3 >= i2) {
                System.arraycopy(a, 0, buf, 0, this.f750iv.length);
                System.arraycopy(block, (i3 - 1) * i, buf, this.f750iv.length, i);
                int t = (n2 * j) + i3;
                int k = 1;
                while (t != 0) {
                    byte v = (byte) t;
                    int length = this.f750iv.length - k;
                    buf[length] = (byte) (buf[length] ^ v);
                    t >>>= 8;
                    k++;
                }
                this.engine.processBlock(buf, 0, buf, 0);
                i = 8;
                System.arraycopy(buf, 0, a, 0, 8);
                System.arraycopy(buf, 8, block, (i3 - 1) * 8, 8);
                i3--;
                i2 = 1;
            }
            j--;
            i2 = 1;
        }
        if (!Arrays.constantTimeAreEqual(a, this.f750iv)) {
            throw new InvalidCipherTextException("checksum failed");
        }
        return block;
    }
}
