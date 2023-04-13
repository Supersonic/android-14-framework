package android.content.p001pm;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.crypto.Mac;
/* renamed from: android.content.pm.MacAuthenticatedInputStream */
/* loaded from: classes.dex */
public class MacAuthenticatedInputStream extends FilterInputStream {
    private final Mac mMac;

    public MacAuthenticatedInputStream(InputStream in, Mac mac) {
        super(in);
        this.mMac = mac;
    }

    public boolean isTagEqual(byte[] tag) {
        byte[] actualTag = this.mMac.doFinal();
        if (tag == null || actualTag == null || tag.length != actualTag.length) {
            return false;
        }
        int value = 0;
        for (int i = 0; i < tag.length; i++) {
            value |= tag[i] ^ actualTag[i];
        }
        return value == 0;
    }

    @Override // java.io.FilterInputStream, java.io.InputStream
    public int read() throws IOException {
        int b = super.read();
        if (b >= 0) {
            this.mMac.update((byte) b);
        }
        return b;
    }

    @Override // java.io.FilterInputStream, java.io.InputStream
    public int read(byte[] buffer, int offset, int count) throws IOException {
        int numRead = super.read(buffer, offset, count);
        if (numRead > 0) {
            this.mMac.update(buffer, offset, numRead);
        }
        return numRead;
    }
}
