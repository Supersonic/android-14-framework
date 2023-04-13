package com.android.internal.org.bouncycastle.asn1.x509;

import com.android.internal.org.bouncycastle.asn1.ASN1EncodableVector;
import com.android.internal.org.bouncycastle.asn1.ASN1Integer;
import com.android.internal.org.bouncycastle.asn1.ASN1Object;
import com.android.internal.org.bouncycastle.asn1.ASN1Primitive;
import com.android.internal.org.bouncycastle.asn1.ASN1Sequence;
import com.android.internal.org.bouncycastle.asn1.ASN1TaggedObject;
import com.android.internal.org.bouncycastle.asn1.DERBitString;
import com.android.internal.org.bouncycastle.asn1.DERSequence;
import com.android.internal.org.bouncycastle.asn1.x500.X500Name;
import java.math.BigInteger;
/* loaded from: classes4.dex */
public class IssuerSerial extends ASN1Object {
    GeneralNames issuer;
    DERBitString issuerUID;
    ASN1Integer serial;

    public static IssuerSerial getInstance(Object obj) {
        if (obj instanceof IssuerSerial) {
            return (IssuerSerial) obj;
        }
        if (obj != null) {
            return new IssuerSerial(ASN1Sequence.getInstance(obj));
        }
        return null;
    }

    public static IssuerSerial getInstance(ASN1TaggedObject obj, boolean explicit) {
        return getInstance(ASN1Sequence.getInstance(obj, explicit));
    }

    private IssuerSerial(ASN1Sequence seq) {
        if (seq.size() != 2 && seq.size() != 3) {
            throw new IllegalArgumentException("Bad sequence size: " + seq.size());
        }
        this.issuer = GeneralNames.getInstance(seq.getObjectAt(0));
        this.serial = ASN1Integer.getInstance(seq.getObjectAt(1));
        if (seq.size() == 3) {
            this.issuerUID = DERBitString.getInstance(seq.getObjectAt(2));
        }
    }

    public IssuerSerial(X500Name issuer, BigInteger serial) {
        this(new GeneralNames(new GeneralName(issuer)), new ASN1Integer(serial));
    }

    public IssuerSerial(GeneralNames issuer, BigInteger serial) {
        this(issuer, new ASN1Integer(serial));
    }

    public IssuerSerial(GeneralNames issuer, ASN1Integer serial) {
        this.issuer = issuer;
        this.serial = serial;
    }

    public GeneralNames getIssuer() {
        return this.issuer;
    }

    public ASN1Integer getSerial() {
        return this.serial;
    }

    public DERBitString getIssuerUID() {
        return this.issuerUID;
    }

    @Override // com.android.internal.org.bouncycastle.asn1.ASN1Object, com.android.internal.org.bouncycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector(3);
        v.add(this.issuer);
        v.add(this.serial);
        DERBitString dERBitString = this.issuerUID;
        if (dERBitString != null) {
            v.add(dERBitString);
        }
        return new DERSequence(v);
    }
}
