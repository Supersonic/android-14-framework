package com.android.framework.protobuf.nano;

import android.hardware.graphics.common.BufferUsage;
import android.p008os.BatteryStats;
import java.io.IOException;
import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ReadOnlyBufferException;
/* loaded from: classes4.dex */
public final class CodedOutputByteBufferNano {
    public static final int LITTLE_ENDIAN_32_SIZE = 4;
    public static final int LITTLE_ENDIAN_64_SIZE = 8;
    private static final int MAX_UTF8_EXPANSION = 3;
    private final ByteBuffer buffer;

    private CodedOutputByteBufferNano(byte[] buffer, int offset, int length) {
        this(ByteBuffer.wrap(buffer, offset, length));
    }

    private CodedOutputByteBufferNano(ByteBuffer buffer) {
        this.buffer = buffer;
        buffer.order(ByteOrder.LITTLE_ENDIAN);
    }

    public static CodedOutputByteBufferNano newInstance(byte[] flatArray) {
        return newInstance(flatArray, 0, flatArray.length);
    }

    public static CodedOutputByteBufferNano newInstance(byte[] flatArray, int offset, int length) {
        return new CodedOutputByteBufferNano(flatArray, offset, length);
    }

    public void writeDouble(int fieldNumber, double value) throws IOException {
        writeTag(fieldNumber, 1);
        writeDoubleNoTag(value);
    }

    public void writeFloat(int fieldNumber, float value) throws IOException {
        writeTag(fieldNumber, 5);
        writeFloatNoTag(value);
    }

    public void writeUInt64(int fieldNumber, long value) throws IOException {
        writeTag(fieldNumber, 0);
        writeUInt64NoTag(value);
    }

    public void writeInt64(int fieldNumber, long value) throws IOException {
        writeTag(fieldNumber, 0);
        writeInt64NoTag(value);
    }

    public void writeInt32(int fieldNumber, int value) throws IOException {
        writeTag(fieldNumber, 0);
        writeInt32NoTag(value);
    }

    public void writeFixed64(int fieldNumber, long value) throws IOException {
        writeTag(fieldNumber, 1);
        writeFixed64NoTag(value);
    }

    public void writeFixed32(int fieldNumber, int value) throws IOException {
        writeTag(fieldNumber, 5);
        writeFixed32NoTag(value);
    }

    public void writeBool(int fieldNumber, boolean value) throws IOException {
        writeTag(fieldNumber, 0);
        writeBoolNoTag(value);
    }

    public void writeString(int fieldNumber, String value) throws IOException {
        writeTag(fieldNumber, 2);
        writeStringNoTag(value);
    }

    public void writeGroup(int fieldNumber, MessageNano value) throws IOException {
        writeTag(fieldNumber, 3);
        writeGroupNoTag(value);
        writeTag(fieldNumber, 4);
    }

    public void writeMessage(int fieldNumber, MessageNano value) throws IOException {
        writeTag(fieldNumber, 2);
        writeMessageNoTag(value);
    }

    public void writeBytes(int fieldNumber, byte[] value) throws IOException {
        writeTag(fieldNumber, 2);
        writeBytesNoTag(value);
    }

    public void writeUInt32(int fieldNumber, int value) throws IOException {
        writeTag(fieldNumber, 0);
        writeUInt32NoTag(value);
    }

    public void writeEnum(int fieldNumber, int value) throws IOException {
        writeTag(fieldNumber, 0);
        writeEnumNoTag(value);
    }

    public void writeSFixed32(int fieldNumber, int value) throws IOException {
        writeTag(fieldNumber, 5);
        writeSFixed32NoTag(value);
    }

    public void writeSFixed64(int fieldNumber, long value) throws IOException {
        writeTag(fieldNumber, 1);
        writeSFixed64NoTag(value);
    }

    public void writeSInt32(int fieldNumber, int value) throws IOException {
        writeTag(fieldNumber, 0);
        writeSInt32NoTag(value);
    }

    public void writeSInt64(int fieldNumber, long value) throws IOException {
        writeTag(fieldNumber, 0);
        writeSInt64NoTag(value);
    }

    public void writeDoubleNoTag(double value) throws IOException {
        writeRawLittleEndian64(Double.doubleToLongBits(value));
    }

