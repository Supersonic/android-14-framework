package com.android.phone.ecc.nano;

import com.android.internal.telephony.data.KeepaliveStatus;
import java.io.IOException;
/* loaded from: classes.dex */
public final class CodedInputByteBufferNano {
    private final byte[] buffer;
    private int bufferPos;
    private int bufferSize;
    private int bufferSizeAfterLimit;
    private int bufferStart;
    private int lastTag;
    private int recursionDepth;
    private int currentLimit = KeepaliveStatus.INVALID_HANDLE;
    private int recursionLimit = 64;
    private int sizeLimit = 67108864;

    public static CodedInputByteBufferNano newInstance(byte[] bArr, int i, int i2) {
        return new CodedInputByteBufferNano(bArr, i, i2);
    }

    public int readTag() throws IOException {
        if (isAtEnd()) {
            this.lastTag = 0;
            return 0;
        }
        int readRawVarint32 = readRawVarint32();
        this.lastTag = readRawVarint32;
        if (readRawVarint32 != 0) {
            return readRawVarint32;
        }
        throw InvalidProtocolBufferNanoException.invalidTag();
    }

    public void checkLastTagWas(int i) throws InvalidProtocolBufferNanoException {
        if (this.lastTag != i) {
            throw InvalidProtocolBufferNanoException.invalidEndTag();
        }
    }

    public boolean skipField(int i) throws IOException {
        int tagWireType = WireFormatNano.getTagWireType(i);
        if (tagWireType == 0) {
            readInt32();
            return true;
        } else if (tagWireType == 1) {
            readRawLittleEndian64();
            return true;
        } else if (tagWireType == 2) {
            skipRawBytes(readRawVarint32());
            return true;
        } else if (tagWireType == 3) {
            skipMessage();
            checkLastTagWas(WireFormatNano.makeTag(WireFormatNano.getTagFieldNumber(i), 4));
            return true;
        } else if (tagWireType != 4) {
            if (tagWireType == 5) {
                readRawLittleEndian32();
                return true;
            }
            throw InvalidProtocolBufferNanoException.invalidWireType();
        } else {
            return false;
        }
    }

    public void skipMessage() throws IOException {
        int readTag;
        do {
            readTag = readTag();
            if (readTag == 0) {
                return;
            }
        } while (skipField(readTag));
    }

    public int readInt32() throws IOException {
        return readRawVarint32();
    }

    public boolean readBool() throws IOException {
        return readRawVarint32() != 0;
    }

    public String readString() throws IOException {
        int readRawVarint32 = readRawVarint32();
        int i = this.bufferSize;
        int i2 = this.bufferPos;
        if (readRawVarint32 <= i - i2 && readRawVarint32 > 0) {
            String str = new String(this.buffer, i2, readRawVarint32, InternalNano.UTF_8);
            this.bufferPos += readRawVarint32;
            return str;
        }
        return new String(readRawBytes(readRawVarint32), InternalNano.UTF_8);
    }

    public void readMessage(MessageNano messageNano) throws IOException {
        int readRawVarint32 = readRawVarint32();
        if (this.recursionDepth >= this.recursionLimit) {
            throw InvalidProtocolBufferNanoException.recursionLimitExceeded();
        }
        int pushLimit = pushLimit(readRawVarint32);
        this.recursionDepth++;
        messageNano.mergeFrom(this);
        checkLastTagWas(0);
        this.recursionDepth--;
        popLimit(pushLimit);
    }

    public int readRawVarint32() throws IOException {
        int i;
        byte readRawByte = readRawByte();
        if (readRawByte >= 0) {
            return readRawByte;
        }
        int i2 = readRawByte & Byte.MAX_VALUE;
        byte readRawByte2 = readRawByte();
        if (readRawByte2 >= 0) {
            i = readRawByte2 << 7;
        } else {
            i2 |= (readRawByte2 & Byte.MAX_VALUE) << 7;
            byte readRawByte3 = readRawByte();
            if (readRawByte3 >= 0) {
                i = readRawByte3 << 14;
            } else {
                i2 |= (readRawByte3 & Byte.MAX_VALUE) << 14;
                byte readRawByte4 = readRawByte();
                if (readRawByte4 < 0) {
                    int i3 = i2 | ((readRawByte4 & Byte.MAX_VALUE) << 21);
                    byte readRawByte5 = readRawByte();
                    int i4 = i3 | (readRawByte5 << 28);
                    if (readRawByte5 < 0) {
                        for (int i5 = 0; i5 < 5; i5++) {
                            if (readRawByte() >= 0) {
                                return i4;
                            }
                        }
                        throw InvalidProtocolBufferNanoException.malformedVarint();
                    }
                    return i4;
                }
                i = readRawByte4 << 21;
            }
        }
        return i | i2;
    }

