package android.content.p001pm;

import com.android.internal.util.ArrayUtils;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
/* renamed from: android.content.pm.LimitedLengthInputStream */
/* loaded from: classes.dex */
public class LimitedLengthInputStream extends FilterInputStream {
    private final long mEnd;
    private long mOffset;

    public LimitedLengthInputStream(InputStream in, long offset, long length) throws IOException {
        super(in);
        if (in == null) {
            throw new IOException("in == null");
        }
        if (offset < 0) {
            throw new IOException("offset < 0");
        }
        if (length < 0) {
            throw new IOException("length < 0");
        }
        if (length > Long.MAX_VALUE - offset) {
            throw new IOException("offset + length > Long.MAX_VALUE");
        }
        this.mEnd = offset + length;
        skip(offset);
        this.mOffset = offset;
    }

    @Override // java.io.FilterInputStream, java.io.InputStream
    public synchronized int read() throws IOException {
        long j = this.mOffset;
        if (j >= this.mEnd) {
            return -1;
        }
        this.mOffset = j + 1;
        return super.read();
    }

    @Override // java.io.FilterInputStream, java.io.InputStream
    public int read(byte[] buffer, int offset, int byteCount) throws IOException {
        if (this.mOffset >= this.mEnd) {
            return -1;
        }
        int arrayLength = buffer.length;
        ArrayUtils.throwsIfOutOfBounds(arrayLength, offset, byteCount);
        long j = this.mOffset;
        if (j > Long.MAX_VALUE - byteCount) {
            throw new IOException("offset out of bounds: " + this.mOffset + " + " + byteCount);
        }
        long j2 = this.mEnd;
        if (byteCount + j > j2) {
            byteCount = (int) (j2 - j);
        }
        int numRead = super.read(buffer, offset, byteCount);
        this.mOffset += numRead;
        return numRead;
    }

    @Override // java.io.FilterInputStream, java.io.InputStream
    public int read(byte[] buffer) throws IOException {
        return read(buffer, 0, buffer.length);
    }
}
