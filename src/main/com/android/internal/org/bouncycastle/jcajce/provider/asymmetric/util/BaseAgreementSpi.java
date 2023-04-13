package com.android.internal.org.bouncycastle.jcajce.provider.asymmetric.util;

import android.security.keystore.KeyProperties;
import com.android.internal.org.bouncycastle.asn1.ASN1ObjectIdentifier;
import com.android.internal.org.bouncycastle.asn1.kisa.KISAObjectIdentifiers;
import com.android.internal.org.bouncycastle.asn1.misc.MiscObjectIdentifiers;
import com.android.internal.org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import com.android.internal.org.bouncycastle.asn1.ntt.NTTObjectIdentifiers;
import com.android.internal.org.bouncycastle.asn1.oiw.OIWObjectIdentifiers;
import com.android.internal.org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import com.android.internal.org.bouncycastle.crypto.DerivationFunction;
import com.android.internal.org.bouncycastle.crypto.params.DESParameters;
import com.android.internal.org.bouncycastle.crypto.params.KDFParameters;
import com.android.internal.org.bouncycastle.util.Arrays;
import com.android.internal.org.bouncycastle.util.Integers;
import com.android.internal.org.bouncycastle.util.Strings;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import javax.crypto.KeyAgreementSpi;
import javax.crypto.SecretKey;
import javax.crypto.ShortBufferException;
import javax.crypto.spec.SecretKeySpec;
/* loaded from: classes4.dex */
public abstract class BaseAgreementSpi extends KeyAgreementSpi {
    private static final Map<String, ASN1ObjectIdentifier> defaultOids;
    private static final Hashtable des;
    private static final Map<String, Integer> keySizes;
    private static final Map<String, String> nameTable;
    private static final Hashtable oids;
    protected final String kaAlgorithm;
    protected final DerivationFunction kdf;
    protected byte[] ukmParameters;

    protected abstract byte[] calcSecret();

