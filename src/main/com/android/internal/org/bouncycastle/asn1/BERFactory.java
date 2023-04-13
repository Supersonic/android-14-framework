package com.android.internal.org.bouncycastle.asn1;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes4.dex */
public class BERFactory {
    static final BERSequence EMPTY_SEQUENCE = new BERSequence();
    static final BERSet EMPTY_SET = new BERSet();

    BERFactory() {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static BERSequence createSequence(ASN1EncodableVector v) {
        if (v.size() < 1) {
            return EMPTY_SEQUENCE;
        }
        return new BERSequence(v);
    }

    static BERSet createSet(ASN1EncodableVector v) {
        if (v.size() < 1) {
            return EMPTY_SET;
        }
        return new BERSet(v);
    }
}
