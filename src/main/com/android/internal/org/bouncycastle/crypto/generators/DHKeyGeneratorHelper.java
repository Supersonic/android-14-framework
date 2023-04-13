package com.android.internal.org.bouncycastle.crypto.generators;

import com.android.internal.org.bouncycastle.crypto.params.DHParameters;
import com.android.internal.org.bouncycastle.math.p025ec.WNafUtil;
import com.android.internal.org.bouncycastle.util.BigIntegers;
import java.math.BigInteger;
import java.security.SecureRandom;
/* loaded from: classes4.dex */
class DHKeyGeneratorHelper {
    static final DHKeyGeneratorHelper INSTANCE = new DHKeyGeneratorHelper();
    private static final BigInteger ONE = BigInteger.valueOf(1);
    private static final BigInteger TWO = BigInteger.valueOf(2);

    private DHKeyGeneratorHelper() {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public BigInteger calculatePrivate(DHParameters dhParams, SecureRandom random) {
        BigInteger x;
        BigInteger x2;
        int limit = dhParams.getL();
        if (limit != 0) {
            int minWeight = limit >>> 2;
            do {
                x2 = BigIntegers.createRandomBigInteger(limit, random).setBit(limit - 1);
            } while (WNafUtil.getNafWeight(x2) < minWeight);
            return x2;
        }
        BigInteger min = TWO;
        int m = dhParams.getM();
        if (m != 0) {
            min = ONE.shiftLeft(m - 1);
        }
        BigInteger q = dhParams.getQ();
        if (q == null) {
            q = dhParams.getP();
        }
        BigInteger max = q.subtract(TWO);
        int minWeight2 = max.bitLength() >>> 2;
        do {
            x = BigIntegers.createRandomInRange(min, max, random);
        } while (WNafUtil.getNafWeight(x) < minWeight2);
        return x;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public BigInteger calculatePublic(DHParameters dhParams, BigInteger x) {
        return dhParams.getG().modPow(x, dhParams.getP());
    }
}
