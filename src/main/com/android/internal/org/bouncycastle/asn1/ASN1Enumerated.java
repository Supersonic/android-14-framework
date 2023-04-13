package com.android.internal.org.bouncycastle.asn1;

import com.android.internal.org.bouncycastle.util.Arrays;
import java.io.IOException;
import java.math.BigInteger;
/* loaded from: classes4.dex */
public class ASN1Enumerated extends ASN1Primitive {
    private static ASN1Enumerated[] cache = new ASN1Enumerated[12];
    private final byte[] bytes;
    private final int start;

    public static ASN1Enumerated getInstance(Object obj) {
        if (obj == null || (obj instanceof ASN1Enumerated)) {
            return (ASN1Enumerated) obj;
        }
        if (obj instanceof byte[]) {
            try {
                return (ASN1Enumerated) fromByteArray((byte[]) obj);
            } catch (Exception e) {
                throw new IllegalArgumentException("encoding error in getInstance: " + e.toString());
            }
        }
        throw new IllegalArgumentException("illegal object in getInstance: " + obj.getClass().getName());
    }

    public static ASN1Enumerated getInstance(ASN1TaggedObject obj, boolean explicit) {
        ASN1Primitive o = obj.getObject();
        if (explicit || (o instanceof ASN1Enumerated)) {
            return getInstance(o);
        }
        return fromOctetString(ASN1OctetString.getInstance(o).getOctets());
    }

    public ASN1Enumerated(int value) {
        if (value < 0) {
            throw new IllegalArgumentException("enumerated must be non-negative");
        }
        this.bytes = BigInteger.valueOf(value).toByteArray();
        this.start = 0;
    }

    public ASN1Enumerated(BigInteger value) {
        if (value.signum() < 0) {
            throw new IllegalArgumentException("enumerated must be non-negative");
        }
        this.bytes = value.toByteArray();
        this.start = 0;
    }

    public ASN1Enumerated(byte[] bytes) {
        if (ASN1Integer.isMalformed(bytes)) {
            throw new IllegalArgumentException("malformed enumerated");
        }
        if ((bytes[0] & 128) != 0) {
            throw new IllegalArgumentException("enumerated must be non-negative");
        }
        this.bytes = Arrays.clone(bytes);
        this.start = ASN1Integer.signBytesToSkip(bytes);
    }

    public BigInteger getValue() {
        return new BigInteger(this.bytes);
    }

    public boolean hasValue(BigInteger x) {
        return x != null && ASN1Integer.intValue(this.bytes, this.start, -1) == x.intValue() && getValue().equals(x);
    }

    public int intValueExact() {
        byte[] bArr = this.bytes;
        int length = bArr.length;
        int i = this.start;
        int count = length - i;
        if (count > 4) {
            throw new ArithmeticException("ASN.1 Enumerated out of int range");
        }
        return ASN1Integer.intValue(bArr, i, -1);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.android.internal.org.bouncycastle.asn1.ASN1Primitive
    public boolean isConstructed() {
        return false;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.android.internal.org.bouncycastle.asn1.ASN1Primitive
    public int encodedLength() {
        return StreamUtil.calculateBodyLength(this.bytes.length) + 1 + this.bytes.length;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.android.internal.org.bouncycastle.asn1.ASN1Primitive
    public void encode(ASN1OutputStream out, boolean withTag) throws IOException {
        out.writeEncoded(withTag, 10, this.bytes);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.android.internal.org.bouncycastle.asn1.ASN1Primitive
    public boolean asn1Equals(ASN1Primitive o) {
        if (!(o instanceof ASN1Enumerated)) {
            return false;
        }
        ASN1Enumerated other = (ASN1Enumerated) o;
        return Arrays.areEqual(this.bytes, other.bytes);
    }

    @Override // com.android.internal.org.bouncycastle.asn1.ASN1Primitive, com.android.internal.org.bouncycastle.asn1.ASN1Object
    public int hashCode() {
        return Arrays.hashCode(this.bytes);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static ASN1Enumerated fromOctetString(byte[] enc) {
        if (enc.length > 1) {
            return new ASN1Enumerated(enc);
        }
        if (enc.length == 0) {
            throw new IllegalArgumentException("ENUMERATED has zero length");
        }
        int value = enc[0] & 255;
        ASN1Enumerated[] aSN1EnumeratedArr = cache;
        if (value >= aSN1EnumeratedArr.length) {
            return new ASN1Enumerated(enc);
        }
        ASN1Enumerated possibleMatch = aSN1EnumeratedArr[value];
        if (possibleMatch == null) {
            ASN1Enumerated possibleMatch2 = new ASN1Enumerated(enc);
            aSN1EnumeratedArr[value] = possibleMatch2;
            return possibleMatch2;
        }
        return possibleMatch;
    }
}
