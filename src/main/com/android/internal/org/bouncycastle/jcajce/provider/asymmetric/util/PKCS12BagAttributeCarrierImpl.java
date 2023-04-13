package com.android.internal.org.bouncycastle.jcajce.provider.asymmetric.util;

import com.android.internal.org.bouncycastle.asn1.ASN1Encodable;
import com.android.internal.org.bouncycastle.asn1.ASN1InputStream;
import com.android.internal.org.bouncycastle.asn1.ASN1ObjectIdentifier;
import com.android.internal.org.bouncycastle.asn1.ASN1OutputStream;
import com.android.internal.org.bouncycastle.asn1.ASN1Primitive;
import com.android.internal.org.bouncycastle.jce.interfaces.PKCS12BagAttributeCarrier;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
/* loaded from: classes4.dex */
public class PKCS12BagAttributeCarrierImpl implements PKCS12BagAttributeCarrier {
    private Hashtable pkcs12Attributes;
    private Vector pkcs12Ordering;

    PKCS12BagAttributeCarrierImpl(Hashtable attributes, Vector ordering) {
        this.pkcs12Attributes = attributes;
        this.pkcs12Ordering = ordering;
    }

    public PKCS12BagAttributeCarrierImpl() {
        this(new Hashtable(), new Vector());
    }

    @Override // com.android.internal.org.bouncycastle.jce.interfaces.PKCS12BagAttributeCarrier
    public void setBagAttribute(ASN1ObjectIdentifier oid, ASN1Encodable attribute) {
        if (this.pkcs12Attributes.containsKey(oid)) {
            this.pkcs12Attributes.put(oid, attribute);
            return;
        }
        this.pkcs12Attributes.put(oid, attribute);
        this.pkcs12Ordering.addElement(oid);
    }

    @Override // com.android.internal.org.bouncycastle.jce.interfaces.PKCS12BagAttributeCarrier
    public ASN1Encodable getBagAttribute(ASN1ObjectIdentifier oid) {
        return (ASN1Encodable) this.pkcs12Attributes.get(oid);
    }

    @Override // com.android.internal.org.bouncycastle.jce.interfaces.PKCS12BagAttributeCarrier
    public Enumeration getBagAttributeKeys() {
        return this.pkcs12Ordering.elements();
    }

    int size() {
        return this.pkcs12Ordering.size();
    }

    Hashtable getAttributes() {
        return this.pkcs12Attributes;
    }

    Vector getOrdering() {
        return this.pkcs12Ordering;
    }

    public void writeObject(ObjectOutputStream out) throws IOException {
        if (this.pkcs12Ordering.size() == 0) {
            out.writeObject(new Hashtable());
            out.writeObject(new Vector());
            return;
        }
        ByteArrayOutputStream bOut = new ByteArrayOutputStream();
        ASN1OutputStream aOut = ASN1OutputStream.create(bOut);
        Enumeration e = getBagAttributeKeys();
        while (e.hasMoreElements()) {
            ASN1ObjectIdentifier oid = ASN1ObjectIdentifier.getInstance(e.nextElement());
            aOut.writeObject((ASN1Primitive) oid);
            aOut.writeObject((ASN1Encodable) this.pkcs12Attributes.get(oid));
        }
        out.writeObject(bOut.toByteArray());
    }

    public void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        Object obj = in.readObject();
        if (obj instanceof Hashtable) {
            this.pkcs12Attributes = (Hashtable) obj;
            this.pkcs12Ordering = (Vector) in.readObject();
            return;
        }
        ASN1InputStream aIn = new ASN1InputStream((byte[]) obj);
        while (true) {
            ASN1ObjectIdentifier oid = (ASN1ObjectIdentifier) aIn.readObject();
            if (oid != null) {
                setBagAttribute(oid, aIn.readObject());
            } else {
                return;
            }
        }
    }
}
