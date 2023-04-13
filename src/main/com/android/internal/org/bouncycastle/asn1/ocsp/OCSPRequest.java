package com.android.internal.org.bouncycastle.asn1.ocsp;

import com.android.internal.org.bouncycastle.asn1.ASN1EncodableVector;
import com.android.internal.org.bouncycastle.asn1.ASN1Object;
import com.android.internal.org.bouncycastle.asn1.ASN1Primitive;
import com.android.internal.org.bouncycastle.asn1.ASN1Sequence;
import com.android.internal.org.bouncycastle.asn1.ASN1TaggedObject;
import com.android.internal.org.bouncycastle.asn1.DERSequence;
import com.android.internal.org.bouncycastle.asn1.DERTaggedObject;
/* loaded from: classes4.dex */
public class OCSPRequest extends ASN1Object {
    Signature optionalSignature;
    TBSRequest tbsRequest;

    public OCSPRequest(TBSRequest tbsRequest, Signature optionalSignature) {
        this.tbsRequest = tbsRequest;
        this.optionalSignature = optionalSignature;
    }

    private OCSPRequest(ASN1Sequence seq) {
        this.tbsRequest = TBSRequest.getInstance(seq.getObjectAt(0));
        if (seq.size() == 2) {
            this.optionalSignature = Signature.getInstance((ASN1TaggedObject) seq.getObjectAt(1), true);
        }
    }

    public static OCSPRequest getInstance(ASN1TaggedObject obj, boolean explicit) {
        return getInstance(ASN1Sequence.getInstance(obj, explicit));
    }

    public static OCSPRequest getInstance(Object obj) {
        if (obj instanceof OCSPRequest) {
            return (OCSPRequest) obj;
        }
        if (obj != null) {
            return new OCSPRequest(ASN1Sequence.getInstance(obj));
        }
        return null;
    }

    public TBSRequest getTbsRequest() {
        return this.tbsRequest;
    }

    public Signature getOptionalSignature() {
        return this.optionalSignature;
    }

    @Override // com.android.internal.org.bouncycastle.asn1.ASN1Object, com.android.internal.org.bouncycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector(2);
        v.add(this.tbsRequest);
        Signature signature = this.optionalSignature;
        if (signature != null) {
            v.add(new DERTaggedObject(true, 0, signature));
        }
        return new DERSequence(v);
    }
}
