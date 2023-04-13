package com.android.internal.org.bouncycastle.asn1;

import com.android.internal.org.bouncycastle.util.p027io.Streams;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
/* loaded from: classes4.dex */
class DefiniteLengthInputStream extends LimitedInputStream {
    private static final byte[] EMPTY_BYTES = new byte[0];
    private final int _originalLength;
    private int _remaining;

    /* JADX INFO: Access modifiers changed from: package-private */
    public DefiniteLengthInputStream(InputStream in, int length, int limit) {
        super(in, limit);
        if (length < 0) {
            throw new IllegalArgumentException("negative lengths not allowed");
        }
        this._originalLength = length;
        this._remaining = length;
        if (length == 0) {
            setParentEofDetect(true);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int getRemaining() {
        return this._remaining;
    }

    @Override // java.io.InputStream
    public int read() throws IOException {
        if (this._remaining == 0) {
            return -1;
        }
        int b = this._in.read();
        if (b < 0) {
            throw new EOFException("DEF length " + this._originalLength + " object truncated by " + this._remaining);
        }
        int i = this._remaining - 1;
        this._remaining = i;
        if (i == 0) {
            setParentEofDetect(true);
        }
        return b;
    }

    @Override // java.io.InputStream
    public int read(byte[] buf, int off, int len) throws IOException {
        int i = this._remaining;
        if (i == 0) {
            return -1;
        }
        int toRead = Math.min(len, i);
        int numRead = this._in.read(buf, off, toRead);
        if (numRead < 0) {
            throw new EOFException("DEF length " + this._originalLength + " object truncated by " + this._remaining);
        }
        int i2 = this._remaining - numRead;
        this._remaining = i2;
        if (i2 == 0) {
            setParentEofDetect(true);
        }
        return numRead;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void readAllIntoByteArray(byte[] buf) throws IOException {
        int i = this._remaining;
        if (i != buf.length) {
            throw new IllegalArgumentException("buffer length not right for data");
        }
        if (i == 0) {
            return;
        }
        int limit = getLimit();
        int i2 = this._remaining;
        if (i2 >= limit) {
            throw new IOException("corrupted stream - out of bounds length found: " + this._remaining + " >= " + limit);
        }
        int readFully = i2 - Streams.readFully(this._in, buf);
        this._remaining = readFully;
        if (readFully != 0) {
            throw new EOFException("DEF length " + this._originalLength + " object truncated by " + this._remaining);
        }
        setParentEofDetect(true);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public byte[] toByteArray() throws IOException {
        if (this._remaining == 0) {
            return EMPTY_BYTES;
        }
        int limit = getLimit();
        int i = this._remaining;
        if (i >= limit) {
            throw new IOException("corrupted stream - out of bounds length found: " + this._remaining + " >= " + limit);
        }
        byte[] bytes = new byte[i];
        int readFully = i - Streams.readFully(this._in, bytes);
        this._remaining = readFully;
        if (readFully != 0) {
            throw new EOFException("DEF length " + this._originalLength + " object truncated by " + this._remaining);
        }
        setParentEofDetect(true);
        return bytes;
    }
}
