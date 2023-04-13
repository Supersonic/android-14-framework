package com.android.internal.org.bouncycastle.jcajce.provider.symmetric;

import android.security.keystore.KeyProperties;
import com.android.internal.org.bouncycastle.asn1.ASN1Encodable;
import com.android.internal.org.bouncycastle.asn1.ASN1ObjectIdentifier;
import com.android.internal.org.bouncycastle.asn1.ASN1OctetString;
import com.android.internal.org.bouncycastle.asn1.ASN1Primitive;
import com.android.internal.org.bouncycastle.asn1.ASN1Sequence;
import com.android.internal.org.bouncycastle.asn1.DERNull;
import com.android.internal.org.bouncycastle.asn1.DEROctetString;
import com.android.internal.org.bouncycastle.asn1.DERSequence;
import com.android.internal.org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import com.android.internal.org.bouncycastle.asn1.pkcs.EncryptionScheme;
import com.android.internal.org.bouncycastle.asn1.pkcs.KeyDerivationFunc;
import com.android.internal.org.bouncycastle.asn1.pkcs.PBES2Parameters;
import com.android.internal.org.bouncycastle.asn1.pkcs.PBKDF2Params;
import com.android.internal.org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import com.android.internal.org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import com.android.internal.org.bouncycastle.jcajce.provider.config.ConfigurableProvider;
import com.android.internal.org.bouncycastle.jcajce.provider.symmetric.util.BaseAlgorithmParameters;
import com.android.internal.org.bouncycastle.jcajce.provider.symmetric.util.PBE;
import com.android.internal.org.bouncycastle.jcajce.provider.util.AlgorithmProvider;
import java.io.IOException;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.InvalidParameterSpecException;
import java.util.Enumeration;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEParameterSpec;
/* loaded from: classes4.dex */
public class PBES2AlgorithmParameters {
    private PBES2AlgorithmParameters() {
    }

    /* loaded from: classes4.dex */
    private static abstract class BasePBEWithHmacAlgorithmParameters extends BaseAlgorithmParameters {
        private final ASN1ObjectIdentifier cipherAlgorithm;
        private final String cipherAlgorithmShortName;
        private final AlgorithmIdentifier kdf;
        private final String kdfShortName;
        private final int keySize;
        private PBES2Parameters params;

        private BasePBEWithHmacAlgorithmParameters(ASN1ObjectIdentifier kdf, String kdfShortName, int keySize, ASN1ObjectIdentifier cipherAlgorithm, String cipherAlgorithmShortName) {
            this.kdf = new AlgorithmIdentifier(kdf, DERNull.INSTANCE);
            this.kdfShortName = kdfShortName;
            this.keySize = keySize;
            this.cipherAlgorithm = cipherAlgorithm;
            this.cipherAlgorithmShortName = cipherAlgorithmShortName;
        }

