package com.android.internal.org.bouncycastle.asn1;

import java.math.BigInteger;
/* loaded from: classes4.dex */
public class DEREnumerated extends ASN1Enumerated {
    DEREnumerated(byte[] bytes) {
        super(bytes);
    }

    public DEREnumerated(BigInteger value) {
        super(value);
    }

    public DEREnumerated(int value) {
        super(value);
    }
}
