package com.android.internal.org.bouncycastle.asn1.x509;

import com.android.internal.org.bouncycastle.asn1.ASN1Encodable;
import com.android.internal.org.bouncycastle.asn1.ASN1Encoding;
import com.android.internal.org.bouncycastle.asn1.ASN1ObjectIdentifier;
import com.android.internal.org.bouncycastle.asn1.DEROctetString;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Vector;
/* loaded from: classes4.dex */
public class X509ExtensionsGenerator {
    private Hashtable extensions = new Hashtable();
    private Vector extOrdering = new Vector();

    public void reset() {
        this.extensions = new Hashtable();
        this.extOrdering = new Vector();
    }

    public void addExtension(ASN1ObjectIdentifier oid, boolean critical, ASN1Encodable value) {
        try {
            addExtension(oid, critical, value.toASN1Primitive().getEncoded(ASN1Encoding.DER));
        } catch (IOException e) {
            throw new IllegalArgumentException("error encoding value: " + e);
        }
    }

    public void addExtension(ASN1ObjectIdentifier oid, boolean critical, byte[] value) {
        if (this.extensions.containsKey(oid)) {
            throw new IllegalArgumentException("extension " + oid + " already added");
        }
        this.extOrdering.addElement(oid);
        this.extensions.put(oid, new X509Extension(critical, new DEROctetString(value)));
    }

    public boolean isEmpty() {
        return this.extOrdering.isEmpty();
    }

    public X509Extensions generate() {
        return new X509Extensions(this.extOrdering, this.extensions);
    }
}
