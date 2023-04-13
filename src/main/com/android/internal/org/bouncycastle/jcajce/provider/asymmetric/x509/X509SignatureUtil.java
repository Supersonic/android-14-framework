package com.android.internal.org.bouncycastle.jcajce.provider.asymmetric.x509;

import com.android.internal.org.bouncycastle.asn1.ASN1Encodable;
import com.android.internal.org.bouncycastle.asn1.ASN1Null;
import com.android.internal.org.bouncycastle.asn1.ASN1ObjectIdentifier;
import com.android.internal.org.bouncycastle.asn1.ASN1Primitive;
import com.android.internal.org.bouncycastle.asn1.ASN1Sequence;
import com.android.internal.org.bouncycastle.asn1.DERNull;
import com.android.internal.org.bouncycastle.asn1.misc.MiscObjectIdentifiers;
import com.android.internal.org.bouncycastle.asn1.oiw.OIWObjectIdentifiers;
import com.android.internal.org.bouncycastle.asn1.p018x9.X9ObjectIdentifiers;
import com.android.internal.org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import com.android.internal.org.bouncycastle.asn1.pkcs.RSASSAPSSparams;
import com.android.internal.org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import com.android.internal.org.bouncycastle.jcajce.util.MessageDigestUtils;
import com.android.internal.org.bouncycastle.jce.provider.BouncyCastleProvider;
import com.android.internal.org.bouncycastle.util.encoders.Hex;
import java.io.IOException;
import java.security.AlgorithmParameters;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.security.Security;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.PSSParameterSpec;
import java.util.HashMap;
import java.util.Map;
/* loaded from: classes4.dex */
class X509SignatureUtil {
    private static final Map<ASN1ObjectIdentifier, String> algNames;
    private static final ASN1Null derNull;

    X509SignatureUtil() {
    }

    static {
        HashMap hashMap = new HashMap();
        algNames = hashMap;
        hashMap.put(OIWObjectIdentifiers.dsaWithSHA1, "SHA1withDSA");
        hashMap.put(X9ObjectIdentifiers.id_dsa_with_sha1, "SHA1withDSA");
        derNull = DERNull.INSTANCE;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static boolean isCompositeAlgorithm(AlgorithmIdentifier algorithmIdentifier) {
        return MiscObjectIdentifiers.id_alg_composite.equals((ASN1Primitive) algorithmIdentifier.getAlgorithm());
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void setSignatureParameters(Signature signature, ASN1Encodable params) throws NoSuchAlgorithmException, SignatureException, InvalidKeyException {
        if (params != null && !derNull.equals(params)) {
            AlgorithmParameters sigParams = AlgorithmParameters.getInstance(signature.getAlgorithm(), signature.getProvider());
            try {
                sigParams.init(params.toASN1Primitive().getEncoded());
                if (signature.getAlgorithm().endsWith("MGF1")) {
                    try {
                        signature.setParameter(sigParams.getParameterSpec(PSSParameterSpec.class));
                    } catch (GeneralSecurityException e) {
                        throw new SignatureException("Exception extracting parameters: " + e.getMessage());
                    }
                }
            } catch (IOException e2) {
                throw new SignatureException("IOException decoding parameters: " + e2.getMessage());
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static String getSignatureName(AlgorithmIdentifier sigAlgId) {
        ASN1Encodable params = sigAlgId.getParameters();
        if (params != null && !derNull.equals(params)) {
            if (sigAlgId.getAlgorithm().equals((ASN1Primitive) PKCSObjectIdentifiers.id_RSASSA_PSS)) {
                RSASSAPSSparams rsaParams = RSASSAPSSparams.getInstance(params);
                return getDigestAlgName(rsaParams.getHashAlgorithm().getAlgorithm()) + "withRSAandMGF1";
            } else if (sigAlgId.getAlgorithm().equals((ASN1Primitive) X9ObjectIdentifiers.ecdsa_with_SHA2)) {
                ASN1Sequence ecDsaParams = ASN1Sequence.getInstance(params);
                return getDigestAlgName((ASN1ObjectIdentifier) ecDsaParams.getObjectAt(0)) + "withECDSA";
            }
        }
        String algName = algNames.get(sigAlgId.getAlgorithm());
        if (algName != null) {
            return algName;
        }
        return findAlgName(sigAlgId.getAlgorithm());
    }

    private static String getDigestAlgName(ASN1ObjectIdentifier digestAlgOID) {
        String name = MessageDigestUtils.getDigestName(digestAlgOID);
        int dIndex = name.indexOf(45);
        if (dIndex > 0 && !name.startsWith("SHA3")) {
            return name.substring(0, dIndex) + name.substring(dIndex + 1);
        }
        return name;
    }

    private static String findAlgName(ASN1ObjectIdentifier algOid) {
        String algName;
        String algName2;
        Provider prov = Security.getProvider(BouncyCastleProvider.PROVIDER_NAME);
        if (prov != null && (algName2 = lookupAlg(prov, algOid)) != null) {
            return algName2;
        }
        Provider[] provs = Security.getProviders();
        for (int i = 0; i != provs.length; i++) {
            if (prov != provs[i] && (algName = lookupAlg(provs[i], algOid)) != null) {
                return algName;
            }
        }
        return algOid.getId();
    }

    private static String lookupAlg(Provider prov, ASN1ObjectIdentifier algOid) {
        String algName = prov.getProperty("Alg.Alias.Signature." + algOid);
        if (algName != null) {
            return algName;
        }
        String algName2 = prov.getProperty("Alg.Alias.Signature.OID." + algOid);
        if (algName2 != null) {
            return algName2;
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void prettyPrintSignature(byte[] sig, StringBuffer buf, String nl) {
        if (sig.length > 20) {
            buf.append("            Signature: ").append(Hex.toHexString(sig, 0, 20)).append(nl);
            for (int i = 20; i < sig.length; i += 20) {
                if (i < sig.length - 20) {
                    buf.append("                       ").append(Hex.toHexString(sig, i, 20)).append(nl);
                } else {
                    buf.append("                       ").append(Hex.toHexString(sig, i, sig.length - i)).append(nl);
                }
            }
            return;
        }
        buf.append("            Signature: ").append(Hex.toHexString(sig)).append(nl);
    }
}
