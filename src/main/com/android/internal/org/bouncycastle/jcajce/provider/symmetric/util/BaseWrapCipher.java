package com.android.internal.org.bouncycastle.jcajce.provider.symmetric.util;

import com.android.internal.org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import com.android.internal.org.bouncycastle.crypto.CipherParameters;
import com.android.internal.org.bouncycastle.crypto.InvalidCipherTextException;
import com.android.internal.org.bouncycastle.crypto.Wrapper;
import com.android.internal.org.bouncycastle.crypto.params.KeyParameter;
import com.android.internal.org.bouncycastle.crypto.params.ParametersWithIV;
import com.android.internal.org.bouncycastle.crypto.params.ParametersWithRandom;
import com.android.internal.org.bouncycastle.jcajce.provider.symmetric.util.PBE;
import com.android.internal.org.bouncycastle.jcajce.util.DefaultJcaJceHelper;
import com.android.internal.org.bouncycastle.jcajce.util.JcaJceHelper;
import com.android.internal.org.bouncycastle.jce.provider.BouncyCastleProvider;
import com.android.internal.org.bouncycastle.util.Arrays;
import java.io.ByteArrayOutputStream;
import java.security.AlgorithmParameters;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.InvalidParameterException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import javax.crypto.BadPaddingException;
import javax.crypto.CipherSpi;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.ShortBufferException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEParameterSpec;
import javax.crypto.spec.SecretKeySpec;
/* loaded from: classes4.dex */
public abstract class BaseWrapCipher extends CipherSpi implements PBE {
    private Class[] availableSpecs;
    protected AlgorithmParameters engineParams;
    private boolean forWrapping;
    private final JcaJceHelper helper;

    /* renamed from: iv */
    private byte[] f812iv;
    private int ivSize;
    protected int pbeHash;
    protected int pbeIvSize;
    protected int pbeKeySize;
    protected int pbeType;
    protected Wrapper wrapEngine;
    private ErasableOutputStream wrapStream;

