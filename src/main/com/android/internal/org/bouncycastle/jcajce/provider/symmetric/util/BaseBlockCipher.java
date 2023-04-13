package com.android.internal.org.bouncycastle.jcajce.provider.symmetric.util;

import android.security.keystore.KeyProperties;
import com.android.internal.org.bouncycastle.asn1.DEROctetString;
import com.android.internal.org.bouncycastle.asn1.cms.GCMParameters;
import com.android.internal.org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import com.android.internal.org.bouncycastle.crypto.BlockCipher;
import com.android.internal.org.bouncycastle.crypto.BufferedBlockCipher;
import com.android.internal.org.bouncycastle.crypto.CipherParameters;
import com.android.internal.org.bouncycastle.crypto.CryptoServicesRegistrar;
import com.android.internal.org.bouncycastle.crypto.DataLengthException;
import com.android.internal.org.bouncycastle.crypto.InvalidCipherTextException;
import com.android.internal.org.bouncycastle.crypto.OutputLengthException;
import com.android.internal.org.bouncycastle.crypto.modes.AEADBlockCipher;
import com.android.internal.org.bouncycastle.crypto.modes.AEADCipher;
import com.android.internal.org.bouncycastle.crypto.modes.CBCBlockCipher;
import com.android.internal.org.bouncycastle.crypto.modes.CCMBlockCipher;
import com.android.internal.org.bouncycastle.crypto.modes.CFBBlockCipher;
import com.android.internal.org.bouncycastle.crypto.modes.CTSBlockCipher;
import com.android.internal.org.bouncycastle.crypto.modes.GCMBlockCipher;
import com.android.internal.org.bouncycastle.crypto.modes.OFBBlockCipher;
import com.android.internal.org.bouncycastle.crypto.modes.SICBlockCipher;
import com.android.internal.org.bouncycastle.crypto.paddings.BlockCipherPadding;
import com.android.internal.org.bouncycastle.crypto.paddings.ISO10126d2Padding;
import com.android.internal.org.bouncycastle.crypto.paddings.ISO7816d4Padding;
import com.android.internal.org.bouncycastle.crypto.paddings.PaddedBufferedBlockCipher;
import com.android.internal.org.bouncycastle.crypto.paddings.TBCPadding;
import com.android.internal.org.bouncycastle.crypto.paddings.X923Padding;
import com.android.internal.org.bouncycastle.crypto.paddings.ZeroBytePadding;
import com.android.internal.org.bouncycastle.crypto.params.AEADParameters;
import com.android.internal.org.bouncycastle.crypto.params.KeyParameter;
import com.android.internal.org.bouncycastle.crypto.params.ParametersWithIV;
import com.android.internal.org.bouncycastle.crypto.params.ParametersWithRandom;
import com.android.internal.org.bouncycastle.jcajce.PKCS12Key;
import com.android.internal.org.bouncycastle.jcajce.PKCS12KeyWithParameters;
import com.android.internal.org.bouncycastle.jcajce.provider.symmetric.util.BaseWrapCipher;
import com.android.internal.org.bouncycastle.jcajce.provider.symmetric.util.PBE;
import com.android.internal.org.bouncycastle.jcajce.spec.AEADParameterSpec;
import com.android.internal.org.bouncycastle.util.Arrays;
import com.android.internal.org.bouncycastle.util.Strings;
import java.lang.reflect.Constructor;
import java.nio.ByteBuffer;
import java.security.AlgorithmParameters;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.InvalidParameterException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.ShortBufferException;
import javax.crypto.interfaces.PBEKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
/* loaded from: classes4.dex */
public class BaseBlockCipher extends BaseWrapCipher implements PBE {
    private static final int BUF_SIZE = 512;
    private static final Class gcmSpecClass = ClassUtil.loadClass(BaseBlockCipher.class, "javax.crypto.spec.GCMParameterSpec");
    private AEADParameters aeadParams;
    private Class[] availableSpecs;
    private BlockCipher baseEngine;
    private GenericBlockCipher cipher;
    private int digest;
    private BlockCipherProvider engineProvider;
    private boolean fixedIv;
    private int ivLength;
    private ParametersWithIV ivParam;
    private int keySizeInBits;
    private String modeName;
    private boolean padded;
    private String pbeAlgorithm;
    private PBEParameterSpec pbeSpec;
    private int scheme;

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes4.dex */
    public interface GenericBlockCipher {
        int doFinal(byte[] bArr, int i) throws IllegalStateException, BadPaddingException;

        String getAlgorithmName();

        int getOutputSize(int i);

        BlockCipher getUnderlyingCipher();

        int getUpdateOutputSize(int i);

        void init(boolean z, CipherParameters cipherParameters) throws IllegalArgumentException;

        int processByte(byte b, byte[] bArr, int i) throws DataLengthException;

        int processBytes(byte[] bArr, int i, int i2, byte[] bArr2, int i3) throws DataLengthException;

        void updateAAD(byte[] bArr, int i, int i2);

