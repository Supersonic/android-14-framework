package com.android.internal.org.bouncycastle.asn1;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.NoSuchElementException;
/* loaded from: classes4.dex */
public class BEROctetString extends ASN1OctetString {
    private static final int DEFAULT_CHUNK_SIZE = 1000;
    private final int chunkSize;
    private final ASN1OctetString[] octs;

    private static byte[] toBytes(ASN1OctetString[] octs) {
        ByteArrayOutputStream bOut = new ByteArrayOutputStream();
        for (int i = 0; i != octs.length; i++) {
            try {
                bOut.write(octs[i].getOctets());
            } catch (IOException e) {
                throw new IllegalArgumentException("exception converting octets " + e.toString());
            }
        }
        return bOut.toByteArray();
    }

    public BEROctetString(byte[] string) {
        this(string, 1000);
    }

    public BEROctetString(ASN1OctetString[] octs) {
        this(octs, 1000);
    }

    public BEROctetString(byte[] string, int chunkSize) {
        this(string, null, chunkSize);
    }

    public BEROctetString(ASN1OctetString[] octs, int chunkSize) {
        this(toBytes(octs), octs, chunkSize);
    }

    private BEROctetString(byte[] string, ASN1OctetString[] octs, int chunkSize) {
        super(string);
        this.octs = octs;
        this.chunkSize = chunkSize;
    }

    public Enumeration getObjects() {
        if (this.octs == null) {
            return new Enumeration() { // from class: com.android.internal.org.bouncycastle.asn1.BEROctetString.1
                int pos = 0;

                @Override // java.util.Enumeration
                public boolean hasMoreElements() {
                    return this.pos < BEROctetString.this.string.length;
                }

                @Override // java.util.Enumeration
                public Object nextElement() {
                    if (this.pos < BEROctetString.this.string.length) {
                        int length = Math.min(BEROctetString.this.string.length - this.pos, BEROctetString.this.chunkSize);
                        byte[] chunk = new byte[length];
                        System.arraycopy(BEROctetString.this.string, this.pos, chunk, 0, length);
                        this.pos += length;
                        return new DEROctetString(chunk);
                    }
                    throw new NoSuchElementException();
                }
            };
        }
        return new Enumeration() { // from class: com.android.internal.org.bouncycastle.asn1.BEROctetString.2
            int counter = 0;

            @Override // java.util.Enumeration
            public boolean hasMoreElements() {
                return this.counter < BEROctetString.this.octs.length;
            }

            @Override // java.util.Enumeration
            public Object nextElement() {
                if (this.counter < BEROctetString.this.octs.length) {
                    ASN1OctetString[] aSN1OctetStringArr = BEROctetString.this.octs;
                    int i = this.counter;
                    this.counter = i + 1;
                    return aSN1OctetStringArr[i];
                }
                throw new NoSuchElementException();
            }
        };
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.android.internal.org.bouncycastle.asn1.ASN1Primitive
    public boolean isConstructed() {
        return true;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.android.internal.org.bouncycastle.asn1.ASN1Primitive
    public int encodedLength() throws IOException {
        int length = 0;
        Enumeration e = getObjects();
        while (e.hasMoreElements()) {
            length += ((ASN1Encodable) e.nextElement()).toASN1Primitive().encodedLength();
        }
        return length + 2 + 2;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.android.internal.org.bouncycastle.asn1.ASN1OctetString, com.android.internal.org.bouncycastle.asn1.ASN1Primitive
    public void encode(ASN1OutputStream out, boolean withTag) throws IOException {
        out.writeEncodedIndef(withTag, 36, getObjects());
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static BEROctetString fromSequence(ASN1Sequence seq) {
        int count = seq.size();
        ASN1OctetString[] v = new ASN1OctetString[count];
        for (int i = 0; i < count; i++) {
            v[i] = ASN1OctetString.getInstance(seq.getObjectAt(i));
        }
        return new BEROctetString(v);
    }
}
