package com.android.internal.org.bouncycastle.math.p025ec;

import com.android.internal.org.bouncycastle.math.p025ec.ECCurve;
import com.android.internal.org.bouncycastle.math.p025ec.ECPoint;
import java.math.BigInteger;
/* renamed from: com.android.internal.org.bouncycastle.math.ec.WTauNafMultiplier */
/* loaded from: classes4.dex */
public class WTauNafMultiplier extends AbstractECMultiplier {
    static final String PRECOMP_NAME = "bc_wtnaf";

    @Override // com.android.internal.org.bouncycastle.math.p025ec.AbstractECMultiplier
    protected ECPoint multiplyPositive(ECPoint point, BigInteger k) {
        if (!(point instanceof ECPoint.AbstractF2m)) {
            throw new IllegalArgumentException("Only ECPoint.AbstractF2m can be used in WTauNafMultiplier");
        }
        ECPoint.AbstractF2m p = (ECPoint.AbstractF2m) point;
        ECCurve.AbstractF2m curve = (ECCurve.AbstractF2m) p.getCurve();
        int m = curve.getFieldSize();
        byte a = curve.getA().toBigInteger().byteValue();
        byte mu = Tnaf.getMu(a);
        BigInteger[] s = curve.getSi();
        ZTauElement rho = Tnaf.partModReduction(k, m, a, s, mu, (byte) 10);
        return multiplyWTnaf(p, rho, a, mu);
    }

    private ECPoint.AbstractF2m multiplyWTnaf(ECPoint.AbstractF2m p, ZTauElement lambda, byte a, byte mu) {
        ZTauElement[] alpha = a == 0 ? Tnaf.alpha0 : Tnaf.alpha1;
        BigInteger tw = Tnaf.getTw(mu, 4);
        byte[] u = Tnaf.tauAdicWNaf(mu, lambda, (byte) 4, BigInteger.valueOf(16L), tw, alpha);
        return multiplyFromWTnaf(p, u);
    }

    private static ECPoint.AbstractF2m multiplyFromWTnaf(final ECPoint.AbstractF2m p, byte[] u) {
        ECCurve.AbstractF2m curve = (ECCurve.AbstractF2m) p.getCurve();
        final byte a = curve.getA().toBigInteger().byteValue();
        WTauNafPreCompInfo preCompInfo = (WTauNafPreCompInfo) curve.precompute(p, PRECOMP_NAME, new PreCompCallback() { // from class: com.android.internal.org.bouncycastle.math.ec.WTauNafMultiplier.1
            @Override // com.android.internal.org.bouncycastle.math.p025ec.PreCompCallback
            public PreCompInfo precompute(PreCompInfo existing) {
                if (existing instanceof WTauNafPreCompInfo) {
                    return existing;
                }
                WTauNafPreCompInfo result = new WTauNafPreCompInfo();
                result.setPreComp(Tnaf.getPreComp(ECPoint.AbstractF2m.this, a));
                return result;
            }
        });
        ECPoint[] pu = preCompInfo.getPreComp();
        ECPoint[] puNeg = new ECPoint.AbstractF2m[pu.length];
        for (int i = 0; i < pu.length; i++) {
            puNeg[i] = (ECPoint.AbstractF2m) pu[i].negate();
        }
        ECPoint.AbstractF2m q = (ECPoint.AbstractF2m) p.getCurve().getInfinity();
        int tauCount = 0;
        for (int i2 = u.length - 1; i2 >= 0; i2--) {
            tauCount++;
            int ui = u[i2];
            if (ui != 0) {
                ECPoint.AbstractF2m q2 = q.tauPow(tauCount);
                tauCount = 0;
                ECPoint x = ui > 0 ? pu[ui >>> 1] : puNeg[(-ui) >>> 1];
                q = (ECPoint.AbstractF2m) q2.add(x);
            }
        }
        if (tauCount > 0) {
            return q.tauPow(tauCount);
        }
        return q;
    }
}
