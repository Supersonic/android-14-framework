package com.android.internal.org.bouncycastle.operator.jcajce;

import com.android.internal.org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import com.android.internal.org.bouncycastle.jcajce.util.DefaultJcaJceHelper;
import com.android.internal.org.bouncycastle.jcajce.util.NamedJcaJceHelper;
import com.android.internal.org.bouncycastle.jcajce.util.ProviderJcaJceHelper;
import com.android.internal.org.bouncycastle.operator.DigestCalculator;
import com.android.internal.org.bouncycastle.operator.DigestCalculatorProvider;
import com.android.internal.org.bouncycastle.operator.OperatorCreationException;
import java.io.IOException;
import java.io.OutputStream;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.Provider;
/* loaded from: classes4.dex */
public class JcaDigestCalculatorProviderBuilder {
    private OperatorHelper helper = new OperatorHelper(new DefaultJcaJceHelper());

    public JcaDigestCalculatorProviderBuilder setProvider(Provider provider) {
        this.helper = new OperatorHelper(new ProviderJcaJceHelper(provider));
        return this;
    }

    public JcaDigestCalculatorProviderBuilder setProvider(String providerName) {
        this.helper = new OperatorHelper(new NamedJcaJceHelper(providerName));
        return this;
    }

    public DigestCalculatorProvider build() throws OperatorCreationException {
        return new DigestCalculatorProvider() { // from class: com.android.internal.org.bouncycastle.operator.jcajce.JcaDigestCalculatorProviderBuilder.1
            @Override // com.android.internal.org.bouncycastle.operator.DigestCalculatorProvider
            public DigestCalculator get(final AlgorithmIdentifier algorithm) throws OperatorCreationException {
                try {
                    MessageDigest dig = JcaDigestCalculatorProviderBuilder.this.helper.createDigest(algorithm);
                    final DigestOutputStream stream = new DigestOutputStream(dig);
                    return new DigestCalculator() { // from class: com.android.internal.org.bouncycastle.operator.jcajce.JcaDigestCalculatorProviderBuilder.1.1
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
                } catch (GeneralSecurityException e) {
                    throw new OperatorCreationException("exception on setup: " + e, e);
                }
            }
        };
    }

    /* loaded from: classes4.dex */
    private class DigestOutputStream extends OutputStream {
        private MessageDigest dig;

        DigestOutputStream(MessageDigest dig) {
            this.dig = dig;
        }

        @Override // java.io.OutputStream
        public void write(byte[] bytes, int off, int len) throws IOException {
            this.dig.update(bytes, off, len);
        }

        @Override // java.io.OutputStream
        public void write(byte[] bytes) throws IOException {
            this.dig.update(bytes);
        }

        @Override // java.io.OutputStream
        public void write(int b) throws IOException {
            this.dig.update((byte) b);
        }

        byte[] getDigest() {
            return this.dig.digest();
        }
    }
}
