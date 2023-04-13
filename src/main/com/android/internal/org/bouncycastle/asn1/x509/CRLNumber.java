package com.android.internal.org.bouncycastle.asn1.x509;

import com.android.internal.org.bouncycastle.asn1.ASN1Integer;
import com.android.internal.org.bouncycastle.asn1.ASN1Object;
import com.android.internal.org.bouncycastle.asn1.ASN1Primitive;
import java.math.BigInteger;
/* loaded from: classes4.dex */
public class CRLNumber extends ASN1Object {
    private BigInteger number;

    public CRLNumber(BigInteger number) {
        this.number = number;
    }

    public BigInteger getCRLNumber() {
        return this.number;
    }

    public String toString() {
        return "CRLNumber: " + getCRLNumber();
    }

    @Override // com.android.internal.org.bouncycastle.asn1.ASN1Object, com.android.internal.org.bouncycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        return new ASN1Integer(this.number);
    }

    public static CRLNumber getInstance(Object o) {
        if (o instanceof CRLNumber) {
            return (CRLNumber) o;
        }
        if (o != null) {
            return new CRLNumber(ASN1Integer.getInstance(o).getValue());
        }
        return null;
    }
}
