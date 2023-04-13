package com.android.internal.org.bouncycastle.asn1;

import java.math.BigInteger;
/* loaded from: classes4.dex */
public class DERInteger extends ASN1Integer {
    public DERInteger(byte[] bytes) {
        super(bytes, true);
    }

    public DERInteger(BigInteger value) {
        super(value);
    }

    public DERInteger(long value) {
        super(value);
    }
}
