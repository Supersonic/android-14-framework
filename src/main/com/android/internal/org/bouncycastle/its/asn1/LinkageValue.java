package com.android.internal.org.bouncycastle.its.asn1;

import com.android.internal.org.bouncycastle.asn1.ASN1Object;
import com.android.internal.org.bouncycastle.asn1.ASN1OctetString;
import com.android.internal.org.bouncycastle.asn1.ASN1Primitive;
import com.android.internal.org.bouncycastle.asn1.DEROctetString;
import com.android.internal.org.bouncycastle.util.Arrays;
/* loaded from: classes4.dex */
public class LinkageValue extends ASN1Object {
    private final byte[] value;

    private LinkageValue(ASN1OctetString octs) {
        this.value = Arrays.clone(Utils.octetStringFixed(octs.getOctets(), 9));
    }

    public static LinkageValue getInstance(Object src) {
        if (src instanceof LinkageValue) {
            return (LinkageValue) src;
        }
        if (src != null) {
            return new LinkageValue(ASN1OctetString.getInstance(src));
        }
        return null;
    }

    @Override // com.android.internal.org.bouncycastle.asn1.ASN1Object, com.android.internal.org.bouncycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        return new DEROctetString(Arrays.clone(this.value));
    }
}
