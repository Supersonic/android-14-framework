package com.android.internal.util;

import java.io.IOException;
import java.io.InputStream;
import libcore.io.Streams;
/* loaded from: classes3.dex */
public class SizedInputStream extends InputStream {
    private long mLength;
    private final InputStream mWrapped;

    public SizedInputStream(InputStream wrapped, long length) {
        this.mWrapped = wrapped;
        this.mLength = length;
    }

    @Override // java.io.InputStream, java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
        super.close();
        this.mWrapped.close();
    }

    @Override // java.io.InputStream
    public int read() throws IOException {
        return Streams.readSingleByte(this);
    }

    @Override // java.io.InputStream
    public int read(byte[] buffer, int byteOffset, int byteCount) throws IOException {
        long j = this.mLength;
        if (j <= 0) {
            return -1;
        }
        if (byteCount > j) {
            byteCount = (int) j;
        }
        int n = this.mWrapped.read(buffer, byteOffset, byteCount);
        if (n == -1) {
            if (this.mLength > 0) {
                throw new IOException("Unexpected EOF; expected " + this.mLength + " more bytes");
            }
        } else {
            this.mLength -= n;
        }
        return n;
    }
}
