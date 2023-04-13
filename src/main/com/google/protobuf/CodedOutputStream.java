package com.google.protobuf;

import com.google.protobuf.Utf8;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.logging.Level;
import java.util.logging.Logger;
/* loaded from: classes.dex */
public abstract class CodedOutputStream extends ByteOutput {
    public static final int DEFAULT_BUFFER_SIZE = 4096;
    @Deprecated
    public static final int LITTLE_ENDIAN_32_SIZE = 4;
    private boolean serializationDeterministic;
    CodedOutputStreamWriter wrapper;
    private static final Logger logger = Logger.getLogger(CodedOutputStream.class.getName());
    private static final boolean HAS_UNSAFE_ARRAY_OPERATIONS = UnsafeUtil.hasUnsafeArrayOperations();

    public abstract void flush() throws IOException;

    public abstract int getTotalBytesWritten();

    public abstract int spaceLeft();

    @Override // com.google.protobuf.ByteOutput
    public abstract void write(byte b) throws IOException;

    @Override // com.google.protobuf.ByteOutput
    public abstract void write(ByteBuffer byteBuffer) throws IOException;

    @Override // com.google.protobuf.ByteOutput
    public abstract void write(byte[] bArr, int i, int i2) throws IOException;

    public abstract void writeBool(int i, boolean z) throws IOException;

    public abstract void writeByteArray(int i, byte[] bArr) throws IOException;

    public abstract void writeByteArray(int i, byte[] bArr, int i2, int i3) throws IOException;

    abstract void writeByteArrayNoTag(byte[] bArr, int i, int i2) throws IOException;

    public abstract void writeByteBuffer(int i, ByteBuffer byteBuffer) throws IOException;

    public abstract void writeBytes(int i, ByteString byteString) throws IOException;

    public abstract void writeBytesNoTag(ByteString byteString) throws IOException;

    public abstract void writeFixed32(int i, int i2) throws IOException;

    public abstract void writeFixed32NoTag(int i) throws IOException;

    public abstract void writeFixed64(int i, long j) throws IOException;

    public abstract void writeFixed64NoTag(long j) throws IOException;

    public abstract void writeInt32(int i, int i2) throws IOException;

    public abstract void writeInt32NoTag(int i) throws IOException;

    @Override // com.google.protobuf.ByteOutput
    public abstract void writeLazy(ByteBuffer byteBuffer) throws IOException;

    @Override // com.google.protobuf.ByteOutput
    public abstract void writeLazy(byte[] bArr, int i, int i2) throws IOException;

    public abstract void writeMessage(int i, MessageLite messageLite) throws IOException;

    /* JADX INFO: Access modifiers changed from: package-private */
    public abstract void writeMessage(int i, MessageLite messageLite, Schema schema) throws IOException;

    public abstract void writeMessageNoTag(MessageLite messageLite) throws IOException;

    abstract void writeMessageNoTag(MessageLite messageLite, Schema schema) throws IOException;

    public abstract void writeMessageSetExtension(int i, MessageLite messageLite) throws IOException;

    public abstract void writeRawBytes(ByteBuffer byteBuffer) throws IOException;

    public abstract void writeRawMessageSetExtension(int i, ByteString byteString) throws IOException;

    public abstract void writeString(int i, String str) throws IOException;

    public abstract void writeStringNoTag(String str) throws IOException;

    public abstract void writeTag(int i, int i2) throws IOException;

    public abstract void writeUInt32(int i, int i2) throws IOException;

    public abstract void writeUInt32NoTag(int i) throws IOException;

    public abstract void writeUInt64(int i, long j) throws IOException;

    public abstract void writeUInt64NoTag(long j) throws IOException;

    /* JADX INFO: Access modifiers changed from: package-private */
    public static int computePreferredBufferSize(int dataLength) {
        if (dataLength > 4096) {
            return 4096;
        }
        return dataLength;
    }

    public static CodedOutputStream newInstance(OutputStream output) {
        return newInstance(output, 4096);
    }

    public static CodedOutputStream newInstance(OutputStream output, int bufferSize) {
        return new OutputStreamEncoder(output, bufferSize);
    }

    public static CodedOutputStream newInstance(byte[] flatArray) {
        return newInstance(flatArray, 0, flatArray.length);
    }

    public static CodedOutputStream newInstance(byte[] flatArray, int offset, int length) {
        return new ArrayEncoder(flatArray, offset, length);
    }

    public static CodedOutputStream newInstance(ByteBuffer buffer) {
        if (buffer.hasArray()) {
            return new HeapNioEncoder(buffer);
        }
        if (buffer.isDirect() && !buffer.isReadOnly()) {
            if (UnsafeDirectNioEncoder.isSupported()) {
                return newUnsafeInstance(buffer);
            }
            return newSafeInstance(buffer);
        }
        throw new IllegalArgumentException("ByteBuffer is read-only");
    }

    static CodedOutputStream newUnsafeInstance(ByteBuffer buffer) {
        return new UnsafeDirectNioEncoder(buffer);
    }

    static CodedOutputStream newSafeInstance(ByteBuffer buffer) {
        return new SafeDirectNioEncoder(buffer);
    }

