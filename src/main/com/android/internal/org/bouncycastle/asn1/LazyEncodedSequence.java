package com.android.internal.org.bouncycastle.asn1;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Iterator;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes4.dex */
public class LazyEncodedSequence extends ASN1Sequence {
    private byte[] encoded;

    /* JADX INFO: Access modifiers changed from: package-private */
    public LazyEncodedSequence(byte[] encoded) throws IOException {
        this.encoded = encoded;
    }

    @Override // com.android.internal.org.bouncycastle.asn1.ASN1Sequence
    public synchronized ASN1Encodable getObjectAt(int index) {
        force();
        return super.getObjectAt(index);
    }

    @Override // com.android.internal.org.bouncycastle.asn1.ASN1Sequence
    public synchronized Enumeration getObjects() {
        byte[] bArr = this.encoded;
        if (bArr != null) {
            return new LazyConstructionEnumeration(bArr);
        }
        return super.getObjects();
    }

    @Override // com.android.internal.org.bouncycastle.asn1.ASN1Sequence, com.android.internal.org.bouncycastle.asn1.ASN1Primitive, com.android.internal.org.bouncycastle.asn1.ASN1Object
    public synchronized int hashCode() {
        force();
        return super.hashCode();
    }

    @Override // com.android.internal.org.bouncycastle.asn1.ASN1Sequence, com.android.internal.org.bouncycastle.util.Iterable, java.lang.Iterable
    public synchronized Iterator<ASN1Encodable> iterator() {
        force();
        return super.iterator();
    }

    @Override // com.android.internal.org.bouncycastle.asn1.ASN1Sequence
    public synchronized int size() {
        force();
        return super.size();
    }

    @Override // com.android.internal.org.bouncycastle.asn1.ASN1Sequence
    public synchronized ASN1Encodable[] toArray() {
        force();
        return super.toArray();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.android.internal.org.bouncycastle.asn1.ASN1Sequence
    public ASN1Encodable[] toArrayInternal() {
        force();
        return super.toArrayInternal();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.android.internal.org.bouncycastle.asn1.ASN1Primitive
    public synchronized int encodedLength() throws IOException {
        byte[] bArr = this.encoded;
        if (bArr != null) {
            return StreamUtil.calculateBodyLength(bArr.length) + 1 + this.encoded.length;
        }
        return super.toDLObject().encodedLength();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.android.internal.org.bouncycastle.asn1.ASN1Sequence, com.android.internal.org.bouncycastle.asn1.ASN1Primitive
    public synchronized void encode(ASN1OutputStream out, boolean withTag) throws IOException {
        byte[] bArr = this.encoded;
        if (bArr != null) {
            out.writeEncoded(withTag, 48, bArr);
        } else {
            super.toDLObject().encode(out, withTag);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.android.internal.org.bouncycastle.asn1.ASN1Sequence, com.android.internal.org.bouncycastle.asn1.ASN1Primitive
    public synchronized ASN1Primitive toDERObject() {
        force();
        return super.toDERObject();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.android.internal.org.bouncycastle.asn1.ASN1Sequence, com.android.internal.org.bouncycastle.asn1.ASN1Primitive
    public synchronized ASN1Primitive toDLObject() {
        force();
        return super.toDLObject();
    }

    private void force() {
        if (this.encoded != null) {
            ASN1EncodableVector v = new ASN1EncodableVector();
            Enumeration en = new LazyConstructionEnumeration(this.encoded);
            while (en.hasMoreElements()) {
                v.add((ASN1Primitive) en.nextElement());
            }
            this.elements = v.takeElements();
            this.encoded = null;
        }
    }
}