    public void writeFloatNoTag(float value) throws IOException {
        writeRawLittleEndian32(Float.floatToIntBits(value));
    }

    public void writeUInt64NoTag(long value) throws IOException {
        writeRawVarint64(value);
    }

    public void writeInt64NoTag(long value) throws IOException {
        writeRawVarint64(value);
    }

    public void writeInt32NoTag(int value) throws IOException {
        if (value >= 0) {
            writeRawVarint32(value);
        } else {
            writeRawVarint64(value);
        }
    }

    public void writeFixed64NoTag(long value) throws IOException {
        writeRawLittleEndian64(value);
    }

    public void writeFixed32NoTag(int value) throws IOException {
        writeRawLittleEndian32(value);
    }

    public void writeBoolNoTag(boolean value) throws IOException {
        writeRawByte(value ? 1 : 0);
    }

    public void writeStringNoTag(String value) throws IOException {
        try {
            int minLengthVarIntSize = computeRawVarint32Size(value.length());
            int maxLengthVarIntSize = computeRawVarint32Size(value.length() * 3);
            if (minLengthVarIntSize == maxLengthVarIntSize) {
                int oldPosition = this.buffer.position();
                if (this.buffer.remaining() < minLengthVarIntSize) {
                    throw new OutOfSpaceException(oldPosition + minLengthVarIntSize, this.buffer.limit());
                }
                this.buffer.position(oldPosition + minLengthVarIntSize);
                encode(value, this.buffer);
                int newPosition = this.buffer.position();
                this.buffer.position(oldPosition);
                writeRawVarint32((newPosition - oldPosition) - minLengthVarIntSize);
                this.buffer.position(newPosition);
                return;
            }
            writeRawVarint32(encodedLength(value));
            encode(value, this.buffer);
        } catch (BufferOverflowException e) {
            OutOfSpaceException outOfSpaceException = new OutOfSpaceException(this.buffer.position(), this.buffer.limit());
            outOfSpaceException.initCause(e);
            throw outOfSpaceException;
        }
    }

    private static int encodedLength(CharSequence sequence) {
        int utf16Length = sequence.length();
        int utf8Length = utf16Length;
        int i = 0;
        while (i < utf16Length && sequence.charAt(i) < 128) {
            i++;
        }
        while (true) {
            if (i < utf16Length) {
                char c = sequence.charAt(i);
                if (c < 2048) {
                    utf8Length += (127 - c) >>> 31;
                    i++;
                } else {
                    utf8Length += encodedLengthGeneral(sequence, i);
                    break;
                }
            } else {
                break;
            }
        }
        if (utf8Length < utf16Length) {
            throw new IllegalArgumentException("UTF-8 length does not fit in int: " + (utf8Length + 4294967296L));
        }
        return utf8Length;
    }

    private static int encodedLengthGeneral(CharSequence sequence, int start) {
        int utf16Length = sequence.length();
        int utf8Length = 0;
        int i = start;
        while (i < utf16Length) {
            char c = sequence.charAt(i);
            if (c < 2048) {
                utf8Length += (127 - c) >>> 31;
            } else {
                utf8Length += 2;
                if (55296 <= c && c <= 57343) {
                    int cp = Character.codePointAt(sequence, i);
                    if (cp < 65536) {
                        throw new IllegalArgumentException("Unpaired surrogate at index " + i);
                    }
                    i++;
                }
            }
            i++;
        }
        return utf8Length;
    }

    private static void encode(CharSequence sequence, ByteBuffer byteBuffer) {
        if (byteBuffer.isReadOnly()) {
            throw new ReadOnlyBufferException();
        }
        if (byteBuffer.hasArray()) {
            try {
                int encoded = encode(sequence, byteBuffer.array(), byteBuffer.arrayOffset() + byteBuffer.position(), byteBuffer.remaining());
                byteBuffer.position(encoded - byteBuffer.arrayOffset());
                return;
            } catch (ArrayIndexOutOfBoundsException e) {
                BufferOverflowException boe = new BufferOverflowException();
                boe.initCause(e);
                throw boe;
            }
        }
        encodeDirect(sequence, byteBuffer);
    }

