package com.android.internal.org.bouncycastle.asn1.x509;

import com.android.internal.org.bouncycastle.asn1.ASN1EncodableVector;
import com.android.internal.org.bouncycastle.asn1.ASN1Integer;
import com.android.internal.org.bouncycastle.asn1.ASN1UTCTime;
import com.android.internal.org.bouncycastle.asn1.DERBitString;
import com.android.internal.org.bouncycastle.asn1.DERSequence;
import com.android.internal.org.bouncycastle.asn1.DERTaggedObject;
import com.android.internal.org.bouncycastle.asn1.x500.X500Name;
/* loaded from: classes4.dex */
public class V3TBSCertificateGenerator {
    private boolean altNamePresentAndCritical;
    Time endDate;
    Extensions extensions;
    X500Name issuer;
    private DERBitString issuerUniqueID;
    ASN1Integer serialNumber;
    AlgorithmIdentifier signature;
    Time startDate;
    X500Name subject;
    SubjectPublicKeyInfo subjectPublicKeyInfo;
    private DERBitString subjectUniqueID;
    DERTaggedObject version = new DERTaggedObject(true, 0, new ASN1Integer(2));

    public void setSerialNumber(ASN1Integer serialNumber) {
        this.serialNumber = serialNumber;
    }

    public void setSignature(AlgorithmIdentifier signature) {
        this.signature = signature;
    }

    public void setIssuer(X509Name issuer) {
        this.issuer = X500Name.getInstance(issuer);
    }

    public void setIssuer(X500Name issuer) {
        this.issuer = issuer;
    }

    public void setStartDate(ASN1UTCTime startDate) {
        this.startDate = new Time(startDate);
    }

    public void setStartDate(Time startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(ASN1UTCTime endDate) {
        this.endDate = new Time(endDate);
    }

    public void setEndDate(Time endDate) {
        this.endDate = endDate;
    }

    public void setSubject(X509Name subject) {
        this.subject = X500Name.getInstance(subject.toASN1Primitive());
    }

    public void setSubject(X500Name subject) {
        this.subject = subject;
    }

    public void setIssuerUniqueID(DERBitString uniqueID) {
        this.issuerUniqueID = uniqueID;
    }

    public void setSubjectUniqueID(DERBitString uniqueID) {
        this.subjectUniqueID = uniqueID;
    }

    public void setSubjectPublicKeyInfo(SubjectPublicKeyInfo pubKeyInfo) {
        this.subjectPublicKeyInfo = pubKeyInfo;
    }

    public void setExtensions(X509Extensions extensions) {
        setExtensions(Extensions.getInstance(extensions));
    }

    public void setExtensions(Extensions extensions) {
        Extension altName;
        this.extensions = extensions;
        if (extensions != null && (altName = extensions.getExtension(Extension.subjectAlternativeName)) != null && altName.isCritical()) {
            this.altNamePresentAndCritical = true;
        }
    }

    public TBSCertificate generateTBSCertificate() {
        if (this.serialNumber == null || this.signature == null || this.issuer == null || this.startDate == null || this.endDate == null || ((this.subject == null && !this.altNamePresentAndCritical) || this.subjectPublicKeyInfo == null)) {
            throw new IllegalStateException("not all mandatory fields set in V3 TBScertificate generator");
        }
        ASN1EncodableVector v = new ASN1EncodableVector(10);
        v.add(this.version);
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
        DERBitString dERBitString = this.issuerUniqueID;
        if (dERBitString != null) {
            v.add(new DERTaggedObject(false, 1, dERBitString));
        }
        DERBitString dERBitString2 = this.subjectUniqueID;
        if (dERBitString2 != null) {
            v.add(new DERTaggedObject(false, 2, dERBitString2));
        }
        Extensions extensions = this.extensions;
        if (extensions != null) {
            v.add(new DERTaggedObject(true, 3, extensions));
        }
        return TBSCertificate.getInstance(new DERSequence(v));
    }
}
