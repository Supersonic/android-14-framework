package com.android.internal.org.bouncycastle.its.asn1;

import com.android.internal.org.bouncycastle.asn1.ASN1EncodableVector;
import com.android.internal.org.bouncycastle.asn1.ASN1Object;
import com.android.internal.org.bouncycastle.asn1.ASN1OctetString;
import com.android.internal.org.bouncycastle.asn1.ASN1Primitive;
import com.android.internal.org.bouncycastle.asn1.ASN1Sequence;
import com.android.internal.org.bouncycastle.asn1.DEROctetString;
import com.android.internal.org.bouncycastle.asn1.DERSequence;
/* loaded from: classes4.dex */
public class GroupLinkageValue extends ASN1Object {
    private byte[] jValue;
    private byte[] value;

    private GroupLinkageValue(ASN1Sequence seq) {
        if (seq.size() != 2) {
            throw new IllegalArgumentException("sequence not length 2");
        }
        this.jValue = Utils.octetStringFixed(ASN1OctetString.getInstance(seq.getObjectAt(0)).getOctets(), 4);
        this.value = Utils.octetStringFixed(ASN1OctetString.getInstance(seq.getObjectAt(1)).getOctets(), 9);
    }

    public static GroupLinkageValue getInstance(Object src) {
        if (src instanceof GroupLinkageValue) {
            return (GroupLinkageValue) src;
        }
        if (src != null) {
            return new GroupLinkageValue(ASN1Sequence.getInstance(src));
        }
        return null;
    }

    public byte[] getJValue() {
        return this.jValue;
    }

    public byte[] getValue() {
        return this.value;
    }

    @Override // com.android.internal.org.bouncycastle.asn1.ASN1Object, com.android.internal.org.bouncycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector avec = new ASN1EncodableVector();
        avec.add(new DEROctetString(this.jValue));
        avec.add(new DEROctetString(this.value));
        return new DERSequence(avec);
    }
}
