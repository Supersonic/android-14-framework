package com.android.internal.org.bouncycastle.crypto.p020io;

import com.android.internal.org.bouncycastle.crypto.Mac;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
/* renamed from: com.android.internal.org.bouncycastle.crypto.io.MacInputStream */
/* loaded from: classes4.dex */
public class MacInputStream extends FilterInputStream {
    protected Mac mac;

    public MacInputStream(InputStream stream, Mac mac) {
        super(stream);
        this.mac = mac;
    }

    @Override // java.io.FilterInputStream, java.io.InputStream
    public int read() throws IOException {
        int b = this.in.read();
        if (b >= 0) {
            this.mac.update((byte) b);
        }
        return b;
    }

    @Override // java.io.FilterInputStream, java.io.InputStream
    public int read(byte[] b, int off, int len) throws IOException {
        int n = this.in.read(b, off, len);
        if (n >= 0) {
            this.mac.update(b, off, n);
        }
        return n;
    }

    public Mac getMac() {
        return this.mac;
    }
}
