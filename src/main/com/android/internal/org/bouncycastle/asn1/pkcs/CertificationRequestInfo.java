package com.android.internal.org.bouncycastle.asn1.pkcs;

import com.android.internal.org.bouncycastle.asn1.ASN1EncodableVector;
import com.android.internal.org.bouncycastle.asn1.ASN1Integer;
import com.android.internal.org.bouncycastle.asn1.ASN1Object;
import com.android.internal.org.bouncycastle.asn1.ASN1Primitive;
import com.android.internal.org.bouncycastle.asn1.ASN1Sequence;
import com.android.internal.org.bouncycastle.asn1.ASN1Set;
import com.android.internal.org.bouncycastle.asn1.ASN1TaggedObject;
import com.android.internal.org.bouncycastle.asn1.DERSequence;
import com.android.internal.org.bouncycastle.asn1.DERTaggedObject;
import com.android.internal.org.bouncycastle.asn1.x500.X500Name;
import com.android.internal.org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import com.android.internal.org.bouncycastle.asn1.x509.X509Name;
import java.util.Enumeration;
/* loaded from: classes4.dex */
public class CertificationRequestInfo extends ASN1Object {
    ASN1Set attributes;
    X500Name subject;
    SubjectPublicKeyInfo subjectPKInfo;
    ASN1Integer version;

    public static CertificationRequestInfo getInstance(Object obj) {
        if (obj instanceof CertificationRequestInfo) {
            return (CertificationRequestInfo) obj;
        }
        if (obj != null) {
            return new CertificationRequestInfo(ASN1Sequence.getInstance(obj));
        }
        return null;
    }

    public CertificationRequestInfo(X500Name subject, SubjectPublicKeyInfo pkInfo, ASN1Set attributes) {
        this.version = new ASN1Integer(0L);
        this.attributes = null;
        if (subject == null || pkInfo == null) {
            throw new IllegalArgumentException("Not all mandatory fields set in CertificationRequestInfo generator.");
        }
        validateAttributes(attributes);
        this.subject = subject;
        this.subjectPKInfo = pkInfo;
        this.attributes = attributes;
    }

    public CertificationRequestInfo(X509Name subject, SubjectPublicKeyInfo pkInfo, ASN1Set attributes) {
        this(X500Name.getInstance(subject.toASN1Primitive()), pkInfo, attributes);
    }

    public CertificationRequestInfo(ASN1Sequence seq) {
        this.version = new ASN1Integer(0L);
        this.attributes = null;
        this.version = (ASN1Integer) seq.getObjectAt(0);
        this.subject = X500Name.getInstance(seq.getObjectAt(1));
        this.subjectPKInfo = SubjectPublicKeyInfo.getInstance(seq.getObjectAt(2));
        if (seq.size() > 3) {
            ASN1TaggedObject tagobj = (ASN1TaggedObject) seq.getObjectAt(3);
            this.attributes = ASN1Set.getInstance(tagobj, false);
        }
        validateAttributes(this.attributes);
        if (this.subject == null || this.version == null || this.subjectPKInfo == null) {
            throw new IllegalArgumentException("Not all mandatory fields set in CertificationRequestInfo generator.");
        }
    }

    public ASN1Integer getVersion() {
        return this.version;
    }

    public X500Name getSubject() {
        return this.subject;
    }

    public SubjectPublicKeyInfo getSubjectPublicKeyInfo() {
        return this.subjectPKInfo;
    }

    public ASN1Set getAttributes() {
        return this.attributes;
    }

    @Override // com.android.internal.org.bouncycastle.asn1.ASN1Object, com.android.internal.org.bouncycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector(4);
        v.add(this.version);
        v.add(this.subject);
        v.add(this.subjectPKInfo);
        ASN1Set aSN1Set = this.attributes;
        if (aSN1Set != null) {
            v.add(new DERTaggedObject(false, 0, aSN1Set));
        }
        return new DERSequence(v);
    }

    private static void validateAttributes(ASN1Set attributes) {
        if (attributes == null) {
            return;
        }
        Enumeration en = attributes.getObjects();
        while (en.hasMoreElements()) {
            Attribute attr = Attribute.getInstance(en.nextElement());
            if (attr.getAttrType().equals((ASN1Primitive) PKCSObjectIdentifiers.pkcs_9_at_challengePassword) && attr.getAttrValues().size() != 1) {
                throw new IllegalArgumentException("challengePassword attribute must have one value");
            }
        }
    }
}
