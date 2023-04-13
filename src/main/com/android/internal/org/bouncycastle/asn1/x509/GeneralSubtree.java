package com.android.internal.org.bouncycastle.asn1.x509;

import com.android.internal.org.bouncycastle.asn1.ASN1EncodableVector;
import com.android.internal.org.bouncycastle.asn1.ASN1Integer;
import com.android.internal.org.bouncycastle.asn1.ASN1Object;
import com.android.internal.org.bouncycastle.asn1.ASN1Primitive;
import com.android.internal.org.bouncycastle.asn1.ASN1Sequence;
import com.android.internal.org.bouncycastle.asn1.ASN1TaggedObject;
import com.android.internal.org.bouncycastle.asn1.DERSequence;
import com.android.internal.org.bouncycastle.asn1.DERTaggedObject;
import java.math.BigInteger;
/* loaded from: classes4.dex */
public class GeneralSubtree extends ASN1Object {
    private static final BigInteger ZERO = BigInteger.valueOf(0);
    private GeneralName base;
    private ASN1Integer maximum;
    private ASN1Integer minimum;

    private GeneralSubtree(ASN1Sequence seq) {
        this.base = GeneralName.getInstance(seq.getObjectAt(0));
        switch (seq.size()) {
            case 1:
                return;
            case 2:
                ASN1TaggedObject o = ASN1TaggedObject.getInstance(seq.getObjectAt(1));
                switch (o.getTagNo()) {
                    case 0:
                        this.minimum = ASN1Integer.getInstance(o, false);
                        return;
                    case 1:
                        this.maximum = ASN1Integer.getInstance(o, false);
                        return;
                    default:
                        throw new IllegalArgumentException("Bad tag number: " + o.getTagNo());
                }
            case 3:
                ASN1TaggedObject oMin = ASN1TaggedObject.getInstance(seq.getObjectAt(1));
                if (oMin.getTagNo() != 0) {
                    throw new IllegalArgumentException("Bad tag number for 'minimum': " + oMin.getTagNo());
                }
                this.minimum = ASN1Integer.getInstance(oMin, false);
                ASN1TaggedObject oMax = ASN1TaggedObject.getInstance(seq.getObjectAt(2));
                if (oMax.getTagNo() != 1) {
                    throw new IllegalArgumentException("Bad tag number for 'maximum': " + oMax.getTagNo());
                }
                this.maximum = ASN1Integer.getInstance(oMax, false);
                return;
            default:
                throw new IllegalArgumentException("Bad sequence size: " + seq.size());
        }
    }

    public GeneralSubtree(GeneralName base, BigInteger minimum, BigInteger maximum) {
        this.base = base;
        if (maximum != null) {
            this.maximum = new ASN1Integer(maximum);
        }
        if (minimum == null) {
            this.minimum = null;
        } else {
            this.minimum = new ASN1Integer(minimum);
        }
    }

    public GeneralSubtree(GeneralName base) {
        this(base, null, null);
    }

    public static GeneralSubtree getInstance(ASN1TaggedObject o, boolean explicit) {
        return new GeneralSubtree(ASN1Sequence.getInstance(o, explicit));
    }

    public static GeneralSubtree getInstance(Object obj) {
        if (obj == null) {
            return null;
        }
        if (obj instanceof GeneralSubtree) {
            return (GeneralSubtree) obj;
        }
        return new GeneralSubtree(ASN1Sequence.getInstance(obj));
    }

    public GeneralName getBase() {
        return this.base;
    }

    public BigInteger getMinimum() {
        ASN1Integer aSN1Integer = this.minimum;
        if (aSN1Integer == null) {
            return ZERO;
        }
        return aSN1Integer.getValue();
    }

    public BigInteger getMaximum() {
        ASN1Integer aSN1Integer = this.maximum;
        if (aSN1Integer == null) {
            return null;
        }
        return aSN1Integer.getValue();
    }

    @Override // com.android.internal.org.bouncycastle.asn1.ASN1Object, com.android.internal.org.bouncycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector(3);
        v.add(this.base);
        ASN1Integer aSN1Integer = this.minimum;
        if (aSN1Integer != null && !aSN1Integer.hasValue(ZERO)) {
            v.add(new DERTaggedObject(false, 0, this.minimum));
        }
        ASN1Integer aSN1Integer2 = this.maximum;
        if (aSN1Integer2 != null) {
            v.add(new DERTaggedObject(false, 1, aSN1Integer2));
        }
        return new DERSequence(v);
    }
}
