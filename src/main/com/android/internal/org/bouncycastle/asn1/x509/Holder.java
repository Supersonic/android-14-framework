package com.android.internal.org.bouncycastle.asn1.x509;

import com.android.internal.org.bouncycastle.asn1.ASN1EncodableVector;
import com.android.internal.org.bouncycastle.asn1.ASN1Object;
import com.android.internal.org.bouncycastle.asn1.ASN1Primitive;
import com.android.internal.org.bouncycastle.asn1.ASN1Sequence;
import com.android.internal.org.bouncycastle.asn1.ASN1TaggedObject;
import com.android.internal.org.bouncycastle.asn1.DERSequence;
import com.android.internal.org.bouncycastle.asn1.DERTaggedObject;
/* loaded from: classes4.dex */
public class Holder extends ASN1Object {
    public static final int V1_CERTIFICATE_HOLDER = 0;
    public static final int V2_CERTIFICATE_HOLDER = 1;
    IssuerSerial baseCertificateID;
    GeneralNames entityName;
    ObjectDigestInfo objectDigestInfo;
    private int version;

    public static Holder getInstance(Object obj) {
        if (obj instanceof Holder) {
            return (Holder) obj;
        }
        if (obj instanceof ASN1TaggedObject) {
            return new Holder(ASN1TaggedObject.getInstance(obj));
        }
        if (obj != null) {
            return new Holder(ASN1Sequence.getInstance(obj));
        }
        return null;
    }

    private Holder(ASN1TaggedObject tagObj) {
        this.version = 1;
        switch (tagObj.getTagNo()) {
            case 0:
                this.baseCertificateID = IssuerSerial.getInstance(tagObj, true);
                break;
            case 1:
                this.entityName = GeneralNames.getInstance(tagObj, true);
                break;
            default:
                throw new IllegalArgumentException("unknown tag in Holder");
        }
        this.version = 0;
    }

    private Holder(ASN1Sequence seq) {
        this.version = 1;
        if (seq.size() > 3) {
            throw new IllegalArgumentException("Bad sequence size: " + seq.size());
        }
        for (int i = 0; i != seq.size(); i++) {
            ASN1TaggedObject tObj = ASN1TaggedObject.getInstance(seq.getObjectAt(i));
            switch (tObj.getTagNo()) {
                case 0:
                    this.baseCertificateID = IssuerSerial.getInstance(tObj, false);
                    break;
                case 1:
                    this.entityName = GeneralNames.getInstance(tObj, false);
                    break;
                case 2:
                    this.objectDigestInfo = ObjectDigestInfo.getInstance(tObj, false);
                    break;
                default:
                    throw new IllegalArgumentException("unknown tag in Holder");
            }
        }
        this.version = 1;
    }

    public Holder(IssuerSerial baseCertificateID) {
        this(baseCertificateID, 1);
    }

    public Holder(IssuerSerial baseCertificateID, int version) {
        this.version = 1;
        this.baseCertificateID = baseCertificateID;
        this.version = version;
    }

    public int getVersion() {
        return this.version;
    }

    public Holder(GeneralNames entityName) {
        this(entityName, 1);
    }

    public Holder(GeneralNames entityName, int version) {
        this.version = 1;
        this.entityName = entityName;
        this.version = version;
    }

    public Holder(ObjectDigestInfo objectDigestInfo) {
        this.version = 1;
        this.objectDigestInfo = objectDigestInfo;
    }

    public IssuerSerial getBaseCertificateID() {
        return this.baseCertificateID;
    }

    public GeneralNames getEntityName() {
        return this.entityName;
    }

    public ObjectDigestInfo getObjectDigestInfo() {
        return this.objectDigestInfo;
    }

    @Override // com.android.internal.org.bouncycastle.asn1.ASN1Object, com.android.internal.org.bouncycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        if (this.version == 1) {
            ASN1EncodableVector v = new ASN1EncodableVector(3);
            IssuerSerial issuerSerial = this.baseCertificateID;
            if (issuerSerial != null) {
                v.add(new DERTaggedObject(false, 0, issuerSerial));
            }
            GeneralNames generalNames = this.entityName;
            if (generalNames != null) {
                v.add(new DERTaggedObject(false, 1, generalNames));
            }
            ObjectDigestInfo objectDigestInfo = this.objectDigestInfo;
            if (objectDigestInfo != null) {
                v.add(new DERTaggedObject(false, 2, objectDigestInfo));
            }
            return new DERSequence(v);
        }
        GeneralNames generalNames2 = this.entityName;
        if (generalNames2 != null) {
            return new DERTaggedObject(true, 1, generalNames2);
        }
        return new DERTaggedObject(true, 0, this.baseCertificateID);
    }
}
