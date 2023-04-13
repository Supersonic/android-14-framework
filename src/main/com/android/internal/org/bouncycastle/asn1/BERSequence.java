package com.android.internal.org.bouncycastle.asn1;

import java.io.IOException;
/* loaded from: classes4.dex */
public class BERSequence extends ASN1Sequence {
    public BERSequence() {
    }

    public BERSequence(ASN1Encodable element) {
        super(element);
    }

    public BERSequence(ASN1EncodableVector elementVector) {
        super(elementVector);
    }

    public BERSequence(ASN1Encodable[] elements) {
        super(elements);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.android.internal.org.bouncycastle.asn1.ASN1Primitive
    public int encodedLength() throws IOException {
        int count = this.elements.length;
        int totalLength = 0;
        for (int i = 0; i < count; i++) {
            ASN1Primitive p = this.elements[i].toASN1Primitive();
            totalLength += p.encodedLength();
        }
        int i2 = totalLength + 2;
        return i2 + 2;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.android.internal.org.bouncycastle.asn1.ASN1Sequence, com.android.internal.org.bouncycastle.asn1.ASN1Primitive
    public void encode(ASN1OutputStream out, boolean withTag) throws IOException {
        out.writeEncodedIndef(withTag, 48, this.elements);
    }
}
