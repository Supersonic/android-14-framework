package com.android.internal.org.bouncycastle.x509;

import com.android.internal.org.bouncycastle.asn1.ASN1Encodable;
import com.android.internal.org.bouncycastle.asn1.ASN1EncodableVector;
import com.android.internal.org.bouncycastle.asn1.ASN1Object;
import com.android.internal.org.bouncycastle.asn1.ASN1ObjectIdentifier;
import com.android.internal.org.bouncycastle.asn1.ASN1Primitive;
import com.android.internal.org.bouncycastle.asn1.ASN1Set;
import com.android.internal.org.bouncycastle.asn1.DERSet;
import com.android.internal.org.bouncycastle.asn1.x509.Attribute;
/* loaded from: classes4.dex */
public class X509Attribute extends ASN1Object {
    Attribute attr;

    /* JADX INFO: Access modifiers changed from: package-private */
    public X509Attribute(ASN1Encodable at) {
        this.attr = Attribute.getInstance(at);
    }

    public X509Attribute(String oid, ASN1Encodable value) {
        this.attr = new Attribute(new ASN1ObjectIdentifier(oid), new DERSet(value));
    }

    public X509Attribute(String oid, ASN1EncodableVector value) {
        this.attr = new Attribute(new ASN1ObjectIdentifier(oid), new DERSet(value));
    }

    public String getOID() {
        return this.attr.getAttrType().getId();
    }

    public ASN1Encodable[] getValues() {
        ASN1Set s = this.attr.getAttrValues();
        ASN1Encodable[] values = new ASN1Encodable[s.size()];
        for (int i = 0; i != s.size(); i++) {
            values[i] = s.getObjectAt(i);
        }
        return values;
    }

    @Override // com.android.internal.org.bouncycastle.asn1.ASN1Object, com.android.internal.org.bouncycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        return this.attr.toASN1Primitive();
    }
}
