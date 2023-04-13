package com.android.internal.org.bouncycastle.math.p025ec.custom.sec;

import com.android.internal.org.bouncycastle.math.p025ec.ECFieldElement;
import com.android.internal.org.bouncycastle.math.raw.Mod;
import com.android.internal.org.bouncycastle.math.raw.Nat;
import com.android.internal.org.bouncycastle.math.raw.Nat224;
import com.android.internal.org.bouncycastle.util.Arrays;
import com.android.internal.org.bouncycastle.util.encoders.Hex;
import java.math.BigInteger;
/* renamed from: com.android.internal.org.bouncycastle.math.ec.custom.sec.SecP224R1FieldElement */
/* loaded from: classes4.dex */
public class SecP224R1FieldElement extends ECFieldElement.AbstractFp {

    /* renamed from: Q */
    public static final BigInteger f872Q = new BigInteger(1, Hex.decodeStrict("FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF000000000000000000000001"));

    /* renamed from: x */
    protected int[] f873x;

    public SecP224R1FieldElement(BigInteger x) {
        if (x == null || x.signum() < 0 || x.compareTo(f872Q) >= 0) {
            throw new IllegalArgumentException("x value invalid for SecP224R1FieldElement");
        }
        this.f873x = SecP224R1Field.fromBigInteger(x);
    }

