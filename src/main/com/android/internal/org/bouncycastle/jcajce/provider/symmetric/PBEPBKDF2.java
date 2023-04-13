package com.android.internal.org.bouncycastle.jcajce.provider.symmetric;

import com.android.internal.org.bouncycastle.asn1.ASN1Encoding;
import com.android.internal.org.bouncycastle.asn1.ASN1ObjectIdentifier;
import com.android.internal.org.bouncycastle.asn1.ASN1Primitive;
import com.android.internal.org.bouncycastle.asn1.pkcs.PBKDF2Params;
import com.android.internal.org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import com.android.internal.org.bouncycastle.crypto.CipherParameters;
import com.android.internal.org.bouncycastle.jcajce.provider.config.ConfigurableProvider;
import com.android.internal.org.bouncycastle.jcajce.provider.symmetric.util.BCPBEKey;
import com.android.internal.org.bouncycastle.jcajce.provider.symmetric.util.BaseAlgorithmParameters;
import com.android.internal.org.bouncycastle.jcajce.provider.symmetric.util.BaseSecretKeyFactory;
import com.android.internal.org.bouncycastle.jcajce.provider.symmetric.util.PBE;
import com.android.internal.org.bouncycastle.jcajce.provider.util.AlgorithmProvider;
import com.android.internal.org.bouncycastle.jcajce.spec.PBKDF2KeySpec;
import com.android.internal.org.bouncycastle.util.Integers;
import java.io.IOException;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.InvalidParameterSpecException;
import java.security.spec.KeySpec;
import java.util.HashMap;
import java.util.Map;
import javax.crypto.SecretKey;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
/* loaded from: classes4.dex */
public class PBEPBKDF2 {
    private static final Map prfCodes;

    static {
        HashMap hashMap = new HashMap();
        prfCodes = hashMap;
        hashMap.put(PKCSObjectIdentifiers.id_hmacWithSHA1, Integers.valueOf(1));
        hashMap.put(PKCSObjectIdentifiers.id_hmacWithSHA256, Integers.valueOf(4));
        hashMap.put(PKCSObjectIdentifiers.id_hmacWithSHA224, Integers.valueOf(7));
        hashMap.put(PKCSObjectIdentifiers.id_hmacWithSHA384, Integers.valueOf(8));
        hashMap.put(PKCSObjectIdentifiers.id_hmacWithSHA512, Integers.valueOf(9));
    }

    private PBEPBKDF2() {
    }

    /* loaded from: classes4.dex */
    public static class AlgParams extends BaseAlgorithmParameters {
        PBKDF2Params params;

        @Override // java.security.AlgorithmParametersSpi
        protected byte[] engineGetEncoded() {
            try {
                return this.params.getEncoded(ASN1Encoding.DER);
            } catch (IOException e) {
                throw new RuntimeException("Oooops! " + e.toString());
            }
        }

        @Override // java.security.AlgorithmParametersSpi
        protected byte[] engineGetEncoded(String format) {
            if (isASN1FormatString(format)) {
                return engineGetEncoded();
            }
            return null;
        }

        @Override // com.android.internal.org.bouncycastle.jcajce.provider.symmetric.util.BaseAlgorithmParameters
        protected AlgorithmParameterSpec localEngineGetParameterSpec(Class paramSpec) throws InvalidParameterSpecException {
            if (paramSpec == PBEParameterSpec.class || paramSpec == AlgorithmParameterSpec.class) {
                return new PBEParameterSpec(this.params.getSalt(), this.params.getIterationCount().intValue());
            }
            throw new InvalidParameterSpecException("unknown parameter spec passed to PBKDF2 PBE parameters object.");
        }

        @Override // java.security.AlgorithmParametersSpi
        protected void engineInit(AlgorithmParameterSpec paramSpec) throws InvalidParameterSpecException {
            if (!(paramSpec instanceof PBEParameterSpec)) {
                throw new InvalidParameterSpecException("PBEParameterSpec required to initialise a PBKDF2 PBE parameters algorithm parameters object");
            }
            PBEParameterSpec pbeSpec = (PBEParameterSpec) paramSpec;
            this.params = new PBKDF2Params(pbeSpec.getSalt(), pbeSpec.getIterationCount());
        }

        @Override // java.security.AlgorithmParametersSpi
        protected void engineInit(byte[] params) throws IOException {
            this.params = PBKDF2Params.getInstance(ASN1Primitive.fromByteArray(params));
        }

