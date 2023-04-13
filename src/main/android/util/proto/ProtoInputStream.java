package android.util.proto;

import android.util.LongArray;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Objects;
/* loaded from: classes3.dex */
public final class ProtoInputStream extends ProtoStream {
    private static final int DEFAULT_BUFFER_SIZE = 8192;
    public static final int NO_MORE_FIELDS = -1;
    private static final byte STATE_FIELD_MISS = 4;
    private static final byte STATE_READING_PACKED = 2;
    private static final byte STATE_STARTED_FIELD_READ = 1;
    private byte[] mBuffer;
    private final int mBufferSize;
    private int mDepth;
    private int mDiscardedBytes;
    private int mEnd;
    private LongArray mExpectedObjectTokenStack;
    private int mFieldNumber;
    private int mOffset;
    private int mPackedEnd;
    private byte mState;
    private InputStream mStream;
    private int mWireType;

    public ProtoInputStream(InputStream stream, int bufferSize) {
        this.mState = (byte) 0;
        this.mExpectedObjectTokenStack = null;
        this.mDepth = -1;
        this.mDiscardedBytes = 0;
        this.mOffset = 0;
        this.mEnd = 0;
        this.mPackedEnd = 0;
        this.mStream = stream;
        if (bufferSize > 0) {
            this.mBufferSize = bufferSize;
        } else {
            this.mBufferSize = 8192;
        }
        this.mBuffer = new byte[this.mBufferSize];
    }

    public ProtoInputStream(InputStream stream) {
        this(stream, 8192);
    }

    public ProtoInputStream(byte[] buffer) {
        this.mState = (byte) 0;
        this.mExpectedObjectTokenStack = null;
        this.mDepth = -1;
        this.mDiscardedBytes = 0;
        this.mOffset = 0;
        this.mEnd = 0;
        this.mPackedEnd = 0;
        this.mBufferSize = buffer.length;
        this.mEnd = buffer.length;
        this.mBuffer = buffer;
        this.mStream = null;
    }

    public int getFieldNumber() {
        return this.mFieldNumber;
    }

    public int getWireType() {
        if ((this.mState & 2) == 2) {
            return 2;
        }
        return this.mWireType;
    }

    public int getOffset() {
        return this.mOffset + this.mDiscardedBytes;
    }

    public int nextField() throws IOException {
        byte b = this.mState;
        if ((b & 4) == 4) {
            this.mState = (byte) (b & (-5));
            return this.mFieldNumber;
        }
        if ((b & 1) == 1) {
            skip();
            this.mState = (byte) (this.mState & (-2));
        }
        if ((this.mState & 2) == 2) {
            if (getOffset() < this.mPackedEnd) {
                this.mState = (byte) (this.mState | 1);
                return this.mFieldNumber;
            } else if (getOffset() == this.mPackedEnd) {
                this.mState = (byte) (this.mState & (-3));
            } else {
                throw new ProtoParseException("Unexpectedly reached end of packed field at offset 0x" + Integer.toHexString(this.mPackedEnd) + dumpDebugData());
            }
        }
        if (this.mDepth >= 0 && getOffset() == getOffsetFromToken(this.mExpectedObjectTokenStack.get(this.mDepth))) {
            this.mFieldNumber = -1;
        } else {
            readTag();
        }
        return this.mFieldNumber;
    }

    public boolean nextField(long fieldId) throws IOException {
        if (nextField() == ((int) fieldId)) {
            return true;
        }
        this.mState = (byte) (this.mState | 4);
        return false;
    }

    public double readDouble(long fieldId) throws IOException {
        assertFreshData();
        assertFieldNumber(fieldId);
        checkPacked(fieldId);
        switch ((int) ((ProtoStream.FIELD_TYPE_MASK & fieldId) >>> 32)) {
            case 1:
                assertWireType(1);
                double value = Double.longBitsToDouble(readFixed64());
                this.mState = (byte) (this.mState & (-2));
                return value;
            default:
                throw new IllegalArgumentException("Requested field id (" + getFieldIdString(fieldId) + ") cannot be read as a double" + dumpDebugData());
        }
    }

