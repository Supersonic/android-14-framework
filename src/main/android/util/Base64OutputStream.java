package android.util;

import android.util.Base64;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
/* loaded from: classes3.dex */
public class Base64OutputStream extends FilterOutputStream {
    private static byte[] EMPTY = new byte[0];
    private int bpos;
    private byte[] buffer;
    private final Base64.Coder coder;
    private final int flags;

    public Base64OutputStream(OutputStream out, int flags) {
        this(out, flags, true);
    }

    public Base64OutputStream(OutputStream out, int flags, boolean encode) {
        super(out);
        this.buffer = null;
        this.bpos = 0;
        this.flags = flags;
        if (encode) {
            this.coder = new Base64.Encoder(flags, null);
        } else {
            this.coder = new Base64.Decoder(flags, null);
        }
    }

    @Override // java.io.FilterOutputStream, java.io.OutputStream
    public void write(int b) throws IOException {
        if (this.buffer == null) {
            this.buffer = new byte[1024];
        }
        int i = this.bpos;
        byte[] bArr = this.buffer;
        if (i >= bArr.length) {
            internalWrite(bArr, 0, i, false);
            this.bpos = 0;
        }
        byte[] bArr2 = this.buffer;
        int i2 = this.bpos;
        this.bpos = i2 + 1;
        bArr2[i2] = (byte) b;
    }

    private void flushBuffer() throws IOException {
        int i = this.bpos;
        if (i > 0) {
            internalWrite(this.buffer, 0, i, false);
            this.bpos = 0;
        }
    }

    @Override // java.io.FilterOutputStream, java.io.OutputStream
    public void write(byte[] b, int off, int len) throws IOException {
        if (len <= 0) {
            return;
        }
        flushBuffer();
        internalWrite(b, off, len, false);
    }

    @Override // java.io.FilterOutputStream, java.io.OutputStream, java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
        IOException thrown = null;
        try {
            flushBuffer();
            internalWrite(EMPTY, 0, 0, true);
        } catch (IOException e) {
            thrown = e;
        }
        try {
            if ((this.flags & 16) == 0) {
                this.out.close();
            } else {
                this.out.flush();
            }
        } catch (IOException e2) {
            if (thrown == null) {
                thrown = e2;
            } else {
                thrown.addSuppressed(e2);
            }
        }
        if (thrown != null) {
            throw thrown;
        }
    }

    private void internalWrite(byte[] b, int off, int len, boolean finish) throws IOException {
        Base64.Coder coder = this.coder;
        coder.output = embiggen(coder.output, this.coder.maxOutputSize(len));
        if (!this.coder.process(b, off, len, finish)) {
            throw new Base64DataException("bad base-64");
        }
        this.out.write(this.coder.output, 0, this.coder.f467op);
    }

    private byte[] embiggen(byte[] b, int len) {
        if (b == null || b.length < len) {
            return new byte[len];
        }
        return b;
    }
}