        @Override // java.security.AlgorithmParametersSpi
        protected void engineInit(byte[] params, String format) throws IOException {
            if (isASN1FormatString(format)) {
                engineInit(params);
                return;
            }
            throw new IOException("Unknown parameters format in PBKDF2 parameters object");
        }

        @Override // java.security.AlgorithmParametersSpi
        protected String engineToString() {
            return "PBKDF2 Parameters";
        }
    }

    /* loaded from: classes4.dex */
    public static class BasePBKDF2 extends BaseSecretKeyFactory {
        private int defaultDigest;
        private int ivSizeInBits;
        private int keySizeInBits;
        private int scheme;

        public BasePBKDF2(String name, int scheme) {
            this(name, scheme, 1);
        }

        private BasePBKDF2(String name, int scheme, int digest, int keySizeInBits, int ivSizeInBits) {
            super(name, PKCSObjectIdentifiers.id_PBKDF2);
            this.scheme = scheme;
            this.keySizeInBits = keySizeInBits;
            this.ivSizeInBits = ivSizeInBits;
            this.defaultDigest = digest;
        }

        private BasePBKDF2(String name, int scheme, int digest) {
            this(name, scheme, digest, 0, 0);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.android.internal.org.bouncycastle.jcajce.provider.symmetric.util.BaseSecretKeyFactory, javax.crypto.SecretKeyFactorySpi
        public SecretKey engineGenerateSecret(KeySpec keySpec) throws InvalidKeySpecException {
            if (keySpec instanceof PBEKeySpec) {
                PBEKeySpec pbeSpec = (PBEKeySpec) keySpec;
                if (pbeSpec.getSalt() == null && pbeSpec.getIterationCount() == 0 && pbeSpec.getKeyLength() == 0 && pbeSpec.getPassword().length > 0 && this.keySizeInBits != 0) {
                    return new BCPBEKey(this.algName, this.algOid, this.scheme, this.defaultDigest, this.keySizeInBits, this.ivSizeInBits, pbeSpec, null);
                }
                if (pbeSpec.getSalt() == null) {
                    throw new InvalidKeySpecException("missing required salt");
                }
                if (pbeSpec.getIterationCount() <= 0) {
                    throw new InvalidKeySpecException("positive iteration count required: " + pbeSpec.getIterationCount());
                }
                if (pbeSpec.getKeyLength() <= 0) {
                    throw new InvalidKeySpecException("positive key length required: " + pbeSpec.getKeyLength());
                }
                if (pbeSpec.getPassword().length == 0) {
                    throw new IllegalArgumentException("password empty");
                }
                if (pbeSpec instanceof PBKDF2KeySpec) {
                    PBKDF2KeySpec spec = (PBKDF2KeySpec) pbeSpec;
                    int digest = getDigestCode(spec.getPrf().getAlgorithm());
                    int keySize = pbeSpec.getKeyLength();
                    CipherParameters param = PBE.Util.makePBEMacParameters(pbeSpec, this.scheme, digest, keySize);
                    return new BCPBEKey(this.algName, this.algOid, this.scheme, digest, keySize, -1, pbeSpec, param);
                }
                int digest2 = this.defaultDigest;
                int keySize2 = pbeSpec.getKeyLength();
                CipherParameters param2 = PBE.Util.makePBEMacParameters(pbeSpec, this.scheme, digest2, keySize2);
                return new BCPBEKey(this.algName, this.algOid, this.scheme, digest2, keySize2, -1, pbeSpec, param2);
            }
            throw new InvalidKeySpecException("Invalid KeySpec");
        }

        private int getDigestCode(ASN1ObjectIdentifier algorithm) throws InvalidKeySpecException {
            Integer code = (Integer) PBEPBKDF2.prfCodes.get(algorithm);
            if (code != null) {
                return code.intValue();
            }
            throw new InvalidKeySpecException("Invalid KeySpec: unknown PRF algorithm " + algorithm);
        }
    }

    /* loaded from: classes4.dex */
    public static class PBKDF2withUTF8 extends BasePBKDF2 {
        public PBKDF2withUTF8() {
            super("PBKDF2", 5);
        }
    }

    /* loaded from: classes4.dex */
    public static class BasePBKDF2WithHmacSHA1 extends BasePBKDF2 {
        public BasePBKDF2WithHmacSHA1(String name, int scheme) {
            super(name, scheme, 1);
        }
    }

    /* loaded from: classes4.dex */
    public static class PBKDF2WithHmacSHA1UTF8 extends BasePBKDF2WithHmacSHA1 {
        public PBKDF2WithHmacSHA1UTF8() {
            super("PBKDF2WithHmacSHA1", 5);
        }
    }

