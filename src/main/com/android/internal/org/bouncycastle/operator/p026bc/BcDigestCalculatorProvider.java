package com.android.internal.org.bouncycastle.operator.p026bc;

import com.android.internal.org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import com.android.internal.org.bouncycastle.crypto.Digest;
import com.android.internal.org.bouncycastle.operator.DigestCalculator;
import com.android.internal.org.bouncycastle.operator.DigestCalculatorProvider;
import com.android.internal.org.bouncycastle.operator.OperatorCreationException;
import java.io.IOException;
import java.io.OutputStream;
/* renamed from: com.android.internal.org.bouncycastle.operator.bc.BcDigestCalculatorProvider */
/* loaded from: classes4.dex */
public class BcDigestCalculatorProvider implements DigestCalculatorProvider {
    private BcDigestProvider digestProvider = BcDefaultDigestProvider.INSTANCE;

    @Override // com.android.internal.org.bouncycastle.operator.DigestCalculatorProvider
    public DigestCalculator get(final AlgorithmIdentifier algorithm) throws OperatorCreationException {
        Digest dig = this.digestProvider.get(algorithm);
        final DigestOutputStream stream = new DigestOutputStream(dig);
        return new DigestCalculator() { // from class: com.android.internal.org.bouncycastle.operator.bc.BcDigestCalculatorProvider.1
            @Override // com.android.internal.org.bouncycastle.operator.DigestCalculator
            public AlgorithmIdentifier getAlgorithmIdentifier() {
                return algorithm;
            }

            @Override // com.android.internal.org.bouncycastle.operator.DigestCalculator
            public OutputStream getOutputStream() {
                return stream;
            }

            @Override // com.android.internal.org.bouncycastle.operator.DigestCalculator
            public byte[] getDigest() {
                return stream.getDigest();
            }
        };
    }

    /* renamed from: com.android.internal.org.bouncycastle.operator.bc.BcDigestCalculatorProvider$DigestOutputStream */
    /* loaded from: classes4.dex */
    private class DigestOutputStream extends OutputStream {
        private Digest dig;

        DigestOutputStream(Digest dig) {
            this.dig = dig;
        }

        @Override // java.io.OutputStream
        public void write(byte[] bytes, int off, int len) throws IOException {
            this.dig.update(bytes, off, len);
        }

        @Override // java.io.OutputStream
        public void write(byte[] bytes) throws IOException {
            this.dig.update(bytes, 0, bytes.length);
        }

        @Override // java.io.OutputStream
        public void write(int b) throws IOException {
            this.dig.update((byte) b);
        }

        byte[] getDigest() {
            byte[] d = new byte[this.dig.getDigestSize()];
            this.dig.doFinal(d, 0);
            return d;
        }
    }
}
