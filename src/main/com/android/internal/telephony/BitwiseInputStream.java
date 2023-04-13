package com.android.internal.telephony;
/* loaded from: classes.dex */
public class BitwiseInputStream {
    private byte[] mBuf;
    private int mEnd;
    private int mPos = 0;

    /* loaded from: classes.dex */
    public static class AccessException extends Exception {
        public AccessException(String str) {
            super("BitwiseInputStream access failed: " + str);
        }
    }

    public BitwiseInputStream(byte[] bArr) {
        this.mBuf = bArr;
        this.mEnd = bArr.length << 3;
    }

    public int available() {
        return this.mEnd - this.mPos;
    }

    public int read(int i) throws AccessException {
        int i2 = this.mPos;
        int i3 = i2 >>> 3;
        int i4 = (16 - (i2 & 7)) - i;
        if (i < 0 || i > 8 || i2 + i > this.mEnd) {
            throw new AccessException("illegal read (pos " + this.mPos + ", end " + this.mEnd + ", bits " + i + ")");
        }
        byte[] bArr = this.mBuf;
        int i5 = (bArr[i3] & 255) << 8;
        if (i4 < 8) {
            i5 |= bArr[i3 + 1] & 255;
        }
        int i6 = (i5 >>> i4) & ((-1) >>> (32 - i));
        this.mPos = i2 + i;
        return i6;
    }

    public byte[] readByteArray(int i) throws AccessException {
        int i2 = (i >>> 3) + ((i & 7) > 0 ? 1 : 0);
        byte[] bArr = new byte[i2];
        for (int i3 = 0; i3 < i2; i3++) {
            int min = Math.min(8, i - (i3 << 3));
            bArr[i3] = (byte) (read(min) << (8 - min));
        }
        return bArr;
    }

    public void skip(int i) throws AccessException {
        int i2 = this.mPos;
        if (i2 + i > this.mEnd) {
            throw new AccessException("illegal skip (pos " + this.mPos + ", end " + this.mEnd + ", bits " + i + ")");
        }
        this.mPos = i2 + i;
    }
}