    private static void encodeDirect(CharSequence sequence, ByteBuffer byteBuffer) {
        int utf16Length = sequence.length();
        int i = 0;
        while (i < utf16Length) {
            char c = sequence.charAt(i);
            if (c < 128) {
                byteBuffer.put((byte) c);
            } else if (c < 2048) {
                byteBuffer.put((byte) ((c >>> 6) | 960));
                byteBuffer.put((byte) (128 | (c & '?')));
            } else if (c < 55296 || 57343 < c) {
                byteBuffer.put((byte) ((c >>> '\f') | 480));
                byteBuffer.put((byte) (((c >>> 6) & 63) | 128));
                byteBuffer.put((byte) (128 | (c & '?')));
            } else {
                if (i + 1 != sequence.length()) {
                    i++;
                    char low = sequence.charAt(i);
                    if (Character.isSurrogatePair(c, low)) {
                        int codePoint = Character.toCodePoint(c, low);
                        byteBuffer.put((byte) ((codePoint >>> 18) | 240));
                        byteBuffer.put((byte) (((codePoint >>> 12) & 63) | 128));
                        byteBuffer.put((byte) (((codePoint >>> 6) & 63) | 128));
                        byteBuffer.put((byte) (128 | (codePoint & 63)));
                    }
                }
                throw new IllegalArgumentException("Unpaired surrogate at index " + (i - 1));
            }
            i++;
        }
    }