    static {
        HashMap hashMap = new HashMap();
        defaultOids = hashMap;
        HashMap hashMap2 = new HashMap();
        keySizes = hashMap2;
        HashMap hashMap3 = new HashMap();
        nameTable = hashMap3;
        Hashtable hashtable = new Hashtable();
        oids = hashtable;
        Hashtable hashtable2 = new Hashtable();
        des = hashtable2;
        Integer i64 = Integers.valueOf(64);
        Integer i128 = Integers.valueOf(128);
        Integer i192 = Integers.valueOf(192);
        Integer i256 = Integers.valueOf(256);
        hashMap2.put("DES", i64);
        hashMap2.put("DESEDE", i192);
        hashMap2.put("BLOWFISH", i128);
        hashMap2.put(KeyProperties.KEY_ALGORITHM_AES, i256);
        hashMap2.put(NISTObjectIdentifiers.id_aes128_ECB.getId(), i128);
        hashMap2.put(NISTObjectIdentifiers.id_aes192_ECB.getId(), i192);
        hashMap2.put(NISTObjectIdentifiers.id_aes256_ECB.getId(), i256);
        hashMap2.put(NISTObjectIdentifiers.id_aes128_CBC.getId(), i128);
        hashMap2.put(NISTObjectIdentifiers.id_aes192_CBC.getId(), i192);
        hashMap2.put(NISTObjectIdentifiers.id_aes256_CBC.getId(), i256);
        hashMap2.put(NISTObjectIdentifiers.id_aes128_CFB.getId(), i128);
        hashMap2.put(NISTObjectIdentifiers.id_aes192_CFB.getId(), i192);
        hashMap2.put(NISTObjectIdentifiers.id_aes256_CFB.getId(), i256);
        hashMap2.put(NISTObjectIdentifiers.id_aes128_OFB.getId(), i128);
        hashMap2.put(NISTObjectIdentifiers.id_aes192_OFB.getId(), i192);
        hashMap2.put(NISTObjectIdentifiers.id_aes256_OFB.getId(), i256);
        hashMap2.put(NISTObjectIdentifiers.id_aes128_wrap.getId(), i128);
        hashMap2.put(NISTObjectIdentifiers.id_aes192_wrap.getId(), i192);
        hashMap2.put(NISTObjectIdentifiers.id_aes256_wrap.getId(), i256);
        hashMap2.put(NISTObjectIdentifiers.id_aes128_CCM.getId(), i128);
        hashMap2.put(NISTObjectIdentifiers.id_aes192_CCM.getId(), i192);
        hashMap2.put(NISTObjectIdentifiers.id_aes256_CCM.getId(), i256);
        hashMap2.put(NISTObjectIdentifiers.id_aes128_GCM.getId(), i128);
        hashMap2.put(NISTObjectIdentifiers.id_aes192_GCM.getId(), i192);
        hashMap2.put(NISTObjectIdentifiers.id_aes256_GCM.getId(), i256);
        hashMap2.put(NTTObjectIdentifiers.id_camellia128_wrap.getId(), i128);
        hashMap2.put(NTTObjectIdentifiers.id_camellia192_wrap.getId(), i192);
        hashMap2.put(NTTObjectIdentifiers.id_camellia256_wrap.getId(), i256);
        hashMap2.put(KISAObjectIdentifiers.id_npki_app_cmsSeed_wrap.getId(), i128);
        hashMap2.put(PKCSObjectIdentifiers.id_alg_CMS3DESwrap.getId(), i192);
        hashMap2.put(PKCSObjectIdentifiers.des_EDE3_CBC.getId(), i192);
        hashMap2.put(OIWObjectIdentifiers.desCBC.getId(), i64);
        hashMap2.put(PKCSObjectIdentifiers.id_hmacWithSHA1.getId(), Integers.valueOf(160));
        hashMap2.put(PKCSObjectIdentifiers.id_hmacWithSHA256.getId(), i256);
        hashMap2.put(PKCSObjectIdentifiers.id_hmacWithSHA384.getId(), Integers.valueOf(384));
        hashMap2.put(PKCSObjectIdentifiers.id_hmacWithSHA512.getId(), Integers.valueOf(512));
        hashMap.put("DESEDE", PKCSObjectIdentifiers.des_EDE3_CBC);
        hashMap.put(KeyProperties.KEY_ALGORITHM_AES, NISTObjectIdentifiers.id_aes256_CBC);
        hashMap.put("CAMELLIA", NTTObjectIdentifiers.id_camellia256_cbc);
        hashMap.put("SEED", KISAObjectIdentifiers.id_seedCBC);
        hashMap.put("DES", OIWObjectIdentifiers.desCBC);
        hashMap3.put(MiscObjectIdentifiers.cast5CBC.getId(), "CAST5");
        hashMap3.put(MiscObjectIdentifiers.as_sys_sec_alg_ideaCBC.getId(), "IDEA");
        hashMap3.put(MiscObjectIdentifiers.cryptlib_algorithm_blowfish_ECB.getId(), "Blowfish");
        hashMap3.put(MiscObjectIdentifiers.cryptlib_algorithm_blowfish_CBC.getId(), "Blowfish");
        hashMap3.put(MiscObjectIdentifiers.cryptlib_algorithm_blowfish_CFB.getId(), "Blowfish");
        hashMap3.put(MiscObjectIdentifiers.cryptlib_algorithm_blowfish_OFB.getId(), "Blowfish");
        hashMap3.put(OIWObjectIdentifiers.desECB.getId(), "DES");
        hashMap3.put(OIWObjectIdentifiers.desCBC.getId(), "DES");
        hashMap3.put(OIWObjectIdentifiers.desCFB.getId(), "DES");
        hashMap3.put(OIWObjectIdentifiers.desOFB.getId(), "DES");
        hashMap3.put(OIWObjectIdentifiers.desEDE.getId(), KeyProperties.KEY_ALGORITHM_3DES);
        hashMap3.put(PKCSObjectIdentifiers.des_EDE3_CBC.getId(), KeyProperties.KEY_ALGORITHM_3DES);
        hashMap3.put(PKCSObjectIdentifiers.id_alg_CMS3DESwrap.getId(), KeyProperties.KEY_ALGORITHM_3DES);
        hashMap3.put(PKCSObjectIdentifiers.id_alg_CMSRC2wrap.getId(), "RC2");
        hashMap3.put(PKCSObjectIdentifiers.id_hmacWithSHA1.getId(), KeyProperties.KEY_ALGORITHM_HMAC_SHA1);
        hashMap3.put(PKCSObjectIdentifiers.id_hmacWithSHA224.getId(), KeyProperties.KEY_ALGORITHM_HMAC_SHA224);
        hashMap3.put(PKCSObjectIdentifiers.id_hmacWithSHA256.getId(), KeyProperties.KEY_ALGORITHM_HMAC_SHA256);
        hashMap3.put(PKCSObjectIdentifiers.id_hmacWithSHA384.getId(), KeyProperties.KEY_ALGORITHM_HMAC_SHA384);
        hashMap3.put(PKCSObjectIdentifiers.id_hmacWithSHA512.getId(), KeyProperties.KEY_ALGORITHM_HMAC_SHA512);
        hashMap3.put(NTTObjectIdentifiers.id_camellia128_cbc.getId(), "Camellia");
        hashMap3.put(NTTObjectIdentifiers.id_camellia192_cbc.getId(), "Camellia");
        hashMap3.put(NTTObjectIdentifiers.id_camellia256_cbc.getId(), "Camellia");
        hashMap3.put(NTTObjectIdentifiers.id_camellia128_wrap.getId(), "Camellia");
        hashMap3.put(NTTObjectIdentifiers.id_camellia192_wrap.getId(), "Camellia");
        hashMap3.put(NTTObjectIdentifiers.id_camellia256_wrap.getId(), "Camellia");
        hashMap3.put(KISAObjectIdentifiers.id_npki_app_cmsSeed_wrap.getId(), "SEED");
        hashMap3.put(KISAObjectIdentifiers.id_seedCBC.getId(), "SEED");
        hashMap3.put(KISAObjectIdentifiers.id_seedMAC.getId(), "SEED");
        hashMap3.put(NISTObjectIdentifiers.id_aes128_wrap.getId(), KeyProperties.KEY_ALGORITHM_AES);
        hashMap3.put(NISTObjectIdentifiers.id_aes128_CCM.getId(), KeyProperties.KEY_ALGORITHM_AES);
        hashMap3.put(NISTObjectIdentifiers.id_aes128_CCM.getId(), KeyProperties.KEY_ALGORITHM_AES);
        hashtable.put("DESEDE", PKCSObjectIdentifiers.des_EDE3_CBC);
        hashtable.put(KeyProperties.KEY_ALGORITHM_AES, NISTObjectIdentifiers.id_aes256_CBC);
        hashtable.put("DES", OIWObjectIdentifiers.desCBC);
        hashtable2.put("DES", "DES");
        hashtable2.put("DESEDE", "DES");
        hashtable2.put(OIWObjectIdentifiers.desCBC.getId(), "DES");
        hashtable2.put(PKCSObjectIdentifiers.des_EDE3_CBC.getId(), "DES");
        hashtable2.put(PKCSObjectIdentifiers.id_alg_CMS3DESwrap.getId(), "DES");
    }