        @Override // java.security.AlgorithmParametersSpi
        protected byte[] engineGetEncoded() {
            try {
                return new DERSequence(new ASN1Encodable[]{PKCSObjectIdentifiers.id_PBES2, this.params}).getEncoded();
            } catch (IOException e) {
                throw new RuntimeException("Unable to read PBES2 parameters: " + e.toString());
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
        protected AlgorithmParameterSpec localEngineGetParameterSpec(Class parameterSpec) throws InvalidParameterSpecException {
            if (parameterSpec == PBEParameterSpec.class) {
                PBKDF2Params pbeParamSpec = (PBKDF2Params) this.params.getKeyDerivationFunc().getParameters();
                byte[] iv = ((ASN1OctetString) this.params.getEncryptionScheme().getParameters()).getOctets();
                return PBES2AlgorithmParameters.createPBEParameterSpec(pbeParamSpec.getSalt(), pbeParamSpec.getIterationCount().intValue(), iv);
            }
            throw new InvalidParameterSpecException("unknown parameter spec passed to PBES2 parameters object.");
        }

        @Override // java.security.AlgorithmParametersSpi
        protected void engineInit(AlgorithmParameterSpec paramSpec) throws InvalidParameterSpecException {
            if (!(paramSpec instanceof PBEParameterSpec)) {
                throw new InvalidParameterSpecException("PBEParameterSpec required to initialise PBES2 algorithm parameters");
            }
            PBEParameterSpec pbeSpec = (PBEParameterSpec) paramSpec;
            AlgorithmParameterSpec algorithmParameterSpec = PBE.Util.getParameterSpecFromPBEParameterSpec(pbeSpec);
            if (algorithmParameterSpec instanceof IvParameterSpec) {
                byte[] iv = ((IvParameterSpec) algorithmParameterSpec).getIV();
                this.params = new PBES2Parameters(new KeyDerivationFunc(PKCSObjectIdentifiers.id_PBKDF2, new PBKDF2Params(pbeSpec.getSalt(), pbeSpec.getIterationCount(), this.keySize, this.kdf)), new EncryptionScheme(this.cipherAlgorithm, new DEROctetString(iv)));
                return;
            }
            throw new IllegalArgumentException("Expecting an IV as a parameter");
        }

        @Override // java.security.AlgorithmParametersSpi
        protected void engineInit(byte[] params) throws IOException {
            ASN1Sequence seq = ASN1Sequence.getInstance(ASN1Primitive.fromByteArray(params));
            Enumeration seqObjects = seq.getObjects();
            ASN1ObjectIdentifier id = (ASN1ObjectIdentifier) seqObjects.nextElement();
            if (!id.getId().equals(PKCSObjectIdentifiers.id_PBES2.getId())) {
                throw new IllegalArgumentException("Invalid PBES2 parameters");
            }
            this.params = PBES2Parameters.getInstance(seqObjects.nextElement());
        }

        @Override // java.security.AlgorithmParametersSpi
        protected void engineInit(byte[] params, String format) throws IOException {
            if (isASN1FormatString(format)) {
                engineInit(params);
                return;
            }
            throw new IOException("Unknown parameters format in PBES2 parameters object");
        }

        @Override // java.security.AlgorithmParametersSpi
        protected String engineToString() {
            return "PBES2 " + this.kdfShortName + " " + this.cipherAlgorithmShortName + " Parameters";
        }
    }

    /* loaded from: classes4.dex */
    public static class PBEWithHmacSHA1AES128AlgorithmParameters extends BasePBEWithHmacAlgorithmParameters {
        public PBEWithHmacSHA1AES128AlgorithmParameters() {
            super(PKCSObjectIdentifiers.id_hmacWithSHA1, KeyProperties.KEY_ALGORITHM_HMAC_SHA1, 16, NISTObjectIdentifiers.id_aes128_CBC, "AES128");
        }
    }

    /* loaded from: classes4.dex */
    public static class PBEWithHmacSHA224AES128AlgorithmParameters extends BasePBEWithHmacAlgorithmParameters {
        public PBEWithHmacSHA224AES128AlgorithmParameters() {
            super(PKCSObjectIdentifiers.id_hmacWithSHA224, KeyProperties.KEY_ALGORITHM_HMAC_SHA224, 16, NISTObjectIdentifiers.id_aes128_CBC, "AES128");
        }
    }

    /* loaded from: classes4.dex */
    public static class PBEWithHmacSHA256AES128AlgorithmParameters extends BasePBEWithHmacAlgorithmParameters {
        public PBEWithHmacSHA256AES128AlgorithmParameters() {
            super(PKCSObjectIdentifiers.id_hmacWithSHA256, KeyProperties.KEY_ALGORITHM_HMAC_SHA256, 16, NISTObjectIdentifiers.id_aes128_CBC, "AES128");
        }
    }

    /* loaded from: classes4.dex */
    public static class PBEWithHmacSHA384AES128AlgorithmParameters extends BasePBEWithHmacAlgorithmParameters {
        public PBEWithHmacSHA384AES128AlgorithmParameters() {
            super(PKCSObjectIdentifiers.id_hmacWithSHA384, KeyProperties.KEY_ALGORITHM_HMAC_SHA384, 16, NISTObjectIdentifiers.id_aes128_CBC, "AES128");
        }
    }

    /* loaded from: classes4.dex */
    public static class PBEWithHmacSHA512AES128AlgorithmParameters extends BasePBEWithHmacAlgorithmParameters {
        public PBEWithHmacSHA512AES128AlgorithmParameters() {
            super(PKCSObjectIdentifiers.id_hmacWithSHA512, KeyProperties.KEY_ALGORITHM_HMAC_SHA512, 16, NISTObjectIdentifiers.id_aes128_CBC, "AES128");
        }
    }

    /* loaded from: classes4.dex */
    public static class PBEWithHmacSHA1AES256AlgorithmParameters extends BasePBEWithHmacAlgorithmParameters {
        public PBEWithHmacSHA1AES256AlgorithmParameters() {
            super(PKCSObjectIdentifiers.id_hmacWithSHA1, KeyProperties.KEY_ALGORITHM_HMAC_SHA1, 32, NISTObjectIdentifiers.id_aes256_CBC, "AES256");
        }
    }

    /* loaded from: classes4.dex */
    public static class PBEWithHmacSHA224AES256AlgorithmParameters extends BasePBEWithHmacAlgorithmParameters {
        public PBEWithHmacSHA224AES256AlgorithmParameters() {
            super(PKCSObjectIdentifiers.id_hmacWithSHA224, KeyProperties.KEY_ALGORITHM_HMAC_SHA224, 32, NISTObjectIdentifiers.id_aes256_CBC, "AES256");
        }
    }

    /* loaded from: classes4.dex */
    public static class PBEWithHmacSHA256AES256AlgorithmParameters extends BasePBEWithHmacAlgorithmParameters {
        public PBEWithHmacSHA256AES256AlgorithmParameters() {
            super(PKCSObjectIdentifiers.id_hmacWithSHA256, KeyProperties.KEY_ALGORITHM_HMAC_SHA256, 32, NISTObjectIdentifiers.id_aes256_CBC, "AES256");
        }
    }

    /* loaded from: classes4.dex */
    public static class PBEWithHmacSHA384AES256AlgorithmParameters extends BasePBEWithHmacAlgorithmParameters {
        public PBEWithHmacSHA384AES256AlgorithmParameters() {
            super(PKCSObjectIdentifiers.id_hmacWithSHA384, KeyProperties.KEY_ALGORITHM_HMAC_SHA384, 32, NISTObjectIdentifiers.id_aes256_CBC, "AES256");
        }
    }

    /* loaded from: classes4.dex */
    public static class PBEWithHmacSHA512AES256AlgorithmParameters extends BasePBEWithHmacAlgorithmParameters {
        public PBEWithHmacSHA512AES256AlgorithmParameters() {
            super(PKCSObjectIdentifiers.id_hmacWithSHA512, KeyProperties.KEY_ALGORITHM_HMAC_SHA512, 32, NISTObjectIdentifiers.id_aes256_CBC, "AES256");
        }
    }

    /* loaded from: classes4.dex */
    public static class Mappings extends AlgorithmProvider {
        private static final String PREFIX = PBES2AlgorithmParameters.class.getName();

        @Override // com.android.internal.org.bouncycastle.jcajce.provider.util.AlgorithmProvider
        public void configure(ConfigurableProvider provider) {
            int[] keySizes = {128, 256};
            int[] shaVariants = {1, 224, 256, 384, 512};
            for (int keySize : keySizes) {
                for (int shaVariant : shaVariants) {
                    provider.addAlgorithm("AlgorithmParameters.PBEWithHmacSHA" + shaVariant + "AndAES_" + keySize, PREFIX + "$PBEWithHmacSHA" + shaVariant + KeyProperties.KEY_ALGORITHM_AES + keySize + "AlgorithmParameters");
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static PBEParameterSpec createPBEParameterSpec(byte[] salt, int iterationCount, byte[] iv) {
        try {
            return (PBEParameterSpec) PBES2AlgorithmParameters.class.getClassLoader().loadClass("javax.crypto.spec.PBEParameterSpec").getConstructor(byte[].class, Integer.TYPE, AlgorithmParameterSpec.class).newInstance(salt, Integer.valueOf(iterationCount), new IvParameterSpec(iv));
        } catch (Exception e) {
            throw new IllegalStateException("Requested creation PBES2 parameters in an SDK that doesn't support them", e);
        }
    }
}