    public int readRawLittleEndian32() throws IOException {
        byte readRawByte = readRawByte();
        byte readRawByte2 = readRawByte();
        byte readRawByte3 = readRawByte();
        return ((readRawByte() & 255) << 24) | (readRawByte & 255) | ((readRawByte2 & 255) << 8) | ((readRawByte3 & 255) << 16);
    }

    public long readRawLittleEndian64() throws IOException {
        byte readRawByte = readRawByte();
        byte readRawByte2 = readRawByte();
        return ((readRawByte2 & 255) << 8) | (readRawByte & 255) | ((readRawByte() & 255) << 16) | ((readRawByte() & 255) << 24) | ((readRawByte() & 255) << 32) | ((readRawByte() & 255) << 40) | ((readRawByte() & 255) << 48) | ((readRawByte() & 255) << 56);
    }

    private CodedInputByteBufferNano(byte[] bArr, int i, int i2) {
        this.buffer = bArr;
        this.bufferStart = i;
        this.bufferSize = i2 + i;
        this.bufferPos = i;
    }

    public int pushLimit(int i) throws InvalidProtocolBufferNanoException {
        if (i < 0) {
            throw InvalidProtocolBufferNanoException.negativeSize();
        }
        int i2 = i + this.bufferPos;
        int i3 = this.currentLimit;
        if (i2 > i3) {
            throw InvalidProtocolBufferNanoException.truncatedMessage();
        }
        this.currentLimit = i2;
        recomputeBufferSizeAfterLimit();
        return i3;
    }

    private void recomputeBufferSizeAfterLimit() {
        int i = this.bufferSize + this.bufferSizeAfterLimit;
        this.bufferSize = i;
        int i2 = this.currentLimit;
        if (i > i2) {
            int i3 = i - i2;
            this.bufferSizeAfterLimit = i3;
            this.bufferSize = i - i3;
            return;
        }
        this.bufferSizeAfterLimit = 0;
    }

    public void popLimit(int i) {
        this.currentLimit = i;
        recomputeBufferSizeAfterLimit();
    }

    public int getBytesUntilLimit() {
        int i = this.currentLimit;
        if (i == Integer.MAX_VALUE) {
            return -1;
        }
        return i - this.bufferPos;
    }

    public boolean isAtEnd() {
        return this.bufferPos == this.bufferSize;
    }

    public int getPosition() {
        return this.bufferPos - this.bufferStart;
    }

    public byte[] getData(int i, int i2) {
        if (i2 == 0) {
            return WireFormatNano.EMPTY_BYTES;
        }
        byte[] bArr = new byte[i2];
        System.arraycopy(this.buffer, this.bufferStart + i, bArr, 0, i2);
        return bArr;
    }

    public void rewindToPosition(int i) {
        int i2 = this.bufferPos;
        int i3 = this.bufferStart;
        if (i > i2 - i3) {
            throw new IllegalArgumentException("Position " + i + " is beyond current " + (this.bufferPos - this.bufferStart));
        } else if (i < 0) {
            throw new IllegalArgumentException("Bad position " + i);
        } else {
            this.bufferPos = i3 + i;
        }
    }

    public byte readRawByte() throws IOException {
        int i = this.bufferPos;
        if (i == this.bufferSize) {
            throw InvalidProtocolBufferNanoException.truncatedMessage();
        }
        byte[] bArr = this.buffer;
        this.bufferPos = i + 1;
        return bArr[i];
    }

    public byte[] readRawBytes(int i) throws IOException {
        if (i < 0) {
            throw InvalidProtocolBufferNanoException.negativeSize();
        }
        int i2 = this.bufferPos;
        int i3 = i2 + i;
        int i4 = this.currentLimit;
        if (i3 > i4) {
            skipRawBytes(i4 - i2);
            throw InvalidProtocolBufferNanoException.truncatedMessage();
        } else if (i <= this.bufferSize - i2) {
            byte[] bArr = new byte[i];
            System.arraycopy(this.buffer, i2, bArr, 0, i);
            this.bufferPos += i;
            return bArr;
        } else {
            throw InvalidProtocolBufferNanoException.truncatedMessage();
        }
    }

    public void skipRawBytes(int i) throws IOException {
        if (i < 0) {
            throw InvalidProtocolBufferNanoException.negativeSize();
        }
        int i2 = this.bufferPos;
        int i3 = i2 + i;
        int i4 = this.currentLimit;
        if (i3 > i4) {
            skipRawBytes(i4 - i2);
            throw InvalidProtocolBufferNanoException.truncatedMessage();
        } else if (i <= this.bufferSize - i2) {
            this.bufferPos = i2 + i;
        } else {
            throw InvalidProtocolBufferNanoException.truncatedMessage();
        }
    }
}