    /* JADX INFO: Access modifiers changed from: protected */
    public BaseWrapCipher() {
        this.availableSpecs = new Class[]{PBEParameterSpec.class, IvParameterSpec.class};
        this.pbeType = 2;
        this.pbeHash = 1;
        this.engineParams = null;
        this.wrapEngine = null;
        this.wrapStream = null;
        this.helper = new DefaultJcaJceHelper();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public BaseWrapCipher(Wrapper wrapEngine) {
        this(wrapEngine, 0);
    }

    protected BaseWrapCipher(Wrapper wrapEngine, int ivSize) {
        this.availableSpecs = new Class[]{PBEParameterSpec.class, IvParameterSpec.class};
        this.pbeType = 2;
        this.pbeHash = 1;
        this.engineParams = null;
        this.wrapEngine = null;
        this.wrapStream = null;
        this.helper = new DefaultJcaJceHelper();
        this.wrapEngine = wrapEngine;
        this.ivSize = ivSize;
    }

    @Override // javax.crypto.CipherSpi
    protected int engineGetBlockSize() {
        return 0;
    }

    @Override // javax.crypto.CipherSpi
    protected byte[] engineGetIV() {
        return Arrays.clone(this.f812iv);
    }

    @Override // javax.crypto.CipherSpi
    protected int engineGetKeySize(Key key) {
        return key.getEncoded().length * 8;
    }

    @Override // javax.crypto.CipherSpi
    protected int engineGetOutputSize(int inputLen) {
        return -1;
    }

    @Override // javax.crypto.CipherSpi
    protected AlgorithmParameters engineGetParameters() {
        if (this.engineParams == null && this.f812iv != null) {
            String name = this.wrapEngine.getAlgorithmName();
            if (name.indexOf(47) >= 0) {
                name = name.substring(0, name.indexOf(47));
            }
            try {
                AlgorithmParameters createParametersInstance = createParametersInstance(name);
                this.engineParams = createParametersInstance;
                createParametersInstance.init(new IvParameterSpec(this.f812iv));
            } catch (Exception e) {
                throw new RuntimeException(e.toString());
            }
        }
        return this.engineParams;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final AlgorithmParameters createParametersInstance(String algorithm) throws NoSuchAlgorithmException, NoSuchProviderException {
        return this.helper.createAlgorithmParameters(algorithm);
    }

    @Override // javax.crypto.CipherSpi
    protected void engineSetMode(String mode) throws NoSuchAlgorithmException {
        throw new NoSuchAlgorithmException("can't support mode " + mode);
    }

    @Override // javax.crypto.CipherSpi
    protected void engineSetPadding(String padding) throws NoSuchPaddingException {
        throw new NoSuchPaddingException("Padding " + padding + " unknown.");
    }

    @Override // javax.crypto.CipherSpi
    protected void engineInit(int opmode, Key key, AlgorithmParameterSpec params, SecureRandom random) throws InvalidKeyException, InvalidAlgorithmParameterException {
        CipherParameters param;
        int i;
        if (key instanceof BCPBEKey) {
            BCPBEKey k = (BCPBEKey) key;
            if (params instanceof PBEParameterSpec) {
                param = PBE.Util.makePBEParameters(k, params, this.wrapEngine.getAlgorithmName());
            } else {
                CipherParameters param2 = k.getParam();
                if (param2 != null) {
                    param = k.getParam();
                } else {
                    throw new InvalidAlgorithmParameterException("PBE requires PBE parameters to be set.");
                }
            }
        } else {
            param = new KeyParameter(key.getEncoded());
        }
        if (params instanceof IvParameterSpec) {
            IvParameterSpec ivSpec = (IvParameterSpec) params;
            byte[] iv = ivSpec.getIV();
            this.f812iv = iv;
            param = new ParametersWithIV(param, iv);
        }
        if ((param instanceof KeyParameter) && (i = this.ivSize) != 0 && (opmode == 3 || opmode == 1)) {
            byte[] bArr = new byte[i];
            this.f812iv = bArr;
            random.nextBytes(bArr);
            param = new ParametersWithIV(param, this.f812iv);
        }
        if (random != null) {
            param = new ParametersWithRandom(param, random);
        }
        try {
            switch (opmode) {
                case 1:
                    this.wrapEngine.init(true, param);
                    this.wrapStream = new ErasableOutputStream();
                    this.forWrapping = true;
                    return;
                case 2:
                    this.wrapEngine.init(false, param);
                    this.wrapStream = new ErasableOutputStream();
                    this.forWrapping = false;
                    return;
                case 3:
                    this.wrapEngine.init(true, param);
                    this.wrapStream = null;
                    this.forWrapping = true;
                    return;
                case 4:
                    this.wrapEngine.init(false, param);
                    this.wrapStream = null;
                    this.forWrapping = false;
                    return;
                default:
                    throw new InvalidParameterException("Unknown mode parameter passed to init.");
            }
        } catch (Exception e) {
            throw new InvalidKeyOrParametersException(e.getMessage(), e);
        }
    }

    @Override // javax.crypto.CipherSpi
    protected void engineInit(int opmode, Key key, AlgorithmParameters params, SecureRandom random) throws InvalidKeyException, InvalidAlgorithmParameterException {
        AlgorithmParameterSpec paramSpec = null;
        if (params != null && (paramSpec = SpecUtil.extractSpec(params, this.availableSpecs)) == null) {
            throw new InvalidAlgorithmParameterException("can't handle parameter " + params.toString());
        }
        this.engineParams = params;
        engineInit(opmode, key, paramSpec, random);
    }

    @Override // javax.crypto.CipherSpi
    protected void engineInit(int opmode, Key key, SecureRandom random) throws InvalidKeyException {
        try {
            AlgorithmParameterSpec algorithmParameterSpec = null;
            engineInit(opmode, key, (AlgorithmParameterSpec) null, random);
        } catch (InvalidAlgorithmParameterException e) {
            throw new InvalidKeyOrParametersException(e.getMessage(), e);
        }
    }

    @Override // javax.crypto.CipherSpi
    protected byte[] engineUpdate(byte[] input, int inputOffset, int inputLen) {
        ErasableOutputStream erasableOutputStream = this.wrapStream;
        if (erasableOutputStream == null) {
            throw new IllegalStateException("not supported in a wrapping mode");
        }
        erasableOutputStream.write(input, inputOffset, inputLen);
        return null;
    }

    @Override // javax.crypto.CipherSpi
    protected int engineUpdate(byte[] input, int inputOffset, int inputLen, byte[] output, int outputOffset) throws ShortBufferException {
        ErasableOutputStream erasableOutputStream = this.wrapStream;
        if (erasableOutputStream == null) {
            throw new IllegalStateException("not supported in a wrapping mode");
        }
        erasableOutputStream.write(input, inputOffset, inputLen);
        return 0;
    }

    @Override // javax.crypto.CipherSpi
    protected byte[] engineDoFinal(byte[] input, int inputOffset, int inputLen) throws IllegalBlockSizeException, BadPaddingException {
        ErasableOutputStream erasableOutputStream = this.wrapStream;
        if (erasableOutputStream == null) {
            throw new IllegalStateException("not supported in a wrapping mode");
        }
        if (input != null) {
            erasableOutputStream.write(input, inputOffset, inputLen);
        }
        try {
            if (this.forWrapping) {
                try {
                    return this.wrapEngine.wrap(this.wrapStream.getBuf(), 0, this.wrapStream.size());
                } catch (Exception e) {
                    throw new IllegalBlockSizeException(e.getMessage());
                }
            }
            try {
                return this.wrapEngine.unwrap(this.wrapStream.getBuf(), 0, this.wrapStream.size());
            } catch (InvalidCipherTextException e2) {
                throw new BadPaddingException(e2.getMessage());
            }
        } finally {
            this.wrapStream.erase();
        }
        this.wrapStream.erase();
    }

    /* JADX WARN: Removed duplicated region for block: B:16:0x0042 A[Catch: all -> 0x0061, TRY_LEAVE, TryCatch #1 {all -> 0x0061, blocks: (B:5:0x0007, B:8:0x000c, B:14:0x003d, B:16:0x0042, B:19:0x004d, B:20:0x0055, B:13:0x002a, B:11:0x0020, B:12:0x0029, B:22:0x0057, B:23:0x0060), top: B:31:0x0007, inners: #0, #2 }] */
    /* JADX WARN: Removed duplicated region for block: B:19:0x004d A[Catch: all -> 0x0061, TRY_ENTER, TryCatch #1 {all -> 0x0061, blocks: (B:5:0x0007, B:8:0x000c, B:14:0x003d, B:16:0x0042, B:19:0x004d, B:20:0x0055, B:13:0x002a, B:11:0x0020, B:12:0x0029, B:22:0x0057, B:23:0x0060), top: B:31:0x0007, inners: #0, #2 }] */
    @Override // javax.crypto.CipherSpi
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    protected int engineDoFinal(byte[] input, int inputOffset, int inputLen, byte[] output, int outputOffset) throws IllegalBlockSizeException, BadPaddingException, ShortBufferException {
        byte[] enc;
        ErasableOutputStream erasableOutputStream = this.wrapStream;
        if (erasableOutputStream == null) {
            throw new IllegalStateException("not supported in a wrapping mode");
        }
        erasableOutputStream.write(input, inputOffset, inputLen);
        try {
            if (this.forWrapping) {
                try {
                    enc = this.wrapEngine.wrap(this.wrapStream.getBuf(), 0, this.wrapStream.size());
                    if (enc.length + outputOffset <= output.length) {
                        throw new ShortBufferException("output buffer too short for input.");
                    }
                    System.arraycopy(enc, 0, output, outputOffset, enc.length);
                    return enc.length;
                } catch (Exception e) {
                    throw new IllegalBlockSizeException(e.getMessage());
                }
            }
            try {
                enc = this.wrapEngine.unwrap(this.wrapStream.getBuf(), 0, this.wrapStream.size());
                if (enc.length + outputOffset <= output.length) {
                }
            } catch (InvalidCipherTextException e2) {
                throw new BadPaddingException(e2.getMessage());
            }
        } finally {
            this.wrapStream.erase();
        }
    }

    @Override // javax.crypto.CipherSpi
    protected byte[] engineWrap(Key key) throws IllegalBlockSizeException, InvalidKeyException {
        byte[] encoded = key.getEncoded();
        if (encoded == null) {
            throw new InvalidKeyException("Cannot wrap key, null encoding.");
        }
        try {
            Wrapper wrapper = this.wrapEngine;
            if (wrapper == null) {
                return engineDoFinal(encoded, 0, encoded.length);
            }
            return wrapper.wrap(encoded, 0, encoded.length);
        } catch (BadPaddingException e) {
            throw new IllegalBlockSizeException(e.getMessage());
        }
    }

    @Override // javax.crypto.CipherSpi
    protected Key engineUnwrap(byte[] wrappedKey, String wrappedKeyAlgorithm, int wrappedKeyType) throws InvalidKeyException, NoSuchAlgorithmException {
        byte[] encoded;
        try {
            Wrapper wrapper = this.wrapEngine;
            if (wrapper == null) {
                encoded = engineDoFinal(wrappedKey, 0, wrappedKey.length);
            } else {
                encoded = wrapper.unwrap(wrappedKey, 0, wrappedKey.length);
            }
            if (wrappedKeyType == 3) {
                return new SecretKeySpec(encoded, wrappedKeyAlgorithm);
            }
            if (wrappedKeyAlgorithm.equals("") && wrappedKeyType == 2) {
                try {
                    PrivateKeyInfo in = PrivateKeyInfo.getInstance(encoded);
                    PrivateKey privKey = BouncyCastleProvider.getPrivateKey(in);
                    if (privKey != null) {
                        return privKey;
                    }
                    throw new InvalidKeyException("algorithm " + in.getPrivateKeyAlgorithm().getAlgorithm() + " not supported");
                } catch (Exception e) {
                    throw new InvalidKeyException("Invalid key encoding.");
                }
            }
            try {
                KeyFactory kf = this.helper.createKeyFactory(wrappedKeyAlgorithm);
                if (wrappedKeyType == 1) {
                    return kf.generatePublic(new X509EncodedKeySpec(encoded));
                }
                if (wrappedKeyType == 2) {
                    return kf.generatePrivate(new PKCS8EncodedKeySpec(encoded));
                }
                throw new InvalidKeyException("Unknown key type " + wrappedKeyType);
            } catch (NoSuchProviderException e2) {
                throw new InvalidKeyException("Unknown key type " + e2.getMessage());
            } catch (InvalidKeySpecException e22) {
                throw new InvalidKeyException("Unknown key type " + e22.getMessage());
            }
        } catch (InvalidCipherTextException e3) {
            throw new InvalidKeyException(e3.getMessage());
        } catch (BadPaddingException e4) {
            throw new InvalidKeyException(e4.getMessage());
        } catch (IllegalBlockSizeException e23) {
            throw new InvalidKeyException(e23.getMessage());
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: classes4.dex */
    public static final class ErasableOutputStream extends ByteArrayOutputStream {
        public byte[] getBuf() {
            return this.buf;
        }

        public void erase() {
            Arrays.fill(this.buf, (byte) 0);
            reset();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: classes4.dex */
    public static class InvalidKeyOrParametersException extends InvalidKeyException {
        private final Throwable cause;

        /* JADX INFO: Access modifiers changed from: package-private */
        public InvalidKeyOrParametersException(String msg, Throwable cause) {
            super(msg);
            this.cause = cause;
        }

        @Override // java.lang.Throwable
        public Throwable getCause() {
            return this.cause;
        }
    }
}
