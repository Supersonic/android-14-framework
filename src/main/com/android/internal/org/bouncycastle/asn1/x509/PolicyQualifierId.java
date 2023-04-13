package com.android.internal.org.bouncycastle.asn1.x509;

import com.android.internal.org.bouncycastle.asn1.ASN1ObjectIdentifier;
/* loaded from: classes4.dex */
public class PolicyQualifierId extends ASN1ObjectIdentifier {
    private static final String id_qt = "1.3.6.1.5.5.7.2";
    public static final PolicyQualifierId id_qt_cps = new PolicyQualifierId("1.3.6.1.5.5.7.2.1");
    public static final PolicyQualifierId id_qt_unotice = new PolicyQualifierId("1.3.6.1.5.5.7.2.2");

    private PolicyQualifierId(String id) {
        super(id);
    }
}
