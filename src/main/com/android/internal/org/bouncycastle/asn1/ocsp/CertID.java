package com.android.internal.org.bouncycastle.asn1.ocsp;

import com.android.internal.org.bouncycastle.asn1.ASN1EncodableVector;
import com.android.internal.org.bouncycastle.asn1.ASN1Integer;
import com.android.internal.org.bouncycastle.asn1.ASN1Object;
import com.android.internal.org.bouncycastle.asn1.ASN1OctetString;
import com.android.internal.org.bouncycastle.asn1.ASN1Primitive;
import com.android.internal.org.bouncycastle.asn1.ASN1Sequence;
import com.android.internal.org.bouncycastle.asn1.ASN1TaggedObject;
import com.android.internal.org.bouncycastle.asn1.DERSequence;
import com.android.internal.org.bouncycastle.asn1.x509.AlgorithmIdentifier;
/* loaded from: classes4.dex */
public class CertID extends ASN1Object {
    AlgorithmIdentifier hashAlgorithm;
    ASN1OctetString issuerKeyHash;
    ASN1OctetString issuerNameHash;
    ASN1Integer serialNumber;

    public CertID(AlgorithmIdentifier hashAlgorithm, ASN1OctetString issuerNameHash, ASN1OctetString issuerKeyHash, ASN1Integer serialNumber) {
        this.hashAlgorithm = hashAlgorithm;
        this.issuerNameHash = issuerNameHash;
        this.issuerKeyHash = issuerKeyHash;
        this.serialNumber = serialNumber;
    }

    private CertID(ASN1Sequence seq) {
        this.hashAlgorithm = AlgorithmIdentifier.getInstance(seq.getObjectAt(0));
        this.issuerNameHash = (ASN1OctetString) seq.getObjectAt(1);
        this.issuerKeyHash = (ASN1OctetString) seq.getObjectAt(2);
        this.serialNumber = (ASN1Integer) seq.getObjectAt(3);
    }

    public static CertID getInstance(ASN1TaggedObject obj, boolean explicit) {
        return getInstance(ASN1Sequence.getInstance(obj, explicit));
    }

    public static CertID getInstance(Object obj) {
        if (obj instanceof CertID) {
            return (CertID) obj;
        }
        if (obj != null) {
            return new CertID(ASN1Sequence.getInstance(obj));
        }
        return null;
    }

    public AlgorithmIdentifier getHashAlgorithm() {
        return this.hashAlgorithm;
    }

    public ASN1OctetString getIssuerNameHash() {
        return this.issuerNameHash;
    }

    public ASN1OctetString getIssuerKeyHash() {
        return this.issuerKeyHash;
    }

    public ASN1Integer getSerialNumber() {
        return this.serialNumber;
    }

    @Override // com.android.internal.org.bouncycastle.asn1.ASN1Object, com.android.internal.org.bouncycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector(4);
        v.add(this.hashAlgorithm);
        v.add(this.issuerNameHash);
        v.add(this.issuerKeyHash);
        v.add(this.serialNumber);
        return new DERSequence(v);
    }
}
