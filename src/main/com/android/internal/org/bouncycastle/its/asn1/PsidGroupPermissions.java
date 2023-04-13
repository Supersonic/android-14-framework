package com.android.internal.org.bouncycastle.its.asn1;

import com.android.internal.org.bouncycastle.asn1.ASN1Integer;
import com.android.internal.org.bouncycastle.asn1.ASN1Object;
import com.android.internal.org.bouncycastle.asn1.ASN1Primitive;
import com.android.internal.org.bouncycastle.asn1.ASN1Sequence;
import java.math.BigInteger;
/* loaded from: classes4.dex */
public class PsidGroupPermissions extends ASN1Object {
    private final BigInteger chainLengthRange;
    private final Object eeType;
    private final BigInteger minChainLength;
    private final SubjectPermissions subjectPermissions;

    private PsidGroupPermissions(ASN1Sequence seq) {
        if (seq.size() != 2) {
            throw new IllegalArgumentException("sequence not length 2");
        }
        this.subjectPermissions = SubjectPermissions.getInstance(seq.getObjectAt(0));
        this.minChainLength = ASN1Integer.getInstance(seq.getObjectAt(1)).getValue();
        this.chainLengthRange = ASN1Integer.getInstance(seq.getObjectAt(2)).getValue();
        this.eeType = EndEntityType.getInstance(seq.getObjectAt(3));
    }

    public static PsidGroupPermissions getInstance(Object src) {
        if (src instanceof PsidGroupPermissions) {
            return (PsidGroupPermissions) src;
        }
        if (src != null) {
            return new PsidGroupPermissions(ASN1Sequence.getInstance(src));
        }
        return null;
    }

    @Override // com.android.internal.org.bouncycastle.asn1.ASN1Object, com.android.internal.org.bouncycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        return null;
    }
}
