package com.android.internal.org.bouncycastle.asn1.x509;

import com.android.internal.org.bouncycastle.asn1.ASN1Integer;
import com.android.internal.org.bouncycastle.asn1.ASN1Object;
import com.android.internal.org.bouncycastle.asn1.ASN1Primitive;
import com.android.internal.org.bouncycastle.asn1.ASN1Sequence;
import com.android.internal.org.bouncycastle.asn1.ASN1TaggedObject;
import com.android.internal.org.bouncycastle.asn1.DERBitString;
import com.android.internal.org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import com.android.internal.org.bouncycastle.asn1.x500.X500Name;
/* loaded from: classes4.dex */
public class TBSCertificateStructure extends ASN1Object implements X509ObjectIdentifiers, PKCSObjectIdentifiers {
    Time endDate;
    X509Extensions extensions;
    X500Name issuer;
    DERBitString issuerUniqueId;
    ASN1Sequence seq;
    ASN1Integer serialNumber;
    AlgorithmIdentifier signature;
    Time startDate;
    X500Name subject;
    SubjectPublicKeyInfo subjectPublicKeyInfo;
    DERBitString subjectUniqueId;
    ASN1Integer version;

    public static TBSCertificateStructure getInstance(ASN1TaggedObject obj, boolean explicit) {
        return getInstance(ASN1Sequence.getInstance(obj, explicit));
    }

    public static TBSCertificateStructure getInstance(Object obj) {
        if (obj instanceof TBSCertificateStructure) {
            return (TBSCertificateStructure) obj;
        }
        if (obj != null) {
            return new TBSCertificateStructure(ASN1Sequence.getInstance(obj));
        }
        return null;
    }

    public TBSCertificateStructure(ASN1Sequence seq) {
        int seqStart = 0;
        this.seq = seq;
        if (seq.getObjectAt(0) instanceof ASN1TaggedObject) {
            this.version = ASN1Integer.getInstance((ASN1TaggedObject) seq.getObjectAt(0), true);
        } else {
            seqStart = -1;
            this.version = new ASN1Integer(0L);
        }
        this.serialNumber = ASN1Integer.getInstance(seq.getObjectAt(seqStart + 1));
        this.signature = AlgorithmIdentifier.getInstance(seq.getObjectAt(seqStart + 2));
        this.issuer = X500Name.getInstance(seq.getObjectAt(seqStart + 3));
        ASN1Sequence dates = (ASN1Sequence) seq.getObjectAt(seqStart + 4);
        this.startDate = Time.getInstance(dates.getObjectAt(0));
        this.endDate = Time.getInstance(dates.getObjectAt(1));
        this.subject = X500Name.getInstance(seq.getObjectAt(seqStart + 5));
        this.subjectPublicKeyInfo = SubjectPublicKeyInfo.getInstance(seq.getObjectAt(seqStart + 6));
        for (int extras = (seq.size() - (seqStart + 6)) - 1; extras > 0; extras--) {
            ASN1TaggedObject extra = ASN1TaggedObject.getInstance(seq.getObjectAt(seqStart + 6 + extras));
            switch (extra.getTagNo()) {
                case 1:
                    this.issuerUniqueId = DERBitString.getInstance(extra, false);
                    break;
                case 2:
                    this.subjectUniqueId = DERBitString.getInstance(extra, false);
                    break;
                case 3:
                    this.extensions = X509Extensions.getInstance(extra);
                    break;
            }
        }
    }

    public int getVersion() {
        return this.version.intValueExact() + 1;
    }

    public ASN1Integer getVersionNumber() {
        return this.version;
    }

    public ASN1Integer getSerialNumber() {
        return this.serialNumber;
    }

    public AlgorithmIdentifier getSignature() {
        return this.signature;
    }

    public X500Name getIssuer() {
        return this.issuer;
    }

    public Time getStartDate() {
        return this.startDate;
    }

    public Time getEndDate() {
        return this.endDate;
    }

    public X500Name getSubject() {
        return this.subject;
    }

    public SubjectPublicKeyInfo getSubjectPublicKeyInfo() {
        return this.subjectPublicKeyInfo;
    }

    public DERBitString getIssuerUniqueId() {
        return this.issuerUniqueId;
    }

    public DERBitString getSubjectUniqueId() {
        return this.subjectUniqueId;
    }

    public X509Extensions getExtensions() {
        return this.extensions;
    }

    @Override // com.android.internal.org.bouncycastle.asn1.ASN1Object, com.android.internal.org.bouncycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        return this.seq;
    }
}
