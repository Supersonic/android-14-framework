package com.android.internal.org.bouncycastle.crypto.engines;

import com.android.internal.org.bouncycastle.crypto.AsymmetricBlockCipher;
import com.android.internal.org.bouncycastle.crypto.CipherParameters;
import com.android.internal.org.bouncycastle.crypto.CryptoServicesRegistrar;
import com.android.internal.org.bouncycastle.crypto.params.ParametersWithRandom;
import com.android.internal.org.bouncycastle.crypto.params.RSAKeyParameters;
import com.android.internal.org.bouncycastle.crypto.params.RSAPrivateCrtKeyParameters;
import com.android.internal.org.bouncycastle.util.BigIntegers;
import java.math.BigInteger;
import java.security.SecureRandom;
/* loaded from: classes4.dex */
public class RSABlindedEngine implements AsymmetricBlockCipher {
    private static final BigInteger ONE = BigInteger.valueOf(1);
    private RSACoreEngine core = new RSACoreEngine();
    private RSAKeyParameters key;
    private SecureRandom random;

    @Override // com.android.internal.org.bouncycastle.crypto.AsymmetricBlockCipher
    public void init(boolean forEncryption, CipherParameters param) {
        this.core.init(forEncryption, param);
        if (param instanceof ParametersWithRandom) {
            ParametersWithRandom rParam = (ParametersWithRandom) param;
            RSAKeyParameters rSAKeyParameters = (RSAKeyParameters) rParam.getParameters();
            this.key = rSAKeyParameters;
            if (rSAKeyParameters instanceof RSAPrivateCrtKeyParameters) {
                this.random = rParam.getRandom();
                return;
            } else {
                this.random = null;
                return;
            }
        }
        RSAKeyParameters rSAKeyParameters2 = (RSAKeyParameters) param;
        this.key = rSAKeyParameters2;
        if (rSAKeyParameters2 instanceof RSAPrivateCrtKeyParameters) {
            this.random = CryptoServicesRegistrar.getSecureRandom();
        } else {
            this.random = null;
        }
    }

    @Override // com.android.internal.org.bouncycastle.crypto.AsymmetricBlockCipher
    public int getInputBlockSize() {
        return this.core.getInputBlockSize();
    }

    @Override // com.android.internal.org.bouncycastle.crypto.AsymmetricBlockCipher
    public int getOutputBlockSize() {
        return this.core.getOutputBlockSize();
    }

    @Override // com.android.internal.org.bouncycastle.crypto.AsymmetricBlockCipher
    public byte[] processBlock(byte[] in, int inOff, int inLen) {
        BigInteger result;
        if (this.key == null) {
            throw new IllegalStateException("RSA engine not initialised");
        }
        BigInteger input = this.core.convertInput(in, inOff, inLen);
        RSAKeyParameters rSAKeyParameters = this.key;
        if (rSAKeyParameters instanceof RSAPrivateCrtKeyParameters) {
            RSAPrivateCrtKeyParameters k = (RSAPrivateCrtKeyParameters) rSAKeyParameters;
            BigInteger e = k.getPublicExponent();
            if (e != null) {
                BigInteger m = k.getModulus();
                BigInteger bigInteger = ONE;
                BigInteger r = BigIntegers.createRandomInRange(bigInteger, m.subtract(bigInteger), this.random);
                BigInteger blindedInput = r.modPow(e, m).multiply(input).mod(m);
                BigInteger blindedResult = this.core.processBlock(blindedInput);
                BigInteger rInv = BigIntegers.modOddInverse(m, r);
                result = blindedResult.multiply(rInv).mod(m);
                if (!input.equals(result.modPow(e, m))) {
                    throw new IllegalStateException("RSA engine faulty decryption/signing detected");
                }
            } else {
                result = this.core.processBlock(input);
            }
        } else {
            result = this.core.processBlock(input);
        }
        return this.core.convertOutput(result);
    }
}
