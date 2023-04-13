package com.android.internal.org.bouncycastle.asn1;

import android.text.format.DateFormat;
import com.android.internal.midi.MidiConstants;
import com.android.internal.org.bouncycastle.util.Arrays;
import java.io.IOException;
/* loaded from: classes4.dex */
public class DERUniversalString extends ASN1Primitive implements ASN1String {
    private static final char[] table = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', DateFormat.CAPITAL_AM_PM, 'B', 'C', 'D', DateFormat.DAY, 'F'};
    private final byte[] string;

    public static DERUniversalString getInstance(Object obj) {
        if (obj == null || (obj instanceof DERUniversalString)) {
            return (DERUniversalString) obj;
        }
        if (obj instanceof byte[]) {
            try {
                return (DERUniversalString) fromByteArray((byte[]) obj);
            } catch (Exception e) {
                throw new IllegalArgumentException("encoding error getInstance: " + e.toString());
            }
        }
        throw new IllegalArgumentException("illegal object in getInstance: " + obj.getClass().getName());
    }

    public static DERUniversalString getInstance(ASN1TaggedObject obj, boolean explicit) {
        ASN1Primitive o = obj.getObject();
        if (explicit || (o instanceof DERUniversalString)) {
            return getInstance(o);
        }
        return new DERUniversalString(ASN1OctetString.getInstance(o).getOctets());
    }

    public DERUniversalString(byte[] string) {
        this.string = Arrays.clone(string);
    }

    @Override // com.android.internal.org.bouncycastle.asn1.ASN1String
    public String getString() {
        StringBuffer buf = new StringBuffer("#");
        try {
            byte[] string = getEncoded();
            for (int i = 0; i != string.length; i++) {
                char[] cArr = table;
                buf.append(cArr[(string[i] >>> 4) & 15]);
                buf.append(cArr[string[i] & MidiConstants.STATUS_CHANNEL_MASK]);
            }
            return buf.toString();
        } catch (IOException e) {
            throw new ASN1ParsingException("internal error encoding UniversalString");
        }
    }

    public String toString() {
        return getString();
    }

    public byte[] getOctets() {
        return Arrays.clone(this.string);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.android.internal.org.bouncycastle.asn1.ASN1Primitive
    public boolean isConstructed() {
        return false;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.android.internal.org.bouncycastle.asn1.ASN1Primitive
    public int encodedLength() {
        return StreamUtil.calculateBodyLength(this.string.length) + 1 + this.string.length;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.android.internal.org.bouncycastle.asn1.ASN1Primitive
    public void encode(ASN1OutputStream out, boolean withTag) throws IOException {
        out.writeEncoded(withTag, 28, this.string);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.android.internal.org.bouncycastle.asn1.ASN1Primitive
    public boolean asn1Equals(ASN1Primitive o) {
        if (!(o instanceof DERUniversalString)) {
            return false;
        }
        return Arrays.areEqual(this.string, ((DERUniversalString) o).string);
    }

    @Override // com.android.internal.org.bouncycastle.asn1.ASN1Primitive, com.android.internal.org.bouncycastle.asn1.ASN1Object
    public int hashCode() {
        return Arrays.hashCode(this.string);
    }
}
