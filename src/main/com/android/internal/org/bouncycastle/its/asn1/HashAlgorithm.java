package com.android.internal.org.bouncycastle.its.asn1;

import com.android.internal.org.bouncycastle.asn1.ASN1Enumerated;
import com.android.internal.org.bouncycastle.asn1.ASN1Primitive;
/* loaded from: classes4.dex */
public class HashAlgorithm {
    public static final HashAlgorithm sha256 = new HashAlgorithm(0);
    public static final HashAlgorithm sha384 = new HashAlgorithm(1);
    private final ASN1Enumerated enumerated;

    protected HashAlgorithm(int ordinal) {
        this.enumerated = new ASN1Enumerated(ordinal);
    }

    private HashAlgorithm(ASN1Enumerated enumerated) {
        this.enumerated = enumerated;
    }

    public HashAlgorithm getInstance(Object src) {
        if (src == null) {
            return null;
        }
        if (src instanceof HashAlgorithm) {
            return (HashAlgorithm) src;
        }
        return new HashAlgorithm(ASN1Enumerated.getInstance(src));
    }

    public ASN1Primitive toASN1Primitive() {
        return this.enumerated;
    }
}
