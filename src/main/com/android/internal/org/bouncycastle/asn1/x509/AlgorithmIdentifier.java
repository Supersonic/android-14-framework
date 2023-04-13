package com.android.internal.org.bouncycastle.asn1.x509;

import com.android.internal.org.bouncycastle.asn1.ASN1Encodable;
import com.android.internal.org.bouncycastle.asn1.ASN1EncodableVector;
import com.android.internal.org.bouncycastle.asn1.ASN1Object;
import com.android.internal.org.bouncycastle.asn1.ASN1ObjectIdentifier;
import com.android.internal.org.bouncycastle.asn1.ASN1Primitive;
import com.android.internal.org.bouncycastle.asn1.ASN1Sequence;
import com.android.internal.org.bouncycastle.asn1.ASN1TaggedObject;
import com.android.internal.org.bouncycastle.asn1.DERSequence;
/* loaded from: classes4.dex */
public class AlgorithmIdentifier extends ASN1Object {
    private ASN1ObjectIdentifier algorithm;
    private ASN1Encodable parameters;

    public static AlgorithmIdentifier getInstance(ASN1TaggedObject obj, boolean explicit) {
        return getInstance(ASN1Sequence.getInstance(obj, explicit));
    }

    public static AlgorithmIdentifier getInstance(Object obj) {
        if (obj instanceof AlgorithmIdentifier) {
            return (AlgorithmIdentifier) obj;
        }
        if (obj != null) {
            return new AlgorithmIdentifier(ASN1Sequence.getInstance(obj));
        }
        return null;
    }

    public AlgorithmIdentifier(ASN1ObjectIdentifier algorithm) {
        this.algorithm = algorithm;
    }

    public AlgorithmIdentifier(ASN1ObjectIdentifier algorithm, ASN1Encodable parameters) {
        this.algorithm = algorithm;
        this.parameters = parameters;
    }

    private AlgorithmIdentifier(ASN1Sequence seq) {
        if (seq.size() < 1 || seq.size() > 2) {
            throw new IllegalArgumentException("Bad sequence size: " + seq.size());
        }
        this.algorithm = ASN1ObjectIdentifier.getInstance(seq.getObjectAt(0));
        if (seq.size() == 2) {
            this.parameters = seq.getObjectAt(1);
        } else {
            this.parameters = null;
        }
    }

    public ASN1ObjectIdentifier getAlgorithm() {
        return this.algorithm;
    }

    public ASN1Encodable getParameters() {
        return this.parameters;
    }

    @Override // com.android.internal.org.bouncycastle.asn1.ASN1Object, com.android.internal.org.bouncycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector(2);
        v.add(this.algorithm);
        ASN1Encodable aSN1Encodable = this.parameters;
        if (aSN1Encodable != null) {
            v.add(aSN1Encodable);
        }
        return new DERSequence(v);
    }
}
