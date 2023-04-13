package com.android.internal.org.bouncycastle.asn1.x509;

import com.android.internal.org.bouncycastle.asn1.ASN1EncodableVector;
import com.android.internal.org.bouncycastle.asn1.ASN1Integer;
import com.android.internal.org.bouncycastle.asn1.ASN1Object;
import com.android.internal.org.bouncycastle.asn1.ASN1Primitive;
import com.android.internal.org.bouncycastle.asn1.ASN1Sequence;
import com.android.internal.org.bouncycastle.asn1.ASN1TaggedObject;
import com.android.internal.org.bouncycastle.asn1.DERSequence;
import java.math.BigInteger;
import java.util.Enumeration;
/* loaded from: classes4.dex */
public class DSAParameter extends ASN1Object {

    /* renamed from: g */
    ASN1Integer f620g;

    /* renamed from: p */
    ASN1Integer f621p;

    /* renamed from: q */
    ASN1Integer f622q;

    public static DSAParameter getInstance(ASN1TaggedObject obj, boolean explicit) {
        return getInstance(ASN1Sequence.getInstance(obj, explicit));
    }

    public static DSAParameter getInstance(Object obj) {
        if (obj instanceof DSAParameter) {
            return (DSAParameter) obj;
        }
        if (obj != null) {
            return new DSAParameter(ASN1Sequence.getInstance(obj));
        }
        return null;
    }

    public DSAParameter(BigInteger p, BigInteger q, BigInteger g) {
        this.f621p = new ASN1Integer(p);
        this.f622q = new ASN1Integer(q);
        this.f620g = new ASN1Integer(g);
    }

    private DSAParameter(ASN1Sequence seq) {
        if (seq.size() != 3) {
            throw new IllegalArgumentException("Bad sequence size: " + seq.size());
        }
        Enumeration e = seq.getObjects();
        this.f621p = ASN1Integer.getInstance(e.nextElement());
        this.f622q = ASN1Integer.getInstance(e.nextElement());
        this.f620g = ASN1Integer.getInstance(e.nextElement());
    }

    public BigInteger getP() {
        return this.f621p.getPositiveValue();
    }

    public BigInteger getQ() {
        return this.f622q.getPositiveValue();
    }

    public BigInteger getG() {
        return this.f620g.getPositiveValue();
    }

    @Override // com.android.internal.org.bouncycastle.asn1.ASN1Object, com.android.internal.org.bouncycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector(3);
        v.add(this.f621p);
        v.add(this.f622q);
        v.add(this.f620g);
        return new DERSequence(v);
    }
}
