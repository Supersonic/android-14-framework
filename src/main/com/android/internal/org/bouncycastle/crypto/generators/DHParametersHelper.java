package com.android.internal.org.bouncycastle.crypto.generators;

import com.android.internal.org.bouncycastle.math.p025ec.WNafUtil;
import com.android.internal.org.bouncycastle.util.BigIntegers;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.logging.Logger;
/* loaded from: classes4.dex */
class DHParametersHelper {
    private static final Logger logger = Logger.getLogger(DHParametersHelper.class.getName());
    private static final BigInteger ONE = BigInteger.valueOf(1);
    private static final BigInteger TWO = BigInteger.valueOf(2);

    DHParametersHelper() {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static BigInteger[] generateSafePrimes(int size, int certainty, SecureRandom random) {
        logger.info("Generating safe primes. This may take a long time.");
        long start = System.currentTimeMillis();
        int tries = 0;
        int qLength = size - 1;
        int minWeight = size >>> 2;
        while (true) {
            tries++;
            BigInteger q = BigIntegers.createRandomPrime(qLength, 2, random);
            BigInteger p = q.shiftLeft(1).add(ONE);
            if (p.isProbablePrime(certainty) && (certainty <= 2 || q.isProbablePrime(certainty - 2))) {
                if (WNafUtil.getNafWeight(p) >= minWeight) {
                    long end = System.currentTimeMillis();
                    long duration = end - start;
                    logger.info("Generated safe primes: " + tries + " tries took " + duration + "ms");
                    return new BigInteger[]{p, q};
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static BigInteger selectGenerator(BigInteger p, BigInteger q, SecureRandom random) {
        BigInteger g;
        BigInteger h;
        BigInteger pMinusTwo = p.subtract(TWO);
        do {
            BigInteger bigInteger = TWO;
            BigInteger h2 = BigIntegers.createRandomInRange(bigInteger, pMinusTwo, random);
            g = h2.modPow(bigInteger, p);
            h = ONE;
        } while (g.equals(h));
        return g;
    }
}
