package com.android.internal.org.bouncycastle.asn1;

import java.io.IOException;
/* loaded from: classes4.dex */
public class DERExternalParser implements ASN1Encodable, InMemoryRepresentable {
    private ASN1StreamParser _parser;

    public DERExternalParser(ASN1StreamParser parser) {
        this._parser = parser;
    }

    public ASN1Encodable readObject() throws IOException {
        return this._parser.readObject();
    }

    @Override // com.android.internal.org.bouncycastle.asn1.InMemoryRepresentable
    public ASN1Primitive getLoadedObject() throws IOException {
        try {
            return new DLExternal(this._parser.readVector());
        } catch (IllegalArgumentException e) {
            throw new ASN1Exception(e.getMessage(), e);
        }
    }

    @Override // com.android.internal.org.bouncycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        try {
            return getLoadedObject();
        } catch (IOException ioe) {
            throw new ASN1ParsingException("unable to get DER object", ioe);
        } catch (IllegalArgumentException ioe2) {
            throw new ASN1ParsingException("unable to get DER object", ioe2);
        }
    }
}