    public float readFloat(long fieldId) throws IOException {
        assertFreshData();
        assertFieldNumber(fieldId);
        checkPacked(fieldId);
        switch ((int) ((ProtoStream.FIELD_TYPE_MASK & fieldId) >>> 32)) {
            case 2:
                assertWireType(5);
                float value = Float.intBitsToFloat(readFixed32());
                this.mState = (byte) (this.mState & (-2));
                return value;
            default:
                throw new IllegalArgumentException("Requested field id (" + getFieldIdString(fieldId) + ") is not a float" + dumpDebugData());
        }
    }

    public int readInt(long fieldId) throws IOException {
        int value;
        assertFreshData();
        assertFieldNumber(fieldId);
        checkPacked(fieldId);
        switch ((int) ((ProtoStream.FIELD_TYPE_MASK & fieldId) >>> 32)) {
            case 5:
            case 13:
            case 14:
                assertWireType(0);
                value = (int) readVarint();
                break;
            case 7:
            case 15:
                assertWireType(5);
                value = readFixed32();
                break;
            case 17:
                assertWireType(0);
                value = decodeZigZag32((int) readVarint());
                break;
            default:
                throw new IllegalArgumentException("Requested field id (" + getFieldIdString(fieldId) + ") is not an int" + dumpDebugData());
        }
        this.mState = (byte) (this.mState & (-2));
        return value;
    }

    public long readLong(long fieldId) throws IOException {
        long value;
        assertFreshData();
        assertFieldNumber(fieldId);
        checkPacked(fieldId);
        switch ((int) ((ProtoStream.FIELD_TYPE_MASK & fieldId) >>> 32)) {
            case 3:
            case 4:
                assertWireType(0);
                value = readVarint();
                break;
            case 6:
            case 16:
                assertWireType(1);
                value = readFixed64();
                break;
            case 18:
                assertWireType(0);
                value = decodeZigZag64(readVarint());
                break;
            default:
                throw new IllegalArgumentException("Requested field id (" + getFieldIdString(fieldId) + ") is not an long" + dumpDebugData());
        }
        this.mState = (byte) (this.mState & (-2));
        return value;
    }

    public boolean readBoolean(long fieldId) throws IOException {
        assertFreshData();
        assertFieldNumber(fieldId);
        checkPacked(fieldId);
        switch ((int) ((ProtoStream.FIELD_TYPE_MASK & fieldId) >>> 32)) {
            case 8:
                assertWireType(0);
                boolean value = readVarint() != 0;
                this.mState = (byte) (this.mState & (-2));
                return value;
            default:
                throw new IllegalArgumentException("Requested field id (" + getFieldIdString(fieldId) + ") is not an boolean" + dumpDebugData());
        }
    }

    public String readString(long fieldId) throws IOException {
        assertFreshData();
        assertFieldNumber(fieldId);
        switch ((int) ((ProtoStream.FIELD_TYPE_MASK & fieldId) >>> 32)) {
            case 9:
                assertWireType(2);
                int len = (int) readVarint();
                String value = readRawString(len);
                int len2 = this.mState;
                this.mState = (byte) (len2 & (-2));
                return value;
            default:
                throw new IllegalArgumentException("Requested field id(" + getFieldIdString(fieldId) + ") is not an string" + dumpDebugData());
        }
    }

    public byte[] readBytes(long fieldId) throws IOException {
        assertFreshData();
        assertFieldNumber(fieldId);
        switch ((int) ((ProtoStream.FIELD_TYPE_MASK & fieldId) >>> 32)) {
            case 11:
            case 12:
                assertWireType(2);
                int len = (int) readVarint();
                byte[] value = readRawBytes(len);
                int len2 = this.mState;
                this.mState = (byte) (len2 & (-2));
                return value;
            default:
                throw new IllegalArgumentException("Requested field type (" + getFieldIdString(fieldId) + ") cannot be read as raw bytes" + dumpDebugData());
        }
    }

