package com.android.internal.org.bouncycastle.its.asn1;

import com.android.internal.org.bouncycastle.asn1.ASN1Sequence;
/* loaded from: classes4.dex */
public class EncryptedData {
    private EncryptedData(ASN1Sequence seq) {
    }

    public static EncryptedData getInstance(Object o) {
        if (o instanceof EncryptedData) {
            return (EncryptedData) o;
        }
        if (o != null) {
            return new EncryptedData(ASN1Sequence.getInstance(o));
        }
        return null;
    }
}
