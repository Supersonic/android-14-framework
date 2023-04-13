package com.android.internal.org.bouncycastle.its.asn1;

import com.android.internal.org.bouncycastle.asn1.ASN1Object;
import com.android.internal.org.bouncycastle.asn1.ASN1Primitive;
import com.android.internal.org.bouncycastle.asn1.ASN1Sequence;
/* loaded from: classes4.dex */
public class CircularRegion extends ASN1Object {
    private CircularRegion(ASN1Sequence seq) {
    }

    public static CircularRegion getInstance(Object o) {
        if (o instanceof CircularRegion) {
            return (CircularRegion) o;
        }
        if (o != null) {
            return new CircularRegion(ASN1Sequence.getInstance(o));
        }
        return null;
    }

    @Override // com.android.internal.org.bouncycastle.asn1.ASN1Object, com.android.internal.org.bouncycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        return null;
    }
}
