package com.android.internal.org.bouncycastle.jcajce.util;

import android.security.keystore.KeyProperties;
import com.android.internal.org.bouncycastle.asn1.ASN1Encodable;
import com.android.internal.org.bouncycastle.asn1.ASN1ObjectIdentifier;
import com.android.internal.org.bouncycastle.asn1.ASN1Primitive;
import com.android.internal.org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import com.android.internal.org.bouncycastle.asn1.oiw.OIWObjectIdentifiers;
import com.android.internal.org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import java.io.IOException;
import java.security.AlgorithmParameters;
/* loaded from: classes4.dex */
public class JcaJceUtils {
    private JcaJceUtils() {
    }

    public static ASN1Encodable extractParameters(AlgorithmParameters params) throws IOException {
        try {
            ASN1Encodable asn1Params = ASN1Primitive.fromByteArray(params.getEncoded("ASN.1"));
            return asn1Params;
        } catch (Exception e) {
            ASN1Encodable asn1Params2 = ASN1Primitive.fromByteArray(params.getEncoded());
            return asn1Params2;
        }
    }

    public static void loadParameters(AlgorithmParameters params, ASN1Encodable sParams) throws IOException {
        try {
            params.init(sParams.toASN1Primitive().getEncoded(), "ASN.1");
        } catch (Exception e) {
            params.init(sParams.toASN1Primitive().getEncoded());
        }
    }

    public static String getDigestAlgName(ASN1ObjectIdentifier digestAlgOID) {
        if (PKCSObjectIdentifiers.md5.equals((ASN1Primitive) digestAlgOID)) {
            return KeyProperties.DIGEST_MD5;
        }
        if (OIWObjectIdentifiers.idSHA1.equals((ASN1Primitive) digestAlgOID)) {
            return "SHA1";
        }
        if (NISTObjectIdentifiers.id_sha224.equals((ASN1Primitive) digestAlgOID)) {
            return "SHA224";
        }
        if (NISTObjectIdentifiers.id_sha256.equals((ASN1Primitive) digestAlgOID)) {
            return "SHA256";
        }
        if (NISTObjectIdentifiers.id_sha384.equals((ASN1Primitive) digestAlgOID)) {
            return "SHA384";
        }
        if (NISTObjectIdentifiers.id_sha512.equals((ASN1Primitive) digestAlgOID)) {
            return "SHA512";
        }
        return digestAlgOID.getId();
    }
}