    public SecP224R1FieldElement() {
        this.f873x = Nat224.create();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public SecP224R1FieldElement(int[] x) {
        this.f873x = x;
    }

    @Override // com.android.internal.org.bouncycastle.math.p025ec.ECFieldElement
    public boolean isZero() {
        return Nat224.isZero(this.f873x);
    }

    @Override // com.android.internal.org.bouncycastle.math.p025ec.ECFieldElement
    public boolean isOne() {
        return Nat224.isOne(this.f873x);
    }

    @Override // com.android.internal.org.bouncycastle.math.p025ec.ECFieldElement
    public boolean testBitZero() {
        return Nat224.getBit(this.f873x, 0) == 1;
    }

    @Override // com.android.internal.org.bouncycastle.math.p025ec.ECFieldElement
    public BigInteger toBigInteger() {
        return Nat224.toBigInteger(this.f873x);
    }

    @Override // com.android.internal.org.bouncycastle.math.p025ec.ECFieldElement
    public String getFieldName() {
        return "SecP224R1Field";
    }

    @Override // com.android.internal.org.bouncycastle.math.p025ec.ECFieldElement
    public int getFieldSize() {
        return f872Q.bitLength();
    }

    @Override // com.android.internal.org.bouncycastle.math.p025ec.ECFieldElement
    public ECFieldElement add(ECFieldElement b) {
        int[] z = Nat224.create();
        SecP224R1Field.add(this.f873x, ((SecP224R1FieldElement) b).f873x, z);
        return new SecP224R1FieldElement(z);
    }

    @Override // com.android.internal.org.bouncycastle.math.p025ec.ECFieldElement
    public ECFieldElement addOne() {
        int[] z = Nat224.create();
        SecP224R1Field.addOne(this.f873x, z);
        return new SecP224R1FieldElement(z);
    }

    @Override // com.android.internal.org.bouncycastle.math.p025ec.ECFieldElement
    public ECFieldElement subtract(ECFieldElement b) {
        int[] z = Nat224.create();
        SecP224R1Field.subtract(this.f873x, ((SecP224R1FieldElement) b).f873x, z);
        return new SecP224R1FieldElement(z);
    }

    @Override // com.android.internal.org.bouncycastle.math.p025ec.ECFieldElement
    public ECFieldElement multiply(ECFieldElement b) {
        int[] z = Nat224.create();
        SecP224R1Field.multiply(this.f873x, ((SecP224R1FieldElement) b).f873x, z);
        return new SecP224R1FieldElement(z);
    }

    @Override // com.android.internal.org.bouncycastle.math.p025ec.ECFieldElement
    public ECFieldElement divide(ECFieldElement b) {
        int[] z = Nat224.create();
        SecP224R1Field.inv(((SecP224R1FieldElement) b).f873x, z);
        SecP224R1Field.multiply(z, this.f873x, z);
        return new SecP224R1FieldElement(z);
    }

    @Override // com.android.internal.org.bouncycastle.math.p025ec.ECFieldElement
    public ECFieldElement negate() {
        int[] z = Nat224.create();
        SecP224R1Field.negate(this.f873x, z);
        return new SecP224R1FieldElement(z);
    }

    @Override // com.android.internal.org.bouncycastle.math.p025ec.ECFieldElement
    public ECFieldElement square() {
        int[] z = Nat224.create();
        SecP224R1Field.square(this.f873x, z);
        return new SecP224R1FieldElement(z);
    }

    @Override // com.android.internal.org.bouncycastle.math.p025ec.ECFieldElement
    public ECFieldElement invert() {
        int[] z = Nat224.create();
        SecP224R1Field.inv(this.f873x, z);
        return new SecP224R1FieldElement(z);
    }

    @Override // com.android.internal.org.bouncycastle.math.p025ec.ECFieldElement
    public ECFieldElement sqrt() {
        int[] c = this.f873x;
        if (Nat224.isZero(c) || Nat224.isOne(c)) {
            return this;
        }
        int[] nc = Nat224.create();
        SecP224R1Field.negate(c, nc);
        int[] r = Mod.random(SecP224R1Field.f870P);
        int[] t = Nat224.create();
        if (isSquare(c)) {
            while (!trySqrt(nc, r, t)) {
                SecP224R1Field.addOne(r, r);
            }
            SecP224R1Field.square(t, r);
            if (Nat224.m40eq(c, r)) {
                return new SecP224R1FieldElement(t);
            }
            return null;
        }
        return null;
    }

    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof SecP224R1FieldElement)) {
            return false;
        }
        SecP224R1FieldElement o = (SecP224R1FieldElement) other;
        return Nat224.m40eq(this.f873x, o.f873x);
    }

    public int hashCode() {
        return f872Q.hashCode() ^ Arrays.hashCode(this.f873x, 0, 7);
    }

    private static boolean isSquare(int[] x) {
        int[] t1 = Nat224.create();
        int[] t2 = Nat224.create();
        Nat224.copy(x, t1);
        for (int i = 0; i < 7; i++) {
            Nat224.copy(t1, t2);
            SecP224R1Field.squareN(t1, 1 << i, t1);
            SecP224R1Field.multiply(t1, t2, t1);
        }
        SecP224R1Field.squareN(t1, 95, t1);
        return Nat224.isOne(t1);
    }

    /* renamed from: RM */
    private static void m45RM(int[] nc, int[] d0, int[] e0, int[] d1, int[] e1, int[] f1, int[] t) {
        SecP224R1Field.multiply(e1, e0, t);
        SecP224R1Field.multiply(t, nc, t);
        SecP224R1Field.multiply(d1, d0, f1);
        SecP224R1Field.add(f1, t, f1);
        SecP224R1Field.multiply(d1, e0, t);
        Nat224.copy(f1, d1);
        SecP224R1Field.multiply(e1, d0, e1);
        SecP224R1Field.add(e1, t, e1);
        SecP224R1Field.square(e1, f1);
        SecP224R1Field.multiply(f1, nc, f1);
    }

    /* renamed from: RP */
    private static void m44RP(int[] nc, int[] d1, int[] e1, int[] f1, int[] t) {
        Nat224.copy(nc, f1);
        int[] d0 = Nat224.create();
        int[] e0 = Nat224.create();
        for (int i = 0; i < 7; i++) {
            Nat224.copy(d1, d0);
            Nat224.copy(e1, e0);
            int j = 1 << i;
            while (true) {
                int j2 = j - 1;
                if (j2 >= 0) {
                    m43RS(d1, e1, f1, t);
                    j = j2;
                }
            }
            m45RM(nc, d0, e0, d1, e1, f1, t);
        }
    }

    /* renamed from: RS */
    private static void m43RS(int[] d, int[] e, int[] f, int[] t) {
        SecP224R1Field.multiply(e, d, e);
        SecP224R1Field.twice(e, e);
        SecP224R1Field.square(d, t);
        SecP224R1Field.add(f, t, d);
        SecP224R1Field.multiply(f, t, f);
        int c = Nat.shiftUpBits(7, f, 2, 0);
        SecP224R1Field.reduce32(c, f);
    }

    private static boolean trySqrt(int[] nc, int[] r, int[] t) {
        int[] d1 = Nat224.create();
        Nat224.copy(r, d1);
        int[] e1 = Nat224.create();
        e1[0] = 1;
        int[] f1 = Nat224.create();
        m44RP(nc, d1, e1, f1, t);
        int[] d0 = Nat224.create();
        int[] e0 = Nat224.create();
        for (int k = 1; k < 96; k++) {
            Nat224.copy(d1, d0);
            Nat224.copy(e1, e0);
            m43RS(d1, e1, f1, t);
            if (Nat224.isZero(d1)) {
                SecP224R1Field.inv(e0, t);
                SecP224R1Field.multiply(t, d0, t);
                return true;
            }
        }
        return false;
    }
}
