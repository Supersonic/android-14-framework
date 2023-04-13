package com.android.internal.org.bouncycastle.math;

import com.android.internal.org.bouncycastle.crypto.Digest;
import com.android.internal.org.bouncycastle.util.Arrays;
import com.android.internal.org.bouncycastle.util.BigIntegers;
import java.math.BigInteger;
import java.security.SecureRandom;
/* loaded from: classes4.dex */
public abstract class Primes {
    public static final int SMALL_FACTOR_LIMIT = 211;
    private static final BigInteger ONE = BigInteger.valueOf(1);
    private static final BigInteger TWO = BigInteger.valueOf(2);
    private static final BigInteger THREE = BigInteger.valueOf(3);

    /* loaded from: classes4.dex */
    public static class MROutput {
        private BigInteger factor;
        private boolean provablyComposite;

        /* JADX INFO: Access modifiers changed from: private */
        public static MROutput probablyPrime() {
            return new MROutput(false, null);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public static MROutput provablyCompositeWithFactor(BigInteger factor) {
            return new MROutput(true, factor);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public static MROutput provablyCompositeNotPrimePower() {
            return new MROutput(true, null);
        }

        private MROutput(boolean provablyComposite, BigInteger factor) {
            this.provablyComposite = provablyComposite;
            this.factor = factor;
        }

        public BigInteger getFactor() {
            return this.factor;
        }

        public boolean isProvablyComposite() {
            return this.provablyComposite;
        }

        public boolean isNotPrimePower() {
            return this.provablyComposite && this.factor == null;
        }
    }

    /* loaded from: classes4.dex */
    public static class STOutput {
        private BigInteger prime;
        private int primeGenCounter;
        private byte[] primeSeed;

        private STOutput(BigInteger prime, byte[] primeSeed, int primeGenCounter) {
            this.prime = prime;
            this.primeSeed = primeSeed;
            this.primeGenCounter = primeGenCounter;
        }

        public BigInteger getPrime() {
            return this.prime;
        }

        public byte[] getPrimeSeed() {
            return this.primeSeed;
        }

        public int getPrimeGenCounter() {
            return this.primeGenCounter;
        }
    }

    public static STOutput generateSTRandomPrime(Digest hash, int length, byte[] inputSeed) {
        if (hash == null) {
            throw new IllegalArgumentException("'hash' cannot be null");
        }
        if (length < 2) {
            throw new IllegalArgumentException("'length' must be >= 2");
        }
        if (inputSeed == null || inputSeed.length == 0) {
            throw new IllegalArgumentException("'inputSeed' cannot be null or empty");
        }
        return implSTRandomPrime(hash, length, Arrays.clone(inputSeed));
    }

    public static MROutput enhancedMRProbablePrimeTest(BigInteger candidate, SecureRandom random, int iterations) {
        checkCandidate(candidate, "candidate");
        if (random == null) {
            throw new IllegalArgumentException("'random' cannot be null");
        }
        if (iterations < 1) {
            throw new IllegalArgumentException("'iterations' must be > 0");
        }
        if (candidate.bitLength() == 2) {
            return MROutput.probablyPrime();
        }
        if (!candidate.testBit(0)) {
            return MROutput.provablyCompositeWithFactor(TWO);
        }
        BigInteger wSubOne = candidate.subtract(ONE);
        BigInteger wSubTwo = candidate.subtract(TWO);
        int a = wSubOne.getLowestSetBit();
        BigInteger m = wSubOne.shiftRight(a);
        for (int i = 0; i < iterations; i++) {
            BigInteger b = BigIntegers.createRandomInRange(TWO, wSubTwo, random);
            BigInteger g = b.gcd(candidate);
            BigInteger bigInteger = ONE;
            if (g.compareTo(bigInteger) > 0) {
                return MROutput.provablyCompositeWithFactor(g);
            }
            BigInteger z = b.modPow(m, candidate);
            if (!z.equals(bigInteger) && !z.equals(wSubOne)) {
                boolean primeToBase = false;
                BigInteger x = z;
                int j = 1;
                while (true) {
                    if (j >= a) {
                        break;
                    }
                    z = z.modPow(TWO, candidate);
                    if (z.equals(wSubOne)) {
                        primeToBase = true;
                        break;
                    } else if (z.equals(ONE)) {
                        break;
                    } else {
                        x = z;
                        j++;
                    }
                }
                if (!primeToBase) {
                    BigInteger bigInteger2 = ONE;
                    if (!z.equals(bigInteger2)) {
                        x = z;
                        BigInteger z2 = z.modPow(TWO, candidate);
                        if (!z2.equals(bigInteger2)) {
                            x = z2;
                        }
                    }
                    BigInteger g2 = x.subtract(bigInteger2).gcd(candidate);
                    if (g2.compareTo(bigInteger2) > 0) {
                        return MROutput.provablyCompositeWithFactor(g2);
                    }
                    return MROutput.provablyCompositeNotPrimePower();
                }
            }
        }
        return MROutput.probablyPrime();
    }

    public static boolean hasAnySmallFactors(BigInteger candidate) {
        checkCandidate(candidate, "candidate");
        return implHasAnySmallFactors(candidate);
    }

    public static boolean isMRProbablePrime(BigInteger candidate, SecureRandom random, int iterations) {
        checkCandidate(candidate, "candidate");
        if (random == null) {
            throw new IllegalArgumentException("'random' cannot be null");
        }
        if (iterations < 1) {
            throw new IllegalArgumentException("'iterations' must be > 0");
        }
        if (candidate.bitLength() == 2) {
            return true;
        }
        if (!candidate.testBit(0)) {
            return false;
        }
        BigInteger wSubOne = candidate.subtract(ONE);
        BigInteger wSubTwo = candidate.subtract(TWO);
        int a = wSubOne.getLowestSetBit();
        BigInteger m = wSubOne.shiftRight(a);
        for (int i = 0; i < iterations; i++) {
            BigInteger b = BigIntegers.createRandomInRange(TWO, wSubTwo, random);
            if (!implMRProbablePrimeToBase(candidate, wSubOne, m, a, b)) {
                return false;
            }
        }
        return true;
    }

    public static boolean isMRProbablePrimeToBase(BigInteger candidate, BigInteger base) {
        checkCandidate(candidate, "candidate");
        checkCandidate(base, "base");
        BigInteger bigInteger = ONE;
        if (base.compareTo(candidate.subtract(bigInteger)) >= 0) {
            throw new IllegalArgumentException("'base' must be < ('candidate' - 1)");
        }
        if (candidate.bitLength() == 2) {
            return true;
        }
        BigInteger wSubOne = candidate.subtract(bigInteger);
        int a = wSubOne.getLowestSetBit();
        BigInteger m = wSubOne.shiftRight(a);
        return implMRProbablePrimeToBase(candidate, wSubOne, m, a, base);
    }

    private static void checkCandidate(BigInteger n, String name) {
        if (n == null || n.signum() < 1 || n.bitLength() < 2) {
            throw new IllegalArgumentException("'" + name + "' must be non-null and >= 2");
        }
    }

    private static boolean implHasAnySmallFactors(BigInteger x) {
        int r = x.mod(BigInteger.valueOf(223092870)).intValue();
        if (r % 2 == 0 || r % 3 == 0 || r % 5 == 0 || r % 7 == 0 || r % 11 == 0 || r % 13 == 0 || r % 17 == 0 || r % 19 == 0 || r % 23 == 0) {
            return true;
        }
        int r2 = x.mod(BigInteger.valueOf(58642669)).intValue();
        if (r2 % 29 == 0 || r2 % 31 == 0 || r2 % 37 == 0 || r2 % 41 == 0 || r2 % 43 == 0) {
            return true;
        }
        int r3 = x.mod(BigInteger.valueOf(600662303)).intValue();
        if (r3 % 47 == 0 || r3 % 53 == 0 || r3 % 59 == 0 || r3 % 61 == 0 || r3 % 67 == 0) {
            return true;
        }
        int r4 = x.mod(BigInteger.valueOf(33984931)).intValue();
        if (r4 % 71 == 0 || r4 % 73 == 0 || r4 % 79 == 0 || r4 % 83 == 0) {
            return true;
        }
        int r5 = x.mod(BigInteger.valueOf(89809099)).intValue();
        if (r5 % 89 == 0 || r5 % 97 == 0 || r5 % 101 == 0 || r5 % 103 == 0) {
            return true;
        }
        int r6 = x.mod(BigInteger.valueOf(167375713)).intValue();
        if (r6 % 107 == 0 || r6 % 109 == 0 || r6 % 113 == 0 || r6 % 127 == 0) {
            return true;
        }
        int r7 = x.mod(BigInteger.valueOf(371700317)).intValue();
        if (r7 % 131 == 0 || r7 % 137 == 0 || r7 % 139 == 0 || r7 % 149 == 0) {
            return true;
        }
        int r8 = x.mod(BigInteger.valueOf(645328247)).intValue();
        if (r8 % 151 == 0 || r8 % 157 == 0 || r8 % 163 == 0 || r8 % 167 == 0) {
            return true;
        }
        int r9 = x.mod(BigInteger.valueOf(1070560157)).intValue();
        if (r9 % 173 == 0 || r9 % 179 == 0 || r9 % 181 == 0 || r9 % 191 == 0) {
            return true;
        }
        int r10 = x.mod(BigInteger.valueOf(1596463769)).intValue();
        return r10 % 193 == 0 || r10 % 197 == 0 || r10 % 199 == 0 || r10 % 211 == 0;
    }

    private static boolean implMRProbablePrimeToBase(BigInteger w, BigInteger wSubOne, BigInteger m, int a, BigInteger b) {
        BigInteger z = b.modPow(m, w);
        if (z.equals(ONE) || z.equals(wSubOne)) {
            return true;
        }
        for (int j = 1; j < a; j++) {
            z = z.modPow(TWO, w);
            if (z.equals(wSubOne)) {
                return true;
            }
            if (z.equals(ONE)) {
                return false;
            }
        }
        return false;
    }

    private static STOutput implSTRandomPrime(Digest d, int length, byte[] primeSeed) {
        STOutput rec;
        BigInteger x;
        int i;
        String str;
        Digest digest = d;
        int dLen = d.getDigestSize();
        String str2 = "Too many iterations in Shawe-Taylor Random_Prime Routine";
        if (length < 33) {
            int primeGenCounter = 0;
            byte[] c0 = new byte[dLen];
            byte[] c1 = new byte[dLen];
            do {
                hash(digest, primeSeed, c0, 0);
                inc(primeSeed, 1);
                hash(digest, primeSeed, c1, 0);
                inc(primeSeed, 1);
                int c = extract32(c0) ^ extract32(c1);
                primeGenCounter++;
                long c64 = ((c & ((-1) >>> (32 - length))) | (1 << (length - 1)) | 1) & 4294967295L;
                if (isPrime32(c64)) {
                    return new STOutput(BigInteger.valueOf(c64), primeSeed, primeGenCounter);
                }
            } while (primeGenCounter <= length * 4);
            throw new IllegalStateException("Too many iterations in Shawe-Taylor Random_Prime Routine");
        }
        STOutput rec2 = implSTRandomPrime(digest, (length + 3) / 2, primeSeed);
        BigInteger c02 = rec2.getPrime();
        byte[] primeSeed2 = rec2.getPrimeSeed();
        int primeGenCounter2 = rec2.getPrimeGenCounter();
        int outlen = dLen * 8;
        int iterations = (length - 1) / outlen;
        BigInteger x2 = hashGen(digest, primeSeed2, iterations + 1);
        BigInteger bigInteger = ONE;
        BigInteger x3 = x2.mod(bigInteger.shiftLeft(length - 1)).setBit(length - 1);
        BigInteger c0x2 = c02.shiftLeft(1);
        BigInteger tx2 = x3.subtract(bigInteger).divide(c0x2).add(bigInteger).shiftLeft(1);
        BigInteger c2 = tx2.multiply(c02).add(bigInteger);
        int dt = 0;
        while (true) {
            int dt2 = dLen;
            if (c2.bitLength() <= length) {
                rec = rec2;
                x = x3;
                i = 1;
            } else {
                BigInteger bigInteger2 = ONE;
                rec = rec2;
                x = x3;
                i = 1;
                tx2 = bigInteger2.shiftLeft(length - 1).subtract(bigInteger2).divide(c0x2).add(bigInteger2).shiftLeft(1);
                c2 = tx2.multiply(c02).add(bigInteger2);
            }
            primeGenCounter2 += i;
            if (!implHasAnySmallFactors(c2)) {
                BigInteger a = hashGen(digest, primeSeed2, iterations + 1);
                BigInteger a2 = a.mod(c2.subtract(THREE)).add(TWO);
                str = str2;
                BigInteger tx22 = tx2.add(BigInteger.valueOf(dt));
                dt = 0;
                BigInteger z = a2.modPow(tx22, c2);
                BigInteger bigInteger3 = ONE;
                if (c2.gcd(z.subtract(bigInteger3)).equals(bigInteger3) && z.modPow(c02, c2).equals(bigInteger3)) {
                    return new STOutput(c2, primeSeed2, primeGenCounter2);
                }
                tx2 = tx22;
            } else {
                str = str2;
                inc(primeSeed2, iterations + 1);
            }
            if (primeGenCounter2 >= (length * 4) + primeGenCounter2) {
                throw new IllegalStateException(str);
            }
            dt += 2;
            c2 = c2.add(c0x2);
            digest = d;
            str2 = str;
            dLen = dt2;
            rec2 = rec;
            x3 = x;
        }
    }

    private static int extract32(byte[] bs) {
        int result = 0;
        int count = Math.min(4, bs.length);
        for (int i = 0; i < count; i++) {
            int b = bs[bs.length - (i + 1)] & 255;
            result |= b << (i * 8);
        }
        return result;
    }

    private static void hash(Digest d, byte[] input, byte[] output, int outPos) {
        d.update(input, 0, input.length);
        d.doFinal(output, outPos);
    }

    private static BigInteger hashGen(Digest d, byte[] seed, int count) {
        int dLen = d.getDigestSize();
        int pos = count * dLen;
        byte[] buf = new byte[pos];
        for (int i = 0; i < count; i++) {
            pos -= dLen;
            hash(d, seed, buf, pos);
            inc(seed, 1);
        }
        return new BigInteger(1, buf);
    }

    private static void inc(byte[] seed, int c) {
        int pos = seed.length;
        while (c > 0) {
            pos--;
            if (pos >= 0) {
                int c2 = c + (seed[pos] & 255);
                seed[pos] = (byte) c2;
                c = c2 >>> 8;
            } else {
                return;
            }
        }
    }

    private static boolean isPrime32(long x) {
        if ((x >>> 32) != 0) {
            throw new IllegalArgumentException("Size limit exceeded");
        }
        if (x <= 5) {
            return x == 2 || x == 3 || x == 5;
        } else if ((1 & x) == 0 || x % 3 == 0 || x % 5 == 0) {
            return false;
        } else {
            long[] ds = {1, 7, 11, 13, 17, 19, 23, 29};
            long base = 0;
            int pos = 1;
            while (true) {
                if (pos < ds.length) {
                    long d = ds[pos] + base;
                    if (x % d == 0) {
                        return x < 30;
                    }
                    pos++;
                } else {
                    base += 30;
                    if (base * base >= x) {
                        return true;
                    }
                    pos = 0;
                }
            }
        }
    }
}
