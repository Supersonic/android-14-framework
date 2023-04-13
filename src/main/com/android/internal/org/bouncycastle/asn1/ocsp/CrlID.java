package com.android.internal.org.bouncycastle.asn1.ocsp;

import com.android.internal.org.bouncycastle.asn1.ASN1EncodableVector;
import com.android.internal.org.bouncycastle.asn1.ASN1GeneralizedTime;
import com.android.internal.org.bouncycastle.asn1.ASN1Integer;
import com.android.internal.org.bouncycastle.asn1.ASN1Object;
import com.android.internal.org.bouncycastle.asn1.ASN1Primitive;
import com.android.internal.org.bouncycastle.asn1.ASN1Sequence;
import com.android.internal.org.bouncycastle.asn1.ASN1TaggedObject;
import com.android.internal.org.bouncycastle.asn1.DERIA5String;
import com.android.internal.org.bouncycastle.asn1.DERSequence;
import com.android.internal.org.bouncycastle.asn1.DERTaggedObject;
import java.util.Enumeration;
/* loaded from: classes4.dex */
public class CrlID extends ASN1Object {
    private ASN1Integer crlNum;
    private ASN1GeneralizedTime crlTime;
    private DERIA5String crlUrl;

    private CrlID(ASN1Sequence seq) {
        Enumeration e = seq.getObjects();
        while (e.hasMoreElements()) {
            ASN1TaggedObject o = (ASN1TaggedObject) e.nextElement();
            switch (o.getTagNo()) {
                case 0:
                    this.crlUrl = DERIA5String.getInstance(o, true);
                    break;
                case 1:
                    this.crlNum = ASN1Integer.getInstance(o, true);
                    break;
                case 2:
                    this.crlTime = ASN1GeneralizedTime.getInstance(o, true);
                    break;
                default:
                    throw new IllegalArgumentException("unknown tag number: " + o.getTagNo());
            }
        }
    }

    public static CrlID getInstance(Object obj) {
        if (obj instanceof CrlID) {
            return (CrlID) obj;
        }
        if (obj != null) {
            return new CrlID(ASN1Sequence.getInstance(obj));
        }
        return null;
    }

    public DERIA5String getCrlUrl() {
        return this.crlUrl;
    }

    public ASN1Integer getCrlNum() {
        return this.crlNum;
    }

    public ASN1GeneralizedTime getCrlTime() {
        return this.crlTime;
    }

    @Override // com.android.internal.org.bouncycastle.asn1.ASN1Object, com.android.internal.org.bouncycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector(3);
        DERIA5String dERIA5String = this.crlUrl;
        if (dERIA5String != null) {
            v.add(new DERTaggedObject(true, 0, dERIA5String));
        }
        ASN1Integer aSN1Integer = this.crlNum;
        if (aSN1Integer != null) {
            v.add(new DERTaggedObject(true, 1, aSN1Integer));
        }
        ASN1GeneralizedTime aSN1GeneralizedTime = this.crlTime;
        if (aSN1GeneralizedTime != null) {
            v.add(new DERTaggedObject(true, 2, aSN1GeneralizedTime));
        }
        return new DERSequence(v);
    }
}
