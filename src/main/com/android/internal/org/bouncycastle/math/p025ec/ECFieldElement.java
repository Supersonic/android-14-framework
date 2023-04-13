package com.android.internal.org.bouncycastle.math.p025ec;

import com.android.internal.org.bouncycastle.util.Arrays;
import com.android.internal.org.bouncycastle.util.BigIntegers;
import com.android.internal.org.bouncycastle.util.Integers;
import java.math.BigInteger;
import java.util.Random;
/* renamed from: com.android.internal.org.bouncycastle.math.ec.ECFieldElement */
/* loaded from: classes4.dex */
public abstract class ECFieldElement implements ECConstants {

    /* renamed from: com.android.internal.org.bouncycastle.math.ec.ECFieldElement$AbstractFp */
    /* loaded from: classes4.dex */
    public static abstract class AbstractFp extends ECFieldElement {
    }

    public abstract ECFieldElement add(ECFieldElement eCFieldElement);

    public abstract ECFieldElement addOne();

    public abstract ECFieldElement divide(ECFieldElement eCFieldElement);

    public abstract String getFieldName();

    public abstract int getFieldSize();

    public abstract ECFieldElement invert();

    public abstract ECFieldElement multiply(ECFieldElement eCFieldElement);

    public abstract ECFieldElement negate();

    public abstract ECFieldElement sqrt();

    public abstract ECFieldElement square();

    public abstract ECFieldElement subtract(ECFieldElement eCFieldElement);

    public abstract BigInteger toBigInteger();

    public int bitLength() {
        return toBigInteger().bitLength();
    }

    public boolean isOne() {
        return bitLength() == 1;
    }

    public boolean isZero() {
        return toBigInteger().signum() == 0;
    }

    public ECFieldElement multiplyMinusProduct(ECFieldElement b, ECFieldElement x, ECFieldElement y) {
        return multiply(b).subtract(x.multiply(y));
    }

    public ECFieldElement multiplyPlusProduct(ECFieldElement b, ECFieldElement x, ECFieldElement y) {
        return multiply(b).add(x.multiply(y));
    }

    public ECFieldElement squareMinusProduct(ECFieldElement x, ECFieldElement y) {
        return square().subtract(x.multiply(y));
    }

    public ECFieldElement squarePlusProduct(ECFieldElement x, ECFieldElement y) {
        return square().add(x.multiply(y));
    }

    public ECFieldElement squarePow(int pow) {
        ECFieldElement r = this;
        for (int i = 0; i < pow; i++) {
            r = r.square();
        }
        return r;
    }

    public boolean testBitZero() {
        return toBigInteger().testBit(0);
    }

    public String toString() {
        return toBigInteger().toString(16);
    }

    public byte[] getEncoded() {
        return BigIntegers.asUnsignedByteArray((getFieldSize() + 7) / 8, toBigInteger());
    }

    /* renamed from: com.android.internal.org.bouncycastle.math.ec.ECFieldElement$Fp */
    /* loaded from: classes4.dex */
    public static class C4298Fp extends AbstractFp {

        /* renamed from: q */
        BigInteger f844q;

        /* renamed from: r */
        BigInteger f845r;

        /* renamed from: x */
        BigInteger f846x;

        /* JADX INFO: Access modifiers changed from: package-private */
        public static BigInteger calculateResidue(BigInteger p) {
            int bitLength = p.bitLength();
            if (bitLength >= 96) {
                BigInteger firstWord = p.shiftRight(bitLength - 64);
                if (firstWord.longValue() == -1) {
                    return ONE.shiftLeft(bitLength).subtract(p);
                }
                return null;
            }
            return null;
        }

