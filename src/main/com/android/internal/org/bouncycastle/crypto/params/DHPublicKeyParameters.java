package com.android.internal.org.bouncycastle.crypto.params;

import com.android.internal.org.bouncycastle.math.raw.Nat;
import com.android.internal.org.bouncycastle.util.Integers;
import java.math.BigInteger;
/* loaded from: classes4.dex */
public class DHPublicKeyParameters extends DHKeyParameters {
    private static final BigInteger ONE = BigInteger.valueOf(1);
    private static final BigInteger TWO = BigInteger.valueOf(2);

    /* renamed from: y */
    private BigInteger f776y;

    public DHPublicKeyParameters(BigInteger y, DHParameters params) {
        super(false, params);
        this.f776y = validate(y, params);
    }

    private BigInteger validate(BigInteger y, DHParameters dhParams) {
        if (y == null) {
            throw new NullPointerException("y value cannot be null");
        }
        BigInteger p = dhParams.getP();
        BigInteger bigInteger = TWO;
        if (y.compareTo(bigInteger) < 0 || y.compareTo(p.subtract(bigInteger)) > 0) {
            throw new IllegalArgumentException("invalid DH public key");
        }
        BigInteger q = dhParams.getQ();
        if (q == null) {
            return y;
        }
        if (p.testBit(0) && p.bitLength() - 1 == q.bitLength() && p.shiftRight(1).equals(q)) {
            if (1 == legendre(y, p)) {
                return y;
            }
        } else if (ONE.equals(y.modPow(q, p))) {
            return y;
        }
        throw new IllegalArgumentException("Y value does not appear to be in correct group");
    }

    public BigInteger getY() {
        return this.f776y;
    }

    @Override // com.android.internal.org.bouncycastle.crypto.params.DHKeyParameters
    public int hashCode() {
        return this.f776y.hashCode() ^ super.hashCode();
    }

    @Override // com.android.internal.org.bouncycastle.crypto.params.DHKeyParameters
    public boolean equals(Object obj) {
        if (obj instanceof DHPublicKeyParameters) {
            DHPublicKeyParameters other = (DHPublicKeyParameters) obj;
            return other.getY().equals(this.f776y) && super.equals(obj);
        }
        return false;
    }

    private static int legendre(BigInteger a, BigInteger b) {
        int bitLength = b.bitLength();
        int[] A = Nat.fromBigInteger(bitLength, a);
        int[] B = Nat.fromBigInteger(bitLength, b);
        int r = 0;
        int len = B.length;
        while (true) {
            if (A[0] == 0) {
                Nat.shiftDownWord(len, A, 0);
            } else {
                int shift = Integers.numberOfTrailingZeros(A[0]);
                if (shift > 0) {
                    Nat.shiftDownBits(len, A, shift, 0);
                    int bits = B[0];
                    r ^= ((bits >>> 1) ^ bits) & (shift << 1);
                }
                int cmp = Nat.compare(len, A, B);
                if (cmp == 0) {
                    break;
                }
                if (cmp < 0) {
                    r ^= B[0] & A[0];
                    int[] t = A;
                    A = B;
                    B = t;
                }
                while (A[len - 1] == 0) {
                    len--;
                }
                Nat.sub(len, A, B, A);
            }
        }
        if (Nat.isOne(len, B)) {
            return 1 - (r & 2);
        }
        return 0;
    }
}
