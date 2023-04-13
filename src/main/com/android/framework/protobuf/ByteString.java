package com.android.framework.protobuf;

import android.telecom.Logging.Session;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.NoSuchElementException;
@CheckReturnValue
/* loaded from: classes4.dex */
public abstract class ByteString implements Iterable<Byte>, Serializable {
    static final int CONCATENATE_BY_COPY_SIZE = 128;
    public static final ByteString EMPTY = new LiteralByteString(Internal.EMPTY_BYTE_ARRAY);
    static final int MAX_READ_FROM_CHUNK_SIZE = 8192;
    static final int MIN_READ_FROM_CHUNK_SIZE = 256;
    private static final int UNSIGNED_BYTE_MASK = 255;
    private static final Comparator<ByteString> UNSIGNED_LEXICOGRAPHICAL_COMPARATOR;
    private static final ByteArrayCopier byteArrayCopier;
    private int hash = 0;

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes4.dex */
    public interface ByteArrayCopier {
        byte[] copyFrom(byte[] bArr, int i, int i2);
    }

    /* loaded from: classes4.dex */
    public interface ByteIterator extends Iterator<Byte> {
        byte nextByte();
    }

    public abstract ByteBuffer asReadOnlyByteBuffer();

    public abstract List<ByteBuffer> asReadOnlyByteBufferList();

    public abstract byte byteAt(int i);

    public abstract void copyTo(ByteBuffer byteBuffer);

    /* JADX INFO: Access modifiers changed from: protected */
    public abstract void copyToInternal(byte[] bArr, int i, int i2, int i3);

    public abstract boolean equals(Object obj);

    /* JADX INFO: Access modifiers changed from: protected */
    public abstract int getTreeDepth();

    /* JADX INFO: Access modifiers changed from: package-private */
    public abstract byte internalByteAt(int i);

    /* JADX INFO: Access modifiers changed from: protected */
    public abstract boolean isBalanced();

    public abstract boolean isValidUtf8();

    public abstract CodedInputStream newCodedInput();

    public abstract InputStream newInput();

    /* JADX INFO: Access modifiers changed from: protected */
    public abstract int partialHash(int i, int i2, int i3);

    /* JADX INFO: Access modifiers changed from: protected */
    public abstract int partialIsValidUtf8(int i, int i2, int i3);

    public abstract int size();

    public abstract ByteString substring(int i, int i2);

    protected abstract String toStringInternal(Charset charset);

    /* JADX INFO: Access modifiers changed from: package-private */
    public abstract void writeTo(ByteOutput byteOutput) throws IOException;

    public abstract void writeTo(OutputStream outputStream) throws IOException;

    /* JADX INFO: Access modifiers changed from: package-private */
    public abstract void writeToInternal(OutputStream outputStream, int i, int i2) throws IOException;

    /* JADX INFO: Access modifiers changed from: package-private */
    public abstract void writeToReverse(ByteOutput byteOutput) throws IOException;

    static {
        byteArrayCopier = Android.isOnAndroidDevice() ? new SystemByteArrayCopier() : new ArraysByteArrayCopier();
        UNSIGNED_LEXICOGRAPHICAL_COMPARATOR = new Comparator<ByteString>() { // from class: com.android.framework.protobuf.ByteString.2
            /* JADX WARN: Type inference failed for: r0v0, types: [com.android.framework.protobuf.ByteString$ByteIterator] */
            /* JADX WARN: Type inference failed for: r1v0, types: [com.android.framework.protobuf.ByteString$ByteIterator] */
            @Override // java.util.Comparator
            public int compare(ByteString former, ByteString latter) {
                ?? iterator2 = former.iterator2();
                ?? iterator22 = latter.iterator2();
                while (iterator2.hasNext() && iterator22.hasNext()) {
                    int result = Integer.valueOf(ByteString.toInt(iterator2.nextByte())).compareTo(Integer.valueOf(ByteString.toInt(iterator22.nextByte())));
                    if (result != 0) {
                        return result;
                    }
                }
                return Integer.valueOf(former.size()).compareTo(Integer.valueOf(latter.size()));
            }
        };
    }

