package com.android.internal.org.bouncycastle.cms;

import java.io.IOException;
import java.io.OutputStream;
/* loaded from: classes4.dex */
class NullOutputStream extends OutputStream {
    @Override // java.io.OutputStream
    public void write(byte[] buf) throws IOException {
    }

    @Override // java.io.OutputStream
    public void write(byte[] buf, int off, int len) throws IOException {
    }

    @Override // java.io.OutputStream
    public void write(int b) throws IOException {
    }
}
