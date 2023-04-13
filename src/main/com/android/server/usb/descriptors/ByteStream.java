package com.android.server.usb.descriptors;
/* loaded from: classes2.dex */
public final class ByteStream {
    public final byte[] mBytes;
    public int mIndex;
    public int mReadCount;

    public ByteStream(byte[] bArr) {
        if (bArr == null) {
            throw new IllegalArgumentException();
        }
        this.mBytes = bArr;
    }

    public void resetReadCount() {
        this.mReadCount = 0;
    }

    public int getReadCount() {
        return this.mReadCount;
    }

    public byte getByte() {
        if (available() > 0) {
            this.mReadCount++;
            byte[] bArr = this.mBytes;
            int i = this.mIndex;
            this.mIndex = i + 1;
            return bArr[i];
        }
        throw new IndexOutOfBoundsException();
    }

    public int getUnsignedByte() {
        if (available() > 0) {
            this.mReadCount++;
            byte[] bArr = this.mBytes;
            int i = this.mIndex;
            this.mIndex = i + 1;
            return bArr[i] & 255;
        }
        throw new IndexOutOfBoundsException();
    }

    public int unpackUsbShort() {
        if (available() >= 2) {
            return (getUnsignedByte() << 8) | getUnsignedByte();
        }
        throw new IndexOutOfBoundsException();
    }

    public int unpackUsbTriple() {
        if (available() >= 3) {
            return (getUnsignedByte() << 16) | (getUnsignedByte() << 8) | getUnsignedByte();
        }
        throw new IndexOutOfBoundsException();
    }

    public int unpackUsbInt() {
        if (available() >= 4) {
            int unsignedByte = getUnsignedByte();
            int unsignedByte2 = getUnsignedByte();
            return (getUnsignedByte() << 24) | (getUnsignedByte() << 16) | (unsignedByte2 << 8) | unsignedByte;
        }
        throw new IndexOutOfBoundsException();
    }

    public void advance(int i) {
        if (i < 0) {
            throw new IllegalArgumentException();
        }
        int i2 = this.mIndex;
        long j = i2 + i;
        byte[] bArr = this.mBytes;
        if (j <= bArr.length) {
            this.mReadCount += i;
            this.mIndex = i2 + i;
            return;
        }
        this.mIndex = bArr.length;
        throw new IndexOutOfBoundsException();
    }

    public void reverse(int i) {
        if (i < 0) {
            throw new IllegalArgumentException();
        }
        int i2 = this.mIndex;
        if (i2 >= i) {
            this.mReadCount -= i;
            this.mIndex = i2 - i;
            return;
        }
        this.mIndex = 0;
        throw new IndexOutOfBoundsException();
    }

    public int available() {
        return this.mBytes.length - this.mIndex;
    }
}
