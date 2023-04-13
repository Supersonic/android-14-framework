package com.android.internal.org.bouncycastle.asn1;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Vector;
/* loaded from: classes4.dex */
public class BERConstructedOctetString extends BEROctetString {
    private static final int MAX_LENGTH = 1000;
    private Vector octs;

    private static byte[] toBytes(Vector octs) {
        ByteArrayOutputStream bOut = new ByteArrayOutputStream();
        for (int i = 0; i != octs.size(); i++) {
            try {
                DEROctetString o = (DEROctetString) octs.elementAt(i);
                bOut.write(o.getOctets());
            } catch (IOException e) {
                throw new IllegalArgumentException("exception converting octets " + e.toString());
            } catch (ClassCastException e2) {
                throw new IllegalArgumentException(octs.elementAt(i).getClass().getName() + " found in input should only contain DEROctetString");
            }
        }
        return bOut.toByteArray();
    }

    public BERConstructedOctetString(byte[] string) {
        super(string);
    }

    public BERConstructedOctetString(Vector octs) {
        super(toBytes(octs));
        this.octs = octs;
    }

    public BERConstructedOctetString(ASN1Primitive obj) {
        super(toByteArray(obj));
    }

    private static byte[] toByteArray(ASN1Primitive obj) {
        try {
            return obj.getEncoded();
        } catch (IOException e) {
            throw new IllegalArgumentException("Unable to encode object");
        }
    }

    public BERConstructedOctetString(ASN1Encodable obj) {
        this(obj.toASN1Primitive());
    }

    @Override // com.android.internal.org.bouncycastle.asn1.ASN1OctetString
    public byte[] getOctets() {
        return this.string;
    }

    @Override // com.android.internal.org.bouncycastle.asn1.BEROctetString
    public Enumeration getObjects() {
        Vector vector = this.octs;
        if (vector == null) {
            return generateOcts().elements();
        }
        return vector.elements();
    }

    private Vector generateOcts() {
        int end;
        Vector vec = new Vector();
        for (int i = 0; i < this.string.length; i += 1000) {
            if (i + 1000 > this.string.length) {
                end = this.string.length;
            } else {
                end = i + 1000;
            }
            byte[] nStr = new byte[end - i];
            System.arraycopy(this.string, i, nStr, 0, nStr.length);
            vec.addElement(new DEROctetString(nStr));
        }
        return vec;
    }

    public static BEROctetString fromSequence(ASN1Sequence seq) {
        Vector v = new Vector();
        Enumeration e = seq.getObjects();
        while (e.hasMoreElements()) {
            v.addElement(e.nextElement());
        }
        return new BERConstructedOctetString(v);
    }
}
