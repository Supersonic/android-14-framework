package com.android.internal.org.bouncycastle.jcajce.provider.symmetric.util;

import android.security.keystore.KeyProperties;
import com.android.internal.org.bouncycastle.crypto.CipherParameters;
import com.android.internal.org.bouncycastle.crypto.CryptoServicesRegistrar;
import com.android.internal.org.bouncycastle.crypto.DataLengthException;
import com.android.internal.org.bouncycastle.crypto.StreamCipher;
import com.android.internal.org.bouncycastle.crypto.params.KeyParameter;
import com.android.internal.org.bouncycastle.crypto.params.ParametersWithIV;
import com.android.internal.org.bouncycastle.jcajce.PKCS12Key;
import com.android.internal.org.bouncycastle.jcajce.PKCS12KeyWithParameters;
import com.android.internal.org.bouncycastle.jcajce.provider.symmetric.util.PBE;
import java.security.AlgorithmParameters;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.InvalidParameterException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.ShortBufferException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEParameterSpec;
/* loaded from: classes4.dex */
public class BaseStreamCipher extends BaseWrapCipher implements PBE {
    private Class[] availableSpecs;
    private StreamCipher cipher;
    private int digest;
    private int ivLength;
    private ParametersWithIV ivParam;
    private int keySizeInBits;
    private String pbeAlgorithm;
    private PBEParameterSpec pbeSpec;

    /* JADX INFO: Access modifiers changed from: protected */
    public BaseStreamCipher(StreamCipher engine, int ivLength) {
        this(engine, ivLength, -1, -1);
    }

