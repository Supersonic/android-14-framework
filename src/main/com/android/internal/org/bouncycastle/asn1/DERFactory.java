package com.android.internal.org.bouncycastle.asn1;
/* loaded from: classes4.dex */
class DERFactory {
    static final ASN1Sequence EMPTY_SEQUENCE = new DERSequence();
    static final ASN1Set EMPTY_SET = new DERSet();

    DERFactory() {
    }

    static ASN1Sequence createSequence(ASN1EncodableVector v) {
        if (v.size() < 1) {
            return EMPTY_SEQUENCE;
        }
        return new DERSequence(v);
    }

    static ASN1Set createSet(ASN1EncodableVector v) {
        if (v.size() < 1) {
            return EMPTY_SET;
        }
        return new DERSet(v);
    }
}
