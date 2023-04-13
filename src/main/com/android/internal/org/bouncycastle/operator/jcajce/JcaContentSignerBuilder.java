package com.android.internal.org.bouncycastle.operator.jcajce;

import com.android.internal.org.bouncycastle.asn1.ASN1EncodableVector;
import com.android.internal.org.bouncycastle.asn1.ASN1Encoding;
import com.android.internal.org.bouncycastle.asn1.ASN1Integer;
import com.android.internal.org.bouncycastle.asn1.ASN1Sequence;
import com.android.internal.org.bouncycastle.asn1.DERBitString;
import com.android.internal.org.bouncycastle.asn1.DERSequence;
import com.android.internal.org.bouncycastle.asn1.misc.MiscObjectIdentifiers;
import com.android.internal.org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import com.android.internal.org.bouncycastle.asn1.pkcs.RSASSAPSSparams;
import com.android.internal.org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import com.android.internal.org.bouncycastle.jcajce.CompositePrivateKey;
import com.android.internal.org.bouncycastle.jcajce.p021io.OutputStreamFactory;
import com.android.internal.org.bouncycastle.jcajce.spec.CompositeAlgorithmSpec;
import com.android.internal.org.bouncycastle.jcajce.util.DefaultJcaJceHelper;
import com.android.internal.org.bouncycastle.jcajce.util.NamedJcaJceHelper;
import com.android.internal.org.bouncycastle.jcajce.util.ProviderJcaJceHelper;
import com.android.internal.org.bouncycastle.operator.ContentSigner;
import com.android.internal.org.bouncycastle.operator.DefaultDigestAlgorithmIdentifierFinder;
import com.android.internal.org.bouncycastle.operator.DefaultSignatureAlgorithmIdentifierFinder;
import com.android.internal.org.bouncycastle.operator.DigestAlgorithmIdentifierFinder;
import com.android.internal.org.bouncycastle.operator.OperatorCreationException;
import com.android.internal.org.bouncycastle.operator.RuntimeOperatorException;
import com.android.internal.org.bouncycastle.operator.SignatureAlgorithmIdentifierFinder;
import com.android.internal.org.bouncycastle.util.p027io.TeeOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.MGF1ParameterSpec;
import java.security.spec.PSSParameterSpec;
import java.util.List;
/* loaded from: classes4.dex */
public class JcaContentSignerBuilder {
    private OperatorHelper helper;
    private SecureRandom random;
    private AlgorithmIdentifier sigAlgId;
    private AlgorithmParameterSpec sigAlgSpec;
    private String signatureAlgorithm;

    public JcaContentSignerBuilder(String signatureAlgorithm) {
        this.helper = new OperatorHelper(new DefaultJcaJceHelper());
        this.signatureAlgorithm = signatureAlgorithm;
        this.sigAlgId = new DefaultSignatureAlgorithmIdentifierFinder().find(signatureAlgorithm);
        this.sigAlgSpec = null;
    }

    public JcaContentSignerBuilder(String signatureAlgorithm, AlgorithmParameterSpec sigParamSpec) {
        this.helper = new OperatorHelper(new DefaultJcaJceHelper());
        this.signatureAlgorithm = signatureAlgorithm;
        if (sigParamSpec instanceof PSSParameterSpec) {
            PSSParameterSpec pssSpec = (PSSParameterSpec) sigParamSpec;
            this.sigAlgSpec = pssSpec;
            this.sigAlgId = new AlgorithmIdentifier(PKCSObjectIdentifiers.id_RSASSA_PSS, createPSSParams(pssSpec));
        } else if (sigParamSpec instanceof CompositeAlgorithmSpec) {
            CompositeAlgorithmSpec compSpec = (CompositeAlgorithmSpec) sigParamSpec;
            this.sigAlgSpec = compSpec;
            this.sigAlgId = new AlgorithmIdentifier(MiscObjectIdentifiers.id_alg_composite, createCompParams(compSpec));
        } else {
            throw new IllegalArgumentException("unknown sigParamSpec: " + (sigParamSpec == null ? "null" : sigParamSpec.getClass().getName()));
        }
    }

    public JcaContentSignerBuilder setProvider(Provider provider) {
        this.helper = new OperatorHelper(new ProviderJcaJceHelper(provider));
        return this;
    }

    public JcaContentSignerBuilder setProvider(String providerName) {
        this.helper = new OperatorHelper(new NamedJcaJceHelper(providerName));
        return this;
    }

    public JcaContentSignerBuilder setSecureRandom(SecureRandom random) {
        this.random = random;
        return this;
    }

