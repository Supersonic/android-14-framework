package com.android.internal.org.bouncycastle.cms;

import com.android.internal.org.bouncycastle.asn1.DERNull;
import com.android.internal.org.bouncycastle.asn1.oiw.OIWObjectIdentifiers;
import com.android.internal.org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import com.android.internal.org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
/* loaded from: classes4.dex */
public class DefaultCMSSignatureEncryptionAlgorithmFinder implements CMSSignatureEncryptionAlgorithmFinder {
    private static final Map GOST_ENC;
    private static final Set RSA_PKCS1d5;

    static {
        HashSet hashSet = new HashSet();
        RSA_PKCS1d5 = hashSet;
        GOST_ENC = new HashMap();
        hashSet.add(PKCSObjectIdentifiers.md5WithRSAEncryption);
        hashSet.add(PKCSObjectIdentifiers.sha1WithRSAEncryption);
        hashSet.add(PKCSObjectIdentifiers.sha224WithRSAEncryption);
        hashSet.add(PKCSObjectIdentifiers.sha256WithRSAEncryption);
        hashSet.add(PKCSObjectIdentifiers.sha384WithRSAEncryption);
        hashSet.add(PKCSObjectIdentifiers.sha512WithRSAEncryption);
        hashSet.add(OIWObjectIdentifiers.md5WithRSA);
        hashSet.add(OIWObjectIdentifiers.sha1WithRSA);
    }

    @Override // com.android.internal.org.bouncycastle.cms.CMSSignatureEncryptionAlgorithmFinder
    public AlgorithmIdentifier findEncryptionAlgorithm(AlgorithmIdentifier signatureAlgorithm) {
        if (RSA_PKCS1d5.contains(signatureAlgorithm.getAlgorithm())) {
            return new AlgorithmIdentifier(PKCSObjectIdentifiers.rsaEncryption, DERNull.INSTANCE);
        }
        Map map = GOST_ENC;
        if (map.containsKey(signatureAlgorithm.getAlgorithm())) {
            return (AlgorithmIdentifier) map.get(signatureAlgorithm.getAlgorithm());
        }
        return signatureAlgorithm;
    }
}
