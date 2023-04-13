package com.android.internal.org.bouncycastle.its.asn1;

import com.android.internal.org.bouncycastle.asn1.ASN1Enumerated;
import com.android.internal.org.bouncycastle.asn1.ASN1Object;
import com.android.internal.org.bouncycastle.asn1.ASN1Primitive;
/* loaded from: classes4.dex */
public class SymmAlgorithm extends ASN1Object {
    public static SymmAlgorithm aes128Ccm = new SymmAlgorithm(new ASN1Enumerated(0));
    private ASN1Enumerated symmAlgorithm;

    private SymmAlgorithm(ASN1Enumerated symmAlgorithm) {
        this.symmAlgorithm = symmAlgorithm;
    }

    public SymmAlgorithm(int ordinal) {
        this.symmAlgorithm = new ASN1Enumerated(ordinal);
    }

    public SymmAlgorithm getInstance(Object src) {
        if (src == null) {
            return null;
        }
        if (src instanceof SymmAlgorithm) {
            return (SymmAlgorithm) src;
        }
        return new SymmAlgorithm(ASN1Enumerated.getInstance(src));
    }

    public ASN1Enumerated getSymmAlgorithm() {
        return this.symmAlgorithm;
    }

    public void setSymmAlgorithm(ASN1Enumerated symmAlgorithm) {
        this.symmAlgorithm = symmAlgorithm;
    }

    @Override // com.android.internal.org.bouncycastle.asn1.ASN1Object, com.android.internal.org.bouncycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        return this.symmAlgorithm.toASN1Primitive();
    }
}
