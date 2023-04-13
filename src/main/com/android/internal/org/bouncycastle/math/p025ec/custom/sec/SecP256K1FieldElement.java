package com.android.internal.org.bouncycastle.math.p025ec.custom.sec;

import com.android.internal.org.bouncycastle.math.p025ec.ECFieldElement;
import com.android.internal.org.bouncycastle.math.raw.Nat256;
import com.android.internal.org.bouncycastle.util.Arrays;
import com.android.internal.org.bouncycastle.util.encoders.Hex;
import java.math.BigInteger;
/* renamed from: com.android.internal.org.bouncycastle.math.ec.custom.sec.SecP256K1FieldElement */
/* loaded from: classes4.dex */
public class SecP256K1FieldElement extends ECFieldElement.AbstractFp {

    /* renamed from: Q */
    public static final BigInteger f877Q = new BigInteger(1, Hex.decodeStrict("FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFEFFFFFC2F"));

    /* renamed from: x */
    protected int[] f878x;

    public SecP256K1FieldElement(BigInteger x) {
        if (x == null || x.signum() < 0 || x.compareTo(f877Q) >= 0) {
            throw new IllegalArgumentException("x value invalid for SecP256K1FieldElement");
        }
        this.f878x = SecP256K1Field.fromBigInteger(x);
    }

    public SecP256K1FieldElement() {
        this.f878x = Nat256.create();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public SecP256K1FieldElement(int[] x) {
        this.f878x = x;
    }

    @Override // com.android.internal.org.bouncycastle.math.p025ec.ECFieldElement
    public boolean isZero() {
        return Nat256.isZero(this.f878x);
    }

    @Override // com.android.internal.org.bouncycastle.math.p025ec.ECFieldElement
    public boolean isOne() {
        return Nat256.isOne(this.f878x);
    }

    @Override // com.android.internal.org.bouncycastle.math.p025ec.ECFieldElement
    public boolean testBitZero() {
        return Nat256.getBit(this.f878x, 0) == 1;
    }

    @Override // com.android.internal.org.bouncycastle.math.p025ec.ECFieldElement
    public BigInteger toBigInteger() {
        return Nat256.toBigInteger(this.f878x);
    }

    @Override // com.android.internal.org.bouncycastle.math.p025ec.ECFieldElement
    public String getFieldName() {
        return "SecP256K1Field";
    }

    @Override // com.android.internal.org.bouncycastle.math.p025ec.ECFieldElement
    public int getFieldSize() {
        return f877Q.bitLength();
    }

    @Override // com.android.internal.org.bouncycastle.math.p025ec.ECFieldElement
    public ECFieldElement add(ECFieldElement b) {
        int[] z = Nat256.create();
        SecP256K1Field.add(this.f878x, ((SecP256K1FieldElement) b).f878x, z);
        return new SecP256K1FieldElement(z);
    }

    @Override // com.android.internal.org.bouncycastle.math.p025ec.ECFieldElement
    public ECFieldElement addOne() {
        int[] z = Nat256.create();
        SecP256K1Field.addOne(this.f878x, z);
        return new SecP256K1FieldElement(z);
    }

    @Override // com.android.internal.org.bouncycastle.math.p025ec.ECFieldElement
    public ECFieldElement subtract(ECFieldElement b) {
        int[] z = Nat256.create();
        SecP256K1Field.subtract(this.f878x, ((SecP256K1FieldElement) b).f878x, z);
        return new SecP256K1FieldElement(z);
    }

    @Override // com.android.internal.org.bouncycastle.math.p025ec.ECFieldElement
    public ECFieldElement multiply(ECFieldElement b) {
        int[] z = Nat256.create();
        SecP256K1Field.multiply(this.f878x, ((SecP256K1FieldElement) b).f878x, z);
        return new SecP256K1FieldElement(z);
    }

    @Override // com.android.internal.org.bouncycastle.math.p025ec.ECFieldElement
    public ECFieldElement divide(ECFieldElement b) {
        int[] z = Nat256.create();
        SecP256K1Field.inv(((SecP256K1FieldElement) b).f878x, z);
        SecP256K1Field.multiply(z, this.f878x, z);
        return new SecP256K1FieldElement(z);
    }

    @Override // com.android.internal.org.bouncycastle.math.p025ec.ECFieldElement
    public ECFieldElement negate() {
        int[] z = Nat256.create();
        SecP256K1Field.negate(this.f878x, z);
        return new SecP256K1FieldElement(z);
    }

    @Override // com.android.internal.org.bouncycastle.math.p025ec.ECFieldElement
    public ECFieldElement square() {
        int[] z = Nat256.create();
        SecP256K1Field.square(this.f878x, z);
        return new SecP256K1FieldElement(z);
    }

    @Override // com.android.internal.org.bouncycastle.math.p025ec.ECFieldElement
    public ECFieldElement invert() {
        int[] z = Nat256.create();
        SecP256K1Field.inv(this.f878x, z);
        return new SecP256K1FieldElement(z);
    }

    @Override // com.android.internal.org.bouncycastle.math.p025ec.ECFieldElement
    public ECFieldElement sqrt() {
        int[] x1 = this.f878x;
        if (Nat256.isZero(x1) || Nat256.isOne(x1)) {
            return this;
        }
        int[] x2 = Nat256.create();
        SecP256K1Field.square(x1, x2);
        SecP256K1Field.multiply(x2, x1, x2);
        int[] x3 = Nat256.create();
        SecP256K1Field.square(x2, x3);
        SecP256K1Field.multiply(x3, x1, x3);
        int[] x6 = Nat256.create();
        SecP256K1Field.squareN(x3, 3, x6);
        SecP256K1Field.multiply(x6, x3, x6);
        SecP256K1Field.squareN(x6, 3, x6);
        SecP256K1Field.multiply(x6, x3, x6);
        SecP256K1Field.squareN(x6, 2, x6);
        SecP256K1Field.multiply(x6, x2, x6);
        int[] x22 = Nat256.create();
        SecP256K1Field.squareN(x6, 11, x22);
        SecP256K1Field.multiply(x22, x6, x22);
        SecP256K1Field.squareN(x22, 22, x6);
        SecP256K1Field.multiply(x6, x22, x6);
        int[] x88 = Nat256.create();
        SecP256K1Field.squareN(x6, 44, x88);
        SecP256K1Field.multiply(x88, x6, x88);
        int[] x176 = Nat256.create();
        SecP256K1Field.squareN(x88, 88, x176);
        SecP256K1Field.multiply(x176, x88, x176);
        SecP256K1Field.squareN(x176, 44, x88);
        SecP256K1Field.multiply(x88, x6, x88);
        SecP256K1Field.squareN(x88, 3, x6);
        SecP256K1Field.multiply(x6, x3, x6);
        SecP256K1Field.squareN(x6, 23, x6);
        SecP256K1Field.multiply(x6, x22, x6);
        SecP256K1Field.squareN(x6, 6, x6);
        SecP256K1Field.multiply(x6, x2, x6);
        SecP256K1Field.squareN(x6, 2, x6);
        SecP256K1Field.square(x6, x2);
        if (Nat256.m39eq(x1, x2)) {
            return new SecP256K1FieldElement(x6);
        }
        return null;
    }

    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof SecP256K1FieldElement)) {
            return false;
        }
        SecP256K1FieldElement o = (SecP256K1FieldElement) other;
        return Nat256.m39eq(this.f878x, o.f878x);
    }

    public int hashCode() {
        return f877Q.hashCode() ^ Arrays.hashCode(this.f878x, 0, 8);
    }
}
