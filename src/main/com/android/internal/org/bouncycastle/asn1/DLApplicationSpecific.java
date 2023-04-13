package com.android.internal.org.bouncycastle.asn1;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
/* loaded from: classes4.dex */
public class DLApplicationSpecific extends ASN1ApplicationSpecific {
    /* JADX INFO: Access modifiers changed from: package-private */
    public DLApplicationSpecific(boolean isConstructed, int tag, byte[] octets) {
        super(isConstructed, tag, octets);
    }

    public DLApplicationSpecific(int tag, byte[] octets) {
        this(false, tag, octets);
    }

    public DLApplicationSpecific(int tag, ASN1Encodable object) throws IOException {
        this(true, tag, object);
    }

    public DLApplicationSpecific(boolean constructed, int tag, ASN1Encodable object) throws IOException {
        super(constructed || object.toASN1Primitive().isConstructed(), tag, getEncoding(constructed, object));
    }

    private static byte[] getEncoding(boolean explicit, ASN1Encodable object) throws IOException {
        byte[] data = object.toASN1Primitive().getEncoded(ASN1Encoding.f591DL);
        if (explicit) {
            return data;
        }
        int lenBytes = getLengthOfHeader(data);
        byte[] tmp = new byte[data.length - lenBytes];
        System.arraycopy(data, lenBytes, tmp, 0, tmp.length);
        return tmp;
    }

    public DLApplicationSpecific(int tagNo, ASN1EncodableVector vec) {
        super(true, tagNo, getEncodedVector(vec));
    }

    private static byte[] getEncodedVector(ASN1EncodableVector vec) {
        ByteArrayOutputStream bOut = new ByteArrayOutputStream();
        for (int i = 0; i != vec.size(); i++) {
            try {
                bOut.write(((ASN1Object) vec.get(i)).getEncoded(ASN1Encoding.f591DL));
            } catch (IOException e) {
                throw new ASN1ParsingException("malformed object: " + e, e);
            }
        }
        return bOut.toByteArray();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.android.internal.org.bouncycastle.asn1.ASN1ApplicationSpecific, com.android.internal.org.bouncycastle.asn1.ASN1Primitive
    public void encode(ASN1OutputStream out, boolean withTag) throws IOException {
        int flags = 64;
        if (this.isConstructed) {
            flags = 64 | 32;
        }
        out.writeEncoded(withTag, flags, this.tag, this.octets);
    }
}