    /* loaded from: classes4.dex */
    private static final class SystemByteArrayCopier implements ByteArrayCopier {
        private SystemByteArrayCopier() {
        }

        @Override // com.android.framework.protobuf.ByteString.ByteArrayCopier
        public byte[] copyFrom(byte[] bytes, int offset, int size) {
            byte[] copy = new byte[size];
            System.arraycopy(bytes, offset, copy, 0, size);
            return copy;
        }
    }

    /* loaded from: classes4.dex */
    private static final class ArraysByteArrayCopier implements ByteArrayCopier {
        private ArraysByteArrayCopier() {
        }

        @Override // com.android.framework.protobuf.ByteString.ByteArrayCopier
        public byte[] copyFrom(byte[] bytes, int offset, int size) {
            return Arrays.copyOfRange(bytes, offset, offset + size);
        }
    }

    @Override // java.lang.Iterable
    /* renamed from: iterator */
    public Iterator<Byte> iterator2() {
        return new AbstractByteIterator() { // from class: com.android.framework.protobuf.ByteString.1
            private final int limit;
            private int position = 0;

            {
                this.limit = ByteString.this.size();
            }

            @Override // java.util.Iterator
            public boolean hasNext() {
                return this.position < this.limit;
            }

            @Override // com.android.framework.protobuf.ByteString.ByteIterator
            public byte nextByte() {
                int currentPos = this.position;
                if (currentPos >= this.limit) {
                    throw new NoSuchElementException();
                }
                this.position = currentPos + 1;
                return ByteString.this.internalByteAt(currentPos);
            }
        };
    }

    /* loaded from: classes4.dex */
    static abstract class AbstractByteIterator implements ByteIterator {
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // java.util.Iterator
        public final Byte next() {
            return Byte.valueOf(nextByte());
        }

        @Override // java.util.Iterator
        public final void remove() {
            throw new UnsupportedOperationException();
        }
    }

    public final boolean isEmpty() {
        return size() == 0;
    }

