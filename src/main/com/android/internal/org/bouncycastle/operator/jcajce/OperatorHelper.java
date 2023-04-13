package com.android.internal.org.bouncycastle.operator.jcajce;

import android.security.keystore.KeyProperties;
import com.android.internal.org.bouncycastle.asn1.ASN1Encodable;
import com.android.internal.org.bouncycastle.asn1.ASN1Integer;
import com.android.internal.org.bouncycastle.asn1.ASN1ObjectIdentifier;
import com.android.internal.org.bouncycastle.asn1.ASN1Primitive;
import com.android.internal.org.bouncycastle.asn1.ASN1Sequence;
import com.android.internal.org.bouncycastle.asn1.DERNull;
import com.android.internal.org.bouncycastle.asn1.kisa.KISAObjectIdentifiers;
import com.android.internal.org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import com.android.internal.org.bouncycastle.asn1.ntt.NTTObjectIdentifiers;
import com.android.internal.org.bouncycastle.asn1.oiw.OIWObjectIdentifiers;
import com.android.internal.org.bouncycastle.asn1.p018x9.X9ObjectIdentifiers;
import com.android.internal.org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import com.android.internal.org.bouncycastle.asn1.pkcs.RSASSAPSSparams;
import com.android.internal.org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import com.android.internal.org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import com.android.internal.org.bouncycastle.cert.X509CertificateHolder;
import com.android.internal.org.bouncycastle.cms.CMSException;
import com.android.internal.org.bouncycastle.jcajce.util.AlgorithmParametersUtils;
import com.android.internal.org.bouncycastle.jcajce.util.JcaJceHelper;
import com.android.internal.org.bouncycastle.jcajce.util.MessageDigestUtils;
import com.android.internal.org.bouncycastle.operator.OperatorCreationException;
import com.android.internal.org.bouncycastle.util.Integers;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.AlgorithmParameters;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.security.Signature;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PSSParameterSpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;
import javax.crypto.Cipher;
import javax.crypto.KeyAgreement;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes4.dex */
public class OperatorHelper {
    private static final Map asymmetricWrapperAlgNames;
    private static final Map oids;
    private static final Map symmetricKeyAlgNames;
    private static final Map symmetricWrapperAlgNames;
    private static final Map symmetricWrapperKeySizes;
    private JcaJceHelper helper;

