package com.android.internal.org.bouncycastle.asn1.x509;

import com.android.internal.org.bouncycastle.asn1.ASN1EncodableVector;
import com.android.internal.org.bouncycastle.asn1.ASN1GeneralizedTime;
import com.android.internal.org.bouncycastle.asn1.ASN1Integer;
import com.android.internal.org.bouncycastle.asn1.ASN1Object;
import com.android.internal.org.bouncycastle.asn1.ASN1Primitive;
import com.android.internal.org.bouncycastle.asn1.ASN1Sequence;
import com.android.internal.org.bouncycastle.asn1.ASN1TaggedObject;
import com.android.internal.org.bouncycastle.asn1.ASN1UTCTime;
import com.android.internal.org.bouncycastle.asn1.DERSequence;
import com.android.internal.org.bouncycastle.asn1.DERTaggedObject;
import com.android.internal.org.bouncycastle.asn1.x500.X500Name;
import java.util.Enumeration;
import java.util.NoSuchElementException;
/* loaded from: classes4.dex */
public class TBSCertList extends ASN1Object {
    Extensions crlExtensions;
    X500Name issuer;
    Time nextUpdate;
    ASN1Sequence revokedCertificates;
    AlgorithmIdentifier signature;
    Time thisUpdate;
    ASN1Integer version;

    /* loaded from: classes4.dex */
    public static class CRLEntry extends ASN1Object {
        Extensions crlEntryExtensions;
        ASN1Sequence seq;

        private CRLEntry(ASN1Sequence seq) {
            if (seq.size() < 2 || seq.size() > 3) {
                throw new IllegalArgumentException("Bad sequence size: " + seq.size());
            }
            this.seq = seq;
        }

        public static CRLEntry getInstance(Object o) {
            if (o instanceof CRLEntry) {
                return (CRLEntry) o;
            }
            if (o != null) {
                return new CRLEntry(ASN1Sequence.getInstance(o));
            }
            return null;
        }

        public ASN1Integer getUserCertificate() {
            return ASN1Integer.getInstance(this.seq.getObjectAt(0));
        }

        public Time getRevocationDate() {
            return Time.getInstance(this.seq.getObjectAt(1));
        }

        public Extensions getExtensions() {
            if (this.crlEntryExtensions == null && this.seq.size() == 3) {
                this.crlEntryExtensions = Extensions.getInstance(this.seq.getObjectAt(2));
            }
            return this.crlEntryExtensions;
        }

        @Override // com.android.internal.org.bouncycastle.asn1.ASN1Object, com.android.internal.org.bouncycastle.asn1.ASN1Encodable
        public ASN1Primitive toASN1Primitive() {
            return this.seq;
        }

        public boolean hasExtensions() {
            return this.seq.size() == 3;
        }
    }

    /* loaded from: classes4.dex */
    private class RevokedCertificatesEnumeration implements Enumeration {

        /* renamed from: en */
        private final Enumeration f624en;

        RevokedCertificatesEnumeration(Enumeration en) {
            this.f624en = en;
        }

        @Override // java.util.Enumeration
        public boolean hasMoreElements() {
            return this.f624en.hasMoreElements();
        }

        @Override // java.util.Enumeration
        public Object nextElement() {
            return CRLEntry.getInstance(this.f624en.nextElement());
        }
    }

    /* loaded from: classes4.dex */
    private class EmptyEnumeration implements Enumeration {
        private EmptyEnumeration() {
        }

        @Override // java.util.Enumeration
        public boolean hasMoreElements() {
            return false;
        }

        @Override // java.util.Enumeration
        public Object nextElement() {
            throw new NoSuchElementException("Empty Enumeration");
        }
    }

    public static TBSCertList getInstance(ASN1TaggedObject obj, boolean explicit) {
        return getInstance(ASN1Sequence.getInstance(obj, explicit));
    }

    public static TBSCertList getInstance(Object obj) {
        if (obj instanceof TBSCertList) {
            return (TBSCertList) obj;
        }
        if (obj != null) {
            return new TBSCertList(ASN1Sequence.getInstance(obj));
        }
        return null;
    }

