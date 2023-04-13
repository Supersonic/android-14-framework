package com.android.internal.org.bouncycastle.asn1;

import java.io.IOException;
/* loaded from: classes4.dex */
public class DLSet extends ASN1Set {
    private int bodyLength;

    public DLSet() {
        this.bodyLength = -1;
    }

    public DLSet(ASN1Encodable element) {
        super(element);
        this.bodyLength = -1;
    }

    public DLSet(ASN1EncodableVector elementVector) {
        super(elementVector, false);
        this.bodyLength = -1;
    }

    public DLSet(ASN1Encodable[] elements) {
        super(elements, false);
        this.bodyLength = -1;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public DLSet(boolean isSorted, ASN1Encodable[] elements) {
        super(isSorted, elements);
        this.bodyLength = -1;
    }

    private int getBodyLength() throws IOException {
        if (this.bodyLength < 0) {
            int count = this.elements.length;
            int totalLength = 0;
            for (int i = 0; i < count; i++) {
                ASN1Primitive dlObject = this.elements[i].toASN1Primitive().toDLObject();
                totalLength += dlObject.encodedLength();
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
    @Override // com.android.internal.org.bouncycastle.asn1.ASN1Set, com.android.internal.org.bouncycastle.asn1.ASN1Primitive
    public void encode(ASN1OutputStream out, boolean withTag) throws IOException {
        if (withTag) {
            out.write(49);
        }
        ASN1OutputStream dlOut = out.getDLSubStream();
        int count = this.elements.length;
        if (this.bodyLength >= 0 || count > 16) {
            int totalLength = getBodyLength();
            out.writeLength(totalLength);
            for (int i = 0; i < count; i++) {
                dlOut.writePrimitive(this.elements[i].toASN1Primitive(), true);
            }
            return;
        }
        int totalLength2 = 0;
        ASN1Primitive[] dlObjects = new ASN1Primitive[count];
        for (int i2 = 0; i2 < count; i2++) {
            ASN1Primitive dlObject = this.elements[i2].toASN1Primitive().toDLObject();
            dlObjects[i2] = dlObject;
            totalLength2 += dlObject.encodedLength();
        }
        this.bodyLength = totalLength2;
        out.writeLength(totalLength2);
        for (int i3 = 0; i3 < count; i3++) {
            dlOut.writePrimitive(dlObjects[i3], true);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.android.internal.org.bouncycastle.asn1.ASN1Set, com.android.internal.org.bouncycastle.asn1.ASN1Primitive
    public ASN1Primitive toDLObject() {
        return this;
    }
}
