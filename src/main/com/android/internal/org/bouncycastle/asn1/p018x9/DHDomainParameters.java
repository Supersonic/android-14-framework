package com.android.internal.org.bouncycastle.asn1.p018x9;

import com.android.internal.org.bouncycastle.asn1.ASN1Encodable;
import com.android.internal.org.bouncycastle.asn1.ASN1EncodableVector;
import com.android.internal.org.bouncycastle.asn1.ASN1Integer;
import com.android.internal.org.bouncycastle.asn1.ASN1Object;
import com.android.internal.org.bouncycastle.asn1.ASN1Primitive;
import com.android.internal.org.bouncycastle.asn1.ASN1Sequence;
import com.android.internal.org.bouncycastle.asn1.ASN1TaggedObject;
import com.android.internal.org.bouncycastle.asn1.DERSequence;
import java.math.BigInteger;
import java.util.Enumeration;
/* renamed from: com.android.internal.org.bouncycastle.asn1.x9.DHDomainParameters */
/* loaded from: classes4.dex */
public class DHDomainParameters extends ASN1Object {

    /* renamed from: g */
    private ASN1Integer f635g;

    /* renamed from: j */
    private ASN1Integer f636j;

    /* renamed from: p */
    private ASN1Integer f637p;

    /* renamed from: q */
    private ASN1Integer f638q;
    private DHValidationParms validationParms;

    public static DHDomainParameters getInstance(ASN1TaggedObject obj, boolean explicit) {
        return getInstance(ASN1Sequence.getInstance(obj, explicit));
    }

    public static DHDomainParameters getInstance(Object obj) {
        if (obj == null || (obj instanceof DHDomainParameters)) {
            return (DHDomainParameters) obj;
        }
        if (obj instanceof ASN1Sequence) {
            return new DHDomainParameters((ASN1Sequence) obj);
        }
        throw new IllegalArgumentException("Invalid DHDomainParameters: " + obj.getClass().getName());
    }

    public DHDomainParameters(BigInteger p, BigInteger g, BigInteger q, BigInteger j, DHValidationParms validationParms) {
        if (p == null) {
            throw new IllegalArgumentException("'p' cannot be null");
        }
        if (g == null) {
            throw new IllegalArgumentException("'g' cannot be null");
        }
        if (q == null) {
            throw new IllegalArgumentException("'q' cannot be null");
        }
        this.f637p = new ASN1Integer(p);
        this.f635g = new ASN1Integer(g);
        this.f638q = new ASN1Integer(q);
        this.f636j = new ASN1Integer(j);
        this.validationParms = validationParms;
    }

    public DHDomainParameters(ASN1Integer p, ASN1Integer g, ASN1Integer q, ASN1Integer j, DHValidationParms validationParms) {
        if (p == null) {
            throw new IllegalArgumentException("'p' cannot be null");
        }
        if (g == null) {
            throw new IllegalArgumentException("'g' cannot be null");
        }
        if (q == null) {
            throw new IllegalArgumentException("'q' cannot be null");
        }
        this.f637p = p;
        this.f635g = g;
        this.f638q = q;
        this.f636j = j;
        this.validationParms = validationParms;
    }

    private DHDomainParameters(ASN1Sequence seq) {
        if (seq.size() < 3 || seq.size() > 5) {
            throw new IllegalArgumentException("Bad sequence size: " + seq.size());
        }
        Enumeration e = seq.getObjects();
        this.f637p = ASN1Integer.getInstance(e.nextElement());
        this.f635g = ASN1Integer.getInstance(e.nextElement());
        this.f638q = ASN1Integer.getInstance(e.nextElement());
        ASN1Encodable next = getNext(e);
        if (next != null && (next instanceof ASN1Integer)) {
            this.f636j = ASN1Integer.getInstance(next);
            next = getNext(e);
        }
        if (next != null) {
            this.validationParms = DHValidationParms.getInstance(next.toASN1Primitive());
        }
    }

    private static ASN1Encodable getNext(Enumeration e) {
        if (e.hasMoreElements()) {
            return (ASN1Encodable) e.nextElement();
        }
        return null;
    }

    public ASN1Integer getP() {
        return this.f637p;
    }

    public ASN1Integer getG() {
        return this.f635g;
    }

    public ASN1Integer getQ() {
        return this.f638q;
    }

    public ASN1Integer getJ() {
        return this.f636j;
    }

    public DHValidationParms getValidationParms() {
        return this.validationParms;
    }

    @Override // com.android.internal.org.bouncycastle.asn1.ASN1Object, com.android.internal.org.bouncycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector(5);
        v.add(this.f637p);
        v.add(this.f635g);
        v.add(this.f638q);
        ASN1Integer aSN1Integer = this.f636j;
        if (aSN1Integer != null) {
            v.add(aSN1Integer);
        }
        DHValidationParms dHValidationParms = this.validationParms;
        if (dHValidationParms != null) {
            v.add(dHValidationParms);
        }
        return new DERSequence(v);
    }
}