        public C4298Fp(BigInteger q, BigInteger x) {
            this(q, calculateResidue(q), x);
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public C4298Fp(BigInteger q, BigInteger r, BigInteger x) {
            if (x == null || x.signum() < 0 || x.compareTo(q) >= 0) {
                throw new IllegalArgumentException("x value invalid in Fp field element");
            }
            this.f844q = q;
            this.f845r = r;
            this.f846x = x;
        }

        @Override // com.android.internal.org.bouncycastle.math.p025ec.ECFieldElement
        public BigInteger toBigInteger() {
            return this.f846x;
        }

        @Override // com.android.internal.org.bouncycastle.math.p025ec.ECFieldElement
        public String getFieldName() {
            return "Fp";
        }

        @Override // com.android.internal.org.bouncycastle.math.p025ec.ECFieldElement
        public int getFieldSize() {
            return this.f844q.bitLength();
        }

        public BigInteger getQ() {
            return this.f844q;
        }

        @Override // com.android.internal.org.bouncycastle.math.p025ec.ECFieldElement
        public ECFieldElement add(ECFieldElement b) {
            return new C4298Fp(this.f844q, this.f845r, modAdd(this.f846x, b.toBigInteger()));
        }

        @Override // com.android.internal.org.bouncycastle.math.p025ec.ECFieldElement
        public ECFieldElement addOne() {
            BigInteger x2 = this.f846x.add(ECConstants.ONE);
            if (x2.compareTo(this.f844q) == 0) {
                x2 = ECConstants.ZERO;
            }
            return new C4298Fp(this.f844q, this.f845r, x2);
        }

        @Override // com.android.internal.org.bouncycastle.math.p025ec.ECFieldElement
        public ECFieldElement subtract(ECFieldElement b) {
            return new C4298Fp(this.f844q, this.f845r, modSubtract(this.f846x, b.toBigInteger()));
        }

        @Override // com.android.internal.org.bouncycastle.math.p025ec.ECFieldElement
        public ECFieldElement multiply(ECFieldElement b) {
            return new C4298Fp(this.f844q, this.f845r, modMult(this.f846x, b.toBigInteger()));
        }

        @Override // com.android.internal.org.bouncycastle.math.p025ec.ECFieldElement
        public ECFieldElement multiplyMinusProduct(ECFieldElement b, ECFieldElement x, ECFieldElement y) {
            BigInteger ax = this.f846x;
            BigInteger bx = b.toBigInteger();
            BigInteger xx = x.toBigInteger();
            BigInteger yx = y.toBigInteger();
            BigInteger ab = ax.multiply(bx);
            BigInteger xy = xx.multiply(yx);
            return new C4298Fp(this.f844q, this.f845r, modReduce(ab.subtract(xy)));
        }

        @Override // com.android.internal.org.bouncycastle.math.p025ec.ECFieldElement
        public ECFieldElement multiplyPlusProduct(ECFieldElement b, ECFieldElement x, ECFieldElement y) {
            BigInteger ax = this.f846x;
            BigInteger bx = b.toBigInteger();
            BigInteger xx = x.toBigInteger();
            BigInteger yx = y.toBigInteger();
            BigInteger ab = ax.multiply(bx);
            BigInteger xy = xx.multiply(yx);
            return new C4298Fp(this.f844q, this.f845r, modReduce(ab.add(xy)));
        }

        @Override // com.android.internal.org.bouncycastle.math.p025ec.ECFieldElement
        public ECFieldElement divide(ECFieldElement b) {
            return new C4298Fp(this.f844q, this.f845r, modMult(this.f846x, modInverse(b.toBigInteger())));
        }

        @Override // com.android.internal.org.bouncycastle.math.p025ec.ECFieldElement
        public ECFieldElement negate() {
            if (this.f846x.signum() == 0) {
                return this;
            }
            BigInteger bigInteger = this.f844q;
            return new C4298Fp(bigInteger, this.f845r, bigInteger.subtract(this.f846x));
        }

        @Override // com.android.internal.org.bouncycastle.math.p025ec.ECFieldElement
        public ECFieldElement square() {
            BigInteger bigInteger = this.f844q;
            BigInteger bigInteger2 = this.f845r;
            BigInteger bigInteger3 = this.f846x;
            return new C4298Fp(bigInteger, bigInteger2, modMult(bigInteger3, bigInteger3));
        }

        @Override // com.android.internal.org.bouncycastle.math.p025ec.ECFieldElement
        public ECFieldElement squareMinusProduct(ECFieldElement x, ECFieldElement y) {
            BigInteger ax = this.f846x;
            BigInteger xx = x.toBigInteger();
            BigInteger yx = y.toBigInteger();
            BigInteger aa = ax.multiply(ax);
            BigInteger xy = xx.multiply(yx);
            return new C4298Fp(this.f844q, this.f845r, modReduce(aa.subtract(xy)));
        }

        @Override // com.android.internal.org.bouncycastle.math.p025ec.ECFieldElement
        public ECFieldElement squarePlusProduct(ECFieldElement x, ECFieldElement y) {
            BigInteger ax = this.f846x;
            BigInteger xx = x.toBigInteger();
            BigInteger yx = y.toBigInteger();
            BigInteger aa = ax.multiply(ax);
            BigInteger xy = xx.multiply(yx);
            return new C4298Fp(this.f844q, this.f845r, modReduce(aa.add(xy)));
        }

        @Override // com.android.internal.org.bouncycastle.math.p025ec.ECFieldElement
        public ECFieldElement invert() {
            return new C4298Fp(this.f844q, this.f845r, modInverse(this.f846x));
        }

        @Override // com.android.internal.org.bouncycastle.math.p025ec.ECFieldElement
        public ECFieldElement sqrt() {
            if (isZero() || isOne()) {
                return this;
            }
            if (!this.f844q.testBit(0)) {
                throw new RuntimeException("not done yet");
            }
            if (this.f844q.testBit(1)) {
                BigInteger e = this.f844q.shiftRight(2).add(ECConstants.ONE);
                BigInteger bigInteger = this.f844q;
                return checkSqrt(new C4298Fp(bigInteger, this.f845r, this.f846x.modPow(e, bigInteger)));
            }
            BigInteger e2 = this.f844q;
            if (e2.testBit(2)) {
                BigInteger t1 = this.f846x.modPow(this.f844q.shiftRight(3), this.f844q);
                BigInteger t2 = modMult(t1, this.f846x);
                BigInteger t3 = modMult(t2, t1);
                if (t3.equals(ECConstants.ONE)) {
                    return checkSqrt(new C4298Fp(this.f844q, this.f845r, t2));
                }
                BigInteger t4 = ECConstants.TWO.modPow(this.f844q.shiftRight(2), this.f844q);
                BigInteger y = modMult(t2, t4);
                return checkSqrt(new C4298Fp(this.f844q, this.f845r, y));
            }
            BigInteger legendreExponent = this.f844q.shiftRight(1);
            if (!this.f846x.modPow(legendreExponent, this.f844q).equals(ECConstants.ONE)) {
                return null;
            }
            BigInteger X = this.f846x;
            BigInteger fourX = modDouble(modDouble(X));
            BigInteger k = legendreExponent.add(ECConstants.ONE);
            BigInteger qMinusOne = this.f844q.subtract(ECConstants.ONE);
            Random rand = new Random();
            while (true) {
                BigInteger P = new BigInteger(this.f844q.bitLength(), rand);
                if (P.compareTo(this.f844q) < 0 && modReduce(P.multiply(P).subtract(fourX)).modPow(legendreExponent, this.f844q).equals(qMinusOne)) {
                    BigInteger[] result = lucasSequence(P, X, k);
                    BigInteger U = result[0];
                    BigInteger V = result[1];
                    if (modMult(V, V).equals(fourX)) {
                        return new C4298Fp(this.f844q, this.f845r, modHalfAbs(V));
                    }
                    if (!U.equals(ECConstants.ONE) && !U.equals(qMinusOne)) {
                        return null;
                    }
                }
            }
        }

        private ECFieldElement checkSqrt(ECFieldElement z) {
            if (z.square().equals(this)) {
                return z;
            }
            return null;
        }

        private BigInteger[] lucasSequence(BigInteger P, BigInteger Q, BigInteger k) {
            int n = k.bitLength();
            int s = k.getLowestSetBit();
            BigInteger Uh = ECConstants.ONE;
            BigInteger Vl = ECConstants.TWO;
            BigInteger Vh = P;
            BigInteger Ql = ECConstants.ONE;
            BigInteger Qh = ECConstants.ONE;
            for (int j = n - 1; j >= s + 1; j--) {
                Ql = modMult(Ql, Qh);
                if (k.testBit(j)) {
                    Qh = modMult(Ql, Q);
                    Uh = modMult(Uh, Vh);
                    Vl = modReduce(Vh.multiply(Vl).subtract(P.multiply(Ql)));
                    Vh = modReduce(Vh.multiply(Vh).subtract(Qh.shiftLeft(1)));
                } else {
                    Qh = Ql;
                    Uh = modReduce(Uh.multiply(Vl).subtract(Ql));
                    Vh = modReduce(Vh.multiply(Vl).subtract(P.multiply(Ql)));
                    Vl = modReduce(Vl.multiply(Vl).subtract(Ql.shiftLeft(1)));
                }
            }
            BigInteger Ql2 = modMult(Ql, Qh);
            BigInteger Qh2 = modMult(Ql2, Q);
            BigInteger Uh2 = modReduce(Uh.multiply(Vl).subtract(Ql2));
            BigInteger Vl2 = modReduce(Vh.multiply(Vl).subtract(P.multiply(Ql2)));
            BigInteger Ql3 = modMult(Ql2, Qh2);
            for (int j2 = 1; j2 <= s; j2++) {
                Uh2 = modMult(Uh2, Vl2);
                Vl2 = modReduce(Vl2.multiply(Vl2).subtract(Ql3.shiftLeft(1)));
                Ql3 = modMult(Ql3, Ql3);
            }
            return new BigInteger[]{Uh2, Vl2};
        }

        protected BigInteger modAdd(BigInteger x1, BigInteger x2) {
            BigInteger x3 = x1.add(x2);
            if (x3.compareTo(this.f844q) >= 0) {
                return x3.subtract(this.f844q);
            }
            return x3;
        }

        protected BigInteger modDouble(BigInteger x) {
            BigInteger _2x = x.shiftLeft(1);
            if (_2x.compareTo(this.f844q) >= 0) {
                return _2x.subtract(this.f844q);
            }
            return _2x;
        }

        protected BigInteger modHalf(BigInteger x) {
            if (x.testBit(0)) {
                x = this.f844q.add(x);
            }
            return x.shiftRight(1);
        }

        protected BigInteger modHalfAbs(BigInteger x) {
            if (x.testBit(0)) {
                x = this.f844q.subtract(x);
            }
            return x.shiftRight(1);
        }

        protected BigInteger modInverse(BigInteger x) {
            return BigIntegers.modOddInverse(this.f844q, x);
        }

        protected BigInteger modMult(BigInteger x1, BigInteger x2) {
            return modReduce(x1.multiply(x2));
        }

        protected BigInteger modReduce(BigInteger x) {
            if (this.f845r != null) {
                boolean negative = x.signum() < 0;
                if (negative) {
                    x = x.abs();
                }
                int qLen = this.f844q.bitLength();
                boolean rIsOne = this.f845r.equals(ECConstants.ONE);
                while (x.bitLength() > qLen + 1) {
                    BigInteger u = x.shiftRight(qLen);
                    BigInteger v = x.subtract(u.shiftLeft(qLen));
                    if (!rIsOne) {
                        u = u.multiply(this.f845r);
                    }
                    x = u.add(v);
                }
                while (x.compareTo(this.f844q) >= 0) {
                    x = x.subtract(this.f844q);
                }
                if (negative && x.signum() != 0) {
                    return this.f844q.subtract(x);
                }
                return x;
            }
            return x.mod(this.f844q);
        }

        protected BigInteger modSubtract(BigInteger x1, BigInteger x2) {
            BigInteger x3 = x1.subtract(x2);
            if (x3.signum() < 0) {
                return x3.add(this.f844q);
            }
            return x3;
        }

        public boolean equals(Object other) {
            if (other == this) {
                return true;
            }
            if (other instanceof C4298Fp) {
                C4298Fp o = (C4298Fp) other;
                return this.f844q.equals(o.f844q) && this.f846x.equals(o.f846x);
            }
            return false;
        }

        public int hashCode() {
            return this.f844q.hashCode() ^ this.f846x.hashCode();
        }
    }