        boolean wrapOnNoPadding();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public BaseBlockCipher(BlockCipher engine) {
        this.availableSpecs = new Class[]{gcmSpecClass, IvParameterSpec.class, PBEParameterSpec.class};
        this.scheme = -1;
        this.ivLength = 0;
        this.fixedIv = true;
        this.pbeSpec = null;
        this.pbeAlgorithm = null;
        this.modeName = null;
        this.baseEngine = engine;
        this.cipher = new BufferedGenericBlockCipher(engine);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public BaseBlockCipher(BlockCipher engine, int scheme, int digest, int keySizeInBits, int ivLength) {
        this.availableSpecs = new Class[]{gcmSpecClass, IvParameterSpec.class, PBEParameterSpec.class};
        this.scheme = -1;
        this.ivLength = 0;
        this.fixedIv = true;
        this.pbeSpec = null;
        this.pbeAlgorithm = null;
        this.modeName = null;
        this.baseEngine = engine;
        this.scheme = scheme;
        this.digest = digest;
        this.keySizeInBits = keySizeInBits;
        this.ivLength = ivLength;
        this.cipher = new BufferedGenericBlockCipher(engine);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public BaseBlockCipher(BlockCipherProvider provider) {
        this.availableSpecs = new Class[]{gcmSpecClass, IvParameterSpec.class, PBEParameterSpec.class};
        this.scheme = -1;
        this.ivLength = 0;
        this.fixedIv = true;
        this.pbeSpec = null;
        this.pbeAlgorithm = null;
        this.modeName = null;
        this.baseEngine = provider.get();
        this.engineProvider = provider;
        this.cipher = new BufferedGenericBlockCipher(provider.get());
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public BaseBlockCipher(AEADBlockCipher engine) {
        this.availableSpecs = new Class[]{gcmSpecClass, IvParameterSpec.class, PBEParameterSpec.class};
        this.scheme = -1;
        this.ivLength = 0;
        this.fixedIv = true;
        this.pbeSpec = null;
        this.pbeAlgorithm = null;
        this.modeName = null;
        BlockCipher underlyingCipher = engine.getUnderlyingCipher();
        this.baseEngine = underlyingCipher;
        this.ivLength = underlyingCipher.getBlockSize();
        this.cipher = new AEADGenericBlockCipher(engine);
    }

    protected BaseBlockCipher(AEADCipher engine, boolean fixedIv, int ivLength) {
        this.availableSpecs = new Class[]{gcmSpecClass, IvParameterSpec.class, PBEParameterSpec.class};
        this.scheme = -1;
        this.ivLength = 0;
        this.fixedIv = true;
        this.pbeSpec = null;
        this.pbeAlgorithm = null;
        this.modeName = null;
        this.baseEngine = null;
        this.fixedIv = fixedIv;
        this.ivLength = ivLength;
        this.cipher = new AEADGenericBlockCipher(engine);
    }

    protected BaseBlockCipher(AEADBlockCipher engine, boolean fixedIv, int ivLength) {
        this.availableSpecs = new Class[]{gcmSpecClass, IvParameterSpec.class, PBEParameterSpec.class};
        this.scheme = -1;
        this.ivLength = 0;
        this.fixedIv = true;
        this.pbeSpec = null;
        this.pbeAlgorithm = null;
        this.modeName = null;
        this.baseEngine = engine.getUnderlyingCipher();
        this.fixedIv = fixedIv;
        this.ivLength = ivLength;
        this.cipher = new AEADGenericBlockCipher(engine);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public BaseBlockCipher(BlockCipher engine, int ivLength) {
        this(engine, true, ivLength);
    }

    protected BaseBlockCipher(BlockCipher engine, boolean fixedIv, int ivLength) {
        this.availableSpecs = new Class[]{gcmSpecClass, IvParameterSpec.class, PBEParameterSpec.class};
        this.scheme = -1;
        this.ivLength = 0;
        this.fixedIv = true;
        this.pbeSpec = null;
        this.pbeAlgorithm = null;
        this.modeName = null;
        this.baseEngine = engine;
        this.fixedIv = fixedIv;
        this.cipher = new BufferedGenericBlockCipher(engine);
        this.ivLength = ivLength / 8;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public BaseBlockCipher(BufferedBlockCipher engine, int ivLength) {
        this(engine, true, ivLength);
    }

    protected BaseBlockCipher(BufferedBlockCipher engine, boolean fixedIv, int ivLength) {
        this.availableSpecs = new Class[]{gcmSpecClass, IvParameterSpec.class, PBEParameterSpec.class};
        this.scheme = -1;
        this.ivLength = 0;
        this.fixedIv = true;
        this.pbeSpec = null;
        this.pbeAlgorithm = null;
        this.modeName = null;
        this.baseEngine = engine.getUnderlyingCipher();
        this.cipher = new BufferedGenericBlockCipher(engine);
        this.fixedIv = fixedIv;
        this.ivLength = ivLength / 8;
    }

    @Override // com.android.internal.org.bouncycastle.jcajce.provider.symmetric.util.BaseWrapCipher, javax.crypto.CipherSpi
    protected int engineGetBlockSize() {
        BlockCipher blockCipher = this.baseEngine;
        if (blockCipher == null) {
            return -1;
        }
        return blockCipher.getBlockSize();
    }

    @Override // com.android.internal.org.bouncycastle.jcajce.provider.symmetric.util.BaseWrapCipher, javax.crypto.CipherSpi
    protected byte[] engineGetIV() {
        AEADParameters aEADParameters = this.aeadParams;
        if (aEADParameters != null) {
            return aEADParameters.getNonce();
        }
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
        return this.cipher.getOutputSize(inputLen);
    }

    @Override // com.android.internal.org.bouncycastle.jcajce.provider.symmetric.util.BaseWrapCipher, javax.crypto.CipherSpi
    protected AlgorithmParameters engineGetParameters() {
        if (this.engineParams == null) {
            if (this.pbeSpec != null) {
                try {
                    this.engineParams = createParametersInstance(this.pbeAlgorithm);
                    this.engineParams.init(this.pbeSpec);
                } catch (Exception e) {
                    return null;
                }
            } else if (this.aeadParams != null) {
                if (this.baseEngine == null) {
                    try {
                        this.engineParams = createParametersInstance(PKCSObjectIdentifiers.id_alg_AEADChaCha20Poly1305.getId());
                        this.engineParams.init(new DEROctetString(this.aeadParams.getNonce()).getEncoded());
                    } catch (Exception e2) {
                        throw new RuntimeException(e2.toString());
                    }
                } else {
                    try {
                        this.engineParams = createParametersInstance(KeyProperties.BLOCK_MODE_GCM);
                        this.engineParams.init(new GCMParameters(this.aeadParams.getNonce(), this.aeadParams.getMacSize() / 8).getEncoded());
                    } catch (Exception e3) {
                        throw new RuntimeException(e3.toString());
                    }
                }
            } else if (this.ivParam != null) {
                String name = this.cipher.getUnderlyingCipher().getAlgorithmName();
                if (name.indexOf(47) >= 0) {
                    name = name.substring(0, name.indexOf(47));
                }
                try {
                    this.engineParams = createParametersInstance(name);
                    this.engineParams.init(new IvParameterSpec(this.ivParam.getIV()));
                } catch (Exception e4) {
                    throw new RuntimeException(e4.toString());
                }
            }
        }
        return this.engineParams;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.internal.org.bouncycastle.jcajce.provider.symmetric.util.BaseWrapCipher, javax.crypto.CipherSpi
    public void engineSetMode(String mode) throws NoSuchAlgorithmException {
        if (this.baseEngine == null) {
            throw new NoSuchAlgorithmException("no mode supported for this algorithm");
        }
        String upperCase = Strings.toUpperCase(mode);
        this.modeName = upperCase;
        if (upperCase.equals(KeyProperties.BLOCK_MODE_ECB)) {
            this.ivLength = 0;
            this.cipher = new BufferedGenericBlockCipher(this.baseEngine);
        } else if (this.modeName.equals(KeyProperties.BLOCK_MODE_CBC)) {
            this.ivLength = this.baseEngine.getBlockSize();
            this.cipher = new BufferedGenericBlockCipher(new CBCBlockCipher(this.baseEngine));
        } else if (this.modeName.startsWith("OFB")) {
            this.ivLength = this.baseEngine.getBlockSize();
            if (this.modeName.length() != 3) {
                int wordSize = Integer.parseInt(this.modeName.substring(3));
                this.cipher = new BufferedGenericBlockCipher(new OFBBlockCipher(this.baseEngine, wordSize));
                return;
            }
            BlockCipher blockCipher = this.baseEngine;
            this.cipher = new BufferedGenericBlockCipher(new OFBBlockCipher(blockCipher, blockCipher.getBlockSize() * 8));
        } else if (this.modeName.startsWith("CFB")) {
            this.ivLength = this.baseEngine.getBlockSize();
            if (this.modeName.length() != 3) {
                int wordSize2 = Integer.parseInt(this.modeName.substring(3));
                this.cipher = new BufferedGenericBlockCipher(new CFBBlockCipher(this.baseEngine, wordSize2));
                return;
            }
            BlockCipher blockCipher2 = this.baseEngine;
            this.cipher = new BufferedGenericBlockCipher(new CFBBlockCipher(blockCipher2, blockCipher2.getBlockSize() * 8));
        } else if (this.modeName.equals(KeyProperties.BLOCK_MODE_CTR)) {
            this.ivLength = this.baseEngine.getBlockSize();
            this.fixedIv = false;
            this.cipher = new BufferedGenericBlockCipher(new BufferedBlockCipher(new SICBlockCipher(this.baseEngine)));
        } else if (this.modeName.equals("CTS")) {
            this.ivLength = this.baseEngine.getBlockSize();
            this.cipher = new BufferedGenericBlockCipher(new CTSBlockCipher(new CBCBlockCipher(this.baseEngine)));
        } else if (this.modeName.equals("CCM")) {
            this.ivLength = 12;
            this.cipher = new AEADGenericBlockCipher(new CCMBlockCipher(this.baseEngine));
        } else if (this.modeName.equals(KeyProperties.BLOCK_MODE_GCM)) {
            this.ivLength = this.baseEngine.getBlockSize();
            this.cipher = new AEADGenericBlockCipher(new GCMBlockCipher(this.baseEngine));
        } else {
            throw new NoSuchAlgorithmException("can't support mode " + mode);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.internal.org.bouncycastle.jcajce.provider.symmetric.util.BaseWrapCipher, javax.crypto.CipherSpi
    public void engineSetPadding(String padding) throws NoSuchPaddingException {
        if (this.baseEngine == null) {
            throw new NoSuchPaddingException("no padding supported for this algorithm");
        }
        String paddingName = Strings.toUpperCase(padding);
        if (paddingName.equals("NOPADDING")) {
            if (this.cipher.wrapOnNoPadding()) {
                this.cipher = new BufferedGenericBlockCipher(new BufferedBlockCipher(this.cipher.getUnderlyingCipher()));
            }
        } else if (paddingName.equals("WITHCTS") || paddingName.equals("CTSPADDING") || paddingName.equals("CS3PADDING")) {
            this.cipher = new BufferedGenericBlockCipher(new CTSBlockCipher(this.cipher.getUnderlyingCipher()));
        } else {
            this.padded = true;
            if (isAEADModeName(this.modeName)) {
                throw new NoSuchPaddingException("Only NoPadding can be used with AEAD modes.");
            }
            if (paddingName.equals("PKCS5PADDING") || paddingName.equals("PKCS7PADDING")) {
                this.cipher = new BufferedGenericBlockCipher(this.cipher.getUnderlyingCipher());
            } else if (paddingName.equals("ZEROBYTEPADDING")) {
                this.cipher = new BufferedGenericBlockCipher(this.cipher.getUnderlyingCipher(), new ZeroBytePadding());
            } else if (paddingName.equals("ISO10126PADDING") || paddingName.equals("ISO10126-2PADDING")) {
                this.cipher = new BufferedGenericBlockCipher(this.cipher.getUnderlyingCipher(), new ISO10126d2Padding());
            } else if (paddingName.equals("X9.23PADDING") || paddingName.equals("X923PADDING")) {
                this.cipher = new BufferedGenericBlockCipher(this.cipher.getUnderlyingCipher(), new X923Padding());
            } else if (paddingName.equals("ISO7816-4PADDING") || paddingName.equals("ISO9797-1PADDING")) {
                this.cipher = new BufferedGenericBlockCipher(this.cipher.getUnderlyingCipher(), new ISO7816d4Padding());
            } else if (paddingName.equals("TBCPADDING")) {
                this.cipher = new BufferedGenericBlockCipher(this.cipher.getUnderlyingCipher(), new TBCPadding());
            } else {
                throw new NoSuchPaddingException("Padding " + padding + " unknown.");
            }
        }
    }

    private boolean isBCPBEKeyWithoutIV(Key key) {
        return (key instanceof BCPBEKey) && !(((BCPBEKey) key).getParam() instanceof ParametersWithIV);
    }

    @Override // com.android.internal.org.bouncycastle.jcajce.provider.symmetric.util.BaseWrapCipher, javax.crypto.CipherSpi
    protected void engineInit(int opmode, Key key, AlgorithmParameterSpec params, SecureRandom random) throws InvalidKeyException, InvalidAlgorithmParameterException {
        CipherParameters param;
        KeyParameter keyParam;
        int i;
        KeyParameter keyParam2;
        BlockCipher blockCipher;
        this.pbeSpec = null;
        this.pbeAlgorithm = null;
        this.engineParams = null;
        this.aeadParams = null;
        if (!(key instanceof SecretKey)) {
            throw new InvalidKeyException("Key for algorithm " + (key != null ? key.getAlgorithm() : null) + " not suitable for symmetric enryption.");
        } else if (params == null && (blockCipher = this.baseEngine) != null && blockCipher.getAlgorithmName().startsWith("RC5-64")) {
            throw new InvalidAlgorithmParameterException("RC5 requires an RC5ParametersSpec to be passed in.");
        } else {
            if ((this.scheme == 2 || (key instanceof PKCS12Key)) && !isBCPBEKeyWithoutIV(key)) {
                try {
                    SecretKey k = (SecretKey) key;
                    if (params instanceof PBEParameterSpec) {
                        this.pbeSpec = (PBEParameterSpec) params;
                    }
                    if ((k instanceof PBEKey) && this.pbeSpec == null) {
                        PBEKey pbeKey = (PBEKey) k;
                        if (pbeKey.getSalt() != null) {
                            this.pbeSpec = new PBEParameterSpec(pbeKey.getSalt(), pbeKey.getIterationCount());
                        } else {
                            throw new InvalidAlgorithmParameterException("PBEKey requires parameters to specify salt");
                        }
                    }
                    if (this.pbeSpec == null && !(k instanceof PBEKey)) {
                        throw new InvalidKeyException("Algorithm requires a PBE key");
                    }
                    if (!(key instanceof BCPBEKey)) {
                        param = PBE.Util.makePBEParameters(k.getEncoded(), 2, this.digest, this.keySizeInBits, this.ivLength * 8, this.pbeSpec, this.cipher.getAlgorithmName());
                    } else {
                        CipherParameters pbeKeyParam = ((BCPBEKey) key).getParam();
                        if (pbeKeyParam instanceof ParametersWithIV) {
                            param = pbeKeyParam;
                        } else if (pbeKeyParam == null) {
                            throw new AssertionError("Unreachable code");
                        } else {
                            throw new InvalidKeyException("Algorithm requires a PBE key suitable for PKCS12");
                        }
                    }
                    if (param instanceof ParametersWithIV) {
                        this.ivParam = (ParametersWithIV) param;
                    }
                } catch (Exception e) {
                    throw new InvalidKeyException("PKCS12 requires a SecretKey/PBEKey");
                }
            } else if (key instanceof BCPBEKey) {
                BCPBEKey k2 = (BCPBEKey) key;
                if (k2.getOID() != null) {
                    this.pbeAlgorithm = k2.getOID().getId();
                } else {
                    this.pbeAlgorithm = k2.getAlgorithm();
                }
                if (k2.getParam() != null) {
                    param = adjustParameters(params, k2.getParam());
                } else if (params instanceof PBEParameterSpec) {
                    PBEParameterSpec pBEParameterSpec = (PBEParameterSpec) params;
                    this.pbeSpec = pBEParameterSpec;
                    if (pBEParameterSpec.getSalt().length != 0 && this.pbeSpec.getIterationCount() > 0) {
                        k2 = new BCPBEKey(k2.getAlgorithm(), k2.getOID(), k2.getType(), k2.getDigest(), k2.getKeySize(), k2.getIvSize(), new PBEKeySpec(k2.getPassword(), this.pbeSpec.getSalt(), this.pbeSpec.getIterationCount(), k2.getKeySize()), null);
                    }
                    param = PBE.Util.makePBEParameters(k2, params, this.cipher.getUnderlyingCipher().getAlgorithmName());
                } else {
                    throw new InvalidAlgorithmParameterException("PBE requires PBE parameters to be set.");
                }
                if (param instanceof ParametersWithIV) {
                    this.ivParam = (ParametersWithIV) param;
                }
            } else if (key instanceof PBEKey) {
                PBEKey k3 = (PBEKey) key;
                PBEParameterSpec pBEParameterSpec2 = (PBEParameterSpec) params;
                this.pbeSpec = pBEParameterSpec2;
                if ((k3 instanceof PKCS12KeyWithParameters) && pBEParameterSpec2 == null) {
                    this.pbeSpec = new PBEParameterSpec(k3.getSalt(), k3.getIterationCount());
                }
                param = PBE.Util.makePBEParameters(k3.getEncoded(), this.scheme, this.digest, this.keySizeInBits, this.ivLength * 8, this.pbeSpec, this.cipher.getAlgorithmName());
                if (param instanceof ParametersWithIV) {
                    this.ivParam = (ParametersWithIV) param;
                }
            } else {
                int i2 = this.scheme;
                if (i2 == 0 || i2 == 4 || i2 == 1 || i2 == 5) {
                    throw new InvalidKeyException("Algorithm requires a PBE key");
                }
                param = new KeyParameter(key.getEncoded());
            }
            if (params instanceof AEADParameterSpec) {
                if (!isAEADModeName(this.modeName) && !(this.cipher instanceof AEADGenericBlockCipher)) {
                    throw new InvalidAlgorithmParameterException("AEADParameterSpec can only be used with AEAD modes.");
                }
                AEADParameterSpec aeadSpec = (AEADParameterSpec) params;
                if (param instanceof ParametersWithIV) {
                    keyParam2 = (KeyParameter) ((ParametersWithIV) param).getParameters();
                } else {
                    keyParam2 = (KeyParameter) param;
                }
                AEADParameters aEADParameters = new AEADParameters(keyParam2, aeadSpec.getMacSizeInBits(), aeadSpec.getNonce(), aeadSpec.getAssociatedData());
                this.aeadParams = aEADParameters;
                param = aEADParameters;
            } else if (params instanceof IvParameterSpec) {
                if (this.ivLength != 0) {
                    IvParameterSpec p = (IvParameterSpec) params;
                    if (p.getIV().length != this.ivLength && !(this.cipher instanceof AEADGenericBlockCipher) && this.fixedIv) {
                        throw new InvalidAlgorithmParameterException("IV must be " + this.ivLength + " bytes long.");
                    }
                    if (param instanceof ParametersWithIV) {
                        param = new ParametersWithIV(((ParametersWithIV) param).getParameters(), p.getIV());
                    } else {
                        param = new ParametersWithIV(param, p.getIV());
                    }
                    this.ivParam = (ParametersWithIV) param;
                } else {
                    String str = this.modeName;
                    if (str != null && str.equals(KeyProperties.BLOCK_MODE_ECB)) {
                        throw new InvalidAlgorithmParameterException("ECB mode does not use an IV");
                    }
                }
            } else {
                Class cls = gcmSpecClass;
                if (cls != null && cls.isInstance(params)) {
                    if (!isAEADModeName(this.modeName) && !(this.cipher instanceof AEADGenericBlockCipher)) {
                        throw new InvalidAlgorithmParameterException("GCMParameterSpec can only be used with AEAD modes.");
                    }
                    if (param instanceof ParametersWithIV) {
                        keyParam = (KeyParameter) ((ParametersWithIV) param).getParameters();
                    } else {
                        keyParam = (KeyParameter) param;
                    }
                    AEADParameters extractAeadParameters = GcmSpecUtil.extractAeadParameters(keyParam, params);
                    this.aeadParams = extractAeadParameters;
                    param = extractAeadParameters;
                } else if (params != null && !(params instanceof PBEParameterSpec)) {
                    throw new InvalidAlgorithmParameterException("unknown parameter type.");
                }
            }
            if (this.ivLength == 0 || (param instanceof ParametersWithIV) || (param instanceof AEADParameters)) {
                i = opmode;
            } else {
                SecureRandom ivRandom = random;
                if (ivRandom == null) {
                    ivRandom = CryptoServicesRegistrar.getSecureRandom();
                }
                i = opmode;
                if (i == 1 || i == 3) {
                    byte[] iv = new byte[this.ivLength];
                    if (!isBCPBEKeyWithoutIV(key)) {
                        ivRandom.nextBytes(iv);
                    } else {
                        System.err.println(" ******** DEPRECATED FUNCTIONALITY ********");
                        System.err.println(" * You have initialized a cipher with a PBE key with no IV and");
                        System.err.println(" * have not provided an IV in the AlgorithmParameterSpec.  This");
                        System.err.println(" * configuration is deprecated.  The cipher will be initialized");
                        System.err.println(" * with an all-zero IV, but in a future release this call will");
                        System.err.println(" * throw an exception.");
                        new InvalidAlgorithmParameterException("No IV set when using PBE key").printStackTrace(System.err);
                    }
                    param = new ParametersWithIV(param, iv);
                    this.ivParam = (ParametersWithIV) param;
                } else if (this.cipher.getUnderlyingCipher().getAlgorithmName().indexOf("PGPCFB") < 0) {
                    if (!isBCPBEKeyWithoutIV(key)) {
                        throw new InvalidAlgorithmParameterException("no IV set when one expected");
                    }
                    System.err.println(" ******** DEPRECATED FUNCTIONALITY ********");
                    System.err.println(" * You have initialized a cipher with a PBE key with no IV and");
                    System.err.println(" * have not provided an IV in the AlgorithmParameterSpec.  This");
                    System.err.println(" * configuration is deprecated.  The cipher will be initialized");
                    System.err.println(" * with an all-zero IV, but in a future release this call will");
                    System.err.println(" * throw an exception.");
                    new InvalidAlgorithmParameterException("No IV set when using PBE key").printStackTrace(System.err);
                    param = new ParametersWithIV(param, new byte[this.ivLength]);
                    this.ivParam = (ParametersWithIV) param;
                }
            }
            if (random != null && this.padded) {
                param = new ParametersWithRandom(param, random);
            }
            try {
                switch (i) {
                    case 1:
                    case 3:
                        this.cipher.init(true, param);
                        break;
                    case 2:
                    case 4:
                        this.cipher.init(false, param);
                        break;
                    default:
                        throw new InvalidParameterException("unknown opmode " + i + " passed");
                }
                GenericBlockCipher genericBlockCipher = this.cipher;
                if ((genericBlockCipher instanceof AEADGenericBlockCipher) && this.aeadParams == null) {
                    AEADCipher aeadCipher = ((AEADGenericBlockCipher) genericBlockCipher).cipher;
                    this.aeadParams = new AEADParameters((KeyParameter) this.ivParam.getParameters(), aeadCipher.getMac().length * 8, this.ivParam.getIV());
                }
            } catch (Exception e2) {
                throw new BaseWrapCipher.InvalidKeyOrParametersException(e2.getMessage(), e2);
            }
        }
    }

    private CipherParameters adjustParameters(AlgorithmParameterSpec params, CipherParameters param) {
        if (param instanceof ParametersWithIV) {
            CipherParameters key = ((ParametersWithIV) param).getParameters();
            if (params instanceof IvParameterSpec) {
                IvParameterSpec iv = (IvParameterSpec) params;
                this.ivParam = new ParametersWithIV(key, iv.getIV());
                return this.ivParam;
            }
            return param;
        } else if (params instanceof IvParameterSpec) {
            IvParameterSpec iv2 = (IvParameterSpec) params;
            this.ivParam = new ParametersWithIV(param, iv2.getIV());
            return this.ivParam;
        } else {
            return param;
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

    @Override // javax.crypto.CipherSpi
    protected void engineUpdateAAD(byte[] input, int offset, int length) {
        this.cipher.updateAAD(input, offset, length);
    }

    @Override // javax.crypto.CipherSpi
    protected void engineUpdateAAD(ByteBuffer src) {
        int remaining = src.remaining();
        if (remaining >= 1) {
            if (src.hasArray()) {
                engineUpdateAAD(src.array(), src.arrayOffset() + src.position(), remaining);
                src.position(src.limit());
            } else if (remaining <= 512) {
                byte[] data = new byte[remaining];
                src.get(data);
                engineUpdateAAD(data, 0, data.length);
                Arrays.fill(data, (byte) 0);
            } else {
                byte[] data2 = new byte[512];
                do {
                    int length = Math.min(data2.length, remaining);
                    src.get(data2, 0, length);
                    engineUpdateAAD(data2, 0, length);
                    remaining -= length;
                } while (remaining > 0);
                Arrays.fill(data2, (byte) 0);
            }
        }
    }

    @Override // com.android.internal.org.bouncycastle.jcajce.provider.symmetric.util.BaseWrapCipher, javax.crypto.CipherSpi
    protected byte[] engineUpdate(byte[] input, int inputOffset, int inputLen) {
        int length = this.cipher.getUpdateOutputSize(inputLen);
        if (length > 0) {
            byte[] out = new byte[length];
            int len = this.cipher.processBytes(input, inputOffset, inputLen, out, 0);
            if (len == 0) {
                return null;
            }
            if (len != out.length) {
                byte[] tmp = new byte[len];
                System.arraycopy(out, 0, tmp, 0, len);
                return tmp;
            }
            return out;
        }
        this.cipher.processBytes(input, inputOffset, inputLen, null, 0);
        return null;
    }

    @Override // com.android.internal.org.bouncycastle.jcajce.provider.symmetric.util.BaseWrapCipher, javax.crypto.CipherSpi
    protected int engineUpdate(byte[] input, int inputOffset, int inputLen, byte[] output, int outputOffset) throws ShortBufferException {
        if (this.cipher.getUpdateOutputSize(inputLen) + outputOffset > output.length) {
            throw new ShortBufferException("output buffer too short for input.");
        }
        try {
            return this.cipher.processBytes(input, inputOffset, inputLen, output, outputOffset);
        } catch (DataLengthException e) {
            throw new IllegalStateException(e.toString());
        }
    }

    @Override // com.android.internal.org.bouncycastle.jcajce.provider.symmetric.util.BaseWrapCipher, javax.crypto.CipherSpi
    protected byte[] engineDoFinal(byte[] input, int inputOffset, int inputLen) throws IllegalBlockSizeException, BadPaddingException {
        int len = 0;
        byte[] tmp = new byte[engineGetOutputSize(inputLen)];
        if (inputLen != 0) {
            len = this.cipher.processBytes(input, inputOffset, inputLen, tmp, 0);
        }
        try {
            int len2 = len + this.cipher.doFinal(tmp, len);
            if (len2 == tmp.length) {
                return tmp;
            }
            if (len2 > tmp.length) {
                throw new IllegalBlockSizeException("internal buffer overflow");
            }
            byte[] out = new byte[len2];
            System.arraycopy(tmp, 0, out, 0, len2);
            return out;
        } catch (DataLengthException e) {
            throw new IllegalBlockSizeException(e.getMessage());
        }
    }

    @Override // com.android.internal.org.bouncycastle.jcajce.provider.symmetric.util.BaseWrapCipher, javax.crypto.CipherSpi
    protected int engineDoFinal(byte[] input, int inputOffset, int inputLen, byte[] output, int outputOffset) throws IllegalBlockSizeException, BadPaddingException, ShortBufferException {
        int len = 0;
        if (engineGetOutputSize(inputLen) + outputOffset > output.length) {
            throw new ShortBufferException("output buffer too short for input.");
        }
        if (inputLen != 0) {
            try {
                len = this.cipher.processBytes(input, inputOffset, inputLen, output, outputOffset);
            } catch (OutputLengthException e) {
                throw new IllegalBlockSizeException(e.getMessage());
            } catch (DataLengthException e2) {
                throw new IllegalBlockSizeException(e2.getMessage());
            }
        }
        return this.cipher.doFinal(output, outputOffset + len) + len;
    }

    private boolean isAEADModeName(String modeName) {
        return "CCM".equals(modeName) || KeyProperties.BLOCK_MODE_GCM.equals(modeName);
    }

    /* loaded from: classes4.dex */
    private static class BufferedGenericBlockCipher implements GenericBlockCipher {
        private BufferedBlockCipher cipher;

        BufferedGenericBlockCipher(BufferedBlockCipher cipher) {
            this.cipher = cipher;
        }

        BufferedGenericBlockCipher(BlockCipher cipher) {
            this.cipher = new PaddedBufferedBlockCipher(cipher);
        }

        BufferedGenericBlockCipher(BlockCipher cipher, BlockCipherPadding padding) {
            this.cipher = new PaddedBufferedBlockCipher(cipher, padding);
        }

        @Override // com.android.internal.org.bouncycastle.jcajce.provider.symmetric.util.BaseBlockCipher.GenericBlockCipher
        public void init(boolean forEncryption, CipherParameters params) throws IllegalArgumentException {
            this.cipher.init(forEncryption, params);
        }

        @Override // com.android.internal.org.bouncycastle.jcajce.provider.symmetric.util.BaseBlockCipher.GenericBlockCipher
        public boolean wrapOnNoPadding() {
            return !(this.cipher instanceof CTSBlockCipher);
        }

        @Override // com.android.internal.org.bouncycastle.jcajce.provider.symmetric.util.BaseBlockCipher.GenericBlockCipher
        public String getAlgorithmName() {
            return this.cipher.getUnderlyingCipher().getAlgorithmName();
        }

        @Override // com.android.internal.org.bouncycastle.jcajce.provider.symmetric.util.BaseBlockCipher.GenericBlockCipher
        public BlockCipher getUnderlyingCipher() {
            return this.cipher.getUnderlyingCipher();
        }

        @Override // com.android.internal.org.bouncycastle.jcajce.provider.symmetric.util.BaseBlockCipher.GenericBlockCipher
        public int getOutputSize(int len) {
            return this.cipher.getOutputSize(len);
        }

        @Override // com.android.internal.org.bouncycastle.jcajce.provider.symmetric.util.BaseBlockCipher.GenericBlockCipher
        public int getUpdateOutputSize(int len) {
            return this.cipher.getUpdateOutputSize(len);
        }

        @Override // com.android.internal.org.bouncycastle.jcajce.provider.symmetric.util.BaseBlockCipher.GenericBlockCipher
        public void updateAAD(byte[] input, int offset, int length) {
            throw new UnsupportedOperationException("AAD is not supported in the current mode.");
        }

        @Override // com.android.internal.org.bouncycastle.jcajce.provider.symmetric.util.BaseBlockCipher.GenericBlockCipher
        public int processByte(byte in, byte[] out, int outOff) throws DataLengthException {
            return this.cipher.processByte(in, out, outOff);
        }

        @Override // com.android.internal.org.bouncycastle.jcajce.provider.symmetric.util.BaseBlockCipher.GenericBlockCipher
        public int processBytes(byte[] in, int inOff, int len, byte[] out, int outOff) throws DataLengthException {
            return this.cipher.processBytes(in, inOff, len, out, outOff);
        }

        @Override // com.android.internal.org.bouncycastle.jcajce.provider.symmetric.util.BaseBlockCipher.GenericBlockCipher
        public int doFinal(byte[] out, int outOff) throws IllegalStateException, BadPaddingException {
            try {
                return this.cipher.doFinal(out, outOff);
            } catch (InvalidCipherTextException e) {
                throw new BadPaddingException(e.getMessage());
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes4.dex */
    public static class AEADGenericBlockCipher implements GenericBlockCipher {
        private static final Constructor aeadBadTagConstructor;
        private AEADCipher cipher;

        static {
            Class aeadBadTagClass = ClassUtil.loadClass(BaseBlockCipher.class, "javax.crypto.AEADBadTagException");
            if (aeadBadTagClass != null) {
                aeadBadTagConstructor = findExceptionConstructor(aeadBadTagClass);
            } else {
                aeadBadTagConstructor = null;
            }
        }

        private static Constructor findExceptionConstructor(Class clazz) {
            try {
                return clazz.getConstructor(String.class);
            } catch (Exception e) {
                return null;
            }
        }

        AEADGenericBlockCipher(AEADCipher cipher) {
            this.cipher = cipher;
        }

        @Override // com.android.internal.org.bouncycastle.jcajce.provider.symmetric.util.BaseBlockCipher.GenericBlockCipher
        public void init(boolean forEncryption, CipherParameters params) throws IllegalArgumentException {
            this.cipher.init(forEncryption, params);
        }

        @Override // com.android.internal.org.bouncycastle.jcajce.provider.symmetric.util.BaseBlockCipher.GenericBlockCipher
        public String getAlgorithmName() {
            AEADCipher aEADCipher = this.cipher;
            if (aEADCipher instanceof AEADBlockCipher) {
                return ((AEADBlockCipher) aEADCipher).getUnderlyingCipher().getAlgorithmName();
            }
            return aEADCipher.getAlgorithmName();
        }

        @Override // com.android.internal.org.bouncycastle.jcajce.provider.symmetric.util.BaseBlockCipher.GenericBlockCipher
        public boolean wrapOnNoPadding() {
            return false;
        }

        @Override // com.android.internal.org.bouncycastle.jcajce.provider.symmetric.util.BaseBlockCipher.GenericBlockCipher
        public BlockCipher getUnderlyingCipher() {
            AEADCipher aEADCipher = this.cipher;
            if (aEADCipher instanceof AEADBlockCipher) {
                return ((AEADBlockCipher) aEADCipher).getUnderlyingCipher();
            }
            return null;
        }

        @Override // com.android.internal.org.bouncycastle.jcajce.provider.symmetric.util.BaseBlockCipher.GenericBlockCipher
        public int getOutputSize(int len) {
            return this.cipher.getOutputSize(len);
        }

        @Override // com.android.internal.org.bouncycastle.jcajce.provider.symmetric.util.BaseBlockCipher.GenericBlockCipher
        public int getUpdateOutputSize(int len) {
            return this.cipher.getUpdateOutputSize(len);
        }

        @Override // com.android.internal.org.bouncycastle.jcajce.provider.symmetric.util.BaseBlockCipher.GenericBlockCipher
        public void updateAAD(byte[] input, int offset, int length) {
            this.cipher.processAADBytes(input, offset, length);
        }

        @Override // com.android.internal.org.bouncycastle.jcajce.provider.symmetric.util.BaseBlockCipher.GenericBlockCipher
        public int processByte(byte in, byte[] out, int outOff) throws DataLengthException {
            return this.cipher.processByte(in, out, outOff);
        }

        @Override // com.android.internal.org.bouncycastle.jcajce.provider.symmetric.util.BaseBlockCipher.GenericBlockCipher
        public int processBytes(byte[] in, int inOff, int len, byte[] out, int outOff) throws DataLengthException {
            return this.cipher.processBytes(in, inOff, len, out, outOff);
        }

        @Override // com.android.internal.org.bouncycastle.jcajce.provider.symmetric.util.BaseBlockCipher.GenericBlockCipher
        public int doFinal(byte[] out, int outOff) throws IllegalStateException, BadPaddingException {
            try {
                return this.cipher.doFinal(out, outOff);
            } catch (InvalidCipherTextException e) {
                Constructor constructor = aeadBadTagConstructor;
                if (constructor != null) {
                    BadPaddingException aeadBadTag = null;
                    try {
                        aeadBadTag = (BadPaddingException) constructor.newInstance(e.getMessage());
                    } catch (Exception e2) {
                    }
                    if (aeadBadTag != null) {
                        throw aeadBadTag;
                    }
                }
                throw new BadPaddingException(e.getMessage());
            }
        }
    }
}
