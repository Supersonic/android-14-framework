package com.android.internal.org.bouncycastle.asn1.ocsp;

import com.android.internal.org.bouncycastle.asn1.ASN1EncodableVector;
import com.android.internal.org.bouncycastle.asn1.ASN1GeneralizedTime;
import com.android.internal.org.bouncycastle.asn1.ASN1Object;
import com.android.internal.org.bouncycastle.asn1.ASN1Primitive;
import com.android.internal.org.bouncycastle.asn1.ASN1Sequence;
import com.android.internal.org.bouncycastle.asn1.ASN1TaggedObject;
import com.android.internal.org.bouncycastle.asn1.DERSequence;
import com.android.internal.org.bouncycastle.asn1.DERTaggedObject;
import com.android.internal.org.bouncycastle.asn1.x509.Extensions;
import com.android.internal.org.bouncycastle.asn1.x509.X509Extensions;
/* loaded from: classes4.dex */
public class SingleResponse extends ASN1Object {
    private CertID certID;
    private CertStatus certStatus;
    private ASN1GeneralizedTime nextUpdate;
    private Extensions singleExtensions;
    private ASN1GeneralizedTime thisUpdate;

    public SingleResponse(CertID certID, CertStatus certStatus, ASN1GeneralizedTime thisUpdate, ASN1GeneralizedTime nextUpdate, X509Extensions singleExtensions) {
        this(certID, certStatus, thisUpdate, nextUpdate, Extensions.getInstance(singleExtensions));
    }

    public SingleResponse(CertID certID, CertStatus certStatus, ASN1GeneralizedTime thisUpdate, ASN1GeneralizedTime nextUpdate, Extensions singleExtensions) {
        this.certID = certID;
        this.certStatus = certStatus;
        this.thisUpdate = thisUpdate;
        this.nextUpdate = nextUpdate;
        this.singleExtensions = singleExtensions;
    }

    private SingleResponse(ASN1Sequence seq) {
        this.certID = CertID.getInstance(seq.getObjectAt(0));
        this.certStatus = CertStatus.getInstance(seq.getObjectAt(1));
        this.thisUpdate = ASN1GeneralizedTime.getInstance(seq.getObjectAt(2));
        if (seq.size() > 4) {
            this.nextUpdate = ASN1GeneralizedTime.getInstance((ASN1TaggedObject) seq.getObjectAt(3), true);
            this.singleExtensions = Extensions.getInstance((ASN1TaggedObject) seq.getObjectAt(4), true);
        } else if (seq.size() > 3) {
            ASN1TaggedObject o = (ASN1TaggedObject) seq.getObjectAt(3);
            if (o.getTagNo() == 0) {
                this.nextUpdate = ASN1GeneralizedTime.getInstance(o, true);
            } else {
                this.singleExtensions = Extensions.getInstance(o, true);
            }
        }
    }

    public static SingleResponse getInstance(ASN1TaggedObject obj, boolean explicit) {
        return getInstance(ASN1Sequence.getInstance(obj, explicit));
    }

    public static SingleResponse getInstance(Object obj) {
        if (obj instanceof SingleResponse) {
            return (SingleResponse) obj;
        }
        if (obj != null) {
            return new SingleResponse(ASN1Sequence.getInstance(obj));
        }
        return null;
    }

    public CertID getCertID() {
        return this.certID;
    }

    public CertStatus getCertStatus() {
        return this.certStatus;
    }

    public ASN1GeneralizedTime getThisUpdate() {
        return this.thisUpdate;
    }

    public ASN1GeneralizedTime getNextUpdate() {
        return this.nextUpdate;
    }

    public Extensions getSingleExtensions() {
        return this.singleExtensions;
    }

    @Override // com.android.internal.org.bouncycastle.asn1.ASN1Object, com.android.internal.org.bouncycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector(5);
        v.add(this.certID);
        v.add(this.certStatus);
        v.add(this.thisUpdate);
        ASN1GeneralizedTime aSN1GeneralizedTime = this.nextUpdate;
        if (aSN1GeneralizedTime != null) {
            v.add(new DERTaggedObject(true, 0, aSN1GeneralizedTime));
        }
        Extensions extensions = this.singleExtensions;
        if (extensions != null) {
            v.add(new DERTaggedObject(true, 1, extensions));
        }
        return new DERSequence(v);
    }
}
