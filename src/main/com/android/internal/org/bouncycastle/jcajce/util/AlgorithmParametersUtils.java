package com.android.internal.org.bouncycastle.jcajce.util;

import com.android.internal.org.bouncycastle.asn1.ASN1Encodable;
import com.android.internal.org.bouncycastle.asn1.ASN1Primitive;
import java.io.IOException;
import java.security.AlgorithmParameters;
/* loaded from: classes4.dex */
public class AlgorithmParametersUtils {
    private AlgorithmParametersUtils() {
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
}
