package com.android.internal.org.bouncycastle.its.asn1;

import com.android.internal.org.bouncycastle.asn1.ASN1Object;
import com.android.internal.org.bouncycastle.asn1.ASN1Primitive;
import com.android.internal.org.bouncycastle.asn1.ASN1Sequence;
import com.android.internal.org.bouncycastle.asn1.DERSequence;
/* loaded from: classes4.dex */
public class SequenceOfRectangularRegion extends ASN1Object {
    private final RectangularRegion[] sequence;

    private SequenceOfRectangularRegion(ASN1Sequence seq) {
        this.sequence = new RectangularRegion[seq.size()];
        for (int i = 0; i != seq.size(); i++) {
            this.sequence[i] = RectangularRegion.getInstance(seq.getObjectAt(i));
        }
    }

    @Override // com.android.internal.org.bouncycastle.asn1.ASN1Object, com.android.internal.org.bouncycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        return new DERSequence(this.sequence);
    }
}
