package com.android.internal.org.bouncycastle.its.asn1;

import com.android.internal.org.bouncycastle.asn1.ASN1EncodableVector;
import com.android.internal.org.bouncycastle.asn1.ASN1Object;
import com.android.internal.org.bouncycastle.asn1.ASN1OctetString;
import com.android.internal.org.bouncycastle.asn1.ASN1Primitive;
import com.android.internal.org.bouncycastle.asn1.ASN1Sequence;
import com.android.internal.org.bouncycastle.asn1.DEROctetString;
import com.android.internal.org.bouncycastle.asn1.DERSequence;
import com.android.internal.org.bouncycastle.util.Arrays;
/* loaded from: classes4.dex */
public class SequenceOfOctetString extends ASN1Object {
    private byte[][] octetStrings;

    private SequenceOfOctetString(ASN1Sequence seq) {
        this.octetStrings = toByteArrays(seq);
    }

    public static SequenceOfOctetString getInstance(Object o) {
        if (o instanceof SequenceOfOctetString) {
            return (SequenceOfOctetString) o;
        }
        if (o != null) {
            return new SequenceOfOctetString(ASN1Sequence.getInstance(o));
        }
        return null;
    }

    public int size() {
        return this.octetStrings.length;
    }

    @Override // com.android.internal.org.bouncycastle.asn1.ASN1Object, com.android.internal.org.bouncycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector();
        int i = 0;
        while (true) {
            byte[][] bArr = this.octetStrings;
            if (i != bArr.length) {
                v.add(new DEROctetString(Arrays.clone(bArr[i])));
                i++;
            } else {
                return new DERSequence(v);
            }
        }
    }

    static byte[][] toByteArrays(ASN1Sequence seq) {
        byte[][] octetStrings = new byte[seq.size()];
        for (int i = 0; i != seq.size(); i++) {
            octetStrings[i] = ASN1OctetString.getInstance(seq.getObjectAt(i)).getOctets();
        }
        return octetStrings;
    }
}