    /* JADX WARN: Code restructure failed: missing block: B:12:0x0023, code lost:
        return r12 + r0;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    private static int encode(CharSequence sequence, byte[] bytes, int offset, int length) {
        char c;
        int utf16Length = sequence.length();
        int i = 0;
        int limit = offset + length;
        while (i < utf16Length && i + offset < limit && (c = sequence.charAt(i)) < 128) {
            bytes[offset + i] = (byte) c;
            i++;
        }
        int j = offset + i;
        while (i < utf16Length) {
            char c2 = sequence.charAt(i);
            if (c2 < 128 && j < limit) {
                bytes[j] = (byte) c2;
                j++;
            } else if (c2 < 2048 && j <= limit - 2) {
                int j2 = j + 1;
                bytes[j] = (byte) ((c2 >>> 6) | 960);
                j = j2 + 1;
                bytes[j2] = (byte) ((c2 & '?') | 128);
            } else if ((c2 < 55296 || 57343 < c2) && j <= limit - 3) {
                int j3 = j + 1;
                bytes[j] = (byte) ((c2 >>> '\f') | 480);
                int j4 = j3 + 1;
                bytes[j3] = (byte) (((c2 >>> 6) & 63) | 128);
                bytes[j4] = (byte) ((c2 & '?') | 128);
                j = j4 + 1;
            } else {
                int j5 = limit - 4;
                if (j <= j5) {
                    if (i + 1 != sequence.length()) {
                        i++;
                        char low = sequence.charAt(i);
                        if (Character.isSurrogatePair(c2, low)) {
                            int codePoint = Character.toCodePoint(c2, low);
                            int j6 = j + 1;
                            bytes[j] = (byte) ((codePoint >>> 18) | 240);
                            int j7 = j6 + 1;
                            bytes[j6] = (byte) (((codePoint >>> 12) & 63) | 128);
                            int j8 = j7 + 1;
                            bytes[j7] = (byte) (((codePoint >>> 6) & 63) | 128);
                            j = j8 + 1;
                            bytes[j8] = (byte) ((codePoint & 63) | 128);
                        }
                    }
                    throw new IllegalArgumentException("Unpaired surrogate at index " + (i - 1));
                }
                throw new ArrayIndexOutOfBoundsException("Failed writing " + c2 + " at index " + j);
            }
            i++;
        }
        return j;
    }

    public void writeGroupNoTag(MessageNano value) throws IOException {
        value.writeTo(this);
    }

    public void writeMessageNoTag(MessageNano value) throws IOException {
        writeRawVarint32(value.getCachedSize());
        value.writeTo(this);
    }

    public void writeBytesNoTag(byte[] value) throws IOException {
        writeRawVarint32(value.length);
        writeRawBytes(value);
    }

    public void writeUInt32NoTag(int value) throws IOException {
        writeRawVarint32(value);
    }

    public void writeEnumNoTag(int value) throws IOException {
        writeRawVarint32(value);
    }

    public void writeSFixed32NoTag(int value) throws IOException {
        writeRawLittleEndian32(value);
    }

    public void writeSFixed64NoTag(long value) throws IOException {
        writeRawLittleEndian64(value);
    }

    public void writeSInt32NoTag(int value) throws IOException {
        writeRawVarint32(encodeZigZag32(value));
    }

    public void writeSInt64NoTag(long value) throws IOException {
        writeRawVarint64(encodeZigZag64(value));
    }

    public static int computeDoubleSize(int fieldNumber, double value) {
        return computeTagSize(fieldNumber) + computeDoubleSizeNoTag(value);
    }

    public static int computeFloatSize(int fieldNumber, float value) {
        return computeTagSize(fieldNumber) + computeFloatSizeNoTag(value);
    }

    public static int computeUInt64Size(int fieldNumber, long value) {
        return computeTagSize(fieldNumber) + computeUInt64SizeNoTag(value);
    }

    public static int computeInt64Size(int fieldNumber, long value) {
        return computeTagSize(fieldNumber) + computeInt64SizeNoTag(value);
    }

    public static int computeInt32Size(int fieldNumber, int value) {
        return computeTagSize(fieldNumber) + computeInt32SizeNoTag(value);
    }

    public static int computeFixed64Size(int fieldNumber, long value) {
        return computeTagSize(fieldNumber) + computeFixed64SizeNoTag(value);
    }

    public static int computeFixed32Size(int fieldNumber, int value) {
        return computeTagSize(fieldNumber) + computeFixed32SizeNoTag(value);
    }

    public static int computeBoolSize(int fieldNumber, boolean value) {
        return computeTagSize(fieldNumber) + computeBoolSizeNoTag(value);
    }

    public static int computeStringSize(int fieldNumber, String value) {
        return computeTagSize(fieldNumber) + computeStringSizeNoTag(value);
    }

    public static int computeGroupSize(int fieldNumber, MessageNano value) {
        return (computeTagSize(fieldNumber) * 2) + computeGroupSizeNoTag(value);
    }

    public static int computeMessageSize(int fieldNumber, MessageNano value) {
        return computeTagSize(fieldNumber) + computeMessageSizeNoTag(value);
    }

    public static int computeBytesSize(int fieldNumber, byte[] value) {
        return computeTagSize(fieldNumber) + computeBytesSizeNoTag(value);
    }

    public static int computeUInt32Size(int fieldNumber, int value) {
        return computeTagSize(fieldNumber) + computeUInt32SizeNoTag(value);
    }

    public static int computeEnumSize(int fieldNumber, int value) {
        return computeTagSize(fieldNumber) + computeEnumSizeNoTag(value);
    }

    public static int computeSFixed32Size(int fieldNumber, int value) {
        return computeTagSize(fieldNumber) + computeSFixed32SizeNoTag(value);
    }

    public static int computeSFixed64Size(int fieldNumber, long value) {
        return computeTagSize(fieldNumber) + computeSFixed64SizeNoTag(value);
    }

    public static int computeSInt32Size(int fieldNumber, int value) {
        return computeTagSize(fieldNumber) + computeSInt32SizeNoTag(value);
    }

    public static int computeSInt64Size(int fieldNumber, long value) {
        return computeTagSize(fieldNumber) + computeSInt64SizeNoTag(value);
    }

    public static int computeDoubleSizeNoTag(double value) {
        return 8;
    }

    public static int computeFloatSizeNoTag(float value) {
        return 4;
    }

    public static int computeUInt64SizeNoTag(long value) {
        return computeRawVarint64Size(value);
    }

    public static int computeInt64SizeNoTag(long value) {
        return computeRawVarint64Size(value);
    }

    public static int computeInt32SizeNoTag(int value) {
        if (value >= 0) {
            return computeRawVarint32Size(value);
        }
        return 10;
    }

    public static int computeFixed64SizeNoTag(long value) {
        return 8;
    }

    public static int computeFixed32SizeNoTag(int value) {
        return 4;
    }

    public static int computeBoolSizeNoTag(boolean value) {
        return 1;
    }

    public static int computeStringSizeNoTag(String value) {
        int length = encodedLength(value);
        return computeRawVarint32Size(length) + length;
    }

    public static int computeGroupSizeNoTag(MessageNano value) {
        return value.getSerializedSize();
    }

    public static int computeMessageSizeNoTag(MessageNano value) {
        int size = value.getSerializedSize();
        return computeRawVarint32Size(size) + size;
    }

    public static int computeBytesSizeNoTag(byte[] value) {
        return computeRawVarint32Size(value.length) + value.length;
    }

    public static int computeUInt32SizeNoTag(int value) {
        return computeRawVarint32Size(value);
    }

    public static int computeEnumSizeNoTag(int value) {
        return computeRawVarint32Size(value);
    }

    public static int computeSFixed32SizeNoTag(int value) {
        return 4;
    }

    public static int computeSFixed64SizeNoTag(long value) {
        return 8;
    }

    public static int computeSInt32SizeNoTag(int value) {
        return computeRawVarint32Size(encodeZigZag32(value));
    }

    public static int computeSInt64SizeNoTag(long value) {
        return computeRawVarint64Size(encodeZigZag64(value));
    }

    public int spaceLeft() {
        return this.buffer.remaining();
    }

    public void checkNoSpaceLeft() {
        if (spaceLeft() != 0) {
            throw new IllegalStateException("Did not write as much data as expected.");
        }
    }

    public int position() {
        return this.buffer.position();
    }

    public void reset() {
        this.buffer.clear();
    }

    /* loaded from: classes4.dex */
    public static class OutOfSpaceException extends IOException {
        private static final long serialVersionUID = -6947486886997889499L;

