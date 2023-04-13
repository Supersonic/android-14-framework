package com.android.internal.org.bouncycastle.util.p027io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
/* renamed from: com.android.internal.org.bouncycastle.util.io.TeeInputStream */
/* loaded from: classes4.dex */
public class TeeInputStream extends InputStream {
    private final InputStream input;
    private final OutputStream output;

    public TeeInputStream(InputStream input, OutputStream output) {
        this.input = input;
        this.output = output;
    }

    @Override // java.io.InputStream
    public int available() throws IOException {
        return this.input.available();
    }

    @Override // java.io.InputStream
    public int read(byte[] buf) throws IOException {
        return read(buf, 0, buf.length);
    }

    @Override // java.io.InputStream
    public int read(byte[] buf, int off, int len) throws IOException {
        int i = this.input.read(buf, off, len);
        if (i > 0) {
            this.output.write(buf, off, i);
        }
        return i;
    }

    @Override // java.io.InputStream
    public int read() throws IOException {
        int i = this.input.read();
        if (i >= 0) {
            this.output.write(i);
        }
        return i;
    }

    @Override // java.io.InputStream, java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
        this.input.close();
        this.output.close();
    }

    public OutputStream getOutputStream() {
        return this.output;
    }
}
