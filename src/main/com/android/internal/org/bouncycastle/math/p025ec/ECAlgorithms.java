package com.android.internal.org.bouncycastle.math.p025ec;

import com.android.internal.org.bouncycastle.math.field.FiniteField;
import com.android.internal.org.bouncycastle.math.field.PolynomialExtensionField;
import com.android.internal.org.bouncycastle.math.p025ec.ECCurve;
import com.android.internal.org.bouncycastle.math.p025ec.endo.ECEndomorphism;
import com.android.internal.org.bouncycastle.math.p025ec.endo.EndoUtil;
import com.android.internal.org.bouncycastle.math.p025ec.endo.GLVEndomorphism;
import com.android.internal.org.bouncycastle.math.raw.Nat;
import java.math.BigInteger;
/* renamed from: com.android.internal.org.bouncycastle.math.ec.ECAlgorithms */
/* loaded from: classes4.dex */
public class ECAlgorithms {
    public static boolean isF2mCurve(ECCurve c) {
        return isF2mField(c.getField());
    }

    public static boolean isF2mField(FiniteField field) {
        return field.getDimension() > 1 && field.getCharacteristic().equals(ECConstants.TWO) && (field instanceof PolynomialExtensionField);
    }

    public static boolean isFpCurve(ECCurve c) {
        return isFpField(c.getField());
    }

    public static boolean isFpField(FiniteField field) {
        return field.getDimension() == 1;
    }

    public static ECPoint sumOfMultiplies(ECPoint[] ps, BigInteger[] ks) {
        if (ps == null || ks == null || ps.length != ks.length || ps.length < 1) {
            throw new IllegalArgumentException("point and scalar arrays should be non-null, and of equal, non-zero, length");
        }
        int count = ps.length;
        switch (count) {
            case 1:
                return ps[0].multiply(ks[0]);
            case 2:
                return sumOfTwoMultiplies(ps[0], ks[0], ps[1], ks[1]);
            default:
                ECPoint p = ps[0];
                ECCurve c = p.getCurve();
                ECPoint[] imported = new ECPoint[count];
                imported[0] = p;
                for (int i = 1; i < count; i++) {
                    imported[i] = importPoint(c, ps[i]);
                }
                ECEndomorphism endomorphism = c.getEndomorphism();
                if (endomorphism instanceof GLVEndomorphism) {
                    return implCheckResult(implSumOfMultipliesGLV(imported, ks, (GLVEndomorphism) endomorphism));
                }
                return implCheckResult(implSumOfMultiplies(imported, ks));
        }
    }

    public static ECPoint sumOfTwoMultiplies(ECPoint P, BigInteger a, ECPoint Q, BigInteger b) {
        ECCurve cp = P.getCurve();
        ECPoint Q2 = importPoint(cp, Q);
        if (cp instanceof ECCurve.AbstractF2m) {
            ECCurve.AbstractF2m f2mCurve = (ECCurve.AbstractF2m) cp;
            if (f2mCurve.isKoblitz()) {
                return implCheckResult(P.multiply(a).add(Q2.multiply(b)));
            }
        }
        ECEndomorphism endomorphism = cp.getEndomorphism();
        if (endomorphism instanceof GLVEndomorphism) {
            return implCheckResult(implSumOfMultipliesGLV(new ECPoint[]{P, Q2}, new BigInteger[]{a, b}, (GLVEndomorphism) endomorphism));
        }
        return implCheckResult(implShamirsTrickWNaf(P, a, Q2, b));
    }

    public static ECPoint shamirsTrick(ECPoint P, BigInteger k, ECPoint Q, BigInteger l) {
        ECCurve cp = P.getCurve();
        return implCheckResult(implShamirsTrickJsf(P, k, importPoint(cp, Q), l));
    }

    public static ECPoint importPoint(ECCurve c, ECPoint p) {
        ECCurve cp = p.getCurve();
        if (!c.equals(cp)) {
            throw new IllegalArgumentException("Point must be on the same curve");
        }
        return c.importPoint(p);
    }

