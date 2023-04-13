package com.android.internal.org.bouncycastle.math.p025ec.custom.sec;

import com.android.internal.org.bouncycastle.math.p025ec.AbstractECLookupTable;
import com.android.internal.org.bouncycastle.math.p025ec.ECConstants;
import com.android.internal.org.bouncycastle.math.p025ec.ECCurve;
import com.android.internal.org.bouncycastle.math.p025ec.ECFieldElement;
import com.android.internal.org.bouncycastle.math.p025ec.ECLookupTable;
import com.android.internal.org.bouncycastle.math.p025ec.ECPoint;
import com.android.internal.org.bouncycastle.math.raw.Nat192;
import com.android.internal.org.bouncycastle.util.encoders.Hex;
import java.math.BigInteger;
import java.security.SecureRandom;
/* renamed from: com.android.internal.org.bouncycastle.math.ec.custom.sec.SecP192R1Curve */
/* loaded from: classes4.dex */
public class SecP192R1Curve extends ECCurve.AbstractFp {
    private static final int SECP192R1_DEFAULT_COORDS = 2;
    protected SecP192R1Point infinity;

    /* renamed from: q */
    public static final BigInteger f857q = SecP192R1FieldElement.f861Q;
    private static final ECFieldElement[] SECP192R1_AFFINE_ZS = {new SecP192R1FieldElement(ECConstants.ONE)};

    public SecP192R1Curve() {
        super(f857q);
        this.infinity = new SecP192R1Point(this, null, null);
        this.f832a = fromBigInteger(new BigInteger(1, Hex.decodeStrict("FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFEFFFFFFFFFFFFFFFC")));
        this.f833b = fromBigInteger(new BigInteger(1, Hex.decodeStrict("64210519E59C80E70FA7E9AB72243049FEB8DEECC146B9B1")));
        this.order = new BigInteger(1, Hex.decodeStrict("FFFFFFFFFFFFFFFFFFFFFFFF99DEF836146BC9B1B4D22831"));
        this.cofactor = BigInteger.valueOf(1L);
        this.coord = 2;
    }

    @Override // com.android.internal.org.bouncycastle.math.p025ec.ECCurve
    protected ECCurve cloneCurve() {
        return new SecP192R1Curve();
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
        return f857q;
    }

    @Override // com.android.internal.org.bouncycastle.math.p025ec.ECCurve
    public int getFieldSize() {
        return f857q.bitLength();
    }

    @Override // com.android.internal.org.bouncycastle.math.p025ec.ECCurve
    public ECFieldElement fromBigInteger(BigInteger x) {
        return new SecP192R1FieldElement(x);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.internal.org.bouncycastle.math.p025ec.ECCurve
    public ECPoint createRawPoint(ECFieldElement x, ECFieldElement y) {
        return new SecP192R1Point(this, x, y);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.internal.org.bouncycastle.math.p025ec.ECCurve
    public ECPoint createRawPoint(ECFieldElement x, ECFieldElement y, ECFieldElement[] zs) {
        return new SecP192R1Point(this, x, y, zs);
    }

    @Override // com.android.internal.org.bouncycastle.math.p025ec.ECCurve
    public ECPoint getInfinity() {
        return this.infinity;
    }

    @Override // com.android.internal.org.bouncycastle.math.p025ec.ECCurve
    public ECLookupTable createCacheSafeLookupTable(ECPoint[] points, int off, final int len) {
        final int[] table = new int[len * 6 * 2];
        int pos = 0;
        for (int i = 0; i < len; i++) {
            ECPoint p = points[off + i];
            Nat192.copy(((SecP192R1FieldElement) p.getRawXCoord()).f862x, 0, table, pos);
            int pos2 = pos + 6;
            Nat192.copy(((SecP192R1FieldElement) p.getRawYCoord()).f862x, 0, table, pos2);
            pos = pos2 + 6;
        }
        return new AbstractECLookupTable() { // from class: com.android.internal.org.bouncycastle.math.ec.custom.sec.SecP192R1Curve.1
            @Override // com.android.internal.org.bouncycastle.math.p025ec.ECLookupTable
            public int getSize() {
                return len;
            }

            @Override // com.android.internal.org.bouncycastle.math.p025ec.ECLookupTable
            public ECPoint lookup(int index) {
                int[] x = Nat192.create();
                int[] y = Nat192.create();
                int pos3 = 0;
                for (int i2 = 0; i2 < len; i2++) {
                    int MASK = ((i2 ^ index) - 1) >> 31;
                    for (int j = 0; j < 6; j++) {
                        int i3 = x[j];
                        int[] iArr = table;
                        x[j] = i3 ^ (iArr[pos3 + j] & MASK);
                        y[j] = y[j] ^ (iArr[(pos3 + 6) + j] & MASK);
                    }
                    pos3 += 12;
                }
                return createPoint(x, y);
            }

            @Override // com.android.internal.org.bouncycastle.math.p025ec.AbstractECLookupTable, com.android.internal.org.bouncycastle.math.p025ec.ECLookupTable
            public ECPoint lookupVar(int index) {
                int[] x = Nat192.create();
                int[] y = Nat192.create();
                int pos3 = index * 6 * 2;
                for (int j = 0; j < 6; j++) {
                    int[] iArr = table;
                    x[j] = iArr[pos3 + j];
                    y[j] = iArr[pos3 + 6 + j];
                }
                return createPoint(x, y);
            }

            private ECPoint createPoint(int[] x, int[] y) {
                return SecP192R1Curve.this.createRawPoint(new SecP192R1FieldElement(x), new SecP192R1FieldElement(y), SecP192R1Curve.SECP192R1_AFFINE_ZS);
            }
        };
    }

    @Override // com.android.internal.org.bouncycastle.math.p025ec.ECCurve.AbstractFp, com.android.internal.org.bouncycastle.math.p025ec.ECCurve
    public ECFieldElement randomFieldElement(SecureRandom r) {
        int[] x = Nat192.create();
        SecP192R1Field.random(r, x);
        return new SecP192R1FieldElement(x);
    }

    @Override // com.android.internal.org.bouncycastle.math.p025ec.ECCurve.AbstractFp, com.android.internal.org.bouncycastle.math.p025ec.ECCurve
    public ECFieldElement randomFieldElementMult(SecureRandom r) {
        int[] x = Nat192.create();
        SecP192R1Field.randomMult(r, x);
        return new SecP192R1FieldElement(x);
    }
}