    public TBSCertList(ASN1Sequence seq) {
        if (seq.size() < 3 || seq.size() > 7) {
            throw new IllegalArgumentException("Bad sequence size: " + seq.size());
        }
        int seqPos = 0;
        if (seq.getObjectAt(0) instanceof ASN1Integer) {
            int seqPos2 = 0 + 1;
            this.version = ASN1Integer.getInstance(seq.getObjectAt(0));
            seqPos = seqPos2;
        } else {
            this.version = null;
        }
        int seqPos3 = seqPos + 1;
        this.signature = AlgorithmIdentifier.getInstance(seq.getObjectAt(seqPos));
        int seqPos4 = seqPos3 + 1;
        this.issuer = X500Name.getInstance(seq.getObjectAt(seqPos3));
        int seqPos5 = seqPos4 + 1;
        this.thisUpdate = Time.getInstance(seq.getObjectAt(seqPos4));
        if (seqPos5 < seq.size() && ((seq.getObjectAt(seqPos5) instanceof ASN1UTCTime) || (seq.getObjectAt(seqPos5) instanceof ASN1GeneralizedTime) || (seq.getObjectAt(seqPos5) instanceof Time))) {
            this.nextUpdate = Time.getInstance(seq.getObjectAt(seqPos5));
            seqPos5++;
        }
        int seqPos6 = seq.size();
        if (seqPos5 < seqPos6 && !(seq.getObjectAt(seqPos5) instanceof ASN1TaggedObject)) {
            this.revokedCertificates = ASN1Sequence.getInstance(seq.getObjectAt(seqPos5));
            seqPos5++;
        }
        int seqPos7 = seq.size();
        if (seqPos5 < seqPos7 && (seq.getObjectAt(seqPos5) instanceof ASN1TaggedObject)) {
            this.crlExtensions = Extensions.getInstance(ASN1Sequence.getInstance((ASN1TaggedObject) seq.getObjectAt(seqPos5), true));
        }
    }

    public int getVersionNumber() {
        ASN1Integer aSN1Integer = this.version;
        if (aSN1Integer == null) {
            return 1;
        }
        return aSN1Integer.intValueExact() + 1;
    }

    public ASN1Integer getVersion() {
        return this.version;
    }

    public AlgorithmIdentifier getSignature() {
        return this.signature;
    }

    public X500Name getIssuer() {
        return this.issuer;
    }

    public Time getThisUpdate() {
        return this.thisUpdate;
    }

    public Time getNextUpdate() {
        return this.nextUpdate;
    }

    public CRLEntry[] getRevokedCertificates() {
        ASN1Sequence aSN1Sequence = this.revokedCertificates;
        if (aSN1Sequence == null) {
            return new CRLEntry[0];
        }
        CRLEntry[] entries = new CRLEntry[aSN1Sequence.size()];
        for (int i = 0; i < entries.length; i++) {
            entries[i] = CRLEntry.getInstance(this.revokedCertificates.getObjectAt(i));
        }
        return entries;
    }

    public Enumeration getRevokedCertificateEnumeration() {
        ASN1Sequence aSN1Sequence = this.revokedCertificates;
        if (aSN1Sequence == null) {
            return new EmptyEnumeration();
        }
        return new RevokedCertificatesEnumeration(aSN1Sequence.getObjects());
    }

    public Extensions getExtensions() {
        return this.crlExtensions;
    }

    @Override // com.android.internal.org.bouncycastle.asn1.ASN1Object, com.android.internal.org.bouncycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector(7);
        ASN1Integer aSN1Integer = this.version;
        if (aSN1Integer != null) {
            v.add(aSN1Integer);
        }
        v.add(this.signature);
        v.add(this.issuer);
        v.add(this.thisUpdate);
        Time time = this.nextUpdate;
        if (time != null) {
            v.add(time);
        }
        ASN1Sequence aSN1Sequence = this.revokedCertificates;
        if (aSN1Sequence != null) {
            v.add(aSN1Sequence);
        }
        Extensions extensions = this.crlExtensions;
        if (extensions != null) {
            v.add(new DERTaggedObject(0, extensions));
        }
        return new DERSequence(v);
    }
}
