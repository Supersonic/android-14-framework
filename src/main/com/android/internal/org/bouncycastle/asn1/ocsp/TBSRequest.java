package com.android.internal.org.bouncycastle.asn1.ocsp;

import com.android.internal.org.bouncycastle.asn1.ASN1EncodableVector;
import com.android.internal.org.bouncycastle.asn1.ASN1Integer;
import com.android.internal.org.bouncycastle.asn1.ASN1Object;
import com.android.internal.org.bouncycastle.asn1.ASN1Primitive;
import com.android.internal.org.bouncycastle.asn1.ASN1Sequence;
import com.android.internal.org.bouncycastle.asn1.ASN1TaggedObject;
import com.android.internal.org.bouncycastle.asn1.DERSequence;
import com.android.internal.org.bouncycastle.asn1.DERTaggedObject;
import com.android.internal.org.bouncycastle.asn1.x509.Extensions;
import com.android.internal.org.bouncycastle.asn1.x509.GeneralName;
import com.android.internal.org.bouncycastle.asn1.x509.X509Extensions;
/* loaded from: classes4.dex */
public class TBSRequest extends ASN1Object {

    /* renamed from: V1 */
    private static final ASN1Integer f596V1 = new ASN1Integer(0);
    Extensions requestExtensions;
    ASN1Sequence requestList;
    GeneralName requestorName;
    ASN1Integer version;
    boolean versionSet;

    public TBSRequest(GeneralName requestorName, ASN1Sequence requestList, X509Extensions requestExtensions) {
        this.version = f596V1;
        this.requestorName = requestorName;
        this.requestList = requestList;
        this.requestExtensions = Extensions.getInstance(requestExtensions);
    }

    public TBSRequest(GeneralName requestorName, ASN1Sequence requestList, Extensions requestExtensions) {
        this.version = f596V1;
        this.requestorName = requestorName;
        this.requestList = requestList;
        this.requestExtensions = requestExtensions;
    }

    private TBSRequest(ASN1Sequence seq) {
        int index = 0;
        if (seq.getObjectAt(0) instanceof ASN1TaggedObject) {
            ASN1TaggedObject o = (ASN1TaggedObject) seq.getObjectAt(0);
            if (o.getTagNo() == 0) {
                this.versionSet = true;
                this.version = ASN1Integer.getInstance((ASN1TaggedObject) seq.getObjectAt(0), true);
                index = 0 + 1;
            } else {
                this.version = f596V1;
            }
        } else {
            this.version = f596V1;
        }
        if (seq.getObjectAt(index) instanceof ASN1TaggedObject) {
            this.requestorName = GeneralName.getInstance((ASN1TaggedObject) seq.getObjectAt(index), true);
            index++;
        }
        int index2 = index + 1;
        this.requestList = (ASN1Sequence) seq.getObjectAt(index);
        if (seq.size() == index2 + 1) {
            this.requestExtensions = Extensions.getInstance((ASN1TaggedObject) seq.getObjectAt(index2), true);
        }
    }

    public static TBSRequest getInstance(ASN1TaggedObject obj, boolean explicit) {
        return getInstance(ASN1Sequence.getInstance(obj, explicit));
    }

    public static TBSRequest getInstance(Object obj) {
        if (obj instanceof TBSRequest) {
            return (TBSRequest) obj;
        }
        if (obj != null) {
            return new TBSRequest(ASN1Sequence.getInstance(obj));
        }
        return null;
    }

    public ASN1Integer getVersion() {
        return this.version;
    }

    public GeneralName getRequestorName() {
        return this.requestorName;
    }

    public ASN1Sequence getRequestList() {
        return this.requestList;
    }

    public Extensions getRequestExtensions() {
        return this.requestExtensions;
    }

    @Override // com.android.internal.org.bouncycastle.asn1.ASN1Object, com.android.internal.org.bouncycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector(4);
        if (!this.version.equals((ASN1Primitive) f596V1) || this.versionSet) {
            v.add(new DERTaggedObject(true, 0, this.version));
        }
        GeneralName generalName = this.requestorName;
        if (generalName != null) {
            v.add(new DERTaggedObject(true, 1, generalName));
        }
        v.add(this.requestList);
        Extensions extensions = this.requestExtensions;
        if (extensions != null) {
            v.add(new DERTaggedObject(true, 2, extensions));
        }
        return new DERSequence(v);
    }
}