    public long start(long fieldId) throws IOException {
        assertFreshData();
        assertFieldNumber(fieldId);
        assertWireType(2);
        int messageSize = (int) readVarint();
        if (this.mExpectedObjectTokenStack == null) {
            this.mExpectedObjectTokenStack = new LongArray();
        }
        int i = this.mDepth + 1;
        this.mDepth = i;
        if (i == this.mExpectedObjectTokenStack.size()) {
            this.mExpectedObjectTokenStack.add(makeToken(0, (fieldId & 2199023255552L) == 2199023255552L, this.mDepth, (int) fieldId, getOffset() + messageSize));
        } else {
            LongArray longArray = this.mExpectedObjectTokenStack;
            int i2 = this.mDepth;
            longArray.set(i2, makeToken(0, (fieldId & 2199023255552L) == 2199023255552L, i2, (int) fieldId, getOffset() + messageSize));
        }
        int i3 = this.mDepth;
        if (i3 > 0 && getOffsetFromToken(this.mExpectedObjectTokenStack.get(i3)) > getOffsetFromToken(this.mExpectedObjectTokenStack.get(this.mDepth - 1))) {
            throw new ProtoParseException("Embedded Object (" + token2String(this.mExpectedObjectTokenStack.get(this.mDepth)) + ") ends after of parent Objects's (" + token2String(this.mExpectedObjectTokenStack.get(this.mDepth - 1)) + ") end" + dumpDebugData());
        }
        this.mState = (byte) (this.mState & (-2));
        return this.mExpectedObjectTokenStack.get(this.mDepth);
    }

    public void end(long token) {
        if (this.mExpectedObjectTokenStack.get(this.mDepth) != token) {
            throw new ProtoParseException("end token " + token + " does not match current message token " + this.mExpectedObjectTokenStack.get(this.mDepth) + dumpDebugData());
        }
        if (getOffsetFromToken(this.mExpectedObjectTokenStack.get(this.mDepth)) > getOffset()) {
            incOffset(getOffsetFromToken(this.mExpectedObjectTokenStack.get(this.mDepth)) - getOffset());
        }
        this.mDepth--;
        this.mState = (byte) (this.mState & (-2));
    }

    private void readTag() throws IOException {
        fillBuffer();
        if (this.mOffset >= this.mEnd) {
            this.mFieldNumber = -1;
            return;
        }
        int tag = (int) readVarint();
        this.mFieldNumber = tag >>> 3;
        this.mWireType = tag & 7;
        this.mState = (byte) (this.mState | 1);
    }

    public int decodeZigZag32(int n) {
        return (n >>> 1) ^ (-(n & 1));
    }

    public long decodeZigZag64(long n) {
        return (n >>> 1) ^ (-(1 & n));
    }

    private long readVarint() throws IOException {
        long value = 0;
        int shift = 0;
        while (true) {
            fillBuffer();
            int fragment = this.mEnd - this.mOffset;
            if (fragment < 0) {
                throw new ProtoParseException("Incomplete varint at offset 0x" + Integer.toHexString(getOffset()) + dumpDebugData());
            }
            for (int i = 0; i < fragment; i++) {
                byte b = this.mBuffer[this.mOffset + i];
                value |= (b & 127) << shift;
                if ((b & 128) == 0) {
                    incOffset(i + 1);
                    return value;
                }
                shift += 7;
                if (shift > 63) {
                    throw new ProtoParseException("Varint is too large at offset 0x" + Integer.toHexString(getOffset() + i) + dumpDebugData());
                }
            }
            incOffset(fragment);
        }
    }

    private int readFixed32() throws IOException {
        if (this.mOffset + 4 <= this.mEnd) {
            incOffset(4);
            byte[] bArr = this.mBuffer;
            int i = this.mOffset;
            return ((bArr[i - 1] & 255) << 24) | (bArr[i - 4] & 255) | ((bArr[i - 3] & 255) << 8) | ((bArr[i - 2] & 255) << 16);
        }
        int value = 0;
        int shift = 0;
        int bytesLeft = 4;
        while (bytesLeft > 0) {
            fillBuffer();
            int i2 = this.mEnd;
            int i3 = this.mOffset;
            int fragment = i2 - i3 < bytesLeft ? i2 - i3 : bytesLeft;
            if (fragment < 0) {
                throw new ProtoParseException("Incomplete fixed32 at offset 0x" + Integer.toHexString(getOffset()) + dumpDebugData());
            }
            incOffset(fragment);
            bytesLeft -= fragment;
            while (fragment > 0) {
                value |= (this.mBuffer[this.mOffset - fragment] & 255) << shift;
                fragment--;
                shift += 8;
            }
        }
        return value;
    }

