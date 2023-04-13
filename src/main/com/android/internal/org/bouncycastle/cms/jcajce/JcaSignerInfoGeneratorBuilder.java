package com.android.internal.org.bouncycastle.cms.jcajce;

import com.android.internal.org.bouncycastle.cert.X509CertificateHolder;
import com.android.internal.org.bouncycastle.cert.jcajce.JcaX509CertificateHolder;
import com.android.internal.org.bouncycastle.cms.CMSAttributeTableGenerator;
import com.android.internal.org.bouncycastle.cms.CMSSignatureEncryptionAlgorithmFinder;
import com.android.internal.org.bouncycastle.cms.DefaultCMSSignatureEncryptionAlgorithmFinder;
import com.android.internal.org.bouncycastle.cms.SignerInfoGenerator;
import com.android.internal.org.bouncycastle.cms.SignerInfoGeneratorBuilder;
import com.android.internal.org.bouncycastle.operator.ContentSigner;
import com.android.internal.org.bouncycastle.operator.DigestCalculatorProvider;
import com.android.internal.org.bouncycastle.operator.OperatorCreationException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
/* loaded from: classes4.dex */
public class JcaSignerInfoGeneratorBuilder {
    private SignerInfoGeneratorBuilder builder;

    public JcaSignerInfoGeneratorBuilder(DigestCalculatorProvider digestProvider) {
        this(digestProvider, new DefaultCMSSignatureEncryptionAlgorithmFinder());
    }

    public JcaSignerInfoGeneratorBuilder(DigestCalculatorProvider digestProvider, CMSSignatureEncryptionAlgorithmFinder sigEncAlgFinder) {
        this.builder = new SignerInfoGeneratorBuilder(digestProvider, sigEncAlgFinder);
    }

    public JcaSignerInfoGeneratorBuilder setDirectSignature(boolean hasNoSignedAttributes) {
        this.builder.setDirectSignature(hasNoSignedAttributes);
        return this;
    }

    public JcaSignerInfoGeneratorBuilder setSignedAttributeGenerator(CMSAttributeTableGenerator signedGen) {
        this.builder.setSignedAttributeGenerator(signedGen);
        return this;
    }

    public JcaSignerInfoGeneratorBuilder setUnsignedAttributeGenerator(CMSAttributeTableGenerator unsignedGen) {
        this.builder.setUnsignedAttributeGenerator(unsignedGen);
        return this;
    }

    public SignerInfoGenerator build(ContentSigner contentSigner, X509CertificateHolder certHolder) throws OperatorCreationException {
        return this.builder.build(contentSigner, certHolder);
    }

    public SignerInfoGenerator build(ContentSigner contentSigner, byte[] keyIdentifier) throws OperatorCreationException {
        return this.builder.build(contentSigner, keyIdentifier);
    }

    public SignerInfoGenerator build(ContentSigner contentSigner, X509Certificate certificate) throws OperatorCreationException, CertificateEncodingException {
        return build(contentSigner, new JcaX509CertificateHolder(certificate));
    }
}
