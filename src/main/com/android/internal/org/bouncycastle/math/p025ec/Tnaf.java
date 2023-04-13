package com.android.internal.org.bouncycastle.math.p025ec;

import com.android.internal.org.bouncycastle.math.p025ec.ECCurve;
import com.android.internal.org.bouncycastle.math.p025ec.ECPoint;
import java.math.BigInteger;
/* JADX INFO: Access modifiers changed from: package-private */
/* renamed from: com.android.internal.org.bouncycastle.math.ec.Tnaf */
/* loaded from: classes4.dex */
public class Tnaf {
    private static final BigInteger MINUS_ONE;
    private static final BigInteger MINUS_THREE;
    private static final BigInteger MINUS_TWO;
    public static final byte POW_2_WIDTH = 16;
    public static final byte WIDTH = 4;
    public static final ZTauElement[] alpha0;
    public static final byte[][] alpha0Tnaf;
    public static final ZTauElement[] alpha1;
    public static final byte[][] alpha1Tnaf;

    Tnaf() {
    }

    static {
        BigInteger negate = ECConstants.ONE.negate();
        MINUS_ONE = negate;
        MINUS_TWO = ECConstants.TWO.negate();
        BigInteger negate2 = ECConstants.THREE.negate();
        MINUS_THREE = negate2;
        alpha0 = new ZTauElement[]{null, new ZTauElement(ECConstants.ONE, ECConstants.ZERO), null, new ZTauElement(negate2, negate), null, new ZTauElement(negate, negate), null, new ZTauElement(ECConstants.ONE, negate), null};
        alpha0Tnaf = new byte[][]{null, new byte[]{1}, null, new byte[]{-1, 0, 1}, null, new byte[]{1, 0, 1}, null, new byte[]{-1, 0, 0, 1}};
        alpha1 = new ZTauElement[]{null, new ZTauElement(ECConstants.ONE, ECConstants.ZERO), null, new ZTauElement(negate2, ECConstants.ONE), null, new ZTauElement(negate, ECConstants.ONE), null, new ZTauElement(ECConstants.ONE, ECConstants.ONE), null};
        alpha1Tnaf = new byte[][]{null, new byte[]{1}, null, new byte[]{-1, 0, 1}, null, new byte[]{1, 0, 1}, null, new byte[]{-1, 0, 0, -1}};
    }

    public static BigInteger norm(byte mu, ZTauElement lambda) {
        BigInteger s1 = lambda.f850u.multiply(lambda.f850u);
        BigInteger s2 = lambda.f850u.multiply(lambda.f851v);
        BigInteger s3 = lambda.f851v.multiply(lambda.f851v).shiftLeft(1);
        if (mu == 1) {
            BigInteger norm = s1.add(s2).add(s3);
            return norm;
        } else if (mu == -1) {
            BigInteger norm2 = s1.subtract(s2).add(s3);
            return norm2;
        } else {
            throw new IllegalArgumentException("mu must be 1 or -1");
        }
    }

    public static SimpleBigDecimal norm(byte mu, SimpleBigDecimal u, SimpleBigDecimal v) {
        SimpleBigDecimal s1 = u.multiply(u);
        SimpleBigDecimal s2 = u.multiply(v);
        SimpleBigDecimal s3 = v.multiply(v).shiftLeft(1);
        if (mu == 1) {
            SimpleBigDecimal norm = s1.add(s2).add(s3);
            return norm;
        } else if (mu == -1) {
            SimpleBigDecimal norm2 = s1.subtract(s2).add(s3);
            return norm2;
        } else {
            throw new IllegalArgumentException("mu must be 1 or -1");
        }
    }