    private long readFixed64() throws IOException {
        if (this.mOffset + 8 <= this.mEnd) {
            incOffset(8);
            byte[] bArr = this.mBuffer;
            int i = this.mOffset;
            return ((bArr[i - 1] & 255) << 56) | (bArr[i - 8] & 255) | ((bArr[i - 7] & 255) << 8) | ((bArr[i - 6] & 255) << 16) | ((bArr[i - 5] & 255) << 24) | ((bArr[i - 4] & 255) << 32) | ((bArr[i - 3] & 255) << 40) | ((bArr[i - 2] & 255) << 48);
        }
        long value = 0;
        int shift = 0;
        int bytesLeft = 8;
        while (bytesLeft > 0) {
            fillBuffer();
            int i2 = this.mEnd;
            int i3 = this.mOffset;
            int fragment = i2 - i3 < bytesLeft ? i2 - i3 : bytesLeft;
            if (fragment < 0) {
                throw new ProtoParseException("Incomplete fixed64 at offset 0x" + Integer.toHexString(getOffset()) + dumpDebugData());
            }
            incOffset(fragment);
            bytesLeft -= fragment;
            while (fragment > 0) {
                value |= (this.mBuffer[this.mOffset - fragment] & 255) << shift;
                fragment--;
                shift += 8;
            }
        }
        return value;
    }

    private byte[] readRawBytes(int n) throws IOException {
        byte[] buffer = new byte[n];
        int pos = 0;
        do {
            int i = this.mOffset;
            int i2 = (i + n) - pos;
            int i3 = this.mEnd;
            if (i2 > i3) {
                int fragment = i3 - i;
                if (fragment > 0) {
                    System.arraycopy(this.mBuffer, i, buffer, pos, fragment);
                    incOffset(fragment);
                    pos += fragment;
                }
                fillBuffer();
            } else {
                System.arraycopy(this.mBuffer, i, buffer, pos, n - pos);
                incOffset(n - pos);
                return buffer;
            }
        } while (this.mOffset < this.mEnd);
        throw new ProtoParseException("Unexpectedly reached end of the InputStream at offset 0x" + Integer.toHexString(this.mEnd) + dumpDebugData());
    }

    private String readRawString(int n) throws IOException {
        fillBuffer();
        int i = this.mOffset;
        int i2 = i + n;
        int i3 = this.mEnd;
        if (i2 <= i3) {
            String value = new String(this.mBuffer, i, n, StandardCharsets.UTF_8);
            incOffset(n);
            return value;
        } else if (n <= this.mBufferSize) {
            int stringHead = i3 - i;
            byte[] bArr = this.mBuffer;
            System.arraycopy(bArr, i, bArr, 0, stringHead);
            this.mEnd = this.mStream.read(this.mBuffer, stringHead, n - stringHead) + stringHead;
            this.mDiscardedBytes += this.mOffset;
            this.mOffset = 0;
            String value2 = new String(this.mBuffer, 0, n, StandardCharsets.UTF_8);
            incOffset(n);
            return value2;
        } else {
            return new String(readRawBytes(n), 0, n, StandardCharsets.UTF_8);
        }
    }

