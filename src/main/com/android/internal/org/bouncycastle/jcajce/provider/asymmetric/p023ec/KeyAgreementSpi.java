package com.android.internal.org.bouncycastle.jcajce.provider.asymmetric.p023ec;

import com.android.internal.org.bouncycastle.asn1.p018x9.X9IntegerConverter;
import com.android.internal.org.bouncycastle.crypto.BasicAgreement;
import com.android.internal.org.bouncycastle.crypto.CipherParameters;
import com.android.internal.org.bouncycastle.crypto.DerivationFunction;
import com.android.internal.org.bouncycastle.crypto.agreement.ECDHBasicAgreement;
import com.android.internal.org.bouncycastle.crypto.params.ECDomainParameters;
import com.android.internal.org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import com.android.internal.org.bouncycastle.jcajce.provider.asymmetric.util.BaseAgreementSpi;
import com.android.internal.org.bouncycastle.jcajce.provider.asymmetric.util.ECUtil;
import com.android.internal.org.bouncycastle.jcajce.spec.UserKeyingMaterialSpec;
import com.android.internal.org.bouncycastle.jce.interfaces.ECPrivateKey;
import com.android.internal.org.bouncycastle.jce.interfaces.ECPublicKey;
import com.android.internal.org.bouncycastle.util.Arrays;
import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
/* renamed from: com.android.internal.org.bouncycastle.jcajce.provider.asymmetric.ec.KeyAgreementSpi */
/* loaded from: classes4.dex */
public class KeyAgreementSpi extends BaseAgreementSpi {
    private static final X9IntegerConverter converter = new X9IntegerConverter();
    private Object agreement;
    private String kaAlgorithm;
    private ECDomainParameters parameters;
    private byte[] result;

    protected KeyAgreementSpi(String kaAlgorithm, BasicAgreement agreement, DerivationFunction kdf) {
        super(kaAlgorithm, kdf);
        this.kaAlgorithm = kaAlgorithm;
        this.agreement = agreement;
    }

    protected byte[] bigIntToBytes(BigInteger r) {
        X9IntegerConverter x9IntegerConverter = converter;
        return x9IntegerConverter.integerToBytes(r, x9IntegerConverter.getByteLength(this.parameters.getCurve()));
    }

    @Override // javax.crypto.KeyAgreementSpi
    protected Key engineDoPhase(Key key, boolean lastPhase) throws InvalidKeyException, IllegalStateException {
        if (this.parameters == null) {
            throw new IllegalStateException(this.kaAlgorithm + " not initialised.");
        }
        if (!lastPhase) {
            throw new IllegalStateException(this.kaAlgorithm + " can only be between two parties.");
        }
        if (!(key instanceof PublicKey)) {
            throw new InvalidKeyException(this.kaAlgorithm + " key agreement requires " + getSimpleName(ECPublicKey.class) + " for doPhase");
        }
        CipherParameters pubKey = ECUtils.generatePublicKeyParameter((PublicKey) key);
        try {
            this.result = bigIntToBytes(((BasicAgreement) this.agreement).calculateAgreement(pubKey));
            return null;
        } catch (Exception e) {
            throw new InvalidKeyException("calculation failed: " + e.getMessage()) { // from class: com.android.internal.org.bouncycastle.jcajce.provider.asymmetric.ec.KeyAgreementSpi.1
                @Override // java.lang.Throwable
                public Throwable getCause() {
                    return e;
                }
            };
        }
    }

    @Override // javax.crypto.KeyAgreementSpi
    protected void engineInit(Key key, AlgorithmParameterSpec params, SecureRandom random) throws InvalidKeyException, InvalidAlgorithmParameterException {
        if (params != null && !(params instanceof UserKeyingMaterialSpec)) {
            throw new InvalidAlgorithmParameterException("No algorithm parameters supported");
        }
        initFromKey(key, params);
    }

    @Override // javax.crypto.KeyAgreementSpi
    protected void engineInit(Key key, SecureRandom random) throws InvalidKeyException {
        try {
            initFromKey(key, null);
        } catch (InvalidAlgorithmParameterException e) {
            throw new InvalidKeyException(e.getMessage());
        }
    }

    private void initFromKey(Key key, AlgorithmParameterSpec parameterSpec) throws InvalidKeyException, InvalidAlgorithmParameterException {
        if (!(key instanceof PrivateKey)) {
            throw new InvalidKeyException(this.kaAlgorithm + " key agreement requires " + getSimpleName(ECPrivateKey.class) + " for initialisation");
        }
        if (this.kdf == null && (parameterSpec instanceof UserKeyingMaterialSpec)) {
            throw new InvalidAlgorithmParameterException("no KDF specified for UserKeyingMaterialSpec");
        }
        ECPrivateKeyParameters privKey = (ECPrivateKeyParameters) ECUtil.generatePrivateKeyParameter((PrivateKey) key);
        this.parameters = privKey.getParameters();
        this.ukmParameters = parameterSpec instanceof UserKeyingMaterialSpec ? ((UserKeyingMaterialSpec) parameterSpec).getUserKeyingMaterial() : null;
        ((BasicAgreement) this.agreement).init(privKey);
    }

    private static String getSimpleName(Class clazz) {
        String fullName = clazz.getName();
        return fullName.substring(fullName.lastIndexOf(46) + 1);
    }

    @Override // com.android.internal.org.bouncycastle.jcajce.provider.asymmetric.util.BaseAgreementSpi
    protected byte[] calcSecret() {
        return Arrays.clone(this.result);
    }

    /* renamed from: com.android.internal.org.bouncycastle.jcajce.provider.asymmetric.ec.KeyAgreementSpi$DH */
    /* loaded from: classes4.dex */
    public static class C4277DH extends KeyAgreementSpi {
        public C4277DH() {
            super("ECDH", new ECDHBasicAgreement(), null);
        }
    }
}
