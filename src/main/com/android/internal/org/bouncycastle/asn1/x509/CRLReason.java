package com.android.internal.org.bouncycastle.asn1.x509;

import com.android.internal.org.bouncycastle.asn1.ASN1Enumerated;
import com.android.internal.org.bouncycastle.asn1.ASN1Object;
import com.android.internal.org.bouncycastle.asn1.ASN1Primitive;
import com.android.internal.org.bouncycastle.util.Integers;
import java.math.BigInteger;
import java.util.Hashtable;
/* loaded from: classes4.dex */
public class CRLReason extends ASN1Object {
    public static final int AA_COMPROMISE = 10;
    public static final int AFFILIATION_CHANGED = 3;
    public static final int CA_COMPROMISE = 2;
    public static final int CERTIFICATE_HOLD = 6;
    public static final int CESSATION_OF_OPERATION = 5;
    public static final int KEY_COMPROMISE = 1;
    public static final int PRIVILEGE_WITHDRAWN = 9;
    public static final int REMOVE_FROM_CRL = 8;
    public static final int SUPERSEDED = 4;
    public static final int UNSPECIFIED = 0;
    public static final int aACompromise = 10;
    public static final int affiliationChanged = 3;
    public static final int cACompromise = 2;
    public static final int certificateHold = 6;
    public static final int cessationOfOperation = 5;
    public static final int keyCompromise = 1;
    public static final int privilegeWithdrawn = 9;
    public static final int removeFromCRL = 8;
    public static final int superseded = 4;
    public static final int unspecified = 0;
    private ASN1Enumerated value;
    private static final String[] reasonString = {"unspecified", "keyCompromise", "cACompromise", "affiliationChanged", "superseded", "cessationOfOperation", "certificateHold", "unknown", "removeFromCRL", "privilegeWithdrawn", "aACompromise"};
    private static final Hashtable table = new Hashtable();

    public static CRLReason getInstance(Object o) {
        if (o instanceof CRLReason) {
            return (CRLReason) o;
        }
        if (o != null) {
            return lookup(ASN1Enumerated.getInstance(o).intValueExact());
        }
        return null;
    }

    private CRLReason(int reason) {
        this.value = new ASN1Enumerated(reason);
    }

    public String toString() {
        String str;
        int reason = getValue().intValue();
        if (reason < 0 || reason > 10) {
            str = "invalid";
        } else {
            str = reasonString[reason];
        }
        return "CRLReason: " + str;
    }

    public BigInteger getValue() {
        return this.value.getValue();
    }

    @Override // com.android.internal.org.bouncycastle.asn1.ASN1Object, com.android.internal.org.bouncycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        return this.value;
    }

    public static CRLReason lookup(int value) {
        Integer idx = Integers.valueOf(value);
        Hashtable hashtable = table;
        if (!hashtable.containsKey(idx)) {
            hashtable.put(idx, new CRLReason(value));
        }
        return (CRLReason) hashtable.get(idx);
    }
}