    private void fillBuffer() throws IOException {
        InputStream inputStream;
        int i;
        int i2 = this.mOffset;
        int i3 = this.mEnd;
        if (i2 >= i3 && (inputStream = this.mStream) != null) {
            int i4 = i2 - i3;
            this.mOffset = i4;
            this.mDiscardedBytes += i3;
            if (i4 >= this.mBufferSize) {
                int skipped = (int) inputStream.skip((i4 / i) * i);
                this.mDiscardedBytes += skipped;
                this.mOffset -= skipped;
            }
            this.mEnd = this.mStream.read(this.mBuffer);
        }
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    public void skip() throws IOException {
        byte b;
        if ((this.mState & 2) == 2) {
            incOffset(this.mPackedEnd - getOffset());
        } else {
            switch (this.mWireType) {
                case 0:
                    do {
                        fillBuffer();
                        b = this.mBuffer[this.mOffset];
                        incOffset(1);
                    } while ((b & 128) != 0);
                    break;
                case 1:
                    incOffset(8);
                    break;
                case 2:
                    fillBuffer();
                    int length = (int) readVarint();
                    incOffset(length);
                    break;
                case 3:
                case 4:
                default:
                    throw new ProtoParseException("Unexpected wire type: " + this.mWireType + " at offset 0x" + Integer.toHexString(this.mOffset) + dumpDebugData());
                case 5:
                    incOffset(4);
                    break;
            }
        }
        byte b2 = this.mState;
        this.mState = (byte) (b2 & (-2));
    }

    private void incOffset(int n) {
        this.mOffset += n;
        if (this.mDepth >= 0 && getOffset() > getOffsetFromToken(this.mExpectedObjectTokenStack.get(this.mDepth))) {
            throw new ProtoParseException("Unexpectedly reached end of embedded object.  " + token2String(this.mExpectedObjectTokenStack.get(this.mDepth)) + dumpDebugData());
        }
    }

    private void checkPacked(long fieldId) throws IOException {
        if (this.mWireType == 2) {
            int length = (int) readVarint();
            this.mPackedEnd = getOffset() + length;
            this.mState = (byte) (2 | this.mState);
            switch ((int) ((ProtoStream.FIELD_TYPE_MASK & fieldId) >>> 32)) {
                case 1:
                case 6:
                case 16:
                    if (length % 8 != 0) {
                        throw new IllegalArgumentException("Requested field id (" + getFieldIdString(fieldId) + ") packed length " + length + " is not aligned for fixed64" + dumpDebugData());
                    }
                    this.mWireType = 1;
                    return;
                case 2:
                case 7:
                case 15:
                    if (length % 4 != 0) {
                        throw new IllegalArgumentException("Requested field id (" + getFieldIdString(fieldId) + ") packed length " + length + " is not aligned for fixed32" + dumpDebugData());
                    }
                    this.mWireType = 5;
                    return;
                case 3:
                case 4:
                case 5:
                case 8:
                case 13:
                case 14:
                case 17:
                case 18:
                    this.mWireType = 0;
                    return;
                case 9:
                case 10:
                case 11:
                case 12:
                default:
                    throw new IllegalArgumentException("Requested field id (" + getFieldIdString(fieldId) + ") is not a packable field" + dumpDebugData());
            }
        }
    }

    private void assertFieldNumber(long fieldId) {
        if (((int) fieldId) != this.mFieldNumber) {
            throw new IllegalArgumentException("Requested field id (" + getFieldIdString(fieldId) + ") does not match current field number (0x" + Integer.toHexString(this.mFieldNumber) + ") at offset 0x" + Integer.toHexString(getOffset()) + dumpDebugData());
        }
    }

    private void assertWireType(int wireType) {
        if (wireType != this.mWireType) {
            throw new WireTypeMismatchException("Current wire type " + getWireTypeString(this.mWireType) + " does not match expected wire type " + getWireTypeString(wireType) + " at offset 0x" + Integer.toHexString(getOffset()) + dumpDebugData());
        }
    }

    private void assertFreshData() {
        if ((this.mState & 1) != 1) {
            throw new ProtoParseException("Attempting to read already read field at offset 0x" + Integer.toHexString(getOffset()) + dumpDebugData());
        }
    }

    public String dumpDebugData() {
        StringBuilder sb = new StringBuilder();
        sb.append("\nmFieldNumber : 0x").append(Integer.toHexString(this.mFieldNumber));
        sb.append("\nmWireType : 0x").append(Integer.toHexString(this.mWireType));
        sb.append("\nmState : 0x").append(Integer.toHexString(this.mState));
        sb.append("\nmDiscardedBytes : 0x").append(Integer.toHexString(this.mDiscardedBytes));
        sb.append("\nmOffset : 0x").append(Integer.toHexString(this.mOffset));
        sb.append("\nmExpectedObjectTokenStack : ").append(Objects.toString(this.mExpectedObjectTokenStack));
        sb.append("\nmDepth : 0x").append(Integer.toHexString(this.mDepth));
        sb.append("\nmBuffer : ").append(Arrays.toString(this.mBuffer));
        sb.append("\nmBufferSize : 0x").append(Integer.toHexString(this.mBufferSize));
        sb.append("\nmEnd : 0x").append(Integer.toHexString(this.mEnd));
        return sb.toString();
    }
}
