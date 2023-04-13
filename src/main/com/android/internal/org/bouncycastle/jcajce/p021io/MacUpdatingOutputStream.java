package com.android.internal.org.bouncycastle.jcajce.p021io;

import java.io.IOException;
import java.io.OutputStream;
import javax.crypto.Mac;
/* renamed from: com.android.internal.org.bouncycastle.jcajce.io.MacUpdatingOutputStream */
/* loaded from: classes4.dex */
class MacUpdatingOutputStream extends OutputStream {
    private Mac mac;

    /* JADX INFO: Access modifiers changed from: package-private */
    public MacUpdatingOutputStream(Mac mac) {
        this.mac = mac;
    }

    @Override // java.io.OutputStream
    public void write(byte[] bytes, int off, int len) throws IOException {
        this.mac.update(bytes, off, len);
    }

    @Override // java.io.OutputStream
    public void write(byte[] bytes) throws IOException {
        this.mac.update(bytes);
    }

    @Override // java.io.OutputStream
    public void write(int b) throws IOException {
        this.mac.update((byte) b);
    }
}
