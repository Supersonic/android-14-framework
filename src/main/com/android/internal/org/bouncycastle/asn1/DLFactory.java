package com.android.internal.org.bouncycastle.asn1;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes4.dex */
public class DLFactory {
    static final ASN1Sequence EMPTY_SEQUENCE = new DLSequence();
    static final ASN1Set EMPTY_SET = new DLSet();

    DLFactory() {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static ASN1Sequence createSequence(ASN1EncodableVector v) {
        if (v.size() < 1) {
            return EMPTY_SEQUENCE;
        }
        return new DLSequence(v);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static ASN1Set createSet(ASN1EncodableVector v) {
        if (v.size() < 1) {
            return EMPTY_SET;
        }
        return new DLSet(v);
    }
}
