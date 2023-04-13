package com.android.internal.org.bouncycastle.its.asn1;

import com.android.internal.org.bouncycastle.asn1.ASN1Integer;
import com.android.internal.org.bouncycastle.asn1.ASN1Object;
import com.android.internal.org.bouncycastle.asn1.ASN1Primitive;
import com.android.internal.org.bouncycastle.util.BigIntegers;
import java.math.BigInteger;
/* loaded from: classes4.dex */
public class IValue extends ASN1Object {
    private final BigInteger value;

    private IValue(ASN1Integer value) {
        int i = BigIntegers.intValueExact(value.getValue());
        if (i < 0 || i > 65535) {
            throw new IllegalArgumentException("value out of range");
        }
        this.value = value.getValue();
    }

    public static IValue getInstance(Object src) {
        if (src instanceof IValue) {
            return (IValue) src;
        }
        if (src != null) {
            return new IValue(ASN1Integer.getInstance(src));
        }
        return null;
    }

    @Override // com.android.internal.org.bouncycastle.asn1.ASN1Object, com.android.internal.org.bouncycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        return new ASN1Integer(this.value);
    }
}
