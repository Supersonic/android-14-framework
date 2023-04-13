package com.android.internal.org.bouncycastle.asn1.x509;

import com.android.internal.org.bouncycastle.asn1.ASN1EncodableVector;
import com.android.internal.org.bouncycastle.asn1.ASN1Integer;
import com.android.internal.org.bouncycastle.asn1.ASN1Object;
import com.android.internal.org.bouncycastle.asn1.ASN1Primitive;
import com.android.internal.org.bouncycastle.asn1.ASN1Sequence;
import com.android.internal.org.bouncycastle.asn1.ASN1TaggedObject;
import com.android.internal.org.bouncycastle.asn1.DERSequence;
import com.android.internal.org.bouncycastle.asn1.DERTaggedObject;
import java.math.BigInteger;
/* loaded from: classes4.dex */
public class PolicyConstraints extends ASN1Object {
    private BigInteger inhibitPolicyMapping;
    private BigInteger requireExplicitPolicyMapping;

    public PolicyConstraints(BigInteger requireExplicitPolicyMapping, BigInteger inhibitPolicyMapping) {
        this.requireExplicitPolicyMapping = requireExplicitPolicyMapping;
        this.inhibitPolicyMapping = inhibitPolicyMapping;
    }

    private PolicyConstraints(ASN1Sequence seq) {
        for (int i = 0; i != seq.size(); i++) {
            ASN1TaggedObject to = ASN1TaggedObject.getInstance(seq.getObjectAt(i));
            if (to.getTagNo() == 0) {
                this.requireExplicitPolicyMapping = ASN1Integer.getInstance(to, false).getValue();
            } else if (to.getTagNo() == 1) {
                this.inhibitPolicyMapping = ASN1Integer.getInstance(to, false).getValue();
            } else {
                throw new IllegalArgumentException("Unknown tag encountered.");
            }
        }
    }

    public static PolicyConstraints getInstance(Object obj) {
        if (obj instanceof PolicyConstraints) {
            return (PolicyConstraints) obj;
        }
        if (obj != null) {
            return new PolicyConstraints(ASN1Sequence.getInstance(obj));
        }
        return null;
    }

    public static PolicyConstraints fromExtensions(Extensions extensions) {
        return getInstance(Extensions.getExtensionParsedValue(extensions, Extension.policyConstraints));
    }

    public BigInteger getRequireExplicitPolicyMapping() {
        return this.requireExplicitPolicyMapping;
    }

    public BigInteger getInhibitPolicyMapping() {
        return this.inhibitPolicyMapping;
    }

    @Override // com.android.internal.org.bouncycastle.asn1.ASN1Object, com.android.internal.org.bouncycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector(2);
        BigInteger bigInteger = this.requireExplicitPolicyMapping;
        if (bigInteger != null) {
            v.add(new DERTaggedObject(false, 0, new ASN1Integer(bigInteger)));
        }
        BigInteger bigInteger2 = this.inhibitPolicyMapping;
        if (bigInteger2 != null) {
            v.add(new DERTaggedObject(false, 1, new ASN1Integer(bigInteger2)));
        }
        return new DERSequence(v);
    }
}
