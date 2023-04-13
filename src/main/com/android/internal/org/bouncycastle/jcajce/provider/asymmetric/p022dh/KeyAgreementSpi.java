package com.android.internal.org.bouncycastle.jcajce.provider.asymmetric.p022dh;

import com.android.internal.org.bouncycastle.crypto.BasicAgreement;
import com.android.internal.org.bouncycastle.crypto.DerivationFunction;
import com.android.internal.org.bouncycastle.crypto.params.DHParameters;
import com.android.internal.org.bouncycastle.crypto.params.DHPrivateKeyParameters;
import com.android.internal.org.bouncycastle.crypto.params.DHPublicKeyParameters;
import com.android.internal.org.bouncycastle.jcajce.provider.asymmetric.util.BaseAgreementSpi;
import com.android.internal.org.bouncycastle.jcajce.spec.DHDomainParameterSpec;
import com.android.internal.org.bouncycastle.jcajce.spec.UserKeyingMaterialSpec;
import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import javax.crypto.SecretKey;
import javax.crypto.ShortBufferException;
import javax.crypto.interfaces.DHPrivateKey;
import javax.crypto.interfaces.DHPublicKey;
import javax.crypto.spec.DHParameterSpec;
import javax.crypto.spec.SecretKeySpec;
/* renamed from: com.android.internal.org.bouncycastle.jcajce.provider.asymmetric.dh.KeyAgreementSpi */
/* loaded from: classes4.dex */
public class KeyAgreementSpi extends BaseAgreementSpi {
    private static final BigInteger ONE = BigInteger.valueOf(1);
    private static final BigInteger TWO = BigInteger.valueOf(2);

    /* renamed from: g */
    private BigInteger f801g;
    private final BasicAgreement mqvAgreement;

    /* renamed from: p */
    private BigInteger f802p;
    private byte[] result;

    /* renamed from: x */
    private BigInteger f803x;

    public KeyAgreementSpi() {
        this("Diffie-Hellman", null);
    }

    public KeyAgreementSpi(String kaAlgorithm, DerivationFunction kdf) {
        super(kaAlgorithm, kdf);
        this.mqvAgreement = null;
    }

    public KeyAgreementSpi(String kaAlgorithm, BasicAgreement mqvAgreement, DerivationFunction kdf) {
        super(kaAlgorithm, kdf);
        this.mqvAgreement = mqvAgreement;
    }

    protected byte[] bigIntToBytes(BigInteger r) {
        int expectedLength = (this.f802p.bitLength() + 7) / 8;
        byte[] tmp = r.toByteArray();
        if (tmp.length == expectedLength) {
            return tmp;
        }
        if (tmp[0] == 0 && tmp.length == expectedLength + 1) {
            byte[] rv = new byte[tmp.length - 1];
            System.arraycopy(tmp, 1, rv, 0, rv.length);
            return rv;
        }
        byte[] rv2 = new byte[expectedLength];
        System.arraycopy(tmp, 0, rv2, rv2.length - tmp.length, tmp.length);
        return rv2;
    }

