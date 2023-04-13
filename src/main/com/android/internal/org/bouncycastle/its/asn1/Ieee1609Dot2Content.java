package com.android.internal.org.bouncycastle.its.asn1;

import com.android.internal.org.bouncycastle.asn1.ASN1Choice;
import com.android.internal.org.bouncycastle.asn1.ASN1EncodableVector;
import com.android.internal.org.bouncycastle.asn1.ASN1Object;
import com.android.internal.org.bouncycastle.asn1.ASN1Primitive;
import com.android.internal.org.bouncycastle.asn1.ASN1Sequence;
import com.android.internal.org.bouncycastle.asn1.DERSequence;
/* loaded from: classes4.dex */
public class Ieee1609Dot2Content extends ASN1Object implements ASN1Choice {
    public static Ieee1609Dot2Content getInstance(Object src) {
        if (src instanceof Ieee1609Dot2Content) {
            return (Ieee1609Dot2Content) src;
        }
        if (src != null) {
            return getInstance(ASN1Sequence.getInstance(src));
        }
        return null;
    }

    @Override // com.android.internal.org.bouncycastle.asn1.ASN1Object, com.android.internal.org.bouncycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector();
        return new DERSequence(v);
    }
}
