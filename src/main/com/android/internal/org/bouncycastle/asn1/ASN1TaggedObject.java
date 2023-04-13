package com.android.internal.org.bouncycastle.asn1;

import android.inputmethodservice.navigationbar.NavigationBarInflaterView;
import java.io.IOException;
/* loaded from: classes4.dex */
public abstract class ASN1TaggedObject extends ASN1Primitive implements ASN1TaggedObjectParser {
    final boolean explicit;
    final ASN1Encodable obj;
    final int tagNo;

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.android.internal.org.bouncycastle.asn1.ASN1Primitive
    public abstract void encode(ASN1OutputStream aSN1OutputStream, boolean z) throws IOException;

    public static ASN1TaggedObject getInstance(ASN1TaggedObject obj, boolean explicit) {
        if (explicit) {
            return getInstance(obj.getObject());
        }
        throw new IllegalArgumentException("implicitly tagged tagged object");
    }

    public static ASN1TaggedObject getInstance(Object obj) {
        if (obj == null || (obj instanceof ASN1TaggedObject)) {
            return (ASN1TaggedObject) obj;
        }
        if (obj instanceof byte[]) {
            try {
                return getInstance(fromByteArray((byte[]) obj));
            } catch (IOException e) {
                throw new IllegalArgumentException("failed to construct tagged object from byte[]: " + e.getMessage());
            }
        }
        throw new IllegalArgumentException("unknown object in getInstance: " + obj.getClass().getName());
    }

    public ASN1TaggedObject(boolean explicit, int tagNo, ASN1Encodable obj) {
        if (obj == null) {
            throw new NullPointerException("'obj' cannot be null");
        }
        this.tagNo = tagNo;
        this.explicit = explicit || (obj instanceof ASN1Choice);
        this.obj = obj;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.android.internal.org.bouncycastle.asn1.ASN1Primitive
    public boolean asn1Equals(ASN1Primitive other) {
        if (other instanceof ASN1TaggedObject) {
            ASN1TaggedObject that = (ASN1TaggedObject) other;
            if (this.tagNo == that.tagNo && this.explicit == that.explicit) {
                ASN1Primitive p1 = this.obj.toASN1Primitive();
                ASN1Primitive p2 = that.obj.toASN1Primitive();
                return p1 == p2 || p1.asn1Equals(p2);
            }
            return false;
        }
        return false;
    }

    @Override // com.android.internal.org.bouncycastle.asn1.ASN1Primitive, com.android.internal.org.bouncycastle.asn1.ASN1Object
    public int hashCode() {
        return (this.tagNo ^ (this.explicit ? 15 : 240)) ^ this.obj.toASN1Primitive().hashCode();
    }

    @Override // com.android.internal.org.bouncycastle.asn1.ASN1TaggedObjectParser
    public int getTagNo() {
        return this.tagNo;
    }

    public boolean isExplicit() {
        return this.explicit;
    }

    public ASN1Primitive getObject() {
        return this.obj.toASN1Primitive();
    }

    @Override // com.android.internal.org.bouncycastle.asn1.ASN1TaggedObjectParser
    public ASN1Encodable getObjectParser(int tag, boolean isExplicit) throws IOException {
        switch (tag) {
            case 4:
                return ASN1OctetString.getInstance(this, isExplicit).parser();
            case 16:
                return ASN1Sequence.getInstance(this, isExplicit).parser();
            case 17:
                return ASN1Set.getInstance(this, isExplicit).parser();
            default:
                if (isExplicit) {
                    return getObject();
                }
                throw new ASN1Exception("implicit tagging not implemented for tag: " + tag);
        }
    }

    @Override // com.android.internal.org.bouncycastle.asn1.InMemoryRepresentable
    public ASN1Primitive getLoadedObject() {
        return toASN1Primitive();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.android.internal.org.bouncycastle.asn1.ASN1Primitive
    public ASN1Primitive toDERObject() {
        return new DERTaggedObject(this.explicit, this.tagNo, this.obj);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.android.internal.org.bouncycastle.asn1.ASN1Primitive
    public ASN1Primitive toDLObject() {
        return new DLTaggedObject(this.explicit, this.tagNo, this.obj);
    }

    public String toString() {
        return NavigationBarInflaterView.SIZE_MOD_START + this.tagNo + NavigationBarInflaterView.SIZE_MOD_END + this.obj;
    }
}