    /* renamed from: com.android.internal.org.bouncycastle.math.ec.ECFieldElement$AbstractF2m */
    /* loaded from: classes4.dex */
    public static abstract class AbstractF2m extends ECFieldElement {
        /* JADX WARN: Multi-variable type inference failed */
        public ECFieldElement halfTrace() {
            int m = getFieldSize();
            if ((m & 1) == 0) {
                throw new IllegalStateException("Half-trace only defined for odd m");
            }
            int n = (m + 1) >>> 1;
            int k = 31 - Integers.numberOfLeadingZeros(n);
            int nk = 1;
            ECFieldElement ht = this;
            while (k > 0) {
                ht = ht.squarePow(nk << 1).add(ht);
                k--;
                nk = n >>> k;
                if ((nk & 1) != 0) {
                    ht = ht.squarePow(2).add(this);
                }
            }
            return ht;
        }

        public boolean hasFastTrace() {
            return false;
        }

        /* JADX WARN: Multi-variable type inference failed */
        public int trace() {
            int m = getFieldSize();
            int k = 31 - Integers.numberOfLeadingZeros(m);
            int mk = 1;
            ECFieldElement tr = this;
            while (k > 0) {
                tr = tr.squarePow(mk).add(tr);
                k--;
                mk = m >>> k;
                if ((mk & 1) != 0) {
                    tr = tr.square().add(this);
                }
            }
            if (tr.isZero()) {
                return 0;
            }
            if (tr.isOne()) {
                return 1;
            }
            throw new IllegalStateException("Internal error in trace calculation");
        }
    }