        OutOfSpaceException(int position, int limit) {
            super("CodedOutputStream was writing to a flat byte array and ran out of space (pos " + position + " limit " + limit + ").");
        }
    }

    public void writeRawByte(byte value) throws IOException {
        if (!this.buffer.hasRemaining()) {
            throw new OutOfSpaceException(this.buffer.position(), this.buffer.limit());
        }
        this.buffer.put(value);
    }

    public void writeRawByte(int value) throws IOException {
        writeRawByte((byte) value);
    }

    public void writeRawBytes(byte[] value) throws IOException {
        writeRawBytes(value, 0, value.length);
    }

    public void writeRawBytes(byte[] value, int offset, int length) throws IOException {
        if (this.buffer.remaining() >= length) {
            this.buffer.put(value, offset, length);
            return;
        }
        throw new OutOfSpaceException(this.buffer.position(), this.buffer.limit());
    }

    public void writeTag(int fieldNumber, int wireType) throws IOException {
        writeRawVarint32(WireFormatNano.makeTag(fieldNumber, wireType));
    }

    public static int computeTagSize(int fieldNumber) {
        return computeRawVarint32Size(WireFormatNano.makeTag(fieldNumber, 0));
    }

    public void writeRawVarint32(int value) throws IOException {
        while ((value & (-128)) != 0) {
            writeRawByte((value & 127) | 128);
            value >>>= 7;
        }
        writeRawByte(value);
    }

    public static int computeRawVarint32Size(int value) {
        if ((value & (-128)) == 0) {
            return 1;
        }
        if ((value & (-16384)) == 0) {
            return 2;
        }
        if (((-2097152) & value) == 0) {
            return 3;
        }
        return ((-268435456) & value) == 0 ? 4 : 5;
    }

    public void writeRawVarint64(long value) throws IOException {
        while (((-128) & value) != 0) {
            writeRawByte((((int) value) & 127) | 128);
            value >>>= 7;
        }
        writeRawByte((int) value);
    }

    public static int computeRawVarint64Size(long value) {
        if (((-128) & value) == 0) {
            return 1;
        }
        if (((-16384) & value) == 0) {
            return 2;
        }
        if (((-2097152) & value) == 0) {
            return 3;
        }
        if ((BufferUsage.VENDOR_MASK & value) == 0) {
            return 4;
        }
        if (((-34359738368L) & value) == 0) {
            return 5;
        }
        if (((-4398046511104L) & value) == 0) {
            return 6;
        }
        if (((-562949953421312L) & value) == 0) {
            return 7;
        }
        if ((BatteryStats.STEP_LEVEL_MODIFIED_MODE_MASK & value) == 0) {
            return 8;
        }
        return (Long.MIN_VALUE & value) == 0 ? 9 : 10;
    }

    public void writeRawLittleEndian32(int value) throws IOException {
        if (this.buffer.remaining() < 4) {
            throw new OutOfSpaceException(this.buffer.position(), this.buffer.limit());
        }
        this.buffer.putInt(value);
    }