    protected BaseStreamCipher(StreamCipher engine, int ivLength, int keySizeInBits) {
        this(engine, ivLength, keySizeInBits, -1);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public BaseStreamCipher(StreamCipher engine, int ivLength, int keySizeInBits, int digest) {
        this.availableSpecs = new Class[]{IvParameterSpec.class, PBEParameterSpec.class};
        this.ivLength = 0;
        this.pbeSpec = null;
        this.pbeAlgorithm = null;
        this.cipher = engine;
        this.ivLength = ivLength;
        this.keySizeInBits = keySizeInBits;
        this.digest = digest;
    }

    @Override // com.android.internal.org.bouncycastle.jcajce.provider.symmetric.util.BaseWrapCipher, javax.crypto.CipherSpi
    protected int engineGetBlockSize() {
        return 0;
    }

    @Override // com.android.internal.org.bouncycastle.jcajce.provider.symmetric.util.BaseWrapCipher, javax.crypto.CipherSpi
    protected byte[] engineGetIV() {
        ParametersWithIV parametersWithIV = this.ivParam;
        if (parametersWithIV != null) {
            return parametersWithIV.getIV();
        }
        return null;
    }

    @Override // com.android.internal.org.bouncycastle.jcajce.provider.symmetric.util.BaseWrapCipher, javax.crypto.CipherSpi
    protected int engineGetKeySize(Key key) {
        return key.getEncoded().length * 8;
    }

    @Override // com.android.internal.org.bouncycastle.jcajce.provider.symmetric.util.BaseWrapCipher, javax.crypto.CipherSpi
    protected int engineGetOutputSize(int inputLen) {
        return inputLen;
    }

    @Override // com.android.internal.org.bouncycastle.jcajce.provider.symmetric.util.BaseWrapCipher, javax.crypto.CipherSpi
    protected AlgorithmParameters engineGetParameters() {
        if (this.engineParams == null) {
            if (this.pbeSpec != null) {
                try {
                    AlgorithmParameters engineParams = createParametersInstance(this.pbeAlgorithm);
                    engineParams.init(this.pbeSpec);
                    return engineParams;
                } catch (Exception e) {
                    return null;
                }
            } else if (this.ivParam != null) {
                String name = this.cipher.getAlgorithmName();
                if (name.indexOf(47) >= 0) {
                    name = name.substring(0, name.indexOf(47));
                }
                if (name.startsWith("ChaCha7539")) {
                    name = "ChaCha7539";
                } else if (name.startsWith("Grain")) {
                    name = "Grainv1";
                } else if (name.startsWith("HC")) {
                    int endIndex = name.indexOf(45);
                    name = name.substring(0, endIndex) + name.substring(endIndex + 1);
                }
                try {
                    this.engineParams = createParametersInstance(name);
                    this.engineParams.init(new IvParameterSpec(this.ivParam.getIV()));
                } catch (Exception e2) {
                    throw new RuntimeException(e2.toString());
                }
            }
        }
        return this.engineParams;
    }

    @Override // com.android.internal.org.bouncycastle.jcajce.provider.symmetric.util.BaseWrapCipher, javax.crypto.CipherSpi
    protected void engineSetMode(String mode) throws NoSuchAlgorithmException {
        if (!mode.equalsIgnoreCase(KeyProperties.BLOCK_MODE_ECB) && !mode.equals(KeyProperties.DIGEST_NONE)) {
            throw new NoSuchAlgorithmException("can't support mode " + mode);
        }
    }

    @Override // com.android.internal.org.bouncycastle.jcajce.provider.symmetric.util.BaseWrapCipher, javax.crypto.CipherSpi
    protected void engineSetPadding(String padding) throws NoSuchPaddingException {
        if (!padding.equalsIgnoreCase(KeyProperties.ENCRYPTION_PADDING_NONE)) {
            throw new NoSuchPaddingException("Padding " + padding + " unknown.");
        }
    }

    @Override // com.android.internal.org.bouncycastle.jcajce.provider.symmetric.util.BaseWrapCipher, javax.crypto.CipherSpi
    protected void engineInit(int opmode, Key key, AlgorithmParameterSpec params, SecureRandom random) throws InvalidKeyException, InvalidAlgorithmParameterException {
        CipherParameters param;
        CipherParameters param2;
        this.pbeSpec = null;
        this.pbeAlgorithm = null;
        this.engineParams = null;
        if (!(key instanceof SecretKey)) {
            throw new InvalidKeyException("Key for algorithm " + key.getAlgorithm() + " not suitable for symmetric enryption.");
        }
        if (key instanceof PKCS12Key) {
            PKCS12Key k = (PKCS12Key) key;
            PBEParameterSpec pBEParameterSpec = (PBEParameterSpec) params;
            this.pbeSpec = pBEParameterSpec;
            if ((k instanceof PKCS12KeyWithParameters) && pBEParameterSpec == null) {
                this.pbeSpec = new PBEParameterSpec(((PKCS12KeyWithParameters) k).getSalt(), ((PKCS12KeyWithParameters) k).getIterationCount());
            }
            param = PBE.Util.makePBEParameters(k.getEncoded(), 2, this.digest, this.keySizeInBits, this.ivLength * 8, this.pbeSpec, this.cipher.getAlgorithmName());
        } else if (key instanceof BCPBEKey) {
            BCPBEKey k2 = (BCPBEKey) key;
            if (k2.getOID() != null) {
                this.pbeAlgorithm = k2.getOID().getId();
            } else {
                this.pbeAlgorithm = k2.getAlgorithm();
            }
            if (k2.getParam() != null) {
                param2 = k2.getParam();
                this.pbeSpec = new PBEParameterSpec(k2.getSalt(), k2.getIterationCount());
            } else if (params instanceof PBEParameterSpec) {
                param2 = PBE.Util.makePBEParameters(k2, params, this.cipher.getAlgorithmName());
                this.pbeSpec = (PBEParameterSpec) params;
            } else {
                throw new InvalidAlgorithmParameterException("PBE requires PBE parameters to be set.");
            }
            if (k2.getIvSize() != 0) {
                this.ivParam = (ParametersWithIV) param2;
            }
            param = param2;
        } else if (params == null) {
            if (this.digest > 0) {
                throw new InvalidKeyException("Algorithm requires a PBE key");
            }
            param = new KeyParameter(key.getEncoded());
        } else if (params instanceof IvParameterSpec) {
            param = new ParametersWithIV(new KeyParameter(key.getEncoded()), ((IvParameterSpec) params).getIV());
            this.ivParam = (ParametersWithIV) param;
        } else {
            throw new InvalidAlgorithmParameterException("unknown parameter type.");
        }
        if (this.ivLength != 0 && !(param instanceof ParametersWithIV)) {
            SecureRandom ivRandom = random;
            if (ivRandom == null) {
                ivRandom = CryptoServicesRegistrar.getSecureRandom();
            }
            if (opmode == 1 || opmode == 3) {
                byte[] iv = new byte[this.ivLength];
                ivRandom.nextBytes(iv);
                param = new ParametersWithIV(param, iv);
                this.ivParam = (ParametersWithIV) param;
            } else {
                throw new InvalidAlgorithmParameterException("no IV set when one expected");
            }
        }
        try {
            switch (opmode) {
                case 1:
                case 3:
                    this.cipher.init(true, param);
                    return;
                case 2:
                case 4:
                    this.cipher.init(false, param);
                    return;
                default:
                    throw new InvalidParameterException("unknown opmode " + opmode + " passed");
            }
        } catch (Exception e) {
            throw new InvalidKeyException(e.getMessage());
        }
    }

    @Override // com.android.internal.org.bouncycastle.jcajce.provider.symmetric.util.BaseWrapCipher, javax.crypto.CipherSpi
    protected void engineInit(int opmode, Key key, AlgorithmParameters params, SecureRandom random) throws InvalidKeyException, InvalidAlgorithmParameterException {
        AlgorithmParameterSpec paramSpec = null;
        if (params != null && (paramSpec = SpecUtil.extractSpec(params, this.availableSpecs)) == null) {
            throw new InvalidAlgorithmParameterException("can't handle parameter " + params.toString());
        }
        engineInit(opmode, key, paramSpec, random);
        this.engineParams = params;
    }

    @Override // com.android.internal.org.bouncycastle.jcajce.provider.symmetric.util.BaseWrapCipher, javax.crypto.CipherSpi
    protected void engineInit(int opmode, Key key, SecureRandom random) throws InvalidKeyException {
        try {
            AlgorithmParameterSpec algorithmParameterSpec = null;
            engineInit(opmode, key, (AlgorithmParameterSpec) null, random);
        } catch (InvalidAlgorithmParameterException e) {
            throw new InvalidKeyException(e.getMessage());
        }
    }

    @Override // com.android.internal.org.bouncycastle.jcajce.provider.symmetric.util.BaseWrapCipher, javax.crypto.CipherSpi
    protected byte[] engineUpdate(byte[] input, int inputOffset, int inputLen) {
        byte[] out = new byte[inputLen];
        this.cipher.processBytes(input, inputOffset, inputLen, out, 0);
        return out;
    }

    @Override // com.android.internal.org.bouncycastle.jcajce.provider.symmetric.util.BaseWrapCipher, javax.crypto.CipherSpi
    protected int engineUpdate(byte[] input, int inputOffset, int inputLen, byte[] output, int outputOffset) throws ShortBufferException {
        if (outputOffset + inputLen > output.length) {
            throw new ShortBufferException("output buffer too short for input.");
        }
        try {
            this.cipher.processBytes(input, inputOffset, inputLen, output, outputOffset);
            return inputLen;
        } catch (DataLengthException e) {
            throw new IllegalStateException(e.getMessage());
        }
    }

    @Override // com.android.internal.org.bouncycastle.jcajce.provider.symmetric.util.BaseWrapCipher, javax.crypto.CipherSpi
    protected byte[] engineDoFinal(byte[] input, int inputOffset, int inputLen) {
        if (inputLen != 0) {
            byte[] out = engineUpdate(input, inputOffset, inputLen);
            this.cipher.reset();
            return out;
        }
        this.cipher.reset();
        return new byte[0];
    }

    @Override // com.android.internal.org.bouncycastle.jcajce.provider.symmetric.util.BaseWrapCipher, javax.crypto.CipherSpi
    protected int engineDoFinal(byte[] input, int inputOffset, int inputLen, byte[] output, int outputOffset) throws ShortBufferException {
        if (outputOffset + inputLen > output.length) {
            throw new ShortBufferException("output buffer too short for input.");
        }
        if (inputLen != 0) {
            this.cipher.processBytes(input, inputOffset, inputLen, output, outputOffset);
        }
        this.cipher.reset();
        return inputLen;
    }
}
