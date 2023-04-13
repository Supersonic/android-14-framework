package com.android.internal.org.bouncycastle.asn1.x509;

import com.android.internal.org.bouncycastle.asn1.ASN1ObjectIdentifier;
import com.android.internal.org.bouncycastle.asn1.ASN1Primitive;
import com.android.internal.org.bouncycastle.asn1.DERPrintableString;
import com.android.internal.org.bouncycastle.util.encoders.Hex;
import java.io.IOException;
/* loaded from: classes4.dex */
public abstract class X509NameEntryConverter {
    public abstract ASN1Primitive getConvertedValue(ASN1ObjectIdentifier aSN1ObjectIdentifier, String str);

    /* JADX INFO: Access modifiers changed from: protected */
    public ASN1Primitive convertHexEncoded(String str, int off) throws IOException {
        return ASN1Primitive.fromByteArray(Hex.decodeStrict(str, off, str.length() - off));
    }

    protected boolean canBePrintable(String str) {
        return DERPrintableString.isPrintableString(str);
    }
}
