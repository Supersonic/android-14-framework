package com.android.internal.org.bouncycastle.asn1;

import java.io.IOException;
/* loaded from: classes4.dex */
public class DERSequence extends ASN1Sequence {
    private int bodyLength;

    public static DERSequence convert(ASN1Sequence seq) {
        return (DERSequence) seq.toDERObject();
    }

    public DERSequence() {
        this.bodyLength = -1;
    }

    public DERSequence(ASN1Encodable element) {
        super(element);
        this.bodyLength = -1;
    }

    public DERSequence(ASN1EncodableVector elementVector) {
        super(elementVector);
        this.bodyLength = -1;
    }

    public DERSequence(ASN1Encodable[] elements) {
        super(elements);
        this.bodyLength = -1;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public DERSequence(ASN1Encodable[] elements, boolean clone) {
        super(elements, clone);
        this.bodyLength = -1;
    }

    private int getBodyLength() throws IOException {
        if (this.bodyLength < 0) {
            int count = this.elements.length;
            int totalLength = 0;
            for (int i = 0; i < count; i++) {
                ASN1Primitive derObject = this.elements[i].toASN1Primitive().toDERObject();
                totalLength += derObject.encodedLength();
            }
            this.bodyLength = totalLength;
        }
        int count2 = this.bodyLength;
        return count2;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.android.internal.org.bouncycastle.asn1.ASN1Primitive
    public int encodedLength() throws IOException {
        int length = getBodyLength();
        return StreamUtil.calculateBodyLength(length) + 1 + length;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.android.internal.org.bouncycastle.asn1.ASN1Sequence, com.android.internal.org.bouncycastle.asn1.ASN1Primitive
    public void encode(ASN1OutputStream out, boolean withTag) throws IOException {
        if (withTag) {
            out.write(48);
        }
        DEROutputStream derOut = out.getDERSubStream();
        int count = this.elements.length;
        if (this.bodyLength >= 0 || count > 16) {
            int totalLength = getBodyLength();
            out.writeLength(totalLength);
            for (int i = 0; i < count; i++) {
                this.elements[i].toASN1Primitive().toDERObject().encode(derOut, true);
            }
            return;
        }
        int totalLength2 = 0;
        ASN1Primitive[] derObjects = new ASN1Primitive[count];
        for (int i2 = 0; i2 < count; i2++) {
            ASN1Primitive derObject = this.elements[i2].toASN1Primitive().toDERObject();
            derObjects[i2] = derObject;
            totalLength2 += derObject.encodedLength();
        }
        this.bodyLength = totalLength2;
        out.writeLength(totalLength2);
        for (int i3 = 0; i3 < count; i3++) {
            derObjects[i3].encode(derOut, true);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.android.internal.org.bouncycastle.asn1.ASN1Sequence, com.android.internal.org.bouncycastle.asn1.ASN1Primitive
    public ASN1Primitive toDERObject() {
        return this;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.android.internal.org.bouncycastle.asn1.ASN1Sequence, com.android.internal.org.bouncycastle.asn1.ASN1Primitive
    public ASN1Primitive toDLObject() {
        return this;
    }
}
