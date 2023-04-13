package com.android.internal.org.bouncycastle.asn1.p018x9;

import com.android.internal.org.bouncycastle.asn1.ASN1Integer;
import com.android.internal.org.bouncycastle.asn1.ASN1Object;
import com.android.internal.org.bouncycastle.asn1.ASN1Primitive;
import com.android.internal.org.bouncycastle.asn1.ASN1TaggedObject;
import java.math.BigInteger;
/* renamed from: com.android.internal.org.bouncycastle.asn1.x9.DHPublicKey */
/* loaded from: classes4.dex */
public class DHPublicKey extends ASN1Object {

    /* renamed from: y */
    private ASN1Integer f639y;

    public static DHPublicKey getInstance(ASN1TaggedObject obj, boolean explicit) {
        return getInstance(ASN1Integer.getInstance(obj, explicit));
    }

    public static DHPublicKey getInstance(Object obj) {
        if (obj == null || (obj instanceof DHPublicKey)) {
            return (DHPublicKey) obj;
        }
        if (obj instanceof ASN1Integer) {
            return new DHPublicKey((ASN1Integer) obj);
        }
        throw new IllegalArgumentException("Invalid DHPublicKey: " + obj.getClass().getName());
    }

    private DHPublicKey(ASN1Integer y) {
        if (y == null) {
            throw new IllegalArgumentException("'y' cannot be null");
        }
        this.f639y = y;
    }

    public DHPublicKey(BigInteger y) {
        if (y == null) {
            throw new IllegalArgumentException("'y' cannot be null");
        }
        this.f639y = new ASN1Integer(y);
    }

    public BigInteger getY() {
        return this.f639y.getPositiveValue();
    }

    @Override // com.android.internal.org.bouncycastle.asn1.ASN1Object, com.android.internal.org.bouncycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        return this.f639y;
    }
}
