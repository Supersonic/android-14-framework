package com.android.internal.org.bouncycastle.asn1.pkcs;

import com.android.internal.org.bouncycastle.asn1.ASN1Encodable;
import com.android.internal.org.bouncycastle.asn1.ASN1EncodableVector;
import com.android.internal.org.bouncycastle.asn1.ASN1Object;
import com.android.internal.org.bouncycastle.asn1.ASN1ObjectIdentifier;
import com.android.internal.org.bouncycastle.asn1.ASN1Primitive;
import com.android.internal.org.bouncycastle.asn1.ASN1Sequence;
import com.android.internal.org.bouncycastle.asn1.ASN1TaggedObject;
import com.android.internal.org.bouncycastle.asn1.BERSequence;
import com.android.internal.org.bouncycastle.asn1.BERTaggedObject;
import com.android.internal.org.bouncycastle.asn1.DLSequence;
import java.util.Enumeration;
/* loaded from: classes4.dex */
public class ContentInfo extends ASN1Object implements PKCSObjectIdentifiers {
    private ASN1Encodable content;
    private ASN1ObjectIdentifier contentType;
    private boolean isBer;

    public static ContentInfo getInstance(Object obj) {
        if (obj instanceof ContentInfo) {
            return (ContentInfo) obj;
        }
        if (obj != null) {
            return new ContentInfo(ASN1Sequence.getInstance(obj));
        }
        return null;
    }

    private ContentInfo(ASN1Sequence seq) {
        this.isBer = true;
        Enumeration e = seq.getObjects();
        this.contentType = (ASN1ObjectIdentifier) e.nextElement();
        if (e.hasMoreElements()) {
            this.content = ((ASN1TaggedObject) e.nextElement()).getObject();
        }
        this.isBer = seq instanceof BERSequence;
    }

    public ContentInfo(ASN1ObjectIdentifier contentType, ASN1Encodable content) {
        this.isBer = true;
        this.contentType = contentType;
        this.content = content;
    }

    public ASN1ObjectIdentifier getContentType() {
        return this.contentType;
    }

    public ASN1Encodable getContent() {
        return this.content;
    }

    @Override // com.android.internal.org.bouncycastle.asn1.ASN1Object, com.android.internal.org.bouncycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector(2);
        v.add(this.contentType);
        ASN1Encodable aSN1Encodable = this.content;
        if (aSN1Encodable != null) {
            v.add(new BERTaggedObject(true, 0, aSN1Encodable));
        }
        if (this.isBer) {
            return new BERSequence(v);
        }
        return new DLSequence(v);
    }
}
