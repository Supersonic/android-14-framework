package com.android.internal.org.bouncycastle.asn1.x509;

import android.inputmethodservice.navigationbar.NavigationBarInflaterView;
import com.android.internal.org.bouncycastle.asn1.ASN1EncodableVector;
import com.android.internal.org.bouncycastle.asn1.ASN1Object;
import com.android.internal.org.bouncycastle.asn1.ASN1ObjectIdentifier;
import com.android.internal.org.bouncycastle.asn1.ASN1Primitive;
import com.android.internal.org.bouncycastle.asn1.ASN1Sequence;
import com.android.internal.org.bouncycastle.asn1.DERSequence;
/* loaded from: classes4.dex */
public class PolicyInformation extends ASN1Object {
    private ASN1ObjectIdentifier policyIdentifier;
    private ASN1Sequence policyQualifiers;

    private PolicyInformation(ASN1Sequence seq) {
        if (seq.size() < 1 || seq.size() > 2) {
            throw new IllegalArgumentException("Bad sequence size: " + seq.size());
        }
        this.policyIdentifier = ASN1ObjectIdentifier.getInstance(seq.getObjectAt(0));
        if (seq.size() > 1) {
            this.policyQualifiers = ASN1Sequence.getInstance(seq.getObjectAt(1));
        }
    }

    public PolicyInformation(ASN1ObjectIdentifier policyIdentifier) {
        this.policyIdentifier = policyIdentifier;
    }

    public PolicyInformation(ASN1ObjectIdentifier policyIdentifier, ASN1Sequence policyQualifiers) {
        this.policyIdentifier = policyIdentifier;
        this.policyQualifiers = policyQualifiers;
    }

    public static PolicyInformation getInstance(Object obj) {
        if (obj == null || (obj instanceof PolicyInformation)) {
            return (PolicyInformation) obj;
        }
        return new PolicyInformation(ASN1Sequence.getInstance(obj));
    }

    public ASN1ObjectIdentifier getPolicyIdentifier() {
        return this.policyIdentifier;
    }

    public ASN1Sequence getPolicyQualifiers() {
        return this.policyQualifiers;
    }

    @Override // com.android.internal.org.bouncycastle.asn1.ASN1Object, com.android.internal.org.bouncycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector(2);
        v.add(this.policyIdentifier);
        ASN1Sequence aSN1Sequence = this.policyQualifiers;
        if (aSN1Sequence != null) {
            v.add(aSN1Sequence);
        }
        return new DERSequence(v);
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("Policy information: ");
        sb.append(this.policyIdentifier);
        if (this.policyQualifiers != null) {
            StringBuffer p = new StringBuffer();
            for (int i = 0; i < this.policyQualifiers.size(); i++) {
                if (p.length() != 0) {
                    p.append(", ");
                }
                p.append(PolicyQualifierInfo.getInstance(this.policyQualifiers.getObjectAt(i)));
            }
            sb.append(NavigationBarInflaterView.SIZE_MOD_START);
            sb.append(p);
            sb.append(NavigationBarInflaterView.SIZE_MOD_END);
        }
        return sb.toString();
    }
}
