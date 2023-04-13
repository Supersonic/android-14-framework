package com.android.internal.org.bouncycastle.asn1.p018x9;

import com.android.internal.org.bouncycastle.asn1.ASN1Object;
import com.android.internal.org.bouncycastle.asn1.ASN1OctetString;
import com.android.internal.org.bouncycastle.asn1.ASN1Primitive;
import com.android.internal.org.bouncycastle.asn1.DEROctetString;
import com.android.internal.org.bouncycastle.math.p025ec.ECCurve;
import com.android.internal.org.bouncycastle.math.p025ec.ECPoint;
import com.android.internal.org.bouncycastle.util.Arrays;
/* renamed from: com.android.internal.org.bouncycastle.asn1.x9.X9ECPoint */
/* loaded from: classes4.dex */
public class X9ECPoint extends ASN1Object {

    /* renamed from: c */
    private ECCurve f647c;
    private final ASN1OctetString encoding;

    /* renamed from: p */
    private ECPoint f648p;

    public X9ECPoint(ECPoint p, boolean compressed) {
        this.f648p = p.normalize();
        this.encoding = new DEROctetString(p.getEncoded(compressed));
    }

    public X9ECPoint(ECCurve c, byte[] encoding) {
        this.f647c = c;
        this.encoding = new DEROctetString(Arrays.clone(encoding));
    }

    public X9ECPoint(ECCurve c, ASN1OctetString s) {
        this(c, s.getOctets());
    }

    public byte[] getPointEncoding() {
        return Arrays.clone(this.encoding.getOctets());
    }

    public synchronized ECPoint getPoint() {
        if (this.f648p == null) {
            this.f648p = this.f647c.decodePoint(this.encoding.getOctets()).normalize();
        }
        return this.f648p;
    }

    public boolean isPointCompressed() {
        byte[] octets = this.encoding.getOctets();
        if (octets == null || octets.length <= 0) {
            return false;
        }
        return octets[0] == 2 || octets[0] == 3;
    }

    @Override // com.android.internal.org.bouncycastle.asn1.ASN1Object, com.android.internal.org.bouncycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        return this.encoding;
    }
}
