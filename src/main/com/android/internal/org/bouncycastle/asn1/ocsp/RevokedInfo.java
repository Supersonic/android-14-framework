package com.android.internal.org.bouncycastle.asn1.ocsp;

import com.android.internal.org.bouncycastle.asn1.ASN1EncodableVector;
import com.android.internal.org.bouncycastle.asn1.ASN1Enumerated;
import com.android.internal.org.bouncycastle.asn1.ASN1GeneralizedTime;
import com.android.internal.org.bouncycastle.asn1.ASN1Object;
import com.android.internal.org.bouncycastle.asn1.ASN1Primitive;
import com.android.internal.org.bouncycastle.asn1.ASN1Sequence;
import com.android.internal.org.bouncycastle.asn1.ASN1TaggedObject;
import com.android.internal.org.bouncycastle.asn1.DERSequence;
import com.android.internal.org.bouncycastle.asn1.DERTaggedObject;
import com.android.internal.org.bouncycastle.asn1.x509.CRLReason;
/* loaded from: classes4.dex */
public class RevokedInfo extends ASN1Object {
    private CRLReason revocationReason;
    private ASN1GeneralizedTime revocationTime;

    public RevokedInfo(ASN1GeneralizedTime revocationTime, CRLReason revocationReason) {
        this.revocationTime = revocationTime;
        this.revocationReason = revocationReason;
    }

    private RevokedInfo(ASN1Sequence seq) {
        this.revocationTime = ASN1GeneralizedTime.getInstance(seq.getObjectAt(0));
        if (seq.size() > 1) {
            this.revocationReason = CRLReason.getInstance(ASN1Enumerated.getInstance((ASN1TaggedObject) seq.getObjectAt(1), true));
        }
    }

    public static RevokedInfo getInstance(ASN1TaggedObject obj, boolean explicit) {
        return getInstance(ASN1Sequence.getInstance(obj, explicit));
    }

    public static RevokedInfo getInstance(Object obj) {
        if (obj instanceof RevokedInfo) {
            return (RevokedInfo) obj;
        }
        if (obj != null) {
            return new RevokedInfo(ASN1Sequence.getInstance(obj));
        }
        return null;
    }

    public ASN1GeneralizedTime getRevocationTime() {
        return this.revocationTime;
    }

    public CRLReason getRevocationReason() {
        return this.revocationReason;
    }

    @Override // com.android.internal.org.bouncycastle.asn1.ASN1Object, com.android.internal.org.bouncycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector(2);
        v.add(this.revocationTime);
        CRLReason cRLReason = this.revocationReason;
        if (cRLReason != null) {
            v.add(new DERTaggedObject(true, 0, cRLReason));
        }
        return new DERSequence(v);
    }
}
