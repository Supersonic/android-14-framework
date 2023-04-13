package com.android.internal.org.bouncycastle.math.p025ec.custom.sec;

import com.android.internal.org.bouncycastle.math.p025ec.ECFieldElement;
import com.android.internal.org.bouncycastle.math.raw.Nat224;
import com.android.internal.org.bouncycastle.util.Arrays;
import com.android.internal.org.bouncycastle.util.encoders.Hex;
import java.math.BigInteger;
/* renamed from: com.android.internal.org.bouncycastle.math.ec.custom.sec.SecP224K1FieldElement */
/* loaded from: classes4.dex */
public class SecP224K1FieldElement extends ECFieldElement.AbstractFp {

    /* renamed from: x */
    protected int[] f867x;

    /* renamed from: Q */
    public static final BigInteger f866Q = new BigInteger(1, Hex.decodeStrict("FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFEFFFFE56D"));
    private static final int[] PRECOMP_POW2 = {868209154, -587542221, 579297866, -1014948952, -1470801668, 514782679, -1897982644};

    public SecP224K1FieldElement(BigInteger x) {
        if (x == null || x.signum() < 0 || x.compareTo(f866Q) >= 0) {
            throw new IllegalArgumentException("x value invalid for SecP224K1FieldElement");
        }
        this.f867x = SecP224K1Field.fromBigInteger(x);
    }

