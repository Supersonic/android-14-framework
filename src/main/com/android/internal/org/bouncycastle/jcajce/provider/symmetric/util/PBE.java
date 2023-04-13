package com.android.internal.org.bouncycastle.jcajce.provider.symmetric.util;

import com.android.internal.org.bouncycastle.crypto.CipherParameters;
import com.android.internal.org.bouncycastle.crypto.PBEParametersGenerator;
import com.android.internal.org.bouncycastle.crypto.digests.AndroidDigestFactory;
import com.android.internal.org.bouncycastle.crypto.generators.OpenSSLPBEParametersGenerator;
import com.android.internal.org.bouncycastle.crypto.generators.PKCS12ParametersGenerator;
import com.android.internal.org.bouncycastle.crypto.generators.PKCS5S1ParametersGenerator;
import com.android.internal.org.bouncycastle.crypto.generators.PKCS5S2ParametersGenerator;
import com.android.internal.org.bouncycastle.crypto.params.DESParameters;
import com.android.internal.org.bouncycastle.crypto.params.KeyParameter;
import com.android.internal.org.bouncycastle.crypto.params.ParametersWithIV;
import java.lang.reflect.Method;
import java.security.InvalidAlgorithmParameterException;
import java.security.spec.AlgorithmParameterSpec;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
/* loaded from: classes4.dex */
public interface PBE {
    public static final int MD5 = 0;
    public static final int OPENSSL = 3;
    public static final int PKCS12 = 2;
    public static final int PKCS5S1 = 0;
    public static final int PKCS5S1_UTF8 = 4;
    public static final int PKCS5S2 = 1;
    public static final int PKCS5S2_UTF8 = 5;
    public static final int SHA1 = 1;
    public static final int SHA224 = 7;
    public static final int SHA256 = 4;
    public static final int SHA384 = 8;
    public static final int SHA512 = 9;

    /* loaded from: classes4.dex */
    public static class Util {
        private static PBEParametersGenerator makePBEGenerator(int type, int hash) {
            if (type == 0 || type == 4) {
                switch (hash) {
                    case 0:
                        PBEParametersGenerator generator = new PKCS5S1ParametersGenerator(AndroidDigestFactory.getMD5());
                        return generator;
                    case 1:
                        PBEParametersGenerator generator2 = new PKCS5S1ParametersGenerator(AndroidDigestFactory.getSHA1());
                        return generator2;
                    default:
                        throw new IllegalStateException("PKCS5 scheme 1 only supports MD2, MD5 and SHA1.");
                }
            } else if (type == 1 || type == 5) {
                switch (hash) {
                    case 0:
                        PBEParametersGenerator generator3 = new PKCS5S2ParametersGenerator(AndroidDigestFactory.getMD5());
                        return generator3;
                    case 1:
                        PBEParametersGenerator generator4 = new PKCS5S2ParametersGenerator(AndroidDigestFactory.getSHA1());
                        return generator4;
                    case 2:
                    case 3:
                    case 5:
                    case 6:
                    default:
                        throw new IllegalStateException("unknown digest scheme for PBE PKCS5S2 encryption.");
                    case 4:
                        PBEParametersGenerator generator5 = new PKCS5S2ParametersGenerator(AndroidDigestFactory.getSHA256());
                        return generator5;
                    case 7:
                        PBEParametersGenerator generator6 = new PKCS5S2ParametersGenerator(AndroidDigestFactory.getSHA224());
                        return generator6;
                    case 8:
                        PBEParametersGenerator generator7 = new PKCS5S2ParametersGenerator(AndroidDigestFactory.getSHA384());
                        return generator7;
                    case 9:
                        PBEParametersGenerator generator8 = new PKCS5S2ParametersGenerator(AndroidDigestFactory.getSHA512());
                        return generator8;
                }
            } else if (type == 2) {
                switch (hash) {
                    case 0:
                        PBEParametersGenerator generator9 = new PKCS12ParametersGenerator(AndroidDigestFactory.getMD5());
                        return generator9;
                    case 1:
                        PBEParametersGenerator generator10 = new PKCS12ParametersGenerator(AndroidDigestFactory.getSHA1());
                        return generator10;
                    case 2:
                    case 3:
                    case 5:
                    case 6:
                    default:
                        throw new IllegalStateException("unknown digest scheme for PBE encryption.");
                    case 4:
                        PBEParametersGenerator generator11 = new PKCS12ParametersGenerator(AndroidDigestFactory.getSHA256());
                        return generator11;
                    case 7:
                        PBEParametersGenerator generator12 = new PKCS12ParametersGenerator(AndroidDigestFactory.getSHA224());
                        return generator12;
                    case 8:
                        PBEParametersGenerator generator13 = new PKCS12ParametersGenerator(AndroidDigestFactory.getSHA384());
                        return generator13;
                    case 9:
                        PBEParametersGenerator generator14 = new PKCS12ParametersGenerator(AndroidDigestFactory.getSHA512());
                        return generator14;
                }
            } else {
                PBEParametersGenerator generator15 = new OpenSSLPBEParametersGenerator();
                return generator15;
            }
        }

