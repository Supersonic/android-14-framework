package com.android.internal.org.bouncycastle.its.asn1;

import com.android.internal.org.bouncycastle.asn1.ASN1Enumerated;
import com.android.internal.org.bouncycastle.asn1.ASN1Primitive;
/* loaded from: classes4.dex */
public class CertificateType {
    public static final CertificateType Explicit = new CertificateType(0);
    public static final CertificateType Implicit = new CertificateType(1);
    private final ASN1Enumerated enumerated;

    protected CertificateType(int ordinal) {
        this.enumerated = new ASN1Enumerated(ordinal);
    }

    private CertificateType(ASN1Enumerated enumerated) {
        this.enumerated = enumerated;
    }

    public CertificateType getInstance(Object src) {
        if (src == null) {
            return null;
        }
        if (src instanceof CertificateType) {
            return (CertificateType) src;
        }
        return new CertificateType(ASN1Enumerated.getInstance(src));
    }

    public ASN1Primitive toASN1Primitive() {
        return this.enumerated;
    }
}
