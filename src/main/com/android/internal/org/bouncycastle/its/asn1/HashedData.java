package com.android.internal.org.bouncycastle.its.asn1;

import com.android.internal.org.bouncycastle.asn1.ASN1Choice;
import com.android.internal.org.bouncycastle.asn1.ASN1Object;
import com.android.internal.org.bouncycastle.asn1.ASN1OctetString;
import com.android.internal.org.bouncycastle.asn1.ASN1Primitive;
import com.android.internal.org.bouncycastle.asn1.DEROctetString;
/* loaded from: classes4.dex */
public class HashedData extends ASN1Object implements ASN1Choice {
    private ASN1OctetString hashData;

    public HashedData(byte[] digest) {
        this.hashData = new DEROctetString(digest);
    }

    private HashedData(ASN1OctetString hashData) {
        this.hashData = hashData;
    }

    @Override // com.android.internal.org.bouncycastle.asn1.ASN1Object, com.android.internal.org.bouncycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        return this.hashData;
    }

    public ASN1OctetString getHashData() {
        return this.hashData;
    }

    public void setHashData(ASN1OctetString hashData) {
        this.hashData = hashData;
    }
}
