package com.android.internal.org.bouncycastle.jcajce.p021io;

import java.io.IOException;
import java.io.OutputStream;
import java.security.MessageDigest;
/* renamed from: com.android.internal.org.bouncycastle.jcajce.io.DigestUpdatingOutputStream */
/* loaded from: classes4.dex */
class DigestUpdatingOutputStream extends OutputStream {
    private MessageDigest digest;

    /* JADX INFO: Access modifiers changed from: package-private */
    public DigestUpdatingOutputStream(MessageDigest digest) {
        this.digest = digest;
    }

    @Override // java.io.OutputStream
    public void write(byte[] bytes, int off, int len) throws IOException {
        this.digest.update(bytes, off, len);
    }

    @Override // java.io.OutputStream
    public void write(byte[] bytes) throws IOException {
        this.digest.update(bytes);
    }

    @Override // java.io.OutputStream
    public void write(int b) throws IOException {
        this.digest.update((byte) b);
    }
}
