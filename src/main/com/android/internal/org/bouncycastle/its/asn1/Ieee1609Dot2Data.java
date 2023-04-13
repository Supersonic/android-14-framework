package com.android.internal.org.bouncycastle.its.asn1;

import com.android.internal.org.bouncycastle.asn1.ASN1EncodableVector;
import com.android.internal.org.bouncycastle.asn1.ASN1Integer;
import com.android.internal.org.bouncycastle.asn1.ASN1Object;
import com.android.internal.org.bouncycastle.asn1.ASN1Primitive;
import com.android.internal.org.bouncycastle.asn1.ASN1Sequence;
import com.android.internal.org.bouncycastle.asn1.DERSequence;
import java.math.BigInteger;
/* loaded from: classes4.dex */
public class Ieee1609Dot2Data extends ASN1Object {
    private final Ieee1609Dot2Content content;
    private final BigInteger protcolVersion;

    private Ieee1609Dot2Data(ASN1Sequence seq) {
        if (seq.size() != 2) {
            throw new IllegalArgumentException("sequence not length 2");
        }
        this.protcolVersion = ASN1Integer.getInstance(seq.getObjectAt(0)).getValue();
        this.content = Ieee1609Dot2Content.getInstance(seq.getObjectAt(1));
    }

    public static Ieee1609Dot2Data getInstance(Object src) {
        if (src instanceof Ieee1609Dot2Data) {
            return (Ieee1609Dot2Data) src;
        }
        if (src != null) {
            return new Ieee1609Dot2Data(ASN1Sequence.getInstance(src));
        }
        return null;
    }

    @Override // com.android.internal.org.bouncycastle.asn1.ASN1Object, com.android.internal.org.bouncycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector();
        return new DERSequence(v);
    }
}
