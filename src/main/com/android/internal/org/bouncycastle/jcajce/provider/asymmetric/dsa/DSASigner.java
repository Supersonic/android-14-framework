package com.android.internal.org.bouncycastle.jcajce.provider.asymmetric.dsa;

import com.android.internal.org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import com.android.internal.org.bouncycastle.asn1.x509.X509ObjectIdentifiers;
import com.android.internal.org.bouncycastle.crypto.CipherParameters;
import com.android.internal.org.bouncycastle.crypto.DSAExt;
import com.android.internal.org.bouncycastle.crypto.Digest;
import com.android.internal.org.bouncycastle.crypto.digests.AndroidDigestFactory;
import com.android.internal.org.bouncycastle.crypto.digests.NullDigest;
import com.android.internal.org.bouncycastle.crypto.params.DSAKeyParameters;
import com.android.internal.org.bouncycastle.crypto.params.DSAParameters;
import com.android.internal.org.bouncycastle.crypto.params.ParametersWithRandom;
import com.android.internal.org.bouncycastle.crypto.signers.DSAEncoding;
import com.android.internal.org.bouncycastle.crypto.signers.StandardDSAEncoding;
import java.math.BigInteger;
import java.security.AlgorithmParameters;
import java.security.InvalidKeyException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.SignatureException;
import java.security.SignatureSpi;
import java.security.spec.AlgorithmParameterSpec;
/* loaded from: classes4.dex */
public class DSASigner extends SignatureSpi implements PKCSObjectIdentifiers, X509ObjectIdentifiers {
    private Digest digest;
    private DSAEncoding encoding = StandardDSAEncoding.INSTANCE;
    private SecureRandom random;
    private DSAExt signer;

    protected DSASigner(Digest digest, DSAExt signer) {
        this.digest = digest;
        this.signer = signer;
    }

    @Override // java.security.SignatureSpi
    protected void engineInitVerify(PublicKey publicKey) throws InvalidKeyException {
        CipherParameters param = DSAUtil.generatePublicKeyParameter(publicKey);
        this.digest.reset();
        this.signer.init(false, param);
    }

    @Override // java.security.SignatureSpi
    protected void engineInitSign(PrivateKey privateKey, SecureRandom random) throws InvalidKeyException {
        this.random = random;
        engineInitSign(privateKey);
    }

    @Override // java.security.SignatureSpi
    protected void engineInitSign(PrivateKey privateKey) throws InvalidKeyException {
        CipherParameters param = DSAUtil.generatePrivateKeyParameter(privateKey);
        DSAParameters dsaParam = ((DSAKeyParameters) param).getParameters();
        checkKey(dsaParam);
        SecureRandom secureRandom = this.random;
        if (secureRandom != null) {
            param = new ParametersWithRandom(param, secureRandom);
        }
        this.digest.reset();
        this.signer.init(true, param);
    }

    @Override // java.security.SignatureSpi
    protected void engineUpdate(byte b) throws SignatureException {
        this.digest.update(b);
    }

    @Override // java.security.SignatureSpi
    protected void engineUpdate(byte[] b, int off, int len) throws SignatureException {
        this.digest.update(b, off, len);
    }

    @Override // java.security.SignatureSpi
    protected byte[] engineSign() throws SignatureException {
        byte[] hash = new byte[this.digest.getDigestSize()];
        this.digest.doFinal(hash, 0);
        try {
            BigInteger[] sig = this.signer.generateSignature(hash);
            return this.encoding.encode(this.signer.getOrder(), sig[0], sig[1]);
        } catch (Exception e) {
            throw new SignatureException(e.toString());
        }
    }

    @Override // java.security.SignatureSpi
    protected boolean engineVerify(byte[] sigBytes) throws SignatureException {
        byte[] hash = new byte[this.digest.getDigestSize()];
        this.digest.doFinal(hash, 0);
        try {
            BigInteger[] sig = this.encoding.decode(this.signer.getOrder(), sigBytes);
            return this.signer.verifySignature(hash, sig[0], sig[1]);
        } catch (Exception e) {
            throw new SignatureException("error decoding signature bytes.");
        }
    }

    @Override // java.security.SignatureSpi
    protected AlgorithmParameters engineGetParameters() {
        return null;
    }

    @Override // java.security.SignatureSpi
    protected void engineSetParameter(AlgorithmParameterSpec params) {
        throw new UnsupportedOperationException("engineSetParameter unsupported");
    }

    protected void checkKey(DSAParameters params) throws InvalidKeyException {
        int valueL = params.getP().bitLength();
        int valueN = params.getQ().bitLength();
        int digestSize = this.digest.getDigestSize();
        if (valueL < 1024 || valueL > 3072 || valueL % 1024 != 0) {
            throw new InvalidKeyException("valueL values must be between 1024 and 3072 and a multiple of 1024");
        }
        if (valueL == 1024 && valueN != 160) {
            throw new InvalidKeyException("valueN must be 160 for valueL = 1024");
        }
        if (valueL == 2048 && valueN != 224 && valueN != 256) {
            throw new InvalidKeyException("valueN must be 224 or 256 for valueL = 2048");
        }
        if (valueL == 3072 && valueN != 256) {
            throw new InvalidKeyException("valueN must be 256 for valueL = 3072");
        }
        if (!(this.digest instanceof NullDigest) && valueN > digestSize * 8) {
            throw new InvalidKeyException("Key is too strong for this signature algorithm");
        }
    }

    @Override // java.security.SignatureSpi
    protected void engineSetParameter(String param, Object value) {
        throw new UnsupportedOperationException("engineSetParameter unsupported");
    }

    @Override // java.security.SignatureSpi
    protected Object engineGetParameter(String param) {
        throw new UnsupportedOperationException("engineGetParameter unsupported");
    }

    /* loaded from: classes4.dex */
    public static class stdDSA extends DSASigner {
        public stdDSA() {
            super(AndroidDigestFactory.getSHA1(), new com.android.internal.org.bouncycastle.crypto.signers.DSASigner());
        }
    }

    /* loaded from: classes4.dex */
    public static class dsa224 extends DSASigner {
        public dsa224() {
            super(AndroidDigestFactory.getSHA224(), new com.android.internal.org.bouncycastle.crypto.signers.DSASigner());
        }
    }

    /* loaded from: classes4.dex */
    public static class dsa256 extends DSASigner {
        public dsa256() {
            super(AndroidDigestFactory.getSHA256(), new com.android.internal.org.bouncycastle.crypto.signers.DSASigner());
        }
    }

    /* loaded from: classes4.dex */
    public static class noneDSA extends DSASigner {
        public noneDSA() {
            super(new NullDigest(), new com.android.internal.org.bouncycastle.crypto.signers.DSASigner());
        }
    }
}