    public SecP224K1FieldElement() {
        this.f867x = Nat224.create();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public SecP224K1FieldElement(int[] x) {
        this.f867x = x;
    }

    @Override // com.android.internal.org.bouncycastle.math.p025ec.ECFieldElement
    public boolean isZero() {
        return Nat224.isZero(this.f867x);
    }

    @Override // com.android.internal.org.bouncycastle.math.p025ec.ECFieldElement
    public boolean isOne() {
        return Nat224.isOne(this.f867x);
    }

    @Override // com.android.internal.org.bouncycastle.math.p025ec.ECFieldElement
    public boolean testBitZero() {
        return Nat224.getBit(this.f867x, 0) == 1;
    }

    @Override // com.android.internal.org.bouncycastle.math.p025ec.ECFieldElement
    public BigInteger toBigInteger() {
        return Nat224.toBigInteger(this.f867x);
    }

    @Override // com.android.internal.org.bouncycastle.math.p025ec.ECFieldElement
    public String getFieldName() {
        return "SecP224K1Field";
    }

    @Override // com.android.internal.org.bouncycastle.math.p025ec.ECFieldElement
    public int getFieldSize() {
        return f866Q.bitLength();
    }

    @Override // com.android.internal.org.bouncycastle.math.p025ec.ECFieldElement
    public ECFieldElement add(ECFieldElement b) {
        int[] z = Nat224.create();
        SecP224K1Field.add(this.f867x, ((SecP224K1FieldElement) b).f867x, z);
        return new SecP224K1FieldElement(z);
    }

    @Override // com.android.internal.org.bouncycastle.math.p025ec.ECFieldElement
    public ECFieldElement addOne() {
        int[] z = Nat224.create();
        SecP224K1Field.addOne(this.f867x, z);
        return new SecP224K1FieldElement(z);
    }

    @Override // com.android.internal.org.bouncycastle.math.p025ec.ECFieldElement
    public ECFieldElement subtract(ECFieldElement b) {
        int[] z = Nat224.create();
        SecP224K1Field.subtract(this.f867x, ((SecP224K1FieldElement) b).f867x, z);
        return new SecP224K1FieldElement(z);
    }

    @Override // com.android.internal.org.bouncycastle.math.p025ec.ECFieldElement
    public ECFieldElement multiply(ECFieldElement b) {
        int[] z = Nat224.create();
        SecP224K1Field.multiply(this.f867x, ((SecP224K1FieldElement) b).f867x, z);
        return new SecP224K1FieldElement(z);
    }

    @Override // com.android.internal.org.bouncycastle.math.p025ec.ECFieldElement
    public ECFieldElement divide(ECFieldElement b) {
        int[] z = Nat224.create();
        SecP224K1Field.inv(((SecP224K1FieldElement) b).f867x, z);
        SecP224K1Field.multiply(z, this.f867x, z);
        return new SecP224K1FieldElement(z);
    }

    @Override // com.android.internal.org.bouncycastle.math.p025ec.ECFieldElement
    public ECFieldElement negate() {
        int[] z = Nat224.create();
        SecP224K1Field.negate(this.f867x, z);
        return new SecP224K1FieldElement(z);
    }

    @Override // com.android.internal.org.bouncycastle.math.p025ec.ECFieldElement
    public ECFieldElement square() {
        int[] z = Nat224.create();
        SecP224K1Field.square(this.f867x, z);
        return new SecP224K1FieldElement(z);
    }

    @Override // com.android.internal.org.bouncycastle.math.p025ec.ECFieldElement
    public ECFieldElement invert() {
        int[] z = Nat224.create();
        SecP224K1Field.inv(this.f867x, z);
        return new SecP224K1FieldElement(z);
    }

    @Override // com.android.internal.org.bouncycastle.math.p025ec.ECFieldElement
    public ECFieldElement sqrt() {
        int[] x1 = this.f867x;
        if (Nat224.isZero(x1) || Nat224.isOne(x1)) {
            return this;
        }
        int[] x2 = Nat224.create();
        SecP224K1Field.square(x1, x2);
        SecP224K1Field.multiply(x2, x1, x2);
        SecP224K1Field.square(x2, x2);
        SecP224K1Field.multiply(x2, x1, x2);
        int[] x4 = Nat224.create();
        SecP224K1Field.square(x2, x4);
        SecP224K1Field.multiply(x4, x1, x4);
        int[] x8 = Nat224.create();
        SecP224K1Field.squareN(x4, 4, x8);
        SecP224K1Field.multiply(x8, x4, x8);
        int[] x11 = Nat224.create();
        SecP224K1Field.squareN(x8, 3, x11);
        SecP224K1Field.multiply(x11, x2, x11);
        SecP224K1Field.squareN(x11, 8, x11);
        SecP224K1Field.multiply(x11, x8, x11);
        SecP224K1Field.squareN(x11, 4, x8);
        SecP224K1Field.multiply(x8, x4, x8);
        SecP224K1Field.squareN(x8, 19, x4);
        SecP224K1Field.multiply(x4, x11, x4);
        int[] x84 = Nat224.create();
        SecP224K1Field.squareN(x4, 42, x84);
        SecP224K1Field.multiply(x84, x4, x84);
        SecP224K1Field.squareN(x84, 23, x4);
        SecP224K1Field.multiply(x4, x8, x4);
        SecP224K1Field.squareN(x4, 84, x8);
        SecP224K1Field.multiply(x8, x84, x8);
        SecP224K1Field.squareN(x8, 20, x8);
        SecP224K1Field.multiply(x8, x11, x8);
        SecP224K1Field.squareN(x8, 3, x8);
        SecP224K1Field.multiply(x8, x1, x8);
        SecP224K1Field.squareN(x8, 2, x8);
        SecP224K1Field.multiply(x8, x1, x8);
        SecP224K1Field.squareN(x8, 4, x8);
        SecP224K1Field.multiply(x8, x2, x8);
        SecP224K1Field.square(x8, x8);
        SecP224K1Field.square(x8, x84);
        if (Nat224.m40eq(x1, x84)) {
            return new SecP224K1FieldElement(x8);
        }
        SecP224K1Field.multiply(x8, PRECOMP_POW2, x8);
        SecP224K1Field.square(x8, x84);
        if (Nat224.m40eq(x1, x84)) {
            return new SecP224K1FieldElement(x8);
        }
        return null;
    }

    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof SecP224K1FieldElement)) {
            return false;
        }
        SecP224K1FieldElement o = (SecP224K1FieldElement) other;
        return Nat224.m40eq(this.f867x, o.f867x);
    }

    public int hashCode() {
        return f866Q.hashCode() ^ Arrays.hashCode(this.f867x, 0, 7);
    }
}
