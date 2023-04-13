package com.android.internal.org.bouncycastle.asn1;

import java.io.IOException;
import java.io.OutputStream;
/* loaded from: classes4.dex */
public abstract class ASN1Primitive extends ASN1Object {
    /* JADX INFO: Access modifiers changed from: package-private */
    public abstract boolean asn1Equals(ASN1Primitive aSN1Primitive);

    /* JADX INFO: Access modifiers changed from: package-private */
    public abstract void encode(ASN1OutputStream aSN1OutputStream, boolean z) throws IOException;

    /* JADX INFO: Access modifiers changed from: package-private */
    public abstract int encodedLength() throws IOException;

    @Override // com.android.internal.org.bouncycastle.asn1.ASN1Object
    public abstract int hashCode();

    /* JADX INFO: Access modifiers changed from: package-private */
    public abstract boolean isConstructed();

    @Override // com.android.internal.org.bouncycastle.asn1.ASN1Object
    public void encodeTo(OutputStream output) throws IOException {
        ASN1OutputStream.create(output).writeObject(this);
    }

    @Override // com.android.internal.org.bouncycastle.asn1.ASN1Object
    public void encodeTo(OutputStream output, String encoding) throws IOException {
        ASN1OutputStream.create(output, encoding).writeObject(this);
    }

    public static ASN1Primitive fromByteArray(byte[] data) throws IOException {
        ASN1InputStream aIn = new ASN1InputStream(data);
        try {
            ASN1Primitive o = aIn.readObject();
            if (aIn.available() != 0) {
                throw new IOException("Extra data detected in stream");
            }
            return o;
        } catch (ClassCastException e) {
            throw new IOException("cannot recognise object in stream");
        }
    }

    @Override // com.android.internal.org.bouncycastle.asn1.ASN1Object
    public final boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        return (o instanceof ASN1Encodable) && asn1Equals(((ASN1Encodable) o).toASN1Primitive());
    }

    public final boolean equals(ASN1Encodable other) {
        return this == other || (other != null && asn1Equals(other.toASN1Primitive()));
    }

    public final boolean equals(ASN1Primitive other) {
        return this == other || asn1Equals(other);
    }

    @Override // com.android.internal.org.bouncycastle.asn1.ASN1Object, com.android.internal.org.bouncycastle.asn1.ASN1Encodable
    public final ASN1Primitive toASN1Primitive() {
        return this;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ASN1Primitive toDERObject() {
        return this;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ASN1Primitive toDLObject() {
        return this;
    }
}
