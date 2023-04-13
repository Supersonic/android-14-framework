package com.android.internal.org.bouncycastle.math.p025ec;

import com.android.internal.org.bouncycastle.math.raw.Nat;
import java.math.BigInteger;
/* renamed from: com.android.internal.org.bouncycastle.math.ec.FixedPointCombMultiplier */
/* loaded from: classes4.dex */
public class FixedPointCombMultiplier extends AbstractECMultiplier {
    @Override // com.android.internal.org.bouncycastle.math.p025ec.AbstractECMultiplier
    protected ECPoint multiplyPositive(ECPoint p, BigInteger k) {
        ECCurve c = p.getCurve();
        int size = FixedPointUtil.getCombSize(c);
        if (k.bitLength() <= size) {
            FixedPointPreCompInfo info = FixedPointUtil.precompute(p);
            ECLookupTable lookupTable = info.getLookupTable();
            int width = info.getWidth();
            int d = ((size + width) - 1) / width;
            ECPoint R = c.getInfinity();
            int fullComb = d * width;
            int[] K = Nat.fromBigInteger(fullComb, k);
            int top = fullComb - 1;
            for (int i = 0; i < d; i++) {
                int secretIndex = 0;
                for (int j = top - i; j >= 0; j -= d) {
                    int secretBit = K[j >>> 5] >>> (j & 31);
                    secretIndex = ((secretIndex ^ (secretBit >>> 1)) << 1) ^ secretBit;
                }
                ECPoint add = lookupTable.lookup(secretIndex);
                R = R.twicePlus(add);
            }
            return R.add(info.getOffset());
        }
        throw new IllegalStateException("fixed-point comb doesn't support scalars larger than the curve order");
    }
}
