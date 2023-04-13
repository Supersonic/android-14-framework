package com.android.internal.org.bouncycastle.crypto.digests;

import com.android.internal.org.bouncycastle.crypto.ExtendedDigest;
import com.android.internal.org.bouncycastle.util.Memoable;
import com.android.internal.org.bouncycastle.util.Pack;
/* loaded from: classes4.dex */
public abstract class GeneralDigest implements ExtendedDigest, Memoable {
    private static final int BYTE_LENGTH = 64;
    private long byteCount;
    private final byte[] xBuf;
    private int xBufOff;

    protected abstract void processBlock();

    protected abstract void processLength(long j);

    protected abstract void processWord(byte[] bArr, int i);

    /* JADX INFO: Access modifiers changed from: protected */
    public GeneralDigest() {
        this.xBuf = new byte[4];
        this.xBufOff = 0;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public GeneralDigest(GeneralDigest t) {
        this.xBuf = new byte[4];
        copyIn(t);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public GeneralDigest(byte[] encodedState) {
        byte[] bArr = new byte[4];
        this.xBuf = bArr;
        System.arraycopy(encodedState, 0, bArr, 0, bArr.length);
        this.xBufOff = Pack.bigEndianToInt(encodedState, 4);
        this.byteCount = Pack.bigEndianToLong(encodedState, 8);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void copyIn(GeneralDigest t) {
        byte[] bArr = t.xBuf;
        System.arraycopy(bArr, 0, this.xBuf, 0, bArr.length);
        this.xBufOff = t.xBufOff;
        this.byteCount = t.byteCount;
    }

    @Override // com.android.internal.org.bouncycastle.crypto.Digest
    public void update(byte in) {
        byte[] bArr = this.xBuf;
        int i = this.xBufOff;
        int i2 = i + 1;
        this.xBufOff = i2;
        bArr[i] = in;
        if (i2 == bArr.length) {
            processWord(bArr, 0);
            this.xBufOff = 0;
        }
        this.byteCount++;
    }

    @Override // com.android.internal.org.bouncycastle.crypto.Digest
    public void update(byte[] in, int inOff, int len) {
        int len2 = Math.max(0, len);
        int i = 0;
        if (this.xBufOff != 0) {
            while (true) {
                if (i >= len2) {
                    break;
                }
                byte[] bArr = this.xBuf;
                int i2 = this.xBufOff;
                int i3 = i2 + 1;
                this.xBufOff = i3;
                int i4 = i + 1;
                bArr[i2] = in[i + inOff];
                if (i3 != 4) {
                    i = i4;
                } else {
                    processWord(bArr, 0);
                    this.xBufOff = 0;
                    i = i4;
                    break;
                }
            }
        }
        int limit = ((len2 - i) & (-4)) + i;
        while (i < limit) {
            processWord(in, inOff + i);
            i += 4;
        }
        while (i < len2) {
            byte[] bArr2 = this.xBuf;
            int i5 = this.xBufOff;
            this.xBufOff = i5 + 1;
            bArr2[i5] = in[i + inOff];
            i++;
        }
        this.byteCount += len2;
    }

    public void finish() {
        long bitLength = this.byteCount << 3;
        update(Byte.MIN_VALUE);
        while (this.xBufOff != 0) {
            update((byte) 0);
        }
        processLength(bitLength);
        processBlock();
    }

    @Override // com.android.internal.org.bouncycastle.crypto.Digest
    public void reset() {
        this.byteCount = 0L;
        this.xBufOff = 0;
        int i = 0;
        while (true) {
            byte[] bArr = this.xBuf;
            if (i < bArr.length) {
                bArr[i] = 0;
                i++;
            } else {
                return;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void populateState(byte[] state) {
        System.arraycopy(this.xBuf, 0, state, 0, this.xBufOff);
        Pack.intToBigEndian(this.xBufOff, state, 4);
        Pack.longToBigEndian(this.byteCount, state, 8);
    }

    @Override // com.android.internal.org.bouncycastle.crypto.ExtendedDigest
    public int getByteLength() {
        return 64;
    }
}
