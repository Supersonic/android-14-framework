package com.android.internal.org.bouncycastle.cms;

import com.android.internal.org.bouncycastle.asn1.ASN1OctetString;
import com.android.internal.org.bouncycastle.asn1.DEROctetString;
import com.android.internal.org.bouncycastle.asn1.cms.IssuerAndSerialNumber;
import com.android.internal.org.bouncycastle.asn1.cms.SignerIdentifier;
import com.android.internal.org.bouncycastle.cert.X509CertificateHolder;
import com.android.internal.org.bouncycastle.operator.ContentSigner;
import com.android.internal.org.bouncycastle.operator.DigestCalculatorProvider;
import com.android.internal.org.bouncycastle.operator.OperatorCreationException;
/* loaded from: classes4.dex */
public class SignerInfoGeneratorBuilder {
    private DigestCalculatorProvider digestProvider;
    private boolean directSignature;
    private CMSSignatureEncryptionAlgorithmFinder sigEncAlgFinder;
    private CMSAttributeTableGenerator signedGen;
    private CMSAttributeTableGenerator unsignedGen;

    public SignerInfoGeneratorBuilder(DigestCalculatorProvider digestProvider) {
        this(digestProvider, new DefaultCMSSignatureEncryptionAlgorithmFinder());
    }

    public SignerInfoGeneratorBuilder(DigestCalculatorProvider digestProvider, CMSSignatureEncryptionAlgorithmFinder sigEncAlgFinder) {
        this.digestProvider = digestProvider;
        this.sigEncAlgFinder = sigEncAlgFinder;
    }

    public SignerInfoGeneratorBuilder setDirectSignature(boolean hasNoSignedAttributes) {
        this.directSignature = hasNoSignedAttributes;
        return this;
    }

    public SignerInfoGeneratorBuilder setSignedAttributeGenerator(CMSAttributeTableGenerator signedGen) {
        this.signedGen = signedGen;
        return this;
    }

    public SignerInfoGeneratorBuilder setUnsignedAttributeGenerator(CMSAttributeTableGenerator unsignedGen) {
        this.unsignedGen = unsignedGen;
        return this;
    }

    public SignerInfoGenerator build(ContentSigner contentSigner, X509CertificateHolder certHolder) throws OperatorCreationException {
        SignerIdentifier sigId = new SignerIdentifier(new IssuerAndSerialNumber(certHolder.toASN1Structure()));
        SignerInfoGenerator sigInfoGen = createGenerator(contentSigner, sigId);
        sigInfoGen.setAssociatedCertificate(certHolder);
        return sigInfoGen;
    }

    public SignerInfoGenerator build(ContentSigner contentSigner, byte[] subjectKeyIdentifier) throws OperatorCreationException {
        SignerIdentifier sigId = new SignerIdentifier((ASN1OctetString) new DEROctetString(subjectKeyIdentifier));
        return createGenerator(contentSigner, sigId);
    }

    private SignerInfoGenerator createGenerator(ContentSigner contentSigner, SignerIdentifier sigId) throws OperatorCreationException {
        if (this.directSignature) {
            return new SignerInfoGenerator(sigId, contentSigner, this.digestProvider, this.sigEncAlgFinder, true);
        }
        CMSAttributeTableGenerator cMSAttributeTableGenerator = this.signedGen;
        if (cMSAttributeTableGenerator != null || this.unsignedGen != null) {
            if (cMSAttributeTableGenerator == null) {
                this.signedGen = new DefaultSignedAttributeTableGenerator();
            }
            return new SignerInfoGenerator(sigId, contentSigner, this.digestProvider, this.sigEncAlgFinder, this.signedGen, this.unsignedGen);
        }
        return new SignerInfoGenerator(sigId, contentSigner, this.digestProvider, this.sigEncAlgFinder);
    }
}
