package com.android.internal.org.bouncycastle.asn1;

import java.io.IOException;
import java.util.Enumeration;
/* loaded from: classes4.dex */
public class BERTaggedObject extends ASN1TaggedObject {
    public BERTaggedObject(int tagNo, ASN1Encodable obj) {
        super(true, tagNo, obj);
    }

    public BERTaggedObject(boolean explicit, int tagNo, ASN1Encodable obj) {
        super(explicit, tagNo, obj);
    }

    public BERTaggedObject(int tagNo) {
        super(false, tagNo, new BERSequence());
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.android.internal.org.bouncycastle.asn1.ASN1Primitive
    public boolean isConstructed() {
        return this.explicit || this.obj.toASN1Primitive().isConstructed();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.android.internal.org.bouncycastle.asn1.ASN1Primitive
    public int encodedLength() throws IOException {
        ASN1Primitive primitive = this.obj.toASN1Primitive();
        int length = primitive.encodedLength();
        if (this.explicit) {
            return StreamUtil.calculateTagLength(this.tagNo) + StreamUtil.calculateBodyLength(length) + length;
        }
        return StreamUtil.calculateTagLength(this.tagNo) + (length - 1);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.android.internal.org.bouncycastle.asn1.ASN1TaggedObject, com.android.internal.org.bouncycastle.asn1.ASN1Primitive
    public void encode(ASN1OutputStream out, boolean withTag) throws IOException {
        Enumeration e;
        out.writeTag(withTag, 160, this.tagNo);
        out.write(128);
        if (!this.explicit) {
            if (this.obj instanceof ASN1OctetString) {
                if (this.obj instanceof BEROctetString) {
                    e = ((BEROctetString) this.obj).getObjects();
                } else {
                    ASN1OctetString octs = (ASN1OctetString) this.obj;
                    BEROctetString berO = new BEROctetString(octs.getOctets());
                    e = berO.getObjects();
                }
            } else if (this.obj instanceof ASN1Sequence) {
                e = ((ASN1Sequence) this.obj).getObjects();
            } else if (this.obj instanceof ASN1Set) {
                e = ((ASN1Set) this.obj).getObjects();
            } else {
                throw new ASN1Exception("not implemented: " + this.obj.getClass().getName());
            }
            out.writeElements(e);
        } else {
            out.writePrimitive(this.obj.toASN1Primitive(), true);
        }
        out.write(0);
        out.write(0);
    }
}
