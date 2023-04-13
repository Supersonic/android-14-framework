package com.android.internal.org.bouncycastle.asn1.ocsp;

import com.android.internal.org.bouncycastle.asn1.ASN1EncodableVector;
import com.android.internal.org.bouncycastle.asn1.ASN1Object;
import com.android.internal.org.bouncycastle.asn1.ASN1Primitive;
import com.android.internal.org.bouncycastle.asn1.ASN1Sequence;
import com.android.internal.org.bouncycastle.asn1.ASN1TaggedObject;
import com.android.internal.org.bouncycastle.asn1.DERBitString;
import com.android.internal.org.bouncycastle.asn1.DERSequence;
import com.android.internal.org.bouncycastle.asn1.DERTaggedObject;
import com.android.internal.org.bouncycastle.asn1.x509.AlgorithmIdentifier;
/* loaded from: classes4.dex */
public class Signature extends ASN1Object {
    ASN1Sequence certs;
    DERBitString signature;
    AlgorithmIdentifier signatureAlgorithm;

    public Signature(AlgorithmIdentifier signatureAlgorithm, DERBitString signature) {
        this.signatureAlgorithm = signatureAlgorithm;
        this.signature = signature;
    }

    public Signature(AlgorithmIdentifier signatureAlgorithm, DERBitString signature, ASN1Sequence certs) {
        this.signatureAlgorithm = signatureAlgorithm;
        this.signature = signature;
        this.certs = certs;
    }

    private Signature(ASN1Sequence seq) {
        this.signatureAlgorithm = AlgorithmIdentifier.getInstance(seq.getObjectAt(0));
        this.signature = (DERBitString) seq.getObjectAt(1);
        if (seq.size() == 3) {
            this.certs = ASN1Sequence.getInstance((ASN1TaggedObject) seq.getObjectAt(2), true);
        }
    }

    public static Signature getInstance(ASN1TaggedObject obj, boolean explicit) {
        return getInstance(ASN1Sequence.getInstance(obj, explicit));
    }

    public static Signature getInstance(Object obj) {
        if (obj instanceof Signature) {
            return (Signature) obj;
        }
        if (obj != null) {
            return new Signature(ASN1Sequence.getInstance(obj));
        }
        return null;
    }

    public AlgorithmIdentifier getSignatureAlgorithm() {
        return this.signatureAlgorithm;
    }

    public DERBitString getSignature() {
        return this.signature;
    }

    public ASN1Sequence getCerts() {
        return this.certs;
    }

    @Override // com.android.internal.org.bouncycastle.asn1.ASN1Object, com.android.internal.org.bouncycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector(3);
        v.add(this.signatureAlgorithm);
        v.add(this.signature);
        ASN1Sequence aSN1Sequence = this.certs;
        if (aSN1Sequence != null) {
            v.add(new DERTaggedObject(true, 0, aSN1Sequence));
        }
        return new DERSequence(v);
    }
}
