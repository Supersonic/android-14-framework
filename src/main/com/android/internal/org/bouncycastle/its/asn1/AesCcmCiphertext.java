package com.android.internal.org.bouncycastle.its.asn1;

import com.android.internal.org.bouncycastle.asn1.ASN1EncodableVector;
import com.android.internal.org.bouncycastle.asn1.ASN1Object;
import com.android.internal.org.bouncycastle.asn1.ASN1OctetString;
import com.android.internal.org.bouncycastle.asn1.ASN1Primitive;
import com.android.internal.org.bouncycastle.asn1.ASN1Sequence;
import com.android.internal.org.bouncycastle.asn1.DEROctetString;
import com.android.internal.org.bouncycastle.asn1.DERSequence;
/* loaded from: classes4.dex */
public class AesCcmCiphertext extends ASN1Object {
    private final byte[] nonce;
    private final SequenceOfOctetString opaque;

    private AesCcmCiphertext(ASN1Sequence seq) {
        if (seq.size() != 2) {
            throw new IllegalArgumentException("sequence not length 2");
        }
        this.nonce = Utils.octetStringFixed(ASN1OctetString.getInstance(seq.getObjectAt(0)).getOctets(), 12);
        this.opaque = SequenceOfOctetString.getInstance(seq.getObjectAt(1));
    }

    public static AesCcmCiphertext getInstance(Object o) {
        if (o instanceof AesCcmCiphertext) {
            return (AesCcmCiphertext) o;
        }
        if (o != null) {
            return new AesCcmCiphertext(ASN1Sequence.getInstance(o));
        }
        return null;
    }

    @Override // com.android.internal.org.bouncycastle.asn1.ASN1Object, com.android.internal.org.bouncycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector();
        v.add(new DEROctetString(this.nonce));
        v.add(this.opaque);
        return new DERSequence(v);
    }
}
