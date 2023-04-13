package com.android.internal.org.bouncycastle.asn1.x509;

import android.inputmethodservice.navigationbar.NavigationBarInflaterView;
import com.android.internal.org.bouncycastle.asn1.ASN1Boolean;
import com.android.internal.org.bouncycastle.asn1.ASN1EncodableVector;
import com.android.internal.org.bouncycastle.asn1.ASN1Integer;
import com.android.internal.org.bouncycastle.asn1.ASN1Object;
import com.android.internal.org.bouncycastle.asn1.ASN1Primitive;
import com.android.internal.org.bouncycastle.asn1.ASN1Sequence;
import com.android.internal.org.bouncycastle.asn1.ASN1TaggedObject;
import com.android.internal.org.bouncycastle.asn1.DERSequence;
import java.math.BigInteger;
/* loaded from: classes4.dex */
public class BasicConstraints extends ASN1Object {

    /* renamed from: cA */
    ASN1Boolean f619cA;
    ASN1Integer pathLenConstraint;

    public static BasicConstraints getInstance(ASN1TaggedObject obj, boolean explicit) {
        return getInstance(ASN1Sequence.getInstance(obj, explicit));
    }

    public static BasicConstraints getInstance(Object obj) {
        if (obj instanceof BasicConstraints) {
            return (BasicConstraints) obj;
        }
        if (obj instanceof X509Extension) {
            return getInstance(X509Extension.convertValueToObject((X509Extension) obj));
        }
        if (obj != null) {
            return new BasicConstraints(ASN1Sequence.getInstance(obj));
        }
        return null;
    }

    public static BasicConstraints fromExtensions(Extensions extensions) {
        return getInstance(Extensions.getExtensionParsedValue(extensions, Extension.basicConstraints));
    }

    private BasicConstraints(ASN1Sequence seq) {
        this.f619cA = ASN1Boolean.getInstance(false);
        this.pathLenConstraint = null;
        if (seq.size() == 0) {
            this.f619cA = null;
            this.pathLenConstraint = null;
            return;
        }
        if (seq.getObjectAt(0) instanceof ASN1Boolean) {
            this.f619cA = ASN1Boolean.getInstance(seq.getObjectAt(0));
        } else {
            this.f619cA = null;
            this.pathLenConstraint = ASN1Integer.getInstance(seq.getObjectAt(0));
        }
        if (seq.size() > 1) {
            if (this.f619cA != null) {
                this.pathLenConstraint = ASN1Integer.getInstance(seq.getObjectAt(1));
                return;
            }
            throw new IllegalArgumentException("wrong sequence in constructor");
        }
    }

    public BasicConstraints(boolean cA) {
        this.f619cA = ASN1Boolean.getInstance(false);
        this.pathLenConstraint = null;
        if (cA) {
            this.f619cA = ASN1Boolean.getInstance(true);
        } else {
            this.f619cA = null;
        }
        this.pathLenConstraint = null;
    }

    public BasicConstraints(int pathLenConstraint) {
        this.f619cA = ASN1Boolean.getInstance(false);
        this.pathLenConstraint = null;
        this.f619cA = ASN1Boolean.getInstance(true);
        this.pathLenConstraint = new ASN1Integer(pathLenConstraint);
    }

    public boolean isCA() {
        ASN1Boolean aSN1Boolean = this.f619cA;
        return aSN1Boolean != null && aSN1Boolean.isTrue();
    }

    public BigInteger getPathLenConstraint() {
        ASN1Integer aSN1Integer = this.pathLenConstraint;
        if (aSN1Integer != null) {
            return aSN1Integer.getValue();
        }
        return null;
    }

    @Override // com.android.internal.org.bouncycastle.asn1.ASN1Object, com.android.internal.org.bouncycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector(2);
        ASN1Boolean aSN1Boolean = this.f619cA;
        if (aSN1Boolean != null) {
            v.add(aSN1Boolean);
        }
        ASN1Integer aSN1Integer = this.pathLenConstraint;
        if (aSN1Integer != null) {
            v.add(aSN1Integer);
        }
        return new DERSequence(v);
    }

    public String toString() {
        if (this.pathLenConstraint == null) {
            return "BasicConstraints: isCa(" + isCA() + NavigationBarInflaterView.KEY_CODE_END;
        }
        return "BasicConstraints: isCa(" + isCA() + "), pathLenConstraint = " + this.pathLenConstraint.getValue();
    }
}
