package com.android.internal.org.bouncycastle.cms;

import com.android.internal.org.bouncycastle.asn1.ASN1Encodable;
import com.android.internal.org.bouncycastle.asn1.ASN1Encoding;
import com.android.internal.org.bouncycastle.asn1.ASN1ObjectIdentifier;
import com.android.internal.org.bouncycastle.asn1.ASN1Sequence;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;
/* loaded from: classes4.dex */
public class PKCS7ProcessableObject implements CMSTypedData {
    private final ASN1Encodable structure;
    private final ASN1ObjectIdentifier type;

    public PKCS7ProcessableObject(ASN1ObjectIdentifier type, ASN1Encodable structure) {
        this.type = type;
        this.structure = structure;
    }

    @Override // com.android.internal.org.bouncycastle.cms.CMSTypedData
    public ASN1ObjectIdentifier getContentType() {
        return this.type;
    }

    @Override // com.android.internal.org.bouncycastle.cms.CMSProcessable
    public void write(OutputStream cOut) throws IOException, CMSException {
        ASN1Encodable aSN1Encodable = this.structure;
        if (aSN1Encodable instanceof ASN1Sequence) {
            ASN1Sequence s = ASN1Sequence.getInstance(aSN1Encodable);
            Iterator it = s.iterator();
            while (it.hasNext()) {
                ASN1Encodable enc = it.next();
                cOut.write(enc.toASN1Primitive().getEncoded(ASN1Encoding.DER));
            }
            return;
        }
        byte[] encoded = aSN1Encodable.toASN1Primitive().getEncoded(ASN1Encoding.DER);
        int index = 1;
        while ((encoded[index] & 255) > 127) {
            index++;
        }
        int index2 = index + 1;
        cOut.write(encoded, index2, encoded.length - index2);
    }

    @Override // com.android.internal.org.bouncycastle.cms.CMSProcessable
    public Object getContent() {
        return this.structure;
    }
}