    public void useDeterministicSerialization() {
        this.serializationDeterministic = true;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean isSerializationDeterministic() {
        return this.serializationDeterministic;
    }

    @Deprecated
    public static CodedOutputStream newInstance(ByteBuffer byteBuffer, int unused) {
        return newInstance(byteBuffer);
    }

    static CodedOutputStream newInstance(ByteOutput byteOutput, int bufferSize) {
        if (bufferSize < 0) {
            throw new IllegalArgumentException("bufferSize must be positive");
        }
        return new ByteOutputEncoder(byteOutput, bufferSize);
    }

    private CodedOutputStream() {
    }

    public final void writeSInt32(int fieldNumber, int value) throws IOException {
        writeUInt32(fieldNumber, encodeZigZag32(value));
    }

    public final void writeSFixed32(int fieldNumber, int value) throws IOException {
        writeFixed32(fieldNumber, value);
    }

    public final void writeInt64(int fieldNumber, long value) throws IOException {
        writeUInt64(fieldNumber, value);
    }

    public final void writeSInt64(int fieldNumber, long value) throws IOException {
        writeUInt64(fieldNumber, encodeZigZag64(value));
    }

    public final void writeSFixed64(int fieldNumber, long value) throws IOException {
        writeFixed64(fieldNumber, value);
    }

    public final void writeFloat(int fieldNumber, float value) throws IOException {
        writeFixed32(fieldNumber, Float.floatToRawIntBits(value));
    }

    public final void writeDouble(int fieldNumber, double value) throws IOException {
        writeFixed64(fieldNumber, Double.doubleToRawLongBits(value));
    }

    public final void writeEnum(int fieldNumber, int value) throws IOException {
        writeInt32(fieldNumber, value);
    }

    public final void writeRawByte(byte value) throws IOException {
        write(value);
    }

    public final void writeRawByte(int value) throws IOException {
        write((byte) value);
    }

    public final void writeRawBytes(byte[] value) throws IOException {
        write(value, 0, value.length);
    }

    public final void writeRawBytes(byte[] value, int offset, int length) throws IOException {
        write(value, offset, length);
    }

    public final void writeRawBytes(ByteString value) throws IOException {
        value.writeTo(this);
    }

    public final void writeSInt32NoTag(int value) throws IOException {
        writeUInt32NoTag(encodeZigZag32(value));
    }

    public final void writeSFixed32NoTag(int value) throws IOException {
        writeFixed32NoTag(value);
    }

    public final void writeInt64NoTag(long value) throws IOException {
        writeUInt64NoTag(value);
    }

    public final void writeSInt64NoTag(long value) throws IOException {
        writeUInt64NoTag(encodeZigZag64(value));
    }

    public final void writeSFixed64NoTag(long value) throws IOException {
        writeFixed64NoTag(value);
    }

    public final void writeFloatNoTag(float value) throws IOException {
        writeFixed32NoTag(Float.floatToRawIntBits(value));
    }

    public final void writeDoubleNoTag(double value) throws IOException {
        writeFixed64NoTag(Double.doubleToRawLongBits(value));
    }

    public final void writeBoolNoTag(boolean value) throws IOException {
        write(value ? (byte) 1 : (byte) 0);
    }

    public final void writeEnumNoTag(int value) throws IOException {
        writeInt32NoTag(value);
    }

    public final void writeByteArrayNoTag(byte[] value) throws IOException {
        writeByteArrayNoTag(value, 0, value.length);
    }

    public static int computeInt32Size(int fieldNumber, int value) {
        return computeTagSize(fieldNumber) + computeInt32SizeNoTag(value);
    }

    public static int computeUInt32Size(int fieldNumber, int value) {
        return computeTagSize(fieldNumber) + computeUInt32SizeNoTag(value);
    }

    public static int computeSInt32Size(int fieldNumber, int value) {
        return computeTagSize(fieldNumber) + computeSInt32SizeNoTag(value);
    }

    public static int computeFixed32Size(int fieldNumber, int value) {
        return computeTagSize(fieldNumber) + computeFixed32SizeNoTag(value);
    }

    public static int computeSFixed32Size(int fieldNumber, int value) {
        return computeTagSize(fieldNumber) + computeSFixed32SizeNoTag(value);
    }

    public static int computeInt64Size(int fieldNumber, long value) {
        return computeTagSize(fieldNumber) + computeInt64SizeNoTag(value);
    }

    public static int computeUInt64Size(int fieldNumber, long value) {
        return computeTagSize(fieldNumber) + computeUInt64SizeNoTag(value);
    }

    public static int computeSInt64Size(int fieldNumber, long value) {
        return computeTagSize(fieldNumber) + computeSInt64SizeNoTag(value);
    }

    public static int computeFixed64Size(int fieldNumber, long value) {
        return computeTagSize(fieldNumber) + computeFixed64SizeNoTag(value);
    }

    public static int computeSFixed64Size(int fieldNumber, long value) {
        return computeTagSize(fieldNumber) + computeSFixed64SizeNoTag(value);
    }

    public static int computeFloatSize(int fieldNumber, float value) {
        return computeTagSize(fieldNumber) + computeFloatSizeNoTag(value);
    }

    public static int computeDoubleSize(int fieldNumber, double value) {
        return computeTagSize(fieldNumber) + computeDoubleSizeNoTag(value);
    }

    public static int computeBoolSize(int fieldNumber, boolean value) {
        return computeTagSize(fieldNumber) + computeBoolSizeNoTag(value);
    }

    public static int computeEnumSize(int fieldNumber, int value) {
        return computeTagSize(fieldNumber) + computeEnumSizeNoTag(value);
    }

    public static int computeStringSize(int fieldNumber, String value) {
        return computeTagSize(fieldNumber) + computeStringSizeNoTag(value);
    }

    public static int computeBytesSize(int fieldNumber, ByteString value) {
        return computeTagSize(fieldNumber) + computeBytesSizeNoTag(value);
    }

    public static int computeByteArraySize(int fieldNumber, byte[] value) {
        return computeTagSize(fieldNumber) + computeByteArraySizeNoTag(value);
    }

    public static int computeByteBufferSize(int fieldNumber, ByteBuffer value) {
        return computeTagSize(fieldNumber) + computeByteBufferSizeNoTag(value);
    }

    public static int computeLazyFieldSize(int fieldNumber, LazyFieldLite value) {
        return computeTagSize(fieldNumber) + computeLazyFieldSizeNoTag(value);
    }

    public static int computeMessageSize(int fieldNumber, MessageLite value) {
        return computeTagSize(fieldNumber) + computeMessageSizeNoTag(value);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static int computeMessageSize(int fieldNumber, MessageLite value, Schema schema) {
        return computeTagSize(fieldNumber) + computeMessageSizeNoTag(value, schema);
    }

    public static int computeMessageSetExtensionSize(int fieldNumber, MessageLite value) {
        return (computeTagSize(1) * 2) + computeUInt32Size(2, fieldNumber) + computeMessageSize(3, value);
    }

    public static int computeRawMessageSetExtensionSize(int fieldNumber, ByteString value) {
        return (computeTagSize(1) * 2) + computeUInt32Size(2, fieldNumber) + computeBytesSize(3, value);
    }

    public static int computeLazyFieldMessageSetExtensionSize(int fieldNumber, LazyFieldLite value) {
        return (computeTagSize(1) * 2) + computeUInt32Size(2, fieldNumber) + computeLazyFieldSize(3, value);
    }

    public static int computeTagSize(int fieldNumber) {
        return computeUInt32SizeNoTag(WireFormat.makeTag(fieldNumber, 0));
    }

    public static int computeInt32SizeNoTag(int value) {
        if (value >= 0) {
            return computeUInt32SizeNoTag(value);
        }
        return 10;
    }

    public static int computeUInt32SizeNoTag(int value) {
        if ((value & (-128)) == 0) {
            return 1;
        }
        if ((value & (-16384)) == 0) {
            return 2;
        }
        if (((-2097152) & value) == 0) {
            return 3;
        }
        if (((-268435456) & value) == 0) {
            return 4;
        }
        return 5;
    }

    public static int computeSInt32SizeNoTag(int value) {
        return computeUInt32SizeNoTag(encodeZigZag32(value));
    }

    public static int computeFixed32SizeNoTag(int unused) {
        return 4;
    }

    public static int computeSFixed32SizeNoTag(int unused) {
        return 4;
    }

    public static int computeInt64SizeNoTag(long value) {
        return computeUInt64SizeNoTag(value);
    }

    public static int computeUInt64SizeNoTag(long value) {
        if (((-128) & value) == 0) {
            return 1;
        }
        if (value < 0) {
            return 10;
        }
        int n = 2;
        if (((-34359738368L) & value) != 0) {
            n = 2 + 4;
            value >>>= 28;
        }
        if (((-2097152) & value) != 0) {
            n += 2;
            value >>>= 14;
        }
        if (((-16384) & value) != 0) {
            return n + 1;
        }
        return n;
    }

    public static int computeSInt64SizeNoTag(long value) {
        return computeUInt64SizeNoTag(encodeZigZag64(value));
    }

    public static int computeFixed64SizeNoTag(long unused) {
        return 8;
    }

    public static int computeSFixed64SizeNoTag(long unused) {
        return 8;
    }

    public static int computeFloatSizeNoTag(float unused) {
        return 4;
    }

    public static int computeDoubleSizeNoTag(double unused) {
        return 8;
    }

    public static int computeBoolSizeNoTag(boolean unused) {
        return 1;
    }

    public static int computeEnumSizeNoTag(int value) {
        return computeInt32SizeNoTag(value);
    }

    public static int computeStringSizeNoTag(String value) {
        int length;
        try {
            length = Utf8.encodedLength(value);
        } catch (Utf8.UnpairedSurrogateException e) {
            byte[] bytes = value.getBytes(Internal.UTF_8);
            length = bytes.length;
        }
        return computeLengthDelimitedFieldSize(length);
    }

    public static int computeLazyFieldSizeNoTag(LazyFieldLite value) {
        return computeLengthDelimitedFieldSize(value.getSerializedSize());
    }

    public static int computeBytesSizeNoTag(ByteString value) {
        return computeLengthDelimitedFieldSize(value.size());
    }

    public static int computeByteArraySizeNoTag(byte[] value) {
        return computeLengthDelimitedFieldSize(value.length);
    }

    public static int computeByteBufferSizeNoTag(ByteBuffer value) {
        return computeLengthDelimitedFieldSize(value.capacity());
    }

    public static int computeMessageSizeNoTag(MessageLite value) {
        return computeLengthDelimitedFieldSize(value.getSerializedSize());
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static int computeMessageSizeNoTag(MessageLite value, Schema schema) {
        return computeLengthDelimitedFieldSize(((AbstractMessageLite) value).getSerializedSize(schema));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static int computeLengthDelimitedFieldSize(int fieldLength) {
        return computeUInt32SizeNoTag(fieldLength) + fieldLength;
    }

    public static int encodeZigZag32(int n) {
        return (n << 1) ^ (n >> 31);
    }

    public static long encodeZigZag64(long n) {
        return (n << 1) ^ (n >> 63);
    }

    public final void checkNoSpaceLeft() {
        if (spaceLeft() != 0) {
            throw new IllegalStateException("Did not write as much data as expected.");
        }
    }

    /* loaded from: classes.dex */
    public static class OutOfSpaceException extends IOException {
        private static final String MESSAGE = "CodedOutputStream was writing to a flat byte array and ran out of space.";
        private static final long serialVersionUID = -6947486886997889499L;

        OutOfSpaceException() {
            super(MESSAGE);
        }

        OutOfSpaceException(String explanationMessage) {
            super("CodedOutputStream was writing to a flat byte array and ran out of space.: " + explanationMessage);
        }

        OutOfSpaceException(Throwable cause) {
            super(MESSAGE, cause);
        }

        OutOfSpaceException(String explanationMessage, Throwable cause) {
            super("CodedOutputStream was writing to a flat byte array and ran out of space.: " + explanationMessage, cause);
        }
    }

    final void inefficientWriteStringNoTag(String value, Utf8.UnpairedSurrogateException cause) throws IOException {
        logger.log(Level.WARNING, "Converting ill-formed UTF-16. Your Protocol Buffer will not round trip correctly!", (Throwable) cause);
        byte[] bytes = value.getBytes(Internal.UTF_8);
        try {
            writeUInt32NoTag(bytes.length);
            writeLazy(bytes, 0, bytes.length);
        } catch (IndexOutOfBoundsException e) {
            throw new OutOfSpaceException(e);
        }
    }

    @Deprecated
    public final void writeGroup(int fieldNumber, MessageLite value) throws IOException {
        writeTag(fieldNumber, 3);
        writeGroupNoTag(value);
        writeTag(fieldNumber, 4);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Deprecated
    public final void writeGroup(int fieldNumber, MessageLite value, Schema schema) throws IOException {
        writeTag(fieldNumber, 3);
        writeGroupNoTag(value, schema);
        writeTag(fieldNumber, 4);
    }

    @Deprecated
    public final void writeGroupNoTag(MessageLite value) throws IOException {
        value.writeTo(this);
    }

    @Deprecated
    final void writeGroupNoTag(MessageLite value, Schema schema) throws IOException {
        schema.writeTo(value, this.wrapper);
    }

    @Deprecated
    public static int computeGroupSize(int fieldNumber, MessageLite value) {
        return (computeTagSize(fieldNumber) * 2) + value.getSerializedSize();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Deprecated
    public static int computeGroupSize(int fieldNumber, MessageLite value, Schema schema) {
        return (computeTagSize(fieldNumber) * 2) + computeGroupSizeNoTag(value, schema);
    }

    @Deprecated
    public static int computeGroupSizeNoTag(MessageLite value) {
        return value.getSerializedSize();
    }

    @Deprecated
    static int computeGroupSizeNoTag(MessageLite value, Schema schema) {
        return ((AbstractMessageLite) value).getSerializedSize(schema);
    }

    @Deprecated
    public final void writeRawVarint32(int value) throws IOException {
        writeUInt32NoTag(value);
    }

    @Deprecated
    public final void writeRawVarint64(long value) throws IOException {
        writeUInt64NoTag(value);
    }

    @Deprecated
    public static int computeRawVarint32Size(int value) {
        return computeUInt32SizeNoTag(value);
    }

    @Deprecated
    public static int computeRawVarint64Size(long value) {
        return computeUInt64SizeNoTag(value);
    }

    @Deprecated
    public final void writeRawLittleEndian32(int value) throws IOException {
        writeFixed32NoTag(value);
    }

    @Deprecated
    public final void writeRawLittleEndian64(long value) throws IOException {
        writeFixed64NoTag(value);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class ArrayEncoder extends CodedOutputStream {
        private final byte[] buffer;
        private final int limit;
        private final int offset;
        private int position;

        ArrayEncoder(byte[] buffer, int offset, int length) {
            super();
            if (buffer == null) {
                throw new NullPointerException("buffer");
            }
            if ((offset | length | (buffer.length - (offset + length))) < 0) {
                throw new IllegalArgumentException(String.format("Array range is invalid. Buffer.length=%d, offset=%d, length=%d", Integer.valueOf(buffer.length), Integer.valueOf(offset), Integer.valueOf(length)));
            }
            this.buffer = buffer;
            this.offset = offset;
            this.position = offset;
            this.limit = offset + length;
        }

        @Override // com.google.protobuf.CodedOutputStream
        public final void writeTag(int fieldNumber, int wireType) throws IOException {
            writeUInt32NoTag(WireFormat.makeTag(fieldNumber, wireType));
        }

        @Override // com.google.protobuf.CodedOutputStream
        public final void writeInt32(int fieldNumber, int value) throws IOException {
            writeTag(fieldNumber, 0);
            writeInt32NoTag(value);
        }

        @Override // com.google.protobuf.CodedOutputStream
        public final void writeUInt32(int fieldNumber, int value) throws IOException {
            writeTag(fieldNumber, 0);
            writeUInt32NoTag(value);
        }

        @Override // com.google.protobuf.CodedOutputStream
        public final void writeFixed32(int fieldNumber, int value) throws IOException {
            writeTag(fieldNumber, 5);
            writeFixed32NoTag(value);
        }

        @Override // com.google.protobuf.CodedOutputStream
        public final void writeUInt64(int fieldNumber, long value) throws IOException {
            writeTag(fieldNumber, 0);
            writeUInt64NoTag(value);
        }

        @Override // com.google.protobuf.CodedOutputStream
        public final void writeFixed64(int fieldNumber, long value) throws IOException {
            writeTag(fieldNumber, 1);
            writeFixed64NoTag(value);
        }

        @Override // com.google.protobuf.CodedOutputStream
        public final void writeBool(int fieldNumber, boolean value) throws IOException {
            writeTag(fieldNumber, 0);
            write(value ? (byte) 1 : (byte) 0);
        }

        @Override // com.google.protobuf.CodedOutputStream
        public final void writeString(int fieldNumber, String value) throws IOException {
            writeTag(fieldNumber, 2);
            writeStringNoTag(value);
        }

        @Override // com.google.protobuf.CodedOutputStream
        public final void writeBytes(int fieldNumber, ByteString value) throws IOException {
            writeTag(fieldNumber, 2);
            writeBytesNoTag(value);
        }

        @Override // com.google.protobuf.CodedOutputStream
        public final void writeByteArray(int fieldNumber, byte[] value) throws IOException {
            writeByteArray(fieldNumber, value, 0, value.length);
        }

        @Override // com.google.protobuf.CodedOutputStream
        public final void writeByteArray(int fieldNumber, byte[] value, int offset, int length) throws IOException {
            writeTag(fieldNumber, 2);
            writeByteArrayNoTag(value, offset, length);
        }

        @Override // com.google.protobuf.CodedOutputStream
        public final void writeByteBuffer(int fieldNumber, ByteBuffer value) throws IOException {
            writeTag(fieldNumber, 2);
            writeUInt32NoTag(value.capacity());
            writeRawBytes(value);
        }

        @Override // com.google.protobuf.CodedOutputStream
        public final void writeBytesNoTag(ByteString value) throws IOException {
            writeUInt32NoTag(value.size());
            value.writeTo(this);
        }

        @Override // com.google.protobuf.CodedOutputStream
        public final void writeByteArrayNoTag(byte[] value, int offset, int length) throws IOException {
            writeUInt32NoTag(length);
            write(value, offset, length);
        }

        @Override // com.google.protobuf.CodedOutputStream
        public final void writeRawBytes(ByteBuffer value) throws IOException {
            if (value.hasArray()) {
                write(value.array(), value.arrayOffset(), value.capacity());
                return;
            }
            ByteBuffer duplicated = value.duplicate();
            duplicated.clear();
            write(duplicated);
        }

        @Override // com.google.protobuf.CodedOutputStream
        public final void writeMessage(int fieldNumber, MessageLite value) throws IOException {
            writeTag(fieldNumber, 2);
            writeMessageNoTag(value);
        }

        @Override // com.google.protobuf.CodedOutputStream
        final void writeMessage(int fieldNumber, MessageLite value, Schema schema) throws IOException {
            writeTag(fieldNumber, 2);
            writeUInt32NoTag(((AbstractMessageLite) value).getSerializedSize(schema));
            schema.writeTo(value, this.wrapper);
        }

        @Override // com.google.protobuf.CodedOutputStream
        public final void writeMessageSetExtension(int fieldNumber, MessageLite value) throws IOException {
            writeTag(1, 3);
            writeUInt32(2, fieldNumber);
            writeMessage(3, value);
            writeTag(1, 4);
        }

        @Override // com.google.protobuf.CodedOutputStream
        public final void writeRawMessageSetExtension(int fieldNumber, ByteString value) throws IOException {
            writeTag(1, 3);
            writeUInt32(2, fieldNumber);
            writeBytes(3, value);
            writeTag(1, 4);
        }

        @Override // com.google.protobuf.CodedOutputStream
        public final void writeMessageNoTag(MessageLite value) throws IOException {
            writeUInt32NoTag(value.getSerializedSize());
            value.writeTo(this);
        }

        @Override // com.google.protobuf.CodedOutputStream
        final void writeMessageNoTag(MessageLite value, Schema schema) throws IOException {
            writeUInt32NoTag(((AbstractMessageLite) value).getSerializedSize(schema));
            schema.writeTo(value, this.wrapper);
        }

        @Override // com.google.protobuf.CodedOutputStream, com.google.protobuf.ByteOutput
        public final void write(byte value) throws IOException {
            try {
                byte[] bArr = this.buffer;
                int i = this.position;
                this.position = i + 1;
                bArr[i] = value;
            } catch (IndexOutOfBoundsException e) {
                throw new OutOfSpaceException(String.format("Pos: %d, limit: %d, len: %d", Integer.valueOf(this.position), Integer.valueOf(this.limit), 1), e);
            }
        }

        @Override // com.google.protobuf.CodedOutputStream
        public final void writeInt32NoTag(int value) throws IOException {
            if (value >= 0) {
                writeUInt32NoTag(value);
            } else {
                writeUInt64NoTag(value);
            }
        }

        @Override // com.google.protobuf.CodedOutputStream
        public final void writeUInt32NoTag(int value) throws IOException {
            while ((value & (-128)) != 0) {
                try {
                    byte[] bArr = this.buffer;
                    int i = this.position;
                    this.position = i + 1;
                    bArr[i] = (byte) ((value & 127) | 128);
                    value >>>= 7;
                } catch (IndexOutOfBoundsException e) {
                    throw new OutOfSpaceException(String.format("Pos: %d, limit: %d, len: %d", Integer.valueOf(this.position), Integer.valueOf(this.limit), 1), e);
                }
            }
            byte[] bArr2 = this.buffer;
            int i2 = this.position;
            this.position = i2 + 1;
            bArr2[i2] = (byte) value;
        }

        @Override // com.google.protobuf.CodedOutputStream
        public final void writeFixed32NoTag(int value) throws IOException {
            try {
                byte[] bArr = this.buffer;
                int i = this.position;
                int i2 = i + 1;
                this.position = i2;
                bArr[i] = (byte) (value & 255);
                int i3 = i2 + 1;
                this.position = i3;
                bArr[i2] = (byte) ((value >> 8) & 255);
                int i4 = i3 + 1;
                this.position = i4;
                bArr[i3] = (byte) ((value >> 16) & 255);
                this.position = i4 + 1;
                bArr[i4] = (byte) ((value >> 24) & 255);
            } catch (IndexOutOfBoundsException e) {
                throw new OutOfSpaceException(String.format("Pos: %d, limit: %d, len: %d", Integer.valueOf(this.position), Integer.valueOf(this.limit), 1), e);
            }
        }

        @Override // com.google.protobuf.CodedOutputStream
        public final void writeUInt64NoTag(long value) throws IOException {
            if (CodedOutputStream.HAS_UNSAFE_ARRAY_OPERATIONS && spaceLeft() >= 10) {
                while ((value & (-128)) != 0) {
                    byte[] bArr = this.buffer;
                    int i = this.position;
                    this.position = i + 1;
                    UnsafeUtil.putByte(bArr, i, (byte) ((((int) value) & 127) | 128));
                    value >>>= 7;
                }
                byte[] bArr2 = this.buffer;
                int i2 = this.position;
                this.position = i2 + 1;
                UnsafeUtil.putByte(bArr2, i2, (byte) value);
                return;
            }
            while ((value & (-128)) != 0) {
                try {
                    byte[] bArr3 = this.buffer;
                    int i3 = this.position;
                    this.position = i3 + 1;
                    bArr3[i3] = (byte) ((((int) value) & 127) | 128);
                    value >>>= 7;
                } catch (IndexOutOfBoundsException e) {
                    throw new OutOfSpaceException(String.format("Pos: %d, limit: %d, len: %d", Integer.valueOf(this.position), Integer.valueOf(this.limit), 1), e);
                }
            }
            byte[] bArr4 = this.buffer;
            int i4 = this.position;
            this.position = i4 + 1;
            bArr4[i4] = (byte) value;
        }

        @Override // com.google.protobuf.CodedOutputStream
        public final void writeFixed64NoTag(long value) throws IOException {
            try {
                byte[] bArr = this.buffer;
                int i = this.position;
                int i2 = i + 1;
                this.position = i2;
                bArr[i] = (byte) (((int) value) & 255);
                int i3 = i2 + 1;
                this.position = i3;
                bArr[i2] = (byte) (((int) (value >> 8)) & 255);
                int i4 = i3 + 1;
                this.position = i4;
                bArr[i3] = (byte) (((int) (value >> 16)) & 255);
                int i5 = i4 + 1;
                this.position = i5;
                bArr[i4] = (byte) (((int) (value >> 24)) & 255);
                int i6 = i5 + 1;
                this.position = i6;
                bArr[i5] = (byte) (((int) (value >> 32)) & 255);
                int i7 = i6 + 1;
                this.position = i7;
                bArr[i6] = (byte) (((int) (value >> 40)) & 255);
                int i8 = i7 + 1;
                this.position = i8;
                bArr[i7] = (byte) (((int) (value >> 48)) & 255);
                this.position = i8 + 1;
                bArr[i8] = (byte) (((int) (value >> 56)) & 255);
            } catch (IndexOutOfBoundsException e) {
                throw new OutOfSpaceException(String.format("Pos: %d, limit: %d, len: %d", Integer.valueOf(this.position), Integer.valueOf(this.limit), 1), e);
            }
        }

        @Override // com.google.protobuf.CodedOutputStream, com.google.protobuf.ByteOutput
        public final void write(byte[] value, int offset, int length) throws IOException {
            try {
                System.arraycopy(value, offset, this.buffer, this.position, length);
                this.position += length;
            } catch (IndexOutOfBoundsException e) {
                throw new OutOfSpaceException(String.format("Pos: %d, limit: %d, len: %d", Integer.valueOf(this.position), Integer.valueOf(this.limit), Integer.valueOf(length)), e);
            }
        }

        @Override // com.google.protobuf.CodedOutputStream, com.google.protobuf.ByteOutput
        public final void writeLazy(byte[] value, int offset, int length) throws IOException {
            write(value, offset, length);
        }

        @Override // com.google.protobuf.CodedOutputStream, com.google.protobuf.ByteOutput
        public final void write(ByteBuffer value) throws IOException {
            int length = value.remaining();
            try {
                value.get(this.buffer, this.position, length);
                this.position += length;
            } catch (IndexOutOfBoundsException e) {
                throw new OutOfSpaceException(String.format("Pos: %d, limit: %d, len: %d", Integer.valueOf(this.position), Integer.valueOf(this.limit), Integer.valueOf(length)), e);
            }
        }

        @Override // com.google.protobuf.CodedOutputStream, com.google.protobuf.ByteOutput
        public final void writeLazy(ByteBuffer value) throws IOException {
            write(value);
        }

        @Override // com.google.protobuf.CodedOutputStream
        public final void writeStringNoTag(String value) throws IOException {
            int oldPosition = this.position;
            try {
                int maxLength = value.length() * 3;
                int maxLengthVarIntSize = computeUInt32SizeNoTag(maxLength);
                int minLengthVarIntSize = computeUInt32SizeNoTag(value.length());
                if (minLengthVarIntSize == maxLengthVarIntSize) {
                    int i = oldPosition + minLengthVarIntSize;
                    this.position = i;
                    int newPosition = Utf8.encode(value, this.buffer, i, spaceLeft());
                    this.position = oldPosition;
                    int length = (newPosition - oldPosition) - minLengthVarIntSize;
                    writeUInt32NoTag(length);
                    this.position = newPosition;
                } else {
                    int length2 = Utf8.encodedLength(value);
                    writeUInt32NoTag(length2);
                    this.position = Utf8.encode(value, this.buffer, this.position, spaceLeft());
                }
            } catch (Utf8.UnpairedSurrogateException e) {
                this.position = oldPosition;
                inefficientWriteStringNoTag(value, e);
            } catch (IndexOutOfBoundsException e2) {
                throw new OutOfSpaceException(e2);
            }
        }

        @Override // com.google.protobuf.CodedOutputStream
        public void flush() {
        }

        @Override // com.google.protobuf.CodedOutputStream
        public final int spaceLeft() {
            return this.limit - this.position;
        }

        @Override // com.google.protobuf.CodedOutputStream
        public final int getTotalBytesWritten() {
            return this.position - this.offset;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static final class HeapNioEncoder extends ArrayEncoder {
        private final ByteBuffer byteBuffer;
        private int initialPosition;

        HeapNioEncoder(ByteBuffer byteBuffer) {
            super(byteBuffer.array(), byteBuffer.arrayOffset() + byteBuffer.position(), byteBuffer.remaining());
            this.byteBuffer = byteBuffer;
            this.initialPosition = byteBuffer.position();
        }

        @Override // com.google.protobuf.CodedOutputStream.ArrayEncoder, com.google.protobuf.CodedOutputStream
        public void flush() {
            this.byteBuffer.position(this.initialPosition + getTotalBytesWritten());
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static final class SafeDirectNioEncoder extends CodedOutputStream {
        private final ByteBuffer buffer;
        private final int initialPosition;
        private final ByteBuffer originalBuffer;

        SafeDirectNioEncoder(ByteBuffer buffer) {
            super();
            this.originalBuffer = buffer;
            this.buffer = buffer.duplicate().order(ByteOrder.LITTLE_ENDIAN);
            this.initialPosition = buffer.position();
        }

        @Override // com.google.protobuf.CodedOutputStream
        public void writeTag(int fieldNumber, int wireType) throws IOException {
            writeUInt32NoTag(WireFormat.makeTag(fieldNumber, wireType));
        }

        @Override // com.google.protobuf.CodedOutputStream
        public void writeInt32(int fieldNumber, int value) throws IOException {
            writeTag(fieldNumber, 0);
            writeInt32NoTag(value);
        }

        @Override // com.google.protobuf.CodedOutputStream
        public void writeUInt32(int fieldNumber, int value) throws IOException {
            writeTag(fieldNumber, 0);
            writeUInt32NoTag(value);
        }

        @Override // com.google.protobuf.CodedOutputStream
        public void writeFixed32(int fieldNumber, int value) throws IOException {
            writeTag(fieldNumber, 5);
            writeFixed32NoTag(value);
        }

        @Override // com.google.protobuf.CodedOutputStream
        public void writeUInt64(int fieldNumber, long value) throws IOException {
            writeTag(fieldNumber, 0);
            writeUInt64NoTag(value);
        }

        @Override // com.google.protobuf.CodedOutputStream
        public void writeFixed64(int fieldNumber, long value) throws IOException {
            writeTag(fieldNumber, 1);
            writeFixed64NoTag(value);
        }

        @Override // com.google.protobuf.CodedOutputStream
        public void writeBool(int fieldNumber, boolean value) throws IOException {
            writeTag(fieldNumber, 0);
            write(value ? (byte) 1 : (byte) 0);
        }

        @Override // com.google.protobuf.CodedOutputStream
        public void writeString(int fieldNumber, String value) throws IOException {
            writeTag(fieldNumber, 2);
            writeStringNoTag(value);
        }

        @Override // com.google.protobuf.CodedOutputStream
        public void writeBytes(int fieldNumber, ByteString value) throws IOException {
            writeTag(fieldNumber, 2);
            writeBytesNoTag(value);
        }

        @Override // com.google.protobuf.CodedOutputStream
        public void writeByteArray(int fieldNumber, byte[] value) throws IOException {
            writeByteArray(fieldNumber, value, 0, value.length);
        }

        @Override // com.google.protobuf.CodedOutputStream
        public void writeByteArray(int fieldNumber, byte[] value, int offset, int length) throws IOException {
            writeTag(fieldNumber, 2);
            writeByteArrayNoTag(value, offset, length);
        }

        @Override // com.google.protobuf.CodedOutputStream
        public void writeByteBuffer(int fieldNumber, ByteBuffer value) throws IOException {
            writeTag(fieldNumber, 2);
            writeUInt32NoTag(value.capacity());
            writeRawBytes(value);
        }

        @Override // com.google.protobuf.CodedOutputStream
        public void writeMessage(int fieldNumber, MessageLite value) throws IOException {
            writeTag(fieldNumber, 2);
            writeMessageNoTag(value);
        }

        @Override // com.google.protobuf.CodedOutputStream
        void writeMessage(int fieldNumber, MessageLite value, Schema schema) throws IOException {
            writeTag(fieldNumber, 2);
            writeMessageNoTag(value, schema);
        }

        @Override // com.google.protobuf.CodedOutputStream
        public void writeMessageSetExtension(int fieldNumber, MessageLite value) throws IOException {
            writeTag(1, 3);
            writeUInt32(2, fieldNumber);
            writeMessage(3, value);
            writeTag(1, 4);
        }

        @Override // com.google.protobuf.CodedOutputStream
        public void writeRawMessageSetExtension(int fieldNumber, ByteString value) throws IOException {
            writeTag(1, 3);
            writeUInt32(2, fieldNumber);
            writeBytes(3, value);
            writeTag(1, 4);
        }

        @Override // com.google.protobuf.CodedOutputStream
        public void writeMessageNoTag(MessageLite value) throws IOException {
            writeUInt32NoTag(value.getSerializedSize());
            value.writeTo(this);
        }

        @Override // com.google.protobuf.CodedOutputStream
        void writeMessageNoTag(MessageLite value, Schema schema) throws IOException {
            writeUInt32NoTag(((AbstractMessageLite) value).getSerializedSize(schema));
            schema.writeTo(value, this.wrapper);
        }

        @Override // com.google.protobuf.CodedOutputStream, com.google.protobuf.ByteOutput
        public void write(byte value) throws IOException {
            try {
                this.buffer.put(value);
            } catch (BufferOverflowException e) {
                throw new OutOfSpaceException(e);
            }
        }

        @Override // com.google.protobuf.CodedOutputStream
        public void writeBytesNoTag(ByteString value) throws IOException {
            writeUInt32NoTag(value.size());
            value.writeTo(this);
        }

        @Override // com.google.protobuf.CodedOutputStream
        public void writeByteArrayNoTag(byte[] value, int offset, int length) throws IOException {
            writeUInt32NoTag(length);
            write(value, offset, length);
        }

        @Override // com.google.protobuf.CodedOutputStream
        public void writeRawBytes(ByteBuffer value) throws IOException {
            if (value.hasArray()) {
                write(value.array(), value.arrayOffset(), value.capacity());
                return;
            }
            ByteBuffer duplicated = value.duplicate();
            duplicated.clear();
            write(duplicated);
        }

        @Override // com.google.protobuf.CodedOutputStream
        public void writeInt32NoTag(int value) throws IOException {
            if (value >= 0) {
                writeUInt32NoTag(value);
            } else {
                writeUInt64NoTag(value);
            }
        }

        @Override // com.google.protobuf.CodedOutputStream
        public void writeUInt32NoTag(int value) throws IOException {
            while ((value & (-128)) != 0) {
                try {
                    this.buffer.put((byte) ((value & 127) | 128));
                    value >>>= 7;
                } catch (BufferOverflowException e) {
                    throw new OutOfSpaceException(e);
                }
            }
            this.buffer.put((byte) value);
        }

        @Override // com.google.protobuf.CodedOutputStream
        public void writeFixed32NoTag(int value) throws IOException {
            try {
                this.buffer.putInt(value);
            } catch (BufferOverflowException e) {
                throw new OutOfSpaceException(e);
            }
        }

        @Override // com.google.protobuf.CodedOutputStream
        public void writeUInt64NoTag(long value) throws IOException {
            while (((-128) & value) != 0) {
                try {
                    this.buffer.put((byte) ((((int) value) & 127) | 128));
                    value >>>= 7;
                } catch (BufferOverflowException e) {
                    throw new OutOfSpaceException(e);
                }
            }
            this.buffer.put((byte) value);
        }

        @Override // com.google.protobuf.CodedOutputStream
        public void writeFixed64NoTag(long value) throws IOException {
            try {
                this.buffer.putLong(value);
            } catch (BufferOverflowException e) {
                throw new OutOfSpaceException(e);
            }
        }

        @Override // com.google.protobuf.CodedOutputStream, com.google.protobuf.ByteOutput
        public void write(byte[] value, int offset, int length) throws IOException {
            try {
                this.buffer.put(value, offset, length);
            } catch (IndexOutOfBoundsException e) {
                throw new OutOfSpaceException(e);
            } catch (BufferOverflowException e2) {
                throw new OutOfSpaceException(e2);
            }
        }

        @Override // com.google.protobuf.CodedOutputStream, com.google.protobuf.ByteOutput
        public void writeLazy(byte[] value, int offset, int length) throws IOException {
            write(value, offset, length);
        }

        @Override // com.google.protobuf.CodedOutputStream, com.google.protobuf.ByteOutput
        public void write(ByteBuffer value) throws IOException {
            try {
                this.buffer.put(value);
            } catch (BufferOverflowException e) {
                throw new OutOfSpaceException(e);
            }
        }

        @Override // com.google.protobuf.CodedOutputStream, com.google.protobuf.ByteOutput
        public void writeLazy(ByteBuffer value) throws IOException {
            write(value);
        }

        @Override // com.google.protobuf.CodedOutputStream
        public void writeStringNoTag(String value) throws IOException {
            int startPos = this.buffer.position();
            try {
                int maxEncodedSize = value.length() * 3;
                int maxLengthVarIntSize = computeUInt32SizeNoTag(maxEncodedSize);
                int minLengthVarIntSize = computeUInt32SizeNoTag(value.length());
                if (minLengthVarIntSize == maxLengthVarIntSize) {
                    int startOfBytes = this.buffer.position() + minLengthVarIntSize;
                    this.buffer.position(startOfBytes);
                    encode(value);
                    int endOfBytes = this.buffer.position();
                    this.buffer.position(startPos);
                    writeUInt32NoTag(endOfBytes - startOfBytes);
                    this.buffer.position(endOfBytes);
                } else {
                    int length = Utf8.encodedLength(value);
                    writeUInt32NoTag(length);
                    encode(value);
                }
            } catch (Utf8.UnpairedSurrogateException e) {
                this.buffer.position(startPos);
                inefficientWriteStringNoTag(value, e);
            } catch (IllegalArgumentException e2) {
                throw new OutOfSpaceException(e2);
            }
        }

        @Override // com.google.protobuf.CodedOutputStream
        public void flush() {
            this.originalBuffer.position(this.buffer.position());
        }

        @Override // com.google.protobuf.CodedOutputStream
        public int spaceLeft() {
            return this.buffer.remaining();
        }

        @Override // com.google.protobuf.CodedOutputStream
        public int getTotalBytesWritten() {
            return this.buffer.position() - this.initialPosition;
        }

        private void encode(String value) throws IOException {
            try {
                Utf8.encodeUtf8(value, this.buffer);
            } catch (IndexOutOfBoundsException e) {
                throw new OutOfSpaceException(e);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static final class UnsafeDirectNioEncoder extends CodedOutputStream {
        private final long address;
        private final ByteBuffer buffer;
        private final long initialPosition;
        private final long limit;
        private final long oneVarintLimit;
        private final ByteBuffer originalBuffer;
        private long position;

        UnsafeDirectNioEncoder(ByteBuffer buffer) {
            super();
            this.originalBuffer = buffer;
            this.buffer = buffer.duplicate().order(ByteOrder.LITTLE_ENDIAN);
            long addressOffset = UnsafeUtil.addressOffset(buffer);
            this.address = addressOffset;
            long position = buffer.position() + addressOffset;
            this.initialPosition = position;
            long limit = addressOffset + buffer.limit();
            this.limit = limit;
            this.oneVarintLimit = limit - 10;
            this.position = position;
        }

        static boolean isSupported() {
            return UnsafeUtil.hasUnsafeByteBufferOperations();
        }

        @Override // com.google.protobuf.CodedOutputStream
        public void writeTag(int fieldNumber, int wireType) throws IOException {
            writeUInt32NoTag(WireFormat.makeTag(fieldNumber, wireType));
        }

        @Override // com.google.protobuf.CodedOutputStream
        public void writeInt32(int fieldNumber, int value) throws IOException {
            writeTag(fieldNumber, 0);
            writeInt32NoTag(value);
        }

        @Override // com.google.protobuf.CodedOutputStream
        public void writeUInt32(int fieldNumber, int value) throws IOException {
            writeTag(fieldNumber, 0);
            writeUInt32NoTag(value);
        }

        @Override // com.google.protobuf.CodedOutputStream
        public void writeFixed32(int fieldNumber, int value) throws IOException {
            writeTag(fieldNumber, 5);
            writeFixed32NoTag(value);
        }

        @Override // com.google.protobuf.CodedOutputStream
        public void writeUInt64(int fieldNumber, long value) throws IOException {
            writeTag(fieldNumber, 0);
            writeUInt64NoTag(value);
        }

        @Override // com.google.protobuf.CodedOutputStream
        public void writeFixed64(int fieldNumber, long value) throws IOException {
            writeTag(fieldNumber, 1);
            writeFixed64NoTag(value);
        }

        @Override // com.google.protobuf.CodedOutputStream
        public void writeBool(int fieldNumber, boolean value) throws IOException {
            writeTag(fieldNumber, 0);
            write(value ? (byte) 1 : (byte) 0);
        }

        @Override // com.google.protobuf.CodedOutputStream
        public void writeString(int fieldNumber, String value) throws IOException {
            writeTag(fieldNumber, 2);
            writeStringNoTag(value);
        }

        @Override // com.google.protobuf.CodedOutputStream
        public void writeBytes(int fieldNumber, ByteString value) throws IOException {
            writeTag(fieldNumber, 2);
            writeBytesNoTag(value);
        }

        @Override // com.google.protobuf.CodedOutputStream
        public void writeByteArray(int fieldNumber, byte[] value) throws IOException {
            writeByteArray(fieldNumber, value, 0, value.length);
        }

        @Override // com.google.protobuf.CodedOutputStream
        public void writeByteArray(int fieldNumber, byte[] value, int offset, int length) throws IOException {
            writeTag(fieldNumber, 2);
            writeByteArrayNoTag(value, offset, length);
        }

        @Override // com.google.protobuf.CodedOutputStream
        public void writeByteBuffer(int fieldNumber, ByteBuffer value) throws IOException {
            writeTag(fieldNumber, 2);
            writeUInt32NoTag(value.capacity());
            writeRawBytes(value);
        }

        @Override // com.google.protobuf.CodedOutputStream
        public void writeMessage(int fieldNumber, MessageLite value) throws IOException {
            writeTag(fieldNumber, 2);
            writeMessageNoTag(value);
        }

        @Override // com.google.protobuf.CodedOutputStream
        void writeMessage(int fieldNumber, MessageLite value, Schema schema) throws IOException {
            writeTag(fieldNumber, 2);
            writeMessageNoTag(value, schema);
        }

        @Override // com.google.protobuf.CodedOutputStream
        public void writeMessageSetExtension(int fieldNumber, MessageLite value) throws IOException {
            writeTag(1, 3);
            writeUInt32(2, fieldNumber);
            writeMessage(3, value);
            writeTag(1, 4);
        }

        @Override // com.google.protobuf.CodedOutputStream
        public void writeRawMessageSetExtension(int fieldNumber, ByteString value) throws IOException {
            writeTag(1, 3);
            writeUInt32(2, fieldNumber);
            writeBytes(3, value);
            writeTag(1, 4);
        }

        @Override // com.google.protobuf.CodedOutputStream
        public void writeMessageNoTag(MessageLite value) throws IOException {
            writeUInt32NoTag(value.getSerializedSize());
            value.writeTo(this);
        }

        @Override // com.google.protobuf.CodedOutputStream
        void writeMessageNoTag(MessageLite value, Schema schema) throws IOException {
            writeUInt32NoTag(((AbstractMessageLite) value).getSerializedSize(schema));
            schema.writeTo(value, this.wrapper);
        }

        @Override // com.google.protobuf.CodedOutputStream, com.google.protobuf.ByteOutput
        public void write(byte value) throws IOException {
            long j = this.position;
            if (j >= this.limit) {
                throw new OutOfSpaceException(String.format("Pos: %d, limit: %d, len: %d", Long.valueOf(this.position), Long.valueOf(this.limit), 1));
            }
            this.position = 1 + j;
            UnsafeUtil.putByte(j, value);
        }

        @Override // com.google.protobuf.CodedOutputStream
        public void writeBytesNoTag(ByteString value) throws IOException {
            writeUInt32NoTag(value.size());
            value.writeTo(this);
        }

        @Override // com.google.protobuf.CodedOutputStream
        public void writeByteArrayNoTag(byte[] value, int offset, int length) throws IOException {
            writeUInt32NoTag(length);
            write(value, offset, length);
        }

        @Override // com.google.protobuf.CodedOutputStream
        public void writeRawBytes(ByteBuffer value) throws IOException {
            if (value.hasArray()) {
                write(value.array(), value.arrayOffset(), value.capacity());
                return;
            }
            ByteBuffer duplicated = value.duplicate();
            duplicated.clear();
            write(duplicated);
        }

        @Override // com.google.protobuf.CodedOutputStream
        public void writeInt32NoTag(int value) throws IOException {
            if (value >= 0) {
                writeUInt32NoTag(value);
            } else {
                writeUInt64NoTag(value);
            }
        }

        @Override // com.google.protobuf.CodedOutputStream
        public void writeUInt32NoTag(int value) throws IOException {
            if (this.position <= this.oneVarintLimit) {
                while ((value & (-128)) != 0) {
                    long j = this.position;
                    this.position = j + 1;
                    UnsafeUtil.putByte(j, (byte) ((value & 127) | 128));
                    value >>>= 7;
                }
                long j2 = this.position;
                this.position = 1 + j2;
                UnsafeUtil.putByte(j2, (byte) value);
                return;
            }
            while (true) {
                long j3 = this.position;
                if (j3 < this.limit) {
                    if ((value & (-128)) == 0) {
                        this.position = 1 + j3;
                        UnsafeUtil.putByte(j3, (byte) value);
                        return;
                    }
                    this.position = j3 + 1;
                    UnsafeUtil.putByte(j3, (byte) ((value & 127) | 128));
                    value >>>= 7;
                } else {
                    throw new OutOfSpaceException(String.format("Pos: %d, limit: %d, len: %d", Long.valueOf(this.position), Long.valueOf(this.limit), 1));
                }
            }
        }

        @Override // com.google.protobuf.CodedOutputStream
        public void writeFixed32NoTag(int value) throws IOException {
            this.buffer.putInt(bufferPos(this.position), value);
            this.position += 4;
        }

        @Override // com.google.protobuf.CodedOutputStream
        public void writeUInt64NoTag(long value) throws IOException {
            if (this.position <= this.oneVarintLimit) {
                while ((value & (-128)) != 0) {
                    long j = this.position;
                    this.position = j + 1;
                    UnsafeUtil.putByte(j, (byte) ((((int) value) & 127) | 128));
                    value >>>= 7;
                }
                long j2 = this.position;
                this.position = 1 + j2;
                UnsafeUtil.putByte(j2, (byte) value);
                return;
            }
            while (true) {
                long j3 = this.position;
                if (j3 < this.limit) {
                    if ((value & (-128)) == 0) {
                        this.position = 1 + j3;
                        UnsafeUtil.putByte(j3, (byte) value);
                        return;
                    }
                    this.position = j3 + 1;
                    UnsafeUtil.putByte(j3, (byte) ((((int) value) & 127) | 128));
                    value >>>= 7;
                } else {
                    throw new OutOfSpaceException(String.format("Pos: %d, limit: %d, len: %d", Long.valueOf(this.position), Long.valueOf(this.limit), 1));
                }
            }
        }

        @Override // com.google.protobuf.CodedOutputStream
        public void writeFixed64NoTag(long value) throws IOException {
            this.buffer.putLong(bufferPos(this.position), value);
            this.position += 8;
        }

        @Override // com.google.protobuf.CodedOutputStream, com.google.protobuf.ByteOutput
        public void write(byte[] value, int offset, int length) throws IOException {
            if (value != null && offset >= 0 && length >= 0 && value.length - length >= offset) {
                long j = this.position;
                if (this.limit - length >= j) {
                    UnsafeUtil.copyMemory(value, offset, j, length);
                    this.position += length;
                    return;
                }
            }
            if (value == null) {
                throw new NullPointerException("value");
            }
            throw new OutOfSpaceException(String.format("Pos: %d, limit: %d, len: %d", Long.valueOf(this.position), Long.valueOf(this.limit), Integer.valueOf(length)));
        }

        @Override // com.google.protobuf.CodedOutputStream, com.google.protobuf.ByteOutput
        public void writeLazy(byte[] value, int offset, int length) throws IOException {
            write(value, offset, length);
        }

        @Override // com.google.protobuf.CodedOutputStream, com.google.protobuf.ByteOutput
        public void write(ByteBuffer value) throws IOException {
            try {
                int length = value.remaining();
                repositionBuffer(this.position);
                this.buffer.put(value);
                this.position += length;
            } catch (BufferOverflowException e) {
                throw new OutOfSpaceException(e);
            }
        }

        @Override // com.google.protobuf.CodedOutputStream, com.google.protobuf.ByteOutput
        public void writeLazy(ByteBuffer value) throws IOException {
            write(value);
        }

        @Override // com.google.protobuf.CodedOutputStream
        public void writeStringNoTag(String value) throws IOException {
            long prevPos = this.position;
            try {
                int maxEncodedSize = value.length() * 3;
                int maxLengthVarIntSize = computeUInt32SizeNoTag(maxEncodedSize);
                int minLengthVarIntSize = computeUInt32SizeNoTag(value.length());
                if (minLengthVarIntSize == maxLengthVarIntSize) {
                    int stringStart = bufferPos(this.position) + minLengthVarIntSize;
                    this.buffer.position(stringStart);
                    Utf8.encodeUtf8(value, this.buffer);
                    int length = this.buffer.position() - stringStart;
                    writeUInt32NoTag(length);
                    this.position += length;
                } else {
                    int length2 = Utf8.encodedLength(value);
                    writeUInt32NoTag(length2);
                    repositionBuffer(this.position);
                    Utf8.encodeUtf8(value, this.buffer);
                    this.position += length2;
                }
            } catch (Utf8.UnpairedSurrogateException e) {
                this.position = prevPos;
                repositionBuffer(prevPos);
                inefficientWriteStringNoTag(value, e);
            } catch (IllegalArgumentException e2) {
                throw new OutOfSpaceException(e2);
            } catch (IndexOutOfBoundsException e3) {
                throw new OutOfSpaceException(e3);
            }
        }

        @Override // com.google.protobuf.CodedOutputStream
        public void flush() {
            this.originalBuffer.position(bufferPos(this.position));
        }

        @Override // com.google.protobuf.CodedOutputStream
        public int spaceLeft() {
            return (int) (this.limit - this.position);
        }

        @Override // com.google.protobuf.CodedOutputStream
        public int getTotalBytesWritten() {
            return (int) (this.position - this.initialPosition);
        }

        private void repositionBuffer(long pos) {
            this.buffer.position(bufferPos(pos));
        }

        private int bufferPos(long pos) {
            return (int) (pos - this.address);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static abstract class AbstractBufferedEncoder extends CodedOutputStream {
        final byte[] buffer;
        final int limit;
        int position;
        int totalBytesWritten;

        AbstractBufferedEncoder(int bufferSize) {
            super();
            if (bufferSize < 0) {
                throw new IllegalArgumentException("bufferSize must be >= 0");
            }
            byte[] bArr = new byte[Math.max(bufferSize, 20)];
            this.buffer = bArr;
            this.limit = bArr.length;
        }

        @Override // com.google.protobuf.CodedOutputStream
        public final int spaceLeft() {
            throw new UnsupportedOperationException("spaceLeft() can only be called on CodedOutputStreams that are writing to a flat array or ByteBuffer.");
        }

        @Override // com.google.protobuf.CodedOutputStream
        public final int getTotalBytesWritten() {
            return this.totalBytesWritten;
        }

        final void buffer(byte value) {
            byte[] bArr = this.buffer;
            int i = this.position;
            this.position = i + 1;
            bArr[i] = value;
            this.totalBytesWritten++;
        }

        final void bufferTag(int fieldNumber, int wireType) {
            bufferUInt32NoTag(WireFormat.makeTag(fieldNumber, wireType));
        }

        final void bufferInt32NoTag(int value) {
            if (value >= 0) {
                bufferUInt32NoTag(value);
            } else {
                bufferUInt64NoTag(value);
            }
        }

        final void bufferUInt32NoTag(int value) {
            if (CodedOutputStream.HAS_UNSAFE_ARRAY_OPERATIONS) {
                long originalPos = this.position;
                while ((value & (-128)) != 0) {
                    byte[] bArr = this.buffer;
                    int i = this.position;
                    this.position = i + 1;
                    UnsafeUtil.putByte(bArr, i, (byte) ((value & 127) | 128));
                    value >>>= 7;
                }
                byte[] bArr2 = this.buffer;
                int i2 = this.position;
                this.position = i2 + 1;
                UnsafeUtil.putByte(bArr2, i2, (byte) value);
                int delta = (int) (this.position - originalPos);
                this.totalBytesWritten += delta;
                return;
            }
            while ((value & (-128)) != 0) {
                byte[] bArr3 = this.buffer;
                int i3 = this.position;
                this.position = i3 + 1;
                bArr3[i3] = (byte) ((value & 127) | 128);
                this.totalBytesWritten++;
                value >>>= 7;
            }
            byte[] bArr4 = this.buffer;
            int i4 = this.position;
            this.position = i4 + 1;
            bArr4[i4] = (byte) value;
            this.totalBytesWritten++;
        }

        /* JADX WARN: Incorrect condition in loop: B:11:0x0047 */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
        */
        final void bufferUInt64NoTag(long value) {
            if (CodedOutputStream.HAS_UNSAFE_ARRAY_OPERATIONS) {
                long originalPos = this.position;
                while ((value & (-128)) != 0) {
                    byte[] bArr = this.buffer;
                    int i = this.position;
                    this.position = i + 1;
                    UnsafeUtil.putByte(bArr, i, (byte) ((((int) value) & 127) | 128));
                    value >>>= 7;
                }
                byte[] bArr2 = this.buffer;
                int i2 = this.position;
                this.position = i2 + 1;
                UnsafeUtil.putByte(bArr2, i2, (byte) value);
                int delta = (int) (this.position - originalPos);
                this.totalBytesWritten += delta;
                return;
            }
            while (originalPos != 0) {
                byte[] bArr3 = this.buffer;
                int i3 = this.position;
                this.position = i3 + 1;
                bArr3[i3] = (byte) ((((int) value) & 127) | 128);
                this.totalBytesWritten++;
                value >>>= 7;
            }
            byte[] bArr4 = this.buffer;
            int i4 = this.position;
            this.position = i4 + 1;
            bArr4[i4] = (byte) value;
            this.totalBytesWritten++;
        }

        final void bufferFixed32NoTag(int value) {
            byte[] bArr = this.buffer;
            int i = this.position;
            int i2 = i + 1;
            this.position = i2;
            bArr[i] = (byte) (value & 255);
            int i3 = i2 + 1;
            this.position = i3;
            bArr[i2] = (byte) ((value >> 8) & 255);
            int i4 = i3 + 1;
            this.position = i4;
            bArr[i3] = (byte) ((value >> 16) & 255);
            this.position = i4 + 1;
            bArr[i4] = (byte) ((value >> 24) & 255);
            this.totalBytesWritten += 4;
        }

        final void bufferFixed64NoTag(long value) {
            byte[] bArr = this.buffer;
            int i = this.position;
            int i2 = i + 1;
            this.position = i2;
            bArr[i] = (byte) (value & 255);
            int i3 = i2 + 1;
            this.position = i3;
            bArr[i2] = (byte) ((value >> 8) & 255);
            int i4 = i3 + 1;
            this.position = i4;
            bArr[i3] = (byte) ((value >> 16) & 255);
            int i5 = i4 + 1;
            this.position = i5;
            bArr[i4] = (byte) (255 & (value >> 24));
            int i6 = i5 + 1;
            this.position = i6;
            bArr[i5] = (byte) (((int) (value >> 32)) & 255);
            int i7 = i6 + 1;
            this.position = i7;
            bArr[i6] = (byte) (((int) (value >> 40)) & 255);
            int i8 = i7 + 1;
            this.position = i8;
            bArr[i7] = (byte) (((int) (value >> 48)) & 255);
            this.position = i8 + 1;
            bArr[i8] = (byte) (((int) (value >> 56)) & 255);
            this.totalBytesWritten += 8;
        }
    }

    /* loaded from: classes.dex */
    private static final class ByteOutputEncoder extends AbstractBufferedEncoder {
        private final ByteOutput out;

        ByteOutputEncoder(ByteOutput out, int bufferSize) {
            super(bufferSize);
            if (out == null) {
                throw new NullPointerException("out");
            }
            this.out = out;
        }

        @Override // com.google.protobuf.CodedOutputStream
        public void writeTag(int fieldNumber, int wireType) throws IOException {
            writeUInt32NoTag(WireFormat.makeTag(fieldNumber, wireType));
        }

        @Override // com.google.protobuf.CodedOutputStream
        public void writeInt32(int fieldNumber, int value) throws IOException {
            flushIfNotAvailable(20);
            bufferTag(fieldNumber, 0);
            bufferInt32NoTag(value);
        }

        @Override // com.google.protobuf.CodedOutputStream
        public void writeUInt32(int fieldNumber, int value) throws IOException {
            flushIfNotAvailable(20);
            bufferTag(fieldNumber, 0);
            bufferUInt32NoTag(value);
        }

        @Override // com.google.protobuf.CodedOutputStream
        public void writeFixed32(int fieldNumber, int value) throws IOException {
            flushIfNotAvailable(14);
            bufferTag(fieldNumber, 5);
            bufferFixed32NoTag(value);
        }

        @Override // com.google.protobuf.CodedOutputStream
        public void writeUInt64(int fieldNumber, long value) throws IOException {
            flushIfNotAvailable(20);
            bufferTag(fieldNumber, 0);
            bufferUInt64NoTag(value);
        }

        @Override // com.google.protobuf.CodedOutputStream
        public void writeFixed64(int fieldNumber, long value) throws IOException {
            flushIfNotAvailable(18);
            bufferTag(fieldNumber, 1);
            bufferFixed64NoTag(value);
        }

        @Override // com.google.protobuf.CodedOutputStream
        public void writeBool(int fieldNumber, boolean value) throws IOException {
            flushIfNotAvailable(11);
            bufferTag(fieldNumber, 0);
            buffer(value ? (byte) 1 : (byte) 0);
        }

        @Override // com.google.protobuf.CodedOutputStream
        public void writeString(int fieldNumber, String value) throws IOException {
            writeTag(fieldNumber, 2);
            writeStringNoTag(value);
        }

        @Override // com.google.protobuf.CodedOutputStream
        public void writeBytes(int fieldNumber, ByteString value) throws IOException {
            writeTag(fieldNumber, 2);
            writeBytesNoTag(value);
        }

        @Override // com.google.protobuf.CodedOutputStream
        public void writeByteArray(int fieldNumber, byte[] value) throws IOException {
            writeByteArray(fieldNumber, value, 0, value.length);
        }

        @Override // com.google.protobuf.CodedOutputStream
        public void writeByteArray(int fieldNumber, byte[] value, int offset, int length) throws IOException {
            writeTag(fieldNumber, 2);
            writeByteArrayNoTag(value, offset, length);
        }

        @Override // com.google.protobuf.CodedOutputStream
        public void writeByteBuffer(int fieldNumber, ByteBuffer value) throws IOException {
            writeTag(fieldNumber, 2);
            writeUInt32NoTag(value.capacity());
            writeRawBytes(value);
        }

        @Override // com.google.protobuf.CodedOutputStream
        public void writeBytesNoTag(ByteString value) throws IOException {
            writeUInt32NoTag(value.size());
            value.writeTo(this);
        }

        @Override // com.google.protobuf.CodedOutputStream
        public void writeByteArrayNoTag(byte[] value, int offset, int length) throws IOException {
            writeUInt32NoTag(length);
            write(value, offset, length);
        }

        @Override // com.google.protobuf.CodedOutputStream
        public void writeRawBytes(ByteBuffer value) throws IOException {
            if (value.hasArray()) {
                write(value.array(), value.arrayOffset(), value.capacity());
                return;
            }
            ByteBuffer duplicated = value.duplicate();
            duplicated.clear();
            write(duplicated);
        }

        @Override // com.google.protobuf.CodedOutputStream
        public void writeMessage(int fieldNumber, MessageLite value) throws IOException {
            writeTag(fieldNumber, 2);
            writeMessageNoTag(value);
        }

        @Override // com.google.protobuf.CodedOutputStream
        void writeMessage(int fieldNumber, MessageLite value, Schema schema) throws IOException {
            writeTag(fieldNumber, 2);
            writeMessageNoTag(value, schema);
        }

        @Override // com.google.protobuf.CodedOutputStream
        public void writeMessageSetExtension(int fieldNumber, MessageLite value) throws IOException {
            writeTag(1, 3);
            writeUInt32(2, fieldNumber);
            writeMessage(3, value);
            writeTag(1, 4);
        }

        @Override // com.google.protobuf.CodedOutputStream
        public void writeRawMessageSetExtension(int fieldNumber, ByteString value) throws IOException {
            writeTag(1, 3);
            writeUInt32(2, fieldNumber);
            writeBytes(3, value);
            writeTag(1, 4);
        }

        @Override // com.google.protobuf.CodedOutputStream
        public void writeMessageNoTag(MessageLite value) throws IOException {
            writeUInt32NoTag(value.getSerializedSize());
            value.writeTo(this);
        }

        @Override // com.google.protobuf.CodedOutputStream
        void writeMessageNoTag(MessageLite value, Schema schema) throws IOException {
            writeUInt32NoTag(((AbstractMessageLite) value).getSerializedSize(schema));
            schema.writeTo(value, this.wrapper);
        }

        @Override // com.google.protobuf.CodedOutputStream, com.google.protobuf.ByteOutput
        public void write(byte value) throws IOException {
            if (this.position == this.limit) {
                doFlush();
            }
            buffer(value);
        }

        @Override // com.google.protobuf.CodedOutputStream
        public void writeInt32NoTag(int value) throws IOException {
            if (value >= 0) {
                writeUInt32NoTag(value);
            } else {
                writeUInt64NoTag(value);
            }
        }

        @Override // com.google.protobuf.CodedOutputStream
        public void writeUInt32NoTag(int value) throws IOException {
            flushIfNotAvailable(5);
            bufferUInt32NoTag(value);
        }

        @Override // com.google.protobuf.CodedOutputStream
        public void writeFixed32NoTag(int value) throws IOException {
            flushIfNotAvailable(4);
            bufferFixed32NoTag(value);
        }

        @Override // com.google.protobuf.CodedOutputStream
        public void writeUInt64NoTag(long value) throws IOException {
            flushIfNotAvailable(10);
            bufferUInt64NoTag(value);
        }

        @Override // com.google.protobuf.CodedOutputStream
        public void writeFixed64NoTag(long value) throws IOException {
            flushIfNotAvailable(8);
            bufferFixed64NoTag(value);
        }

        @Override // com.google.protobuf.CodedOutputStream
        public void writeStringNoTag(String value) throws IOException {
            int maxLength = value.length() * 3;
            int maxLengthVarIntSize = computeUInt32SizeNoTag(maxLength);
            if (maxLengthVarIntSize + maxLength > this.limit) {
                byte[] encodedBytes = new byte[maxLength];
                int actualLength = Utf8.encode(value, encodedBytes, 0, maxLength);
                writeUInt32NoTag(actualLength);
                writeLazy(encodedBytes, 0, actualLength);
                return;
            }
            if (maxLengthVarIntSize + maxLength > this.limit - this.position) {
                doFlush();
            }
            int oldPosition = this.position;
            try {
                int minLengthVarIntSize = computeUInt32SizeNoTag(value.length());
                if (minLengthVarIntSize == maxLengthVarIntSize) {
                    this.position = oldPosition + minLengthVarIntSize;
                    int newPosition = Utf8.encode(value, this.buffer, this.position, this.limit - this.position);
                    this.position = oldPosition;
                    int length = (newPosition - oldPosition) - minLengthVarIntSize;
                    bufferUInt32NoTag(length);
                    this.position = newPosition;
                    this.totalBytesWritten += length;
                } else {
                    int length2 = Utf8.encodedLength(value);
                    bufferUInt32NoTag(length2);
                    this.position = Utf8.encode(value, this.buffer, this.position, length2);
                    this.totalBytesWritten += length2;
                }
            } catch (Utf8.UnpairedSurrogateException e) {
                this.totalBytesWritten -= this.position - oldPosition;
                this.position = oldPosition;
                inefficientWriteStringNoTag(value, e);
            } catch (IndexOutOfBoundsException e2) {
                throw new OutOfSpaceException(e2);
            }
        }

        @Override // com.google.protobuf.CodedOutputStream
        public void flush() throws IOException {
            if (this.position > 0) {
                doFlush();
            }
        }

        @Override // com.google.protobuf.CodedOutputStream, com.google.protobuf.ByteOutput
        public void write(byte[] value, int offset, int length) throws IOException {
            flush();
            this.out.write(value, offset, length);
            this.totalBytesWritten += length;
        }

        @Override // com.google.protobuf.CodedOutputStream, com.google.protobuf.ByteOutput
        public void writeLazy(byte[] value, int offset, int length) throws IOException {
            flush();
            this.out.writeLazy(value, offset, length);
            this.totalBytesWritten += length;
        }

        @Override // com.google.protobuf.CodedOutputStream, com.google.protobuf.ByteOutput
        public void write(ByteBuffer value) throws IOException {
            flush();
            int length = value.remaining();
            this.out.write(value);
            this.totalBytesWritten += length;
        }

        @Override // com.google.protobuf.CodedOutputStream, com.google.protobuf.ByteOutput
        public void writeLazy(ByteBuffer value) throws IOException {
            flush();
            int length = value.remaining();
            this.out.writeLazy(value);
            this.totalBytesWritten += length;
        }

        private void flushIfNotAvailable(int requiredSize) throws IOException {
            if (this.limit - this.position < requiredSize) {
                doFlush();
            }
        }

        private void doFlush() throws IOException {
            this.out.write(this.buffer, 0, this.position);
            this.position = 0;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static final class OutputStreamEncoder extends AbstractBufferedEncoder {
        private final OutputStream out;

        OutputStreamEncoder(OutputStream out, int bufferSize) {
            super(bufferSize);
            if (out == null) {
                throw new NullPointerException("out");
            }
            this.out = out;
        }

        @Override // com.google.protobuf.CodedOutputStream
        public void writeTag(int fieldNumber, int wireType) throws IOException {
            writeUInt32NoTag(WireFormat.makeTag(fieldNumber, wireType));
        }

        @Override // com.google.protobuf.CodedOutputStream
        public void writeInt32(int fieldNumber, int value) throws IOException {
            flushIfNotAvailable(20);
            bufferTag(fieldNumber, 0);
            bufferInt32NoTag(value);
        }

        @Override // com.google.protobuf.CodedOutputStream
        public void writeUInt32(int fieldNumber, int value) throws IOException {
            flushIfNotAvailable(20);
            bufferTag(fieldNumber, 0);
            bufferUInt32NoTag(value);
        }

        @Override // com.google.protobuf.CodedOutputStream
        public void writeFixed32(int fieldNumber, int value) throws IOException {
            flushIfNotAvailable(14);
            bufferTag(fieldNumber, 5);
            bufferFixed32NoTag(value);
        }

        @Override // com.google.protobuf.CodedOutputStream
        public void writeUInt64(int fieldNumber, long value) throws IOException {
            flushIfNotAvailable(20);
            bufferTag(fieldNumber, 0);
            bufferUInt64NoTag(value);
        }

        @Override // com.google.protobuf.CodedOutputStream
        public void writeFixed64(int fieldNumber, long value) throws IOException {
            flushIfNotAvailable(18);
            bufferTag(fieldNumber, 1);
            bufferFixed64NoTag(value);
        }

        @Override // com.google.protobuf.CodedOutputStream
        public void writeBool(int fieldNumber, boolean value) throws IOException {
            flushIfNotAvailable(11);
            bufferTag(fieldNumber, 0);
            buffer(value ? (byte) 1 : (byte) 0);
        }

        @Override // com.google.protobuf.CodedOutputStream
        public void writeString(int fieldNumber, String value) throws IOException {
            writeTag(fieldNumber, 2);
            writeStringNoTag(value);
        }

        @Override // com.google.protobuf.CodedOutputStream
        public void writeBytes(int fieldNumber, ByteString value) throws IOException {
            writeTag(fieldNumber, 2);
            writeBytesNoTag(value);
        }

        @Override // com.google.protobuf.CodedOutputStream
        public void writeByteArray(int fieldNumber, byte[] value) throws IOException {
            writeByteArray(fieldNumber, value, 0, value.length);
        }

        @Override // com.google.protobuf.CodedOutputStream
        public void writeByteArray(int fieldNumber, byte[] value, int offset, int length) throws IOException {
            writeTag(fieldNumber, 2);
            writeByteArrayNoTag(value, offset, length);
        }

        @Override // com.google.protobuf.CodedOutputStream
        public void writeByteBuffer(int fieldNumber, ByteBuffer value) throws IOException {
            writeTag(fieldNumber, 2);
            writeUInt32NoTag(value.capacity());
            writeRawBytes(value);
        }

        @Override // com.google.protobuf.CodedOutputStream
        public void writeBytesNoTag(ByteString value) throws IOException {
            writeUInt32NoTag(value.size());
            value.writeTo(this);
        }

        @Override // com.google.protobuf.CodedOutputStream
        public void writeByteArrayNoTag(byte[] value, int offset, int length) throws IOException {
            writeUInt32NoTag(length);
            write(value, offset, length);
        }

        @Override // com.google.protobuf.CodedOutputStream
        public void writeRawBytes(ByteBuffer value) throws IOException {
            if (value.hasArray()) {
                write(value.array(), value.arrayOffset(), value.capacity());
                return;
            }
            ByteBuffer duplicated = value.duplicate();
            duplicated.clear();
            write(duplicated);
        }

        @Override // com.google.protobuf.CodedOutputStream
        public void writeMessage(int fieldNumber, MessageLite value) throws IOException {
            writeTag(fieldNumber, 2);
            writeMessageNoTag(value);
        }

        @Override // com.google.protobuf.CodedOutputStream
        void writeMessage(int fieldNumber, MessageLite value, Schema schema) throws IOException {
            writeTag(fieldNumber, 2);
            writeMessageNoTag(value, schema);
        }

        @Override // com.google.protobuf.CodedOutputStream
        public void writeMessageSetExtension(int fieldNumber, MessageLite value) throws IOException {
            writeTag(1, 3);
            writeUInt32(2, fieldNumber);
            writeMessage(3, value);
            writeTag(1, 4);
        }

        @Override // com.google.protobuf.CodedOutputStream
        public void writeRawMessageSetExtension(int fieldNumber, ByteString value) throws IOException {
            writeTag(1, 3);
            writeUInt32(2, fieldNumber);
            writeBytes(3, value);
            writeTag(1, 4);
        }

        @Override // com.google.protobuf.CodedOutputStream
        public void writeMessageNoTag(MessageLite value) throws IOException {
            writeUInt32NoTag(value.getSerializedSize());
            value.writeTo(this);
        }

        @Override // com.google.protobuf.CodedOutputStream
        void writeMessageNoTag(MessageLite value, Schema schema) throws IOException {
            writeUInt32NoTag(((AbstractMessageLite) value).getSerializedSize(schema));
            schema.writeTo(value, this.wrapper);
        }

        @Override // com.google.protobuf.CodedOutputStream, com.google.protobuf.ByteOutput
        public void write(byte value) throws IOException {
            if (this.position == this.limit) {
                doFlush();
            }
            buffer(value);
        }

        @Override // com.google.protobuf.CodedOutputStream
        public void writeInt32NoTag(int value) throws IOException {
            if (value >= 0) {
                writeUInt32NoTag(value);
            } else {
                writeUInt64NoTag(value);
            }
        }

        @Override // com.google.protobuf.CodedOutputStream
        public void writeUInt32NoTag(int value) throws IOException {
            flushIfNotAvailable(5);
            bufferUInt32NoTag(value);
        }

        @Override // com.google.protobuf.CodedOutputStream
        public void writeFixed32NoTag(int value) throws IOException {
            flushIfNotAvailable(4);
            bufferFixed32NoTag(value);
        }

        @Override // com.google.protobuf.CodedOutputStream
        public void writeUInt64NoTag(long value) throws IOException {
            flushIfNotAvailable(10);
            bufferUInt64NoTag(value);
        }

        @Override // com.google.protobuf.CodedOutputStream
        public void writeFixed64NoTag(long value) throws IOException {
            flushIfNotAvailable(8);
            bufferFixed64NoTag(value);
        }

        @Override // com.google.protobuf.CodedOutputStream
        public void writeStringNoTag(String value) throws IOException {
            int length;
            try {
                int maxLength = value.length() * 3;
                int maxLengthVarIntSize = computeUInt32SizeNoTag(maxLength);
                if (maxLengthVarIntSize + maxLength > this.limit) {
                    byte[] encodedBytes = new byte[maxLength];
                    int actualLength = Utf8.encode(value, encodedBytes, 0, maxLength);
                    writeUInt32NoTag(actualLength);
                    writeLazy(encodedBytes, 0, actualLength);
                    return;
                }
                if (maxLengthVarIntSize + maxLength > this.limit - this.position) {
                    doFlush();
                }
                int minLengthVarIntSize = computeUInt32SizeNoTag(value.length());
                int oldPosition = this.position;
                try {
                    try {
                        if (minLengthVarIntSize == maxLengthVarIntSize) {
                            this.position = oldPosition + minLengthVarIntSize;
                            int newPosition = Utf8.encode(value, this.buffer, this.position, this.limit - this.position);
                            this.position = oldPosition;
                            length = (newPosition - oldPosition) - minLengthVarIntSize;
                            bufferUInt32NoTag(length);
                            this.position = newPosition;
                        } else {
                            length = Utf8.encodedLength(value);
                            bufferUInt32NoTag(length);
                            this.position = Utf8.encode(value, this.buffer, this.position, length);
                        }
                        this.totalBytesWritten += length;
                    } catch (Utf8.UnpairedSurrogateException e) {
                        this.totalBytesWritten -= this.position - oldPosition;
                        this.position = oldPosition;
                        throw e;
                    }
                } catch (ArrayIndexOutOfBoundsException e2) {
                    throw new OutOfSpaceException(e2);
                }
            } catch (Utf8.UnpairedSurrogateException e3) {
                inefficientWriteStringNoTag(value, e3);
            }
        }

        @Override // com.google.protobuf.CodedOutputStream
        public void flush() throws IOException {
            if (this.position > 0) {
                doFlush();
            }
        }

        @Override // com.google.protobuf.CodedOutputStream, com.google.protobuf.ByteOutput
        public void write(byte[] value, int offset, int length) throws IOException {
            if (this.limit - this.position >= length) {
                System.arraycopy(value, offset, this.buffer, this.position, length);
                this.position += length;
                this.totalBytesWritten += length;
                return;
            }
            int bytesWritten = this.limit - this.position;
            System.arraycopy(value, offset, this.buffer, this.position, bytesWritten);
            int offset2 = offset + bytesWritten;
            int length2 = length - bytesWritten;
            this.position = this.limit;
            this.totalBytesWritten += bytesWritten;
            doFlush();
            if (length2 <= this.limit) {
                System.arraycopy(value, offset2, this.buffer, 0, length2);
                this.position = length2;
            } else {
                this.out.write(value, offset2, length2);
            }
            this.totalBytesWritten += length2;
        }

        @Override // com.google.protobuf.CodedOutputStream, com.google.protobuf.ByteOutput
        public void writeLazy(byte[] value, int offset, int length) throws IOException {
            write(value, offset, length);
        }

        @Override // com.google.protobuf.CodedOutputStream, com.google.protobuf.ByteOutput
        public void write(ByteBuffer value) throws IOException {
            int length = value.remaining();
            if (this.limit - this.position >= length) {
                value.get(this.buffer, this.position, length);
                this.position += length;
                this.totalBytesWritten += length;
                return;
            }
            int bytesWritten = this.limit - this.position;
            value.get(this.buffer, this.position, bytesWritten);
            int length2 = length - bytesWritten;
            this.position = this.limit;
            this.totalBytesWritten += bytesWritten;
            doFlush();
            while (length2 > this.limit) {
                value.get(this.buffer, 0, this.limit);
                this.out.write(this.buffer, 0, this.limit);
                length2 -= this.limit;
                this.totalBytesWritten += this.limit;
            }
            value.get(this.buffer, 0, length2);
            this.position = length2;
            this.totalBytesWritten += length2;
        }

        @Override // com.google.protobuf.CodedOutputStream, com.google.protobuf.ByteOutput
        public void writeLazy(ByteBuffer value) throws IOException {
            write(value);
        }

        private void flushIfNotAvailable(int requiredSize) throws IOException {
            if (this.limit - this.position < requiredSize) {
                doFlush();
            }
        }

        private void doFlush() throws IOException {
            this.out.write(this.buffer, 0, this.position);
            this.position = 0;
        }
    }
}
