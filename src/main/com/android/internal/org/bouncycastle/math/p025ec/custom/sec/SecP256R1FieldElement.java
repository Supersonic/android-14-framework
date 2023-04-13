package com.android.internal.org.bouncycastle.math.p025ec.custom.sec;

import com.android.internal.org.bouncycastle.math.p025ec.ECFieldElement;
import com.android.internal.org.bouncycastle.math.raw.Nat256;
import com.android.internal.org.bouncycastle.util.Arrays;
import com.android.internal.org.bouncycastle.util.encoders.Hex;
import java.math.BigInteger;
/* renamed from: com.android.internal.org.bouncycastle.math.ec.custom.sec.SecP256R1FieldElement */
/* loaded from: classes4.dex */
public class SecP256R1FieldElement extends ECFieldElement.AbstractFp {

    /* renamed from: Q */
    public static final BigInteger f883Q = new BigInteger(1, Hex.decodeStrict("FFFFFFFF00000001000000000000000000000000FFFFFFFFFFFFFFFFFFFFFFFF"));

    /* renamed from: x */
    protected int[] f884x;

    public SecP256R1FieldElement(BigInteger x) {
        if (x == null || x.signum() < 0 || x.compareTo(f883Q) >= 0) {
            throw new IllegalArgumentException("x value invalid for SecP256R1FieldElement");
        }
        this.f884x = SecP256R1Field.fromBigInteger(x);
    }

    public SecP256R1FieldElement() {
        this.f884x = Nat256.create();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public SecP256R1FieldElement(int[] x) {
        this.f884x = x;
    }

    @Override // com.android.internal.org.bouncycastle.math.p025ec.ECFieldElement
    public boolean isZero() {
        return Nat256.isZero(this.f884x);
    }

    @Override // com.android.internal.org.bouncycastle.math.p025ec.ECFieldElement
    public boolean isOne() {
        return Nat256.isOne(this.f884x);
    }

    @Override // com.android.internal.org.bouncycastle.math.p025ec.ECFieldElement
    public boolean testBitZero() {
        return Nat256.getBit(this.f884x, 0) == 1;
    }

    @Override // com.android.internal.org.bouncycastle.math.p025ec.ECFieldElement
    public BigInteger toBigInteger() {
        return Nat256.toBigInteger(this.f884x);
    }

    @Override // com.android.internal.org.bouncycastle.math.p025ec.ECFieldElement
    public String getFieldName() {
        return "SecP256R1Field";
    }

    @Override // com.android.internal.org.bouncycastle.math.p025ec.ECFieldElement
    public int getFieldSize() {
        return f883Q.bitLength();
    }

    @Override // com.android.internal.org.bouncycastle.math.p025ec.ECFieldElement
    public ECFieldElement add(ECFieldElement b) {
        int[] z = Nat256.create();
        SecP256R1Field.add(this.f884x, ((SecP256R1FieldElement) b).f884x, z);
        return new SecP256R1FieldElement(z);
    }

    @Override // com.android.internal.org.bouncycastle.math.p025ec.ECFieldElement
    public ECFieldElement addOne() {
        int[] z = Nat256.create();
        SecP256R1Field.addOne(this.f884x, z);
        return new SecP256R1FieldElement(z);
    }

    @Override // com.android.internal.org.bouncycastle.math.p025ec.ECFieldElement
    public ECFieldElement subtract(ECFieldElement b) {
        int[] z = Nat256.create();
        SecP256R1Field.subtract(this.f884x, ((SecP256R1FieldElement) b).f884x, z);
        return new SecP256R1FieldElement(z);
    }

    @Override // com.android.internal.org.bouncycastle.math.p025ec.ECFieldElement
    public ECFieldElement multiply(ECFieldElement b) {
        int[] z = Nat256.create();
        SecP256R1Field.multiply(this.f884x, ((SecP256R1FieldElement) b).f884x, z);
        return new SecP256R1FieldElement(z);
    }

    @Override // com.android.internal.org.bouncycastle.math.p025ec.ECFieldElement
    public ECFieldElement divide(ECFieldElement b) {
        int[] z = Nat256.create();
        SecP256R1Field.inv(((SecP256R1FieldElement) b).f884x, z);
        SecP256R1Field.multiply(z, this.f884x, z);
        return new SecP256R1FieldElement(z);
    }

    @Override // com.android.internal.org.bouncycastle.math.p025ec.ECFieldElement
    public ECFieldElement negate() {
        int[] z = Nat256.create();
        SecP256R1Field.negate(this.f884x, z);
        return new SecP256R1FieldElement(z);
    }

    @Override // com.android.internal.org.bouncycastle.math.p025ec.ECFieldElement
    public ECFieldElement square() {
        int[] z = Nat256.create();
        SecP256R1Field.square(this.f884x, z);
        return new SecP256R1FieldElement(z);
    }

    @Override // com.android.internal.org.bouncycastle.math.p025ec.ECFieldElement
    public ECFieldElement invert() {
        int[] z = Nat256.create();
        SecP256R1Field.inv(this.f884x, z);
        return new SecP256R1FieldElement(z);
    }

    @Override // com.android.internal.org.bouncycastle.math.p025ec.ECFieldElement
    public ECFieldElement sqrt() {
        int[] x1 = this.f884x;
        if (Nat256.isZero(x1) || Nat256.isOne(x1)) {
            return this;
        }
        int[] t1 = Nat256.create();
        int[] t2 = Nat256.create();
        SecP256R1Field.square(x1, t1);
        SecP256R1Field.multiply(t1, x1, t1);
        SecP256R1Field.squareN(t1, 2, t2);
        SecP256R1Field.multiply(t2, t1, t2);
        SecP256R1Field.squareN(t2, 4, t1);
        SecP256R1Field.multiply(t1, t2, t1);
        SecP256R1Field.squareN(t1, 8, t2);
        SecP256R1Field.multiply(t2, t1, t2);
        SecP256R1Field.squareN(t2, 16, t1);
        SecP256R1Field.multiply(t1, t2, t1);
        SecP256R1Field.squareN(t1, 32, t1);
        SecP256R1Field.multiply(t1, x1, t1);
        SecP256R1Field.squareN(t1, 96, t1);
        SecP256R1Field.multiply(t1, x1, t1);
        SecP256R1Field.squareN(t1, 94, t1);
        SecP256R1Field.square(t1, t2);
        if (Nat256.m39eq(x1, t2)) {
            return new SecP256R1FieldElement(t1);
        }
        return null;
    }

    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof SecP256R1FieldElement)) {
            return false;
        }
        SecP256R1FieldElement o = (SecP256R1FieldElement) other;
        return Nat256.m39eq(this.f884x, o.f884x);
    }

    public int hashCode() {
        return f883Q.hashCode() ^ Arrays.hashCode(this.f884x, 0, 8);
    }
}
