package com.android.internal.org.bouncycastle.asn1;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
/* loaded from: classes4.dex */
public class DLExternal extends ASN1External {
    public DLExternal(ASN1EncodableVector vector) {
        super(vector);
    }

    public DLExternal(ASN1ObjectIdentifier directReference, ASN1Integer indirectReference, ASN1Primitive dataValueDescriptor, DERTaggedObject externalData) {
        this(directReference, indirectReference, dataValueDescriptor, externalData.getTagNo(), externalData.toASN1Primitive());
    }

    public DLExternal(ASN1ObjectIdentifier directReference, ASN1Integer indirectReference, ASN1Primitive dataValueDescriptor, int encoding, ASN1Primitive externalData) {
        super(directReference, indirectReference, dataValueDescriptor, encoding, externalData);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.android.internal.org.bouncycastle.asn1.ASN1External, com.android.internal.org.bouncycastle.asn1.ASN1Primitive
    public ASN1Primitive toDLObject() {
        return this;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.android.internal.org.bouncycastle.asn1.ASN1External, com.android.internal.org.bouncycastle.asn1.ASN1Primitive
    public int encodedLength() throws IOException {
        return getEncoded().length;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.android.internal.org.bouncycastle.asn1.ASN1Primitive
    public void encode(ASN1OutputStream out, boolean withTag) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        if (this.directReference != null) {
            baos.write(this.directReference.getEncoded(ASN1Encoding.f591DL));
        }
        if (this.indirectReference != null) {
            baos.write(this.indirectReference.getEncoded(ASN1Encoding.f591DL));
        }
        if (this.dataValueDescriptor != null) {
            baos.write(this.dataValueDescriptor.getEncoded(ASN1Encoding.f591DL));
        }
        ASN1TaggedObject obj = new DLTaggedObject(true, this.encoding, this.externalContent);
        baos.write(obj.getEncoded(ASN1Encoding.f591DL));
        out.writeEncoded(withTag, 32, 8, baos.toByteArray());
    }
}