    public static ZTauElement round(SimpleBigDecimal lambda0, SimpleBigDecimal lambda1, byte mu) {
        SimpleBigDecimal eta;
        SimpleBigDecimal check1;
        SimpleBigDecimal check2;
        int scale = lambda0.getScale();
        if (lambda1.getScale() == scale) {
            if (mu != 1 && mu != -1) {
                throw new IllegalArgumentException("mu must be 1 or -1");
            }
            BigInteger f0 = lambda0.round();
            BigInteger f1 = lambda1.round();
            SimpleBigDecimal eta0 = lambda0.subtract(f0);
            SimpleBigDecimal eta1 = lambda1.subtract(f1);
            SimpleBigDecimal eta2 = eta0.add(eta0);
            if (mu == 1) {
                eta = eta2.add(eta1);
            } else {
                eta = eta2.subtract(eta1);
            }
            SimpleBigDecimal threeEta1 = eta1.add(eta1).add(eta1);
            SimpleBigDecimal fourEta1 = threeEta1.add(eta1);
            if (mu == 1) {
                check1 = eta0.subtract(threeEta1);
                check2 = eta0.add(fourEta1);
            } else {
                check1 = eta0.add(threeEta1);
                check2 = eta0.subtract(fourEta1);
            }
            byte h0 = 0;
            byte h1 = 0;
            if (eta.compareTo(ECConstants.ONE) >= 0) {
                if (check1.compareTo(MINUS_ONE) < 0) {
                    h1 = mu;
                } else {
                    h0 = 1;
                }
            } else if (check2.compareTo(ECConstants.TWO) >= 0) {
                h1 = mu;
            }
            if (eta.compareTo(MINUS_ONE) < 0) {
                if (check1.compareTo(ECConstants.ONE) >= 0) {
                    h1 = (byte) (-mu);
                } else {
                    h0 = -1;
                }
            } else if (check2.compareTo(MINUS_TWO) < 0) {
                h1 = (byte) (-mu);
            }
            BigInteger q0 = f0.add(BigInteger.valueOf(h0));
            BigInteger q1 = f1.add(BigInteger.valueOf(h1));
            return new ZTauElement(q0, q1);
        }
        throw new IllegalArgumentException("lambda0 and lambda1 do not have same scale");
    }

    public static SimpleBigDecimal approximateDivisionByN(BigInteger k, BigInteger s, BigInteger vm, byte a, int m, int c) {
        int _k = ((m + 5) / 2) + c;
        BigInteger ns = k.shiftRight(((m - _k) - 2) + a);
        BigInteger gs = s.multiply(ns);
        BigInteger hs = gs.shiftRight(m);
        BigInteger js = vm.multiply(hs);
        BigInteger gsPlusJs = gs.add(js);
        BigInteger ls = gsPlusJs.shiftRight(_k - c);
        if (gsPlusJs.testBit((_k - c) - 1)) {
            ls = ls.add(ECConstants.ONE);
        }
        return new SimpleBigDecimal(ls, c);
    }

    public static byte[] tauAdicNaf(byte mu, ZTauElement lambda) {
        if (mu != 1 && mu != -1) {
            throw new IllegalArgumentException("mu must be 1 or -1");
        }
        BigInteger norm = norm(mu, lambda);
        int log2Norm = norm.bitLength();
        int maxLength = log2Norm > 30 ? log2Norm + 4 : 34;
        byte[] u = new byte[maxLength];
        int i = 0;
        int length = 0;
        BigInteger r0 = lambda.f850u;
        BigInteger r1 = lambda.f851v;
        while (true) {
            if (!r0.equals(ECConstants.ZERO) || !r1.equals(ECConstants.ZERO)) {
                if (r0.testBit(0)) {
                    u[i] = (byte) ECConstants.TWO.subtract(r0.subtract(r1.shiftLeft(1)).mod(ECConstants.FOUR)).intValue();
                    if (u[i] == 1) {
                        r0 = r0.clearBit(0);
                    } else {
                        r0 = r0.add(ECConstants.ONE);
                    }
                    length = i;
                } else {
                    u[i] = 0;
                }
                BigInteger t = r0;
                BigInteger s = r0.shiftRight(1);
                if (mu == 1) {
                    r0 = r1.add(s);
                } else {
                    r0 = r1.subtract(s);
                }
                r1 = t.shiftRight(1).negate();
                i++;
            } else {
                int length2 = length + 1;
                byte[] tnaf = new byte[length2];
                System.arraycopy(u, 0, tnaf, 0, length2);
                return tnaf;
            }
        }
    }

    public static ECPoint.AbstractF2m tau(ECPoint.AbstractF2m p) {
        return p.tau();
    }

    public static byte getMu(ECCurve.AbstractF2m curve) {
        if (!curve.isKoblitz()) {
            throw new IllegalArgumentException("No Koblitz curve (ABC), TNAF multiplication not possible");
        }
        if (curve.getA().isZero()) {
            return (byte) -1;
        }
        return (byte) 1;
    }

    public static byte getMu(ECFieldElement curveA) {
        return (byte) (curveA.isZero() ? -1 : 1);
    }

    public static byte getMu(int curveA) {
        return (byte) (curveA == 0 ? -1 : 1);
    }

