package com.android.internal.org.bouncycastle.asn1;
/* loaded from: classes4.dex */
public class DERObjectIdentifier extends ASN1ObjectIdentifier {
    public DERObjectIdentifier(String identifier) {
        super(identifier);
    }

    DERObjectIdentifier(byte[] bytes) {
        super(bytes);
    }

    DERObjectIdentifier(ASN1ObjectIdentifier oid, String branch) {
        super(oid, branch);
    }
}
