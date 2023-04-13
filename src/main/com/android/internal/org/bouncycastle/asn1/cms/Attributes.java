package com.android.internal.org.bouncycastle.asn1.cms;

import com.android.internal.org.bouncycastle.asn1.ASN1EncodableVector;
import com.android.internal.org.bouncycastle.asn1.ASN1Object;
import com.android.internal.org.bouncycastle.asn1.ASN1Primitive;
import com.android.internal.org.bouncycastle.asn1.ASN1Set;
import com.android.internal.org.bouncycastle.asn1.ASN1TaggedObject;
import com.android.internal.org.bouncycastle.asn1.DLSet;
/* loaded from: classes4.dex */
public class Attributes extends ASN1Object {
    private ASN1Set attributes;

    private Attributes(ASN1Set set) {
        this.attributes = set;
    }

    public Attributes(ASN1EncodableVector v) {
        this.attributes = new DLSet(v);
    }

    public static Attributes getInstance(Object obj) {
        if (obj instanceof Attributes) {
            return (Attributes) obj;
        }
        if (obj != null) {
            return new Attributes(ASN1Set.getInstance(obj));
        }
        return null;
    }

    public static Attributes getInstance(ASN1TaggedObject obj, boolean explicit) {
        return getInstance(ASN1Set.getInstance(obj, explicit));
    }

    public Attribute[] getAttributes() {
        Attribute[] rv = new Attribute[this.attributes.size()];
        for (int i = 0; i != rv.length; i++) {
            rv[i] = Attribute.getInstance(this.attributes.getObjectAt(i));
        }
        return rv;
    }

    @Override // com.android.internal.org.bouncycastle.asn1.ASN1Object, com.android.internal.org.bouncycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        return this.attributes;
    }
}
