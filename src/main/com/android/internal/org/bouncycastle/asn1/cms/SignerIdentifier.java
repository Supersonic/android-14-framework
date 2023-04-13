package com.android.internal.org.bouncycastle.asn1.cms;

import com.android.internal.org.bouncycastle.asn1.ASN1Choice;
import com.android.internal.org.bouncycastle.asn1.ASN1Encodable;
import com.android.internal.org.bouncycastle.asn1.ASN1Object;
import com.android.internal.org.bouncycastle.asn1.ASN1OctetString;
import com.android.internal.org.bouncycastle.asn1.ASN1Primitive;
import com.android.internal.org.bouncycastle.asn1.ASN1TaggedObject;
import com.android.internal.org.bouncycastle.asn1.DERTaggedObject;
/* loaded from: classes4.dex */
public class SignerIdentifier extends ASN1Object implements ASN1Choice {

    /* renamed from: id */
    private ASN1Encodable f594id;

    public SignerIdentifier(IssuerAndSerialNumber id) {
        this.f594id = id;
    }

    public SignerIdentifier(ASN1OctetString id) {
        this.f594id = new DERTaggedObject(false, 0, id);
    }

    public SignerIdentifier(ASN1Primitive id) {
        this.f594id = id;
    }

    public static SignerIdentifier getInstance(Object o) {
        if (o == null || (o instanceof SignerIdentifier)) {
            return (SignerIdentifier) o;
        }
        if (o instanceof IssuerAndSerialNumber) {
            return new SignerIdentifier((IssuerAndSerialNumber) o);
        }
        if (o instanceof ASN1OctetString) {
            return new SignerIdentifier((ASN1OctetString) o);
        }
        if (o instanceof ASN1Primitive) {
            return new SignerIdentifier((ASN1Primitive) o);
        }
        throw new IllegalArgumentException("Illegal object in SignerIdentifier: " + o.getClass().getName());
    }

    public boolean isTagged() {
        return this.f594id instanceof ASN1TaggedObject;
    }

    public ASN1Encodable getId() {
        ASN1Encodable aSN1Encodable = this.f594id;
        if (aSN1Encodable instanceof ASN1TaggedObject) {
            return ASN1OctetString.getInstance((ASN1TaggedObject) aSN1Encodable, false);
        }
        return aSN1Encodable;
    }

    @Override // com.android.internal.org.bouncycastle.asn1.ASN1Object, com.android.internal.org.bouncycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        return this.f594id.toASN1Primitive();
    }
}
