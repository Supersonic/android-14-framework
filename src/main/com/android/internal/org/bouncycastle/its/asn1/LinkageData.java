package com.android.internal.org.bouncycastle.its.asn1;

import com.android.internal.org.bouncycastle.asn1.ASN1EncodableVector;
import com.android.internal.org.bouncycastle.asn1.ASN1Object;
import com.android.internal.org.bouncycastle.asn1.ASN1Primitive;
import com.android.internal.org.bouncycastle.asn1.ASN1Sequence;
import com.android.internal.org.bouncycastle.asn1.DERSequence;
/* loaded from: classes4.dex */
public class LinkageData extends ASN1Object {
    private final GroupLinkageValue groupLinkageValue;
    private final IValue iCert;
    private final LinkageValue linkageValue;

    private LinkageData(ASN1Sequence seq) {
        if (seq.size() != 2 && seq.size() != 3) {
            throw new IllegalArgumentException("sequence must be size 2 or 3");
        }
        this.iCert = IValue.getInstance(seq.getObjectAt(2));
        this.linkageValue = LinkageValue.getInstance(seq.getObjectAt(2));
        this.groupLinkageValue = GroupLinkageValue.getInstance(seq.getObjectAt(2));
    }

    public static LinkageData getInstance(Object src) {
        if (src instanceof LinkageData) {
            return (LinkageData) src;
        }
        if (src != null) {
            return new LinkageData(ASN1Sequence.getInstance(src));
        }
        return null;
    }

    @Override // com.android.internal.org.bouncycastle.asn1.ASN1Object, com.android.internal.org.bouncycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector();
        return new DERSequence(v);
    }
}