    /* loaded from: classes4.dex */
    public static class PBKDF2WithHmacSHA18BIT extends BasePBKDF2WithHmacSHA1 {
        public PBKDF2WithHmacSHA18BIT() {
            super("PBKDF2WithHmacSHA1And8bit", 1);
        }
    }

    /* loaded from: classes4.dex */
    public static class BasePBKDF2WithHmacSHA224 extends BasePBKDF2 {
        public BasePBKDF2WithHmacSHA224(String name, int scheme) {
            super(name, scheme, 7);
        }
    }

    /* loaded from: classes4.dex */
    public static class PBKDF2WithHmacSHA224UTF8 extends BasePBKDF2WithHmacSHA224 {
        public PBKDF2WithHmacSHA224UTF8() {
            super("PBKDF2WithHmacSHA224", 5);
        }
    }

    /* loaded from: classes4.dex */
    public static class BasePBKDF2WithHmacSHA256 extends BasePBKDF2 {
        public BasePBKDF2WithHmacSHA256(String name, int scheme) {
            super(name, scheme, 4);
        }
    }

    /* loaded from: classes4.dex */
    public static class PBKDF2WithHmacSHA256UTF8 extends BasePBKDF2WithHmacSHA256 {
        public PBKDF2WithHmacSHA256UTF8() {
            super("PBKDF2WithHmacSHA256", 5);
        }
    }

    /* loaded from: classes4.dex */
    public static class BasePBKDF2WithHmacSHA384 extends BasePBKDF2 {
        public BasePBKDF2WithHmacSHA384(String name, int scheme) {
            super(name, scheme, 8);
        }
    }

    /* loaded from: classes4.dex */
    public static class PBKDF2WithHmacSHA384UTF8 extends BasePBKDF2WithHmacSHA384 {
        public PBKDF2WithHmacSHA384UTF8() {
            super("PBKDF2WithHmacSHA384", 5);
        }
    }

    /* loaded from: classes4.dex */
    public static class BasePBKDF2WithHmacSHA512 extends BasePBKDF2 {
        public BasePBKDF2WithHmacSHA512(String name, int scheme) {
            super(name, scheme, 9);
        }
    }

    /* loaded from: classes4.dex */
    public static class PBKDF2WithHmacSHA512UTF8 extends BasePBKDF2WithHmacSHA512 {
        public PBKDF2WithHmacSHA512UTF8() {
            super("PBKDF2WithHmacSHA512", 5);
        }
    }

    /* loaded from: classes4.dex */
    public static class PBEWithHmacSHA1AndAES_128 extends BasePBKDF2 {
        public PBEWithHmacSHA1AndAES_128() {
            super("PBEWithHmacSHA1AndAES_128", 5, 1, 128, 128);
        }
    }

    /* loaded from: classes4.dex */
    public static class PBEWithHmacSHA224AndAES_128 extends BasePBKDF2 {
        public PBEWithHmacSHA224AndAES_128() {
            super("PBEWithHmacSHA224AndAES_128", 5, 7, 128, 128);
        }
    }

    /* loaded from: classes4.dex */
    public static class PBEWithHmacSHA256AndAES_128 extends BasePBKDF2 {
        public PBEWithHmacSHA256AndAES_128() {
            super("PBEWithHmacSHA256AndAES_128", 5, 4, 128, 128);
        }
    }

    /* loaded from: classes4.dex */
    public static class PBEWithHmacSHA384AndAES_128 extends BasePBKDF2 {
        public PBEWithHmacSHA384AndAES_128() {
            super("PBEWithHmacSHA384AndAES_128", 5, 8, 128, 128);
        }
    }

    /* loaded from: classes4.dex */
    public static class PBEWithHmacSHA512AndAES_128 extends BasePBKDF2 {
        public PBEWithHmacSHA512AndAES_128() {
            super("PBEWithHmacSHA512AndAES_128", 5, 9, 128, 128);
        }
    }

    /* loaded from: classes4.dex */
    public static class PBEWithHmacSHA1AndAES_256 extends BasePBKDF2 {
        public PBEWithHmacSHA1AndAES_256() {
            super("PBEWithHmacSHA1AndAES_256", 5, 1, 256, 128);
        }
    }

    /* loaded from: classes4.dex */
    public static class PBEWithHmacSHA224AndAES_256 extends BasePBKDF2 {
        public PBEWithHmacSHA224AndAES_256() {
            super("PBEWithHmacSHA224AndAES_256", 5, 7, 256, 128);
        }
    }

