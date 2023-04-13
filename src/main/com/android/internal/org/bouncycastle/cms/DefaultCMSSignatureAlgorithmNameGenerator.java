package com.android.internal.org.bouncycastle.cms;

import android.security.keystore.KeyProperties;
import com.android.internal.org.bouncycastle.asn1.ASN1ObjectIdentifier;
import com.android.internal.org.bouncycastle.asn1.eac.EACObjectIdentifiers;
import com.android.internal.org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import com.android.internal.org.bouncycastle.asn1.oiw.OIWObjectIdentifiers;
import com.android.internal.org.bouncycastle.asn1.p018x9.X9ObjectIdentifiers;
import com.android.internal.org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import com.android.internal.org.bouncycastle.asn1.teletrust.TeleTrusTObjectIdentifiers;
import com.android.internal.org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import com.android.internal.org.bouncycastle.asn1.x509.X509ObjectIdentifiers;
import java.util.HashMap;
import java.util.Map;
/* loaded from: classes4.dex */
public class DefaultCMSSignatureAlgorithmNameGenerator implements CMSSignatureAlgorithmNameGenerator {
    private final Map digestAlgs;
    private final Map encryptionAlgs;

    private void addEntries(ASN1ObjectIdentifier alias, String digest, String encryption) {
        this.digestAlgs.put(alias, digest);
        this.encryptionAlgs.put(alias, encryption);
    }

    public DefaultCMSSignatureAlgorithmNameGenerator() {
        HashMap hashMap = new HashMap();
        this.encryptionAlgs = hashMap;
        HashMap hashMap2 = new HashMap();
        this.digestAlgs = hashMap2;
        addEntries(NISTObjectIdentifiers.dsa_with_sha224, "SHA224", "DSA");
        addEntries(NISTObjectIdentifiers.dsa_with_sha256, "SHA256", "DSA");
        addEntries(NISTObjectIdentifiers.dsa_with_sha384, "SHA384", "DSA");
        addEntries(NISTObjectIdentifiers.dsa_with_sha512, "SHA512", "DSA");
        addEntries(OIWObjectIdentifiers.dsaWithSHA1, "SHA1", "DSA");
        addEntries(OIWObjectIdentifiers.md5WithRSA, KeyProperties.DIGEST_MD5, KeyProperties.KEY_ALGORITHM_RSA);
        addEntries(OIWObjectIdentifiers.sha1WithRSA, "SHA1", KeyProperties.KEY_ALGORITHM_RSA);
        addEntries(PKCSObjectIdentifiers.md5WithRSAEncryption, KeyProperties.DIGEST_MD5, KeyProperties.KEY_ALGORITHM_RSA);
        addEntries(PKCSObjectIdentifiers.sha1WithRSAEncryption, "SHA1", KeyProperties.KEY_ALGORITHM_RSA);
        addEntries(PKCSObjectIdentifiers.sha224WithRSAEncryption, "SHA224", KeyProperties.KEY_ALGORITHM_RSA);
        addEntries(PKCSObjectIdentifiers.sha256WithRSAEncryption, "SHA256", KeyProperties.KEY_ALGORITHM_RSA);
        addEntries(PKCSObjectIdentifiers.sha384WithRSAEncryption, "SHA384", KeyProperties.KEY_ALGORITHM_RSA);
        addEntries(PKCSObjectIdentifiers.sha512WithRSAEncryption, "SHA512", KeyProperties.KEY_ALGORITHM_RSA);
        addEntries(X9ObjectIdentifiers.ecdsa_with_SHA1, "SHA1", "ECDSA");
        addEntries(X9ObjectIdentifiers.ecdsa_with_SHA224, "SHA224", "ECDSA");
        addEntries(X9ObjectIdentifiers.ecdsa_with_SHA256, "SHA256", "ECDSA");
        addEntries(X9ObjectIdentifiers.ecdsa_with_SHA384, "SHA384", "ECDSA");
        addEntries(X9ObjectIdentifiers.ecdsa_with_SHA512, "SHA512", "ECDSA");
        addEntries(X9ObjectIdentifiers.id_dsa_with_sha1, "SHA1", "DSA");
        addEntries(EACObjectIdentifiers.id_TA_ECDSA_SHA_1, "SHA1", "ECDSA");
        addEntries(EACObjectIdentifiers.id_TA_ECDSA_SHA_224, "SHA224", "ECDSA");
        addEntries(EACObjectIdentifiers.id_TA_ECDSA_SHA_256, "SHA256", "ECDSA");
        addEntries(EACObjectIdentifiers.id_TA_ECDSA_SHA_384, "SHA384", "ECDSA");
        addEntries(EACObjectIdentifiers.id_TA_ECDSA_SHA_512, "SHA512", "ECDSA");
        addEntries(EACObjectIdentifiers.id_TA_RSA_v1_5_SHA_1, "SHA1", KeyProperties.KEY_ALGORITHM_RSA);
        addEntries(EACObjectIdentifiers.id_TA_RSA_v1_5_SHA_256, "SHA256", KeyProperties.KEY_ALGORITHM_RSA);
        addEntries(EACObjectIdentifiers.id_TA_RSA_PSS_SHA_1, "SHA1", "RSAandMGF1");
        addEntries(EACObjectIdentifiers.id_TA_RSA_PSS_SHA_256, "SHA256", "RSAandMGF1");
        hashMap.put(X9ObjectIdentifiers.id_dsa, "DSA");
        hashMap.put(PKCSObjectIdentifiers.rsaEncryption, KeyProperties.KEY_ALGORITHM_RSA);
        hashMap.put(TeleTrusTObjectIdentifiers.teleTrusTRSAsignatureAlgorithm, KeyProperties.KEY_ALGORITHM_RSA);
        hashMap.put(X509ObjectIdentifiers.id_ea_rsa, KeyProperties.KEY_ALGORITHM_RSA);
        hashMap.put(PKCSObjectIdentifiers.id_RSASSA_PSS, "RSAandMGF1");
        hashMap2.put(PKCSObjectIdentifiers.md5, KeyProperties.DIGEST_MD5);
        hashMap2.put(OIWObjectIdentifiers.idSHA1, "SHA1");
        hashMap2.put(NISTObjectIdentifiers.id_sha224, "SHA224");
        hashMap2.put(NISTObjectIdentifiers.id_sha256, "SHA256");
        hashMap2.put(NISTObjectIdentifiers.id_sha384, "SHA384");
        hashMap2.put(NISTObjectIdentifiers.id_sha512, "SHA512");
    }

