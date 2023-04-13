package com.android.framework.protobuf.nano;

import android.hardware.biometrics.fingerprint.AcquiredInfo;
import java.io.IOException;
/* loaded from: classes4.dex */
public final class CodedInputByteBufferNano {
    private static final int DEFAULT_RECURSION_LIMIT = 64;
    private static final int DEFAULT_SIZE_LIMIT = 67108864;
    private final byte[] buffer;
    private int bufferPos;
    private int bufferSize;
    private int bufferSizeAfterLimit;
    private int bufferStart;
    private int lastTag;
    private int recursionDepth;
    private int currentLimit = Integer.MAX_VALUE;
    private int recursionLimit = 64;
    private int sizeLimit = 67108864;

    public static CodedInputByteBufferNano newInstance(byte[] buf) {
        return newInstance(buf, 0, buf.length);
    }

    public static CodedInputByteBufferNano newInstance(byte[] buf, int off, int len) {
        return new CodedInputByteBufferNano(buf, off, len);
    }

    public int readTag() throws IOException {
        if (isAtEnd()) {
            this.lastTag = 0;
            return 0;
        }
        int readRawVarint32 = readRawVarint32();
        this.lastTag = readRawVarint32;
        if (readRawVarint32 == 0) {
            throw InvalidProtocolBufferNanoException.invalidTag();
        }
        return readRawVarint32;
    }

    public void checkLastTagWas(int value) throws InvalidProtocolBufferNanoException {
        if (this.lastTag != value) {
            throw InvalidProtocolBufferNanoException.invalidEndTag();
        }
    }

    public boolean skipField(int tag) throws IOException {
        switch (WireFormatNano.getTagWireType(tag)) {
            case 0:
                readInt32();
                return true;
            case 1:
                readRawLittleEndian64();
                return true;
            case 2:
                skipRawBytes(readRawVarint32());
                return true;
            case 3:
                skipMessage();
                checkLastTagWas(WireFormatNano.makeTag(WireFormatNano.getTagFieldNumber(tag), 4));
                return true;
            case 4:
                return false;
            case 5:
                readRawLittleEndian32();
                return true;
            default:
                throw InvalidProtocolBufferNanoException.invalidWireType();
        }
    }

    public void skipMessage() throws IOException {
        int tag;
        do {
            tag = readTag();
            if (tag == 0) {
                return;
            }
        } while (skipField(tag));
    }

    public double readDouble() throws IOException {
        return Double.longBitsToDouble(readRawLittleEndian64());
    }

    public float readFloat() throws IOException {
        return Float.intBitsToFloat(readRawLittleEndian32());
    }

    public long readUInt64() throws IOException {
        return readRawVarint64();
    }

    public long readInt64() throws IOException {
        return readRawVarint64();
    }

    public int readInt32() throws IOException {
        return readRawVarint32();
    }

    public long readFixed64() throws IOException {
        return readRawLittleEndian64();
    }

    public int readFixed32() throws IOException {
        return readRawLittleEndian32();
    }

    public boolean readBool() throws IOException {
        return readRawVarint32() != 0;
    }

    public String readString() throws IOException {
        int size = readRawVarint32();
        int i = this.bufferSize;
        int i2 = this.bufferPos;
        if (size <= i - i2 && size > 0) {
            String result = new String(this.buffer, i2, size, InternalNano.UTF_8);
            this.bufferPos += size;
            return result;
        }
        return new String(readRawBytes(size), InternalNano.UTF_8);
    }

    public void readGroup(MessageNano msg, int fieldNumber) throws IOException {
        int i = this.recursionDepth;
        if (i >= this.recursionLimit) {
            throw InvalidProtocolBufferNanoException.recursionLimitExceeded();
        }
        this.recursionDepth = i + 1;
        msg.mergeFrom(this);
        checkLastTagWas(WireFormatNano.makeTag(fieldNumber, 4));
        this.recursionDepth--;
    }

    public void readMessage(MessageNano msg) throws IOException {
        int length = readRawVarint32();
        if (this.recursionDepth >= this.recursionLimit) {
            throw InvalidProtocolBufferNanoException.recursionLimitExceeded();
        }
        int oldLimit = pushLimit(length);
        this.recursionDepth++;
        msg.mergeFrom(this);
        checkLastTagWas(0);
        this.recursionDepth--;
        popLimit(oldLimit);
    }