    public BaseAgreementSpi(String kaAlgorithm, DerivationFunction kdf) {
        this.kaAlgorithm = kaAlgorithm;
        this.kdf = kdf;
    }

    protected static String getAlgorithm(String algDetails) {
        if (algDetails.indexOf(91) > 0) {
            return algDetails.substring(0, algDetails.indexOf(91));
        }
        if (algDetails.startsWith(NISTObjectIdentifiers.aes.getId())) {
            return KeyProperties.KEY_ALGORITHM_AES;
        }
        String name = nameTable.get(Strings.toUpperCase(algDetails));
        if (name != null) {
            return name;
        }
        return algDetails;
    }

    protected static int getKeySize(String algDetails) {
        if (algDetails.indexOf(91) > 0) {
            return Integer.parseInt(algDetails.substring(algDetails.indexOf(91) + 1, algDetails.indexOf(93)));
        }
        String algKey = Strings.toUpperCase(algDetails);
        Map<String, Integer> map = keySizes;
        if (!map.containsKey(algKey)) {
            return -1;
        }
        return map.get(algKey).intValue();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public static byte[] trimZeroes(byte[] secret) {
        if (secret[0] != 0) {
            return secret;
        }
        int ind = 0;
        while (ind < secret.length && secret[ind] == 0) {
            ind++;
        }
        byte[] rv = new byte[secret.length - ind];
        System.arraycopy(secret, ind, rv, 0, rv.length);
        return rv;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // javax.crypto.KeyAgreementSpi
    public byte[] engineGenerateSecret() throws IllegalStateException {
        if (this.kdf != null) {
            byte[] secret = calcSecret();
            try {
                return getSharedSecretBytes(secret, null, secret.length * 8);
            } catch (NoSuchAlgorithmException e) {
                throw new IllegalStateException(e.getMessage());
            }
        }
        return calcSecret();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // javax.crypto.KeyAgreementSpi
    public int engineGenerateSecret(byte[] sharedSecret, int offset) throws IllegalStateException, ShortBufferException {
        byte[] secret = engineGenerateSecret();
        if (sharedSecret.length - offset < secret.length) {
            throw new ShortBufferException(this.kaAlgorithm + " key agreement: need " + secret.length + " bytes");
        }
        System.arraycopy(secret, 0, sharedSecret, offset, secret.length);
        return secret.length;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // javax.crypto.KeyAgreementSpi
    public SecretKey engineGenerateSecret(String algorithm) throws NoSuchAlgorithmException {
        String algKey = Strings.toUpperCase(algorithm);
        String oidAlgorithm = algorithm;
        Hashtable hashtable = oids;
        if (hashtable.containsKey(algKey)) {
            oidAlgorithm = ((ASN1ObjectIdentifier) hashtable.get(algKey)).getId();
        }
        int keySize = getKeySize(oidAlgorithm);
        byte[] secret = getSharedSecretBytes(calcSecret(), oidAlgorithm, keySize);
        String algName = getAlgorithm(algorithm);
        if (des.containsKey(algName)) {
            DESParameters.setOddParity(secret);
        }
        return new SecretKeySpec(secret, algName);
    }

    private byte[] getSharedSecretBytes(byte[] secret, String oidAlgorithm, int keySize) throws NoSuchAlgorithmException {
        if (this.kdf != null) {
            if (keySize < 0) {
                throw new NoSuchAlgorithmException("unknown algorithm encountered: " + oidAlgorithm);
            }
            byte[] keyBytes = new byte[keySize / 8];
            KDFParameters params = new KDFParameters(secret, this.ukmParameters);
            this.kdf.init(params);
            this.kdf.generateBytes(keyBytes, 0, keyBytes.length);
            Arrays.clear(secret);
            return keyBytes;
        } else if (keySize > 0) {
            byte[] keyBytes2 = new byte[keySize / 8];
            System.arraycopy(secret, 0, keyBytes2, 0, keyBytes2.length);
            Arrays.clear(secret);
            return keyBytes2;
        } else {
            return secret;
        }
    }
}
