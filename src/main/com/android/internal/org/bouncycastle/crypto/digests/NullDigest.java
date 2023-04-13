package com.android.internal.org.bouncycastle.crypto.digests;

import com.android.internal.org.bouncycastle.crypto.Digest;
import com.android.internal.org.bouncycastle.util.Arrays;
import java.io.ByteArrayOutputStream;
/* loaded from: classes4.dex */
public class NullDigest implements Digest {
    private OpenByteArrayOutputStream bOut = new OpenByteArrayOutputStream();

    @Override // com.android.internal.org.bouncycastle.crypto.Digest
    public String getAlgorithmName() {
        return "NULL";
    }

    @Override // com.android.internal.org.bouncycastle.crypto.Digest
    public int getDigestSize() {
        return this.bOut.size();
    }

    @Override // com.android.internal.org.bouncycastle.crypto.Digest
    public void update(byte in) {
        this.bOut.write(in);
    }

    @Override // com.android.internal.org.bouncycastle.crypto.Digest
    public void update(byte[] in, int inOff, int len) {
        this.bOut.write(in, inOff, len);
    }

    @Override // com.android.internal.org.bouncycastle.crypto.Digest
    public int doFinal(byte[] out, int outOff) {
        int size = this.bOut.size();
        this.bOut.copy(out, outOff);
        reset();
        return size;
    }

    @Override // com.android.internal.org.bouncycastle.crypto.Digest
    public void reset() {
        this.bOut.reset();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes4.dex */
    public static class OpenByteArrayOutputStream extends ByteArrayOutputStream {
        private OpenByteArrayOutputStream() {
        }

        @Override // java.io.ByteArrayOutputStream
        public void reset() {
            super.reset();
            Arrays.clear(this.buf);
        }

        void copy(byte[] out, int outOff) {
            System.arraycopy(this.buf, 0, out, outOff, size());
        }
    }
}
