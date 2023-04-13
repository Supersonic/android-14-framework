package com.android.internal.org.bouncycastle.asn1;

import java.io.IOException;
/* loaded from: classes4.dex */
public class DERBitString extends ASN1BitString {
    public static DERBitString getInstance(Object obj) {
        if (obj == null || (obj instanceof DERBitString)) {
            return (DERBitString) obj;
        }
        if (obj instanceof DLBitString) {
            return new DERBitString(((DLBitString) obj).data, ((DLBitString) obj).padBits);
        }
        if (obj instanceof byte[]) {
            try {
                return (DERBitString) fromByteArray((byte[]) obj);
            } catch (Exception e) {
                throw new IllegalArgumentException("encoding error in getInstance: " + e.toString());
            }
        }
        throw new IllegalArgumentException("illegal object in getInstance: " + obj.getClass().getName());
    }

    public static DERBitString getInstance(ASN1TaggedObject obj, boolean explicit) {
        ASN1Primitive o = obj.getObject();
        if (explicit || (o instanceof DERBitString)) {
            return getInstance(o);
        }
        return fromOctetString(ASN1OctetString.getInstance(o).getOctets());
    }

    protected DERBitString(byte data, int padBits) {
        super(data, padBits);
    }

    public DERBitString(byte[] data, int padBits) {
        super(data, padBits);
    }

    public DERBitString(byte[] data) {
        this(data, 0);
    }

    public DERBitString(int value) {
        super(getBytes(value), getPadBits(value));
    }

    public DERBitString(ASN1Encodable obj) throws IOException {
        super(obj.toASN1Primitive().getEncoded(ASN1Encoding.DER), 0);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.android.internal.org.bouncycastle.asn1.ASN1Primitive
    public boolean isConstructed() {
        return false;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.android.internal.org.bouncycastle.asn1.ASN1Primitive
    public int encodedLength() {
        return StreamUtil.calculateBodyLength(this.data.length + 1) + 1 + this.data.length + 1;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.android.internal.org.bouncycastle.asn1.ASN1BitString, com.android.internal.org.bouncycastle.asn1.ASN1Primitive
    public void encode(ASN1OutputStream out, boolean withTag) throws IOException {
        int len = this.data.length;
        if (len == 0 || this.padBits == 0 || this.data[len - 1] == ((byte) (this.data[len - 1] & (255 << this.padBits)))) {
            out.writeEncoded(withTag, 3, (byte) this.padBits, this.data);
            return;
        }
        byte der = (byte) (this.data[len - 1] & (255 << this.padBits));
        out.writeEncoded(withTag, 3, (byte) this.padBits, this.data, 0, len - 1, der);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.android.internal.org.bouncycastle.asn1.ASN1BitString, com.android.internal.org.bouncycastle.asn1.ASN1Primitive
    public ASN1Primitive toDERObject() {
        return this;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.android.internal.org.bouncycastle.asn1.ASN1BitString, com.android.internal.org.bouncycastle.asn1.ASN1Primitive
    public ASN1Primitive toDLObject() {
        return this;
    }

    static DERBitString fromOctetString(byte[] bytes) {
        if (bytes.length < 1) {
            throw new IllegalArgumentException("truncated BIT STRING detected");
        }
        int padBits = bytes[0];
        byte[] data = new byte[bytes.length - 1];
        if (data.length != 0) {
            System.arraycopy(bytes, 1, data, 0, bytes.length - 1);
        }
        return new DERBitString(data, padBits);
    }
}
