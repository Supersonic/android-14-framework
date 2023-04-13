package com.android.framework.protobuf;

import android.hardware.biometrics.fingerprint.AcquiredInfo;
import com.android.framework.protobuf.MessageLite;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
/* loaded from: classes4.dex */
public abstract class CodedInputStream {
    private static final int DEFAULT_BUFFER_SIZE = 4096;
    private static final int DEFAULT_SIZE_LIMIT = Integer.MAX_VALUE;
    private static volatile int defaultRecursionLimit = 100;
    int recursionDepth;
    int recursionLimit;
    private boolean shouldDiscardUnknownFields;
    int sizeLimit;
    CodedInputStreamReader wrapper;

    public abstract void checkLastTagWas(int i) throws InvalidProtocolBufferException;

    public abstract void enableAliasing(boolean z);

    public abstract int getBytesUntilLimit();

    public abstract int getLastTag();

    public abstract int getTotalBytesRead();

    public abstract boolean isAtEnd() throws IOException;

    public abstract void popLimit(int i);

    public abstract int pushLimit(int i) throws InvalidProtocolBufferException;

    public abstract boolean readBool() throws IOException;

    public abstract byte[] readByteArray() throws IOException;

    public abstract ByteBuffer readByteBuffer() throws IOException;

    public abstract ByteString readBytes() throws IOException;

    public abstract double readDouble() throws IOException;

    public abstract int readEnum() throws IOException;

    public abstract int readFixed32() throws IOException;

    public abstract long readFixed64() throws IOException;

    public abstract float readFloat() throws IOException;

    public abstract <T extends MessageLite> T readGroup(int i, Parser<T> parser, ExtensionRegistryLite extensionRegistryLite) throws IOException;

    public abstract void readGroup(int i, MessageLite.Builder builder, ExtensionRegistryLite extensionRegistryLite) throws IOException;

    public abstract int readInt32() throws IOException;

    public abstract long readInt64() throws IOException;

    public abstract <T extends MessageLite> T readMessage(Parser<T> parser, ExtensionRegistryLite extensionRegistryLite) throws IOException;

    public abstract void readMessage(MessageLite.Builder builder, ExtensionRegistryLite extensionRegistryLite) throws IOException;

    public abstract byte readRawByte() throws IOException;

    public abstract byte[] readRawBytes(int i) throws IOException;

    public abstract int readRawLittleEndian32() throws IOException;

    public abstract long readRawLittleEndian64() throws IOException;

    public abstract int readRawVarint32() throws IOException;

    public abstract long readRawVarint64() throws IOException;

    abstract long readRawVarint64SlowPath() throws IOException;

    public abstract int readSFixed32() throws IOException;

    public abstract long readSFixed64() throws IOException;

    public abstract int readSInt32() throws IOException;

    public abstract long readSInt64() throws IOException;

    public abstract String readString() throws IOException;

    public abstract String readStringRequireUtf8() throws IOException;

    public abstract int readTag() throws IOException;

    public abstract int readUInt32() throws IOException;

    public abstract long readUInt64() throws IOException;

    @Deprecated
    public abstract void readUnknownGroup(int i, MessageLite.Builder builder) throws IOException;

    public abstract void resetSizeCounter();

    public abstract boolean skipField(int i) throws IOException;

    @Deprecated
    public abstract boolean skipField(int i, CodedOutputStream codedOutputStream) throws IOException;

    public abstract void skipMessage() throws IOException;

    public abstract void skipMessage(CodedOutputStream codedOutputStream) throws IOException;

    public abstract void skipRawBytes(int i) throws IOException;

    public static CodedInputStream newInstance(InputStream input) {
        return newInstance(input, 4096);
    }

    public static CodedInputStream newInstance(InputStream input, int bufferSize) {
        if (bufferSize <= 0) {
            throw new IllegalArgumentException("bufferSize must be > 0");
        }
        if (input == null) {
            return newInstance(Internal.EMPTY_BYTE_ARRAY);
        }
        return new StreamDecoder(input, bufferSize);
    }

