package com.android.internal.org.bouncycastle.crypto.generators;

import com.android.internal.org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import com.android.internal.org.bouncycastle.crypto.AsymmetricCipherKeyPairGenerator;
import com.android.internal.org.bouncycastle.crypto.KeyGenerationParameters;
import com.android.internal.org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import com.android.internal.org.bouncycastle.crypto.params.DSAKeyGenerationParameters;
import com.android.internal.org.bouncycastle.crypto.params.DSAParameters;
import com.android.internal.org.bouncycastle.crypto.params.DSAPrivateKeyParameters;
import com.android.internal.org.bouncycastle.crypto.params.DSAPublicKeyParameters;
import com.android.internal.org.bouncycastle.math.p025ec.WNafUtil;
import com.android.internal.org.bouncycastle.util.BigIntegers;
import java.math.BigInteger;
import java.security.SecureRandom;
/* loaded from: classes4.dex */
public class DSAKeyPairGenerator implements AsymmetricCipherKeyPairGenerator {
    private static final BigInteger ONE = BigInteger.valueOf(1);
    private DSAKeyGenerationParameters param;

    @Override // com.android.internal.org.bouncycastle.crypto.AsymmetricCipherKeyPairGenerator
    public void init(KeyGenerationParameters param) {
        this.param = (DSAKeyGenerationParameters) param;
    }

    @Override // com.android.internal.org.bouncycastle.crypto.AsymmetricCipherKeyPairGenerator
    public AsymmetricCipherKeyPair generateKeyPair() {
        DSAParameters dsaParams = this.param.getParameters();
        BigInteger x = generatePrivateKey(dsaParams.getQ(), this.param.getRandom());
        BigInteger y = calculatePublicKey(dsaParams.getP(), dsaParams.getG(), x);
        return new AsymmetricCipherKeyPair((AsymmetricKeyParameter) new DSAPublicKeyParameters(y, dsaParams), (AsymmetricKeyParameter) new DSAPrivateKeyParameters(x, dsaParams));
    }

    private static BigInteger generatePrivateKey(BigInteger q, SecureRandom random) {
        BigInteger x;
        int minWeight = q.bitLength() >>> 2;
        do {
            BigInteger bigInteger = ONE;
            x = BigIntegers.createRandomInRange(bigInteger, q.subtract(bigInteger), random);
        } while (WNafUtil.getNafWeight(x) < minWeight);
        return x;
    }

    private static BigInteger calculatePublicKey(BigInteger p, BigInteger g, BigInteger x) {
        return g.modPow(x, p);
    }
}
