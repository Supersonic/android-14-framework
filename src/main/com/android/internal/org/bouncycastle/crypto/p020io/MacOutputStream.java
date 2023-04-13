package com.android.internal.org.bouncycastle.crypto.p020io;

import com.android.internal.org.bouncycastle.crypto.Mac;
import java.io.IOException;
import java.io.OutputStream;
/* renamed from: com.android.internal.org.bouncycastle.crypto.io.MacOutputStream */
/* loaded from: classes4.dex */
public class MacOutputStream extends OutputStream {
    protected Mac mac;

    public MacOutputStream(Mac mac) {
        this.mac = mac;
    }

    @Override // java.io.OutputStream
    public void write(int b) throws IOException {
        this.mac.update((byte) b);
    }

    @Override // java.io.OutputStream
    public void write(byte[] b, int off, int len) throws IOException {
        this.mac.update(b, off, len);
    }

    public byte[] getMac() {
        byte[] res = new byte[this.mac.getMacSize()];
        this.mac.doFinal(res, 0);
        return res;
    }
}
