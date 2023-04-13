package com.android.internal.org.bouncycastle.asn1.p018x9;

import com.android.internal.org.bouncycastle.asn1.ASN1EncodableVector;
import com.android.internal.org.bouncycastle.asn1.ASN1Integer;
import com.android.internal.org.bouncycastle.asn1.ASN1Object;
import com.android.internal.org.bouncycastle.asn1.ASN1ObjectIdentifier;
import com.android.internal.org.bouncycastle.asn1.ASN1OctetString;
import com.android.internal.org.bouncycastle.asn1.ASN1Primitive;
import com.android.internal.org.bouncycastle.asn1.ASN1Sequence;
import com.android.internal.org.bouncycastle.asn1.DERBitString;
import com.android.internal.org.bouncycastle.asn1.DERSequence;
import com.android.internal.org.bouncycastle.math.p025ec.ECAlgorithms;
import com.android.internal.org.bouncycastle.math.p025ec.ECCurve;
import com.android.internal.org.bouncycastle.util.Arrays;
import java.math.BigInteger;
/* renamed from: com.android.internal.org.bouncycastle.asn1.x9.X9Curve */
/* loaded from: classes4.dex */
public class X9Curve extends ASN1Object implements X9ObjectIdentifiers {
    private ECCurve curve;
    private ASN1ObjectIdentifier fieldIdentifier;
    private byte[] seed;

    public X9Curve(ECCurve curve) {
        this(curve, null);
    }

    public X9Curve(ECCurve curve, byte[] seed) {
        this.fieldIdentifier = null;
        this.curve = curve;
        this.seed = Arrays.clone(seed);
        setFieldIdentifier();
    }

    public X9Curve(X9FieldID fieldID, BigInteger order, BigInteger cofactor, ASN1Sequence seq) {
        int k1;
        int k2;
        int k3;
        this.fieldIdentifier = null;
        ASN1ObjectIdentifier identifier = fieldID.getIdentifier();
        this.fieldIdentifier = identifier;
        if (identifier.equals((ASN1Primitive) prime_field)) {
            BigInteger p = ((ASN1Integer) fieldID.getParameters()).getValue();
            BigInteger A = new BigInteger(1, ASN1OctetString.getInstance(seq.getObjectAt(0)).getOctets());
            BigInteger B = new BigInteger(1, ASN1OctetString.getInstance(seq.getObjectAt(1)).getOctets());
            this.curve = new ECCurve.C4297Fp(p, A, B, order, cofactor);
        } else if (this.fieldIdentifier.equals((ASN1Primitive) characteristic_two_field)) {
            ASN1Sequence parameters = ASN1Sequence.getInstance(fieldID.getParameters());
            int m = ((ASN1Integer) parameters.getObjectAt(0)).intValueExact();
            ASN1ObjectIdentifier representation = (ASN1ObjectIdentifier) parameters.getObjectAt(1);
            if (representation.equals((ASN1Primitive) tpBasis)) {
                int k12 = ASN1Integer.getInstance(parameters.getObjectAt(2)).intValueExact();
                k1 = k12;
                k2 = 0;
                k3 = 0;
            } else if (representation.equals((ASN1Primitive) ppBasis)) {
                ASN1Sequence pentanomial = ASN1Sequence.getInstance(parameters.getObjectAt(2));
                int k13 = ASN1Integer.getInstance(pentanomial.getObjectAt(0)).intValueExact();
                int k22 = ASN1Integer.getInstance(pentanomial.getObjectAt(1)).intValueExact();
                int k32 = ASN1Integer.getInstance(pentanomial.getObjectAt(2)).intValueExact();
                k1 = k13;
                k2 = k22;
                k3 = k32;
            } else {
                throw new IllegalArgumentException("This type of EC basis is not implemented");
            }
            BigInteger A2 = new BigInteger(1, ASN1OctetString.getInstance(seq.getObjectAt(0)).getOctets());
            BigInteger B2 = new BigInteger(1, ASN1OctetString.getInstance(seq.getObjectAt(1)).getOctets());
            this.curve = new ECCurve.F2m(m, k1, k2, k3, A2, B2, order, cofactor);
        } else {
            throw new IllegalArgumentException("This type of ECCurve is not implemented");
        }
        if (seq.size() == 3) {
            this.seed = ((DERBitString) seq.getObjectAt(2)).getBytes();
        }
    }

    private void setFieldIdentifier() {
        if (ECAlgorithms.isFpCurve(this.curve)) {
            this.fieldIdentifier = prime_field;
        } else if (ECAlgorithms.isF2mCurve(this.curve)) {
            this.fieldIdentifier = characteristic_two_field;
        } else {
            throw new IllegalArgumentException("This type of ECCurve is not implemented");
        }
    }

    public ECCurve getCurve() {
        return this.curve;
    }

    public byte[] getSeed() {
        return Arrays.clone(this.seed);
    }

    @Override // com.android.internal.org.bouncycastle.asn1.ASN1Object, com.android.internal.org.bouncycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector(3);
        if (this.fieldIdentifier.equals((ASN1Primitive) prime_field)) {
            v.add(new X9FieldElement(this.curve.getA()).toASN1Primitive());
            v.add(new X9FieldElement(this.curve.getB()).toASN1Primitive());
        } else if (this.fieldIdentifier.equals((ASN1Primitive) characteristic_two_field)) {
            v.add(new X9FieldElement(this.curve.getA()).toASN1Primitive());
            v.add(new X9FieldElement(this.curve.getB()).toASN1Primitive());
        }
        if (this.seed != null) {
            v.add(new DERBitString(this.seed));
        }
        return new DERSequence(v);
    }
}