    public static void montgomeryTrick(ECFieldElement[] zs, int off, int len) {
        montgomeryTrick(zs, off, len, null);
    }

    public static void montgomeryTrick(ECFieldElement[] zs, int off, int len, ECFieldElement scale) {
        ECFieldElement[] c = new ECFieldElement[len];
        c[0] = zs[off];
        int i = 0;
        while (true) {
            i++;
            if (i >= len) {
                break;
            }
            c[i] = c[i - 1].multiply(zs[off + i]);
        }
        int j = i - 1;
        if (scale != null) {
            c[j] = c[j].multiply(scale);
        }
        ECFieldElement u = c[j].invert();
        while (j > 0) {
            int i2 = j - 1;
            int j2 = j + off;
            ECFieldElement tmp = zs[j2];
            zs[j2] = c[i2].multiply(u);
            u = u.multiply(tmp);
            j = i2;
        }
        zs[off] = u;
    }

    public static ECPoint referenceMultiply(ECPoint p, BigInteger k) {
        BigInteger x = k.abs();
        ECPoint q = p.getCurve().getInfinity();
        int t = x.bitLength();
        if (t > 0) {
            if (x.testBit(0)) {
                q = p;
            }
            for (int i = 1; i < t; i++) {
                p = p.twice();
                if (x.testBit(i)) {
                    q = q.add(p);
                }
            }
        }
        int i2 = k.signum();
        return i2 < 0 ? q.negate() : q;
    }

    public static ECPoint validatePoint(ECPoint p) {
        if (!p.isValid()) {
            throw new IllegalStateException("Invalid point");
        }
        return p;
    }

