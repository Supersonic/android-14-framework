package com.android.internal.org.bouncycastle.crypto.p020io;

import com.android.internal.org.bouncycastle.crypto.Digest;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
/* renamed from: com.android.internal.org.bouncycastle.crypto.io.DigestInputStream */
/* loaded from: classes4.dex */
public class DigestInputStream extends FilterInputStream {
    protected Digest digest;

    public DigestInputStream(InputStream stream, Digest digest) {
        super(stream);
        this.digest = digest;
    }

    @Override // java.io.FilterInputStream, java.io.InputStream
    public int read() throws IOException {
        int b = this.in.read();
        if (b >= 0) {
            this.digest.update((byte) b);
        }
        return b;
    }

    @Override // java.io.FilterInputStream, java.io.InputStream
    public int read(byte[] b, int off, int len) throws IOException {
        int n = this.in.read(b, off, len);
        if (n > 0) {
            this.digest.update(b, off, n);
        }
        return n;
    }

    public Digest getDigest() {
        return this.digest;
    }
}
