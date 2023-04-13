package com.android.internal.org.bouncycastle.asn1.ocsp;

import com.android.internal.org.bouncycastle.asn1.ASN1Choice;
import com.android.internal.org.bouncycastle.asn1.ASN1Encodable;
import com.android.internal.org.bouncycastle.asn1.ASN1Object;
import com.android.internal.org.bouncycastle.asn1.ASN1OctetString;
import com.android.internal.org.bouncycastle.asn1.ASN1Primitive;
import com.android.internal.org.bouncycastle.asn1.ASN1TaggedObject;
import com.android.internal.org.bouncycastle.asn1.DEROctetString;
import com.android.internal.org.bouncycastle.asn1.DERTaggedObject;
import com.android.internal.org.bouncycastle.asn1.x500.X500Name;
/* loaded from: classes4.dex */
public class ResponderID extends ASN1Object implements ASN1Choice {
    private ASN1Encodable value;

    public ResponderID(ASN1OctetString value) {
        this.value = value;
    }

    public ResponderID(X500Name value) {
        this.value = value;
    }

    public static ResponderID getInstance(Object obj) {
        if (obj instanceof ResponderID) {
            return (ResponderID) obj;
        }
        if (obj instanceof DEROctetString) {
            return new ResponderID((DEROctetString) obj);
        }
        if (obj instanceof ASN1TaggedObject) {
            ASN1TaggedObject o = (ASN1TaggedObject) obj;
            if (o.getTagNo() == 1) {
                return new ResponderID(X500Name.getInstance(o, true));
            }
            return new ResponderID(ASN1OctetString.getInstance(o, true));
        }
        return new ResponderID(X500Name.getInstance(obj));
    }

    public static ResponderID getInstance(ASN1TaggedObject obj, boolean explicit) {
        return getInstance(obj.getObject());
    }

    public byte[] getKeyHash() {
        ASN1Encodable aSN1Encodable = this.value;
        if (aSN1Encodable instanceof ASN1OctetString) {
            ASN1OctetString octetString = (ASN1OctetString) aSN1Encodable;
            return octetString.getOctets();
        }
        return null;
    }

    public X500Name getName() {
        ASN1Encodable aSN1Encodable = this.value;
        if (aSN1Encodable instanceof ASN1OctetString) {
            return null;
        }
        return X500Name.getInstance(aSN1Encodable);
    }

    @Override // com.android.internal.org.bouncycastle.asn1.ASN1Object, com.android.internal.org.bouncycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        ASN1Encodable aSN1Encodable = this.value;
        if (aSN1Encodable instanceof ASN1OctetString) {
            return new DERTaggedObject(true, 2, aSN1Encodable);
        }
        return new DERTaggedObject(true, 1, aSN1Encodable);
    }
}