    /* loaded from: classes4.dex */
    public static class PBEWithHmacSHA256AndAES_256 extends BasePBKDF2 {
        public PBEWithHmacSHA256AndAES_256() {
            super("PBEWithHmacSHA256AndAES_256", 5, 4, 256, 128);
        }
    }

    /* loaded from: classes4.dex */
    public static class PBEWithHmacSHA384AndAES_256 extends BasePBKDF2 {
        public PBEWithHmacSHA384AndAES_256() {
            super("PBEWithHmacSHA384AndAES_256", 5, 8, 256, 128);
        }
    }

    /* loaded from: classes4.dex */
    public static class PBEWithHmacSHA512AndAES_256 extends BasePBKDF2 {
        public PBEWithHmacSHA512AndAES_256() {
            super("PBEWithHmacSHA512AndAES_256", 5, 9, 256, 128);
        }
    }

    /* loaded from: classes4.dex */
    public static class Mappings extends AlgorithmProvider {
        private static final String PREFIX = PBEPBKDF2.class.getName();

        @Override // com.android.internal.org.bouncycastle.jcajce.provider.util.AlgorithmProvider
        public void configure(ConfigurableProvider provider) {
            provider.addAlgorithm("Alg.Alias.SecretKeyFactory.PBKDF2WithHmacSHA1AndUTF8", "PBKDF2WithHmacSHA1");
            provider.addAlgorithm("Alg.Alias.SecretKeyFactory.PBKDF2with8BIT", "PBKDF2WithHmacSHA1And8BIT");
            provider.addAlgorithm("Alg.Alias.SecretKeyFactory.PBKDF2withASCII", "PBKDF2WithHmacSHA1And8BIT");
            StringBuilder sb = new StringBuilder();
            String str = PREFIX;
            provider.addAlgorithm("SecretKeyFactory.PBKDF2WithHmacSHA1", sb.append(str).append("$PBKDF2WithHmacSHA1UTF8").toString());
            provider.addAlgorithm("SecretKeyFactory.PBKDF2WithHmacSHA224", str + "$PBKDF2WithHmacSHA224UTF8");
            provider.addAlgorithm("SecretKeyFactory.PBKDF2WithHmacSHA256", str + "$PBKDF2WithHmacSHA256UTF8");
            provider.addAlgorithm("SecretKeyFactory.PBKDF2WithHmacSHA384", str + "$PBKDF2WithHmacSHA384UTF8");
            provider.addAlgorithm("SecretKeyFactory.PBKDF2WithHmacSHA512", str + "$PBKDF2WithHmacSHA512UTF8");
            provider.addAlgorithm("SecretKeyFactory.PBEWithHmacSHA1AndAES_128", str + "$PBEWithHmacSHA1AndAES_128");
            provider.addAlgorithm("SecretKeyFactory.PBEWithHmacSHA224AndAES_128", str + "$PBEWithHmacSHA224AndAES_128");
            provider.addAlgorithm("SecretKeyFactory.PBEWithHmacSHA256AndAES_128", str + "$PBEWithHmacSHA256AndAES_128");
            provider.addAlgorithm("SecretKeyFactory.PBEWithHmacSHA384AndAES_128", str + "$PBEWithHmacSHA384AndAES_128");
            provider.addAlgorithm("SecretKeyFactory.PBEWithHmacSHA512AndAES_128", str + "$PBEWithHmacSHA512AndAES_128");
            provider.addAlgorithm("SecretKeyFactory.PBEWithHmacSHA1AndAES_256", str + "$PBEWithHmacSHA1AndAES_256");
            provider.addAlgorithm("SecretKeyFactory.PBEWithHmacSHA224AndAES_256", str + "$PBEWithHmacSHA224AndAES_256");
            provider.addAlgorithm("SecretKeyFactory.PBEWithHmacSHA256AndAES_256", str + "$PBEWithHmacSHA256AndAES_256");
            provider.addAlgorithm("SecretKeyFactory.PBEWithHmacSHA384AndAES_256", str + "$PBEWithHmacSHA384AndAES_256");
            provider.addAlgorithm("SecretKeyFactory.PBEWithHmacSHA512AndAES_256", str + "$PBEWithHmacSHA512AndAES_256");
            provider.addAlgorithm("SecretKeyFactory.PBKDF2WithHmacSHA1And8BIT", str + "$PBKDF2WithHmacSHA18BIT");
            provider.addPrivateAlgorithm("SecretKeyFactory.PBKDF2", str + "$PBKDF2withUTF8");
            provider.addPrivateAlgorithm("Alg.Alias.SecretKeyFactory.1.2.840.113549.1.5.12", "PBKDF2");
        }
    }
}
