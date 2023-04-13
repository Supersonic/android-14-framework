package com.android.internal.org.bouncycastle.crypto.generators;

import android.content.p001pm.PackageManager;
import com.android.internal.org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import com.android.internal.org.bouncycastle.crypto.AsymmetricCipherKeyPairGenerator;
import com.android.internal.org.bouncycastle.crypto.KeyGenerationParameters;
import com.android.internal.org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import com.android.internal.org.bouncycastle.crypto.params.RSAKeyGenerationParameters;
import com.android.internal.org.bouncycastle.crypto.params.RSAKeyParameters;
import com.android.internal.org.bouncycastle.crypto.params.RSAPrivateCrtKeyParameters;
import com.android.internal.org.bouncycastle.math.Primes;
import com.android.internal.org.bouncycastle.math.p025ec.WNafUtil;
import com.android.internal.org.bouncycastle.util.BigIntegers;
import java.math.BigInteger;
/* loaded from: classes4.dex */
public class RSAKeyPairGenerator implements AsymmetricCipherKeyPairGenerator {
    private static final BigInteger ONE = BigInteger.valueOf(1);
    private RSAKeyGenerationParameters param;

    @Override // com.android.internal.org.bouncycastle.crypto.AsymmetricCipherKeyPairGenerator
    public void init(KeyGenerationParameters param) {
        this.param = (RSAKeyGenerationParameters) param;
    }

    @Override // com.android.internal.org.bouncycastle.crypto.AsymmetricCipherKeyPairGenerator
    public AsymmetricCipherKeyPair generateKeyPair() {
        BigInteger q;
        BigInteger n;
        boolean done;
        BigInteger gcd;
        BigInteger q2;
        RSAKeyPairGenerator rSAKeyPairGenerator = this;
        AsymmetricCipherKeyPair result = null;
        boolean done2 = false;
        int strength = rSAKeyPairGenerator.param.getStrength();
        int pbitlength = (strength + 1) / 2;
        int qbitlength = strength - pbitlength;
        int mindiffbits = (strength / 2) - 100;
        if (mindiffbits < strength / 3) {
            mindiffbits = strength / 3;
        }
        int minWeight = strength >> 2;
        BigInteger dLowerBound = BigInteger.valueOf(2L).pow(strength / 2);
        BigInteger bigInteger = ONE;
        BigInteger squaredBound = bigInteger.shiftLeft(strength - 1);
        BigInteger minDiff = bigInteger.shiftLeft(mindiffbits);
        while (!done2) {
            BigInteger e = rSAKeyPairGenerator.param.getPublicExponent();
            BigInteger p = rSAKeyPairGenerator.chooseRandomPrime(pbitlength, e, squaredBound);
            while (true) {
                q = rSAKeyPairGenerator.chooseRandomPrime(qbitlength, e, squaredBound);
                BigInteger diff = q.subtract(p).abs();
                if (diff.bitLength() < mindiffbits || diff.compareTo(minDiff) <= 0) {
                    rSAKeyPairGenerator = this;
                    done2 = done2;
                    strength = strength;
                    pbitlength = pbitlength;
                    qbitlength = qbitlength;
                } else {
                    n = p.multiply(q);
                    done = done2;
                    if (n.bitLength() != strength) {
                        p = p.max(q);
                        done2 = done;
                    } else if (WNafUtil.getNafWeight(n) >= minWeight) {
                        break;
                    } else {
                        p = rSAKeyPairGenerator.chooseRandomPrime(pbitlength, e, squaredBound);
                        done2 = done;
                    }
                }
            }
            if (p.compareTo(q) >= 0) {
                gcd = p;
                q2 = q;
            } else {
                BigInteger gcd2 = p;
                gcd = q;
                q2 = gcd2;
            }
            BigInteger p2 = ONE;
            BigInteger pSub1 = gcd.subtract(p2);
            BigInteger qSub1 = q2.subtract(p2);
            BigInteger gcd3 = pSub1.gcd(qSub1);
            int strength2 = strength;
            BigInteger lcm = pSub1.divide(gcd3).multiply(qSub1);
            BigInteger d = e.modInverse(lcm);
            if (d.compareTo(dLowerBound) <= 0) {
                rSAKeyPairGenerator = this;
                done2 = done;
                strength = strength2;
            } else {
                BigInteger dP = d.remainder(pSub1);
                BigInteger dQ = d.remainder(qSub1);
                BigInteger qInv = BigIntegers.modOddInverse(gcd, q2);
                result = new AsymmetricCipherKeyPair((AsymmetricKeyParameter) new RSAKeyParameters(false, n, e), (AsymmetricKeyParameter) new RSAPrivateCrtKeyParameters(n, e, d, gcd, q2, dP, dQ, qInv));
                rSAKeyPairGenerator = this;
                strength = strength2;
                done2 = true;
                pbitlength = pbitlength;
                qbitlength = qbitlength;
            }
        }
        return result;
    }

    protected BigInteger chooseRandomPrime(int bitlength, BigInteger e, BigInteger sqrdBound) {
        for (int i = 0; i != bitlength * 5; i++) {
            BigInteger p = BigIntegers.createRandomPrime(bitlength, 1, this.param.getRandom());
            BigInteger mod = p.mod(e);
            BigInteger bigInteger = ONE;
            if (!mod.equals(bigInteger) && p.multiply(p).compareTo(sqrdBound) >= 0 && isProbablePrime(p) && e.gcd(p.subtract(bigInteger)).equals(bigInteger)) {
                return p;
            }
        }
        throw new IllegalStateException("unable to generate prime number for RSA key");
    }

    protected boolean isProbablePrime(BigInteger x) {
        int iterations = getNumberOfIterations(x.bitLength(), this.param.getCertainty());
        return !Primes.hasAnySmallFactors(x) && Primes.isMRProbablePrime(x, this.param.getRandom(), iterations);
    }

    private static int getNumberOfIterations(int bits, int certainty) {
        if (bits >= 1536) {
            if (certainty <= 100) {
                return 3;
            }
            if (certainty <= 128) {
                return 4;
            }
            return 4 + (((certainty - 128) + 1) / 2);
        } else if (bits >= 1024) {
            if (certainty <= 100) {
                return 4;
            }
            if (certainty <= 112) {
                return 5;
            }
            return (((certainty + PackageManager.INSTALL_FAILED_DUPLICATE_PERMISSION) + 1) / 2) + 5;
        } else if (bits < 512) {
            if (certainty <= 80) {
                return 40;
            }
            return 40 + (((certainty - 80) + 1) / 2);
        } else if (certainty <= 80) {
            return 5;
        } else {
            if (certainty <= 100) {
                return 7;
            }
            return 7 + (((certainty - 100) + 1) / 2);
        }
    }
}