    @Override // javax.crypto.KeyAgreementSpi
    protected Key engineDoPhase(Key key, boolean lastPhase) throws InvalidKeyException, IllegalStateException {
        if (this.f803x == null) {
            throw new IllegalStateException("Diffie-Hellman not initialised.");
        }
        if (!(key instanceof DHPublicKey)) {
            throw new InvalidKeyException("DHKeyAgreement doPhase requires DHPublicKey");
        }
        DHPublicKey pubKey = (DHPublicKey) key;
        if (!pubKey.getParams().getG().equals(this.f801g) || !pubKey.getParams().getP().equals(this.f802p)) {
            throw new InvalidKeyException("DHPublicKey not for this KeyAgreement!");
        }
        BigInteger peerY = ((DHPublicKey) key).getY();
        if (peerY != null && peerY.compareTo(TWO) >= 0) {
            BigInteger bigInteger = this.f802p;
            BigInteger bigInteger2 = ONE;
            if (peerY.compareTo(bigInteger.subtract(bigInteger2)) < 0) {
                BigInteger res = peerY.modPow(this.f803x, this.f802p);
                if (res.compareTo(bigInteger2) == 0) {
                    throw new InvalidKeyException("Shared key can't be 1");
                }
                this.result = bigIntToBytes(res);
                if (lastPhase) {
                    return null;
                }
                return new BCDHPublicKey(res, pubKey.getParams());
            }
        }
        throw new InvalidKeyException("Invalid DH PublicKey");
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.internal.org.bouncycastle.jcajce.provider.asymmetric.util.BaseAgreementSpi, javax.crypto.KeyAgreementSpi
    public byte[] engineGenerateSecret() throws IllegalStateException {
        if (this.f803x == null) {
            throw new IllegalStateException("Diffie-Hellman not initialised.");
        }
        return super.engineGenerateSecret();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.internal.org.bouncycastle.jcajce.provider.asymmetric.util.BaseAgreementSpi, javax.crypto.KeyAgreementSpi
    public int engineGenerateSecret(byte[] sharedSecret, int offset) throws IllegalStateException, ShortBufferException {
        if (this.f803x == null) {
            throw new IllegalStateException("Diffie-Hellman not initialised.");
        }
        return super.engineGenerateSecret(sharedSecret, offset);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.internal.org.bouncycastle.jcajce.provider.asymmetric.util.BaseAgreementSpi, javax.crypto.KeyAgreementSpi
    public SecretKey engineGenerateSecret(String algorithm) throws NoSuchAlgorithmException {
        if (this.f803x == null) {
            throw new IllegalStateException("Diffie-Hellman not initialised.");
        }
        if (algorithm.equals("TlsPremasterSecret")) {
            return new SecretKeySpec(trimZeroes(this.result), algorithm);
        }
        return super.engineGenerateSecret(algorithm);
    }

    @Override // javax.crypto.KeyAgreementSpi
    protected void engineInit(Key key, AlgorithmParameterSpec params, SecureRandom random) throws InvalidKeyException, InvalidAlgorithmParameterException {
        if (!(key instanceof DHPrivateKey)) {
            throw new InvalidKeyException("DHKeyAgreement requires DHPrivateKey for initialisation");
        }
        DHPrivateKey privKey = (DHPrivateKey) key;
        if (params != null) {
            if (params instanceof DHParameterSpec) {
                DHParameterSpec p = (DHParameterSpec) params;
                this.f802p = p.getP();
                this.f801g = p.getG();
                this.ukmParameters = null;
            } else if (params instanceof UserKeyingMaterialSpec) {
                if (this.kdf == null) {
                    throw new InvalidAlgorithmParameterException("no KDF specified for UserKeyingMaterialSpec");
                }
                this.f802p = privKey.getParams().getP();
                this.f801g = privKey.getParams().getG();
                this.ukmParameters = ((UserKeyingMaterialSpec) params).getUserKeyingMaterial();
            } else {
                throw new InvalidAlgorithmParameterException("DHKeyAgreement only accepts DHParameterSpec");
            }
        } else {
            this.f802p = privKey.getParams().getP();
            this.f801g = privKey.getParams().getG();
        }
        BigInteger x = privKey.getX();
        this.f803x = x;
        this.result = bigIntToBytes(x);
    }

    @Override // javax.crypto.KeyAgreementSpi
    protected void engineInit(Key key, SecureRandom random) throws InvalidKeyException {
        if (!(key instanceof DHPrivateKey)) {
            throw new InvalidKeyException("DHKeyAgreement requires DHPrivateKey");
        }
        DHPrivateKey privKey = (DHPrivateKey) key;
        this.f802p = privKey.getParams().getP();
        this.f801g = privKey.getParams().getG();
        BigInteger x = privKey.getX();
        this.f803x = x;
        this.result = bigIntToBytes(x);
    }

    @Override // com.android.internal.org.bouncycastle.jcajce.provider.asymmetric.util.BaseAgreementSpi
    protected byte[] calcSecret() {
        return this.result;
    }

    private DHPrivateKeyParameters generatePrivateKeyParameter(PrivateKey privKey) throws InvalidKeyException {
        if (privKey instanceof DHPrivateKey) {
            if (privKey instanceof BCDHPrivateKey) {
                return ((BCDHPrivateKey) privKey).engineGetKeyParameters();
            }
            DHPrivateKey pub = (DHPrivateKey) privKey;
            DHParameterSpec params = pub.getParams();
            return new DHPrivateKeyParameters(pub.getX(), new DHParameters(params.getP(), params.getG(), null, params.getL()));
        }
        throw new InvalidKeyException("private key not a DHPrivateKey");
    }

    private DHPublicKeyParameters generatePublicKeyParameter(PublicKey pubKey) throws InvalidKeyException {
        if (pubKey instanceof DHPublicKey) {
            if (pubKey instanceof BCDHPublicKey) {
                return ((BCDHPublicKey) pubKey).engineGetKeyParameters();
            }
            DHPublicKey pub = (DHPublicKey) pubKey;
            DHParameterSpec params = pub.getParams();
            if (params instanceof DHDomainParameterSpec) {
                return new DHPublicKeyParameters(pub.getY(), ((DHDomainParameterSpec) params).getDomainParameters());
            }
            return new DHPublicKeyParameters(pub.getY(), new DHParameters(params.getP(), params.getG(), null, params.getL()));
        }
        throw new InvalidKeyException("public key not a DHPublicKey");
    }
}
