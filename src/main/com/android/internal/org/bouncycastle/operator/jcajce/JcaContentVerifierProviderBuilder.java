package com.android.internal.org.bouncycastle.operator.jcajce;

import com.android.internal.org.bouncycastle.asn1.ASN1Primitive;
import com.android.internal.org.bouncycastle.asn1.ASN1Sequence;
import com.android.internal.org.bouncycastle.asn1.DERBitString;
import com.android.internal.org.bouncycastle.asn1.misc.MiscObjectIdentifiers;
import com.android.internal.org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import com.android.internal.org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import com.android.internal.org.bouncycastle.cert.X509CertificateHolder;
import com.android.internal.org.bouncycastle.cert.jcajce.JcaX509CertificateHolder;
import com.android.internal.org.bouncycastle.jcajce.CompositePublicKey;
import com.android.internal.org.bouncycastle.jcajce.p021io.OutputStreamFactory;
import com.android.internal.org.bouncycastle.jcajce.util.DefaultJcaJceHelper;
import com.android.internal.org.bouncycastle.jcajce.util.NamedJcaJceHelper;
import com.android.internal.org.bouncycastle.jcajce.util.ProviderJcaJceHelper;
import com.android.internal.org.bouncycastle.operator.ContentVerifier;
import com.android.internal.org.bouncycastle.operator.ContentVerifierProvider;
import com.android.internal.org.bouncycastle.operator.OperatorCreationException;
import com.android.internal.org.bouncycastle.operator.RawContentVerifier;
import com.android.internal.org.bouncycastle.operator.RuntimeOperatorException;
import com.android.internal.org.bouncycastle.util.p027io.TeeOutputStream;
import java.io.OutputStream;
import java.security.GeneralSecurityException;
import java.security.Provider;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.List;
/* loaded from: classes4.dex */
public class JcaContentVerifierProviderBuilder {
    private OperatorHelper helper = new OperatorHelper(new DefaultJcaJceHelper());

    public JcaContentVerifierProviderBuilder setProvider(Provider provider) {
        this.helper = new OperatorHelper(new ProviderJcaJceHelper(provider));
        return this;
    }

    public JcaContentVerifierProviderBuilder setProvider(String providerName) {
        this.helper = new OperatorHelper(new NamedJcaJceHelper(providerName));
        return this;
    }

    public ContentVerifierProvider build(X509CertificateHolder certHolder) throws OperatorCreationException, CertificateException {
        return build(this.helper.convertCertificate(certHolder));
    }

    public ContentVerifierProvider build(final X509Certificate certificate) throws OperatorCreationException {
        try {
            final X509CertificateHolder certHolder = new JcaX509CertificateHolder(certificate);
            return new ContentVerifierProvider() { // from class: com.android.internal.org.bouncycastle.operator.jcajce.JcaContentVerifierProviderBuilder.1
                @Override // com.android.internal.org.bouncycastle.operator.ContentVerifierProvider
                public boolean hasAssociatedCertificate() {
                    return true;
                }

                @Override // com.android.internal.org.bouncycastle.operator.ContentVerifierProvider
                public X509CertificateHolder getAssociatedCertificate() {
                    return certHolder;
                }

                @Override // com.android.internal.org.bouncycastle.operator.ContentVerifierProvider
                public ContentVerifier get(AlgorithmIdentifier algorithm) throws OperatorCreationException {
                    if (algorithm.getAlgorithm().equals((ASN1Primitive) MiscObjectIdentifiers.id_alg_composite)) {
                        return JcaContentVerifierProviderBuilder.this.createCompositeVerifier(algorithm, certificate.getPublicKey());
                    }
                    try {
                        Signature sig = JcaContentVerifierProviderBuilder.this.helper.createSignature(algorithm);
                        sig.initVerify(certificate.getPublicKey());
                        Signature rawSig = JcaContentVerifierProviderBuilder.this.createRawSig(algorithm, certificate.getPublicKey());
                        if (rawSig != null) {
                            return new RawSigVerifier(algorithm, sig, rawSig);
                        }
                        return new SigVerifier(algorithm, sig);
                    } catch (GeneralSecurityException e) {
                        throw new OperatorCreationException("exception on setup: " + e, e);
                    }
                }
            };
        } catch (CertificateEncodingException e) {
            throw new OperatorCreationException("cannot process certificate: " + e.getMessage(), e);
        }
    }

