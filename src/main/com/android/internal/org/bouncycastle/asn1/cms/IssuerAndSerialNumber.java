package com.android.internal.org.bouncycastle.asn1.cms;

import com.android.internal.org.bouncycastle.asn1.ASN1EncodableVector;
import com.android.internal.org.bouncycastle.asn1.ASN1Integer;
import com.android.internal.org.bouncycastle.asn1.ASN1Object;
import com.android.internal.org.bouncycastle.asn1.ASN1Primitive;
import com.android.internal.org.bouncycastle.asn1.ASN1Sequence;
import com.android.internal.org.bouncycastle.asn1.DERSequence;
import com.android.internal.org.bouncycastle.asn1.x500.X500Name;
import com.android.internal.org.bouncycastle.asn1.x509.Certificate;
import com.android.internal.org.bouncycastle.asn1.x509.X509CertificateStructure;
import com.android.internal.org.bouncycastle.asn1.x509.X509Name;
import java.math.BigInteger;
/* loaded from: classes4.dex */
public class IssuerAndSerialNumber extends ASN1Object {
    private X500Name name;
    private ASN1Integer serialNumber;

    public static IssuerAndSerialNumber getInstance(Object obj) {
        if (obj instanceof IssuerAndSerialNumber) {
            return (IssuerAndSerialNumber) obj;
        }
        if (obj != null) {
            return new IssuerAndSerialNumber(ASN1Sequence.getInstance(obj));
        }
        return null;
    }

    public IssuerAndSerialNumber(ASN1Sequence seq) {
        this.name = X500Name.getInstance(seq.getObjectAt(0));
        this.serialNumber = (ASN1Integer) seq.getObjectAt(1);
    }

    public IssuerAndSerialNumber(Certificate certificate) {
        this.name = certificate.getIssuer();
        this.serialNumber = certificate.getSerialNumber();
    }

    public IssuerAndSerialNumber(X509CertificateStructure certificate) {
        this.name = certificate.getIssuer();
        this.serialNumber = certificate.getSerialNumber();
    }

    public IssuerAndSerialNumber(X500Name name, BigInteger serialNumber) {
        this.name = name;
        this.serialNumber = new ASN1Integer(serialNumber);
    }

    public IssuerAndSerialNumber(X509Name name, BigInteger serialNumber) {
        this.name = X500Name.getInstance(name);
        this.serialNumber = new ASN1Integer(serialNumber);
    }

    public IssuerAndSerialNumber(X509Name name, ASN1Integer serialNumber) {
        this.name = X500Name.getInstance(name);
        this.serialNumber = serialNumber;
    }

    public X500Name getName() {
        return this.name;
    }

    public ASN1Integer getSerialNumber() {
        return this.serialNumber;
    }

    @Override // com.android.internal.org.bouncycastle.asn1.ASN1Object, com.android.internal.org.bouncycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector(2);
        v.add(this.name);
        v.add(this.serialNumber);
        return new DERSequence(v);
    }
}
