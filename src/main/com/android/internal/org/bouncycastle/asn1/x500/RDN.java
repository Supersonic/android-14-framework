package com.android.internal.org.bouncycastle.asn1.x500;

import com.android.internal.org.bouncycastle.asn1.ASN1Encodable;
import com.android.internal.org.bouncycastle.asn1.ASN1EncodableVector;
import com.android.internal.org.bouncycastle.asn1.ASN1Object;
import com.android.internal.org.bouncycastle.asn1.ASN1ObjectIdentifier;
import com.android.internal.org.bouncycastle.asn1.ASN1Primitive;
import com.android.internal.org.bouncycastle.asn1.ASN1Set;
import com.android.internal.org.bouncycastle.asn1.DERSequence;
import com.android.internal.org.bouncycastle.asn1.DERSet;
/* loaded from: classes4.dex */
public class RDN extends ASN1Object {
    private ASN1Set values;

    private RDN(ASN1Set values) {
        this.values = values;
    }

    public static RDN getInstance(Object obj) {
        if (obj instanceof RDN) {
            return (RDN) obj;
        }
        if (obj != null) {
            return new RDN(ASN1Set.getInstance(obj));
        }
        return null;
    }

    public RDN(ASN1ObjectIdentifier oid, ASN1Encodable value) {
        ASN1EncodableVector v = new ASN1EncodableVector(2);
        v.add(oid);
        v.add(value);
        this.values = new DERSet(new DERSequence(v));
    }

    public RDN(AttributeTypeAndValue attrTAndV) {
        this.values = new DERSet(attrTAndV);
    }

    public RDN(AttributeTypeAndValue[] aAndVs) {
        this.values = new DERSet(aAndVs);
    }

    public boolean isMultiValued() {
        return this.values.size() > 1;
    }

    public int size() {
        return this.values.size();
    }

    public AttributeTypeAndValue getFirst() {
        if (this.values.size() == 0) {
            return null;
        }
        return AttributeTypeAndValue.getInstance(this.values.getObjectAt(0));
    }

    public AttributeTypeAndValue[] getTypesAndValues() {
        AttributeTypeAndValue[] tmp = new AttributeTypeAndValue[this.values.size()];
        for (int i = 0; i != tmp.length; i++) {
            tmp[i] = AttributeTypeAndValue.getInstance(this.values.getObjectAt(i));
        }
        return tmp;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int collectAttributeTypes(ASN1ObjectIdentifier[] oids, int oidsOff) {
        int count = this.values.size();
        for (int i = 0; i < count; i++) {
            AttributeTypeAndValue attr = AttributeTypeAndValue.getInstance(this.values.getObjectAt(i));
            oids[oidsOff + i] = attr.getType();
        }
        return count;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean containsAttributeType(ASN1ObjectIdentifier attributeType) {
        int count = this.values.size();
        for (int i = 0; i < count; i++) {
            AttributeTypeAndValue attr = AttributeTypeAndValue.getInstance(this.values.getObjectAt(i));
            if (attr.getType().equals((ASN1Primitive) attributeType)) {
                return true;
            }
        }
        return false;
    }

    @Override // com.android.internal.org.bouncycastle.asn1.ASN1Object, com.android.internal.org.bouncycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        return this.values;
    }
}