    public static BigInteger[] getLucas(byte mu, int k, boolean doV) {
        BigInteger u0;
        BigInteger u1;
        BigInteger s;
        if (mu != 1 && mu != -1) {
            throw new IllegalArgumentException("mu must be 1 or -1");
        }
        if (doV) {
            u0 = ECConstants.TWO;
            u1 = BigInteger.valueOf(mu);
        } else {
            u0 = ECConstants.ZERO;
            u1 = ECConstants.ONE;
        }
        for (int i = 1; i < k; i++) {
            if (mu == 1) {
                s = u1;
            } else {
                s = u1.negate();
            }
            BigInteger u2 = s.subtract(u0.shiftLeft(1));
            u0 = u1;
            u1 = u2;
        }
        BigInteger[] retVal = {u0, u1};
        return retVal;
    }

    public static BigInteger getTw(byte mu, int w) {
        if (w == 4) {
            if (mu == 1) {
                return BigInteger.valueOf(6L);
            }
            return BigInteger.valueOf(10L);
        }
        BigInteger[] us = getLucas(mu, w, false);
        BigInteger twoToW = ECConstants.ZERO.setBit(w);
        BigInteger u1invert = us[1].modInverse(twoToW);
        BigInteger tw = ECConstants.TWO.multiply(us[0]).multiply(u1invert).mod(twoToW);
        return tw;
    }

    public static BigInteger[] getSi(ECCurve.AbstractF2m curve) {
        if (!curve.isKoblitz()) {
            throw new IllegalArgumentException("si is defined for Koblitz curves only");
        }
        int m = curve.getFieldSize();
        int a = curve.getA().toBigInteger().intValue();
        byte mu = getMu(a);
        int shifts = getShiftsForCofactor(curve.getCofactor());
        int index = (m + 3) - a;
        BigInteger[] ui = getLucas(mu, index, false);
        if (mu == 1) {
            ui[0] = ui[0].negate();
            ui[1] = ui[1].negate();
        }
        BigInteger dividend0 = ECConstants.ONE.add(ui[1]).shiftRight(shifts);
        BigInteger dividend1 = ECConstants.ONE.add(ui[0]).shiftRight(shifts).negate();
        return new BigInteger[]{dividend0, dividend1};
    }

    public static BigInteger[] getSi(int fieldSize, int curveA, BigInteger cofactor) {
        byte mu = getMu(curveA);
        int shifts = getShiftsForCofactor(cofactor);
        int index = (fieldSize + 3) - curveA;
        BigInteger[] ui = getLucas(mu, index, false);
        if (mu == 1) {
            ui[0] = ui[0].negate();
            ui[1] = ui[1].negate();
        }
        BigInteger dividend0 = ECConstants.ONE.add(ui[1]).shiftRight(shifts);
        BigInteger dividend1 = ECConstants.ONE.add(ui[0]).shiftRight(shifts).negate();
        return new BigInteger[]{dividend0, dividend1};
    }

    protected static int getShiftsForCofactor(BigInteger h) {
        if (h != null) {
            if (h.equals(ECConstants.TWO)) {
                return 1;
            }
            if (h.equals(ECConstants.FOUR)) {
                return 2;
            }
        }
        throw new IllegalArgumentException("h (Cofactor) must be 2 or 4");
    }

    public static ZTauElement partModReduction(BigInteger k, int m, byte a, BigInteger[] s, byte mu, byte c) {
        BigInteger d0;
        if (mu == 1) {
            d0 = s[0].add(s[1]);
        } else {
            BigInteger d02 = s[0];
            d0 = d02.subtract(s[1]);
        }
        BigInteger[] v = getLucas(mu, m, true);
        BigInteger vm = v[1];
        SimpleBigDecimal lambda0 = approximateDivisionByN(k, s[0], vm, a, m, c);
        SimpleBigDecimal lambda1 = approximateDivisionByN(k, s[1], vm, a, m, c);
        ZTauElement q = round(lambda0, lambda1, mu);
        BigInteger r0 = k.subtract(d0.multiply(q.f850u)).subtract(BigInteger.valueOf(2L).multiply(s[1]).multiply(q.f851v));
        BigInteger r1 = s[1].multiply(q.f850u).subtract(s[0].multiply(q.f851v));
        return new ZTauElement(r0, r1);
    }

