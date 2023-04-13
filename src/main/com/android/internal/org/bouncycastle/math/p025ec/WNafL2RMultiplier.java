package com.android.internal.org.bouncycastle.math.p025ec;

import com.android.internal.org.bouncycastle.util.Integers;
import java.math.BigInteger;
/* renamed from: com.android.internal.org.bouncycastle.math.ec.WNafL2RMultiplier */
/* loaded from: classes4.dex */
public class WNafL2RMultiplier extends AbstractECMultiplier {
    @Override // com.android.internal.org.bouncycastle.math.p025ec.AbstractECMultiplier
    protected ECPoint multiplyPositive(ECPoint p, BigInteger k) {
        ECPoint R;
        int minWidth = WNafUtil.getWindowSize(k.bitLength());
        WNafPreCompInfo info = WNafUtil.precompute(p, minWidth, true);
        ECPoint[] preComp = info.getPreComp();
        ECPoint[] preCompNeg = info.getPreCompNeg();
        int width = info.getWidth();
        int[] wnaf = WNafUtil.generateCompactWindowNaf(width, k);
        ECPoint R2 = p.getCurve().getInfinity();
        int i = wnaf.length;
        if (i > 1) {
            i--;
            int wi = wnaf[i];
            int digit = wi >> 16;
            int zeroes = wi & 65535;
            int n = Math.abs(digit);
            ECPoint[] table = digit < 0 ? preCompNeg : preComp;
            int minWidth2 = 1 << width;
            if ((n << 2) < minWidth2) {
                int highest = 32 - Integers.numberOfLeadingZeros(n);
                int scale = width - highest;
                int lowBits = n ^ (1 << (highest - 1));
                int i1 = (1 << (width - 1)) - 1;
                int i2 = (lowBits << scale) + 1;
                R = table[i1 >>> 1].add(table[i2 >>> 1]);
                zeroes -= scale;
            } else {
                R = table[n >>> 1];
            }
            R2 = R.timesPow2(zeroes);
        }
        while (i > 0) {
            i--;
            int wi2 = wnaf[i];
            int digit2 = wi2 >> 16;
            int zeroes2 = wi2 & 65535;
            ECPoint r = (digit2 < 0 ? preCompNeg : preComp)[Math.abs(digit2) >>> 1];
            R2 = R2.twicePlus(r).timesPow2(zeroes2);
        }
        return R2;
    }
}