        public static CipherParameters makePBEParameters(byte[] pbeKey, int scheme, int digest, int keySize, int ivSize, AlgorithmParameterSpec spec, String targetAlgorithm) throws InvalidAlgorithmParameterException {
            CipherParameters param;
            if (spec == null || !(spec instanceof PBEParameterSpec)) {
                throw new InvalidAlgorithmParameterException("Need a PBEParameter spec with a PBE key.");
            }
            PBEParameterSpec pbeParam = (PBEParameterSpec) spec;
            PBEParametersGenerator generator = makePBEGenerator(scheme, digest);
            generator.init(pbeKey, pbeParam.getSalt(), pbeParam.getIterationCount());
            if (ivSize != 0) {
                param = generator.generateDerivedParameters(keySize, ivSize);
                AlgorithmParameterSpec parameterSpecFromPBEParameterSpec = getParameterSpecFromPBEParameterSpec(pbeParam);
                if ((scheme == 1 || scheme == 5) && (parameterSpecFromPBEParameterSpec instanceof IvParameterSpec)) {
                    ParametersWithIV parametersWithIV = (ParametersWithIV) param;
                    IvParameterSpec ivParameterSpec = (IvParameterSpec) parameterSpecFromPBEParameterSpec;
                    param = new ParametersWithIV((KeyParameter) parametersWithIV.getParameters(), ivParameterSpec.getIV());
                }
            } else {
                param = generator.generateDerivedParameters(keySize);
            }
            if (targetAlgorithm.startsWith("DES")) {
                if (param instanceof ParametersWithIV) {
                    KeyParameter kParam = (KeyParameter) ((ParametersWithIV) param).getParameters();
                    DESParameters.setOddParity(kParam.getKey());
                } else {
                    KeyParameter kParam2 = (KeyParameter) param;
                    DESParameters.setOddParity(kParam2.getKey());
                }
            }
            return param;
        }

        public static CipherParameters makePBEParameters(BCPBEKey pbeKey, AlgorithmParameterSpec spec, String targetAlgorithm) {
            CipherParameters param;
            if (spec == null || !(spec instanceof PBEParameterSpec)) {
                throw new IllegalArgumentException("Need a PBEParameter spec with a PBE key.");
            }
            PBEParameterSpec pbeParam = (PBEParameterSpec) spec;
            PBEParametersGenerator generator = makePBEGenerator(pbeKey.getType(), pbeKey.getDigest());
            byte[] key = pbeKey.getEncoded();
            if (pbeKey.shouldTryWrongPKCS12()) {
                key = new byte[2];
            }
            generator.init(key, pbeParam.getSalt(), pbeParam.getIterationCount());
            if (pbeKey.getIvSize() != 0) {
                param = generator.generateDerivedParameters(pbeKey.getKeySize(), pbeKey.getIvSize());
                AlgorithmParameterSpec parameterSpecFromPBEParameterSpec = getParameterSpecFromPBEParameterSpec(pbeParam);
                if ((pbeKey.getType() == 1 || pbeKey.getType() == 5) && (parameterSpecFromPBEParameterSpec instanceof IvParameterSpec)) {
                    ParametersWithIV parametersWithIV = (ParametersWithIV) param;
                    IvParameterSpec ivParameterSpec = (IvParameterSpec) parameterSpecFromPBEParameterSpec;
                    param = new ParametersWithIV((KeyParameter) parametersWithIV.getParameters(), ivParameterSpec.getIV());
                }
            } else {
                param = generator.generateDerivedParameters(pbeKey.getKeySize());
            }
            if (targetAlgorithm.startsWith("DES")) {
                if (param instanceof ParametersWithIV) {
                    KeyParameter kParam = (KeyParameter) ((ParametersWithIV) param).getParameters();
                    DESParameters.setOddParity(kParam.getKey());
                } else {
                    KeyParameter kParam2 = (KeyParameter) param;
                    DESParameters.setOddParity(kParam2.getKey());
                }
            }
            return param;
        }

