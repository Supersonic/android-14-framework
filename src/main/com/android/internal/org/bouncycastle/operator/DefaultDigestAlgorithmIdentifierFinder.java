package com.android.internal.org.bouncycastle.operator;

import android.security.keystore.KeyProperties;
import com.android.internal.org.bouncycastle.asn1.ASN1ObjectIdentifier;
import com.android.internal.org.bouncycastle.asn1.ASN1Primitive;
import com.android.internal.org.bouncycastle.asn1.DERNull;
import com.android.internal.org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import com.android.internal.org.bouncycastle.asn1.oiw.OIWObjectIdentifiers;
import com.android.internal.org.bouncycastle.asn1.p018x9.X9ObjectIdentifiers;
import com.android.internal.org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import com.android.internal.org.bouncycastle.asn1.pkcs.RSASSAPSSparams;
import com.android.internal.org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import java.util.HashMap;
import java.util.Map;
/* loaded from: classes4.dex */
public class DefaultDigestAlgorithmIdentifierFinder implements DigestAlgorithmIdentifierFinder {
    private static Map digestOids = new HashMap();
    private static Map digestNameToOids = new HashMap();

    static {
        digestOids.put(OIWObjectIdentifiers.sha1WithRSA, OIWObjectIdentifiers.idSHA1);
        digestOids.put(PKCSObjectIdentifiers.sha224WithRSAEncryption, NISTObjectIdentifiers.id_sha224);
        digestOids.put(PKCSObjectIdentifiers.sha256WithRSAEncryption, NISTObjectIdentifiers.id_sha256);
        digestOids.put(PKCSObjectIdentifiers.sha384WithRSAEncryption, NISTObjectIdentifiers.id_sha384);
        digestOids.put(PKCSObjectIdentifiers.sha512WithRSAEncryption, NISTObjectIdentifiers.id_sha512);
        digestOids.put(PKCSObjectIdentifiers.md5WithRSAEncryption, PKCSObjectIdentifiers.md5);
        digestOids.put(PKCSObjectIdentifiers.sha1WithRSAEncryption, OIWObjectIdentifiers.idSHA1);
        digestOids.put(X9ObjectIdentifiers.ecdsa_with_SHA1, OIWObjectIdentifiers.idSHA1);
        digestOids.put(X9ObjectIdentifiers.ecdsa_with_SHA224, NISTObjectIdentifiers.id_sha224);
        digestOids.put(X9ObjectIdentifiers.ecdsa_with_SHA256, NISTObjectIdentifiers.id_sha256);
        digestOids.put(X9ObjectIdentifiers.ecdsa_with_SHA384, NISTObjectIdentifiers.id_sha384);
        digestOids.put(X9ObjectIdentifiers.ecdsa_with_SHA512, NISTObjectIdentifiers.id_sha512);
        digestOids.put(X9ObjectIdentifiers.id_dsa_with_sha1, OIWObjectIdentifiers.idSHA1);
        digestOids.put(NISTObjectIdentifiers.dsa_with_sha224, NISTObjectIdentifiers.id_sha224);
        digestOids.put(NISTObjectIdentifiers.dsa_with_sha256, NISTObjectIdentifiers.id_sha256);
        digestOids.put(NISTObjectIdentifiers.dsa_with_sha384, NISTObjectIdentifiers.id_sha384);
        digestOids.put(NISTObjectIdentifiers.dsa_with_sha512, NISTObjectIdentifiers.id_sha512);
        digestNameToOids.put("SHA-1", OIWObjectIdentifiers.idSHA1);
        digestNameToOids.put(KeyProperties.DIGEST_SHA224, NISTObjectIdentifiers.id_sha224);
        digestNameToOids.put("SHA-256", NISTObjectIdentifiers.id_sha256);
        digestNameToOids.put(KeyProperties.DIGEST_SHA384, NISTObjectIdentifiers.id_sha384);
        digestNameToOids.put(KeyProperties.DIGEST_SHA512, NISTObjectIdentifiers.id_sha512);
        digestNameToOids.put(KeyProperties.DIGEST_MD5, PKCSObjectIdentifiers.md5);
    }

    @Override // com.android.internal.org.bouncycastle.operator.DigestAlgorithmIdentifierFinder
    public AlgorithmIdentifier find(AlgorithmIdentifier sigAlgId) {
        if (sigAlgId.getAlgorithm().equals((ASN1Primitive) PKCSObjectIdentifiers.id_RSASSA_PSS)) {
            AlgorithmIdentifier digAlgId = RSASSAPSSparams.getInstance(sigAlgId.getParameters()).getHashAlgorithm();
            return digAlgId;
        }
        AlgorithmIdentifier digAlgId2 = new AlgorithmIdentifier((ASN1ObjectIdentifier) digestOids.get(sigAlgId.getAlgorithm()), DERNull.INSTANCE);
        return digAlgId2;
    }

    @Override // com.android.internal.org.bouncycastle.operator.DigestAlgorithmIdentifierFinder
    public AlgorithmIdentifier find(String digAlgName) {
        return new AlgorithmIdentifier((ASN1ObjectIdentifier) digestNameToOids.get(digAlgName), DERNull.INSTANCE);
    }
}
