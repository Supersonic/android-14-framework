package com.android.internal.org.bouncycastle.asn1;

import com.android.internal.org.bouncycastle.util.Encodable;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
/* loaded from: classes4.dex */
public abstract class ASN1Object implements ASN1Encodable, Encodable {
    @Override // com.android.internal.org.bouncycastle.asn1.ASN1Encodable
    public abstract ASN1Primitive toASN1Primitive();

    public void encodeTo(OutputStream output) throws IOException {
        ASN1OutputStream.create(output).writeObject(this);
    }

    public void encodeTo(OutputStream output, String encoding) throws IOException {
        ASN1OutputStream.create(output, encoding).writeObject(this);
    }

    @Override // com.android.internal.org.bouncycastle.util.Encodable
    public byte[] getEncoded() throws IOException {
        ByteArrayOutputStream bOut = new ByteArrayOutputStream();
        encodeTo(bOut);
        return bOut.toByteArray();
    }

    public byte[] getEncoded(String encoding) throws IOException {
        ByteArrayOutputStream bOut = new ByteArrayOutputStream();
        encodeTo(bOut, encoding);
        return bOut.toByteArray();
    }

    public int hashCode() {
        return toASN1Primitive().hashCode();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ASN1Encodable)) {
            return false;
        }
        ASN1Encodable other = (ASN1Encodable) o;
        return toASN1Primitive().equals(other.toASN1Primitive());
    }

    public ASN1Primitive toASN1Object() {
        return toASN1Primitive();
    }

    protected static boolean hasEncodedTagValue(Object obj, int tagValue) {
        return (obj instanceof byte[]) && ((byte[]) obj)[0] == tagValue;
    }
}