    public static final ByteString empty() {
        return EMPTY;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static int toInt(byte value) {
        return value & 255;
    }

    private static int hexDigit(char c) {
        if (c >= '0' && c <= '9') {
            return c - '0';
        }
        if (c >= 'A' && c <= 'F') {
            return (c - 'A') + 10;
        }
        if (c >= 'a' && c <= 'f') {
            return (c - 'a') + 10;
        }
        return -1;
    }

    private static int extractHexDigit(String hexString, int index) {
        int digit = hexDigit(hexString.charAt(index));
        if (digit == -1) {
            throw new NumberFormatException("Invalid hexString " + hexString + " must only contain [0-9a-fA-F] but contained " + hexString.charAt(index) + " at index " + index);
        }
        return digit;
    }

    public static Comparator<ByteString> unsignedLexicographicalComparator() {
        return UNSIGNED_LEXICOGRAPHICAL_COMPARATOR;
    }

    public final ByteString substring(int beginIndex) {
        return substring(beginIndex, size());
    }

    public final boolean startsWith(ByteString prefix) {
        return size() >= prefix.size() && substring(0, prefix.size()).equals(prefix);
    }

    public final boolean endsWith(ByteString suffix) {
        return size() >= suffix.size() && substring(size() - suffix.size()).equals(suffix);
    }

    public static ByteString fromHex(String hexString) {
        if (hexString.length() % 2 != 0) {
            throw new NumberFormatException("Invalid hexString " + hexString + " of length " + hexString.length() + " must be even.");
        }
        byte[] bytes = new byte[hexString.length() / 2];
        for (int i = 0; i < bytes.length; i++) {
            int d1 = extractHexDigit(hexString, i * 2);
            int d2 = extractHexDigit(hexString, (i * 2) + 1);
            bytes[i] = (byte) ((d1 << 4) | d2);
        }
        return new LiteralByteString(bytes);
    }

    public static ByteString copyFrom(byte[] bytes, int offset, int size) {
        checkRange(offset, offset + size, bytes.length);
        return new LiteralByteString(byteArrayCopier.copyFrom(bytes, offset, size));
    }

    public static ByteString copyFrom(byte[] bytes) {
        return copyFrom(bytes, 0, bytes.length);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static ByteString wrap(ByteBuffer buffer) {
        if (buffer.hasArray()) {
            int offset = buffer.arrayOffset();
            return wrap(buffer.array(), buffer.position() + offset, buffer.remaining());
        }
        return new NioByteString(buffer);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static ByteString wrap(byte[] bytes) {
        return new LiteralByteString(bytes);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static ByteString wrap(byte[] bytes, int offset, int length) {
        return new BoundedByteString(bytes, offset, length);
    }

    public static ByteString copyFrom(ByteBuffer bytes, int size) {
        checkRange(0, size, bytes.remaining());
        byte[] copy = new byte[size];
        bytes.get(copy);
        return new LiteralByteString(copy);
    }

    public static ByteString copyFrom(ByteBuffer bytes) {
        return copyFrom(bytes, bytes.remaining());
    }

    public static ByteString copyFrom(String text, String charsetName) throws UnsupportedEncodingException {
        return new LiteralByteString(text.getBytes(charsetName));
    }

    public static ByteString copyFrom(String text, Charset charset) {
        return new LiteralByteString(text.getBytes(charset));
    }

    public static ByteString copyFromUtf8(String text) {
        return new LiteralByteString(text.getBytes(Internal.UTF_8));
    }

    public static ByteString readFrom(InputStream streamToDrain) throws IOException {
        return readFrom(streamToDrain, 256, 8192);
    }

    public static ByteString readFrom(InputStream streamToDrain, int chunkSize) throws IOException {
        return readFrom(streamToDrain, chunkSize, chunkSize);
    }

    public static ByteString readFrom(InputStream streamToDrain, int minChunkSize, int maxChunkSize) throws IOException {
        Collection<ByteString> results = new ArrayList<>();
        int chunkSize = minChunkSize;
        while (true) {
            ByteString chunk = readChunk(streamToDrain, chunkSize);
            if (chunk != null) {
                results.add(chunk);
                chunkSize = Math.min(chunkSize * 2, maxChunkSize);
            } else {
                return copyFrom(results);
            }
        }
    }

    private static ByteString readChunk(InputStream in, int chunkSize) throws IOException {
        byte[] buf = new byte[chunkSize];
        int bytesRead = 0;
        while (bytesRead < chunkSize) {
            int count = in.read(buf, bytesRead, chunkSize - bytesRead);
            if (count == -1) {
                break;
            }
            bytesRead += count;
        }
        if (bytesRead == 0) {
            return null;
        }
        return copyFrom(buf, 0, bytesRead);
    }

    public final ByteString concat(ByteString other) {
        if (Integer.MAX_VALUE - size() < other.size()) {
            throw new IllegalArgumentException("ByteString would be too long: " + size() + "+" + other.size());
        }
        return RopeByteString.concatenate(this, other);
    }

    public static ByteString copyFrom(Iterable<ByteString> byteStrings) {
        int tempSize;
        if (!(byteStrings instanceof Collection)) {
            tempSize = 0;
            Iterator<ByteString> iter = byteStrings.iterator();
            while (iter.hasNext()) {
                iter.next();
                tempSize++;
            }
        } else {
            tempSize = ((Collection) byteStrings).size();
        }
        if (tempSize == 0) {
            return EMPTY;
        }
        return balancedConcat(byteStrings.iterator(), tempSize);
    }

    private static ByteString balancedConcat(Iterator<ByteString> iterator, int length) {
        if (length < 1) {
            throw new IllegalArgumentException(String.format("length (%s) must be >= 1", Integer.valueOf(length)));
        }
        if (length == 1) {
            ByteString result = iterator.next();
            return result;
        }
        int halfLength = length >>> 1;
        ByteString left = balancedConcat(iterator, halfLength);
        ByteString right = balancedConcat(iterator, length - halfLength);
        ByteString result2 = left.concat(right);
        return result2;
    }

    public void copyTo(byte[] target, int offset) {
        copyTo(target, 0, offset, size());
    }

    @Deprecated
    public final void copyTo(byte[] target, int sourceOffset, int targetOffset, int numberToCopy) {
        checkRange(sourceOffset, sourceOffset + numberToCopy, size());
        checkRange(targetOffset, targetOffset + numberToCopy, target.length);
        if (numberToCopy > 0) {
            copyToInternal(target, sourceOffset, targetOffset, numberToCopy);
        }
    }

    public final byte[] toByteArray() {
        int size = size();
        if (size == 0) {
            return Internal.EMPTY_BYTE_ARRAY;
        }
        byte[] result = new byte[size];
        copyToInternal(result, 0, 0, size);
        return result;
    }

    final void writeTo(OutputStream out, int sourceOffset, int numberToWrite) throws IOException {
        checkRange(sourceOffset, sourceOffset + numberToWrite, size());
        if (numberToWrite > 0) {
            writeToInternal(out, sourceOffset, numberToWrite);
        }
    }

    public final String toString(String charsetName) throws UnsupportedEncodingException {
        try {
            return toString(Charset.forName(charsetName));
        } catch (UnsupportedCharsetException e) {
            UnsupportedEncodingException exception = new UnsupportedEncodingException(charsetName);
            exception.initCause(e);
            throw exception;
        }
    }

    public final String toString(Charset charset) {
        return size() == 0 ? "" : toStringInternal(charset);
    }

    public final String toStringUtf8() {
        return toString(Internal.UTF_8);
    }

    /* loaded from: classes4.dex */
    static abstract class LeafByteString extends ByteString {
        /* JADX INFO: Access modifiers changed from: package-private */
        public abstract boolean equalsRange(ByteString byteString, int i, int i2);

        @Override // com.android.framework.protobuf.ByteString
        protected final int getTreeDepth() {
            return 0;
        }

        @Override // com.android.framework.protobuf.ByteString
        protected final boolean isBalanced() {
            return true;
        }

        @Override // com.android.framework.protobuf.ByteString
        void writeToReverse(ByteOutput byteOutput) throws IOException {
            writeTo(byteOutput);
        }
    }

    public final int hashCode() {
        int h = this.hash;
        if (h == 0) {
            int size = size();
            h = partialHash(size, 0, size);
            if (h == 0) {
                h = 1;
            }
            this.hash = h;
        }
        return h;
    }

    public static Output newOutput(int initialCapacity) {
        return new Output(initialCapacity);
    }

    public static Output newOutput() {
        return new Output(128);
    }

    /* loaded from: classes4.dex */
    public static final class Output extends OutputStream {
        private static final byte[] EMPTY_BYTE_ARRAY = new byte[0];
        private byte[] buffer;
        private int bufferPos;
        private final ArrayList<ByteString> flushedBuffers;
        private int flushedBuffersTotalBytes;
        private final int initialCapacity;

        Output(int initialCapacity) {
            if (initialCapacity < 0) {
                throw new IllegalArgumentException("Buffer size < 0");
            }
            this.initialCapacity = initialCapacity;
            this.flushedBuffers = new ArrayList<>();
            this.buffer = new byte[initialCapacity];
        }

        @Override // java.io.OutputStream
        public synchronized void write(int b) {
            if (this.bufferPos == this.buffer.length) {
                flushFullBuffer(1);
            }
            byte[] bArr = this.buffer;
            int i = this.bufferPos;
            this.bufferPos = i + 1;
            bArr[i] = (byte) b;
        }

        @Override // java.io.OutputStream
        public synchronized void write(byte[] b, int offset, int length) {
            byte[] bArr = this.buffer;
            int length2 = bArr.length;
            int i = this.bufferPos;
            if (length <= length2 - i) {
                System.arraycopy(b, offset, bArr, i, length);
                this.bufferPos += length;
            } else {
                int copySize = bArr.length - i;
                System.arraycopy(b, offset, bArr, i, copySize);
                int length3 = length - copySize;
                flushFullBuffer(length3);
                System.arraycopy(b, offset + copySize, this.buffer, 0, length3);
                this.bufferPos = length3;
            }
        }

        public synchronized ByteString toByteString() {
            flushLastBuffer();
            return ByteString.copyFrom(this.flushedBuffers);
        }

        private byte[] copyArray(byte[] buffer, int length) {
            byte[] result = new byte[length];
            System.arraycopy(buffer, 0, result, 0, Math.min(buffer.length, length));
            return result;
        }

        public void writeTo(OutputStream out) throws IOException {
            ByteString[] cachedFlushBuffers;
            byte[] cachedBuffer;
            int cachedBufferPos;
            synchronized (this) {
                ArrayList<ByteString> arrayList = this.flushedBuffers;
                cachedFlushBuffers = (ByteString[]) arrayList.toArray(new ByteString[arrayList.size()]);
                cachedBuffer = this.buffer;
                cachedBufferPos = this.bufferPos;
            }
            for (ByteString byteString : cachedFlushBuffers) {
                byteString.writeTo(out);
            }
            out.write(copyArray(cachedBuffer, cachedBufferPos));
        }

        public synchronized int size() {
            return this.flushedBuffersTotalBytes + this.bufferPos;
        }

        public synchronized void reset() {
            this.flushedBuffers.clear();
            this.flushedBuffersTotalBytes = 0;
            this.bufferPos = 0;
        }

        public String toString() {
            return String.format("<ByteString.Output@%s size=%d>", Integer.toHexString(System.identityHashCode(this)), Integer.valueOf(size()));
        }

        private void flushFullBuffer(int minSize) {
            this.flushedBuffers.add(new LiteralByteString(this.buffer));
            int length = this.flushedBuffersTotalBytes + this.buffer.length;
            this.flushedBuffersTotalBytes = length;
            int newSize = Math.max(this.initialCapacity, Math.max(minSize, length >>> 1));
            this.buffer = new byte[newSize];
            this.bufferPos = 0;
        }

        private void flushLastBuffer() {
            int i = this.bufferPos;
            byte[] bArr = this.buffer;
            if (i < bArr.length) {
                if (i > 0) {
                    byte[] bufferCopy = copyArray(bArr, i);
                    this.flushedBuffers.add(new LiteralByteString(bufferCopy));
                }
            } else {
                this.flushedBuffers.add(new LiteralByteString(this.buffer));
                this.buffer = EMPTY_BYTE_ARRAY;
            }
            this.flushedBuffersTotalBytes += this.bufferPos;
            this.bufferPos = 0;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static CodedBuilder newCodedBuilder(int size) {
        return new CodedBuilder(size);
    }

    /* loaded from: classes4.dex */
    static final class CodedBuilder {
        private final byte[] buffer;
        private final CodedOutputStream output;

        private CodedBuilder(int size) {
            byte[] bArr = new byte[size];
            this.buffer = bArr;
            this.output = CodedOutputStream.newInstance(bArr);
        }

        public ByteString build() {
            this.output.checkNoSpaceLeft();
            return new LiteralByteString(this.buffer);
        }

        public CodedOutputStream getCodedOutput() {
            return this.output;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final int peekCachedHashCode() {
        return this.hash;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void checkIndex(int index, int size) {
        if (((size - (index + 1)) | index) < 0) {
            if (index < 0) {
                throw new ArrayIndexOutOfBoundsException("Index < 0: " + index);
            }
            throw new ArrayIndexOutOfBoundsException("Index > length: " + index + ", " + size);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static int checkRange(int startIndex, int endIndex, int size) {
        int length = endIndex - startIndex;
        if ((startIndex | endIndex | length | (size - endIndex)) < 0) {
            if (startIndex < 0) {
                throw new IndexOutOfBoundsException("Beginning index: " + startIndex + " < 0");
            }
            if (endIndex < startIndex) {
                throw new IndexOutOfBoundsException("Beginning index larger than ending index: " + startIndex + ", " + endIndex);
            }
            throw new IndexOutOfBoundsException("End index: " + endIndex + " >= " + size);
        }
        return length;
    }

    public final String toString() {
        return String.format(Locale.ROOT, "<ByteString@%s size=%d contents=\"%s\">", Integer.toHexString(System.identityHashCode(this)), Integer.valueOf(size()), truncateAndEscapeForDisplay());
    }

    private String truncateAndEscapeForDisplay() {
        return size() <= 50 ? TextFormatEscaper.escapeBytes(this) : TextFormatEscaper.escapeBytes(substring(0, 47)) + Session.TRUNCATE_STRING;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes4.dex */
    public static class LiteralByteString extends LeafByteString {
        private static final long serialVersionUID = 1;
        protected final byte[] bytes;

        LiteralByteString(byte[] bytes) {
            if (bytes == null) {
                throw new NullPointerException();
            }
            this.bytes = bytes;
        }

        @Override // com.android.framework.protobuf.ByteString
        public byte byteAt(int index) {
            return this.bytes[index];
        }

        @Override // com.android.framework.protobuf.ByteString
        byte internalByteAt(int index) {
            return this.bytes[index];
        }

        @Override // com.android.framework.protobuf.ByteString
        public int size() {
            return this.bytes.length;
        }

        @Override // com.android.framework.protobuf.ByteString
        public final ByteString substring(int beginIndex, int endIndex) {
            int length = checkRange(beginIndex, endIndex, size());
            if (length == 0) {
                return ByteString.EMPTY;
            }
            return new BoundedByteString(this.bytes, getOffsetIntoBytes() + beginIndex, length);
        }

        @Override // com.android.framework.protobuf.ByteString
        protected void copyToInternal(byte[] target, int sourceOffset, int targetOffset, int numberToCopy) {
            System.arraycopy(this.bytes, sourceOffset, target, targetOffset, numberToCopy);
        }

        @Override // com.android.framework.protobuf.ByteString
        public final void copyTo(ByteBuffer target) {
            target.put(this.bytes, getOffsetIntoBytes(), size());
        }

        @Override // com.android.framework.protobuf.ByteString
        public final ByteBuffer asReadOnlyByteBuffer() {
            return ByteBuffer.wrap(this.bytes, getOffsetIntoBytes(), size()).asReadOnlyBuffer();
        }

        @Override // com.android.framework.protobuf.ByteString
        public final List<ByteBuffer> asReadOnlyByteBufferList() {
            return Collections.singletonList(asReadOnlyByteBuffer());
        }

        @Override // com.android.framework.protobuf.ByteString
        public final void writeTo(OutputStream outputStream) throws IOException {
            outputStream.write(toByteArray());
        }

        @Override // com.android.framework.protobuf.ByteString
        final void writeToInternal(OutputStream outputStream, int sourceOffset, int numberToWrite) throws IOException {
            outputStream.write(this.bytes, getOffsetIntoBytes() + sourceOffset, numberToWrite);
        }

        @Override // com.android.framework.protobuf.ByteString
        final void writeTo(ByteOutput output) throws IOException {
            output.writeLazy(this.bytes, getOffsetIntoBytes(), size());
        }

        @Override // com.android.framework.protobuf.ByteString
        protected final String toStringInternal(Charset charset) {
            return new String(this.bytes, getOffsetIntoBytes(), size(), charset);
        }

        @Override // com.android.framework.protobuf.ByteString
        public final boolean isValidUtf8() {
            int offset = getOffsetIntoBytes();
            return Utf8.isValidUtf8(this.bytes, offset, size() + offset);
        }

        @Override // com.android.framework.protobuf.ByteString
        protected final int partialIsValidUtf8(int state, int offset, int length) {
            int index = getOffsetIntoBytes() + offset;
            return Utf8.partialIsValidUtf8(state, this.bytes, index, index + length);
        }

        @Override // com.android.framework.protobuf.ByteString
        public final boolean equals(Object other) {
            if (other == this) {
                return true;
            }
            if ((other instanceof ByteString) && size() == ((ByteString) other).size()) {
                if (size() == 0) {
                    return true;
                }
                if (other instanceof LiteralByteString) {
                    LiteralByteString otherAsLiteral = (LiteralByteString) other;
                    int thisHash = peekCachedHashCode();
                    int thatHash = otherAsLiteral.peekCachedHashCode();
                    if (thisHash == 0 || thatHash == 0 || thisHash == thatHash) {
                        return equalsRange((LiteralByteString) other, 0, size());
                    }
                    return false;
                }
                return other.equals(this);
            }
            return false;
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        @Override // com.android.framework.protobuf.ByteString.LeafByteString
        public final boolean equalsRange(ByteString other, int offset, int length) {
            if (length > other.size()) {
                throw new IllegalArgumentException("Length too large: " + length + size());
            }
            if (offset + length > other.size()) {
                throw new IllegalArgumentException("Ran off end of other: " + offset + ", " + length + ", " + other.size());
            }
            if (other instanceof LiteralByteString) {
                LiteralByteString lbsOther = (LiteralByteString) other;
                byte[] thisBytes = this.bytes;
                byte[] otherBytes = lbsOther.bytes;
                int thisLimit = getOffsetIntoBytes() + length;
                int thisIndex = getOffsetIntoBytes();
                int otherIndex = lbsOther.getOffsetIntoBytes() + offset;
                while (thisIndex < thisLimit) {
                    if (thisBytes[thisIndex] != otherBytes[otherIndex]) {
                        return false;
                    }
                    thisIndex++;
                    otherIndex++;
                }
                return true;
            }
            return other.substring(offset, offset + length).equals(substring(0, length));
        }

        @Override // com.android.framework.protobuf.ByteString
        protected final int partialHash(int h, int offset, int length) {
            return Internal.partialHash(h, this.bytes, getOffsetIntoBytes() + offset, length);
        }

        @Override // com.android.framework.protobuf.ByteString
        public final InputStream newInput() {
            return new ByteArrayInputStream(this.bytes, getOffsetIntoBytes(), size());
        }

        @Override // com.android.framework.protobuf.ByteString
        public final CodedInputStream newCodedInput() {
            return CodedInputStream.newInstance(this.bytes, getOffsetIntoBytes(), size(), true);
        }

        protected int getOffsetIntoBytes() {
            return 0;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes4.dex */
    public static final class BoundedByteString extends LiteralByteString {
        private static final long serialVersionUID = 1;
        private final int bytesLength;
        private final int bytesOffset;

        BoundedByteString(byte[] bytes, int offset, int length) {
            super(bytes);
            checkRange(offset, offset + length, bytes.length);
            this.bytesOffset = offset;
            this.bytesLength = length;
        }

        @Override // com.android.framework.protobuf.ByteString.LiteralByteString, com.android.framework.protobuf.ByteString
        public byte byteAt(int index) {
            checkIndex(index, size());
            return this.bytes[this.bytesOffset + index];
        }

        @Override // com.android.framework.protobuf.ByteString.LiteralByteString, com.android.framework.protobuf.ByteString
        byte internalByteAt(int index) {
            return this.bytes[this.bytesOffset + index];
        }

        @Override // com.android.framework.protobuf.ByteString.LiteralByteString, com.android.framework.protobuf.ByteString
        public int size() {
            return this.bytesLength;
        }

        @Override // com.android.framework.protobuf.ByteString.LiteralByteString
        protected int getOffsetIntoBytes() {
            return this.bytesOffset;
        }

        @Override // com.android.framework.protobuf.ByteString.LiteralByteString, com.android.framework.protobuf.ByteString
        protected void copyToInternal(byte[] target, int sourceOffset, int targetOffset, int numberToCopy) {
            System.arraycopy(this.bytes, getOffsetIntoBytes() + sourceOffset, target, targetOffset, numberToCopy);
        }

        Object writeReplace() {
            return ByteString.wrap(toByteArray());
        }

        private void readObject(ObjectInputStream in) throws IOException {
            throw new InvalidObjectException("BoundedByteStream instances are not to be serialized directly");
        }
    }
}
