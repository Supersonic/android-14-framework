package com.android.internal.org.bouncycastle.asn1;

import com.android.internal.org.bouncycastle.util.Arrays;
import java.io.IOException;
/* loaded from: classes4.dex */
public class DERBMPString extends ASN1Primitive implements ASN1String {
    private final char[] string;

    public static DERBMPString getInstance(Object obj) {
        if (obj == null || (obj instanceof DERBMPString)) {
            return (DERBMPString) obj;
        }
        if (obj instanceof byte[]) {
            try {
                return (DERBMPString) fromByteArray((byte[]) obj);
            } catch (Exception e) {
                throw new IllegalArgumentException("encoding error in getInstance: " + e.toString());
            }
        }
        throw new IllegalArgumentException("illegal object in getInstance: " + obj.getClass().getName());
    }

    public static DERBMPString getInstance(ASN1TaggedObject obj, boolean explicit) {
        ASN1Primitive o = obj.getObject();
        if (explicit || (o instanceof DERBMPString)) {
            return getInstance(o);
        }
        return new DERBMPString(ASN1OctetString.getInstance(o).getOctets());
    }

    DERBMPString(byte[] string) {
        if (string == null) {
            throw new NullPointerException("'string' cannot be null");
        }
        int byteLen = string.length;
        if ((byteLen & 1) != 0) {
            throw new IllegalArgumentException("malformed BMPString encoding encountered");
        }
        int charLen = byteLen / 2;
        char[] cs = new char[charLen];
        for (int i = 0; i != charLen; i++) {
            cs[i] = (char) ((string[i * 2] << 8) | (string[(i * 2) + 1] & 255));
        }
        this.string = cs;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public DERBMPString(char[] string) {
        if (string == null) {
            throw new NullPointerException("'string' cannot be null");
        }
        this.string = string;
    }

    public DERBMPString(String string) {
        if (string == null) {
            throw new NullPointerException("'string' cannot be null");
        }
        this.string = string.toCharArray();
    }

    @Override // com.android.internal.org.bouncycastle.asn1.ASN1String
    public String getString() {
        return new String(this.string);
    }

    public String toString() {
        return getString();
    }

    @Override // com.android.internal.org.bouncycastle.asn1.ASN1Primitive, com.android.internal.org.bouncycastle.asn1.ASN1Object
    public int hashCode() {
        return Arrays.hashCode(this.string);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.internal.org.bouncycastle.asn1.ASN1Primitive
    public boolean asn1Equals(ASN1Primitive o) {
        if (!(o instanceof DERBMPString)) {
            return false;
        }
        DERBMPString s = (DERBMPString) o;
        return Arrays.areEqual(this.string, s.string);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.android.internal.org.bouncycastle.asn1.ASN1Primitive
    public boolean isConstructed() {
        return false;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.android.internal.org.bouncycastle.asn1.ASN1Primitive
    public int encodedLength() {
        return StreamUtil.calculateBodyLength(this.string.length * 2) + 1 + (this.string.length * 2);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.android.internal.org.bouncycastle.asn1.ASN1Primitive
    public void encode(ASN1OutputStream out, boolean withTag) throws IOException {
        int count = this.string.length;
        if (withTag) {
            out.write(30);
        }
        out.writeLength(count * 2);
        byte[] buf = new byte[8];
        int i = 0;
        int limit = count & (-4);
        while (i < limit) {
            char[] cArr = this.string;
            char c0 = cArr[i];
            char c1 = cArr[i + 1];
            char c2 = cArr[i + 2];
            char c3 = cArr[i + 3];
            i += 4;
            buf[0] = (byte) (c0 >> '\b');
            buf[1] = (byte) c0;
            buf[2] = (byte) (c1 >> '\b');
            buf[3] = (byte) c1;
            buf[4] = (byte) (c2 >> '\b');
            buf[5] = (byte) c2;
            buf[6] = (byte) (c3 >> '\b');
            buf[7] = (byte) c3;
            out.write(buf, 0, 8);
        }
        if (i < count) {
            int bufPos = 0;
            do {
                char c02 = this.string[i];
                i++;
                int bufPos2 = bufPos + 1;
                buf[bufPos] = (byte) (c02 >> '\b');
                bufPos = bufPos2 + 1;
                buf[bufPos2] = (byte) c02;
            } while (i < count);
            out.write(buf, 0, bufPos);
        }
    }
}
