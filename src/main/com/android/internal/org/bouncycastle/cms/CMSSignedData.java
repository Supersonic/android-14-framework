package com.android.internal.org.bouncycastle.cms;

import com.android.internal.org.bouncycastle.asn1.ASN1Encodable;
import com.android.internal.org.bouncycastle.asn1.ASN1EncodableVector;
import com.android.internal.org.bouncycastle.asn1.ASN1InputStream;
import com.android.internal.org.bouncycastle.asn1.ASN1ObjectIdentifier;
import com.android.internal.org.bouncycastle.asn1.ASN1OctetString;
import com.android.internal.org.bouncycastle.asn1.ASN1Sequence;
import com.android.internal.org.bouncycastle.asn1.ASN1Set;
import com.android.internal.org.bouncycastle.asn1.BERSequence;
import com.android.internal.org.bouncycastle.asn1.DERSet;
import com.android.internal.org.bouncycastle.asn1.DLSet;
import com.android.internal.org.bouncycastle.asn1.cms.ContentInfo;
import com.android.internal.org.bouncycastle.asn1.cms.SignedData;
import com.android.internal.org.bouncycastle.asn1.cms.SignerInfo;
import com.android.internal.org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import com.android.internal.org.bouncycastle.cert.X509AttributeCertificateHolder;
import com.android.internal.org.bouncycastle.cert.X509CRLHolder;
import com.android.internal.org.bouncycastle.cert.X509CertificateHolder;
import com.android.internal.org.bouncycastle.util.Encodable;
import com.android.internal.org.bouncycastle.util.Store;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
/* loaded from: classes4.dex */
public class CMSSignedData implements Encodable {
    private static final CMSSignedHelper HELPER = CMSSignedHelper.INSTANCE;
    ContentInfo contentInfo;
    private Map hashes;
    CMSTypedData signedContent;
    SignedData signedData;
    SignerInformationStore signerInfoStore;

    private CMSSignedData(CMSSignedData c) {
        this.signedData = c.signedData;
        this.contentInfo = c.contentInfo;
        this.signedContent = c.signedContent;
        this.signerInfoStore = c.signerInfoStore;
    }

    public CMSSignedData(byte[] sigBlock) throws CMSException {
        this(CMSUtils.readContentInfo(sigBlock));
    }

    public CMSSignedData(CMSProcessable signedContent, byte[] sigBlock) throws CMSException {
        this(signedContent, CMSUtils.readContentInfo(sigBlock));
    }

    public CMSSignedData(Map hashes, byte[] sigBlock) throws CMSException {
        this(hashes, CMSUtils.readContentInfo(sigBlock));
    }

    public CMSSignedData(CMSProcessable signedContent, InputStream sigData) throws CMSException {
        this(signedContent, CMSUtils.readContentInfo((InputStream) new ASN1InputStream(sigData)));
    }

    public CMSSignedData(InputStream sigData) throws CMSException {
        this(CMSUtils.readContentInfo(sigData));
    }

    public CMSSignedData(final CMSProcessable signedContent, ContentInfo sigData) throws CMSException {
        if (signedContent instanceof CMSTypedData) {
            this.signedContent = (CMSTypedData) signedContent;
        } else {
            this.signedContent = new CMSTypedData() { // from class: com.android.internal.org.bouncycastle.cms.CMSSignedData.1
                @Override // com.android.internal.org.bouncycastle.cms.CMSTypedData
                public ASN1ObjectIdentifier getContentType() {
                    return CMSSignedData.this.signedData.getEncapContentInfo().getContentType();
                }

                @Override // com.android.internal.org.bouncycastle.cms.CMSProcessable
                public void write(OutputStream out) throws IOException, CMSException {
                    signedContent.write(out);
                }

                @Override // com.android.internal.org.bouncycastle.cms.CMSProcessable
                public Object getContent() {
                    return signedContent.getContent();
                }
            };
        }
        this.contentInfo = sigData;
        this.signedData = getSignedData();
    }

    public CMSSignedData(Map hashes, ContentInfo sigData) throws CMSException {
        this.hashes = hashes;
        this.contentInfo = sigData;
        this.signedData = getSignedData();
    }

    public CMSSignedData(ContentInfo sigData) throws CMSException {
        this.contentInfo = sigData;
        SignedData signedData = getSignedData();
        this.signedData = signedData;
        ASN1Encodable content = signedData.getEncapContentInfo().getContent();
        if (content != null) {
            if (content instanceof ASN1OctetString) {
                this.signedContent = new CMSProcessableByteArray(this.signedData.getEncapContentInfo().getContentType(), ((ASN1OctetString) content).getOctets());
                return;
            } else {
                this.signedContent = new PKCS7ProcessableObject(this.signedData.getEncapContentInfo().getContentType(), content);
                return;
            }
        }
        this.signedContent = null;
    }

    private SignedData getSignedData() throws CMSException {
        try {
            return SignedData.getInstance(this.contentInfo.getContent());
        } catch (ClassCastException e) {
            throw new CMSException("Malformed content.", e);
        } catch (IllegalArgumentException e2) {
            throw new CMSException("Malformed content.", e2);
        }
    }

    public int getVersion() {
        return this.signedData.getVersion().intValueExact();
    }