    public static ECPoint.AbstractF2m multiplyRTnaf(ECPoint.AbstractF2m p, BigInteger k) {
        ECCurve.AbstractF2m curve = (ECCurve.AbstractF2m) p.getCurve();
        int m = curve.getFieldSize();
        int a = curve.getA().toBigInteger().intValue();
        byte mu = getMu(a);
        BigInteger[] s = curve.getSi();
        ZTauElement rho = partModReduction(k, m, (byte) a, s, mu, (byte) 10);
        return multiplyTnaf(p, rho);
    }

    public static ECPoint.AbstractF2m multiplyTnaf(ECPoint.AbstractF2m p, ZTauElement lambda) {
        ECCurve.AbstractF2m curve = (ECCurve.AbstractF2m) p.getCurve();
        byte mu = getMu(curve.getA());
        byte[] u = tauAdicNaf(mu, lambda);
        ECPoint.AbstractF2m q = multiplyFromTnaf(p, u);
        return q;
    }

    public static ECPoint.AbstractF2m multiplyFromTnaf(ECPoint.AbstractF2m p, byte[] u) {
        ECCurve curve = p.getCurve();
        ECPoint.AbstractF2m q = (ECPoint.AbstractF2m) curve.getInfinity();
        ECPoint pNeg = (ECPoint.AbstractF2m) p.negate();
        int tauCount = 0;
        for (int i = u.length - 1; i >= 0; i--) {
            tauCount++;
            byte ui = u[i];
            if (ui != 0) {
                ECPoint.AbstractF2m q2 = q.tauPow(tauCount);
                tauCount = 0;
                ECPoint x = ui > 0 ? p : pNeg;
                q = (ECPoint.AbstractF2m) q2.add(x);
            }
        }
        if (tauCount > 0) {
            return q.tauPow(tauCount);
        }
        return q;
    }

    public static byte[] tauAdicWNaf(byte mu, ZTauElement lambda, byte width, BigInteger pow2w, BigInteger tw, ZTauElement[] alpha) {
        byte uLocal;
        if (mu != 1 && mu != -1) {
            throw new IllegalArgumentException("mu must be 1 or -1");
        }
        BigInteger norm = norm(mu, lambda);
        int log2Norm = norm.bitLength();
        int maxLength = log2Norm > 30 ? log2Norm + 4 + width : width + 34;
        byte[] u = new byte[maxLength];
        BigInteger pow2wMin1 = pow2w.shiftRight(1);
        BigInteger r0 = lambda.f850u;
        BigInteger r1 = lambda.f851v;
        int i = 0;
        while (true) {
            if (!r0.equals(ECConstants.ZERO) || !r1.equals(ECConstants.ZERO)) {
                if (!r0.testBit(0)) {
                    u[i] = 0;
                } else {
                    BigInteger uUnMod = r0.add(r1.multiply(tw)).mod(pow2w);
                    if (uUnMod.compareTo(pow2wMin1) >= 0) {
                        uLocal = (byte) uUnMod.subtract(pow2w).intValue();
                    } else {
                        uLocal = (byte) uUnMod.intValue();
                    }
                    u[i] = uLocal;
                    boolean s = true;
                    if (uLocal < 0) {
                        s = false;
                        uLocal = (byte) (-uLocal);
                    }
                    if (s) {
                        BigInteger r02 = r0.subtract(alpha[uLocal].f850u);
                        r1 = r1.subtract(alpha[uLocal].f851v);
                        r0 = r02;
                    } else {
                        BigInteger r03 = r0.add(alpha[uLocal].f850u);
                        r1 = r1.add(alpha[uLocal].f851v);
                        r0 = r03;
                    }
                }
                BigInteger t = r0;
                if (mu == 1) {
                    r0 = r1.add(r0.shiftRight(1));
                } else {
                    r0 = r1.subtract(r0.shiftRight(1));
                }
                r1 = t.shiftRight(1).negate();
                i++;
            } else {
                return u;
            }
        }
    }

    public static ECPoint.AbstractF2m[] getPreComp(ECPoint.AbstractF2m p, byte a) {
        byte[][] alphaTnaf = a == 0 ? alpha0Tnaf : alpha1Tnaf;
        ECPoint.AbstractF2m[] pu = new ECPoint.AbstractF2m[(alphaTnaf.length + 1) >>> 1];
        pu[0] = p;
        int precompLen = alphaTnaf.length;
        for (int i = 3; i < precompLen; i += 2) {
            pu[i >>> 1] = multiplyFromTnaf(p, alphaTnaf[i]);
        }
        p.getCurve().normalizeAll(pu);
        return pu;
    }
}