    static {
        HashMap hashMap = new HashMap();
        oids = hashMap;
        HashMap hashMap2 = new HashMap();
        asymmetricWrapperAlgNames = hashMap2;
        HashMap hashMap3 = new HashMap();
        symmetricWrapperAlgNames = hashMap3;
        HashMap hashMap4 = new HashMap();
        symmetricKeyAlgNames = hashMap4;
        HashMap hashMap5 = new HashMap();
        symmetricWrapperKeySizes = hashMap5;
        hashMap.put(new ASN1ObjectIdentifier("1.2.840.113549.1.1.5"), "SHA1WITHRSA");
        hashMap.put(PKCSObjectIdentifiers.sha224WithRSAEncryption, "SHA224WITHRSA");
        hashMap.put(PKCSObjectIdentifiers.sha256WithRSAEncryption, "SHA256WITHRSA");
        hashMap.put(PKCSObjectIdentifiers.sha384WithRSAEncryption, "SHA384WITHRSA");
        hashMap.put(PKCSObjectIdentifiers.sha512WithRSAEncryption, "SHA512WITHRSA");
        hashMap.put(new ASN1ObjectIdentifier("1.2.840.113549.1.1.4"), "MD5WITHRSA");
        hashMap.put(new ASN1ObjectIdentifier("1.2.840.10040.4.3"), "SHA1WITHDSA");
        hashMap.put(X9ObjectIdentifiers.ecdsa_with_SHA1, "SHA1WITHECDSA");
        hashMap.put(X9ObjectIdentifiers.ecdsa_with_SHA224, "SHA224WITHECDSA");
        hashMap.put(X9ObjectIdentifiers.ecdsa_with_SHA256, "SHA256WITHECDSA");
        hashMap.put(X9ObjectIdentifiers.ecdsa_with_SHA384, "SHA384WITHECDSA");
        hashMap.put(X9ObjectIdentifiers.ecdsa_with_SHA512, "SHA512WITHECDSA");
        hashMap.put(OIWObjectIdentifiers.sha1WithRSA, "SHA1WITHRSA");
        hashMap.put(OIWObjectIdentifiers.dsaWithSHA1, "SHA1WITHDSA");
        hashMap.put(NISTObjectIdentifiers.dsa_with_sha224, "SHA224WITHDSA");
        hashMap.put(NISTObjectIdentifiers.dsa_with_sha256, "SHA256WITHDSA");
        hashMap.put(OIWObjectIdentifiers.idSHA1, "SHA1");
        hashMap.put(NISTObjectIdentifiers.id_sha224, "SHA224");
        hashMap.put(NISTObjectIdentifiers.id_sha256, "SHA256");
        hashMap.put(NISTObjectIdentifiers.id_sha384, "SHA384");
        hashMap.put(NISTObjectIdentifiers.id_sha512, "SHA512");
        hashMap2.put(PKCSObjectIdentifiers.rsaEncryption, "RSA/ECB/PKCS1Padding");
        hashMap3.put(PKCSObjectIdentifiers.id_alg_CMS3DESwrap, "DESEDEWrap");
        hashMap3.put(PKCSObjectIdentifiers.id_alg_CMSRC2wrap, "RC2Wrap");
        hashMap3.put(NISTObjectIdentifiers.id_aes128_wrap, "AESWrap");
        hashMap3.put(NISTObjectIdentifiers.id_aes192_wrap, "AESWrap");
        hashMap3.put(NISTObjectIdentifiers.id_aes256_wrap, "AESWrap");
        hashMap3.put(NTTObjectIdentifiers.id_camellia128_wrap, "CamelliaWrap");
        hashMap3.put(NTTObjectIdentifiers.id_camellia192_wrap, "CamelliaWrap");
        hashMap3.put(NTTObjectIdentifiers.id_camellia256_wrap, "CamelliaWrap");
        hashMap3.put(KISAObjectIdentifiers.id_npki_app_cmsSeed_wrap, "SEEDWrap");
        hashMap3.put(PKCSObjectIdentifiers.des_EDE3_CBC, KeyProperties.KEY_ALGORITHM_3DES);
        hashMap5.put(PKCSObjectIdentifiers.id_alg_CMS3DESwrap, Integers.valueOf(192));
        hashMap5.put(NISTObjectIdentifiers.id_aes128_wrap, Integers.valueOf(128));
        hashMap5.put(NISTObjectIdentifiers.id_aes192_wrap, Integers.valueOf(192));
        hashMap5.put(NISTObjectIdentifiers.id_aes256_wrap, Integers.valueOf(256));
        hashMap5.put(NTTObjectIdentifiers.id_camellia128_wrap, Integers.valueOf(128));
        hashMap5.put(NTTObjectIdentifiers.id_camellia192_wrap, Integers.valueOf(192));
        hashMap5.put(NTTObjectIdentifiers.id_camellia256_wrap, Integers.valueOf(256));
        hashMap5.put(KISAObjectIdentifiers.id_npki_app_cmsSeed_wrap, Integers.valueOf(128));
        hashMap5.put(PKCSObjectIdentifiers.des_EDE3_CBC, Integers.valueOf(192));
        hashMap4.put(NISTObjectIdentifiers.aes, KeyProperties.KEY_ALGORITHM_AES);
        hashMap4.put(NISTObjectIdentifiers.id_aes128_CBC, KeyProperties.KEY_ALGORITHM_AES);
        hashMap4.put(NISTObjectIdentifiers.id_aes192_CBC, KeyProperties.KEY_ALGORITHM_AES);
        hashMap4.put(NISTObjectIdentifiers.id_aes256_CBC, KeyProperties.KEY_ALGORITHM_AES);
        hashMap4.put(PKCSObjectIdentifiers.des_EDE3_CBC, KeyProperties.KEY_ALGORITHM_3DES);
        hashMap4.put(PKCSObjectIdentifiers.RC2_CBC, "RC2");
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public OperatorHelper(JcaJceHelper helper) {
        this.helper = helper;
    }

    String getWrappingAlgorithmName(ASN1ObjectIdentifier algOid) {
        return (String) symmetricWrapperAlgNames.get(algOid);
    }

    int getKeySizeInBits(ASN1ObjectIdentifier algOid) {
        return ((Integer) symmetricWrapperKeySizes.get(algOid)).intValue();
    }

    KeyPairGenerator createKeyPairGenerator(ASN1ObjectIdentifier algorithm) throws CMSException {
        try {
            if (0 != 0) {
                try {
                    return this.helper.createKeyPairGenerator(null);
                } catch (NoSuchAlgorithmException e) {
                }
            }
            return this.helper.createKeyPairGenerator(algorithm.getId());
        } catch (GeneralSecurityException e2) {
            throw new CMSException("cannot create key agreement: " + e2.getMessage(), e2);
        }
    }

    Cipher createCipher(ASN1ObjectIdentifier algorithm) throws OperatorCreationException {
        try {
            return this.helper.createCipher(algorithm.getId());
        } catch (GeneralSecurityException e) {
            throw new OperatorCreationException("cannot create cipher: " + e.getMessage(), e);
        }
    }

    KeyAgreement createKeyAgreement(ASN1ObjectIdentifier algorithm) throws OperatorCreationException {
        try {
            if (0 != 0) {
                try {
                    return this.helper.createKeyAgreement(null);
                } catch (NoSuchAlgorithmException e) {
                }
            }
            return this.helper.createKeyAgreement(algorithm.getId());
        } catch (GeneralSecurityException e2) {
            throw new OperatorCreationException("cannot create key agreement: " + e2.getMessage(), e2);
        }
    }

    Cipher createAsymmetricWrapper(ASN1ObjectIdentifier algorithm, Map extraAlgNames) throws OperatorCreationException {
        String cipherName = null;
        try {
            if (!extraAlgNames.isEmpty()) {
                cipherName = (String) extraAlgNames.get(algorithm);
            }
            if (cipherName == null) {
                cipherName = (String) asymmetricWrapperAlgNames.get(algorithm);
            }
            if (cipherName != null) {
                try {
                    return this.helper.createCipher(cipherName);
                } catch (NoSuchAlgorithmException e) {
                    if (cipherName.equals("RSA/ECB/PKCS1Padding")) {
                        try {
                            return this.helper.createCipher("RSA/NONE/PKCS1Padding");
                        } catch (NoSuchAlgorithmException e2) {
                        }
                    }
                }
            }
            return this.helper.createCipher(algorithm.getId());
        } catch (GeneralSecurityException e3) {
            throw new OperatorCreationException("cannot create cipher: " + e3.getMessage(), e3);
        }
    }

    Cipher createSymmetricWrapper(ASN1ObjectIdentifier algorithm) throws OperatorCreationException {
        try {
            String cipherName = (String) symmetricWrapperAlgNames.get(algorithm);
            if (cipherName != null) {
                try {
                    return this.helper.createCipher(cipherName);
                } catch (NoSuchAlgorithmException e) {
                }
            }
            return this.helper.createCipher(algorithm.getId());
        } catch (GeneralSecurityException e2) {
            throw new OperatorCreationException("cannot create cipher: " + e2.getMessage(), e2);
        }
    }

    AlgorithmParameters createAlgorithmParameters(AlgorithmIdentifier cipherAlgId) throws OperatorCreationException {
        if (cipherAlgId.getAlgorithm().equals((ASN1Primitive) PKCSObjectIdentifiers.rsaEncryption)) {
            return null;
        }
        try {
            AlgorithmParameters parameters = this.helper.createAlgorithmParameters(cipherAlgId.getAlgorithm().getId());
            try {
                parameters.init(cipherAlgId.getParameters().toASN1Primitive().getEncoded());
                return parameters;
            } catch (IOException e) {
                throw new OperatorCreationException("cannot initialise algorithm parameters: " + e.getMessage(), e);
            }
        } catch (NoSuchAlgorithmException e2) {
            return null;
        } catch (NoSuchProviderException e3) {
            throw new OperatorCreationException("cannot create algorithm parameters: " + e3.getMessage(), e3);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public MessageDigest createDigest(AlgorithmIdentifier digAlgId) throws GeneralSecurityException {
        MessageDigest dig;
        try {
            if (digAlgId.getAlgorithm().equals((ASN1Primitive) NISTObjectIdentifiers.id_shake256_len)) {
                dig = this.helper.createMessageDigest("SHAKE256-" + ASN1Integer.getInstance(digAlgId.getParameters()).getValue());
            } else {
                dig = this.helper.createMessageDigest(MessageDigestUtils.getDigestName(digAlgId.getAlgorithm()));
            }
            return dig;
        } catch (NoSuchAlgorithmException e) {
            Map map = oids;
            if (map.get(digAlgId.getAlgorithm()) != null) {
                String digestAlgorithm = (String) map.get(digAlgId.getAlgorithm());
                MessageDigest dig2 = this.helper.createMessageDigest(digestAlgorithm);
                return dig2;
            }
            throw e;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Signature createSignature(AlgorithmIdentifier sigAlgId) throws GeneralSecurityException {
        Signature sig;
        String sigName = getSignatureName(sigAlgId);
        try {
            sig = this.helper.createSignature(sigName);
        } catch (NoSuchAlgorithmException e) {
            if (sigName.endsWith("WITHRSAANDMGF1")) {
                String signatureAlgorithm = sigName.substring(0, sigName.indexOf(87)) + "WITHRSASSA-PSS";
                Signature sig2 = this.helper.createSignature(signatureAlgorithm);
                sig = sig2;
            } else {
                Map map = oids;
                if (map.get(sigAlgId.getAlgorithm()) != null) {
                    String signatureAlgorithm2 = (String) map.get(sigAlgId.getAlgorithm());
                    Signature sig3 = this.helper.createSignature(signatureAlgorithm2);
                    sig = sig3;
                } else {
                    throw e;
                }
            }
        }
        if (sigAlgId.getAlgorithm().equals((ASN1Primitive) PKCSObjectIdentifiers.id_RSASSA_PSS)) {
            ASN1Sequence seq = ASN1Sequence.getInstance(sigAlgId.getParameters());
            if (notDefaultPSSParams(seq)) {
                try {
                    AlgorithmParameters algParams = this.helper.createAlgorithmParameters(KeyProperties.SIGNATURE_PADDING_RSA_PSS);
                    algParams.init(seq.getEncoded());
                    sig.setParameter(algParams.getParameterSpec(PSSParameterSpec.class));
                } catch (IOException e2) {
                    throw new GeneralSecurityException("unable to process PSS parameters: " + e2.getMessage());
                }
            }
        }
        return sig;
    }

    public Signature createRawSignature(AlgorithmIdentifier algorithm) {
        try {
            String algName = getSignatureName(algorithm);
            String algName2 = KeyProperties.DIGEST_NONE + algName.substring(algName.indexOf("WITH"));
            Signature sig = this.helper.createSignature(algName2);
            if (algorithm.getAlgorithm().equals((ASN1Primitive) PKCSObjectIdentifiers.id_RSASSA_PSS)) {
                AlgorithmParameters params = this.helper.createAlgorithmParameters(algName2);
                AlgorithmParametersUtils.loadParameters(params, algorithm.getParameters());
                PSSParameterSpec spec = (PSSParameterSpec) params.getParameterSpec(PSSParameterSpec.class);
                sig.setParameter(spec);
            }
            return sig;
        } catch (Exception e) {
            return null;
        }
    }

    private static String getSignatureName(AlgorithmIdentifier sigAlgId) {
        ASN1Encodable params = sigAlgId.getParameters();
        if (params != null && !DERNull.INSTANCE.equals(params) && sigAlgId.getAlgorithm().equals((ASN1Primitive) PKCSObjectIdentifiers.id_RSASSA_PSS)) {
            RSASSAPSSparams rsaParams = RSASSAPSSparams.getInstance(params);
            return getDigestName(rsaParams.getHashAlgorithm().getAlgorithm()) + "WITHRSAANDMGF1";
        }
        Map map = oids;
        if (map.containsKey(sigAlgId.getAlgorithm())) {
            return (String) map.get(sigAlgId.getAlgorithm());
        }
        return sigAlgId.getAlgorithm().getId();
    }

    private static String getDigestName(ASN1ObjectIdentifier oid) {
        String name = MessageDigestUtils.getDigestName(oid);
        int dIndex = name.indexOf(45);
        if (dIndex > 0 && !name.startsWith("SHA3")) {
            return name.substring(0, dIndex) + name.substring(dIndex + 1);
        }
        return name;
    }

    public X509Certificate convertCertificate(X509CertificateHolder certHolder) throws CertificateException {
        try {
            CertificateFactory certFact = this.helper.createCertificateFactory("X.509");
            return (X509Certificate) certFact.generateCertificate(new ByteArrayInputStream(certHolder.getEncoded()));
        } catch (IOException e) {
            throw new OpCertificateException("cannot get encoded form of certificate: " + e.getMessage(), e);
        } catch (NoSuchProviderException e2) {
            throw new OpCertificateException("cannot find factory provider: " + e2.getMessage(), e2);
        }
    }

    public PublicKey convertPublicKey(SubjectPublicKeyInfo publicKeyInfo) throws OperatorCreationException {
        try {
            KeyFactory keyFact = this.helper.createKeyFactory(publicKeyInfo.getAlgorithm().getAlgorithm().getId());
            return keyFact.generatePublic(new X509EncodedKeySpec(publicKeyInfo.getEncoded()));
        } catch (IOException e) {
            throw new OperatorCreationException("cannot get encoded form of key: " + e.getMessage(), e);
        } catch (NoSuchAlgorithmException e2) {
            throw new OperatorCreationException("cannot create key factory: " + e2.getMessage(), e2);
        } catch (NoSuchProviderException e3) {
            throw new OperatorCreationException("cannot find factory provider: " + e3.getMessage(), e3);
        } catch (InvalidKeySpecException e4) {
            throw new OperatorCreationException("cannot create key factory: " + e4.getMessage(), e4);
        }
    }

    /* loaded from: classes4.dex */
    private static class OpCertificateException extends CertificateException {
        private Throwable cause;

        public OpCertificateException(String msg, Throwable cause) {
            super(msg);
            this.cause = cause;
        }

        @Override // java.lang.Throwable
        public Throwable getCause() {
            return this.cause;
        }
    }

    String getKeyAlgorithmName(ASN1ObjectIdentifier oid) {
        String name = (String) symmetricKeyAlgNames.get(oid);
        if (name != null) {
            return name;
        }
        return oid.getId();
    }

    private boolean notDefaultPSSParams(ASN1Sequence seq) throws GeneralSecurityException {
        if (seq == null || seq.size() == 0) {
            return false;
        }
        RSASSAPSSparams pssParams = RSASSAPSSparams.getInstance(seq);
        if (pssParams.getMaskGenAlgorithm().getAlgorithm().equals((ASN1Primitive) PKCSObjectIdentifiers.id_mgf1) && pssParams.getHashAlgorithm().equals(AlgorithmIdentifier.getInstance(pssParams.getMaskGenAlgorithm().getParameters()))) {
            MessageDigest digest = createDigest(pssParams.getHashAlgorithm());
            return pssParams.getSaltLength().intValue() != digest.getDigestLength();
        }
        return true;
    }
}
