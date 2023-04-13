package com.android.internal.org.bouncycastle.asn1.ocsp;

import com.android.internal.org.bouncycastle.asn1.ASN1EncodableVector;
import com.android.internal.org.bouncycastle.asn1.ASN1Object;
import com.android.internal.org.bouncycastle.asn1.ASN1Primitive;
import com.android.internal.org.bouncycastle.asn1.ASN1Sequence;
import com.android.internal.org.bouncycastle.asn1.ASN1TaggedObject;
import com.android.internal.org.bouncycastle.asn1.DERSequence;
import com.android.internal.org.bouncycastle.asn1.DERTaggedObject;
import com.android.internal.org.bouncycastle.asn1.x509.Extensions;
/* loaded from: classes4.dex */
public class Request extends ASN1Object {
    CertID reqCert;
    Extensions singleRequestExtensions;

    public Request(CertID reqCert, Extensions singleRequestExtensions) {
        this.reqCert = reqCert;
        this.singleRequestExtensions = singleRequestExtensions;
    }

    private Request(ASN1Sequence seq) {
        this.reqCert = CertID.getInstance(seq.getObjectAt(0));
        if (seq.size() == 2) {
            this.singleRequestExtensions = Extensions.getInstance((ASN1TaggedObject) seq.getObjectAt(1), true);
        }
    }

    public static Request getInstance(ASN1TaggedObject obj, boolean explicit) {
        return getInstance(ASN1Sequence.getInstance(obj, explicit));
    }

    public static Request getInstance(Object obj) {
        if (obj instanceof Request) {
            return (Request) obj;
        }
        if (obj != null) {
            return new Request(ASN1Sequence.getInstance(obj));
        }
        return null;
    }

    public CertID getReqCert() {
        return this.reqCert;
    }

    public Extensions getSingleRequestExtensions() {
        return this.singleRequestExtensions;
    }

    @Override // com.android.internal.org.bouncycastle.asn1.ASN1Object, com.android.internal.org.bouncycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector(2);
        v.add(this.reqCert);
        Extensions extensions = this.singleRequestExtensions;
        if (extensions != null) {
            v.add(new DERTaggedObject(true, 0, extensions));
        }
        return new DERSequence(v);
    }
}