    public byte[] readBytes() throws IOException {
        int size = readRawVarint32();
        int i = this.bufferSize;
        int i2 = this.bufferPos;
        if (size <= i - i2 && size > 0) {
            byte[] result = new byte[size];
            System.arraycopy(this.buffer, i2, result, 0, size);
            this.bufferPos += size;
            return result;
        } else if (size == 0) {
            return WireFormatNano.EMPTY_BYTES;
        } else {
            return readRawBytes(size);
        }
    }

    public int readUInt32() throws IOException {
        return readRawVarint32();
    }

    public int readEnum() throws IOException {
        return readRawVarint32();
    }

    public int readSFixed32() throws IOException {
        return readRawLittleEndian32();
    }

    public long readSFixed64() throws IOException {
        return readRawLittleEndian64();
    }

    public int readSInt32() throws IOException {
        return decodeZigZag32(readRawVarint32());
    }

    public long readSInt64() throws IOException {
        return decodeZigZag64(readRawVarint64());
    }

    public int readRawVarint32() throws IOException {
        byte tmp = readRawByte();
        if (tmp >= 0) {
            return tmp;
        }
        int result = tmp & Byte.MAX_VALUE;
        byte tmp2 = readRawByte();
        if (tmp2 >= 0) {
            return result | (tmp2 << 7);
        }
        int result2 = result | ((tmp2 & Byte.MAX_VALUE) << 7);
        byte tmp3 = readRawByte();
        if (tmp3 >= 0) {
            return result2 | (tmp3 << AcquiredInfo.POWER_PRESS);
        }
        int result3 = result2 | ((tmp3 & Byte.MAX_VALUE) << 14);
        byte tmp4 = readRawByte();
        if (tmp4 >= 0) {
            return result3 | (tmp4 << 21);
        }
        int result4 = result3 | ((tmp4 & Byte.MAX_VALUE) << 21);
        byte tmp5 = readRawByte();
        int result5 = result4 | (tmp5 << 28);
        if (tmp5 < 0) {
            for (int i = 0; i < 5; i++) {
                if (readRawByte() >= 0) {
                    return result5;
                }
            }
            throw InvalidProtocolBufferNanoException.malformedVarint();
        }
        return result5;
    }

    public long readRawVarint64() throws IOException {
        long result = 0;
        for (int shift = 0; shift < 64; shift += 7) {
            byte b = readRawByte();
            result |= (b & Byte.MAX_VALUE) << shift;
            if ((b & 128) == 0) {
                return result;
            }
        }
        throw InvalidProtocolBufferNanoException.malformedVarint();
    }

    public int readRawLittleEndian32() throws IOException {
        byte b1 = readRawByte();
        byte b2 = readRawByte();
        byte b3 = readRawByte();
        byte b4 = readRawByte();
        return (b1 & 255) | ((b2 & 255) << 8) | ((b3 & 255) << 16) | ((b4 & 255) << 24);
    }

    public long readRawLittleEndian64() throws IOException {
        byte b1 = readRawByte();
        byte b2 = readRawByte();
        byte b3 = readRawByte();
        byte b4 = readRawByte();
        byte b5 = readRawByte();
        byte b6 = readRawByte();
        byte b7 = readRawByte();
        byte b8 = readRawByte();
        return (b1 & 255) | ((b2 & 255) << 8) | ((b3 & 255) << 16) | ((b4 & 255) << 24) | ((b5 & 255) << 32) | ((b6 & 255) << 40) | ((b7 & 255) << 48) | ((255 & b8) << 56);
    }

    public static int decodeZigZag32(int n) {
        return (n >>> 1) ^ (-(n & 1));
    }

    public static long decodeZigZag64(long n) {
        return (n >>> 1) ^ (-(1 & n));
    }

    private CodedInputByteBufferNano(byte[] buffer, int off, int len) {
        this.buffer = buffer;
        this.bufferStart = off;
        this.bufferSize = off + len;
        this.bufferPos = off;
    }

    public int setRecursionLimit(int limit) {
        if (limit < 0) {
            throw new IllegalArgumentException("Recursion limit cannot be negative: " + limit);
        }
        int oldLimit = this.recursionLimit;
        this.recursionLimit = limit;
        return oldLimit;
    }

    public int setSizeLimit(int limit) {
        if (limit < 0) {
            throw new IllegalArgumentException("Size limit cannot be negative: " + limit);
        }
        int oldLimit = this.sizeLimit;
        this.sizeLimit = limit;
        return oldLimit;
    }

    public void resetSizeCounter() {
    }

