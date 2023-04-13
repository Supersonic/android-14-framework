package com.android.internal.org.bouncycastle.its.asn1;

import com.android.internal.org.bouncycastle.asn1.ASN1BitString;
import com.android.internal.org.bouncycastle.asn1.ASN1Object;
import com.android.internal.org.bouncycastle.asn1.ASN1Primitive;
import com.android.internal.org.bouncycastle.asn1.DERBitString;
/* loaded from: classes4.dex */
public class EndEntityType extends ASN1Object {
    public static final int app = 128;
    public static final int enrol = 64;
    private final ASN1BitString type;

    public EndEntityType(int type) {
        if (type != 128 && type != 64) {
            throw new IllegalArgumentException("value out of range");
        }
        this.type = new DERBitString(type);
    }

    private EndEntityType(DERBitString str) {
        this.type = str;
    }

    public static EndEntityType getInstance(Object src) {
        if (src instanceof EndEntityType) {
            return (EndEntityType) src;
        }
        if (src != null) {
            return new EndEntityType(DERBitString.getInstance(src));
        }
        return null;
    }

    @Override // com.android.internal.org.bouncycastle.asn1.ASN1Object, com.android.internal.org.bouncycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        return this.type;
    }
}
