package com.android.internal.org.bouncycastle.cms;

import com.android.internal.org.bouncycastle.asn1.ASN1Encoding;
import com.android.internal.org.bouncycastle.asn1.ASN1ObjectIdentifier;
import com.android.internal.org.bouncycastle.asn1.ASN1Set;
import com.android.internal.org.bouncycastle.asn1.DEROctetString;
import com.android.internal.org.bouncycastle.asn1.DERSet;
import com.android.internal.org.bouncycastle.asn1.cms.AttributeTable;
import com.android.internal.org.bouncycastle.asn1.cms.SignerIdentifier;
import com.android.internal.org.bouncycastle.asn1.cms.SignerInfo;
import com.android.internal.org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import com.android.internal.org.bouncycastle.cert.X509CertificateHolder;
import com.android.internal.org.bouncycastle.operator.ContentSigner;
import com.android.internal.org.bouncycastle.operator.DefaultDigestAlgorithmIdentifierFinder;
import com.android.internal.org.bouncycastle.operator.DigestAlgorithmIdentifierFinder;
import com.android.internal.org.bouncycastle.operator.DigestCalculator;
import com.android.internal.org.bouncycastle.operator.DigestCalculatorProvider;
import com.android.internal.org.bouncycastle.operator.OperatorCreationException;
import com.android.internal.org.bouncycastle.util.Arrays;
import com.android.internal.org.bouncycastle.util.p027io.TeeOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
/* loaded from: classes4.dex */
public class SignerInfoGenerator {
    private byte[] calculatedDigest;
    private X509CertificateHolder certHolder;
    private final DigestAlgorithmIdentifierFinder digAlgFinder;
    private final DigestCalculator digester;
    private final CMSAttributeTableGenerator sAttrGen;
    private final CMSSignatureEncryptionAlgorithmFinder sigEncAlgFinder;
    private final ContentSigner signer;
    private final SignerIdentifier signerIdentifier;
    private final CMSAttributeTableGenerator unsAttrGen;