    public ContentVerifierProvider build(final PublicKey publicKey) throws OperatorCreationException {
        return new ContentVerifierProvider() { // from class: com.android.internal.org.bouncycastle.operator.jcajce.JcaContentVerifierProviderBuilder.2
            @Override // com.android.internal.org.bouncycastle.operator.ContentVerifierProvider
            public boolean hasAssociatedCertificate() {
                return false;
            }

            @Override // com.android.internal.org.bouncycastle.operator.ContentVerifierProvider
            public X509CertificateHolder getAssociatedCertificate() {
                return null;
            }

            @Override // com.android.internal.org.bouncycastle.operator.ContentVerifierProvider
            public ContentVerifier get(AlgorithmIdentifier algorithm) throws OperatorCreationException {
                if (algorithm.getAlgorithm().equals((ASN1Primitive) MiscObjectIdentifiers.id_alg_composite)) {
                    return JcaContentVerifierProviderBuilder.this.createCompositeVerifier(algorithm, publicKey);
                }
                PublicKey publicKey2 = publicKey;
                if (publicKey2 instanceof CompositePublicKey) {
                    List<PublicKey> keys = ((CompositePublicKey) publicKey2).getPublicKeys();
                    for (int i = 0; i != keys.size(); i++) {
                        try {
                            Signature sig = JcaContentVerifierProviderBuilder.this.createSignature(algorithm, keys.get(i));
                            Signature rawSig = JcaContentVerifierProviderBuilder.this.createRawSig(algorithm, keys.get(i));
                            if (rawSig != null) {
                                return new RawSigVerifier(algorithm, sig, rawSig);
                            }
                            return new SigVerifier(algorithm, sig);
                        } catch (OperatorCreationException e) {
                        }
                    }
                    throw new OperatorCreationException("no matching algorithm found for key");
                }
                Signature sig2 = JcaContentVerifierProviderBuilder.this.createSignature(algorithm, publicKey2);
                Signature rawSig2 = JcaContentVerifierProviderBuilder.this.createRawSig(algorithm, publicKey);
                if (rawSig2 != null) {
                    return new RawSigVerifier(algorithm, sig2, rawSig2);
                }
                return new SigVerifier(algorithm, sig2);
            }
        };
    }

