package com.android.internal.org.bouncycastle.math.p025ec.custom.sec;

import com.android.internal.org.bouncycastle.math.p025ec.ECFieldElement;
import com.android.internal.org.bouncycastle.math.raw.Nat192;
import com.android.internal.org.bouncycastle.util.Arrays;
import com.android.internal.org.bouncycastle.util.encoders.Hex;
import java.math.BigInteger;
/* renamed from: com.android.internal.org.bouncycastle.math.ec.custom.sec.SecP192R1FieldElement */
/* loaded from: classes4.dex */
public class SecP192R1FieldElement extends ECFieldElement.AbstractFp {

    /* renamed from: Q */
    public static final BigInteger f861Q = new BigInteger(1, Hex.decodeStrict("FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFEFFFFFFFFFFFFFFFF"));

    /* renamed from: x */
    protected int[] f862x;

    public SecP192R1FieldElement(BigInteger x) {
        if (x == null || x.signum() < 0 || x.compareTo(f861Q) >= 0) {
            throw new IllegalArgumentException("x value invalid for SecP192R1FieldElement");
        }
        this.f862x = SecP192R1Field.fromBigInteger(x);
    }

    public SecP192R1FieldElement() {
        this.f862x = Nat192.create();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public SecP192R1FieldElement(int[] x) {
        this.f862x = x;
    }

    @Override // com.android.internal.org.bouncycastle.math.p025ec.ECFieldElement
    public boolean isZero() {
        return Nat192.isZero(this.f862x);
    }

    @Override // com.android.internal.org.bouncycastle.math.p025ec.ECFieldElement
    public boolean isOne() {
        return Nat192.isOne(this.f862x);
    }

    @Override // com.android.internal.org.bouncycastle.math.p025ec.ECFieldElement
    public boolean testBitZero() {
        return Nat192.getBit(this.f862x, 0) == 1;
    }

    @Override // com.android.internal.org.bouncycastle.math.p025ec.ECFieldElement
    public BigInteger toBigInteger() {
        return Nat192.toBigInteger(this.f862x);
    }

    @Override // com.android.internal.org.bouncycastle.math.p025ec.ECFieldElement
    public String getFieldName() {
        return "SecP192R1Field";
    }

    @Override // com.android.internal.org.bouncycastle.math.p025ec.ECFieldElement
    public int getFieldSize() {
        return f861Q.bitLength();
    }

    @Override // com.android.internal.org.bouncycastle.math.p025ec.ECFieldElement
    public ECFieldElement add(ECFieldElement b) {
        int[] z = Nat192.create();
        SecP192R1Field.add(this.f862x, ((SecP192R1FieldElement) b).f862x, z);
        return new SecP192R1FieldElement(z);
    }

    @Override // com.android.internal.org.bouncycastle.math.p025ec.ECFieldElement
    public ECFieldElement addOne() {
        int[] z = Nat192.create();
        SecP192R1Field.addOne(this.f862x, z);
        return new SecP192R1FieldElement(z);
    }

    @Override // com.android.internal.org.bouncycastle.math.p025ec.ECFieldElement
    public ECFieldElement subtract(ECFieldElement b) {
        int[] z = Nat192.create();
        SecP192R1Field.subtract(this.f862x, ((SecP192R1FieldElement) b).f862x, z);
        return new SecP192R1FieldElement(z);
    }

    @Override // com.android.internal.org.bouncycastle.math.p025ec.ECFieldElement
    public ECFieldElement multiply(ECFieldElement b) {
        int[] z = Nat192.create();
        SecP192R1Field.multiply(this.f862x, ((SecP192R1FieldElement) b).f862x, z);
        return new SecP192R1FieldElement(z);
    }

    @Override // com.android.internal.org.bouncycastle.math.p025ec.ECFieldElement
    public ECFieldElement divide(ECFieldElement b) {
        int[] z = Nat192.create();
        SecP192R1Field.inv(((SecP192R1FieldElement) b).f862x, z);
        SecP192R1Field.multiply(z, this.f862x, z);
        return new SecP192R1FieldElement(z);
    }

    @Override // com.android.internal.org.bouncycastle.math.p025ec.ECFieldElement
    public ECFieldElement negate() {
        int[] z = Nat192.create();
        SecP192R1Field.negate(this.f862x, z);
        return new SecP192R1FieldElement(z);
    }

    @Override // com.android.internal.org.bouncycastle.math.p025ec.ECFieldElement
    public ECFieldElement square() {
        int[] z = Nat192.create();
        SecP192R1Field.square(this.f862x, z);
        return new SecP192R1FieldElement(z);
    }

    @Override // com.android.internal.org.bouncycastle.math.p025ec.ECFieldElement
    public ECFieldElement invert() {
        int[] z = Nat192.create();
        SecP192R1Field.inv(this.f862x, z);
        return new SecP192R1FieldElement(z);
    }

    @Override // com.android.internal.org.bouncycastle.math.p025ec.ECFieldElement
    public ECFieldElement sqrt() {
        int[] x1 = this.f862x;
        if (Nat192.isZero(x1) || Nat192.isOne(x1)) {
            return this;
        }
        int[] t1 = Nat192.create();
        int[] t2 = Nat192.create();
        SecP192R1Field.square(x1, t1);
        SecP192R1Field.multiply(t1, x1, t1);
        SecP192R1Field.squareN(t1, 2, t2);
        SecP192R1Field.multiply(t2, t1, t2);
        SecP192R1Field.squareN(t2, 4, t1);
        SecP192R1Field.multiply(t1, t2, t1);
        SecP192R1Field.squareN(t1, 8, t2);
        SecP192R1Field.multiply(t2, t1, t2);
        SecP192R1Field.squareN(t2, 16, t1);
        SecP192R1Field.multiply(t1, t2, t1);
        SecP192R1Field.squareN(t1, 32, t2);
        SecP192R1Field.multiply(t2, t1, t2);
        SecP192R1Field.squareN(t2, 64, t1);
        SecP192R1Field.multiply(t1, t2, t1);
        SecP192R1Field.squareN(t1, 62, t1);
        SecP192R1Field.square(t1, t2);
        if (Nat192.m41eq(x1, t2)) {
            return new SecP192R1FieldElement(t1);
        }
        return null;
    }

    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof SecP192R1FieldElement)) {
            return false;
        }
        SecP192R1FieldElement o = (SecP192R1FieldElement) other;
        return Nat192.m41eq(this.f862x, o.f862x);
    }

    public int hashCode() {
        return f861Q.hashCode() ^ Arrays.hashCode(this.f862x, 0, 6);
    }
}
