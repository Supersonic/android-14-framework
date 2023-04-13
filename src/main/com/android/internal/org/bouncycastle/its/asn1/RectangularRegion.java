package com.android.internal.org.bouncycastle.its.asn1;

import com.android.internal.org.bouncycastle.asn1.ASN1Object;
import com.android.internal.org.bouncycastle.asn1.ASN1Primitive;
import com.android.internal.org.bouncycastle.asn1.ASN1Sequence;
/* loaded from: classes4.dex */
public class RectangularRegion extends ASN1Object {
    private RectangularRegion(ASN1Sequence seq) {
    }

    public static RectangularRegion getInstance(Object o) {
        if (o instanceof RectangularRegion) {
            return (RectangularRegion) o;
        }
        if (o != null) {
            return new RectangularRegion(ASN1Sequence.getInstance(o));
        }
        return null;
    }

    @Override // com.android.internal.org.bouncycastle.asn1.ASN1Object, com.android.internal.org.bouncycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        return null;
    }
}