    public void writeRawLittleEndian64(long value) throws IOException {
        if (this.buffer.remaining() < 8) {
            throw new OutOfSpaceException(this.buffer.position(), this.buffer.limit());
        }
        this.buffer.putLong(value);
    }

    public static int encodeZigZag32(int n) {
        return (n << 1) ^ (n >> 31);
    }

    public static long encodeZigZag64(long n) {
        return (n << 1) ^ (n >> 63);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static int computeFieldSize(int number, int type, Object object) {
        switch (type) {
            case 1:
                return computeDoubleSize(number, ((Double) object).doubleValue());
            case 2:
                return computeFloatSize(number, ((Float) object).floatValue());
            case 3:
                return computeInt64Size(number, ((Long) object).longValue());
            case 4:
                return computeUInt64Size(number, ((Long) object).longValue());
            case 5:
                return computeInt32Size(number, ((Integer) object).intValue());
            case 6:
                return computeFixed64Size(number, ((Long) object).longValue());
            case 7:
                return computeFixed32Size(number, ((Integer) object).intValue());
            case 8:
                return computeBoolSize(number, ((Boolean) object).booleanValue());
            case 9:
                return computeStringSize(number, (String) object);
            case 10:
                return computeGroupSize(number, (MessageNano) object);
            case 11:
                return computeMessageSize(number, (MessageNano) object);
            case 12:
                return computeBytesSize(number, (byte[]) object);
            case 13:
                return computeUInt32Size(number, ((Integer) object).intValue());
            case 14:
                return computeEnumSize(number, ((Integer) object).intValue());
            case 15:
                return computeSFixed32Size(number, ((Integer) object).intValue());
            case 16:
                return computeSFixed64Size(number, ((Long) object).longValue());
            case 17:
                return computeSInt32Size(number, ((Integer) object).intValue());
            case 18:
                return computeSInt64Size(number, ((Long) object).longValue());
            default:
                throw new IllegalArgumentException("Unknown type: " + type);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void writeField(int number, int type, Object value) throws IOException {
        switch (type) {
            case 1:
                Double doubleValue = (Double) value;
                writeDouble(number, doubleValue.doubleValue());
                return;
            case 2:
                Float floatValue = (Float) value;
                writeFloat(number, floatValue.floatValue());
                return;
            case 3:
                Long int64Value = (Long) value;
                writeInt64(number, int64Value.longValue());
                return;
            case 4:
                Long uint64Value = (Long) value;
                writeUInt64(number, uint64Value.longValue());
                return;
            case 5:
                Integer int32Value = (Integer) value;
                writeInt32(number, int32Value.intValue());
                return;
            case 6:
                Long fixed64Value = (Long) value;
                writeFixed64(number, fixed64Value.longValue());
                return;
            case 7:
                Integer fixed32Value = (Integer) value;
                writeFixed32(number, fixed32Value.intValue());
                return;
            case 8:
                Boolean boolValue = (Boolean) value;
                writeBool(number, boolValue.booleanValue());
                return;
            case 9:
                String stringValue = (String) value;
                writeString(number, stringValue);
                return;
            case 10:
                MessageNano groupValue = (MessageNano) value;
                writeGroup(number, groupValue);
                return;
            case 11:
                MessageNano messageValue = (MessageNano) value;
                writeMessage(number, messageValue);
                return;
            case 12:
                byte[] bytesValue = (byte[]) value;
                writeBytes(number, bytesValue);
                return;
            case 13:
                Integer uint32Value = (Integer) value;
                writeUInt32(number, uint32Value.intValue());
                return;
            case 14:
                Integer enumValue = (Integer) value;
                writeEnum(number, enumValue.intValue());
                return;
            case 15:
                Integer sfixed32Value = (Integer) value;
                writeSFixed32(number, sfixed32Value.intValue());
                return;
            case 16:
                Long sfixed64Value = (Long) value;
                writeSFixed64(number, sfixed64Value.longValue());
                return;
            case 17:
                Integer sint32Value = (Integer) value;
                writeSInt32(number, sint32Value.intValue());
                return;
            case 18:
                Long sint64Value = (Long) value;
                writeSInt64(number, sint64Value.longValue());
                return;
            default:
                throw new IOException("Unknown type: " + type);
        }
    }
}
