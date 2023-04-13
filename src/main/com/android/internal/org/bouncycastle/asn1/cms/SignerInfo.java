package com.android.internal.org.bouncycastle.asn1.cms;

import com.android.internal.org.bouncycastle.asn1.ASN1EncodableVector;
import com.android.internal.org.bouncycastle.asn1.ASN1Integer;
import com.android.internal.org.bouncycastle.asn1.ASN1Object;
import com.android.internal.org.bouncycastle.asn1.ASN1OctetString;
import com.android.internal.org.bouncycastle.asn1.ASN1Primitive;
import com.android.internal.org.bouncycastle.asn1.ASN1Sequence;
import com.android.internal.org.bouncycastle.asn1.ASN1Set;
import com.android.internal.org.bouncycastle.asn1.ASN1TaggedObject;
import com.android.internal.org.bouncycastle.asn1.DEROctetString;
import com.android.internal.org.bouncycastle.asn1.DERSequence;
import com.android.internal.org.bouncycastle.asn1.DERTaggedObject;
import com.android.internal.org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import java.util.Enumeration;
/* loaded from: classes4.dex */
public class SignerInfo extends ASN1Object {
    private ASN1Set authenticatedAttributes;
    private AlgorithmIdentifier digAlgorithm;
    private AlgorithmIdentifier digEncryptionAlgorithm;
    private ASN1OctetString encryptedDigest;
    private SignerIdentifier sid;
    private ASN1Set unauthenticatedAttributes;
    private ASN1Integer version;

    public static SignerInfo getInstance(Object o) throws IllegalArgumentException {
        if (o instanceof SignerInfo) {
            return (SignerInfo) o;
        }
        if (o != null) {
            return new SignerInfo(ASN1Sequence.getInstance(o));
        }
        return null;
    }

    public SignerInfo(SignerIdentifier sid, AlgorithmIdentifier digAlgorithm, ASN1Set authenticatedAttributes, AlgorithmIdentifier digEncryptionAlgorithm, ASN1OctetString encryptedDigest, ASN1Set unauthenticatedAttributes) {
        if (sid.isTagged()) {
            this.version = new ASN1Integer(3L);
        } else {
            this.version = new ASN1Integer(1L);
        }
        this.sid = sid;
        this.digAlgorithm = digAlgorithm;
        this.authenticatedAttributes = authenticatedAttributes;
        this.digEncryptionAlgorithm = digEncryptionAlgorithm;
        this.encryptedDigest = encryptedDigest;
        this.unauthenticatedAttributes = unauthenticatedAttributes;
    }

    public SignerInfo(SignerIdentifier sid, AlgorithmIdentifier digAlgorithm, Attributes authenticatedAttributes, AlgorithmIdentifier digEncryptionAlgorithm, ASN1OctetString encryptedDigest, Attributes unauthenticatedAttributes) {
        if (sid.isTagged()) {
            this.version = new ASN1Integer(3L);
        } else {
            this.version = new ASN1Integer(1L);
        }
        this.sid = sid;
        this.digAlgorithm = digAlgorithm;
        this.authenticatedAttributes = ASN1Set.getInstance(authenticatedAttributes);
        this.digEncryptionAlgorithm = digEncryptionAlgorithm;
        this.encryptedDigest = encryptedDigest;
        this.unauthenticatedAttributes = ASN1Set.getInstance(unauthenticatedAttributes);
    }

    public SignerInfo(ASN1Sequence seq) {
        Enumeration e = seq.getObjects();
        this.version = (ASN1Integer) e.nextElement();
        this.sid = SignerIdentifier.getInstance(e.nextElement());
        this.digAlgorithm = AlgorithmIdentifier.getInstance(e.nextElement());
        Object obj = e.nextElement();
        if (obj instanceof ASN1TaggedObject) {
            this.authenticatedAttributes = ASN1Set.getInstance((ASN1TaggedObject) obj, false);
            this.digEncryptionAlgorithm = AlgorithmIdentifier.getInstance(e.nextElement());
        } else {
            this.authenticatedAttributes = null;
            this.digEncryptionAlgorithm = AlgorithmIdentifier.getInstance(obj);
        }
        this.encryptedDigest = DEROctetString.getInstance(e.nextElement());
        if (e.hasMoreElements()) {
            this.unauthenticatedAttributes = ASN1Set.getInstance((ASN1TaggedObject) e.nextElement(), false);
        } else {
            this.unauthenticatedAttributes = null;
        }
    }

    public ASN1Integer getVersion() {
        return this.version;
    }

    public SignerIdentifier getSID() {
        return this.sid;
    }

    public ASN1Set getAuthenticatedAttributes() {
        return this.authenticatedAttributes;
    }

    public AlgorithmIdentifier getDigestAlgorithm() {
        return this.digAlgorithm;
    }

    public ASN1OctetString getEncryptedDigest() {
        return this.encryptedDigest;
    }

    public AlgorithmIdentifier getDigestEncryptionAlgorithm() {
        return this.digEncryptionAlgorithm;
    }

    public ASN1Set getUnauthenticatedAttributes() {
        return this.unauthenticatedAttributes;
    }

    @Override // com.android.internal.org.bouncycastle.asn1.ASN1Object, com.android.internal.org.bouncycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector(7);
        v.add(this.version);
        v.add(this.sid);
        v.add(this.digAlgorithm);
        ASN1Set aSN1Set = this.authenticatedAttributes;
        if (aSN1Set != null) {
            v.add(new DERTaggedObject(false, 0, aSN1Set));
        }
        v.add(this.digEncryptionAlgorithm);
        v.add(this.encryptedDigest);
        ASN1Set aSN1Set2 = this.unauthenticatedAttributes;
        if (aSN1Set2 != null) {
            v.add(new DERTaggedObject(false, 1, aSN1Set2));
        }
        return new DERSequence(v);
    }
}
