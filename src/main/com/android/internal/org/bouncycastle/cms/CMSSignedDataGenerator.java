package com.android.internal.org.bouncycastle.cms;

import com.android.internal.org.bouncycastle.asn1.ASN1EncodableVector;
import com.android.internal.org.bouncycastle.asn1.ASN1ObjectIdentifier;
import com.android.internal.org.bouncycastle.asn1.ASN1OctetString;
import com.android.internal.org.bouncycastle.asn1.ASN1Set;
import com.android.internal.org.bouncycastle.asn1.BEROctetString;
import com.android.internal.org.bouncycastle.asn1.DERSet;
import com.android.internal.org.bouncycastle.asn1.cms.CMSObjectIdentifiers;
import com.android.internal.org.bouncycastle.asn1.cms.ContentInfo;
import com.android.internal.org.bouncycastle.asn1.cms.SignedData;
import com.android.internal.org.bouncycastle.asn1.cms.SignerInfo;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes4.dex */
public class CMSSignedDataGenerator extends CMSSignedGenerator {
    private List signerInfs = new ArrayList();

    public CMSSignedData generate(CMSTypedData content) throws CMSException {
        return generate(content, false);
    }

    public CMSSignedData generate(CMSTypedData content, boolean encapsulate) throws CMSException {
        ASN1Set certrevlist;
        if (!this.signerInfs.isEmpty()) {
            throw new IllegalStateException("this method can only be used with SignerInfoGenerator");
        }
        ASN1EncodableVector digestAlgs = new ASN1EncodableVector();
        ASN1EncodableVector signerInfos = new ASN1EncodableVector();
        this.digests.clear();
        for (SignerInformation signer : this._signers) {
            digestAlgs.add(CMSSignedHelper.INSTANCE.fixAlgID(signer.getDigestAlgorithmID()));
            signerInfos.add(signer.toASN1Structure());
        }
        ASN1ObjectIdentifier contentTypeOID = content.getContentType();
        ASN1OctetString octs = null;
        if (content.getContent() != null) {
            ByteArrayOutputStream bOut = null;
            if (encapsulate) {
                bOut = new ByteArrayOutputStream();
            }
            OutputStream cOut = CMSUtils.getSafeOutputStream(CMSUtils.attachSignersToOutputStream(this.signerGens, bOut));
            try {
                content.write(cOut);
                cOut.close();
                if (encapsulate) {
                    octs = new BEROctetString(bOut.toByteArray());
                }
            } catch (IOException e) {
                throw new CMSException("data processing exception: " + e.getMessage(), e);
            }
        }
        for (SignerInfoGenerator sGen : this.signerGens) {
            SignerInfo inf = sGen.generate(contentTypeOID);
            digestAlgs.add(inf.getDigestAlgorithm());
            signerInfos.add(inf);
            byte[] calcDigest = sGen.getCalculatedDigest();
            if (calcDigest != null) {
                this.digests.put(inf.getDigestAlgorithm().getAlgorithm().getId(), calcDigest);
            }
        }
        ASN1Set certificates = null;
        if (this.certs.size() != 0) {
            certificates = CMSUtils.createBerSetFromList(this.certs);
        }
        if (this.crls.size() == 0) {
            certrevlist = null;
        } else {
            ASN1Set certrevlist2 = CMSUtils.createBerSetFromList(this.crls);
            certrevlist = certrevlist2;
        }
        ContentInfo encInfo = new ContentInfo(contentTypeOID, octs);
        SignedData sd = new SignedData(new DERSet(digestAlgs), encInfo, certificates, certrevlist, new DERSet(signerInfos));
        ContentInfo contentInfo = new ContentInfo(CMSObjectIdentifiers.signedData, sd);
        return new CMSSignedData(content, contentInfo);
    }

    public SignerInformationStore generateCounterSigners(SignerInformation signer) throws CMSException {
        return generate(new CMSProcessableByteArray(null, signer.getSignature()), false).getSignerInfos();
    }
}