    public ContentSigner build(PrivateKey privateKey) throws OperatorCreationException {
        if (privateKey instanceof CompositePrivateKey) {
            return buildComposite((CompositePrivateKey) privateKey);
        }
        try {
            Signature sig = this.helper.createSignature(this.sigAlgId);
            AlgorithmIdentifier signatureAlgId = this.sigAlgId;
            SecureRandom secureRandom = this.random;
            if (secureRandom != null) {
                sig.initSign(privateKey, secureRandom);
            } else {
                sig.initSign(privateKey);
            }
            return new ContentSigner(sig, signatureAlgId) { // from class: com.android.internal.org.bouncycastle.operator.jcajce.JcaContentSignerBuilder.1
                private OutputStream stream;
                final /* synthetic */ Signature val$sig;
                final /* synthetic */ AlgorithmIdentifier val$signatureAlgId;

                {
                    this.val$sig = sig;
                    this.val$signatureAlgId = signatureAlgId;
                    this.stream = OutputStreamFactory.createStream(sig);
                }

                @Override // com.android.internal.org.bouncycastle.operator.ContentSigner
                public AlgorithmIdentifier getAlgorithmIdentifier() {
                    return this.val$signatureAlgId;
                }

                @Override // com.android.internal.org.bouncycastle.operator.ContentSigner
                public OutputStream getOutputStream() {
                    return this.stream;
                }

                @Override // com.android.internal.org.bouncycastle.operator.ContentSigner
                public byte[] getSignature() {
                    try {
                        return this.val$sig.sign();
                    } catch (SignatureException e) {
                        throw new RuntimeOperatorException("exception obtaining signature: " + e.getMessage(), e);
                    }
                }
            };
        } catch (GeneralSecurityException e) {
            throw new OperatorCreationException("cannot create signer: " + e.getMessage(), e);
        }
    }

    private ContentSigner buildComposite(CompositePrivateKey privateKey) throws OperatorCreationException {
        try {
            List<PrivateKey> privateKeys = privateKey.getPrivateKeys();
            ASN1Sequence sigAlgIds = ASN1Sequence.getInstance(this.sigAlgId.getParameters());
            Signature[] sigs = new Signature[sigAlgIds.size()];
            for (int i = 0; i != sigAlgIds.size(); i++) {
                sigs[i] = this.helper.createSignature(AlgorithmIdentifier.getInstance(sigAlgIds.getObjectAt(i)));
                if (this.random != null) {
                    sigs[i].initSign(privateKeys.get(i), this.random);
                } else {
                    sigs[i].initSign(privateKeys.get(i));
                }
            }
            OutputStream sStream = OutputStreamFactory.createStream(sigs[0]);
            for (int i2 = 1; i2 != sigs.length; i2++) {
                sStream = new TeeOutputStream(sStream, OutputStreamFactory.createStream(sigs[i2]));
            }
            OutputStream sigStream = sStream;
            return new ContentSigner(sigStream, sigs) { // from class: com.android.internal.org.bouncycastle.operator.jcajce.JcaContentSignerBuilder.2
                OutputStream stream;
                final /* synthetic */ OutputStream val$sigStream;
                final /* synthetic */ Signature[] val$sigs;

                {
                    this.val$sigStream = sigStream;
                    this.val$sigs = sigs;
                    this.stream = sigStream;
                }

                @Override // com.android.internal.org.bouncycastle.operator.ContentSigner
                public AlgorithmIdentifier getAlgorithmIdentifier() {
                    return JcaContentSignerBuilder.this.sigAlgId;
                }

                @Override // com.android.internal.org.bouncycastle.operator.ContentSigner
                public OutputStream getOutputStream() {
                    return this.stream;
                }

                @Override // com.android.internal.org.bouncycastle.operator.ContentSigner
                public byte[] getSignature() {
                    try {
                        ASN1EncodableVector sigV = new ASN1EncodableVector();
                        for (int i3 = 0; i3 != this.val$sigs.length; i3++) {
                            sigV.add(new DERBitString(this.val$sigs[i3].sign()));
                        }
                        return new DERSequence(sigV).getEncoded(ASN1Encoding.DER);
                    } catch (IOException e) {
                        throw new RuntimeOperatorException("exception encoding signature: " + e.getMessage(), e);
                    } catch (SignatureException e2) {
                        throw new RuntimeOperatorException("exception obtaining signature: " + e2.getMessage(), e2);
                    }
                }
            };
        } catch (GeneralSecurityException e) {
            throw new OperatorCreationException("cannot create signer: " + e.getMessage(), e);
        }
    }

    private static RSASSAPSSparams createPSSParams(PSSParameterSpec pssSpec) {
        DigestAlgorithmIdentifierFinder digFinder = new DefaultDigestAlgorithmIdentifierFinder();
        AlgorithmIdentifier digId = digFinder.find(pssSpec.getDigestAlgorithm());
        AlgorithmIdentifier mgfDig = digFinder.find(((MGF1ParameterSpec) pssSpec.getMGFParameters()).getDigestAlgorithm());
        return new RSASSAPSSparams(digId, new AlgorithmIdentifier(PKCSObjectIdentifiers.id_mgf1, mgfDig), new ASN1Integer(pssSpec.getSaltLength()), new ASN1Integer(pssSpec.getTrailerField()));
    }

    private static ASN1Sequence createCompParams(CompositeAlgorithmSpec compSpec) {
        SignatureAlgorithmIdentifierFinder algFinder = new DefaultSignatureAlgorithmIdentifierFinder();
        ASN1EncodableVector v = new ASN1EncodableVector();
        List<String> algorithmNames = compSpec.getAlgorithmNames();
        List<AlgorithmParameterSpec> algorithmSpecs = compSpec.getParameterSpecs();
        for (int i = 0; i != algorithmNames.size(); i++) {
            AlgorithmParameterSpec sigSpec = algorithmSpecs.get(i);
            if (sigSpec == null) {
                v.add(algFinder.find(algorithmNames.get(i)));
            } else if (sigSpec instanceof PSSParameterSpec) {
                v.add(createPSSParams((PSSParameterSpec) sigSpec));
            } else {
                throw new IllegalArgumentException("unrecognized parameterSpec");
            }
        }
        return new DERSequence(v);
    }
}