    private String getDigestAlgName(ASN1ObjectIdentifier digestAlgOID) {
        String algName = (String) this.digestAlgs.get(digestAlgOID);
        if (algName != null) {
            return algName;
        }
        return digestAlgOID.getId();
    }

    private String getEncryptionAlgName(ASN1ObjectIdentifier encryptionAlgOID) {
        String algName = (String) this.encryptionAlgs.get(encryptionAlgOID);
        if (algName != null) {
            return algName;
        }
        return encryptionAlgOID.getId();
    }

    protected void setSigningEncryptionAlgorithmMapping(ASN1ObjectIdentifier oid, String algorithmName) {
        this.encryptionAlgs.put(oid, algorithmName);
    }

    protected void setSigningDigestAlgorithmMapping(ASN1ObjectIdentifier oid, String algorithmName) {
        this.digestAlgs.put(oid, algorithmName);
    }

    @Override // com.android.internal.org.bouncycastle.cms.CMSSignatureAlgorithmNameGenerator
    public String getSignatureName(AlgorithmIdentifier digestAlg, AlgorithmIdentifier encryptionAlg) {
        String digestName = getDigestAlgName(encryptionAlg.getAlgorithm());
        if (!digestName.equals(encryptionAlg.getAlgorithm().getId())) {
            return digestName + "with" + getEncryptionAlgName(encryptionAlg.getAlgorithm());
        }
        return getDigestAlgName(digestAlg.getAlgorithm()) + "with" + getEncryptionAlgName(encryptionAlg.getAlgorithm());
    }
}
