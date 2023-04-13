package com.android.internal.org.bouncycastle.its.asn1;

import com.android.internal.org.bouncycastle.asn1.ASN1Null;
import com.android.internal.org.bouncycastle.asn1.ASN1Object;
import com.android.internal.org.bouncycastle.asn1.ASN1Primitive;
import com.android.internal.org.bouncycastle.asn1.ASN1Sequence;
import com.android.internal.org.bouncycastle.asn1.DERNull;
import java.io.IOException;
/* loaded from: classes4.dex */
public class SspRange extends ASN1Object {
    private final BitmapSspRange bitmapSspRange;
    private final boolean isAll;
    private final SequenceOfOctetString opaque;

    private SspRange() {
        this.isAll = true;
        this.opaque = null;
        this.bitmapSspRange = null;
    }

    private SspRange(SequenceOfOctetString seq) {
        BitmapSspRange bitMapRange;
        this.isAll = false;
        if (seq.size() != 2) {
            this.opaque = seq;
            this.bitmapSspRange = null;
            return;
        }
        this.opaque = SequenceOfOctetString.getInstance(seq);
        try {
            bitMapRange = BitmapSspRange.getInstance(seq);
        } catch (IllegalArgumentException e) {
            bitMapRange = null;
        }
        this.bitmapSspRange = bitMapRange;
    }

    public SspRange(BitmapSspRange range) {
        this.isAll = false;
        this.bitmapSspRange = range;
        this.opaque = null;
    }

    public static SspRange getInstance(Object src) {
        if (src == null) {
            return null;
        }
        if (src instanceof SspRange) {
            return (SspRange) src;
        }
        if (src instanceof ASN1Null) {
            return new SspRange();
        }
        if (src instanceof ASN1Sequence) {
            return new SspRange(SequenceOfOctetString.getInstance(src));
        }
        if (src instanceof byte[]) {
            try {
                return getInstance(ASN1Primitive.fromByteArray((byte[]) src));
            } catch (IOException e) {
                throw new IllegalArgumentException("unable to parse encoded general name");
            }
        }
        throw new IllegalArgumentException("unknown object in getInstance: " + src.getClass().getName());
    }

    public boolean isAll() {
        return this.isAll;
    }

    public boolean maybeOpaque() {
        return this.opaque != null;
    }

    public BitmapSspRange getBitmapSspRange() {
        return this.bitmapSspRange;
    }

    public SequenceOfOctetString getOpaque() {
        return this.opaque;
    }

    @Override // com.android.internal.org.bouncycastle.asn1.ASN1Object, com.android.internal.org.bouncycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        if (this.isAll) {
            return DERNull.INSTANCE;
        }
        BitmapSspRange bitmapSspRange = this.bitmapSspRange;
        if (bitmapSspRange != null) {
            return bitmapSspRange.toASN1Primitive();
        }
        return this.opaque.toASN1Primitive();
    }
}
