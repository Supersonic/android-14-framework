package com.android.internal.org.bouncycastle.asn1.x509;

import com.android.internal.org.bouncycastle.asn1.ASN1EncodableVector;
import com.android.internal.org.bouncycastle.asn1.ASN1Enumerated;
import com.android.internal.org.bouncycastle.asn1.ASN1Object;
import com.android.internal.org.bouncycastle.asn1.ASN1ObjectIdentifier;
import com.android.internal.org.bouncycastle.asn1.ASN1Primitive;
import com.android.internal.org.bouncycastle.asn1.ASN1Sequence;
import com.android.internal.org.bouncycastle.asn1.ASN1TaggedObject;
import com.android.internal.org.bouncycastle.asn1.DERBitString;
import com.android.internal.org.bouncycastle.asn1.DERSequence;
/* loaded from: classes4.dex */
public class ObjectDigestInfo extends ASN1Object {
    public static final int otherObjectDigest = 2;
    public static final int publicKey = 0;
    public static final int publicKeyCert = 1;
    AlgorithmIdentifier digestAlgorithm;
    ASN1Enumerated digestedObjectType;
    DERBitString objectDigest;
    ASN1ObjectIdentifier otherObjectTypeID;

    public static ObjectDigestInfo getInstance(Object obj) {
        if (obj instanceof ObjectDigestInfo) {
            return (ObjectDigestInfo) obj;
        }
        if (obj != null) {
            return new ObjectDigestInfo(ASN1Sequence.getInstance(obj));
        }
        return null;
    }

    public static ObjectDigestInfo getInstance(ASN1TaggedObject obj, boolean explicit) {
        return getInstance(ASN1Sequence.getInstance(obj, explicit));
    }

    public ObjectDigestInfo(int digestedObjectType, ASN1ObjectIdentifier otherObjectTypeID, AlgorithmIdentifier digestAlgorithm, byte[] objectDigest) {
        this.digestedObjectType = new ASN1Enumerated(digestedObjectType);
        if (digestedObjectType == 2) {
            this.otherObjectTypeID = otherObjectTypeID;
        }
        this.digestAlgorithm = digestAlgorithm;
        this.objectDigest = new DERBitString(objectDigest);
    }

    private ObjectDigestInfo(ASN1Sequence seq) {
        if (seq.size() > 4 || seq.size() < 3) {
            throw new IllegalArgumentException("Bad sequence size: " + seq.size());
        }
        this.digestedObjectType = ASN1Enumerated.getInstance(seq.getObjectAt(0));
        int offset = 0;
        if (seq.size() == 4) {
            this.otherObjectTypeID = ASN1ObjectIdentifier.getInstance(seq.getObjectAt(1));
            offset = 0 + 1;
        }
        this.digestAlgorithm = AlgorithmIdentifier.getInstance(seq.getObjectAt(offset + 1));
        this.objectDigest = DERBitString.getInstance(seq.getObjectAt(offset + 2));
    }

    public ASN1Enumerated getDigestedObjectType() {
        return this.digestedObjectType;
    }

    public ASN1ObjectIdentifier getOtherObjectTypeID() {
        return this.otherObjectTypeID;
    }

    public AlgorithmIdentifier getDigestAlgorithm() {
        return this.digestAlgorithm;
    }

    public DERBitString getObjectDigest() {
        return this.objectDigest;
    }

    @Override // com.android.internal.org.bouncycastle.asn1.ASN1Object, com.android.internal.org.bouncycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector(4);
        v.add(this.digestedObjectType);
        ASN1ObjectIdentifier aSN1ObjectIdentifier = this.otherObjectTypeID;
        if (aSN1ObjectIdentifier != null) {
            v.add(aSN1ObjectIdentifier);
        }
        v.add(this.digestAlgorithm);
        v.add(this.objectDigest);
        return new DERSequence(v);
    }
}
