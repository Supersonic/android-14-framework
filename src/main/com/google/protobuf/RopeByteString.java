package com.google.protobuf;

import com.google.protobuf.ByteString;
import java.io.IOException;
import java.io.InputStream;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public final class RopeByteString extends ByteString {
    static final int[] minLengthByDepth = {1, 1, 2, 3, 5, 8, 13, 21, 34, 55, 89, 144, 233, 377, 610, 987, 1597, 2584, 4181, 6765, 10946, 17711, 28657, 46368, 75025, 121393, 196418, 317811, 514229, 832040, 1346269, 2178309, 3524578, 5702887, 9227465, 14930352, 24157817, 39088169, 63245986, 102334155, 165580141, 267914296, 433494437, 701408733, 1134903170, 1836311903, Reader.READ_DONE};
    private static final long serialVersionUID = 1;
    private final ByteString left;
    private final int leftLength;
    private final ByteString right;
    private final int totalLength;
    private final int treeDepth;

    private RopeByteString(ByteString left, ByteString right) {
        this.left = left;
        this.right = right;
        int size = left.size();
        this.leftLength = size;
        this.totalLength = size + right.size();
        this.treeDepth = Math.max(left.getTreeDepth(), right.getTreeDepth()) + 1;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static ByteString concatenate(ByteString left, ByteString right) {
        if (right.size() == 0) {
            return left;
        }
        if (left.size() == 0) {
            return right;
        }
        int newLength = left.size() + right.size();
        if (newLength < 128) {
            return concatenateBytes(left, right);
        }
        if (left instanceof RopeByteString) {
            RopeByteString leftRope = (RopeByteString) left;
            if (leftRope.right.size() + right.size() < 128) {
                ByteString newRight = concatenateBytes(leftRope.right, right);
                return new RopeByteString(leftRope.left, newRight);
            }
            ByteString newRight2 = leftRope.left;
            if (newRight2.getTreeDepth() > leftRope.right.getTreeDepth() && leftRope.getTreeDepth() > right.getTreeDepth()) {
                ByteString newRight3 = new RopeByteString(leftRope.right, right);
                return new RopeByteString(leftRope.left, newRight3);
            }
        }
        int newDepth = Math.max(left.getTreeDepth(), right.getTreeDepth()) + 1;
        if (newLength >= minLength(newDepth)) {
            return new RopeByteString(left, right);
        }
        return new Balancer().balance(left, right);
    }

    private static ByteString concatenateBytes(ByteString left, ByteString right) {
        int leftSize = left.size();
        int rightSize = right.size();
        byte[] bytes = new byte[leftSize + rightSize];
        left.copyTo(bytes, 0, 0, leftSize);
        right.copyTo(bytes, 0, leftSize, rightSize);
        return ByteString.wrap(bytes);
    }

    static RopeByteString newInstanceForTest(ByteString left, ByteString right) {
        return new RopeByteString(left, right);
    }

    static int minLength(int depth) {
        int[] iArr = minLengthByDepth;
        if (depth >= iArr.length) {
            return Reader.READ_DONE;
        }
        return iArr[depth];
    }

    @Override // com.google.protobuf.ByteString
    public byte byteAt(int index) {
        checkIndex(index, this.totalLength);
        return internalByteAt(index);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.google.protobuf.ByteString
    public byte internalByteAt(int index) {
        int i = this.leftLength;
        if (index < i) {
            return this.left.internalByteAt(index);
        }
        return this.right.internalByteAt(index - i);
    }

    @Override // com.google.protobuf.ByteString
    public int size() {
        return this.totalLength;
    }

    @Override // com.google.protobuf.ByteString, java.lang.Iterable
    /* renamed from: iterator */
    public Iterator<Byte> iterator2() {
        return new ByteString.AbstractByteIterator() { // from class: com.google.protobuf.RopeByteString.1
            ByteString.ByteIterator current = nextPiece();
            final PieceIterator pieces;

            {
                this.pieces = new PieceIterator(RopeByteString.this);
            }

            /* JADX WARN: Type inference failed for: r0v5, types: [com.google.protobuf.ByteString$ByteIterator] */
            private ByteString.ByteIterator nextPiece() {
                if (this.pieces.hasNext()) {
                    return this.pieces.next().iterator2();
                }
                return null;
            }

            @Override // java.util.Iterator
            public boolean hasNext() {
                return this.current != null;
            }

            @Override // com.google.protobuf.ByteString.ByteIterator
            public byte nextByte() {
                ByteString.ByteIterator byteIterator = this.current;
                if (byteIterator == null) {
                    throw new NoSuchElementException();
                }
                byte b = byteIterator.nextByte();
                if (!this.current.hasNext()) {
                    this.current = nextPiece();
                }
                return b;
            }
        };
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.google.protobuf.ByteString
    public int getTreeDepth() {
        return this.treeDepth;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.google.protobuf.ByteString
    public boolean isBalanced() {
        return this.totalLength >= minLength(this.treeDepth);
    }

    @Override // com.google.protobuf.ByteString
    public ByteString substring(int beginIndex, int endIndex) {
        int length = checkRange(beginIndex, endIndex, this.totalLength);
        if (length == 0) {
            return ByteString.EMPTY;
        }
        if (length == this.totalLength) {
            return this;
        }
        int i = this.leftLength;
        if (endIndex <= i) {
            return this.left.substring(beginIndex, endIndex);
        }
        if (beginIndex >= i) {
            return this.right.substring(beginIndex - i, endIndex - i);
        }
        ByteString leftSub = this.left.substring(beginIndex);
        ByteString rightSub = this.right.substring(0, endIndex - this.leftLength);
        return new RopeByteString(leftSub, rightSub);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.google.protobuf.ByteString
    public void copyToInternal(byte[] target, int sourceOffset, int targetOffset, int numberToCopy) {
        int i = sourceOffset + numberToCopy;
        int i2 = this.leftLength;
        if (i <= i2) {
            this.left.copyToInternal(target, sourceOffset, targetOffset, numberToCopy);
        } else if (sourceOffset >= i2) {
            this.right.copyToInternal(target, sourceOffset - i2, targetOffset, numberToCopy);
        } else {
            int leftLength = i2 - sourceOffset;
            this.left.copyToInternal(target, sourceOffset, targetOffset, leftLength);
            this.right.copyToInternal(target, 0, targetOffset + leftLength, numberToCopy - leftLength);
        }
    }

    @Override // com.google.protobuf.ByteString
    public void copyTo(ByteBuffer target) {
        this.left.copyTo(target);
        this.right.copyTo(target);
    }

    @Override // com.google.protobuf.ByteString
    public ByteBuffer asReadOnlyByteBuffer() {
        ByteBuffer byteBuffer = ByteBuffer.wrap(toByteArray());
        return byteBuffer.asReadOnlyBuffer();
    }

    @Override // com.google.protobuf.ByteString
    public List<ByteBuffer> asReadOnlyByteBufferList() {
        List<ByteBuffer> result = new ArrayList<>();
        PieceIterator pieces = new PieceIterator(this);
        while (pieces.hasNext()) {
            ByteString.LeafByteString byteString = pieces.next();
            result.add(byteString.asReadOnlyByteBuffer());
        }
        return result;
    }

    @Override // com.google.protobuf.ByteString
    public void writeTo(OutputStream outputStream) throws IOException {
        this.left.writeTo(outputStream);
        this.right.writeTo(outputStream);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.google.protobuf.ByteString
    public void writeToInternal(OutputStream out, int sourceOffset, int numberToWrite) throws IOException {
        int i = sourceOffset + numberToWrite;
        int i2 = this.leftLength;
        if (i <= i2) {
            this.left.writeToInternal(out, sourceOffset, numberToWrite);
        } else if (sourceOffset >= i2) {
            this.right.writeToInternal(out, sourceOffset - i2, numberToWrite);
        } else {
            int numberToWriteInLeft = i2 - sourceOffset;
            this.left.writeToInternal(out, sourceOffset, numberToWriteInLeft);
            this.right.writeToInternal(out, 0, numberToWrite - numberToWriteInLeft);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.google.protobuf.ByteString
    public void writeTo(ByteOutput output) throws IOException {
        this.left.writeTo(output);
        this.right.writeTo(output);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.google.protobuf.ByteString
    public void writeToReverse(ByteOutput output) throws IOException {
        this.right.writeToReverse(output);
        this.left.writeToReverse(output);
    }

    @Override // com.google.protobuf.ByteString
    protected String toStringInternal(Charset charset) {
        return new String(toByteArray(), charset);
    }

    @Override // com.google.protobuf.ByteString
    public boolean isValidUtf8() {
        int leftPartial = this.left.partialIsValidUtf8(0, 0, this.leftLength);
        ByteString byteString = this.right;
        int state = byteString.partialIsValidUtf8(leftPartial, 0, byteString.size());
        return state == 0;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.google.protobuf.ByteString
    public int partialIsValidUtf8(int state, int offset, int length) {
        int toIndex = offset + length;
        int i = this.leftLength;
        if (toIndex <= i) {
            return this.left.partialIsValidUtf8(state, offset, length);
        }
        if (offset >= i) {
            return this.right.partialIsValidUtf8(state, offset - i, length);
        }
        int leftLength = i - offset;
        int leftPartial = this.left.partialIsValidUtf8(state, offset, leftLength);
        return this.right.partialIsValidUtf8(leftPartial, 0, length - leftLength);
    }

    @Override // com.google.protobuf.ByteString
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (other instanceof ByteString) {
            ByteString otherByteString = (ByteString) other;
            if (this.totalLength != otherByteString.size()) {
                return false;
            }
            if (this.totalLength == 0) {
                return true;
            }
            int thisHash = peekCachedHashCode();
            int thatHash = otherByteString.peekCachedHashCode();
            if (thisHash == 0 || thatHash == 0 || thisHash == thatHash) {
                return equalsFragments(otherByteString);
            }
            return false;
        }
        return false;
    }

    private boolean equalsFragments(ByteString other) {
        boolean stillEqual;
        int thisOffset = 0;
        Iterator<ByteString.LeafByteString> thisIter = new PieceIterator(this);
        ByteString.LeafByteString thisString = thisIter.next();
        int thatOffset = 0;
        Iterator<ByteString.LeafByteString> thatIter = new PieceIterator(other);
        ByteString.LeafByteString thatString = thatIter.next();
        int pos = 0;
        while (true) {
            int thisRemaining = thisString.size() - thisOffset;
            int thatRemaining = thatString.size() - thatOffset;
            int bytesToCompare = Math.min(thisRemaining, thatRemaining);
            if (thisOffset == 0) {
                stillEqual = thisString.equalsRange(thatString, thatOffset, bytesToCompare);
            } else {
                stillEqual = thatString.equalsRange(thisString, thisOffset, bytesToCompare);
            }
            if (!stillEqual) {
                return false;
            }
            pos += bytesToCompare;
            int i = this.totalLength;
            if (pos >= i) {
                if (pos == i) {
                    return true;
                }
                throw new IllegalStateException();
            }
            if (bytesToCompare == thisRemaining) {
                thisOffset = 0;
                ByteString.LeafByteString thisString2 = thisIter.next();
                thisString = thisString2;
            } else {
                thisOffset += bytesToCompare;
                thisString = thisString;
            }
            if (bytesToCompare == thatRemaining) {
                thatOffset = 0;
                ByteString.LeafByteString thatString2 = thatIter.next();
                thatString = thatString2;
            } else {
                thatOffset += bytesToCompare;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.google.protobuf.ByteString
    public int partialHash(int h, int offset, int length) {
        int toIndex = offset + length;
        int i = this.leftLength;
        if (toIndex <= i) {
            return this.left.partialHash(h, offset, length);
        }
        if (offset >= i) {
            return this.right.partialHash(h, offset - i, length);
        }
        int leftLength = i - offset;
        int leftPartial = this.left.partialHash(h, offset, leftLength);
        return this.right.partialHash(leftPartial, 0, length - leftLength);
    }

    @Override // com.google.protobuf.ByteString
    public CodedInputStream newCodedInput() {
        return CodedInputStream.newInstance((Iterable<ByteBuffer>) asReadOnlyByteBufferList(), true);
    }

    @Override // com.google.protobuf.ByteString
    public InputStream newInput() {
        return new RopeInputStream();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class Balancer {
        private final ArrayDeque<ByteString> prefixesStack;

        private Balancer() {
            this.prefixesStack = new ArrayDeque<>();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public ByteString balance(ByteString left, ByteString right) {
            doBalance(left);
            doBalance(right);
            ByteString partialString = this.prefixesStack.pop();
            while (!this.prefixesStack.isEmpty()) {
                ByteString newLeft = this.prefixesStack.pop();
                partialString = new RopeByteString(newLeft, partialString);
            }
            return partialString;
        }

        private void doBalance(ByteString root) {
            if (root.isBalanced()) {
                insert(root);
            } else if (root instanceof RopeByteString) {
                RopeByteString rbs = (RopeByteString) root;
                doBalance(rbs.left);
                doBalance(rbs.right);
            } else {
                throw new IllegalArgumentException("Has a new type of ByteString been created? Found " + root.getClass());
            }
        }

        private void insert(ByteString byteString) {
            int depthBin = getDepthBinForLength(byteString.size());
            int binEnd = RopeByteString.minLength(depthBin + 1);
            if (this.prefixesStack.isEmpty() || this.prefixesStack.peek().size() >= binEnd) {
                this.prefixesStack.push(byteString);
                return;
            }
            int binStart = RopeByteString.minLength(depthBin);
            ByteString newTree = this.prefixesStack.pop();
            while (!this.prefixesStack.isEmpty() && this.prefixesStack.peek().size() < binStart) {
                ByteString left = this.prefixesStack.pop();
                newTree = new RopeByteString(left, newTree);
            }
            ByteString newTree2 = new RopeByteString(newTree, byteString);
            while (!this.prefixesStack.isEmpty()) {
                int binEnd2 = RopeByteString.minLength(getDepthBinForLength(newTree2.size()) + 1);
                if (this.prefixesStack.peek().size() >= binEnd2) {
                    break;
                }
                ByteString left2 = this.prefixesStack.pop();
                newTree2 = new RopeByteString(left2, newTree2);
            }
            this.prefixesStack.push(newTree2);
        }

        private int getDepthBinForLength(int length) {
            int depth = Arrays.binarySearch(RopeByteString.minLengthByDepth, length);
            if (depth < 0) {
                int insertionPoint = -(depth + 1);
                return insertionPoint - 1;
            }
            return depth;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static final class PieceIterator implements Iterator<ByteString.LeafByteString> {
        private final ArrayDeque<RopeByteString> breadCrumbs;
        private ByteString.LeafByteString next;

        private PieceIterator(ByteString root) {
            if (root instanceof RopeByteString) {
                RopeByteString rbs = (RopeByteString) root;
                ArrayDeque<RopeByteString> arrayDeque = new ArrayDeque<>(rbs.getTreeDepth());
                this.breadCrumbs = arrayDeque;
                arrayDeque.push(rbs);
                this.next = getLeafByLeft(rbs.left);
                return;
            }
            this.breadCrumbs = null;
            this.next = (ByteString.LeafByteString) root;
        }

        private ByteString.LeafByteString getLeafByLeft(ByteString root) {
            ByteString pos = root;
            while (pos instanceof RopeByteString) {
                RopeByteString rbs = (RopeByteString) pos;
                this.breadCrumbs.push(rbs);
                pos = rbs.left;
            }
            return (ByteString.LeafByteString) pos;
        }

        private ByteString.LeafByteString getNextNonEmptyLeaf() {
            ByteString.LeafByteString result;
            do {
                ArrayDeque<RopeByteString> arrayDeque = this.breadCrumbs;
                if (arrayDeque != null && !arrayDeque.isEmpty()) {
                    result = getLeafByLeft(this.breadCrumbs.pop().right);
                } else {
                    return null;
                }
            } while (result.isEmpty());
            return result;
        }

        @Override // java.util.Iterator
        public boolean hasNext() {
            return this.next != null;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // java.util.Iterator
        public ByteString.LeafByteString next() {
            if (this.next == null) {
                throw new NoSuchElementException();
            }
            ByteString.LeafByteString result = this.next;
            this.next = getNextNonEmptyLeaf();
            return result;
        }

        @Override // java.util.Iterator
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    Object writeReplace() {
        return ByteString.wrap(toByteArray());
    }

    private void readObject(ObjectInputStream in) throws IOException {
        throw new InvalidObjectException("RopeByteStream instances are not to be serialized directly");
    }

    /* loaded from: classes.dex */
    private class RopeInputStream extends InputStream {
        private ByteString.LeafByteString currentPiece;
        private int currentPieceIndex;
        private int currentPieceOffsetInRope;
        private int currentPieceSize;
        private int mark;
        private PieceIterator pieceIterator;

        public RopeInputStream() {
            initialize();
        }

        @Override // java.io.InputStream
        public int read(byte[] b, int offset, int length) {
            if (b == null) {
                throw new NullPointerException();
            }
            if (offset < 0 || length < 0 || length > b.length - offset) {
                throw new IndexOutOfBoundsException();
            }
            int bytesRead = readSkipInternal(b, offset, length);
            if (bytesRead == 0 && (length > 0 || availableInternal() == 0)) {
                return -1;
            }
            return bytesRead;
        }

        @Override // java.io.InputStream
        public long skip(long length) {
            if (length < 0) {
                throw new IndexOutOfBoundsException();
            }
            if (length > 2147483647L) {
                length = 2147483647L;
            }
            return readSkipInternal(null, 0, (int) length);
        }

        private int readSkipInternal(byte[] b, int offset, int length) {
            int bytesRemaining = length;
            while (bytesRemaining > 0) {
                advanceIfCurrentPieceFullyRead();
                if (this.currentPiece == null) {
                    break;
                }
                int currentPieceRemaining = this.currentPieceSize - this.currentPieceIndex;
                int count = Math.min(currentPieceRemaining, bytesRemaining);
                if (b != null) {
                    this.currentPiece.copyTo(b, this.currentPieceIndex, offset, count);
                    offset += count;
                }
                this.currentPieceIndex += count;
                bytesRemaining -= count;
            }
            return length - bytesRemaining;
        }

        @Override // java.io.InputStream
        public int read() throws IOException {
            advanceIfCurrentPieceFullyRead();
            ByteString.LeafByteString leafByteString = this.currentPiece;
            if (leafByteString == null) {
                return -1;
            }
            int i = this.currentPieceIndex;
            this.currentPieceIndex = i + 1;
            return leafByteString.byteAt(i) & 255;
        }

        @Override // java.io.InputStream
        public int available() throws IOException {
            return availableInternal();
        }

        @Override // java.io.InputStream
        public boolean markSupported() {
            return true;
        }

        @Override // java.io.InputStream
        public void mark(int readAheadLimit) {
            this.mark = this.currentPieceOffsetInRope + this.currentPieceIndex;
        }

        @Override // java.io.InputStream
        public synchronized void reset() {
            initialize();
            readSkipInternal(null, 0, this.mark);
        }

        private void initialize() {
            PieceIterator pieceIterator = new PieceIterator(RopeByteString.this);
            this.pieceIterator = pieceIterator;
            ByteString.LeafByteString next = pieceIterator.next();
            this.currentPiece = next;
            this.currentPieceSize = next.size();
            this.currentPieceIndex = 0;
            this.currentPieceOffsetInRope = 0;
        }

        private void advanceIfCurrentPieceFullyRead() {
            if (this.currentPiece != null) {
                int i = this.currentPieceIndex;
                int i2 = this.currentPieceSize;
                if (i == i2) {
                    this.currentPieceOffsetInRope += i2;
                    this.currentPieceIndex = 0;
                    if (this.pieceIterator.hasNext()) {
                        ByteString.LeafByteString next = this.pieceIterator.next();
                        this.currentPiece = next;
                        this.currentPieceSize = next.size();
                        return;
                    }
                    this.currentPiece = null;
                    this.currentPieceSize = 0;
                }
            }
        }

        private int availableInternal() {
            int bytesRead = this.currentPieceOffsetInRope + this.currentPieceIndex;
            return RopeByteString.this.size() - bytesRead;
        }
    }
}
