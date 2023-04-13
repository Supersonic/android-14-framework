package com.android.internal.org.bouncycastle.asn1;

import com.android.internal.org.bouncycastle.util.Arrays;
import com.android.internal.org.bouncycastle.util.Strings;
import com.android.internal.org.bouncycastle.util.encoders.Hex;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
/* loaded from: classes4.dex */
public abstract class ASN1OctetString extends ASN1Primitive implements ASN1OctetStringParser {
    byte[] string;

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.android.internal.org.bouncycastle.asn1.ASN1Primitive
    public abstract void encode(ASN1OutputStream aSN1OutputStream, boolean z) throws IOException;

    public static ASN1OctetString getInstance(ASN1TaggedObject taggedObject, boolean explicit) {
        if (explicit) {
            if (!taggedObject.isExplicit()) {
                throw new IllegalArgumentException("object implicit - explicit expected.");
            }
            return getInstance(taggedObject.getObject());
        }
        ASN1Primitive o = taggedObject.getObject();
        if (taggedObject.isExplicit()) {
            ASN1OctetString singleSegment = getInstance(o);
            if (taggedObject instanceof BERTaggedObject) {
                return new BEROctetString(new ASN1OctetString[]{singleSegment});
            }
            return (ASN1OctetString) new BEROctetString(new ASN1OctetString[]{singleSegment}).toDLObject();
        } else if (o instanceof ASN1OctetString) {
            ASN1OctetString s = (ASN1OctetString) o;
            if (taggedObject instanceof BERTaggedObject) {
                return s;
            }
            return (ASN1OctetString) s.toDLObject();
        } else if (o instanceof ASN1Sequence) {
            ASN1Sequence s2 = (ASN1Sequence) o;
            if (taggedObject instanceof BERTaggedObject) {
                return BEROctetString.fromSequence(s2);
            }
            return (ASN1OctetString) BEROctetString.fromSequence(s2).toDLObject();
        } else {
            throw new IllegalArgumentException("unknown object in getInstance: " + taggedObject.getClass().getName());
        }
    }

    public static ASN1OctetString getInstance(Object obj) {
        if (obj == null || (obj instanceof ASN1OctetString)) {
            return (ASN1OctetString) obj;
        }
        if (obj instanceof byte[]) {
            try {
                return getInstance(fromByteArray((byte[]) obj));
            } catch (IOException e) {
                throw new IllegalArgumentException("failed to construct OCTET STRING from byte[]: " + e.getMessage());
            }
        }
        if (obj instanceof ASN1Encodable) {
            ASN1Primitive primitive = ((ASN1Encodable) obj).toASN1Primitive();
            if (primitive instanceof ASN1OctetString) {
                return (ASN1OctetString) primitive;
            }
        }
        throw new IllegalArgumentException("illegal object in getInstance: " + obj.getClass().getName());
    }

    public ASN1OctetString(byte[] string) {
        if (string == null) {
            throw new NullPointerException("'string' cannot be null");
        }
        this.string = string;
    }

    @Override // com.android.internal.org.bouncycastle.asn1.ASN1OctetStringParser
    public InputStream getOctetStream() {
        return new ByteArrayInputStream(this.string);
    }

    public ASN1OctetStringParser parser() {
        return this;
    }

    public byte[] getOctets() {
        return this.string;
    }

    @Override // com.android.internal.org.bouncycastle.asn1.ASN1Primitive, com.android.internal.org.bouncycastle.asn1.ASN1Object
    public int hashCode() {
        return Arrays.hashCode(getOctets());
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.android.internal.org.bouncycastle.asn1.ASN1Primitive
    public boolean asn1Equals(ASN1Primitive o) {
        if (!(o instanceof ASN1OctetString)) {
            return false;
        }
        ASN1OctetString other = (ASN1OctetString) o;
        return Arrays.areEqual(this.string, other.string);
    }

    @Override // com.android.internal.org.bouncycastle.asn1.InMemoryRepresentable
    public ASN1Primitive getLoadedObject() {
        return toASN1Primitive();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.android.internal.org.bouncycastle.asn1.ASN1Primitive
    public ASN1Primitive toDERObject() {
        return new DEROctetString(this.string);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.android.internal.org.bouncycastle.asn1.ASN1Primitive
    public ASN1Primitive toDLObject() {
        return new DEROctetString(this.string);
    }

    public String toString() {
        return "#" + Strings.fromByteArray(Hex.encode(this.string));
    }
}
