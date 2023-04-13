package com.android.internal.org.bouncycastle.its.asn1;

import com.android.internal.org.bouncycastle.asn1.ASN1EncodableVector;
import com.android.internal.org.bouncycastle.asn1.ASN1Object;
import com.android.internal.org.bouncycastle.asn1.ASN1Primitive;
import com.android.internal.org.bouncycastle.asn1.ASN1Sequence;
import com.android.internal.org.bouncycastle.asn1.DERSequence;
/* loaded from: classes4.dex */
public class CertificateBase extends ASN1Object {
    private CertificateType type;
    private byte[] version;

    /* JADX INFO: Access modifiers changed from: protected */
    public CertificateBase(ASN1Sequence seq) {
    }

    public static CertificateBase getInstance(Object o) {
        if (o instanceof ImplicitCertificate) {
            return (ImplicitCertificate) o;
        }
        if (o instanceof ExplicitCertificate) {
            return (ExplicitCertificate) o;
        }
        if (o != null) {
            ASN1Sequence seq = ASN1Sequence.getInstance(o);
            if (seq.getObjectAt(1).equals(CertificateType.Implicit)) {
                return ImplicitCertificate.getInstance(seq);
            }
            if (seq.getObjectAt(1).equals(CertificateType.Explicit)) {
                return ExplicitCertificate.getInstance(seq);
            }
            throw new IllegalArgumentException("unknown certificate type");
        }
        return null;
    }

    @Override // com.android.internal.org.bouncycastle.asn1.ASN1Object, com.android.internal.org.bouncycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector();
        return new DERSequence(v);
    }
}
