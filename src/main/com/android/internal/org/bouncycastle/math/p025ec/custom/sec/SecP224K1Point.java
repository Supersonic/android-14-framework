package com.android.internal.org.bouncycastle.math.p025ec.custom.sec;

import com.android.internal.org.bouncycastle.math.p025ec.ECCurve;
import com.android.internal.org.bouncycastle.math.p025ec.ECFieldElement;
import com.android.internal.org.bouncycastle.math.p025ec.ECPoint;
import com.android.internal.org.bouncycastle.math.raw.Nat;
import com.android.internal.org.bouncycastle.math.raw.Nat224;
/* renamed from: com.android.internal.org.bouncycastle.math.ec.custom.sec.SecP224K1Point */
/* loaded from: classes4.dex */
public class SecP224K1Point extends ECPoint.AbstractFp {
    /* JADX INFO: Access modifiers changed from: package-private */
    public SecP224K1Point(ECCurve curve, ECFieldElement x, ECFieldElement y) {
        super(curve, x, y);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public SecP224K1Point(ECCurve curve, ECFieldElement x, ECFieldElement y, ECFieldElement[] zs) {
        super(curve, x, y, zs);
    }

    @Override // com.android.internal.org.bouncycastle.math.p025ec.ECPoint
    protected ECPoint detach() {
        return new SecP224K1Point(null, getAffineXCoord(), getAffineYCoord());
    }

    @Override // com.android.internal.org.bouncycastle.math.p025ec.ECPoint
    public ECPoint add(ECPoint b) {
        int[] S2;
        int[] U2;
        int[] S1;
        int[] S12;
        if (isInfinity()) {
            return b;
        }
        if (b.isInfinity()) {
            return this;
        }
        if (this == b) {
            return twice();
        }
        ECCurve curve = getCurve();
        SecP224K1FieldElement X1 = (SecP224K1FieldElement) this.f847x;
        SecP224K1FieldElement Y1 = (SecP224K1FieldElement) this.f848y;
        SecP224K1FieldElement X2 = (SecP224K1FieldElement) b.getXCoord();
        SecP224K1FieldElement Y2 = (SecP224K1FieldElement) b.getYCoord();
        SecP224K1FieldElement Z1 = (SecP224K1FieldElement) this.f849zs[0];
        SecP224K1FieldElement Z2 = (SecP224K1FieldElement) b.getZCoord(0);
        int[] tt1 = Nat224.createExt();
        int[] t2 = Nat224.create();
        int[] t3 = Nat224.create();
        int[] t4 = Nat224.create();
        boolean Z1IsOne = Z1.isOne();
        if (Z1IsOne) {
            U2 = X2.f867x;
            S2 = Y2.f867x;
        } else {
            S2 = t3;
            SecP224K1Field.square(Z1.f867x, S2);
            U2 = t2;
            SecP224K1Field.multiply(S2, X2.f867x, U2);
            SecP224K1Field.multiply(S2, Z1.f867x, S2);
            SecP224K1Field.multiply(S2, Y2.f867x, S2);
        }
        boolean Z2IsOne = Z2.isOne();
        if (Z2IsOne) {
            int[] U1 = X1.f867x;
            int[] U12 = Y1.f867x;
            S1 = U12;
            S12 = U1;
        } else {
            SecP224K1Field.square(Z2.f867x, t4);
            SecP224K1Field.multiply(t4, X1.f867x, tt1);
            SecP224K1Field.multiply(t4, Z2.f867x, t4);
            SecP224K1Field.multiply(t4, Y1.f867x, t4);
            S1 = t4;
            S12 = tt1;
        }
        int[] H = Nat224.create();
        SecP224K1Field.subtract(S12, U2, H);
        SecP224K1Field.subtract(S1, S2, t2);
        if (Nat224.isZero(H)) {
            if (Nat224.isZero(t2)) {
                return twice();
            }
            return curve.getInfinity();
        }
        SecP224K1Field.square(H, t3);
        int[] G = Nat224.create();
        SecP224K1Field.multiply(t3, H, G);
        SecP224K1Field.multiply(t3, S12, t3);
        SecP224K1Field.negate(G, G);
        Nat224.mul(S1, G, tt1);
        int c = Nat224.addBothTo(t3, t3, G);
        SecP224K1Field.reduce32(c, G);
        SecP224K1FieldElement X3 = new SecP224K1FieldElement(t4);
        int[] HSquared = X3.f867x;
        SecP224K1Field.square(t2, HSquared);
        int[] iArr = X3.f867x;
        int[] S13 = X3.f867x;
        SecP224K1Field.subtract(iArr, G, S13);
        SecP224K1FieldElement Y3 = new SecP224K1FieldElement(G);
        SecP224K1Field.subtract(t3, X3.f867x, Y3.f867x);
        SecP224K1Field.multiplyAddToExt(Y3.f867x, t2, tt1);
        SecP224K1Field.reduce(tt1, Y3.f867x);
        SecP224K1FieldElement Z3 = new SecP224K1FieldElement(H);
        if (!Z1IsOne) {
            int[] iArr2 = Z3.f867x;
            int[] R = Z1.f867x;
            SecP224K1Field.multiply(iArr2, R, Z3.f867x);
        }
        if (!Z2IsOne) {
            SecP224K1Field.multiply(Z3.f867x, Z2.f867x, Z3.f867x);
        }
        ECFieldElement[] zs = {Z3};
        return new SecP224K1Point(curve, X3, Y3, zs);
    }

    @Override // com.android.internal.org.bouncycastle.math.p025ec.ECPoint
    public ECPoint twice() {
        if (isInfinity()) {
            return this;
        }
        ECCurve curve = getCurve();
        SecP224K1FieldElement Y1 = (SecP224K1FieldElement) this.f848y;
        if (Y1.isZero()) {
            return curve.getInfinity();
        }
        SecP224K1FieldElement X1 = (SecP224K1FieldElement) this.f847x;
        SecP224K1FieldElement Z1 = (SecP224K1FieldElement) this.f849zs[0];
        int[] Y1Squared = Nat224.create();
        SecP224K1Field.square(Y1.f867x, Y1Squared);
        int[] T = Nat224.create();
        SecP224K1Field.square(Y1Squared, T);
        int[] M = Nat224.create();
        SecP224K1Field.square(X1.f867x, M);
        int c = Nat224.addBothTo(M, M, M);
        SecP224K1Field.reduce32(c, M);
        SecP224K1Field.multiply(Y1Squared, X1.f867x, Y1Squared);
        int c2 = Nat.shiftUpBits(7, Y1Squared, 2, 0);
        SecP224K1Field.reduce32(c2, Y1Squared);
        int[] t1 = Nat224.create();
        int c3 = Nat.shiftUpBits(7, T, 3, 0, t1);
        SecP224K1Field.reduce32(c3, t1);
        SecP224K1FieldElement X3 = new SecP224K1FieldElement(T);
        SecP224K1Field.square(M, X3.f867x);
        SecP224K1Field.subtract(X3.f867x, Y1Squared, X3.f867x);
        SecP224K1Field.subtract(X3.f867x, Y1Squared, X3.f867x);
        SecP224K1FieldElement Y3 = new SecP224K1FieldElement(Y1Squared);
        SecP224K1Field.subtract(Y1Squared, X3.f867x, Y3.f867x);
        SecP224K1Field.multiply(Y3.f867x, M, Y3.f867x);
        SecP224K1Field.subtract(Y3.f867x, t1, Y3.f867x);
        SecP224K1FieldElement Z3 = new SecP224K1FieldElement(M);
        SecP224K1Field.twice(Y1.f867x, Z3.f867x);
        if (!Z1.isOne()) {
            SecP224K1Field.multiply(Z3.f867x, Z1.f867x, Z3.f867x);
        }
        return new SecP224K1Point(curve, X3, Y3, new ECFieldElement[]{Z3});
    }

    @Override // com.android.internal.org.bouncycastle.math.p025ec.ECPoint
    public ECPoint twicePlus(ECPoint b) {
        if (this == b) {
            return threeTimes();
        }
        if (isInfinity()) {
            return b;
        }
        if (b.isInfinity()) {
            return twice();
        }
        ECFieldElement Y1 = this.f848y;
        if (Y1.isZero()) {
            return b;
        }
        return twice().add(b);
    }

    @Override // com.android.internal.org.bouncycastle.math.p025ec.ECPoint
    public ECPoint threeTimes() {
        if (isInfinity() || this.f848y.isZero()) {
            return this;
        }
        return twice().add(this);
    }

    @Override // com.android.internal.org.bouncycastle.math.p025ec.ECPoint
    public ECPoint negate() {
        if (isInfinity()) {
            return this;
        }
        return new SecP224K1Point(this.curve, this.f847x, this.f848y.negate(), this.f849zs);
    }
}
