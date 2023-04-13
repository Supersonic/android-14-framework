package com.android.internal.org.bouncycastle.asn1.ocsp;

import com.android.internal.org.bouncycastle.asn1.ASN1EncodableVector;
import com.android.internal.org.bouncycastle.asn1.ASN1GeneralizedTime;
import com.android.internal.org.bouncycastle.asn1.ASN1Integer;
import com.android.internal.org.bouncycastle.asn1.ASN1Object;
import com.android.internal.org.bouncycastle.asn1.ASN1Primitive;
import com.android.internal.org.bouncycastle.asn1.ASN1Sequence;
import com.android.internal.org.bouncycastle.asn1.ASN1TaggedObject;
import com.android.internal.org.bouncycastle.asn1.DERSequence;
import com.android.internal.org.bouncycastle.asn1.DERTaggedObject;
import com.android.internal.org.bouncycastle.asn1.x509.Extensions;
import com.android.internal.org.bouncycastle.asn1.x509.X509Extensions;
/* loaded from: classes4.dex */
public class ResponseData extends ASN1Object {

    /* renamed from: V1 */
    private static final ASN1Integer f595V1 = new ASN1Integer(0);
    private ASN1GeneralizedTime producedAt;
    private ResponderID responderID;
    private Extensions responseExtensions;
    private ASN1Sequence responses;
    private ASN1Integer version;
    private boolean versionPresent;

    public ResponseData(ASN1Integer version, ResponderID responderID, ASN1GeneralizedTime producedAt, ASN1Sequence responses, Extensions responseExtensions) {
        this.version = version;
        this.responderID = responderID;
        this.producedAt = producedAt;
        this.responses = responses;
        this.responseExtensions = responseExtensions;
    }

    public ResponseData(ResponderID responderID, ASN1GeneralizedTime producedAt, ASN1Sequence responses, X509Extensions responseExtensions) {
        this(f595V1, responderID, ASN1GeneralizedTime.getInstance(producedAt), responses, Extensions.getInstance(responseExtensions));
    }

    public ResponseData(ResponderID responderID, ASN1GeneralizedTime producedAt, ASN1Sequence responses, Extensions responseExtensions) {
        this(f595V1, responderID, producedAt, responses, responseExtensions);
    }

    private ResponseData(ASN1Sequence seq) {
        int index = 0;
        if (seq.getObjectAt(0) instanceof ASN1TaggedObject) {
            ASN1TaggedObject o = (ASN1TaggedObject) seq.getObjectAt(0);
            if (o.getTagNo() == 0) {
                this.versionPresent = true;
                this.version = ASN1Integer.getInstance((ASN1TaggedObject) seq.getObjectAt(0), true);
                index = 0 + 1;
            } else {
                this.version = f595V1;
            }
        } else {
            this.version = f595V1;
        }
        int index2 = index + 1;
        this.responderID = ResponderID.getInstance(seq.getObjectAt(index));
        int index3 = index2 + 1;
        this.producedAt = ASN1GeneralizedTime.getInstance(seq.getObjectAt(index2));
        int index4 = index3 + 1;
        this.responses = (ASN1Sequence) seq.getObjectAt(index3);
        if (seq.size() > index4) {
            this.responseExtensions = Extensions.getInstance((ASN1TaggedObject) seq.getObjectAt(index4), true);
        }
    }

    public static ResponseData getInstance(ASN1TaggedObject obj, boolean explicit) {
        return getInstance(ASN1Sequence.getInstance(obj, explicit));
    }

    public static ResponseData getInstance(Object obj) {
        if (obj instanceof ResponseData) {
            return (ResponseData) obj;
        }
        if (obj != null) {
            return new ResponseData(ASN1Sequence.getInstance(obj));
        }
        return null;
    }

    public ASN1Integer getVersion() {
        return this.version;
    }

    public ResponderID getResponderID() {
        return this.responderID;
    }

    public ASN1GeneralizedTime getProducedAt() {
        return this.producedAt;
    }

    public ASN1Sequence getResponses() {
        return this.responses;
    }

    public Extensions getResponseExtensions() {
        return this.responseExtensions;
    }

    @Override // com.android.internal.org.bouncycastle.asn1.ASN1Object, com.android.internal.org.bouncycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector(5);
        if (this.versionPresent || !this.version.equals((ASN1Primitive) f595V1)) {
            v.add(new DERTaggedObject(true, 0, this.version));
        }
        v.add(this.responderID);
        v.add(this.producedAt);
        v.add(this.responses);
        Extensions extensions = this.responseExtensions;
        if (extensions != null) {
            v.add(new DERTaggedObject(true, 1, extensions));
        }
        return new DERSequence(v);
    }
}
