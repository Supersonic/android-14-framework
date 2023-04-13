package com.android.internal.org.bouncycastle.asn1.pkcs;

import com.android.internal.org.bouncycastle.asn1.ASN1EncodableVector;
import com.android.internal.org.bouncycastle.asn1.ASN1Integer;
import com.android.internal.org.bouncycastle.asn1.ASN1Object;
import com.android.internal.org.bouncycastle.asn1.ASN1Primitive;
import com.android.internal.org.bouncycastle.asn1.ASN1Sequence;
import com.android.internal.org.bouncycastle.asn1.DERSequence;
import java.math.BigInteger;
import java.util.Enumeration;
/* loaded from: classes4.dex */
public class DHParameter extends ASN1Object {

    /* renamed from: g */
    ASN1Integer f597g;

    /* renamed from: l */
    ASN1Integer f598l;

    /* renamed from: p */
    ASN1Integer f599p;

    public DHParameter(BigInteger p, BigInteger g, int l) {
        this.f599p = new ASN1Integer(p);
        this.f597g = new ASN1Integer(g);
        if (l != 0) {
            this.f598l = new ASN1Integer(l);
        } else {
            this.f598l = null;
        }
    }

    public static DHParameter getInstance(Object obj) {
        if (obj instanceof DHParameter) {
            return (DHParameter) obj;
        }
        if (obj != null) {
            return new DHParameter(ASN1Sequence.getInstance(obj));
        }
        return null;
    }

    private DHParameter(ASN1Sequence seq) {
        Enumeration e = seq.getObjects();
        this.f599p = ASN1Integer.getInstance(e.nextElement());
        this.f597g = ASN1Integer.getInstance(e.nextElement());
        if (e.hasMoreElements()) {
            this.f598l = (ASN1Integer) e.nextElement();
        } else {
            this.f598l = null;
        }
    }

    public BigInteger getP() {
        return this.f599p.getPositiveValue();
    }

    public BigInteger getG() {
        return this.f597g.getPositiveValue();
    }

    public BigInteger getL() {
        ASN1Integer aSN1Integer = this.f598l;
        if (aSN1Integer == null) {
            return null;
        }
        return aSN1Integer.getPositiveValue();
    }

    @Override // com.android.internal.org.bouncycastle.asn1.ASN1Object, com.android.internal.org.bouncycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector(3);
        v.add(this.f599p);
        v.add(this.f597g);
        if (getL() != null) {
            v.add(this.f598l);
        }
        return new DERSequence(v);
    }
}