    public static CodedInputStream newInstance(Iterable<ByteBuffer> input) {
        if (!UnsafeDirectNioDecoder.isSupported()) {
            return newInstance(new IterableByteBufferInputStream(input));
        }
        return newInstance(input, false);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static CodedInputStream newInstance(Iterable<ByteBuffer> bufs, boolean bufferIsImmutable) {
        int flag = 0;
        int totalSize = 0;
        for (ByteBuffer buf : bufs) {
            totalSize += buf.remaining();
            if (buf.hasArray()) {
                flag |= 1;
            } else if (buf.isDirect()) {
                flag |= 2;
            } else {
                flag |= 4;
            }
        }
        if (flag == 2) {
            return new IterableDirectByteBufferDecoder(bufs, totalSize, bufferIsImmutable);
        }
        return newInstance(new IterableByteBufferInputStream(bufs));
    }

    public static CodedInputStream newInstance(byte[] buf) {
        return newInstance(buf, 0, buf.length);
    }

    public static CodedInputStream newInstance(byte[] buf, int off, int len) {
        return newInstance(buf, off, len, false);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static CodedInputStream newInstance(byte[] buf, int off, int len, boolean bufferIsImmutable) {
        ArrayDecoder result = new ArrayDecoder(buf, off, len, bufferIsImmutable);
        try {
            result.pushLimit(len);
            return result;
        } catch (InvalidProtocolBufferException ex) {
            throw new IllegalArgumentException(ex);
        }
    }

    public static CodedInputStream newInstance(ByteBuffer buf) {
        return newInstance(buf, false);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static CodedInputStream newInstance(ByteBuffer buf, boolean bufferIsImmutable) {
        if (buf.hasArray()) {
            return newInstance(buf.array(), buf.arrayOffset() + buf.position(), buf.remaining(), bufferIsImmutable);
        }
        if (buf.isDirect() && UnsafeDirectNioDecoder.isSupported()) {
            return new UnsafeDirectNioDecoder(buf, bufferIsImmutable);
        }
        byte[] buffer = new byte[buf.remaining()];
        buf.duplicate().get(buffer);
        return newInstance(buffer, 0, buffer.length, true);
    }

    public void checkRecursionLimit() throws InvalidProtocolBufferException {
        if (this.recursionDepth >= this.recursionLimit) {
            throw InvalidProtocolBufferException.recursionLimitExceeded();
        }
    }

    private CodedInputStream() {
        this.recursionLimit = defaultRecursionLimit;
        this.sizeLimit = Integer.MAX_VALUE;
        this.shouldDiscardUnknownFields = false;
    }

    public final int setRecursionLimit(int limit) {
        if (limit < 0) {
            throw new IllegalArgumentException("Recursion limit cannot be negative: " + limit);
        }
        int oldLimit = this.recursionLimit;
        this.recursionLimit = limit;
        return oldLimit;
    }

    public final int setSizeLimit(int limit) {
        if (limit < 0) {
            throw new IllegalArgumentException("Size limit cannot be negative: " + limit);
        }
        int oldLimit = this.sizeLimit;
        this.sizeLimit = limit;
        return oldLimit;
    }

    final void discardUnknownFields() {
        this.shouldDiscardUnknownFields = true;
    }

    final void unsetDiscardUnknownFields() {
        this.shouldDiscardUnknownFields = false;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final boolean shouldDiscardUnknownFields() {
        return this.shouldDiscardUnknownFields;
    }

    public static int decodeZigZag32(int n) {
        return (n >>> 1) ^ (-(n & 1));
    }

    public static long decodeZigZag64(long n) {
        return (n >>> 1) ^ (-(1 & n));
    }

    public static int readRawVarint32(int firstByte, InputStream input) throws IOException {
        if ((firstByte & 128) == 0) {
            return firstByte;
        }
        int result = firstByte & 127;
        int offset = 7;
        while (offset < 32) {
            int b = input.read();
            if (b == -1) {
                throw InvalidProtocolBufferException.truncatedMessage();
            }
            result |= (b & 127) << offset;
            if ((b & 128) != 0) {
                offset += 7;
            } else {
                return result;
            }
        }
        while (offset < 64) {
            int b2 = input.read();
            if (b2 == -1) {
                throw InvalidProtocolBufferException.truncatedMessage();
            }
            if ((b2 & 128) != 0) {
                offset += 7;
            } else {
                return result;
            }
        }
        throw InvalidProtocolBufferException.malformedVarint();
    }

    static int readRawVarint32(InputStream input) throws IOException {
        int firstByte = input.read();
        if (firstByte == -1) {
            throw InvalidProtocolBufferException.truncatedMessage();
        }
        return readRawVarint32(firstByte, input);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes4.dex */
    public static final class ArrayDecoder extends CodedInputStream {
        private final byte[] buffer;
        private int bufferSizeAfterLimit;
        private int currentLimit;
        private boolean enableAliasing;
        private final boolean immutable;
        private int lastTag;
        private int limit;
        private int pos;
        private int startPos;

        private ArrayDecoder(byte[] buffer, int offset, int len, boolean immutable) {
            super();
            this.currentLimit = Integer.MAX_VALUE;
            this.buffer = buffer;
            this.limit = offset + len;
            this.pos = offset;
            this.startPos = offset;
            this.immutable = immutable;
        }

        @Override // com.android.framework.protobuf.CodedInputStream
        public int readTag() throws IOException {
            if (isAtEnd()) {
                this.lastTag = 0;
                return 0;
            }
            int readRawVarint32 = readRawVarint32();
            this.lastTag = readRawVarint32;
            if (WireFormat.getTagFieldNumber(readRawVarint32) == 0) {
                throw InvalidProtocolBufferException.invalidTag();
            }
            return this.lastTag;
        }

        @Override // com.android.framework.protobuf.CodedInputStream
        public void checkLastTagWas(int value) throws InvalidProtocolBufferException {
            if (this.lastTag != value) {
                throw InvalidProtocolBufferException.invalidEndTag();
            }
        }

        @Override // com.android.framework.protobuf.CodedInputStream
        public int getLastTag() {
            return this.lastTag;
        }

        @Override // com.android.framework.protobuf.CodedInputStream
        public boolean skipField(int tag) throws IOException {
            switch (WireFormat.getTagWireType(tag)) {
                case 0:
                    skipRawVarint();
                    return true;
                case 1:
                    skipRawBytes(8);
                    return true;
                case 2:
                    skipRawBytes(readRawVarint32());
                    return true;
                case 3:
                    skipMessage();
                    checkLastTagWas(WireFormat.makeTag(WireFormat.getTagFieldNumber(tag), 4));
                    return true;
                case 4:
                    return false;
                case 5:
                    skipRawBytes(4);
                    return true;
                default:
                    throw InvalidProtocolBufferException.invalidWireType();
            }
        }

        @Override // com.android.framework.protobuf.CodedInputStream
        public boolean skipField(int tag, CodedOutputStream output) throws IOException {
            switch (WireFormat.getTagWireType(tag)) {
                case 0:
                    long value = readInt64();
                    output.writeUInt32NoTag(tag);
                    output.writeUInt64NoTag(value);
                    return true;
                case 1:
                    long value2 = readRawLittleEndian64();
                    output.writeUInt32NoTag(tag);
                    output.writeFixed64NoTag(value2);
                    return true;
                case 2:
                    ByteString value3 = readBytes();
                    output.writeUInt32NoTag(tag);
                    output.writeBytesNoTag(value3);
                    return true;
                case 3:
                    output.writeUInt32NoTag(tag);
                    skipMessage(output);
                    int endtag = WireFormat.makeTag(WireFormat.getTagFieldNumber(tag), 4);
                    checkLastTagWas(endtag);
                    output.writeUInt32NoTag(endtag);
                    return true;
                case 4:
                    return false;
                case 5:
                    int value4 = readRawLittleEndian32();
                    output.writeUInt32NoTag(tag);
                    output.writeFixed32NoTag(value4);
                    return true;
                default:
                    throw InvalidProtocolBufferException.invalidWireType();
            }
        }

        @Override // com.android.framework.protobuf.CodedInputStream
        public void skipMessage() throws IOException {
            int tag;
            do {
                tag = readTag();
                if (tag == 0) {
                    return;
                }
            } while (skipField(tag));
        }

        @Override // com.android.framework.protobuf.CodedInputStream
        public void skipMessage(CodedOutputStream output) throws IOException {
            int tag;
            do {
                tag = readTag();
                if (tag == 0) {
                    return;
                }
            } while (skipField(tag, output));
        }

        @Override // com.android.framework.protobuf.CodedInputStream
        public double readDouble() throws IOException {
            return Double.longBitsToDouble(readRawLittleEndian64());
        }

        @Override // com.android.framework.protobuf.CodedInputStream
        public float readFloat() throws IOException {
            return Float.intBitsToFloat(readRawLittleEndian32());
        }

        @Override // com.android.framework.protobuf.CodedInputStream
        public long readUInt64() throws IOException {
            return readRawVarint64();
        }

        @Override // com.android.framework.protobuf.CodedInputStream
        public long readInt64() throws IOException {
            return readRawVarint64();
        }

        @Override // com.android.framework.protobuf.CodedInputStream
        public int readInt32() throws IOException {
            return readRawVarint32();
        }

        @Override // com.android.framework.protobuf.CodedInputStream
        public long readFixed64() throws IOException {
            return readRawLittleEndian64();
        }

        @Override // com.android.framework.protobuf.CodedInputStream
        public int readFixed32() throws IOException {
            return readRawLittleEndian32();
        }

        @Override // com.android.framework.protobuf.CodedInputStream
        public boolean readBool() throws IOException {
            return readRawVarint64() != 0;
        }

        @Override // com.android.framework.protobuf.CodedInputStream
        public String readString() throws IOException {
            int size = readRawVarint32();
            if (size > 0) {
                int i = this.limit;
                int i2 = this.pos;
                if (size <= i - i2) {
                    String result = new String(this.buffer, i2, size, Internal.UTF_8);
                    this.pos += size;
                    return result;
                }
            }
            if (size == 0) {
                return "";
            }
            if (size < 0) {
                throw InvalidProtocolBufferException.negativeSize();
            }
            throw InvalidProtocolBufferException.truncatedMessage();
        }

        @Override // com.android.framework.protobuf.CodedInputStream
        public String readStringRequireUtf8() throws IOException {
            int size = readRawVarint32();
            if (size > 0) {
                int i = this.limit;
                int i2 = this.pos;
                if (size <= i - i2) {
                    String result = Utf8.decodeUtf8(this.buffer, i2, size);
                    this.pos += size;
                    return result;
                }
            }
            if (size == 0) {
                return "";
            }
            if (size <= 0) {
                throw InvalidProtocolBufferException.negativeSize();
            }
            throw InvalidProtocolBufferException.truncatedMessage();
        }

        @Override // com.android.framework.protobuf.CodedInputStream
        public void readGroup(int fieldNumber, MessageLite.Builder builder, ExtensionRegistryLite extensionRegistry) throws IOException {
            checkRecursionLimit();
            this.recursionDepth++;
            builder.mergeFrom(this, extensionRegistry);
            checkLastTagWas(WireFormat.makeTag(fieldNumber, 4));
            this.recursionDepth--;
        }

        @Override // com.android.framework.protobuf.CodedInputStream
        public <T extends MessageLite> T readGroup(int fieldNumber, Parser<T> parser, ExtensionRegistryLite extensionRegistry) throws IOException {
            checkRecursionLimit();
            this.recursionDepth++;
            T result = parser.parsePartialFrom(this, extensionRegistry);
            checkLastTagWas(WireFormat.makeTag(fieldNumber, 4));
            this.recursionDepth--;
            return result;
        }

        @Override // com.android.framework.protobuf.CodedInputStream
        @Deprecated
        public void readUnknownGroup(int fieldNumber, MessageLite.Builder builder) throws IOException {
            readGroup(fieldNumber, builder, ExtensionRegistryLite.getEmptyRegistry());
        }

        @Override // com.android.framework.protobuf.CodedInputStream
        public void readMessage(MessageLite.Builder builder, ExtensionRegistryLite extensionRegistry) throws IOException {
            int length = readRawVarint32();
            checkRecursionLimit();
            int oldLimit = pushLimit(length);
            this.recursionDepth++;
            builder.mergeFrom(this, extensionRegistry);
            checkLastTagWas(0);
            this.recursionDepth--;
            if (getBytesUntilLimit() != 0) {
                throw InvalidProtocolBufferException.truncatedMessage();
            }
            popLimit(oldLimit);
        }

        @Override // com.android.framework.protobuf.CodedInputStream
        public <T extends MessageLite> T readMessage(Parser<T> parser, ExtensionRegistryLite extensionRegistry) throws IOException {
            int length = readRawVarint32();
            checkRecursionLimit();
            int oldLimit = pushLimit(length);
            this.recursionDepth++;
            T result = parser.parsePartialFrom(this, extensionRegistry);
            checkLastTagWas(0);
            this.recursionDepth--;
            if (getBytesUntilLimit() != 0) {
                throw InvalidProtocolBufferException.truncatedMessage();
            }
            popLimit(oldLimit);
            return result;
        }

        @Override // com.android.framework.protobuf.CodedInputStream
        public ByteString readBytes() throws IOException {
            ByteString result;
            int size = readRawVarint32();
            if (size > 0) {
                int i = this.limit;
                int i2 = this.pos;
                if (size <= i - i2) {
                    if (this.immutable && this.enableAliasing) {
                        result = ByteString.wrap(this.buffer, i2, size);
                    } else {
                        result = ByteString.copyFrom(this.buffer, i2, size);
                    }
                    this.pos += size;
                    return result;
                }
            }
            if (size == 0) {
                return ByteString.EMPTY;
            }
            return ByteString.wrap(readRawBytes(size));
        }

        @Override // com.android.framework.protobuf.CodedInputStream
        public byte[] readByteArray() throws IOException {
            int size = readRawVarint32();
            return readRawBytes(size);
        }

        @Override // com.android.framework.protobuf.CodedInputStream
        public ByteBuffer readByteBuffer() throws IOException {
            ByteBuffer result;
            int size = readRawVarint32();
            if (size > 0) {
                int i = this.limit;
                int i2 = this.pos;
                if (size <= i - i2) {
                    if (!this.immutable && this.enableAliasing) {
                        result = ByteBuffer.wrap(this.buffer, i2, size).slice();
                    } else {
                        result = ByteBuffer.wrap(Arrays.copyOfRange(this.buffer, i2, i2 + size));
                    }
                    this.pos += size;
                    return result;
                }
            }
            if (size == 0) {
                return Internal.EMPTY_BYTE_BUFFER;
            }
            if (size < 0) {
                throw InvalidProtocolBufferException.negativeSize();
            }
            throw InvalidProtocolBufferException.truncatedMessage();
        }

        @Override // com.android.framework.protobuf.CodedInputStream
        public int readUInt32() throws IOException {
            return readRawVarint32();
        }

        @Override // com.android.framework.protobuf.CodedInputStream
        public int readEnum() throws IOException {
            return readRawVarint32();
        }

        @Override // com.android.framework.protobuf.CodedInputStream
        public int readSFixed32() throws IOException {
            return readRawLittleEndian32();
        }

        @Override // com.android.framework.protobuf.CodedInputStream
        public long readSFixed64() throws IOException {
            return readRawLittleEndian64();
        }

        @Override // com.android.framework.protobuf.CodedInputStream
        public int readSInt32() throws IOException {
            return decodeZigZag32(readRawVarint32());
        }

        @Override // com.android.framework.protobuf.CodedInputStream
        public long readSInt64() throws IOException {
            return decodeZigZag64(readRawVarint64());
        }

        /* JADX WARN: Code restructure failed: missing block: B:32:0x006f, code lost:
            if (r2[r1] < 0) goto L35;
         */
        @Override // com.android.framework.protobuf.CodedInputStream
        /*
            Code decompiled incorrectly, please refer to instructions dump.
        */
        public int readRawVarint32() throws IOException {
            int tempPos;
            int tempPos2 = this.pos;
            int i = this.limit;
            if (i != tempPos2) {
                byte[] buffer = this.buffer;
                int tempPos3 = tempPos2 + 1;
                int x = buffer[tempPos2];
                if (x >= 0) {
                    this.pos = tempPos3;
                    return x;
                } else if (i - tempPos3 >= 9) {
                    int tempPos4 = tempPos3 + 1;
                    int x2 = (buffer[tempPos3] << 7) ^ x;
                    if (x2 < 0) {
                        tempPos = x2 ^ (-128);
                    } else {
                        int x3 = tempPos4 + 1;
                        int x4 = (buffer[tempPos4] << 14) ^ x2;
                        if (x4 >= 0) {
                            tempPos = x4 ^ 16256;
                            tempPos4 = x3;
                        } else {
                            tempPos4 = x3 + 1;
                            int x5 = (buffer[x3] << 21) ^ x4;
                            if (x5 < 0) {
                                tempPos = (-2080896) ^ x5;
                            } else {
                                int tempPos5 = tempPos4 + 1;
                                int y = buffer[tempPos4];
                                int x6 = (x5 ^ (y << 28)) ^ 266354560;
                                if (y < 0) {
                                    int tempPos6 = tempPos5 + 1;
                                    if (buffer[tempPos5] < 0) {
                                        tempPos5 = tempPos6 + 1;
                                        if (buffer[tempPos6] < 0) {
                                            tempPos6 = tempPos5 + 1;
                                            if (buffer[tempPos5] < 0) {
                                                tempPos5 = tempPos6 + 1;
                                                if (buffer[tempPos6] < 0) {
                                                    tempPos6 = tempPos5 + 1;
                                                }
                                            }
                                        }
                                    }
                                    tempPos = x6;
                                    tempPos4 = tempPos6;
                                }
                                tempPos4 = tempPos5;
                                tempPos = x6;
                            }
                        }
                    }
                    this.pos = tempPos4;
                    return tempPos;
                }
            }
            return (int) readRawVarint64SlowPath();
        }

        private void skipRawVarint() throws IOException {
            if (this.limit - this.pos >= 10) {
                skipRawVarintFastPath();
            } else {
                skipRawVarintSlowPath();
            }
        }

        private void skipRawVarintFastPath() throws IOException {
            for (int i = 0; i < 10; i++) {
                byte[] bArr = this.buffer;
                int i2 = this.pos;
                this.pos = i2 + 1;
                if (bArr[i2] >= 0) {
                    return;
                }
            }
            throw InvalidProtocolBufferException.malformedVarint();
        }

        private void skipRawVarintSlowPath() throws IOException {
            for (int i = 0; i < 10; i++) {
                if (readRawByte() >= 0) {
                    return;
                }
            }
            throw InvalidProtocolBufferException.malformedVarint();
        }

        /* JADX WARN: Code restructure failed: missing block: B:36:0x00bd, code lost:
            if (r2[r1] < 0) goto L38;
         */
        @Override // com.android.framework.protobuf.CodedInputStream
        /*
            Code decompiled incorrectly, please refer to instructions dump.
        */
        public long readRawVarint64() throws IOException {
            long x;
            int tempPos = this.pos;
            int i = this.limit;
            if (i != tempPos) {
                byte[] buffer = this.buffer;
                int tempPos2 = tempPos + 1;
                int y = buffer[tempPos];
                if (y >= 0) {
                    this.pos = tempPos2;
                    return y;
                } else if (i - tempPos2 >= 9) {
                    int tempPos3 = tempPos2 + 1;
                    int y2 = (buffer[tempPos2] << 7) ^ y;
                    if (y2 < 0) {
                        x = y2 ^ (-128);
                    } else {
                        int tempPos4 = tempPos3 + 1;
                        int y3 = (buffer[tempPos3] << 14) ^ y2;
                        if (y3 >= 0) {
                            x = y3 ^ 16256;
                            tempPos3 = tempPos4;
                        } else {
                            tempPos3 = tempPos4 + 1;
                            int y4 = (buffer[tempPos4] << 21) ^ y3;
                            if (y4 < 0) {
                                x = (-2080896) ^ y4;
                            } else {
                                long x2 = y4;
                                int tempPos5 = tempPos3 + 1;
                                long x3 = x2 ^ (buffer[tempPos3] << 28);
                                if (x3 >= 0) {
                                    x = 266354560 ^ x3;
                                    tempPos3 = tempPos5;
                                } else {
                                    tempPos3 = tempPos5 + 1;
                                    long x4 = (buffer[tempPos5] << 35) ^ x3;
                                    if (x4 < 0) {
                                        x = (-34093383808L) ^ x4;
                                    } else {
                                        int tempPos6 = tempPos3 + 1;
                                        long x5 = (buffer[tempPos3] << 42) ^ x4;
                                        if (x5 >= 0) {
                                            x = 4363953127296L ^ x5;
                                            tempPos3 = tempPos6;
                                        } else {
                                            tempPos3 = tempPos6 + 1;
                                            long x6 = (buffer[tempPos6] << 49) ^ x5;
                                            if (x6 < 0) {
                                                x = (-558586000294016L) ^ x6;
                                            } else {
                                                int tempPos7 = tempPos3 + 1;
                                                x = ((buffer[tempPos3] << 56) ^ x6) ^ 71499008037633920L;
                                                if (x >= 0) {
                                                    tempPos3 = tempPos7;
                                                } else {
                                                    tempPos3 = tempPos7 + 1;
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    this.pos = tempPos3;
                    return x;
                }
            }
            return readRawVarint64SlowPath();
        }

        @Override // com.android.framework.protobuf.CodedInputStream
        long readRawVarint64SlowPath() throws IOException {
            long result = 0;
            for (int shift = 0; shift < 64; shift += 7) {
                byte b = readRawByte();
                result |= (b & Byte.MAX_VALUE) << shift;
                if ((b & 128) == 0) {
                    return result;
                }
            }
            throw InvalidProtocolBufferException.malformedVarint();
        }

        @Override // com.android.framework.protobuf.CodedInputStream
        public int readRawLittleEndian32() throws IOException {
            int tempPos = this.pos;
            if (this.limit - tempPos < 4) {
                throw InvalidProtocolBufferException.truncatedMessage();
            }
            byte[] buffer = this.buffer;
            this.pos = tempPos + 4;
            return (buffer[tempPos] & 255) | ((buffer[tempPos + 1] & 255) << 8) | ((buffer[tempPos + 2] & 255) << 16) | ((buffer[tempPos + 3] & 255) << 24);
        }

        @Override // com.android.framework.protobuf.CodedInputStream
        public long readRawLittleEndian64() throws IOException {
            int tempPos = this.pos;
            if (this.limit - tempPos < 8) {
                throw InvalidProtocolBufferException.truncatedMessage();
            }
            byte[] buffer = this.buffer;
            this.pos = tempPos + 8;
            return (buffer[tempPos] & 255) | ((buffer[tempPos + 1] & 255) << 8) | ((buffer[tempPos + 2] & 255) << 16) | ((buffer[tempPos + 3] & 255) << 24) | ((buffer[tempPos + 4] & 255) << 32) | ((buffer[tempPos + 5] & 255) << 40) | ((buffer[tempPos + 6] & 255) << 48) | ((buffer[tempPos + 7] & 255) << 56);
        }

        @Override // com.android.framework.protobuf.CodedInputStream
        public void enableAliasing(boolean enabled) {
            this.enableAliasing = enabled;
        }

        @Override // com.android.framework.protobuf.CodedInputStream
        public void resetSizeCounter() {
            this.startPos = this.pos;
        }

        @Override // com.android.framework.protobuf.CodedInputStream
        public int pushLimit(int byteLimit) throws InvalidProtocolBufferException {
            if (byteLimit < 0) {
                throw InvalidProtocolBufferException.negativeSize();
            }
            int byteLimit2 = byteLimit + getTotalBytesRead();
            if (byteLimit2 < 0) {
                throw InvalidProtocolBufferException.parseFailure();
            }
            int oldLimit = this.currentLimit;
            if (byteLimit2 > oldLimit) {
                throw InvalidProtocolBufferException.truncatedMessage();
            }
            this.currentLimit = byteLimit2;
            recomputeBufferSizeAfterLimit();
            return oldLimit;
        }

        private void recomputeBufferSizeAfterLimit() {
            int i = this.limit + this.bufferSizeAfterLimit;
            this.limit = i;
            int bufferEnd = i - this.startPos;
            int i2 = this.currentLimit;
            if (bufferEnd > i2) {
                int i3 = bufferEnd - i2;
                this.bufferSizeAfterLimit = i3;
                this.limit = i - i3;
                return;
            }
            this.bufferSizeAfterLimit = 0;
        }

        @Override // com.android.framework.protobuf.CodedInputStream
        public void popLimit(int oldLimit) {
            this.currentLimit = oldLimit;
            recomputeBufferSizeAfterLimit();
        }

        @Override // com.android.framework.protobuf.CodedInputStream
        public int getBytesUntilLimit() {
            int i = this.currentLimit;
            if (i == Integer.MAX_VALUE) {
                return -1;
            }
            return i - getTotalBytesRead();
        }

        @Override // com.android.framework.protobuf.CodedInputStream
        public boolean isAtEnd() throws IOException {
            return this.pos == this.limit;
        }

        @Override // com.android.framework.protobuf.CodedInputStream
        public int getTotalBytesRead() {
            return this.pos - this.startPos;
        }

        @Override // com.android.framework.protobuf.CodedInputStream
        public byte readRawByte() throws IOException {
            int i = this.pos;
            if (i == this.limit) {
                throw InvalidProtocolBufferException.truncatedMessage();
            }
            byte[] bArr = this.buffer;
            this.pos = i + 1;
            return bArr[i];
        }

        @Override // com.android.framework.protobuf.CodedInputStream
        public byte[] readRawBytes(int length) throws IOException {
            if (length > 0) {
                int i = this.limit;
                int i2 = this.pos;
                if (length <= i - i2) {
                    int tempPos = this.pos;
                    int i3 = i2 + length;
                    this.pos = i3;
                    return Arrays.copyOfRange(this.buffer, tempPos, i3);
                }
            }
            if (length <= 0) {
                if (length == 0) {
                    return Internal.EMPTY_BYTE_ARRAY;
                }
                throw InvalidProtocolBufferException.negativeSize();
            }
            throw InvalidProtocolBufferException.truncatedMessage();
        }

        @Override // com.android.framework.protobuf.CodedInputStream
        public void skipRawBytes(int length) throws IOException {
            if (length >= 0) {
                int i = this.limit;
                int i2 = this.pos;
                if (length <= i - i2) {
                    this.pos = i2 + length;
                    return;
                }
            }
            if (length < 0) {
                throw InvalidProtocolBufferException.negativeSize();
            }
            throw InvalidProtocolBufferException.truncatedMessage();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes4.dex */
    public static final class UnsafeDirectNioDecoder extends CodedInputStream {
        private final long address;
        private final ByteBuffer buffer;
        private int bufferSizeAfterLimit;
        private int currentLimit;
        private boolean enableAliasing;
        private final boolean immutable;
        private int lastTag;
        private long limit;
        private long pos;
        private long startPos;

        static boolean isSupported() {
            return UnsafeUtil.hasUnsafeByteBufferOperations();
        }

        private UnsafeDirectNioDecoder(ByteBuffer buffer, boolean immutable) {
            super();
            this.currentLimit = Integer.MAX_VALUE;
            this.buffer = buffer;
            long addressOffset = UnsafeUtil.addressOffset(buffer);
            this.address = addressOffset;
            this.limit = buffer.limit() + addressOffset;
            long position = addressOffset + buffer.position();
            this.pos = position;
            this.startPos = position;
            this.immutable = immutable;
        }

        @Override // com.android.framework.protobuf.CodedInputStream
        public int readTag() throws IOException {
            if (isAtEnd()) {
                this.lastTag = 0;
                return 0;
            }
            int readRawVarint32 = readRawVarint32();
            this.lastTag = readRawVarint32;
            if (WireFormat.getTagFieldNumber(readRawVarint32) == 0) {
                throw InvalidProtocolBufferException.invalidTag();
            }
            return this.lastTag;
        }

        @Override // com.android.framework.protobuf.CodedInputStream
        public void checkLastTagWas(int value) throws InvalidProtocolBufferException {
            if (this.lastTag != value) {
                throw InvalidProtocolBufferException.invalidEndTag();
            }
        }

        @Override // com.android.framework.protobuf.CodedInputStream
        public int getLastTag() {
            return this.lastTag;
        }

        @Override // com.android.framework.protobuf.CodedInputStream
        public boolean skipField(int tag) throws IOException {
            switch (WireFormat.getTagWireType(tag)) {
                case 0:
                    skipRawVarint();
                    return true;
                case 1:
                    skipRawBytes(8);
                    return true;
                case 2:
                    skipRawBytes(readRawVarint32());
                    return true;
                case 3:
                    skipMessage();
                    checkLastTagWas(WireFormat.makeTag(WireFormat.getTagFieldNumber(tag), 4));
                    return true;
                case 4:
                    return false;
                case 5:
                    skipRawBytes(4);
                    return true;
                default:
                    throw InvalidProtocolBufferException.invalidWireType();
            }
        }

        @Override // com.android.framework.protobuf.CodedInputStream
        public boolean skipField(int tag, CodedOutputStream output) throws IOException {
            switch (WireFormat.getTagWireType(tag)) {
                case 0:
                    long value = readInt64();
                    output.writeUInt32NoTag(tag);
                    output.writeUInt64NoTag(value);
                    return true;
                case 1:
                    long value2 = readRawLittleEndian64();
                    output.writeUInt32NoTag(tag);
                    output.writeFixed64NoTag(value2);
                    return true;
                case 2:
                    ByteString value3 = readBytes();
                    output.writeUInt32NoTag(tag);
                    output.writeBytesNoTag(value3);
                    return true;
                case 3:
                    output.writeUInt32NoTag(tag);
                    skipMessage(output);
                    int endtag = WireFormat.makeTag(WireFormat.getTagFieldNumber(tag), 4);
                    checkLastTagWas(endtag);
                    output.writeUInt32NoTag(endtag);
                    return true;
                case 4:
                    return false;
                case 5:
                    int value4 = readRawLittleEndian32();
                    output.writeUInt32NoTag(tag);
                    output.writeFixed32NoTag(value4);
                    return true;
                default:
                    throw InvalidProtocolBufferException.invalidWireType();
            }
        }

        @Override // com.android.framework.protobuf.CodedInputStream
        public void skipMessage() throws IOException {
            int tag;
            do {
                tag = readTag();
                if (tag == 0) {
                    return;
                }
            } while (skipField(tag));
        }

        @Override // com.android.framework.protobuf.CodedInputStream
        public void skipMessage(CodedOutputStream output) throws IOException {
            int tag;
            do {
                tag = readTag();
                if (tag == 0) {
                    return;
                }
            } while (skipField(tag, output));
        }

        @Override // com.android.framework.protobuf.CodedInputStream
        public double readDouble() throws IOException {
            return Double.longBitsToDouble(readRawLittleEndian64());
        }

        @Override // com.android.framework.protobuf.CodedInputStream
        public float readFloat() throws IOException {
            return Float.intBitsToFloat(readRawLittleEndian32());
        }

        @Override // com.android.framework.protobuf.CodedInputStream
        public long readUInt64() throws IOException {
            return readRawVarint64();
        }

        @Override // com.android.framework.protobuf.CodedInputStream
        public long readInt64() throws IOException {
            return readRawVarint64();
        }

        @Override // com.android.framework.protobuf.CodedInputStream
        public int readInt32() throws IOException {
            return readRawVarint32();
        }

        @Override // com.android.framework.protobuf.CodedInputStream
        public long readFixed64() throws IOException {
            return readRawLittleEndian64();
        }

        @Override // com.android.framework.protobuf.CodedInputStream
        public int readFixed32() throws IOException {
            return readRawLittleEndian32();
        }

        @Override // com.android.framework.protobuf.CodedInputStream
        public boolean readBool() throws IOException {
            return readRawVarint64() != 0;
        }

        @Override // com.android.framework.protobuf.CodedInputStream
        public String readString() throws IOException {
            int size = readRawVarint32();
            if (size > 0 && size <= remaining()) {
                byte[] bytes = new byte[size];
                UnsafeUtil.copyMemory(this.pos, bytes, 0L, size);
                String result = new String(bytes, Internal.UTF_8);
                this.pos += size;
                return result;
            } else if (size == 0) {
                return "";
            } else {
                if (size < 0) {
                    throw InvalidProtocolBufferException.negativeSize();
                }
                throw InvalidProtocolBufferException.truncatedMessage();
            }
        }

        @Override // com.android.framework.protobuf.CodedInputStream
        public String readStringRequireUtf8() throws IOException {
            int size = readRawVarint32();
            if (size > 0 && size <= remaining()) {
                int bufferPos = bufferPos(this.pos);
                String result = Utf8.decodeUtf8(this.buffer, bufferPos, size);
                this.pos += size;
                return result;
            } else if (size == 0) {
                return "";
            } else {
                if (size <= 0) {
                    throw InvalidProtocolBufferException.negativeSize();
                }
                throw InvalidProtocolBufferException.truncatedMessage();
            }
        }

        @Override // com.android.framework.protobuf.CodedInputStream
        public void readGroup(int fieldNumber, MessageLite.Builder builder, ExtensionRegistryLite extensionRegistry) throws IOException {
            checkRecursionLimit();
            this.recursionDepth++;
            builder.mergeFrom(this, extensionRegistry);
            checkLastTagWas(WireFormat.makeTag(fieldNumber, 4));
            this.recursionDepth--;
        }

        @Override // com.android.framework.protobuf.CodedInputStream
        public <T extends MessageLite> T readGroup(int fieldNumber, Parser<T> parser, ExtensionRegistryLite extensionRegistry) throws IOException {
            checkRecursionLimit();
            this.recursionDepth++;
            T result = parser.parsePartialFrom(this, extensionRegistry);
            checkLastTagWas(WireFormat.makeTag(fieldNumber, 4));
            this.recursionDepth--;
            return result;
        }

        @Override // com.android.framework.protobuf.CodedInputStream
        @Deprecated
        public void readUnknownGroup(int fieldNumber, MessageLite.Builder builder) throws IOException {
            readGroup(fieldNumber, builder, ExtensionRegistryLite.getEmptyRegistry());
        }

        @Override // com.android.framework.protobuf.CodedInputStream
        public void readMessage(MessageLite.Builder builder, ExtensionRegistryLite extensionRegistry) throws IOException {
            int length = readRawVarint32();
            checkRecursionLimit();
            int oldLimit = pushLimit(length);
            this.recursionDepth++;
            builder.mergeFrom(this, extensionRegistry);
            checkLastTagWas(0);
            this.recursionDepth--;
            if (getBytesUntilLimit() != 0) {
                throw InvalidProtocolBufferException.truncatedMessage();
            }
            popLimit(oldLimit);
        }

        @Override // com.android.framework.protobuf.CodedInputStream
        public <T extends MessageLite> T readMessage(Parser<T> parser, ExtensionRegistryLite extensionRegistry) throws IOException {
            int length = readRawVarint32();
            checkRecursionLimit();
            int oldLimit = pushLimit(length);
            this.recursionDepth++;
            T result = parser.parsePartialFrom(this, extensionRegistry);
            checkLastTagWas(0);
            this.recursionDepth--;
            if (getBytesUntilLimit() != 0) {
                throw InvalidProtocolBufferException.truncatedMessage();
            }
            popLimit(oldLimit);
            return result;
        }

        @Override // com.android.framework.protobuf.CodedInputStream
        public ByteString readBytes() throws IOException {
            int size = readRawVarint32();
            if (size > 0 && size <= remaining()) {
                if (this.immutable && this.enableAliasing) {
                    long j = this.pos;
                    ByteBuffer result = slice(j, size + j);
                    this.pos += size;
                    return ByteString.wrap(result);
                }
                byte[] bytes = new byte[size];
                UnsafeUtil.copyMemory(this.pos, bytes, 0L, size);
                this.pos += size;
                return ByteString.wrap(bytes);
            } else if (size == 0) {
                return ByteString.EMPTY;
            } else {
                if (size < 0) {
                    throw InvalidProtocolBufferException.negativeSize();
                }
                throw InvalidProtocolBufferException.truncatedMessage();
            }
        }

        @Override // com.android.framework.protobuf.CodedInputStream
        public byte[] readByteArray() throws IOException {
            return readRawBytes(readRawVarint32());
        }

        @Override // com.android.framework.protobuf.CodedInputStream
        public ByteBuffer readByteBuffer() throws IOException {
            int size = readRawVarint32();
            if (size > 0 && size <= remaining()) {
                if (!this.immutable && this.enableAliasing) {
                    long j = this.pos;
                    ByteBuffer result = slice(j, size + j);
                    this.pos += size;
                    return result;
                }
                byte[] bytes = new byte[size];
                UnsafeUtil.copyMemory(this.pos, bytes, 0L, size);
                this.pos += size;
                return ByteBuffer.wrap(bytes);
            } else if (size == 0) {
                return Internal.EMPTY_BYTE_BUFFER;
            } else {
                if (size < 0) {
                    throw InvalidProtocolBufferException.negativeSize();
                }
                throw InvalidProtocolBufferException.truncatedMessage();
            }
        }

        @Override // com.android.framework.protobuf.CodedInputStream
        public int readUInt32() throws IOException {
            return readRawVarint32();
        }

        @Override // com.android.framework.protobuf.CodedInputStream
        public int readEnum() throws IOException {
            return readRawVarint32();
        }

        @Override // com.android.framework.protobuf.CodedInputStream
        public int readSFixed32() throws IOException {
            return readRawLittleEndian32();
        }

        @Override // com.android.framework.protobuf.CodedInputStream
        public long readSFixed64() throws IOException {
            return readRawLittleEndian64();
        }

        @Override // com.android.framework.protobuf.CodedInputStream
        public int readSInt32() throws IOException {
            return decodeZigZag32(readRawVarint32());
        }

        @Override // com.android.framework.protobuf.CodedInputStream
        public long readSInt64() throws IOException {
            return decodeZigZag64(readRawVarint64());
        }

        @Override // com.android.framework.protobuf.CodedInputStream
        public int readRawVarint32() throws IOException {
            int y;
            long tempPos = this.pos;
            if (this.limit != tempPos) {
                long tempPos2 = tempPos + 1;
                int x = UnsafeUtil.getByte(tempPos);
                if (x >= 0) {
                    this.pos = tempPos2;
                    return x;
                } else if (this.limit - tempPos2 >= 9) {
                    long tempPos3 = tempPos2 + 1;
                    int x2 = (UnsafeUtil.getByte(tempPos2) << 7) ^ x;
                    if (x2 < 0) {
                        y = x2 ^ (-128);
                    } else {
                        long tempPos4 = tempPos3 + 1;
                        int x3 = (UnsafeUtil.getByte(tempPos3) << AcquiredInfo.POWER_PRESS) ^ x2;
                        if (x3 >= 0) {
                            y = x3 ^ 16256;
                            tempPos3 = tempPos4;
                        } else {
                            tempPos3 = tempPos4 + 1;
                            int x4 = (UnsafeUtil.getByte(tempPos4) << 21) ^ x3;
                            if (x4 < 0) {
                                y = (-2080896) ^ x4;
                            } else {
                                long tempPos5 = tempPos3 + 1;
                                int y2 = UnsafeUtil.getByte(tempPos3);
                                int x5 = (x4 ^ (y2 << 28)) ^ 266354560;
                                if (y2 < 0) {
                                    tempPos3 = tempPos5 + 1;
                                    if (UnsafeUtil.getByte(tempPos5) < 0) {
                                        long tempPos6 = tempPos3 + 1;
                                        if (UnsafeUtil.getByte(tempPos3) < 0) {
                                            tempPos3 = tempPos6 + 1;
                                            if (UnsafeUtil.getByte(tempPos6) < 0) {
                                                long tempPos7 = tempPos3 + 1;
                                                if (UnsafeUtil.getByte(tempPos3) < 0) {
                                                    tempPos3 = tempPos7 + 1;
                                                    if (UnsafeUtil.getByte(tempPos7) >= 0) {
                                                        y = x5;
                                                    }
                                                } else {
                                                    y = x5;
                                                    tempPos3 = tempPos7;
                                                }
                                            } else {
                                                y = x5;
                                            }
                                        } else {
                                            y = x5;
                                            tempPos3 = tempPos6;
                                        }
                                    } else {
                                        y = x5;
                                    }
                                } else {
                                    y = x5;
                                    tempPos3 = tempPos5;
                                }
                            }
                        }
                    }
                    this.pos = tempPos3;
                    return y;
                }
            }
            return (int) readRawVarint64SlowPath();
        }

        private void skipRawVarint() throws IOException {
            if (remaining() >= 10) {
                skipRawVarintFastPath();
            } else {
                skipRawVarintSlowPath();
            }
        }

        private void skipRawVarintFastPath() throws IOException {
            for (int i = 0; i < 10; i++) {
                long j = this.pos;
                this.pos = 1 + j;
                if (UnsafeUtil.getByte(j) >= 0) {
                    return;
                }
            }
            throw InvalidProtocolBufferException.malformedVarint();
        }

        private void skipRawVarintSlowPath() throws IOException {
            for (int i = 0; i < 10; i++) {
                if (readRawByte() >= 0) {
                    return;
                }
            }
            throw InvalidProtocolBufferException.malformedVarint();
        }

        @Override // com.android.framework.protobuf.CodedInputStream
        public long readRawVarint64() throws IOException {
            long x;
            long tempPos = this.pos;
            if (this.limit != tempPos) {
                long tempPos2 = tempPos + 1;
                int y = UnsafeUtil.getByte(tempPos);
                if (y >= 0) {
                    this.pos = tempPos2;
                    return y;
                } else if (this.limit - tempPos2 >= 9) {
                    long tempPos3 = tempPos2 + 1;
                    int y2 = (UnsafeUtil.getByte(tempPos2) << 7) ^ y;
                    if (y2 < 0) {
                        x = y2 ^ (-128);
                    } else {
                        long tempPos4 = tempPos3 + 1;
                        int y3 = (UnsafeUtil.getByte(tempPos3) << AcquiredInfo.POWER_PRESS) ^ y2;
                        if (y3 >= 0) {
                            x = y3 ^ 16256;
                            tempPos3 = tempPos4;
                        } else {
                            tempPos3 = tempPos4 + 1;
                            int y4 = (UnsafeUtil.getByte(tempPos4) << 21) ^ y3;
                            if (y4 >= 0) {
                                long tempPos5 = tempPos3 + 1;
                                long x2 = y4 ^ (UnsafeUtil.getByte(tempPos3) << 28);
                                if (x2 >= 0) {
                                    x = 266354560 ^ x2;
                                    tempPos3 = tempPos5;
                                } else {
                                    long tempPos6 = tempPos5 + 1;
                                    long x3 = (UnsafeUtil.getByte(tempPos5) << 35) ^ x2;
                                    if (x3 < 0) {
                                        x = (-34093383808L) ^ x3;
                                        tempPos3 = tempPos6;
                                    } else {
                                        long tempPos7 = tempPos6 + 1;
                                        long x4 = (UnsafeUtil.getByte(tempPos6) << 42) ^ x3;
                                        if (x4 >= 0) {
                                            x = 4363953127296L ^ x4;
                                            tempPos3 = tempPos7;
                                        } else {
                                            long tempPos8 = tempPos7 + 1;
                                            long x5 = (UnsafeUtil.getByte(tempPos7) << 49) ^ x4;
                                            if (x5 < 0) {
                                                x = (-558586000294016L) ^ x5;
                                                tempPos3 = tempPos8;
                                            } else {
                                                long tempPos9 = tempPos8 + 1;
                                                long x6 = ((UnsafeUtil.getByte(tempPos8) << 56) ^ x5) ^ 71499008037633920L;
                                                if (x6 >= 0) {
                                                    x = x6;
                                                    tempPos3 = tempPos9;
                                                } else {
                                                    tempPos3 = tempPos9 + 1;
                                                    if (UnsafeUtil.getByte(tempPos9) >= 0) {
                                                        x = x6;
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            } else {
                                x = (-2080896) ^ y4;
                            }
                        }
                    }
                    this.pos = tempPos3;
                    return x;
                }
            }
            return readRawVarint64SlowPath();
        }

        @Override // com.android.framework.protobuf.CodedInputStream
        long readRawVarint64SlowPath() throws IOException {
            long result = 0;
            for (int shift = 0; shift < 64; shift += 7) {
                byte b = readRawByte();
                result |= (b & Byte.MAX_VALUE) << shift;
                if ((b & 128) == 0) {
                    return result;
                }
            }
            throw InvalidProtocolBufferException.malformedVarint();
        }

        @Override // com.android.framework.protobuf.CodedInputStream
        public int readRawLittleEndian32() throws IOException {
            long tempPos = this.pos;
            if (this.limit - tempPos < 4) {
                throw InvalidProtocolBufferException.truncatedMessage();
            }
            this.pos = 4 + tempPos;
            return (UnsafeUtil.getByte(tempPos) & 255) | ((UnsafeUtil.getByte(1 + tempPos) & 255) << 8) | ((UnsafeUtil.getByte(2 + tempPos) & 255) << 16) | ((UnsafeUtil.getByte(3 + tempPos) & 255) << 24);
        }

        @Override // com.android.framework.protobuf.CodedInputStream
        public long readRawLittleEndian64() throws IOException {
            long tempPos = this.pos;
            if (this.limit - tempPos < 8) {
                throw InvalidProtocolBufferException.truncatedMessage();
            }
            this.pos = 8 + tempPos;
            return (UnsafeUtil.getByte(tempPos) & 255) | ((UnsafeUtil.getByte(1 + tempPos) & 255) << 8) | ((UnsafeUtil.getByte(2 + tempPos) & 255) << 16) | ((UnsafeUtil.getByte(3 + tempPos) & 255) << 24) | ((UnsafeUtil.getByte(4 + tempPos) & 255) << 32) | ((UnsafeUtil.getByte(5 + tempPos) & 255) << 40) | ((UnsafeUtil.getByte(6 + tempPos) & 255) << 48) | ((255 & UnsafeUtil.getByte(7 + tempPos)) << 56);
        }

        @Override // com.android.framework.protobuf.CodedInputStream
        public void enableAliasing(boolean enabled) {
            this.enableAliasing = enabled;
        }

        @Override // com.android.framework.protobuf.CodedInputStream
        public void resetSizeCounter() {
            this.startPos = this.pos;
        }

        @Override // com.android.framework.protobuf.CodedInputStream
        public int pushLimit(int byteLimit) throws InvalidProtocolBufferException {
            if (byteLimit < 0) {
                throw InvalidProtocolBufferException.negativeSize();
            }
            int byteLimit2 = byteLimit + getTotalBytesRead();
            int oldLimit = this.currentLimit;
            if (byteLimit2 > oldLimit) {
                throw InvalidProtocolBufferException.truncatedMessage();
            }
            this.currentLimit = byteLimit2;
            recomputeBufferSizeAfterLimit();
            return oldLimit;
        }

        @Override // com.android.framework.protobuf.CodedInputStream
        public void popLimit(int oldLimit) {
            this.currentLimit = oldLimit;
            recomputeBufferSizeAfterLimit();
        }

        @Override // com.android.framework.protobuf.CodedInputStream
        public int getBytesUntilLimit() {
            int i = this.currentLimit;
            if (i == Integer.MAX_VALUE) {
                return -1;
            }
            return i - getTotalBytesRead();
        }

        @Override // com.android.framework.protobuf.CodedInputStream
        public boolean isAtEnd() throws IOException {
            return this.pos == this.limit;
        }

        @Override // com.android.framework.protobuf.CodedInputStream
        public int getTotalBytesRead() {
            return (int) (this.pos - this.startPos);
        }

        @Override // com.android.framework.protobuf.CodedInputStream
        public byte readRawByte() throws IOException {
            long j = this.pos;
            if (j == this.limit) {
                throw InvalidProtocolBufferException.truncatedMessage();
            }
            this.pos = 1 + j;
            return UnsafeUtil.getByte(j);
        }

        @Override // com.android.framework.protobuf.CodedInputStream
        public byte[] readRawBytes(int length) throws IOException {
            if (length >= 0 && length <= remaining()) {
                byte[] bytes = new byte[length];
                long j = this.pos;
                slice(j, length + j).get(bytes);
                this.pos += length;
                return bytes;
            } else if (length <= 0) {
                if (length == 0) {
                    return Internal.EMPTY_BYTE_ARRAY;
                }
                throw InvalidProtocolBufferException.negativeSize();
            } else {
                throw InvalidProtocolBufferException.truncatedMessage();
            }
        }

        @Override // com.android.framework.protobuf.CodedInputStream
        public void skipRawBytes(int length) throws IOException {
            if (length >= 0 && length <= remaining()) {
                this.pos += length;
            } else if (length < 0) {
                throw InvalidProtocolBufferException.negativeSize();
            } else {
                throw InvalidProtocolBufferException.truncatedMessage();
            }
        }

        private void recomputeBufferSizeAfterLimit() {
            long j = this.limit + this.bufferSizeAfterLimit;
            this.limit = j;
            int bufferEnd = (int) (j - this.startPos);
            int i = this.currentLimit;
            if (bufferEnd > i) {
                int i2 = bufferEnd - i;
                this.bufferSizeAfterLimit = i2;
                this.limit = j - i2;
                return;
            }
            this.bufferSizeAfterLimit = 0;
        }

        private int remaining() {
            return (int) (this.limit - this.pos);
        }

        private int bufferPos(long pos) {
            return (int) (pos - this.address);
        }

        private ByteBuffer slice(long begin, long end) throws IOException {
            int prevPos = this.buffer.position();
            int prevLimit = this.buffer.limit();
            Buffer asBuffer = this.buffer;
            try {
                try {
                    asBuffer.position(bufferPos(begin));
                    asBuffer.limit(bufferPos(end));
                    return this.buffer.slice();
                } catch (IllegalArgumentException e) {
                    InvalidProtocolBufferException ex = InvalidProtocolBufferException.truncatedMessage();
                    ex.initCause(e);
                    throw ex;
                }
            } finally {
                asBuffer.position(prevPos);
                asBuffer.limit(prevLimit);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes4.dex */
    public static final class StreamDecoder extends CodedInputStream {
        private final byte[] buffer;
        private int bufferSize;
        private int bufferSizeAfterLimit;
        private int currentLimit;
        private final InputStream input;
        private int lastTag;
        private int pos;
        private RefillCallback refillCallback;
        private int totalBytesRetired;

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: classes4.dex */
        public interface RefillCallback {
            void onRefill();
        }

        private StreamDecoder(InputStream input, int bufferSize) {
            super();
            this.currentLimit = Integer.MAX_VALUE;
            this.refillCallback = null;
            Internal.checkNotNull(input, "input");
            this.input = input;
            this.buffer = new byte[bufferSize];
            this.bufferSize = 0;
            this.pos = 0;
            this.totalBytesRetired = 0;
        }

        private static int read(InputStream input, byte[] data, int offset, int length) throws IOException {
            try {
                return input.read(data, offset, length);
            } catch (InvalidProtocolBufferException e) {
                e.setThrownFromInputStream();
                throw e;
            }
        }

        private static long skip(InputStream input, long length) throws IOException {
            try {
                return input.skip(length);
            } catch (InvalidProtocolBufferException e) {
                e.setThrownFromInputStream();
                throw e;
            }
        }

        private static int available(InputStream input) throws IOException {
            try {
                return input.available();
            } catch (InvalidProtocolBufferException e) {
                e.setThrownFromInputStream();
                throw e;
            }
        }

        @Override // com.android.framework.protobuf.CodedInputStream
        public int readTag() throws IOException {
            if (isAtEnd()) {
                this.lastTag = 0;
                return 0;
            }
            int readRawVarint32 = readRawVarint32();
            this.lastTag = readRawVarint32;
            if (WireFormat.getTagFieldNumber(readRawVarint32) == 0) {
                throw InvalidProtocolBufferException.invalidTag();
            }
            return this.lastTag;
        }

        @Override // com.android.framework.protobuf.CodedInputStream
        public void checkLastTagWas(int value) throws InvalidProtocolBufferException {
            if (this.lastTag != value) {
                throw InvalidProtocolBufferException.invalidEndTag();
            }
        }

        @Override // com.android.framework.protobuf.CodedInputStream
        public int getLastTag() {
            return this.lastTag;
        }

        @Override // com.android.framework.protobuf.CodedInputStream
        public boolean skipField(int tag) throws IOException {
            switch (WireFormat.getTagWireType(tag)) {
                case 0:
                    skipRawVarint();
                    return true;
                case 1:
                    skipRawBytes(8);
                    return true;
                case 2:
                    skipRawBytes(readRawVarint32());
                    return true;
                case 3:
                    skipMessage();
                    checkLastTagWas(WireFormat.makeTag(WireFormat.getTagFieldNumber(tag), 4));
                    return true;
                case 4:
                    return false;
                case 5:
                    skipRawBytes(4);
                    return true;
                default:
                    throw InvalidProtocolBufferException.invalidWireType();
            }
        }

        @Override // com.android.framework.protobuf.CodedInputStream
        public boolean skipField(int tag, CodedOutputStream output) throws IOException {
            switch (WireFormat.getTagWireType(tag)) {
                case 0:
                    long value = readInt64();
                    output.writeUInt32NoTag(tag);
                    output.writeUInt64NoTag(value);
                    return true;
                case 1:
                    long value2 = readRawLittleEndian64();
                    output.writeUInt32NoTag(tag);
                    output.writeFixed64NoTag(value2);
                    return true;
                case 2:
                    ByteString value3 = readBytes();
                    output.writeUInt32NoTag(tag);
                    output.writeBytesNoTag(value3);
                    return true;
                case 3:
                    output.writeUInt32NoTag(tag);
                    skipMessage(output);
                    int endtag = WireFormat.makeTag(WireFormat.getTagFieldNumber(tag), 4);
                    checkLastTagWas(endtag);
                    output.writeUInt32NoTag(endtag);
                    return true;
                case 4:
                    return false;
                case 5:
                    int value4 = readRawLittleEndian32();
                    output.writeUInt32NoTag(tag);
                    output.writeFixed32NoTag(value4);
                    return true;
                default:
                    throw InvalidProtocolBufferException.invalidWireType();
            }
        }

        @Override // com.android.framework.protobuf.CodedInputStream
        public void skipMessage() throws IOException {
            int tag;
            do {
                tag = readTag();
                if (tag == 0) {
                    return;
                }
            } while (skipField(tag));
        }

        @Override // com.android.framework.protobuf.CodedInputStream
        public void skipMessage(CodedOutputStream output) throws IOException {
            int tag;
            do {
                tag = readTag();
                if (tag == 0) {
                    return;
                }
            } while (skipField(tag, output));
        }

        /* loaded from: classes4.dex */
        private class SkippedDataSink implements RefillCallback {
            private ByteArrayOutputStream byteArrayStream;
            private int lastPos;

            private SkippedDataSink() {
                this.lastPos = StreamDecoder.this.pos;
            }

            @Override // com.android.framework.protobuf.CodedInputStream.StreamDecoder.RefillCallback
            public void onRefill() {
                if (this.byteArrayStream == null) {
                    this.byteArrayStream = new ByteArrayOutputStream();
                }
                this.byteArrayStream.write(StreamDecoder.this.buffer, this.lastPos, StreamDecoder.this.pos - this.lastPos);
                this.lastPos = 0;
            }

            ByteBuffer getSkippedData() {
                ByteArrayOutputStream byteArrayOutputStream = this.byteArrayStream;
                if (byteArrayOutputStream == null) {
                    return ByteBuffer.wrap(StreamDecoder.this.buffer, this.lastPos, StreamDecoder.this.pos - this.lastPos);
                }
                byteArrayOutputStream.write(StreamDecoder.this.buffer, this.lastPos, StreamDecoder.this.pos);
                return ByteBuffer.wrap(this.byteArrayStream.toByteArray());
            }
        }

        @Override // com.android.framework.protobuf.CodedInputStream
        public double readDouble() throws IOException {
            return Double.longBitsToDouble(readRawLittleEndian64());
        }

        @Override // com.android.framework.protobuf.CodedInputStream
        public float readFloat() throws IOException {
            return Float.intBitsToFloat(readRawLittleEndian32());
        }

        @Override // com.android.framework.protobuf.CodedInputStream
        public long readUInt64() throws IOException {
            return readRawVarint64();
        }

        @Override // com.android.framework.protobuf.CodedInputStream
        public long readInt64() throws IOException {
            return readRawVarint64();
        }

        @Override // com.android.framework.protobuf.CodedInputStream
        public int readInt32() throws IOException {
            return readRawVarint32();
        }

        @Override // com.android.framework.protobuf.CodedInputStream
        public long readFixed64() throws IOException {
            return readRawLittleEndian64();
        }

        @Override // com.android.framework.protobuf.CodedInputStream
        public int readFixed32() throws IOException {
            return readRawLittleEndian32();
        }

        @Override // com.android.framework.protobuf.CodedInputStream
        public boolean readBool() throws IOException {
            return readRawVarint64() != 0;
        }

        @Override // com.android.framework.protobuf.CodedInputStream
        public String readString() throws IOException {
            int size = readRawVarint32();
            if (size > 0) {
                int i = this.bufferSize;
                int i2 = this.pos;
                if (size <= i - i2) {
                    String result = new String(this.buffer, i2, size, Internal.UTF_8);
                    this.pos += size;
                    return result;
                }
            }
            if (size == 0) {
                return "";
            }
            if (size <= this.bufferSize) {
                refillBuffer(size);
                String result2 = new String(this.buffer, this.pos, size, Internal.UTF_8);
                this.pos += size;
                return result2;
            }
            return new String(readRawBytesSlowPath(size, false), Internal.UTF_8);
        }

        @Override // com.android.framework.protobuf.CodedInputStream
        public String readStringRequireUtf8() throws IOException {
            byte[] bytes;
            int tempPos;
            int size = readRawVarint32();
            int oldPos = this.pos;
            int i = this.bufferSize;
            if (size <= i - oldPos && size > 0) {
                bytes = this.buffer;
                this.pos = oldPos + size;
                tempPos = oldPos;
            } else if (size == 0) {
                return "";
            } else {
                if (size <= i) {
                    refillBuffer(size);
                    bytes = this.buffer;
                    tempPos = 0;
                    this.pos = 0 + size;
                } else {
                    bytes = readRawBytesSlowPath(size, false);
                    tempPos = 0;
                }
            }
            return Utf8.decodeUtf8(bytes, tempPos, size);
        }

        @Override // com.android.framework.protobuf.CodedInputStream
        public void readGroup(int fieldNumber, MessageLite.Builder builder, ExtensionRegistryLite extensionRegistry) throws IOException {
            checkRecursionLimit();
            this.recursionDepth++;
            builder.mergeFrom(this, extensionRegistry);
            checkLastTagWas(WireFormat.makeTag(fieldNumber, 4));
            this.recursionDepth--;
        }

        @Override // com.android.framework.protobuf.CodedInputStream
        public <T extends MessageLite> T readGroup(int fieldNumber, Parser<T> parser, ExtensionRegistryLite extensionRegistry) throws IOException {
            checkRecursionLimit();
            this.recursionDepth++;
            T result = parser.parsePartialFrom(this, extensionRegistry);
            checkLastTagWas(WireFormat.makeTag(fieldNumber, 4));
            this.recursionDepth--;
            return result;
        }

        @Override // com.android.framework.protobuf.CodedInputStream
        @Deprecated
        public void readUnknownGroup(int fieldNumber, MessageLite.Builder builder) throws IOException {
            readGroup(fieldNumber, builder, ExtensionRegistryLite.getEmptyRegistry());
        }

        @Override // com.android.framework.protobuf.CodedInputStream
        public void readMessage(MessageLite.Builder builder, ExtensionRegistryLite extensionRegistry) throws IOException {
            int length = readRawVarint32();
            checkRecursionLimit();
            int oldLimit = pushLimit(length);
            this.recursionDepth++;
            builder.mergeFrom(this, extensionRegistry);
            checkLastTagWas(0);
            this.recursionDepth--;
            if (getBytesUntilLimit() != 0) {
                throw InvalidProtocolBufferException.truncatedMessage();
            }
            popLimit(oldLimit);
        }

        @Override // com.android.framework.protobuf.CodedInputStream
        public <T extends MessageLite> T readMessage(Parser<T> parser, ExtensionRegistryLite extensionRegistry) throws IOException {
            int length = readRawVarint32();
            checkRecursionLimit();
            int oldLimit = pushLimit(length);
            this.recursionDepth++;
            T result = parser.parsePartialFrom(this, extensionRegistry);
            checkLastTagWas(0);
            this.recursionDepth--;
            if (getBytesUntilLimit() != 0) {
                throw InvalidProtocolBufferException.truncatedMessage();
            }
            popLimit(oldLimit);
            return result;
        }

        @Override // com.android.framework.protobuf.CodedInputStream
        public ByteString readBytes() throws IOException {
            int size = readRawVarint32();
            int i = this.bufferSize;
            int i2 = this.pos;
            if (size <= i - i2 && size > 0) {
                ByteString result = ByteString.copyFrom(this.buffer, i2, size);
                this.pos += size;
                return result;
            } else if (size == 0) {
                return ByteString.EMPTY;
            } else {
                return readBytesSlowPath(size);
            }
        }

        @Override // com.android.framework.protobuf.CodedInputStream
        public byte[] readByteArray() throws IOException {
            int size = readRawVarint32();
            int i = this.bufferSize;
            int i2 = this.pos;
            if (size <= i - i2 && size > 0) {
                byte[] result = Arrays.copyOfRange(this.buffer, i2, i2 + size);
                this.pos += size;
                return result;
            }
            return readRawBytesSlowPath(size, false);
        }

        @Override // com.android.framework.protobuf.CodedInputStream
        public ByteBuffer readByteBuffer() throws IOException {
            int size = readRawVarint32();
            int i = this.bufferSize;
            int i2 = this.pos;
            if (size <= i - i2 && size > 0) {
                ByteBuffer result = ByteBuffer.wrap(Arrays.copyOfRange(this.buffer, i2, i2 + size));
                this.pos += size;
                return result;
            } else if (size == 0) {
                return Internal.EMPTY_BYTE_BUFFER;
            } else {
                return ByteBuffer.wrap(readRawBytesSlowPath(size, true));
            }
        }

        @Override // com.android.framework.protobuf.CodedInputStream
        public int readUInt32() throws IOException {
            return readRawVarint32();
        }

        @Override // com.android.framework.protobuf.CodedInputStream
        public int readEnum() throws IOException {
            return readRawVarint32();
        }

        @Override // com.android.framework.protobuf.CodedInputStream
        public int readSFixed32() throws IOException {
            return readRawLittleEndian32();
        }

        @Override // com.android.framework.protobuf.CodedInputStream
        public long readSFixed64() throws IOException {
            return readRawLittleEndian64();
        }

        @Override // com.android.framework.protobuf.CodedInputStream
        public int readSInt32() throws IOException {
            return decodeZigZag32(readRawVarint32());
        }

        @Override // com.android.framework.protobuf.CodedInputStream
        public long readSInt64() throws IOException {
            return decodeZigZag64(readRawVarint64());
        }

        /* JADX WARN: Code restructure failed: missing block: B:32:0x006f, code lost:
            if (r2[r1] < 0) goto L35;
         */
        @Override // com.android.framework.protobuf.CodedInputStream
        /*
            Code decompiled incorrectly, please refer to instructions dump.
        */
        public int readRawVarint32() throws IOException {
            int tempPos;
            int tempPos2 = this.pos;
            int i = this.bufferSize;
            if (i != tempPos2) {
                byte[] buffer = this.buffer;
                int tempPos3 = tempPos2 + 1;
                int x = buffer[tempPos2];
                if (x >= 0) {
                    this.pos = tempPos3;
                    return x;
                } else if (i - tempPos3 >= 9) {
                    int tempPos4 = tempPos3 + 1;
                    int x2 = (buffer[tempPos3] << 7) ^ x;
                    if (x2 < 0) {
                        tempPos = x2 ^ (-128);
                    } else {
                        int x3 = tempPos4 + 1;
                        int x4 = (buffer[tempPos4] << 14) ^ x2;
                        if (x4 >= 0) {
                            tempPos = x4 ^ 16256;
                            tempPos4 = x3;
                        } else {
                            tempPos4 = x3 + 1;
                            int x5 = (buffer[x3] << 21) ^ x4;
                            if (x5 < 0) {
                                tempPos = (-2080896) ^ x5;
                            } else {
                                int tempPos5 = tempPos4 + 1;
                                int y = buffer[tempPos4];
                                int x6 = (x5 ^ (y << 28)) ^ 266354560;
                                if (y < 0) {
                                    int tempPos6 = tempPos5 + 1;
                                    if (buffer[tempPos5] < 0) {
                                        tempPos5 = tempPos6 + 1;
                                        if (buffer[tempPos6] < 0) {
                                            tempPos6 = tempPos5 + 1;
                                            if (buffer[tempPos5] < 0) {
                                                tempPos5 = tempPos6 + 1;
                                                if (buffer[tempPos6] < 0) {
                                                    tempPos6 = tempPos5 + 1;
                                                }
                                            }
                                        }
                                    }
                                    tempPos = x6;
                                    tempPos4 = tempPos6;
                                }
                                tempPos4 = tempPos5;
                                tempPos = x6;
                            }
                        }
                    }
                    this.pos = tempPos4;
                    return tempPos;
                }
            }
            return (int) readRawVarint64SlowPath();
        }

        private void skipRawVarint() throws IOException {
            if (this.bufferSize - this.pos >= 10) {
                skipRawVarintFastPath();
            } else {
                skipRawVarintSlowPath();
            }
        }

        private void skipRawVarintFastPath() throws IOException {
            for (int i = 0; i < 10; i++) {
                byte[] bArr = this.buffer;
                int i2 = this.pos;
                this.pos = i2 + 1;
                if (bArr[i2] >= 0) {
                    return;
                }
            }
            throw InvalidProtocolBufferException.malformedVarint();
        }

        private void skipRawVarintSlowPath() throws IOException {
            for (int i = 0; i < 10; i++) {
                if (readRawByte() >= 0) {
                    return;
                }
            }
            throw InvalidProtocolBufferException.malformedVarint();
        }

        /* JADX WARN: Code restructure failed: missing block: B:36:0x00bd, code lost:
            if (r2[r1] < 0) goto L38;
         */
        @Override // com.android.framework.protobuf.CodedInputStream
        /*
            Code decompiled incorrectly, please refer to instructions dump.
        */
        public long readRawVarint64() throws IOException {
            long x;
            int tempPos = this.pos;
            int i = this.bufferSize;
            if (i != tempPos) {
                byte[] buffer = this.buffer;
                int tempPos2 = tempPos + 1;
                int y = buffer[tempPos];
                if (y >= 0) {
                    this.pos = tempPos2;
                    return y;
                } else if (i - tempPos2 >= 9) {
                    int tempPos3 = tempPos2 + 1;
                    int y2 = (buffer[tempPos2] << 7) ^ y;
                    if (y2 < 0) {
                        x = y2 ^ (-128);
                    } else {
                        int tempPos4 = tempPos3 + 1;
                        int y3 = (buffer[tempPos3] << 14) ^ y2;
                        if (y3 >= 0) {
                            x = y3 ^ 16256;
                            tempPos3 = tempPos4;
                        } else {
                            tempPos3 = tempPos4 + 1;
                            int y4 = (buffer[tempPos4] << 21) ^ y3;
                            if (y4 < 0) {
                                x = (-2080896) ^ y4;
                            } else {
                                long x2 = y4;
                                int tempPos5 = tempPos3 + 1;
                                long x3 = x2 ^ (buffer[tempPos3] << 28);
                                if (x3 >= 0) {
                                    x = 266354560 ^ x3;
                                    tempPos3 = tempPos5;
                                } else {
                                    tempPos3 = tempPos5 + 1;
                                    long x4 = (buffer[tempPos5] << 35) ^ x3;
                                    if (x4 < 0) {
                                        x = (-34093383808L) ^ x4;
                                    } else {
                                        int tempPos6 = tempPos3 + 1;
                                        long x5 = (buffer[tempPos3] << 42) ^ x4;
                                        if (x5 >= 0) {
                                            x = 4363953127296L ^ x5;
                                            tempPos3 = tempPos6;
                                        } else {
                                            tempPos3 = tempPos6 + 1;
                                            long x6 = (buffer[tempPos6] << 49) ^ x5;
                                            if (x6 < 0) {
                                                x = (-558586000294016L) ^ x6;
                                            } else {
                                                int tempPos7 = tempPos3 + 1;
                                                x = ((buffer[tempPos3] << 56) ^ x6) ^ 71499008037633920L;
                                                if (x >= 0) {
                                                    tempPos3 = tempPos7;
                                                } else {
                                                    tempPos3 = tempPos7 + 1;
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    this.pos = tempPos3;
                    return x;
                }
            }
            return readRawVarint64SlowPath();
        }

        @Override // com.android.framework.protobuf.CodedInputStream
        long readRawVarint64SlowPath() throws IOException {
            long result = 0;
            for (int shift = 0; shift < 64; shift += 7) {
                byte b = readRawByte();
                result |= (b & Byte.MAX_VALUE) << shift;
                if ((b & 128) == 0) {
                    return result;
                }
            }
            throw InvalidProtocolBufferException.malformedVarint();
        }

        @Override // com.android.framework.protobuf.CodedInputStream
        public int readRawLittleEndian32() throws IOException {
            int tempPos = this.pos;
            if (this.bufferSize - tempPos < 4) {
                refillBuffer(4);
                tempPos = this.pos;
            }
            byte[] buffer = this.buffer;
            this.pos = tempPos + 4;
            return (buffer[tempPos] & 255) | ((buffer[tempPos + 1] & 255) << 8) | ((buffer[tempPos + 2] & 255) << 16) | ((buffer[tempPos + 3] & 255) << 24);
        }

        @Override // com.android.framework.protobuf.CodedInputStream
        public long readRawLittleEndian64() throws IOException {
            int tempPos = this.pos;
            if (this.bufferSize - tempPos < 8) {
                refillBuffer(8);
                tempPos = this.pos;
            }
            byte[] buffer = this.buffer;
            this.pos = tempPos + 8;
            return (buffer[tempPos] & 255) | ((buffer[tempPos + 1] & 255) << 8) | ((buffer[tempPos + 2] & 255) << 16) | ((buffer[tempPos + 3] & 255) << 24) | ((buffer[tempPos + 4] & 255) << 32) | ((buffer[tempPos + 5] & 255) << 40) | ((buffer[tempPos + 6] & 255) << 48) | ((buffer[tempPos + 7] & 255) << 56);
        }

        @Override // com.android.framework.protobuf.CodedInputStream
        public void enableAliasing(boolean enabled) {
        }

        @Override // com.android.framework.protobuf.CodedInputStream
        public void resetSizeCounter() {
            this.totalBytesRetired = -this.pos;
        }

        @Override // com.android.framework.protobuf.CodedInputStream
        public int pushLimit(int byteLimit) throws InvalidProtocolBufferException {
            if (byteLimit < 0) {
                throw InvalidProtocolBufferException.negativeSize();
            }
            int byteLimit2 = byteLimit + this.totalBytesRetired + this.pos;
            int oldLimit = this.currentLimit;
            if (byteLimit2 > oldLimit) {
                throw InvalidProtocolBufferException.truncatedMessage();
            }
            this.currentLimit = byteLimit2;
            recomputeBufferSizeAfterLimit();
            return oldLimit;
        }

        private void recomputeBufferSizeAfterLimit() {
            int i = this.bufferSize + this.bufferSizeAfterLimit;
            this.bufferSize = i;
            int bufferEnd = this.totalBytesRetired + i;
            int i2 = this.currentLimit;
            if (bufferEnd > i2) {
                int i3 = bufferEnd - i2;
                this.bufferSizeAfterLimit = i3;
                this.bufferSize = i - i3;
                return;
            }
            this.bufferSizeAfterLimit = 0;
        }

        @Override // com.android.framework.protobuf.CodedInputStream
        public void popLimit(int oldLimit) {
            this.currentLimit = oldLimit;
            recomputeBufferSizeAfterLimit();
        }

        @Override // com.android.framework.protobuf.CodedInputStream
        public int getBytesUntilLimit() {
            int i = this.currentLimit;
            if (i == Integer.MAX_VALUE) {
                return -1;
            }
            int currentAbsolutePosition = this.totalBytesRetired + this.pos;
            return i - currentAbsolutePosition;
        }

        @Override // com.android.framework.protobuf.CodedInputStream
        public boolean isAtEnd() throws IOException {
            return this.pos == this.bufferSize && !tryRefillBuffer(1);
        }

        @Override // com.android.framework.protobuf.CodedInputStream
        public int getTotalBytesRead() {
            return this.totalBytesRetired + this.pos;
        }

        private void refillBuffer(int n) throws IOException {
            if (!tryRefillBuffer(n)) {
                if (n > (this.sizeLimit - this.totalBytesRetired) - this.pos) {
                    throw InvalidProtocolBufferException.sizeLimitExceeded();
                }
                throw InvalidProtocolBufferException.truncatedMessage();
            }
        }

        private boolean tryRefillBuffer(int n) throws IOException {
            if (this.pos + n <= this.bufferSize) {
                throw new IllegalStateException("refillBuffer() called when " + n + " bytes were already available in buffer");
            }
            int i = this.sizeLimit;
            int i2 = this.totalBytesRetired;
            int i3 = this.pos;
            if (n <= (i - i2) - i3 && i2 + i3 + n <= this.currentLimit) {
                RefillCallback refillCallback = this.refillCallback;
                if (refillCallback != null) {
                    refillCallback.onRefill();
                }
                int tempPos = this.pos;
                if (tempPos > 0) {
                    int i4 = this.bufferSize;
                    if (i4 > tempPos) {
                        byte[] bArr = this.buffer;
                        System.arraycopy(bArr, tempPos, bArr, 0, i4 - tempPos);
                    }
                    this.totalBytesRetired += tempPos;
                    this.bufferSize -= tempPos;
                    this.pos = 0;
                }
                InputStream inputStream = this.input;
                byte[] bArr2 = this.buffer;
                int i5 = this.bufferSize;
                int bytesRead = read(inputStream, bArr2, i5, Math.min(bArr2.length - i5, (this.sizeLimit - this.totalBytesRetired) - this.bufferSize));
                if (bytesRead == 0 || bytesRead < -1 || bytesRead > this.buffer.length) {
                    throw new IllegalStateException(this.input.getClass() + "#read(byte[]) returned invalid result: " + bytesRead + "\nThe InputStream implementation is buggy.");
                }
                if (bytesRead > 0) {
                    this.bufferSize += bytesRead;
                    recomputeBufferSizeAfterLimit();
                    if (this.bufferSize >= n) {
                        return true;
                    }
                    return tryRefillBuffer(n);
                }
                return false;
            }
            return false;
        }

        @Override // com.android.framework.protobuf.CodedInputStream
        public byte readRawByte() throws IOException {
            if (this.pos == this.bufferSize) {
                refillBuffer(1);
            }
            byte[] bArr = this.buffer;
            int i = this.pos;
            this.pos = i + 1;
            return bArr[i];
        }

        @Override // com.android.framework.protobuf.CodedInputStream
        public byte[] readRawBytes(int size) throws IOException {
            int tempPos = this.pos;
            if (size <= this.bufferSize - tempPos && size > 0) {
                this.pos = tempPos + size;
                return Arrays.copyOfRange(this.buffer, tempPos, tempPos + size);
            }
            return readRawBytesSlowPath(size, false);
        }

        private byte[] readRawBytesSlowPath(int size, boolean ensureNoLeakedReferences) throws IOException {
            byte[] result = readRawBytesSlowPathOneChunk(size);
            if (result != null) {
                return ensureNoLeakedReferences ? (byte[]) result.clone() : result;
            }
            int originalBufferPos = this.pos;
            int i = this.bufferSize;
            int bufferedBytes = i - this.pos;
            this.totalBytesRetired += i;
            this.pos = 0;
            this.bufferSize = 0;
            int sizeLeft = size - bufferedBytes;
            List<byte[]> chunks = readRawBytesSlowPathRemainingChunks(sizeLeft);
            byte[] bytes = new byte[size];
            System.arraycopy(this.buffer, originalBufferPos, bytes, 0, bufferedBytes);
            int tempPos = bufferedBytes;
            for (byte[] chunk : chunks) {
                System.arraycopy(chunk, 0, bytes, tempPos, chunk.length);
                tempPos += chunk.length;
            }
            return bytes;
        }

        private byte[] readRawBytesSlowPathOneChunk(int size) throws IOException {
            if (size == 0) {
                return Internal.EMPTY_BYTE_ARRAY;
            }
            if (size < 0) {
                throw InvalidProtocolBufferException.negativeSize();
            }
            int currentMessageSize = this.totalBytesRetired + this.pos + size;
            if (currentMessageSize - this.sizeLimit > 0) {
                throw InvalidProtocolBufferException.sizeLimitExceeded();
            }
            int i = this.currentLimit;
            if (currentMessageSize > i) {
                skipRawBytes((i - this.totalBytesRetired) - this.pos);
                throw InvalidProtocolBufferException.truncatedMessage();
            }
            int bufferedBytes = this.bufferSize - this.pos;
            int sizeLeft = size - bufferedBytes;
            if (sizeLeft < 4096 || sizeLeft <= available(this.input)) {
                byte[] bytes = new byte[size];
                System.arraycopy(this.buffer, this.pos, bytes, 0, bufferedBytes);
                this.totalBytesRetired += this.bufferSize;
                this.pos = 0;
                this.bufferSize = 0;
                int tempPos = bufferedBytes;
                while (tempPos < bytes.length) {
                    int n = read(this.input, bytes, tempPos, size - tempPos);
                    if (n == -1) {
                        throw InvalidProtocolBufferException.truncatedMessage();
                    }
                    this.totalBytesRetired += n;
                    tempPos += n;
                }
                return bytes;
            }
            return null;
        }

        private List<byte[]> readRawBytesSlowPathRemainingChunks(int sizeLeft) throws IOException {
            List<byte[]> chunks = new ArrayList<>();
            while (sizeLeft > 0) {
                byte[] chunk = new byte[Math.min(sizeLeft, 4096)];
                int tempPos = 0;
                while (tempPos < chunk.length) {
                    int n = this.input.read(chunk, tempPos, chunk.length - tempPos);
                    if (n == -1) {
                        throw InvalidProtocolBufferException.truncatedMessage();
                    }
                    this.totalBytesRetired += n;
                    tempPos += n;
                }
                sizeLeft -= chunk.length;
                chunks.add(chunk);
            }
            return chunks;
        }

        private ByteString readBytesSlowPath(int size) throws IOException {
            byte[] result = readRawBytesSlowPathOneChunk(size);
            if (result != null) {
                return ByteString.copyFrom(result);
            }
            int originalBufferPos = this.pos;
            int i = this.bufferSize;
            int bufferedBytes = i - this.pos;
            this.totalBytesRetired += i;
            this.pos = 0;
            this.bufferSize = 0;
            int sizeLeft = size - bufferedBytes;
            List<byte[]> chunks = readRawBytesSlowPathRemainingChunks(sizeLeft);
            byte[] bytes = new byte[size];
            System.arraycopy(this.buffer, originalBufferPos, bytes, 0, bufferedBytes);
            int tempPos = bufferedBytes;
            for (byte[] chunk : chunks) {
                System.arraycopy(chunk, 0, bytes, tempPos, chunk.length);
                tempPos += chunk.length;
            }
            return ByteString.wrap(bytes);
        }

        @Override // com.android.framework.protobuf.CodedInputStream
        public void skipRawBytes(int size) throws IOException {
            int i = this.bufferSize;
            int i2 = this.pos;
            if (size <= i - i2 && size >= 0) {
                this.pos = i2 + size;
            } else {
                skipRawBytesSlowPath(size);
            }
        }

        private void skipRawBytesSlowPath(int size) throws IOException {
            if (size < 0) {
                throw InvalidProtocolBufferException.negativeSize();
            }
            int i = this.totalBytesRetired;
            int i2 = this.pos;
            int i3 = i + i2 + size;
            int i4 = this.currentLimit;
            if (i3 > i4) {
                skipRawBytes((i4 - i) - i2);
                throw InvalidProtocolBufferException.truncatedMessage();
            }
            int totalSkipped = 0;
            if (this.refillCallback == null) {
                this.totalBytesRetired = i + i2;
                int totalSkipped2 = this.bufferSize - i2;
                this.bufferSize = 0;
                this.pos = 0;
                totalSkipped = totalSkipped2;
                while (totalSkipped < size) {
                    int toSkip = size - totalSkipped;
                    try {
                        long skipped = skip(this.input, toSkip);
                        if (skipped < 0 || skipped > toSkip) {
                            throw new IllegalStateException(this.input.getClass() + "#skip returned invalid result: " + skipped + "\nThe InputStream implementation is buggy.");
                        } else if (skipped == 0) {
                            break;
                        } else {
                            totalSkipped += (int) skipped;
                        }
                    } finally {
                        this.totalBytesRetired += totalSkipped;
                        recomputeBufferSizeAfterLimit();
                    }
                }
            }
            if (totalSkipped < size) {
                int i5 = this.bufferSize;
                int tempPos = i5 - this.pos;
                this.pos = i5;
                refillBuffer(1);
                while (true) {
                    int i6 = size - tempPos;
                    int i7 = this.bufferSize;
                    if (i6 > i7) {
                        tempPos += i7;
                        this.pos = i7;
                        refillBuffer(1);
                    } else {
                        this.pos = size - tempPos;
                        return;
                    }
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes4.dex */
    public static final class IterableDirectByteBufferDecoder extends CodedInputStream {
        private int bufferSizeAfterCurrentLimit;
        private long currentAddress;
        private ByteBuffer currentByteBuffer;
        private long currentByteBufferLimit;
        private long currentByteBufferPos;
        private long currentByteBufferStartPos;
        private int currentLimit;
        private boolean enableAliasing;
        private final boolean immutable;
        private final Iterable<ByteBuffer> input;
        private final Iterator<ByteBuffer> iterator;
        private int lastTag;
        private int startOffset;
        private int totalBufferSize;
        private int totalBytesRead;

        private IterableDirectByteBufferDecoder(Iterable<ByteBuffer> inputBufs, int size, boolean immutableFlag) {
            super();
            this.currentLimit = Integer.MAX_VALUE;
            this.totalBufferSize = size;
            this.input = inputBufs;
            this.iterator = inputBufs.iterator();
            this.immutable = immutableFlag;
            this.totalBytesRead = 0;
            this.startOffset = 0;
            if (size == 0) {
                this.currentByteBuffer = Internal.EMPTY_BYTE_BUFFER;
                this.currentByteBufferPos = 0L;
                this.currentByteBufferStartPos = 0L;
                this.currentByteBufferLimit = 0L;
                this.currentAddress = 0L;
                return;
            }
            tryGetNextByteBuffer();
        }

        private void getNextByteBuffer() throws InvalidProtocolBufferException {
            if (!this.iterator.hasNext()) {
                throw InvalidProtocolBufferException.truncatedMessage();
            }
            tryGetNextByteBuffer();
        }

        private void tryGetNextByteBuffer() {
            ByteBuffer next = this.iterator.next();
            this.currentByteBuffer = next;
            this.totalBytesRead += (int) (this.currentByteBufferPos - this.currentByteBufferStartPos);
            long position = next.position();
            this.currentByteBufferPos = position;
            this.currentByteBufferStartPos = position;
            this.currentByteBufferLimit = this.currentByteBuffer.limit();
            long addressOffset = UnsafeUtil.addressOffset(this.currentByteBuffer);
            this.currentAddress = addressOffset;
            this.currentByteBufferPos += addressOffset;
            this.currentByteBufferStartPos += addressOffset;
            this.currentByteBufferLimit += addressOffset;
        }

        @Override // com.android.framework.protobuf.CodedInputStream
        public int readTag() throws IOException {
            if (isAtEnd()) {
                this.lastTag = 0;
                return 0;
            }
            int readRawVarint32 = readRawVarint32();
            this.lastTag = readRawVarint32;
            if (WireFormat.getTagFieldNumber(readRawVarint32) == 0) {
                throw InvalidProtocolBufferException.invalidTag();
            }
            return this.lastTag;
        }

        @Override // com.android.framework.protobuf.CodedInputStream
        public void checkLastTagWas(int value) throws InvalidProtocolBufferException {
            if (this.lastTag != value) {
                throw InvalidProtocolBufferException.invalidEndTag();
            }
        }

        @Override // com.android.framework.protobuf.CodedInputStream
        public int getLastTag() {
            return this.lastTag;
        }

        @Override // com.android.framework.protobuf.CodedInputStream
        public boolean skipField(int tag) throws IOException {
            switch (WireFormat.getTagWireType(tag)) {
                case 0:
                    skipRawVarint();
                    return true;
                case 1:
                    skipRawBytes(8);
                    return true;
                case 2:
                    skipRawBytes(readRawVarint32());
                    return true;
                case 3:
                    skipMessage();
                    checkLastTagWas(WireFormat.makeTag(WireFormat.getTagFieldNumber(tag), 4));
                    return true;
                case 4:
                    return false;
                case 5:
                    skipRawBytes(4);
                    return true;
                default:
                    throw InvalidProtocolBufferException.invalidWireType();
            }
        }

        @Override // com.android.framework.protobuf.CodedInputStream
        public boolean skipField(int tag, CodedOutputStream output) throws IOException {
            switch (WireFormat.getTagWireType(tag)) {
                case 0:
                    long value = readInt64();
                    output.writeUInt32NoTag(tag);
                    output.writeUInt64NoTag(value);
                    return true;
                case 1:
                    long value2 = readRawLittleEndian64();
                    output.writeUInt32NoTag(tag);
                    output.writeFixed64NoTag(value2);
                    return true;
                case 2:
                    ByteString value3 = readBytes();
                    output.writeUInt32NoTag(tag);
                    output.writeBytesNoTag(value3);
                    return true;
                case 3:
                    output.writeUInt32NoTag(tag);
                    skipMessage(output);
                    int endtag = WireFormat.makeTag(WireFormat.getTagFieldNumber(tag), 4);
                    checkLastTagWas(endtag);
                    output.writeUInt32NoTag(endtag);
                    return true;
                case 4:
                    return false;
                case 5:
                    int value4 = readRawLittleEndian32();
                    output.writeUInt32NoTag(tag);
                    output.writeFixed32NoTag(value4);
                    return true;
                default:
                    throw InvalidProtocolBufferException.invalidWireType();
            }
        }

        @Override // com.android.framework.protobuf.CodedInputStream
        public void skipMessage() throws IOException {
            int tag;
            do {
                tag = readTag();
                if (tag == 0) {
                    return;
                }
            } while (skipField(tag));
        }

        @Override // com.android.framework.protobuf.CodedInputStream
        public void skipMessage(CodedOutputStream output) throws IOException {
            int tag;
            do {
                tag = readTag();
                if (tag == 0) {
                    return;
                }
            } while (skipField(tag, output));
        }

        @Override // com.android.framework.protobuf.CodedInputStream
        public double readDouble() throws IOException {
            return Double.longBitsToDouble(readRawLittleEndian64());
        }

        @Override // com.android.framework.protobuf.CodedInputStream
        public float readFloat() throws IOException {
            return Float.intBitsToFloat(readRawLittleEndian32());
        }

        @Override // com.android.framework.protobuf.CodedInputStream
        public long readUInt64() throws IOException {
            return readRawVarint64();
        }

        @Override // com.android.framework.protobuf.CodedInputStream
        public long readInt64() throws IOException {
            return readRawVarint64();
        }

        @Override // com.android.framework.protobuf.CodedInputStream
        public int readInt32() throws IOException {
            return readRawVarint32();
        }

        @Override // com.android.framework.protobuf.CodedInputStream
        public long readFixed64() throws IOException {
            return readRawLittleEndian64();
        }

        @Override // com.android.framework.protobuf.CodedInputStream
        public int readFixed32() throws IOException {
            return readRawLittleEndian32();
        }

        @Override // com.android.framework.protobuf.CodedInputStream
        public boolean readBool() throws IOException {
            return readRawVarint64() != 0;
        }

        @Override // com.android.framework.protobuf.CodedInputStream
        public String readString() throws IOException {
            int size = readRawVarint32();
            if (size > 0) {
                long j = this.currentByteBufferLimit;
                long j2 = this.currentByteBufferPos;
                if (size <= j - j2) {
                    byte[] bytes = new byte[size];
                    UnsafeUtil.copyMemory(j2, bytes, 0L, size);
                    String result = new String(bytes, Internal.UTF_8);
                    this.currentByteBufferPos += size;
                    return result;
                }
            }
            if (size > 0 && size <= remaining()) {
                byte[] bytes2 = new byte[size];
                readRawBytesTo(bytes2, 0, size);
                String result2 = new String(bytes2, Internal.UTF_8);
                return result2;
            } else if (size == 0) {
                return "";
            } else {
                if (size < 0) {
                    throw InvalidProtocolBufferException.negativeSize();
                }
                throw InvalidProtocolBufferException.truncatedMessage();
            }
        }

        @Override // com.android.framework.protobuf.CodedInputStream
        public String readStringRequireUtf8() throws IOException {
            int size = readRawVarint32();
            if (size > 0) {
                long j = this.currentByteBufferLimit;
                long j2 = this.currentByteBufferPos;
                if (size <= j - j2) {
                    int bufferPos = (int) (j2 - this.currentByteBufferStartPos);
                    String result = Utf8.decodeUtf8(this.currentByteBuffer, bufferPos, size);
                    this.currentByteBufferPos += size;
                    return result;
                }
            }
            if (size >= 0 && size <= remaining()) {
                byte[] bytes = new byte[size];
                readRawBytesTo(bytes, 0, size);
                return Utf8.decodeUtf8(bytes, 0, size);
            } else if (size == 0) {
                return "";
            } else {
                if (size <= 0) {
                    throw InvalidProtocolBufferException.negativeSize();
                }
                throw InvalidProtocolBufferException.truncatedMessage();
            }
        }

        @Override // com.android.framework.protobuf.CodedInputStream
        public void readGroup(int fieldNumber, MessageLite.Builder builder, ExtensionRegistryLite extensionRegistry) throws IOException {
            checkRecursionLimit();
            this.recursionDepth++;
            builder.mergeFrom(this, extensionRegistry);
            checkLastTagWas(WireFormat.makeTag(fieldNumber, 4));
            this.recursionDepth--;
        }

        @Override // com.android.framework.protobuf.CodedInputStream
        public <T extends MessageLite> T readGroup(int fieldNumber, Parser<T> parser, ExtensionRegistryLite extensionRegistry) throws IOException {
            checkRecursionLimit();
            this.recursionDepth++;
            T result = parser.parsePartialFrom(this, extensionRegistry);
            checkLastTagWas(WireFormat.makeTag(fieldNumber, 4));
            this.recursionDepth--;
            return result;
        }

        @Override // com.android.framework.protobuf.CodedInputStream
        @Deprecated
        public void readUnknownGroup(int fieldNumber, MessageLite.Builder builder) throws IOException {
            readGroup(fieldNumber, builder, ExtensionRegistryLite.getEmptyRegistry());
        }

        @Override // com.android.framework.protobuf.CodedInputStream
        public void readMessage(MessageLite.Builder builder, ExtensionRegistryLite extensionRegistry) throws IOException {
            int length = readRawVarint32();
            checkRecursionLimit();
            int oldLimit = pushLimit(length);
            this.recursionDepth++;
            builder.mergeFrom(this, extensionRegistry);
            checkLastTagWas(0);
            this.recursionDepth--;
            if (getBytesUntilLimit() != 0) {
                throw InvalidProtocolBufferException.truncatedMessage();
            }
            popLimit(oldLimit);
        }

        @Override // com.android.framework.protobuf.CodedInputStream
        public <T extends MessageLite> T readMessage(Parser<T> parser, ExtensionRegistryLite extensionRegistry) throws IOException {
            int length = readRawVarint32();
            checkRecursionLimit();
            int oldLimit = pushLimit(length);
            this.recursionDepth++;
            T result = parser.parsePartialFrom(this, extensionRegistry);
            checkLastTagWas(0);
            this.recursionDepth--;
            if (getBytesUntilLimit() != 0) {
                throw InvalidProtocolBufferException.truncatedMessage();
            }
            popLimit(oldLimit);
            return result;
        }

        @Override // com.android.framework.protobuf.CodedInputStream
        public ByteString readBytes() throws IOException {
            int size = readRawVarint32();
            if (size > 0) {
                long j = this.currentByteBufferLimit;
                long j2 = this.currentByteBufferPos;
                if (size <= j - j2) {
                    if (this.immutable && this.enableAliasing) {
                        int idx = (int) (j2 - this.currentAddress);
                        ByteString result = ByteString.wrap(slice(idx, idx + size));
                        this.currentByteBufferPos += size;
                        return result;
                    }
                    byte[] bytes = new byte[size];
                    UnsafeUtil.copyMemory(j2, bytes, 0L, size);
                    this.currentByteBufferPos += size;
                    return ByteString.wrap(bytes);
                }
            }
            if (size > 0 && size <= remaining()) {
                if (this.immutable && this.enableAliasing) {
                    ArrayList<ByteString> byteStrings = new ArrayList<>();
                    int l = size;
                    while (l > 0) {
                        if (currentRemaining() == 0) {
                            getNextByteBuffer();
                        }
                        int bytesToCopy = Math.min(l, (int) currentRemaining());
                        int idx2 = (int) (this.currentByteBufferPos - this.currentAddress);
                        byteStrings.add(ByteString.wrap(slice(idx2, idx2 + bytesToCopy)));
                        l -= bytesToCopy;
                        this.currentByteBufferPos += bytesToCopy;
                    }
                    return ByteString.copyFrom(byteStrings);
                }
                byte[] temp = new byte[size];
                readRawBytesTo(temp, 0, size);
                return ByteString.wrap(temp);
            } else if (size == 0) {
                return ByteString.EMPTY;
            } else {
                if (size < 0) {
                    throw InvalidProtocolBufferException.negativeSize();
                }
                throw InvalidProtocolBufferException.truncatedMessage();
            }
        }

        @Override // com.android.framework.protobuf.CodedInputStream
        public byte[] readByteArray() throws IOException {
            return readRawBytes(readRawVarint32());
        }

        @Override // com.android.framework.protobuf.CodedInputStream
        public ByteBuffer readByteBuffer() throws IOException {
            int size = readRawVarint32();
            if (size > 0 && size <= currentRemaining()) {
                if (!this.immutable && this.enableAliasing) {
                    long j = this.currentByteBufferPos + size;
                    this.currentByteBufferPos = j;
                    long j2 = this.currentAddress;
                    return slice((int) ((j - j2) - size), (int) (j - j2));
                }
                byte[] bytes = new byte[size];
                UnsafeUtil.copyMemory(this.currentByteBufferPos, bytes, 0L, size);
                this.currentByteBufferPos += size;
                return ByteBuffer.wrap(bytes);
            } else if (size > 0 && size <= remaining()) {
                byte[] temp = new byte[size];
                readRawBytesTo(temp, 0, size);
                return ByteBuffer.wrap(temp);
            } else if (size == 0) {
                return Internal.EMPTY_BYTE_BUFFER;
            } else {
                if (size < 0) {
                    throw InvalidProtocolBufferException.negativeSize();
                }
                throw InvalidProtocolBufferException.truncatedMessage();
            }
        }

        @Override // com.android.framework.protobuf.CodedInputStream
        public int readUInt32() throws IOException {
            return readRawVarint32();
        }

        @Override // com.android.framework.protobuf.CodedInputStream
        public int readEnum() throws IOException {
            return readRawVarint32();
        }

        @Override // com.android.framework.protobuf.CodedInputStream
        public int readSFixed32() throws IOException {
            return readRawLittleEndian32();
        }

        @Override // com.android.framework.protobuf.CodedInputStream
        public long readSFixed64() throws IOException {
            return readRawLittleEndian64();
        }

        @Override // com.android.framework.protobuf.CodedInputStream
        public int readSInt32() throws IOException {
            return decodeZigZag32(readRawVarint32());
        }

        @Override // com.android.framework.protobuf.CodedInputStream
        public long readSInt64() throws IOException {
            return decodeZigZag64(readRawVarint64());
        }

        @Override // com.android.framework.protobuf.CodedInputStream
        public int readRawVarint32() throws IOException {
            int y;
            long tempPos = this.currentByteBufferPos;
            if (this.currentByteBufferLimit != this.currentByteBufferPos) {
                long tempPos2 = tempPos + 1;
                int x = UnsafeUtil.getByte(tempPos);
                if (x >= 0) {
                    this.currentByteBufferPos++;
                    return x;
                } else if (this.currentByteBufferLimit - this.currentByteBufferPos >= 10) {
                    long tempPos3 = tempPos2 + 1;
                    int x2 = (UnsafeUtil.getByte(tempPos2) << 7) ^ x;
                    if (x2 < 0) {
                        y = x2 ^ (-128);
                    } else {
                        long tempPos4 = tempPos3 + 1;
                        int x3 = (UnsafeUtil.getByte(tempPos3) << AcquiredInfo.POWER_PRESS) ^ x2;
                        if (x3 >= 0) {
                            y = x3 ^ 16256;
                            tempPos3 = tempPos4;
                        } else {
                            tempPos3 = tempPos4 + 1;
                            int x4 = (UnsafeUtil.getByte(tempPos4) << 21) ^ x3;
                            if (x4 < 0) {
                                y = (-2080896) ^ x4;
                            } else {
                                long tempPos5 = tempPos3 + 1;
                                int y2 = UnsafeUtil.getByte(tempPos3);
                                int x5 = (x4 ^ (y2 << 28)) ^ 266354560;
                                if (y2 < 0) {
                                    tempPos3 = tempPos5 + 1;
                                    if (UnsafeUtil.getByte(tempPos5) < 0) {
                                        long tempPos6 = tempPos3 + 1;
                                        if (UnsafeUtil.getByte(tempPos3) < 0) {
                                            tempPos3 = tempPos6 + 1;
                                            if (UnsafeUtil.getByte(tempPos6) < 0) {
                                                long tempPos7 = tempPos3 + 1;
                                                if (UnsafeUtil.getByte(tempPos3) < 0) {
                                                    tempPos3 = tempPos7 + 1;
                                                    if (UnsafeUtil.getByte(tempPos7) >= 0) {
                                                        y = x5;
                                                    }
                                                } else {
                                                    y = x5;
                                                    tempPos3 = tempPos7;
                                                }
                                            } else {
                                                y = x5;
                                            }
                                        } else {
                                            y = x5;
                                            tempPos3 = tempPos6;
                                        }
                                    } else {
                                        y = x5;
                                    }
                                } else {
                                    y = x5;
                                    tempPos3 = tempPos5;
                                }
                            }
                        }
                    }
                    this.currentByteBufferPos = tempPos3;
                    return y;
                }
            }
            return (int) readRawVarint64SlowPath();
        }

        @Override // com.android.framework.protobuf.CodedInputStream
        public long readRawVarint64() throws IOException {
            long x;
            long tempPos = this.currentByteBufferPos;
            if (this.currentByteBufferLimit != this.currentByteBufferPos) {
                long tempPos2 = tempPos + 1;
                int y = UnsafeUtil.getByte(tempPos);
                if (y >= 0) {
                    this.currentByteBufferPos++;
                    return y;
                } else if (this.currentByteBufferLimit - this.currentByteBufferPos >= 10) {
                    long tempPos3 = tempPos2 + 1;
                    int y2 = (UnsafeUtil.getByte(tempPos2) << 7) ^ y;
                    if (y2 < 0) {
                        x = y2 ^ (-128);
                    } else {
                        long tempPos4 = tempPos3 + 1;
                        int y3 = (UnsafeUtil.getByte(tempPos3) << AcquiredInfo.POWER_PRESS) ^ y2;
                        if (y3 >= 0) {
                            x = y3 ^ 16256;
                            tempPos3 = tempPos4;
                        } else {
                            tempPos3 = tempPos4 + 1;
                            int y4 = (UnsafeUtil.getByte(tempPos4) << 21) ^ y3;
                            if (y4 >= 0) {
                                long tempPos5 = tempPos3 + 1;
                                long x2 = y4 ^ (UnsafeUtil.getByte(tempPos3) << 28);
                                if (x2 >= 0) {
                                    x = 266354560 ^ x2;
                                    tempPos3 = tempPos5;
                                } else {
                                    long tempPos6 = tempPos5 + 1;
                                    long x3 = (UnsafeUtil.getByte(tempPos5) << 35) ^ x2;
                                    if (x3 < 0) {
                                        x = (-34093383808L) ^ x3;
                                        tempPos3 = tempPos6;
                                    } else {
                                        long tempPos7 = tempPos6 + 1;
                                        long x4 = (UnsafeUtil.getByte(tempPos6) << 42) ^ x3;
                                        if (x4 >= 0) {
                                            x = 4363953127296L ^ x4;
                                            tempPos3 = tempPos7;
                                        } else {
                                            long tempPos8 = tempPos7 + 1;
                                            long x5 = (UnsafeUtil.getByte(tempPos7) << 49) ^ x4;
                                            if (x5 < 0) {
                                                x = (-558586000294016L) ^ x5;
                                                tempPos3 = tempPos8;
                                            } else {
                                                long tempPos9 = tempPos8 + 1;
                                                long x6 = ((UnsafeUtil.getByte(tempPos8) << 56) ^ x5) ^ 71499008037633920L;
                                                if (x6 >= 0) {
                                                    x = x6;
                                                    tempPos3 = tempPos9;
                                                } else {
                                                    tempPos3 = tempPos9 + 1;
                                                    if (UnsafeUtil.getByte(tempPos9) >= 0) {
                                                        x = x6;
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            } else {
                                x = (-2080896) ^ y4;
                            }
                        }
                    }
                    this.currentByteBufferPos = tempPos3;
                    return x;
                }
            }
            return readRawVarint64SlowPath();
        }

        @Override // com.android.framework.protobuf.CodedInputStream
        long readRawVarint64SlowPath() throws IOException {
            long result = 0;
            for (int shift = 0; shift < 64; shift += 7) {
                byte b = readRawByte();
                result |= (b & Byte.MAX_VALUE) << shift;
                if ((b & 128) == 0) {
                    return result;
                }
            }
            throw InvalidProtocolBufferException.malformedVarint();
        }

        @Override // com.android.framework.protobuf.CodedInputStream
        public int readRawLittleEndian32() throws IOException {
            if (currentRemaining() >= 4) {
                long tempPos = this.currentByteBufferPos;
                this.currentByteBufferPos += 4;
                return (UnsafeUtil.getByte(tempPos) & 255) | ((UnsafeUtil.getByte(1 + tempPos) & 255) << 8) | ((UnsafeUtil.getByte(2 + tempPos) & 255) << 16) | ((UnsafeUtil.getByte(3 + tempPos) & 255) << 24);
            }
            return (readRawByte() & 255) | ((readRawByte() & 255) << 8) | ((readRawByte() & 255) << 16) | ((readRawByte() & 255) << 24);
        }

        @Override // com.android.framework.protobuf.CodedInputStream
        public long readRawLittleEndian64() throws IOException {
            if (currentRemaining() >= 8) {
                long tempPos = this.currentByteBufferPos;
                this.currentByteBufferPos += 8;
                return ((UnsafeUtil.getByte(7 + tempPos) & 255) << 56) | (UnsafeUtil.getByte(tempPos) & 255) | ((UnsafeUtil.getByte(1 + tempPos) & 255) << 8) | ((UnsafeUtil.getByte(2 + tempPos) & 255) << 16) | ((UnsafeUtil.getByte(3 + tempPos) & 255) << 24) | ((UnsafeUtil.getByte(4 + tempPos) & 255) << 32) | ((UnsafeUtil.getByte(5 + tempPos) & 255) << 40) | ((UnsafeUtil.getByte(6 + tempPos) & 255) << 48);
            }
            return ((readRawByte() & 255) << 56) | (readRawByte() & 255) | ((readRawByte() & 255) << 8) | ((readRawByte() & 255) << 16) | ((readRawByte() & 255) << 24) | ((readRawByte() & 255) << 32) | ((readRawByte() & 255) << 40) | ((readRawByte() & 255) << 48);
        }

        @Override // com.android.framework.protobuf.CodedInputStream
        public void enableAliasing(boolean enabled) {
            this.enableAliasing = enabled;
        }

        @Override // com.android.framework.protobuf.CodedInputStream
        public void resetSizeCounter() {
            this.startOffset = (int) ((this.totalBytesRead + this.currentByteBufferPos) - this.currentByteBufferStartPos);
        }

        @Override // com.android.framework.protobuf.CodedInputStream
        public int pushLimit(int byteLimit) throws InvalidProtocolBufferException {
            if (byteLimit < 0) {
                throw InvalidProtocolBufferException.negativeSize();
            }
            int byteLimit2 = byteLimit + getTotalBytesRead();
            int oldLimit = this.currentLimit;
            if (byteLimit2 > oldLimit) {
                throw InvalidProtocolBufferException.truncatedMessage();
            }
            this.currentLimit = byteLimit2;
            recomputeBufferSizeAfterLimit();
            return oldLimit;
        }

        private void recomputeBufferSizeAfterLimit() {
            int i = this.totalBufferSize + this.bufferSizeAfterCurrentLimit;
            this.totalBufferSize = i;
            int bufferEnd = i - this.startOffset;
            int i2 = this.currentLimit;
            if (bufferEnd > i2) {
                int i3 = bufferEnd - i2;
                this.bufferSizeAfterCurrentLimit = i3;
                this.totalBufferSize = i - i3;
                return;
            }
            this.bufferSizeAfterCurrentLimit = 0;
        }

        @Override // com.android.framework.protobuf.CodedInputStream
        public void popLimit(int oldLimit) {
            this.currentLimit = oldLimit;
            recomputeBufferSizeAfterLimit();
        }

        @Override // com.android.framework.protobuf.CodedInputStream
        public int getBytesUntilLimit() {
            int i = this.currentLimit;
            if (i == Integer.MAX_VALUE) {
                return -1;
            }
            return i - getTotalBytesRead();
        }

        @Override // com.android.framework.protobuf.CodedInputStream
        public boolean isAtEnd() throws IOException {
            return (((long) this.totalBytesRead) + this.currentByteBufferPos) - this.currentByteBufferStartPos == ((long) this.totalBufferSize);
        }

        @Override // com.android.framework.protobuf.CodedInputStream
        public int getTotalBytesRead() {
            return (int) (((this.totalBytesRead - this.startOffset) + this.currentByteBufferPos) - this.currentByteBufferStartPos);
        }

        @Override // com.android.framework.protobuf.CodedInputStream
        public byte readRawByte() throws IOException {
            if (currentRemaining() == 0) {
                getNextByteBuffer();
            }
            long j = this.currentByteBufferPos;
            this.currentByteBufferPos = 1 + j;
            return UnsafeUtil.getByte(j);
        }

        @Override // com.android.framework.protobuf.CodedInputStream
        public byte[] readRawBytes(int length) throws IOException {
            if (length >= 0 && length <= currentRemaining()) {
                byte[] bytes = new byte[length];
                UnsafeUtil.copyMemory(this.currentByteBufferPos, bytes, 0L, length);
                this.currentByteBufferPos += length;
                return bytes;
            } else if (length >= 0 && length <= remaining()) {
                byte[] bytes2 = new byte[length];
                readRawBytesTo(bytes2, 0, length);
                return bytes2;
            } else if (length <= 0) {
                if (length == 0) {
                    return Internal.EMPTY_BYTE_ARRAY;
                }
                throw InvalidProtocolBufferException.negativeSize();
            } else {
                throw InvalidProtocolBufferException.truncatedMessage();
            }
        }

        private void readRawBytesTo(byte[] bytes, int offset, int length) throws IOException {
            if (length >= 0 && length <= remaining()) {
                int l = length;
                while (l > 0) {
                    if (currentRemaining() == 0) {
                        getNextByteBuffer();
                    }
                    int bytesToCopy = Math.min(l, (int) currentRemaining());
                    UnsafeUtil.copyMemory(this.currentByteBufferPos, bytes, (length - l) + offset, bytesToCopy);
                    l -= bytesToCopy;
                    this.currentByteBufferPos += bytesToCopy;
                }
            } else if (length <= 0) {
                if (length == 0) {
                    return;
                }
                throw InvalidProtocolBufferException.negativeSize();
            } else {
                throw InvalidProtocolBufferException.truncatedMessage();
            }
        }

        @Override // com.android.framework.protobuf.CodedInputStream
        public void skipRawBytes(int length) throws IOException {
            if (length >= 0 && length <= ((this.totalBufferSize - this.totalBytesRead) - this.currentByteBufferPos) + this.currentByteBufferStartPos) {
                int l = length;
                while (l > 0) {
                    if (currentRemaining() == 0) {
                        getNextByteBuffer();
                    }
                    int rl = Math.min(l, (int) currentRemaining());
                    l -= rl;
                    this.currentByteBufferPos += rl;
                }
            } else if (length < 0) {
                throw InvalidProtocolBufferException.negativeSize();
            } else {
                throw InvalidProtocolBufferException.truncatedMessage();
            }
        }

        private void skipRawVarint() throws IOException {
            for (int i = 0; i < 10; i++) {
                if (readRawByte() >= 0) {
                    return;
                }
            }
            throw InvalidProtocolBufferException.malformedVarint();
        }

        private int remaining() {
            return (int) (((this.totalBufferSize - this.totalBytesRead) - this.currentByteBufferPos) + this.currentByteBufferStartPos);
        }

        private long currentRemaining() {
            return this.currentByteBufferLimit - this.currentByteBufferPos;
        }

        private ByteBuffer slice(int begin, int end) throws IOException {
            int prevPos = this.currentByteBuffer.position();
            int prevLimit = this.currentByteBuffer.limit();
            Buffer asBuffer = this.currentByteBuffer;
            try {
                try {
                    asBuffer.position(begin);
                    asBuffer.limit(end);
                    return this.currentByteBuffer.slice();
                } catch (IllegalArgumentException e) {
                    throw InvalidProtocolBufferException.truncatedMessage();
                }
            } finally {
                asBuffer.position(prevPos);
                asBuffer.limit(prevLimit);
            }
        }
    }
}
