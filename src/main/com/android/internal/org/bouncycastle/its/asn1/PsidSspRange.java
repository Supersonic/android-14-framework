package com.android.internal.org.bouncycastle.its.asn1;

import com.android.internal.org.bouncycastle.asn1.ASN1EncodableVector;
import com.android.internal.org.bouncycastle.asn1.ASN1Integer;
import com.android.internal.org.bouncycastle.asn1.ASN1Object;
import com.android.internal.org.bouncycastle.asn1.ASN1Primitive;
import com.android.internal.org.bouncycastle.asn1.ASN1Sequence;
import com.android.internal.org.bouncycastle.asn1.DERSequence;
/* loaded from: classes4.dex */
public class PsidSspRange extends ASN1Object {
    private ASN1Integer psid;
    private SspRange sspRange;

    public static PsidSspRange getInstance(Object src) {
        if (src == null) {
            return null;
        }
        if (src instanceof PsidSspRange) {
            return (PsidSspRange) src;
        }
        ASN1Sequence seq = ASN1Sequence.getInstance(src);
        PsidSspRange psidSspRange = new PsidSspRange();
        if (seq.size() < 1 || seq.size() > 2) {
            throw new IllegalStateException("expected sequences with one or optionally two items");
        }
        if (seq.size() == 1) {
            psidSspRange.psid = (ASN1Integer) seq.getObjectAt(0);
        }
        if (seq.size() == 2) {
            psidSspRange.sspRange = SspRange.getInstance(seq.getObjectAt(1));
        }
        return psidSspRange;
    }

    public ASN1Integer getPsid() {
        return this.psid;
    }

    public void setPsid(ASN1Integer psid) {
        this.psid = psid;
    }

    public SspRange getSspRange() {
        return this.sspRange;
    }

    public void setSspRange(SspRange sspRange) {
        this.sspRange = sspRange;
    }

    @Override // com.android.internal.org.bouncycastle.asn1.ASN1Object, com.android.internal.org.bouncycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector avec = new ASN1EncodableVector();
        avec.add(this.psid);
        SspRange sspRange = this.sspRange;
        if (sspRange != null) {
            avec.add(sspRange);
        }
        return new DERSequence(avec);
    }
}