    public int pushLimit(int byteLimit) throws InvalidProtocolBufferNanoException {
        if (byteLimit < 0) {
            throw InvalidProtocolBufferNanoException.negativeSize();
        }
        int byteLimit2 = byteLimit + this.bufferPos;
        int oldLimit = this.currentLimit;
        if (byteLimit2 > oldLimit) {
            throw InvalidProtocolBufferNanoException.truncatedMessage();
        }
        this.currentLimit = byteLimit2;
        recomputeBufferSizeAfterLimit();
        return oldLimit;
    }

    private void recomputeBufferSizeAfterLimit() {
        int i = this.bufferSize + this.bufferSizeAfterLimit;
        this.bufferSize = i;
        int bufferEnd = this.bufferSize;
        int i2 = this.currentLimit;
        if (bufferEnd > i2) {
            int i3 = bufferEnd - i2;
            this.bufferSizeAfterLimit = i3;
            this.bufferSize = i - i3;
            return;
        }
        this.bufferSizeAfterLimit = 0;
    }

    public void popLimit(int oldLimit) {
        this.currentLimit = oldLimit;
        recomputeBufferSizeAfterLimit();
    }

    public int getBytesUntilLimit() {
        int i = this.currentLimit;
        if (i == Integer.MAX_VALUE) {
            return -1;
        }
        int currentAbsolutePosition = this.bufferPos;
        return i - currentAbsolutePosition;
    }

    public boolean isAtEnd() {
        return this.bufferPos == this.bufferSize;
    }

    public int getPosition() {
        return this.bufferPos - this.bufferStart;
    }

    public byte[] getData(int offset, int length) {
        if (length == 0) {
            return WireFormatNano.EMPTY_BYTES;
        }
        byte[] copy = new byte[length];
        int start = this.bufferStart + offset;
        System.arraycopy(this.buffer, start, copy, 0, length);
        return copy;
    }

    public void rewindToPosition(int position) {
        int i = this.bufferPos;
        int i2 = this.bufferStart;
        if (position > i - i2) {
            throw new IllegalArgumentException("Position " + position + " is beyond current " + (this.bufferPos - this.bufferStart));
        }
        if (position < 0) {
            throw new IllegalArgumentException("Bad position " + position);
        }
        this.bufferPos = i2 + position;
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

    public byte[] readRawBytes(int size) throws IOException {
        if (size < 0) {
            throw InvalidProtocolBufferNanoException.negativeSize();
        }
        int i = this.bufferPos;
        int i2 = i + size;
        int i3 = this.currentLimit;
        if (i2 > i3) {
            skipRawBytes(i3 - i);
            throw InvalidProtocolBufferNanoException.truncatedMessage();
        } else if (size <= this.bufferSize - i) {
            byte[] bytes = new byte[size];
            System.arraycopy(this.buffer, i, bytes, 0, size);
            this.bufferPos += size;
            return bytes;
        } else {
            throw InvalidProtocolBufferNanoException.truncatedMessage();
        }
    }

    public void skipRawBytes(int size) throws IOException {
        if (size < 0) {
            throw InvalidProtocolBufferNanoException.negativeSize();
        }
        int i = this.bufferPos;
        int i2 = i + size;
        int i3 = this.currentLimit;
        if (i2 > i3) {
            skipRawBytes(i3 - i);
            throw InvalidProtocolBufferNanoException.truncatedMessage();
        } else if (size <= this.bufferSize - i) {
            this.bufferPos = i + size;
        } else {
            throw InvalidProtocolBufferNanoException.truncatedMessage();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Object readPrimitiveField(int type) throws IOException {
        switch (type) {
            case 1:
                return Double.valueOf(readDouble());
            case 2:
                return Float.valueOf(readFloat());
            case 3:
                return Long.valueOf(readInt64());
            case 4:
                return Long.valueOf(readUInt64());
            case 5:
                return Integer.valueOf(readInt32());
            case 6:
                return Long.valueOf(readFixed64());
            case 7:
                return Integer.valueOf(readFixed32());
            case 8:
                return Boolean.valueOf(readBool());
            case 9:
                return readString();
            case 10:
            case 11:
            default:
                throw new IllegalArgumentException("Unknown type " + type);
            case 12:
                return readBytes();
            case 13:
                return Integer.valueOf(readUInt32());
            case 14:
                return Integer.valueOf(readEnum());
            case 15:
                return Integer.valueOf(readSFixed32());
            case 16:
                return Long.valueOf(readSFixed64());
            case 17:
                return Integer.valueOf(readSInt32());
            case 18:
                return Long.valueOf(readSInt64());
        }
    }
}
