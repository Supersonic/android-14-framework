package com.android.internal.org.bouncycastle.asn1.x509;

import com.android.internal.org.bouncycastle.asn1.ASN1EncodableVector;
import com.android.internal.org.bouncycastle.asn1.ASN1Integer;
import com.android.internal.org.bouncycastle.asn1.ASN1Object;
import com.android.internal.org.bouncycastle.asn1.ASN1Primitive;
import com.android.internal.org.bouncycastle.asn1.ASN1Sequence;
import com.android.internal.org.bouncycastle.asn1.ASN1TaggedObject;
import com.android.internal.org.bouncycastle.asn1.DERBitString;
import com.android.internal.org.bouncycastle.asn1.DERSequence;
import com.android.internal.org.bouncycastle.asn1.DERTaggedObject;
import com.android.internal.org.bouncycastle.asn1.x500.X500Name;
import com.android.internal.org.bouncycastle.util.BigIntegers;
import com.android.internal.org.bouncycastle.util.Properties;
import java.math.BigInteger;
/* loaded from: classes4.dex */
public class TBSCertificate extends ASN1Object {
    Time endDate;
    Extensions extensions;
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

    public static TBSCertificate getInstance(ASN1TaggedObject obj, boolean explicit) {
        return getInstance(ASN1Sequence.getInstance(obj, explicit));
    }

    public static TBSCertificate getInstance(Object obj) {
        if (obj instanceof TBSCertificate) {
            return (TBSCertificate) obj;
        }
        if (obj != null) {
            return new TBSCertificate(ASN1Sequence.getInstance(obj));
        }
        return null;
    }

    private TBSCertificate(ASN1Sequence seq) {
        int seqStart = 0;
        this.seq = seq;
        if (seq.getObjectAt(0) instanceof ASN1TaggedObject) {
            this.version = ASN1Integer.getInstance((ASN1TaggedObject) seq.getObjectAt(0), true);
        } else {
            seqStart = -1;
            this.version = new ASN1Integer(0L);
        }
        boolean isV1 = false;
        boolean isV2 = false;
        if (this.version.hasValue(BigInteger.valueOf(0L))) {
            isV1 = true;
        } else if (this.version.hasValue(BigInteger.valueOf(1L))) {
            isV2 = true;
        } else if (!this.version.hasValue(BigInteger.valueOf(2L))) {
            throw new IllegalArgumentException("version number not recognised");
        }
        this.serialNumber = ASN1Integer.getInstance(seq.getObjectAt(seqStart + 1));
        this.signature = AlgorithmIdentifier.getInstance(seq.getObjectAt(seqStart + 2));
        this.issuer = X500Name.getInstance(seq.getObjectAt(seqStart + 3));
        ASN1Sequence dates = (ASN1Sequence) seq.getObjectAt(seqStart + 4);
        this.startDate = Time.getInstance(dates.getObjectAt(0));
        this.endDate = Time.getInstance(dates.getObjectAt(1));
        this.subject = X500Name.getInstance(seq.getObjectAt(seqStart + 5));
        this.subjectPublicKeyInfo = SubjectPublicKeyInfo.getInstance(seq.getObjectAt(seqStart + 6));
        int extras = (seq.size() - (seqStart + 6)) - 1;
        if (extras != 0 && isV1) {
            throw new IllegalArgumentException("version 1 certificate contains extra data");
        }
        while (extras > 0) {
            ASN1TaggedObject extra = (ASN1TaggedObject) seq.getObjectAt(seqStart + 6 + extras);
            switch (extra.getTagNo()) {
                case 1:
                    this.issuerUniqueId = DERBitString.getInstance(extra, false);
                    break;
                case 2:
                    this.subjectUniqueId = DERBitString.getInstance(extra, false);
                    break;
                case 3:
                    if (isV2) {
                        throw new IllegalArgumentException("version 2 certificate cannot contain extensions");
                    }
                    this.extensions = Extensions.getInstance(ASN1Sequence.getInstance(extra, true));
                    break;
                default:
                    throw new IllegalArgumentException("Unknown tag encountered in structure: " + extra.getTagNo());
            }
            extras--;
        }
    }

    public int getVersionNumber() {
        return this.version.intValueExact() + 1;
    }

    public ASN1Integer getVersion() {
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

    public Extensions getExtensions() {
        return this.extensions;
    }

    @Override // com.android.internal.org.bouncycastle.asn1.ASN1Object, com.android.internal.org.bouncycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        if (Properties.getPropertyValue("com.android.internal.org.bouncycastle.x509.allow_non-der_tbscert") != null) {
            if (Properties.isOverrideSet("com.android.internal.org.bouncycastle.x509.allow_non-der_tbscert")) {
                return this.seq;
            }
            ASN1EncodableVector v = new ASN1EncodableVector();
            if (!this.version.hasValue(BigIntegers.ZERO)) {
                v.add(new DERTaggedObject(true, 0, this.version));
            }
            v.add(this.serialNumber);
            v.add(this.signature);
            v.add(this.issuer);
            ASN1EncodableVector validity = new ASN1EncodableVector(2);
            validity.add(this.startDate);
            validity.add(this.endDate);
            v.add(new DERSequence(validity));
            X500Name x500Name = this.subject;
            if (x500Name != null) {
                v.add(x500Name);
            } else {
                v.add(new DERSequence());
            }
            v.add(this.subjectPublicKeyInfo);
            DERBitString dERBitString = this.issuerUniqueId;
            if (dERBitString != null) {
                v.add(new DERTaggedObject(false, 1, dERBitString));
            }
            DERBitString dERBitString2 = this.subjectUniqueId;
            if (dERBitString2 != null) {
                v.add(new DERTaggedObject(false, 2, dERBitString2));
            }
            Extensions extensions = this.extensions;
            if (extensions != null) {
                v.add(new DERTaggedObject(true, 3, extensions));
            }
            return new DERSequence(v);
        }
        return this.seq;
    }
}
