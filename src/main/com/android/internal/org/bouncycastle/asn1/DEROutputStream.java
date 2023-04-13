package com.android.internal.org.bouncycastle.asn1;

import java.io.IOException;
import java.io.OutputStream;
/* loaded from: classes4.dex */
public class DEROutputStream extends ASN1OutputStream {
    public DEROutputStream(OutputStream os) {
        super(os);
    }

    @Override // com.android.internal.org.bouncycastle.asn1.ASN1OutputStream
    void writePrimitive(ASN1Primitive primitive, boolean withTag) throws IOException {
        primitive.toDERObject().encode(this, withTag);
    }

    @Override // com.android.internal.org.bouncycastle.asn1.ASN1OutputStream
    DEROutputStream getDERSubStream() {
        return this;
    }

    @Override // com.android.internal.org.bouncycastle.asn1.ASN1OutputStream
    ASN1OutputStream getDLSubStream() {
        return this;
    }
}
