package com.android.internal.org.bouncycastle.asn1.x509;

import com.android.internal.org.bouncycastle.asn1.ASN1EncodableVector;
import com.android.internal.org.bouncycastle.asn1.ASN1Object;
import com.android.internal.org.bouncycastle.asn1.ASN1Primitive;
import com.android.internal.org.bouncycastle.asn1.ASN1Sequence;
import com.android.internal.org.bouncycastle.asn1.ASN1TaggedObject;
import com.android.internal.org.bouncycastle.asn1.DERSequence;
import com.android.internal.org.bouncycastle.asn1.DERTaggedObject;
/* loaded from: classes4.dex */
public class V2Form extends ASN1Object {
    IssuerSerial baseCertificateID;
    GeneralNames issuerName;
    ObjectDigestInfo objectDigestInfo;

    public static V2Form getInstance(ASN1TaggedObject obj, boolean explicit) {
        return getInstance(ASN1Sequence.getInstance(obj, explicit));
    }

    public static V2Form getInstance(Object obj) {
        if (obj instanceof V2Form) {
            return (V2Form) obj;
        }
        if (obj != null) {
            return new V2Form(ASN1Sequence.getInstance(obj));
        }
        return null;
    }

    public V2Form(GeneralNames issuerName) {
        this(issuerName, null, null);
    }

    public V2Form(GeneralNames issuerName, IssuerSerial baseCertificateID) {
        this(issuerName, baseCertificateID, null);
    }

    public V2Form(GeneralNames issuerName, ObjectDigestInfo objectDigestInfo) {
        this(issuerName, null, objectDigestInfo);
    }

    public V2Form(GeneralNames issuerName, IssuerSerial baseCertificateID, ObjectDigestInfo objectDigestInfo) {
        this.issuerName = issuerName;
        this.baseCertificateID = baseCertificateID;
        this.objectDigestInfo = objectDigestInfo;
    }

    public V2Form(ASN1Sequence seq) {
        if (seq.size() > 3) {
            throw new IllegalArgumentException("Bad sequence size: " + seq.size());
        }
        int index = 0;
        if (!(seq.getObjectAt(0) instanceof ASN1TaggedObject)) {
            index = 0 + 1;
            this.issuerName = GeneralNames.getInstance(seq.getObjectAt(0));
        }
        for (int i = index; i != seq.size(); i++) {
            ASN1TaggedObject o = ASN1TaggedObject.getInstance(seq.getObjectAt(i));
            if (o.getTagNo() == 0) {
                this.baseCertificateID = IssuerSerial.getInstance(o, false);
            } else if (o.getTagNo() == 1) {
                this.objectDigestInfo = ObjectDigestInfo.getInstance(o, false);
            } else {
                throw new IllegalArgumentException("Bad tag number: " + o.getTagNo());
            }
        }
    }

    public GeneralNames getIssuerName() {
        return this.issuerName;
    }

    public IssuerSerial getBaseCertificateID() {
        return this.baseCertificateID;
    }

    public ObjectDigestInfo getObjectDigestInfo() {
        return this.objectDigestInfo;
    }

    @Override // com.android.internal.org.bouncycastle.asn1.ASN1Object, com.android.internal.org.bouncycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector(3);
        GeneralNames generalNames = this.issuerName;
        if (generalNames != null) {
            v.add(generalNames);
        }
        IssuerSerial issuerSerial = this.baseCertificateID;
        if (issuerSerial != null) {
            v.add(new DERTaggedObject(false, 0, issuerSerial));
        }
        ObjectDigestInfo objectDigestInfo = this.objectDigestInfo;
        if (objectDigestInfo != null) {
            v.add(new DERTaggedObject(false, 1, objectDigestInfo));
        }
        return new DERSequence(v);
    }
}
