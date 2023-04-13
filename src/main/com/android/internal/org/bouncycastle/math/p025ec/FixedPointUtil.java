package com.android.internal.org.bouncycastle.math.p025ec;

import java.math.BigInteger;
/* renamed from: com.android.internal.org.bouncycastle.math.ec.FixedPointUtil */
/* loaded from: classes4.dex */
public class FixedPointUtil {
    public static final String PRECOMP_NAME = "bc_fixed_point";

    public static int getCombSize(ECCurve c) {
        BigInteger order = c.getOrder();
        return order == null ? c.getFieldSize() + 1 : order.bitLength();
    }

    public static FixedPointPreCompInfo getFixedPointPreCompInfo(PreCompInfo preCompInfo) {
        if (preCompInfo instanceof FixedPointPreCompInfo) {
            return (FixedPointPreCompInfo) preCompInfo;
        }
        return null;
    }

    public static FixedPointPreCompInfo precompute(final ECPoint p) {
        final ECCurve c = p.getCurve();
        return (FixedPointPreCompInfo) c.precompute(p, PRECOMP_NAME, new PreCompCallback() { // from class: com.android.internal.org.bouncycastle.math.ec.FixedPointUtil.1
            @Override // com.android.internal.org.bouncycastle.math.p025ec.PreCompCallback
            public PreCompInfo precompute(PreCompInfo existing) {
                FixedPointPreCompInfo existingFP = existing instanceof FixedPointPreCompInfo ? (FixedPointPreCompInfo) existing : null;
                int bits = FixedPointUtil.getCombSize(ECCurve.this);
                int minWidth = bits > 250 ? 6 : 5;
                int n = 1 << minWidth;
                if (!checkExisting(existingFP, n)) {
                    int d = ((bits + minWidth) - 1) / minWidth;
                    ECPoint[] pow2Table = new ECPoint[minWidth + 1];
                    pow2Table[0] = p;
                    for (int i = 1; i < minWidth; i++) {
                        pow2Table[i] = pow2Table[i - 1].timesPow2(d);
                    }
                    pow2Table[minWidth] = pow2Table[0].subtract(pow2Table[1]);
                    ECCurve.this.normalizeAll(pow2Table);
                    ECPoint[] lookupTable = new ECPoint[n];
                    lookupTable[0] = pow2Table[0];
                    for (int bit = minWidth - 1; bit >= 0; bit--) {
                        ECPoint pow2 = pow2Table[bit];
                        int step = 1 << bit;
                        for (int i2 = step; i2 < n; i2 += step << 1) {
                            lookupTable[i2] = lookupTable[i2 - step].add(pow2);
                        }
                    }
                    ECCurve.this.normalizeAll(lookupTable);
                    FixedPointPreCompInfo result = new FixedPointPreCompInfo();
                    result.setLookupTable(ECCurve.this.createCacheSafeLookupTable(lookupTable, 0, lookupTable.length));
                    result.setOffset(pow2Table[minWidth]);
                    result.setWidth(minWidth);
                    return result;
                }
                return existingFP;
            }

            private boolean checkExisting(FixedPointPreCompInfo existingFP, int n) {
                return existingFP != null && checkTable(existingFP.getLookupTable(), n);
            }

            private boolean checkTable(ECLookupTable table, int n) {
                return table != null && table.getSize() >= n;
            }
        });
    }
}