    public ContentVerifierProvider build(SubjectPublicKeyInfo publicKey) throws OperatorCreationException {
        return build(this.helper.convertPublicKey(publicKey));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public ContentVerifier createCompositeVerifier(AlgorithmIdentifier compAlgId, PublicKey publicKey) throws OperatorCreationException {
        if (publicKey instanceof CompositePublicKey) {
            List<PublicKey> pubKeys = ((CompositePublicKey) publicKey).getPublicKeys();
            ASN1Sequence keySeq = ASN1Sequence.getInstance(compAlgId.getParameters());
            Signature[] sigs = new Signature[keySeq.size()];
            for (int i = 0; i != keySeq.size(); i++) {
                AlgorithmIdentifier sigAlg = AlgorithmIdentifier.getInstance(keySeq.getObjectAt(i));
                if (pubKeys.get(i) != null) {
                    sigs[i] = createSignature(sigAlg, pubKeys.get(i));
                } else {
                    sigs[i] = null;
                }
            }
            return new CompositeVerifier(sigs);
        }
        ASN1Sequence keySeq2 = ASN1Sequence.getInstance(compAlgId.getParameters());
        Signature[] sigs2 = new Signature[keySeq2.size()];
        for (int i2 = 0; i2 != keySeq2.size(); i2++) {
            AlgorithmIdentifier sigAlg2 = AlgorithmIdentifier.getInstance(keySeq2.getObjectAt(i2));
            try {
                sigs2[i2] = createSignature(sigAlg2, publicKey);
            } catch (Exception e) {
                sigs2[i2] = null;
            }
        }
        return new CompositeVerifier(sigs2);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public Signature createSignature(AlgorithmIdentifier algorithm, PublicKey publicKey) throws OperatorCreationException {
        try {
            Signature sig = this.helper.createSignature(algorithm);
            sig.initVerify(publicKey);
            return sig;
        } catch (GeneralSecurityException e) {
            throw new OperatorCreationException("exception on setup: " + e, e);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public Signature createRawSig(AlgorithmIdentifier algorithm, PublicKey publicKey) {
        try {
            Signature rawSig = this.helper.createRawSignature(algorithm);
            if (rawSig != null) {
                rawSig.initVerify(publicKey);
                return rawSig;
            }
            return rawSig;
        } catch (Exception e) {
            return null;
        }
    }

    /* loaded from: classes4.dex */
    private class SigVerifier implements ContentVerifier {
        private final AlgorithmIdentifier algorithm;
        private final Signature signature;
        protected final OutputStream stream;

        SigVerifier(AlgorithmIdentifier algorithm, Signature signature) {
            this.algorithm = algorithm;
            this.signature = signature;
            this.stream = OutputStreamFactory.createStream(signature);
        }

        @Override // com.android.internal.org.bouncycastle.operator.ContentVerifier
        public AlgorithmIdentifier getAlgorithmIdentifier() {
            return this.algorithm;
        }

        @Override // com.android.internal.org.bouncycastle.operator.ContentVerifier
        public OutputStream getOutputStream() {
            OutputStream outputStream = this.stream;
            if (outputStream == null) {
                throw new IllegalStateException("verifier not initialised");
            }
            return outputStream;
        }

        @Override // com.android.internal.org.bouncycastle.operator.ContentVerifier
        public boolean verify(byte[] expected) {
            try {
                return this.signature.verify(expected);
            } catch (SignatureException e) {
                throw new RuntimeOperatorException("exception obtaining signature: " + e.getMessage(), e);
            }
        }
    }

    /* loaded from: classes4.dex */
    private class RawSigVerifier extends SigVerifier implements RawContentVerifier {
        private Signature rawSignature;

        RawSigVerifier(AlgorithmIdentifier algorithm, Signature standardSig, Signature rawSignature) {
            super(algorithm, standardSig);
            this.rawSignature = rawSignature;
        }

        @Override // com.android.internal.org.bouncycastle.operator.jcajce.JcaContentVerifierProviderBuilder.SigVerifier, com.android.internal.org.bouncycastle.operator.ContentVerifier
        public boolean verify(byte[] expected) {
            try {
                return super.verify(expected);
            } finally {
                try {
                    this.rawSignature.verify(expected);
                } catch (Exception e) {
                }
            }
        }

        @Override // com.android.internal.org.bouncycastle.operator.RawContentVerifier
        public boolean verify(byte[] digest, byte[] expected) {
            try {
                try {
                    this.rawSignature.update(digest);
                    boolean verify = this.rawSignature.verify(expected);
                    try {
                        this.rawSignature.verify(expected);
                    } catch (Exception e) {
                    }
                    return verify;
                } catch (Throwable th) {
                    try {
                        this.rawSignature.verify(expected);
                    } catch (Exception e2) {
                    }
                    throw th;
                }
            } catch (SignatureException e3) {
                throw new RuntimeOperatorException("exception obtaining raw signature: " + e3.getMessage(), e3);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes4.dex */
    public class CompositeVerifier implements ContentVerifier {
        private Signature[] sigs;
        private OutputStream stream;

        public CompositeVerifier(Signature[] sigs) throws OperatorCreationException {
            this.sigs = sigs;
            int start = 0;
            while (start < sigs.length && sigs[start] == null) {
                start++;
            }
            if (start == sigs.length) {
                throw new OperatorCreationException("no matching signature found in composite");
            }
            this.stream = OutputStreamFactory.createStream(sigs[start]);
            for (int i = start + 1; i != sigs.length; i++) {
                if (sigs[i] != null) {
                    this.stream = new TeeOutputStream(this.stream, OutputStreamFactory.createStream(sigs[i]));
                }
            }
        }

        @Override // com.android.internal.org.bouncycastle.operator.ContentVerifier
        public AlgorithmIdentifier getAlgorithmIdentifier() {
            return new AlgorithmIdentifier(MiscObjectIdentifiers.id_alg_composite);
        }

        @Override // com.android.internal.org.bouncycastle.operator.ContentVerifier
        public OutputStream getOutputStream() {
            return this.stream;
        }

        @Override // com.android.internal.org.bouncycastle.operator.ContentVerifier
        public boolean verify(byte[] expected) {
            try {
                ASN1Sequence sigSeq = ASN1Sequence.getInstance(expected);
                boolean failed = false;
                for (int i = 0; i != sigSeq.size(); i++) {
                    Signature signature = this.sigs[i];
                    if (signature != null && !signature.verify(DERBitString.getInstance(sigSeq.getObjectAt(i)).getBytes())) {
                        failed = true;
                    }
                }
                return !failed;
            } catch (SignatureException e) {
                throw new RuntimeOperatorException("exception obtaining signature: " + e.getMessage(), e);
            }
        }
    }
}