    /* renamed from: com.android.internal.org.bouncycastle.math.ec.ECFieldElement$F2m */
    /* loaded from: classes4.dex */
    public static class F2m extends AbstractF2m {
        public static final int GNB = 1;
        public static final int PPB = 3;
        public static final int TPB = 2;

        /* renamed from: ks */
        private int[] f841ks;

        /* renamed from: m */
        private int f842m;
        private int representation;

        /* renamed from: x */
        LongArray f843x;

        public F2m(int m, int k1, int k2, int k3, BigInteger x) {
            if (x == null || x.signum() < 0 || x.bitLength() > m) {
                throw new IllegalArgumentException("x value invalid in F2m field element");
            }
            if (k2 == 0 && k3 == 0) {
                this.representation = 2;
                this.f841ks = new int[]{k1};
            } else if (k2 >= k3) {
                throw new IllegalArgumentException("k2 must be smaller than k3");
            } else {
                if (k2 <= 0) {
                    throw new IllegalArgumentException("k2 must be larger than 0");
                }
                this.representation = 3;
                this.f841ks = new int[]{k1, k2, k3};
            }
            this.f842m = m;
            this.f843x = new LongArray(x);
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public F2m(int m, int[] ks, LongArray x) {
            this.f842m = m;
            this.representation = ks.length == 1 ? 2 : 3;
            this.f841ks = ks;
            this.f843x = x;
        }

        @Override // com.android.internal.org.bouncycastle.math.p025ec.ECFieldElement
        public int bitLength() {
            return this.f843x.degree();
        }

        @Override // com.android.internal.org.bouncycastle.math.p025ec.ECFieldElement
        public boolean isOne() {
            return this.f843x.isOne();
        }

        @Override // com.android.internal.org.bouncycastle.math.p025ec.ECFieldElement
        public boolean isZero() {
            return this.f843x.isZero();
        }

        @Override // com.android.internal.org.bouncycastle.math.p025ec.ECFieldElement
        public boolean testBitZero() {
            return this.f843x.testBitZero();
        }

        @Override // com.android.internal.org.bouncycastle.math.p025ec.ECFieldElement
        public BigInteger toBigInteger() {
            return this.f843x.toBigInteger();
        }

        @Override // com.android.internal.org.bouncycastle.math.p025ec.ECFieldElement
        public String getFieldName() {
            return "F2m";
        }

        @Override // com.android.internal.org.bouncycastle.math.p025ec.ECFieldElement
        public int getFieldSize() {
            return this.f842m;
        }

        @Override // com.android.internal.org.bouncycastle.math.p025ec.ECFieldElement
        public ECFieldElement add(ECFieldElement b) {
            LongArray iarrClone = (LongArray) this.f843x.clone();
            F2m bF2m = (F2m) b;
            iarrClone.addShiftedByWords(bF2m.f843x, 0);
            return new F2m(this.f842m, this.f841ks, iarrClone);
        }

        @Override // com.android.internal.org.bouncycastle.math.p025ec.ECFieldElement
        public ECFieldElement addOne() {
            return new F2m(this.f842m, this.f841ks, this.f843x.addOne());
        }

        @Override // com.android.internal.org.bouncycastle.math.p025ec.ECFieldElement
        public ECFieldElement subtract(ECFieldElement b) {
            return add(b);
        }

        @Override // com.android.internal.org.bouncycastle.math.p025ec.ECFieldElement
        public ECFieldElement multiply(ECFieldElement b) {
            int i = this.f842m;
            int[] iArr = this.f841ks;
            return new F2m(i, iArr, this.f843x.modMultiply(((F2m) b).f843x, i, iArr));
        }

        @Override // com.android.internal.org.bouncycastle.math.p025ec.ECFieldElement
        public ECFieldElement multiplyMinusProduct(ECFieldElement b, ECFieldElement x, ECFieldElement y) {
            return multiplyPlusProduct(b, x, y);
        }

        @Override // com.android.internal.org.bouncycastle.math.p025ec.ECFieldElement
        public ECFieldElement multiplyPlusProduct(ECFieldElement b, ECFieldElement x, ECFieldElement y) {
            LongArray ax = this.f843x;
            LongArray bx = ((F2m) b).f843x;
            LongArray xx = ((F2m) x).f843x;
            LongArray yx = ((F2m) y).f843x;
            LongArray ab = ax.multiply(bx, this.f842m, this.f841ks);
            LongArray xy = xx.multiply(yx, this.f842m, this.f841ks);
            if (ab == ax || ab == bx) {
                ab = (LongArray) ab.clone();
            }
            ab.addShiftedByWords(xy, 0);
            ab.reduce(this.f842m, this.f841ks);
            return new F2m(this.f842m, this.f841ks, ab);
        }

        @Override // com.android.internal.org.bouncycastle.math.p025ec.ECFieldElement
        public ECFieldElement divide(ECFieldElement b) {
            ECFieldElement bInv = b.invert();
            return multiply(bInv);
        }

        @Override // com.android.internal.org.bouncycastle.math.p025ec.ECFieldElement
        public ECFieldElement negate() {
            return this;
        }

        @Override // com.android.internal.org.bouncycastle.math.p025ec.ECFieldElement
        public ECFieldElement square() {
            int i = this.f842m;
            int[] iArr = this.f841ks;
            return new F2m(i, iArr, this.f843x.modSquare(i, iArr));
        }

        @Override // com.android.internal.org.bouncycastle.math.p025ec.ECFieldElement
        public ECFieldElement squareMinusProduct(ECFieldElement x, ECFieldElement y) {
            return squarePlusProduct(x, y);
        }

        @Override // com.android.internal.org.bouncycastle.math.p025ec.ECFieldElement
        public ECFieldElement squarePlusProduct(ECFieldElement x, ECFieldElement y) {
            LongArray ax = this.f843x;
            LongArray xx = ((F2m) x).f843x;
            LongArray yx = ((F2m) y).f843x;
            LongArray aa = ax.square(this.f842m, this.f841ks);
            LongArray xy = xx.multiply(yx, this.f842m, this.f841ks);
            if (aa == ax) {
                aa = (LongArray) aa.clone();
            }
            aa.addShiftedByWords(xy, 0);
            aa.reduce(this.f842m, this.f841ks);
            return new F2m(this.f842m, this.f841ks, aa);
        }

        @Override // com.android.internal.org.bouncycastle.math.p025ec.ECFieldElement
        public ECFieldElement squarePow(int pow) {
            if (pow < 1) {
                return this;
            }
            int i = this.f842m;
            int[] iArr = this.f841ks;
            return new F2m(i, iArr, this.f843x.modSquareN(pow, i, iArr));
        }

        @Override // com.android.internal.org.bouncycastle.math.p025ec.ECFieldElement
        public ECFieldElement invert() {
            int i = this.f842m;
            int[] iArr = this.f841ks;
            return new F2m(i, iArr, this.f843x.modInverse(i, iArr));
        }

        @Override // com.android.internal.org.bouncycastle.math.p025ec.ECFieldElement
        public ECFieldElement sqrt() {
            return (this.f843x.isZero() || this.f843x.isOne()) ? this : squarePow(this.f842m - 1);
        }

        public int getRepresentation() {
            return this.representation;
        }

        public int getM() {
            return this.f842m;
        }

        public int getK1() {
            return this.f841ks[0];
        }

        public int getK2() {
            int[] iArr = this.f841ks;
            if (iArr.length >= 2) {
                return iArr[1];
            }
            return 0;
        }

        public int getK3() {
            int[] iArr = this.f841ks;
            if (iArr.length >= 3) {
                return iArr[2];
            }
            return 0;
        }

        public boolean equals(Object anObject) {
            if (anObject == this) {
                return true;
            }
            if (anObject instanceof F2m) {
                F2m b = (F2m) anObject;
                return this.f842m == b.f842m && this.representation == b.representation && Arrays.areEqual(this.f841ks, b.f841ks) && this.f843x.equals(b.f843x);
            }
            return false;
        }

        public int hashCode() {
            return (this.f843x.hashCode() ^ this.f842m) ^ Arrays.hashCode(this.f841ks);
        }
    }
}