    /* JADX INFO: Access modifiers changed from: package-private */
    public SignerInfoGenerator(SignerIdentifier signerIdentifier, ContentSigner signer, DigestCalculatorProvider digesterProvider, CMSSignatureEncryptionAlgorithmFinder sigEncAlgFinder) throws OperatorCreationException {
        this(signerIdentifier, signer, digesterProvider, sigEncAlgFinder, false);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public SignerInfoGenerator(SignerIdentifier signerIdentifier, ContentSigner signer, DigestCalculatorProvider digesterProvider, CMSSignatureEncryptionAlgorithmFinder sigEncAlgFinder, boolean isDirectSignature) throws OperatorCreationException {
        DefaultDigestAlgorithmIdentifierFinder defaultDigestAlgorithmIdentifierFinder = new DefaultDigestAlgorithmIdentifierFinder();
        this.digAlgFinder = defaultDigestAlgorithmIdentifierFinder;
        this.calculatedDigest = null;
        this.signerIdentifier = signerIdentifier;
        this.signer = signer;
        if (digesterProvider != null) {
            this.digester = digesterProvider.get(defaultDigestAlgorithmIdentifierFinder.find(signer.getAlgorithmIdentifier()));
        } else {
            this.digester = null;
        }
        if (isDirectSignature) {
            this.sAttrGen = null;
            this.unsAttrGen = null;
        } else {
            this.sAttrGen = new DefaultSignedAttributeTableGenerator();
            this.unsAttrGen = null;
        }
        this.sigEncAlgFinder = sigEncAlgFinder;
    }

    public SignerInfoGenerator(SignerInfoGenerator original, CMSAttributeTableGenerator sAttrGen, CMSAttributeTableGenerator unsAttrGen) {
        this.digAlgFinder = new DefaultDigestAlgorithmIdentifierFinder();
        this.calculatedDigest = null;
        this.signerIdentifier = original.signerIdentifier;
        this.signer = original.signer;
        this.digester = original.digester;
        this.sigEncAlgFinder = original.sigEncAlgFinder;
        this.sAttrGen = sAttrGen;
        this.unsAttrGen = unsAttrGen;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public SignerInfoGenerator(SignerIdentifier signerIdentifier, ContentSigner signer, DigestCalculatorProvider digesterProvider, CMSSignatureEncryptionAlgorithmFinder sigEncAlgFinder, CMSAttributeTableGenerator sAttrGen, CMSAttributeTableGenerator unsAttrGen) throws OperatorCreationException {
        DefaultDigestAlgorithmIdentifierFinder defaultDigestAlgorithmIdentifierFinder = new DefaultDigestAlgorithmIdentifierFinder();
        this.digAlgFinder = defaultDigestAlgorithmIdentifierFinder;
        this.calculatedDigest = null;
        this.signerIdentifier = signerIdentifier;
        this.signer = signer;
        if (digesterProvider != null) {
            this.digester = digesterProvider.get(defaultDigestAlgorithmIdentifierFinder.find(signer.getAlgorithmIdentifier()));
        } else {
            this.digester = null;
        }
        this.sAttrGen = sAttrGen;
        this.unsAttrGen = unsAttrGen;
        this.sigEncAlgFinder = sigEncAlgFinder;
    }

    public SignerIdentifier getSID() {
        return this.signerIdentifier;
    }

    public int getGeneratedVersion() {
        return this.signerIdentifier.isTagged() ? 3 : 1;
    }

    public boolean hasAssociatedCertificate() {
        return this.certHolder != null;
    }

    public X509CertificateHolder getAssociatedCertificate() {
        return this.certHolder;
    }

    public AlgorithmIdentifier getDigestAlgorithm() {
        DigestCalculator digestCalculator = this.digester;
        if (digestCalculator != null) {
            return digestCalculator.getAlgorithmIdentifier();
        }
        return this.digAlgFinder.find(this.signer.getAlgorithmIdentifier());
    }

    public OutputStream getCalculatingOutputStream() {
        DigestCalculator digestCalculator = this.digester;
        if (digestCalculator != null) {
            if (this.sAttrGen == null) {
                return new TeeOutputStream(this.digester.getOutputStream(), this.signer.getOutputStream());
            }
            return digestCalculator.getOutputStream();
        }
        return this.signer.getOutputStream();
    }

    public SignerInfo generate(ASN1ObjectIdentifier contentType) throws CMSException {
        AlgorithmIdentifier digestAlg;
        ASN1Set unsignedAttr;
        ASN1Set signedAttr = null;
        try {
            AlgorithmIdentifier digestEncryptionAlgorithm = this.sigEncAlgFinder.findEncryptionAlgorithm(this.signer.getAlgorithmIdentifier());
            if (this.sAttrGen != null) {
                AlgorithmIdentifier digestAlg2 = this.digester.getAlgorithmIdentifier();
                this.calculatedDigest = this.digester.getDigest();
                AttributeTable signed = this.sAttrGen.getAttributes(Collections.unmodifiableMap(getBaseParameters(contentType, this.digester.getAlgorithmIdentifier(), digestEncryptionAlgorithm, this.calculatedDigest)));
                signedAttr = getAttributeSet(signed);
                OutputStream sOut = this.signer.getOutputStream();
                sOut.write(signedAttr.getEncoded(ASN1Encoding.DER));
                sOut.close();
                digestAlg = digestAlg2;
            } else {
                DigestCalculator digestCalculator = this.digester;
                if (digestCalculator != null) {
                    AlgorithmIdentifier digestAlg3 = digestCalculator.getAlgorithmIdentifier();
                    this.calculatedDigest = this.digester.getDigest();
                    digestAlg = digestAlg3;
                } else {
                    AlgorithmIdentifier digestAlg4 = this.digAlgFinder.find(this.signer.getAlgorithmIdentifier());
                    this.calculatedDigest = null;
                    digestAlg = digestAlg4;
                }
            }
            byte[] sigBytes = this.signer.getSignature();
            if (this.unsAttrGen == null) {
                unsignedAttr = null;
            } else {
                Map parameters = getBaseParameters(contentType, digestAlg, digestEncryptionAlgorithm, this.calculatedDigest);
                parameters.put(CMSAttributeTableGenerator.SIGNATURE, Arrays.clone(sigBytes));
                AttributeTable unsigned = this.unsAttrGen.getAttributes(Collections.unmodifiableMap(parameters));
                ASN1Set unsignedAttr2 = getAttributeSet(unsigned);
                unsignedAttr = unsignedAttr2;
            }
            return new SignerInfo(this.signerIdentifier, digestAlg, signedAttr, digestEncryptionAlgorithm, new DEROctetString(sigBytes), unsignedAttr);
        } catch (IOException e) {
            throw new CMSException("encoding error.", e);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setAssociatedCertificate(X509CertificateHolder certHolder) {
        this.certHolder = certHolder;
    }

    private ASN1Set getAttributeSet(AttributeTable attr) {
        if (attr != null) {
            return new DERSet(attr.toASN1EncodableVector());
        }
        return null;
    }

    private Map getBaseParameters(ASN1ObjectIdentifier contentType, AlgorithmIdentifier digAlgId, AlgorithmIdentifier sigAlgId, byte[] hash) {
        Map param = new HashMap();
        if (contentType != null) {
            param.put(CMSAttributeTableGenerator.CONTENT_TYPE, contentType);
        }
        param.put(CMSAttributeTableGenerator.DIGEST_ALGORITHM_IDENTIFIER, digAlgId);
        param.put(CMSAttributeTableGenerator.SIGNATURE_ALGORITHM_IDENTIFIER, sigAlgId);
        param.put(CMSAttributeTableGenerator.DIGEST, Arrays.clone(hash));
        return param;
    }

    public byte[] getCalculatedDigest() {
        byte[] bArr = this.calculatedDigest;
        if (bArr != null) {
            return Arrays.clone(bArr);
        }
        return null;
    }

    public CMSAttributeTableGenerator getSignedAttributeTableGenerator() {
        return this.sAttrGen;
    }

    public CMSAttributeTableGenerator getUnsignedAttributeTableGenerator() {
        return this.unsAttrGen;
    }
}
