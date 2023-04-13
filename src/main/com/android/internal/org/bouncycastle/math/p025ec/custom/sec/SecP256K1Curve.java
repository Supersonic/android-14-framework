package com.android.internal.org.bouncycastle.math.p025ec.custom.sec;

import com.android.internal.org.bouncycastle.math.p025ec.AbstractECLookupTable;
import com.android.internal.org.bouncycastle.math.p025ec.ECConstants;
import com.android.internal.org.bouncycastle.math.p025ec.ECCurve;
import com.android.internal.org.bouncycastle.math.p025ec.ECFieldElement;
import com.android.internal.org.bouncycastle.math.p025ec.ECLookupTable;
import com.android.internal.org.bouncycastle.math.p025ec.ECPoint;
import com.android.internal.org.bouncycastle.math.raw.Nat256;
import com.android.internal.org.bouncycastle.util.encoders.Hex;
import java.math.BigInteger;
import java.security.SecureRandom;
/* renamed from: com.android.internal.org.bouncycastle.math.ec.custom.sec.SecP256K1Curve */
/* loaded from: classes4.dex */
public class SecP256K1Curve extends ECCurve.AbstractFp {
    private static final int SECP256K1_DEFAULT_COORDS = 2;
    protected SecP256K1Point infinity;

    /* renamed from: q */
    public static final BigInteger f874q = SecP256K1FieldElement.f877Q;
    private static final ECFieldElement[] SECP256K1_AFFINE_ZS = {new SecP256K1FieldElement(ECConstants.ONE)};

    public SecP256K1Curve() {
        super(f874q);
        this.infinity = new SecP256K1Point(this, null, null);
        this.f832a = fromBigInteger(ECConstants.ZERO);
        this.f833b = fromBigInteger(BigInteger.valueOf(7L));
        this.order = new BigInteger(1, Hex.decodeStrict("FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFEBAAEDCE6AF48A03BBFD25E8CD0364141"));
        this.cofactor = BigInteger.valueOf(1L);
        this.coord = 2;
    }

    @Override // com.android.internal.org.bouncycastle.math.p025ec.ECCurve
    protected ECCurve cloneCurve() {
        return new SecP256K1Curve();
    }

    @Override // com.android.internal.org.bouncycastle.math.p025ec.ECCurve
    public boolean supportsCoordinateSystem(int coord) {
        switch (coord) {
            case 2:
                return true;
            default:
                return false;
        }
    }

    public BigInteger getQ() {
        return f874q;
    }

    @Override // com.android.internal.org.bouncycastle.math.p025ec.ECCurve
    public int getFieldSize() {
        return f874q.bitLength();
    }

    @Override // com.android.internal.org.bouncycastle.math.p025ec.ECCurve
    public ECFieldElement fromBigInteger(BigInteger x) {
        return new SecP256K1FieldElement(x);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.internal.org.bouncycastle.math.p025ec.ECCurve
    public ECPoint createRawPoint(ECFieldElement x, ECFieldElement y) {
        return new SecP256K1Point(this, x, y);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.internal.org.bouncycastle.math.p025ec.ECCurve
    public ECPoint createRawPoint(ECFieldElement x, ECFieldElement y, ECFieldElement[] zs) {
        return new SecP256K1Point(this, x, y, zs);
    }

    @Override // com.android.internal.org.bouncycastle.math.p025ec.ECCurve
    public ECPoint getInfinity() {
        return this.infinity;
    }

    @Override // com.android.internal.org.bouncycastle.math.p025ec.ECCurve
    public ECLookupTable createCacheSafeLookupTable(ECPoint[] points, int off, final int len) {
        final int[] table = new int[len * 8 * 2];
        int pos = 0;
        for (int i = 0; i < len; i++) {
            ECPoint p = points[off + i];
            Nat256.copy(((SecP256K1FieldElement) p.getRawXCoord()).f878x, 0, table, pos);
            int pos2 = pos + 8;
            Nat256.copy(((SecP256K1FieldElement) p.getRawYCoord()).f878x, 0, table, pos2);
            pos = pos2 + 8;
        }
        return new AbstractECLookupTable() { // from class: com.android.internal.org.bouncycastle.math.ec.custom.sec.SecP256K1Curve.1
            @Override // com.android.internal.org.bouncycastle.math.p025ec.ECLookupTable
            public int getSize() {
                return len;
            }

            @Override // com.android.internal.org.bouncycastle.math.p025ec.ECLookupTable
            public ECPoint lookup(int index) {
                int[] x = Nat256.create();
                int[] y = Nat256.create();
                int pos3 = 0;
                for (int i2 = 0; i2 < len; i2++) {
                    int MASK = ((i2 ^ index) - 1) >> 31;
                    for (int j = 0; j < 8; j++) {
                        int i3 = x[j];
                        int[] iArr = table;
                        x[j] = i3 ^ (iArr[pos3 + j] & MASK);
                        y[j] = y[j] ^ (iArr[(pos3 + 8) + j] & MASK);
                    }
                    pos3 += 16;
                }
                return createPoint(x, y);
            }

            @Override // com.android.internal.org.bouncycastle.math.p025ec.AbstractECLookupTable, com.android.internal.org.bouncycastle.math.p025ec.ECLookupTable
            public ECPoint lookupVar(int index) {
                int[] x = Nat256.create();
                int[] y = Nat256.create();
                int pos3 = index * 8 * 2;
                for (int j = 0; j < 8; j++) {
                    int[] iArr = table;
                    x[j] = iArr[pos3 + j];
                    y[j] = iArr[pos3 + 8 + j];
                }
                return createPoint(x, y);
            }

            private ECPoint createPoint(int[] x, int[] y) {
                return SecP256K1Curve.this.createRawPoint(new SecP256K1FieldElement(x), new SecP256K1FieldElement(y), SecP256K1Curve.SECP256K1_AFFINE_ZS);
            }
        };
    }

    @Override // com.android.internal.org.bouncycastle.math.p025ec.ECCurve.AbstractFp, com.android.internal.org.bouncycastle.math.p025ec.ECCurve
    public ECFieldElement randomFieldElement(SecureRandom r) {
        int[] x = Nat256.create();
        SecP256K1Field.random(r, x);
        return new SecP256K1FieldElement(x);
    }

    @Override // com.android.internal.org.bouncycastle.math.p025ec.ECCurve.AbstractFp, com.android.internal.org.bouncycastle.math.p025ec.ECCurve
    public ECFieldElement randomFieldElementMult(SecureRandom r) {
        int[] x = Nat256.create();
        SecP256K1Field.randomMult(r, x);
        return new SecP256K1FieldElement(x);
    }
}