    public SignerInformationStore getSignerInfos() {
        Map map;
        Object algorithm;
        if (this.signerInfoStore == null) {
            ASN1Set s = this.signedData.getSignerInfos();
            List signerInfos = new ArrayList();
            for (int i = 0; i != s.size(); i++) {
                SignerInfo info = SignerInfo.getInstance(s.getObjectAt(i));
                ASN1ObjectIdentifier contentType = this.signedData.getEncapContentInfo().getContentType();
                Map map2 = this.hashes;
                if (map2 == null) {
                    signerInfos.add(new SignerInformation(info, contentType, this.signedContent, null));
                } else {
                    Object obj = map2.keySet().iterator().next();
                    if (obj instanceof String) {
                        map = this.hashes;
                        algorithm = info.getDigestAlgorithm().getAlgorithm().getId();
                    } else {
                        map = this.hashes;
                        algorithm = info.getDigestAlgorithm().getAlgorithm();
                    }
                    byte[] hash = (byte[]) map.get(algorithm);
                    signerInfos.add(new SignerInformation(info, contentType, null, hash));
                }
            }
            this.signerInfoStore = new SignerInformationStore(signerInfos);
        }
        return this.signerInfoStore;
    }

    public boolean isDetachedSignature() {
        return this.signedData.getEncapContentInfo().getContent() == null && this.signedData.getSignerInfos().size() > 0;
    }

    public boolean isCertificateManagementMessage() {
        return this.signedData.getEncapContentInfo().getContent() == null && this.signedData.getSignerInfos().size() == 0;
    }

    public Store<X509CertificateHolder> getCertificates() {
        return HELPER.getCertificates(this.signedData.getCertificates());
    }

    public Store<X509CRLHolder> getCRLs() {
        return HELPER.getCRLs(this.signedData.getCRLs());
    }

    public Store<X509AttributeCertificateHolder> getAttributeCertificates() {
        return HELPER.getAttributeCertificates(this.signedData.getCertificates());
    }

    public Set<AlgorithmIdentifier> getDigestAlgorithmIDs() {
        Set<AlgorithmIdentifier> digests = new HashSet<>(this.signedData.getDigestAlgorithms().size());
        Enumeration en = this.signedData.getDigestAlgorithms().getObjects();
        while (en.hasMoreElements()) {
            digests.add(AlgorithmIdentifier.getInstance(en.nextElement()));
        }
        return Collections.unmodifiableSet(digests);
    }

    public String getSignedContentTypeOID() {
        return this.signedData.getEncapContentInfo().getContentType().getId();
    }

    public CMSTypedData getSignedContent() {
        return this.signedContent;
    }

    public ContentInfo toASN1Structure() {
        return this.contentInfo;
    }

    @Override // com.android.internal.org.bouncycastle.util.Encodable
    public byte[] getEncoded() throws IOException {
        return this.contentInfo.getEncoded();
    }

    public byte[] getEncoded(String encoding) throws IOException {
        return this.contentInfo.getEncoded(encoding);
    }

    public static CMSSignedData replaceSigners(CMSSignedData signedData, SignerInformationStore signerInformationStore) {
        CMSSignedData cms = new CMSSignedData(signedData);
        cms.signerInfoStore = signerInformationStore;
        ASN1EncodableVector digestAlgs = new ASN1EncodableVector();
        ASN1EncodableVector vec = new ASN1EncodableVector();
        for (SignerInformation signer : signerInformationStore.getSigners()) {
            digestAlgs.add(CMSSignedHelper.INSTANCE.fixAlgID(signer.getDigestAlgorithmID()));
            vec.add(signer.toASN1Structure());
        }
        ASN1Set digests = new DERSet(digestAlgs);
        ASN1Set signers = new DLSet(vec);
        ASN1Sequence sD = (ASN1Sequence) signedData.signedData.toASN1Primitive();
        ASN1EncodableVector vec2 = new ASN1EncodableVector();
        vec2.add(sD.getObjectAt(0));
        vec2.add(digests);
        for (int i = 2; i != sD.size() - 1; i++) {
            vec2.add(sD.getObjectAt(i));
        }
        vec2.add(signers);
        cms.signedData = SignedData.getInstance(new BERSequence(vec2));
        cms.contentInfo = new ContentInfo(cms.contentInfo.getContentType(), cms.signedData);
        return cms;
    }

    public static CMSSignedData replaceCertificatesAndCRLs(CMSSignedData signedData, Store certificates, Store attrCerts, Store revocations) throws CMSException {
        CMSSignedData cms = new CMSSignedData(signedData);
        ASN1Set certSet = null;
        ASN1Set crlSet = null;
        if (certificates != null || attrCerts != null) {
            List certs = new ArrayList();
            if (certificates != null) {
                certs.addAll(CMSUtils.getCertificatesFromStore(certificates));
            }
            if (attrCerts != null) {
                certs.addAll(CMSUtils.getAttributeCertificatesFromStore(attrCerts));
            }
            ASN1Set set = CMSUtils.createBerSetFromList(certs);
            if (set.size() != 0) {
                certSet = set;
            }
        }
        if (revocations != null) {
            ASN1Set set2 = CMSUtils.createBerSetFromList(CMSUtils.getCRLsFromStore(revocations));
            if (set2.size() != 0) {
                crlSet = set2;
            }
        }
        cms.signedData = new SignedData(signedData.signedData.getDigestAlgorithms(), signedData.signedData.getEncapContentInfo(), certSet, crlSet, signedData.signedData.getSignerInfos());
        cms.contentInfo = new ContentInfo(cms.contentInfo.getContentType(), cms.signedData);
        return cms;
    }
}