    public static ECPoint cleanPoint(ECCurve c, ECPoint p) {
        ECCurve cp = p.getCurve();
        if (!c.equals(cp)) {
            throw new IllegalArgumentException("Point must be on the same curve");
        }
        return c.decodePoint(p.getEncoded(false));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static ECPoint implCheckResult(ECPoint p) {
        if (!p.isValidPartial()) {
            throw new IllegalStateException("Invalid result");
        }
        return p;
    }

    static ECPoint implShamirsTrickJsf(ECPoint P, BigInteger k, ECPoint Q, BigInteger l) {
        ECCurve curve = P.getCurve();
        ECPoint infinity = curve.getInfinity();
        ECPoint PaddQ = P.add(Q);
        ECPoint PsubQ = P.subtract(Q);
        ECPoint[] points = {Q, PsubQ, P, PaddQ};
        curve.normalizeAll(points);
        ECPoint R = infinity;
        ECPoint[] table = {points[3].negate(), points[2].negate(), points[1].negate(), points[0].negate(), R, points[0], points[1], points[2], points[3]};
        byte[] jsf = WNafUtil.generateJSF(k, l);
        int i = jsf.length;
        while (true) {
            i--;
            if (i >= 0) {
                int jsfi = jsf[i];
                int kDigit = (jsfi << 24) >> 28;
                int lDigit = (jsfi << 28) >> 28;
                int index = (kDigit * 3) + 4 + lDigit;
                R = R.twicePlus(table[index]);
            } else {
                return R;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static ECPoint implShamirsTrickWNaf(ECPoint P, BigInteger k, ECPoint Q, BigInteger l) {
        boolean negK = k.signum() < 0;
        boolean negL = l.signum() < 0;
        BigInteger kAbs = k.abs();
        BigInteger lAbs = l.abs();
        int minWidthP = WNafUtil.getWindowSize(kAbs.bitLength(), 8);
        int minWidthQ = WNafUtil.getWindowSize(lAbs.bitLength(), 8);
        WNafPreCompInfo infoP = WNafUtil.precompute(P, minWidthP, true);
        WNafPreCompInfo infoQ = WNafUtil.precompute(Q, minWidthQ, true);
        ECCurve c = P.getCurve();
        int combSize = FixedPointUtil.getCombSize(c);
        if (!negK && !negL && k.bitLength() <= combSize && l.bitLength() <= combSize && infoP.isPromoted() && infoQ.isPromoted()) {
            return implShamirsTrickFixedPoint(P, k, Q, l);
        }
        int widthP = Math.min(8, infoP.getWidth());
        int widthQ = Math.min(8, infoQ.getWidth());
        ECPoint[] preCompP = negK ? infoP.getPreCompNeg() : infoP.getPreComp();
        ECPoint[] preCompQ = negL ? infoQ.getPreCompNeg() : infoQ.getPreComp();
        ECPoint[] preCompNegP = negK ? infoP.getPreComp() : infoP.getPreCompNeg();
        ECPoint[] preCompNegQ = negL ? infoQ.getPreComp() : infoQ.getPreCompNeg();
        byte[] wnafP = WNafUtil.generateWindowNaf(widthP, kAbs);
        byte[] wnafQ = WNafUtil.generateWindowNaf(widthQ, lAbs);
        return implShamirsTrickWNaf(preCompP, preCompNegP, wnafP, preCompQ, preCompNegQ, wnafQ);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static ECPoint implShamirsTrickWNaf(ECEndomorphism endomorphism, ECPoint P, BigInteger k, BigInteger l) {
        boolean negK = k.signum() < 0;
        boolean negL = l.signum() < 0;
        BigInteger k2 = k.abs();
        BigInteger l2 = l.abs();
        int minWidth = WNafUtil.getWindowSize(Math.max(k2.bitLength(), l2.bitLength()), 8);
        WNafPreCompInfo infoP = WNafUtil.precompute(P, minWidth, true);
        ECPoint Q = EndoUtil.mapPoint(endomorphism, P);
        WNafPreCompInfo infoQ = WNafUtil.precomputeWithPointMap(Q, endomorphism.getPointMap(), infoP, true);
        int widthP = Math.min(8, infoP.getWidth());
        int widthQ = Math.min(8, infoQ.getWidth());
        ECPoint[] preCompP = negK ? infoP.getPreCompNeg() : infoP.getPreComp();
        ECPoint[] preCompQ = negL ? infoQ.getPreCompNeg() : infoQ.getPreComp();
        ECPoint[] preCompNegP = negK ? infoP.getPreComp() : infoP.getPreCompNeg();
        ECPoint[] preCompNegQ = negL ? infoQ.getPreComp() : infoQ.getPreCompNeg();
        byte[] wnafP = WNafUtil.generateWindowNaf(widthP, k2);
        byte[] wnafQ = WNafUtil.generateWindowNaf(widthQ, l2);
        return implShamirsTrickWNaf(preCompP, preCompNegP, wnafP, preCompQ, preCompNegQ, wnafQ);
    }

    private static ECPoint implShamirsTrickWNaf(ECPoint[] preCompP, ECPoint[] preCompNegP, byte[] wnafP, ECPoint[] preCompQ, ECPoint[] preCompNegQ, byte[] wnafQ) {
        int wiP;
        int len = Math.max(wnafP.length, wnafQ.length);
        ECCurve curve = preCompP[0].getCurve();
        ECPoint infinity = curve.getInfinity();
        ECPoint R = infinity;
        int zeroes = 0;
        int i = len - 1;
        while (i >= 0) {
            if (i < wnafP.length) {
                wiP = wnafP[i];
            } else {
                wiP = 0;
            }
            int wiQ = i < wnafQ.length ? wnafQ[i] : 0;
            if ((wiP | wiQ) == 0) {
                zeroes++;
            } else {
                ECPoint r = infinity;
                if (wiP != 0) {
                    int nP = Math.abs(wiP);
                    ECPoint[] tableP = wiP < 0 ? preCompNegP : preCompP;
                    r = r.add(tableP[nP >>> 1]);
                }
                if (wiQ != 0) {
                    int nQ = Math.abs(wiQ);
                    ECPoint[] tableQ = wiQ < 0 ? preCompNegQ : preCompQ;
                    r = r.add(tableQ[nQ >>> 1]);
                }
                if (zeroes > 0) {
                    R = R.timesPow2(zeroes);
                    zeroes = 0;
                }
                R = R.twicePlus(r);
            }
            i--;
        }
        if (zeroes > 0) {
            return R.timesPow2(zeroes);
        }
        return R;
    }

    static ECPoint implSumOfMultiplies(ECPoint[] ps, BigInteger[] ks) {
        int count = ps.length;
        boolean[] negs = new boolean[count];
        WNafPreCompInfo[] infos = new WNafPreCompInfo[count];
        byte[][] wnafs = new byte[count];
        for (int i = 0; i < count; i++) {
            BigInteger ki = ks[i];
            negs[i] = ki.signum() < 0;
            BigInteger ki2 = ki.abs();
            int minWidth = WNafUtil.getWindowSize(ki2.bitLength(), 8);
            WNafPreCompInfo info = WNafUtil.precompute(ps[i], minWidth, true);
            int width = Math.min(8, info.getWidth());
            infos[i] = info;
            wnafs[i] = WNafUtil.generateWindowNaf(width, ki2);
        }
        return implSumOfMultiplies(negs, infos, wnafs);
    }

    static ECPoint implSumOfMultipliesGLV(ECPoint[] ps, BigInteger[] ks, GLVEndomorphism glvEndomorphism) {
        BigInteger n = ps[0].getCurve().getOrder();
        int len = ps.length;
        BigInteger[] abs = new BigInteger[len << 1];
        int j = 0;
        for (int i = 0; i < len; i++) {
            BigInteger[] ab = glvEndomorphism.decomposeScalar(ks[i].mod(n));
            int j2 = j + 1;
            abs[j] = ab[0];
            j = j2 + 1;
            abs[j2] = ab[1];
        }
        if (glvEndomorphism.hasEfficientPointMap()) {
            return implSumOfMultiplies(glvEndomorphism, ps, abs);
        }
        ECPoint[] pqs = new ECPoint[len << 1];
        int j3 = 0;
        for (ECPoint p : ps) {
            ECPoint q = EndoUtil.mapPoint(glvEndomorphism, p);
            int j4 = j3 + 1;
            pqs[j3] = p;
            j3 = j4 + 1;
            pqs[j4] = q;
        }
        return implSumOfMultiplies(pqs, abs);
    }

    static ECPoint implSumOfMultiplies(ECEndomorphism endomorphism, ECPoint[] ps, BigInteger[] ks) {
        ECPoint[] eCPointArr = ps;
        int halfCount = eCPointArr.length;
        int fullCount = halfCount << 1;
        boolean[] negs = new boolean[fullCount];
        WNafPreCompInfo[] infos = new WNafPreCompInfo[fullCount];
        byte[][] wnafs = new byte[fullCount];
        ECPointMap pointMap = endomorphism.getPointMap();
        int i = 0;
        while (i < halfCount) {
            int j0 = i << 1;
            int j1 = j0 + 1;
            BigInteger kj0 = ks[j0];
            boolean z = false;
            negs[j0] = kj0.signum() < 0;
            BigInteger kj02 = kj0.abs();
            BigInteger kj1 = ks[j1];
            if (kj1.signum() < 0) {
                z = true;
            }
            negs[j1] = z;
            BigInteger kj12 = kj1.abs();
            int minWidth = WNafUtil.getWindowSize(Math.max(kj02.bitLength(), kj12.bitLength()), 8);
            ECPoint P = eCPointArr[i];
            WNafPreCompInfo infoP = WNafUtil.precompute(P, minWidth, true);
            ECPoint Q = EndoUtil.mapPoint(endomorphism, P);
            int halfCount2 = halfCount;
            WNafPreCompInfo infoQ = WNafUtil.precomputeWithPointMap(Q, pointMap, infoP, true);
            int fullCount2 = fullCount;
            int widthP = Math.min(8, infoP.getWidth());
            int widthQ = Math.min(8, infoQ.getWidth());
            infos[j0] = infoP;
            infos[j1] = infoQ;
            wnafs[j0] = WNafUtil.generateWindowNaf(widthP, kj02);
            wnafs[j1] = WNafUtil.generateWindowNaf(widthQ, kj12);
            i++;
            eCPointArr = ps;
            pointMap = pointMap;
            halfCount = halfCount2;
            fullCount = fullCount2;
        }
        return implSumOfMultiplies(negs, infos, wnafs);
    }

    private static ECPoint implSumOfMultiplies(boolean[] negs, WNafPreCompInfo[] infos, byte[][] wnafs) {
        int len = 0;
        int count = wnafs.length;
        for (byte[] bArr : wnafs) {
            len = Math.max(len, bArr.length);
        }
        int i = 0;
        ECCurve curve = infos[0].getPreComp()[0].getCurve();
        ECPoint infinity = curve.getInfinity();
        ECPoint R = infinity;
        int zeroes = 0;
        int i2 = len - 1;
        while (i2 >= 0) {
            ECPoint r = infinity;
            int j = 0;
            while (j < count) {
                byte[] wnaf = wnafs[j];
                int wi = i2 < wnaf.length ? wnaf[i2] : i;
                if (wi != 0) {
                    int n = Math.abs(wi);
                    WNafPreCompInfo info = infos[j];
                    ECPoint[] table = (wi < 0 ? 1 : i) == negs[j] ? info.getPreComp() : info.getPreCompNeg();
                    r = r.add(table[n >>> 1]);
                }
                j++;
                i = 0;
            }
            if (r == infinity) {
                zeroes++;
            } else {
                if (zeroes > 0) {
                    R = R.timesPow2(zeroes);
                    zeroes = 0;
                }
                R = R.twicePlus(r);
            }
            i2--;
            i = 0;
        }
        if (zeroes > 0) {
            return R.timesPow2(zeroes);
        }
        return R;
    }

    private static ECPoint implShamirsTrickFixedPoint(ECPoint p, BigInteger k, ECPoint q, BigInteger l) {
        ECCurve c = p.getCurve();
        int combSize = FixedPointUtil.getCombSize(c);
        if (k.bitLength() > combSize || l.bitLength() > combSize) {
            throw new IllegalStateException("fixed-point comb doesn't support scalars larger than the curve order");
        }
        FixedPointPreCompInfo infoP = FixedPointUtil.precompute(p);
        FixedPointPreCompInfo infoQ = FixedPointUtil.precompute(q);
        ECLookupTable lookupTableP = infoP.getLookupTable();
        ECLookupTable lookupTableQ = infoQ.getLookupTable();
        int widthP = infoP.getWidth();
        int widthQ = infoQ.getWidth();
        if (widthP != widthQ) {
            FixedPointCombMultiplier m = new FixedPointCombMultiplier();
            ECPoint r1 = m.multiply(p, k);
            ECPoint r2 = m.multiply(q, l);
            return r1.add(r2);
        }
        int d = ((combSize + widthP) - 1) / widthP;
        ECPoint R = c.getInfinity();
        int fullComb = d * widthP;
        int[] K = Nat.fromBigInteger(fullComb, k);
        int[] L = Nat.fromBigInteger(fullComb, l);
        int top = fullComb - 1;
        int i = 0;
        while (i < d) {
            int secretIndexK = 0;
            ECCurve c2 = c;
            int secretIndexL = 0;
            for (int j = top - i; j >= 0; j -= d) {
                int secretBitK = K[j >>> 5] >>> (j & 31);
                secretIndexK = ((secretIndexK ^ (secretBitK >>> 1)) << 1) ^ secretBitK;
                int secretBitL = L[j >>> 5] >>> (j & 31);
                secretIndexL = ((secretIndexL ^ (secretBitL >>> 1)) << 1) ^ secretBitL;
            }
            int combSize2 = combSize;
            ECPoint addP = lookupTableP.lookupVar(secretIndexK);
            ECPoint addQ = lookupTableQ.lookupVar(secretIndexL);
            ECPoint T = addP.add(addQ);
            R = R.twicePlus(T);
            i++;
            c = c2;
            combSize = combSize2;
        }
        return R.add(infoP.getOffset()).add(infoQ.getOffset());
    }
}
