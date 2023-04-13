package com.android.internal.org.bouncycastle.asn1;

import android.text.format.DateFormat;
import com.android.internal.midi.MidiConstants;
import com.android.internal.org.bouncycastle.util.Arrays;
import com.android.internal.org.bouncycastle.util.p027io.Streams;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
/* loaded from: classes4.dex */
public abstract class ASN1BitString extends ASN1Primitive implements ASN1String {
    private static final char[] table = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', DateFormat.CAPITAL_AM_PM, 'B', 'C', 'D', DateFormat.DAY, 'F'};
    protected final byte[] data;
    protected final int padBits;

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.android.internal.org.bouncycastle.asn1.ASN1Primitive
    public abstract void encode(ASN1OutputStream aSN1OutputStream, boolean z) throws IOException;

    /* JADX INFO: Access modifiers changed from: protected */
    public static int getPadBits(int bitString) {
        int val = 0;
        int i = 3;
        while (true) {
            if (i < 0) {
                break;
            } else if (i != 0) {
                if ((bitString >> (i * 8)) == 0) {
                    i--;
                } else {
                    val = (bitString >> (i * 8)) & 255;
                    break;
                }
            } else if (bitString == 0) {
                i--;
            } else {
                val = bitString & 255;
                break;
            }
        }
        if (val == 0) {
            return 0;
        }
        int bits = 1;
        while (true) {
            int i2 = val << 1;
            val = i2;
            if ((i2 & 255) != 0) {
                bits++;
            } else {
                return 8 - bits;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public static byte[] getBytes(int bitString) {
        if (bitString == 0) {
            return new byte[0];
        }
        int bytes = 4;
        for (int i = 3; i >= 1 && ((255 << (i * 8)) & bitString) == 0; i--) {
            bytes--;
        }
        byte[] result = new byte[bytes];
        for (int i2 = 0; i2 < bytes; i2++) {
            result[i2] = (byte) ((bitString >> (i2 * 8)) & 255);
        }
        return result;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public ASN1BitString(byte data, int padBits) {
        if (padBits > 7 || padBits < 0) {
            throw new IllegalArgumentException("pad bits cannot be greater than 7 or less than 0");
        }
        this.data = new byte[]{data};
        this.padBits = padBits;
    }

    public ASN1BitString(byte[] data, int padBits) {
        if (data == null) {
            throw new NullPointerException("'data' cannot be null");
        }
        if (data.length == 0 && padBits != 0) {
            throw new IllegalArgumentException("zero length data with non-zero pad bits");
        }
        if (padBits > 7 || padBits < 0) {
            throw new IllegalArgumentException("pad bits cannot be greater than 7 or less than 0");
        }
        this.data = Arrays.clone(data);
        this.padBits = padBits;
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
            throw new ASN1ParsingException("Internal error encoding BitString: " + e.getMessage(), e);
        }
    }

    public int intValue() {
        int value = 0;
        int end = Math.min(4, this.data.length - 1);
        for (int i = 0; i < end; i++) {
            value |= (255 & this.data[i]) << (i * 8);
        }
        if (end >= 0 && end < 4) {
            byte der = (byte) (this.data[end] & (255 << this.padBits));
            return value | ((der & 255) << (end * 8));
        }
        return value;
    }

    public byte[] getOctets() {
        if (this.padBits != 0) {
            throw new IllegalStateException("attempt to get non-octet aligned data from BIT STRING");
        }
        return Arrays.clone(this.data);
    }

    public byte[] getBytes() {
        byte[] bArr = this.data;
        if (bArr.length == 0) {
            return bArr;
        }
        byte[] rv = Arrays.clone(bArr);
        int length = this.data.length - 1;
        rv[length] = (byte) (rv[length] & (255 << this.padBits));
        return rv;
    }

    public int getPadBits() {
        return this.padBits;
    }

    public String toString() {
        return getString();
    }

    @Override // com.android.internal.org.bouncycastle.asn1.ASN1Primitive, com.android.internal.org.bouncycastle.asn1.ASN1Object
    public int hashCode() {
        byte[] bArr = this.data;
        int end = bArr.length - 1;
        if (end < 0) {
            return 1;
        }
        byte der = (byte) (bArr[end] & (255 << this.padBits));
        int hc = Arrays.hashCode(bArr, 0, end);
        return this.padBits ^ ((hc * 257) ^ der);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.android.internal.org.bouncycastle.asn1.ASN1Primitive
    public boolean asn1Equals(ASN1Primitive o) {
        if (o instanceof ASN1BitString) {
            ASN1BitString other = (ASN1BitString) o;
            if (this.padBits != other.padBits) {
                return false;
            }
            byte[] a = this.data;
            byte[] b = other.data;
            int end = a.length;
            if (end != b.length) {
                return false;
            }
            int end2 = end - 1;
            if (end2 < 0) {
                return true;
            }
            for (int i = 0; i < end2; i++) {
                if (a[i] != b[i]) {
                    return false;
                }
            }
            int i2 = a[end2];
            int i3 = this.padBits;
            byte derA = (byte) (i2 & (255 << i3));
            byte derB = (byte) ((255 << i3) & b[end2]);
            return derA == derB;
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static ASN1BitString fromInputStream(int length, InputStream stream) throws IOException {
        if (length < 1) {
            throw new IllegalArgumentException("truncated BIT STRING detected");
        }
        int padBits = stream.read();
        byte[] data = new byte[length - 1];
        if (data.length != 0) {
            if (Streams.readFully(stream, data) != data.length) {
                throw new EOFException("EOF encountered in middle of BIT STRING");
            }
            if (padBits > 0 && padBits < 8 && data[data.length - 1] != ((byte) (data[data.length - 1] & (255 << padBits)))) {
                return new DLBitString(data, padBits);
            }
        }
        return new DERBitString(data, padBits);
    }

    public ASN1Primitive getLoadedObject() {
        return toASN1Primitive();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.android.internal.org.bouncycastle.asn1.ASN1Primitive
    public ASN1Primitive toDERObject() {
        return new DERBitString(this.data, this.padBits);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.android.internal.org.bouncycastle.asn1.ASN1Primitive
    public ASN1Primitive toDLObject() {
        return new DLBitString(this.data, this.padBits);
    }
}