        public static CipherParameters makePBEMacParameters(BCPBEKey pbeKey, AlgorithmParameterSpec spec) {
            if (spec == null || !(spec instanceof PBEParameterSpec)) {
                throw new IllegalArgumentException("Need a PBEParameter spec with a PBE key.");
            }
            PBEParameterSpec pbeParam = (PBEParameterSpec) spec;
            PBEParametersGenerator generator = makePBEGenerator(pbeKey.getType(), pbeKey.getDigest());
            byte[] key = pbeKey.getEncoded();
            generator.init(key, pbeParam.getSalt(), pbeParam.getIterationCount());
            CipherParameters param = generator.generateDerivedMacParameters(pbeKey.getKeySize());
            return param;
        }

        public static CipherParameters makePBEMacParameters(PBEKeySpec keySpec, int type, int hash, int keySize) {
            PBEParametersGenerator generator = makePBEGenerator(type, hash);
            byte[] key = convertPassword(type, keySpec);
            generator.init(key, keySpec.getSalt(), keySpec.getIterationCount());
            CipherParameters param = generator.generateDerivedMacParameters(keySize);
            for (int i = 0; i != key.length; i++) {
                key[i] = 0;
            }
            return param;
        }

        public static CipherParameters makePBEParameters(PBEKeySpec keySpec, int type, int hash, int keySize, int ivSize) {
            CipherParameters param;
            PBEParametersGenerator generator = makePBEGenerator(type, hash);
            byte[] key = convertPassword(type, keySpec);
            generator.init(key, keySpec.getSalt(), keySpec.getIterationCount());
            if (ivSize != 0) {
                param = generator.generateDerivedParameters(keySize, ivSize);
            } else {
                param = generator.generateDerivedParameters(keySize);
            }
            for (int i = 0; i != key.length; i++) {
                key[i] = 0;
            }
            return param;
        }

        public static CipherParameters makePBEMacParameters(SecretKey key, int type, int hash, int keySize, PBEParameterSpec pbeSpec) {
            PBEParametersGenerator generator = makePBEGenerator(type, hash);
            byte[] keyBytes = key.getEncoded();
            generator.init(key.getEncoded(), pbeSpec.getSalt(), pbeSpec.getIterationCount());
            CipherParameters param = generator.generateDerivedMacParameters(keySize);
            for (int i = 0; i != keyBytes.length; i++) {
                keyBytes[i] = 0;
            }
            return param;
        }

        public static AlgorithmParameterSpec getParameterSpecFromPBEParameterSpec(PBEParameterSpec pbeParameterSpec) {
            try {
                Method getParameterSpecMethod = PBE.class.getClassLoader().loadClass("javax.crypto.spec.PBEParameterSpec").getMethod("getParameterSpec", new Class[0]);
                return (AlgorithmParameterSpec) getParameterSpecMethod.invoke(pbeParameterSpec, new Object[0]);
            } catch (Exception e) {
                return null;
            }
        }

        private static byte[] convertPassword(int type, PBEKeySpec keySpec) {
            if (type == 2) {
                byte[] key = PBEParametersGenerator.PKCS12PasswordToBytes(keySpec.getPassword());
                return key;
            } else if (type == 5 || type == 4) {
                byte[] key2 = PBEParametersGenerator.PKCS5PasswordToUTF8Bytes(keySpec.getPassword());
                return key2;
            } else {
                byte[] key3 = PBEParametersGenerator.PKCS5PasswordToBytes(keySpec.getPassword());
                return key3;
            }
        }
    }
}
