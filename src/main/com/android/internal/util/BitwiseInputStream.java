package com.android.internal.util;

import android.inputmethodservice.navigationbar.NavigationBarInflaterView;
/* loaded from: classes3.dex */
public class BitwiseInputStream {
    private byte[] mBuf;
    private int mEnd;
    private int mPos = 0;

    /* loaded from: classes3.dex */
    public static class AccessException extends Exception {
        public AccessException(String s) {
            super("BitwiseInputStream access failed: " + s);
        }
    }

    public BitwiseInputStream(byte[] buf) {
        this.mBuf = buf;
        this.mEnd = buf.length << 3;
    }

    public int available() {
        return this.mEnd - this.mPos;
    }

    public int read(int bits) throws AccessException {
        int i = this.mPos;
        int index = i >>> 3;
        int offset = (16 - (i & 7)) - bits;
        if (bits < 0 || bits > 8 || i + bits > this.mEnd) {
            throw new AccessException("illegal read (pos " + this.mPos + ", end " + this.mEnd + ", bits " + bits + NavigationBarInflaterView.KEY_CODE_END);
        }
        byte[] bArr = this.mBuf;
        int data = (bArr[index] & 255) << 8;
        if (offset < 8) {
            data |= bArr[index + 1] & 255;
        }
        int data2 = (data >>> offset) & ((-1) >>> (32 - bits));
        this.mPos = i + bits;
        return data2;
    }

    public byte[] readByteArray(int bits) throws AccessException {
        int bytes = (bits >>> 3) + ((bits & 7) > 0 ? 1 : 0);
        byte[] arr = new byte[bytes];
        for (int i = 0; i < bytes; i++) {
            int increment = Math.min(8, bits - (i << 3));
            arr[i] = (byte) (read(increment) << (8 - increment));
        }
        return arr;
    }

    public void skip(int bits) throws AccessException {
        int i = this.mPos;
        if (i + bits > this.mEnd) {
            throw new AccessException("illegal skip (pos " + this.mPos + ", end " + this.mEnd + ", bits " + bits + NavigationBarInflaterView.KEY_CODE_END);
        }
        this.mPos = i + bits;
    }
}
