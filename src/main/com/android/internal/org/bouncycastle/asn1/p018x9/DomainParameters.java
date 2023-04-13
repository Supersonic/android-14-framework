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
/* renamed from: com.android.internal.org.bouncycastle.asn1.x9.DomainParameters */
/* loaded from: classes4.dex */
public class DomainParameters extends ASN1Object {

    /* renamed from: g */
    private final ASN1Integer f640g;

    /* renamed from: j */
    private final ASN1Integer f641j;

    /* renamed from: p */
    private final ASN1Integer f642p;

    /* renamed from: q */
    private final ASN1Integer f643q;
    private final ValidationParams validationParams;

    public static DomainParameters getInstance(ASN1TaggedObject obj, boolean explicit) {
        return getInstance(ASN1Sequence.getInstance(obj, explicit));
    }

    public static DomainParameters getInstance(Object obj) {
        if (obj instanceof DomainParameters) {
            return (DomainParameters) obj;
        }
        if (obj != null) {
            return new DomainParameters(ASN1Sequence.getInstance(obj));
        }
        return null;
    }

    public DomainParameters(BigInteger p, BigInteger g, BigInteger q, BigInteger j, ValidationParams validationParams) {
        if (p == null) {
            throw new IllegalArgumentException("'p' cannot be null");
        }
        if (g == null) {
            throw new IllegalArgumentException("'g' cannot be null");
        }
        if (q == null) {
            throw new IllegalArgumentException("'q' cannot be null");
        }
        this.f642p = new ASN1Integer(p);
        this.f640g = new ASN1Integer(g);
        this.f643q = new ASN1Integer(q);
        if (j != null) {
            this.f641j = new ASN1Integer(j);
        } else {
            this.f641j = null;
        }
        this.validationParams = validationParams;
    }

    private DomainParameters(ASN1Sequence seq) {
        if (seq.size() < 3 || seq.size() > 5) {
            throw new IllegalArgumentException("Bad sequence size: " + seq.size());
        }
        Enumeration e = seq.getObjects();
        this.f642p = ASN1Integer.getInstance(e.nextElement());
        this.f640g = ASN1Integer.getInstance(e.nextElement());
        this.f643q = ASN1Integer.getInstance(e.nextElement());
        ASN1Encodable next = getNext(e);
        if (next != null && (next instanceof ASN1Integer)) {
            this.f641j = ASN1Integer.getInstance(next);
            next = getNext(e);
        } else {
            this.f641j = null;
        }
        if (next != null) {
            this.validationParams = ValidationParams.getInstance(next.toASN1Primitive());
        } else {
            this.validationParams = null;
        }
    }

    private static ASN1Encodable getNext(Enumeration e) {
        if (e.hasMoreElements()) {
            return (ASN1Encodable) e.nextElement();
        }
        return null;
    }

    public BigInteger getP() {
        return this.f642p.getPositiveValue();
    }

    public BigInteger getG() {
        return this.f640g.getPositiveValue();
    }

    public BigInteger getQ() {
        return this.f643q.getPositiveValue();
    }

    public BigInteger getJ() {
        ASN1Integer aSN1Integer = this.f641j;
        if (aSN1Integer == null) {
            return null;
        }
        return aSN1Integer.getPositiveValue();
    }

    public ValidationParams getValidationParams() {
        return this.validationParams;
    }

    @Override // com.android.internal.org.bouncycastle.asn1.ASN1Object, com.android.internal.org.bouncycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector(5);
        v.add(this.f642p);
        v.add(this.f640g);
        v.add(this.f643q);
        ASN1Integer aSN1Integer = this.f641j;
        if (aSN1Integer != null) {
            v.add(aSN1Integer);
        }
        ValidationParams validationParams = this.validationParams;
        if (validationParams != null) {
            v.add(validationParams);
        }
        return new DERSequence(v);
    }
}
