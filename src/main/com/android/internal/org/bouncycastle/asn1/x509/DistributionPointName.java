package com.android.internal.org.bouncycastle.asn1.x509;

import android.inputmethodservice.navigationbar.NavigationBarInflaterView;
import com.android.internal.org.bouncycastle.asn1.ASN1Choice;
import com.android.internal.org.bouncycastle.asn1.ASN1Encodable;
import com.android.internal.org.bouncycastle.asn1.ASN1Object;
import com.android.internal.org.bouncycastle.asn1.ASN1Primitive;
import com.android.internal.org.bouncycastle.asn1.ASN1Set;
import com.android.internal.org.bouncycastle.asn1.ASN1TaggedObject;
import com.android.internal.org.bouncycastle.asn1.DERTaggedObject;
import com.android.internal.org.bouncycastle.util.Strings;
/* loaded from: classes4.dex */
public class DistributionPointName extends ASN1Object implements ASN1Choice {
    public static final int FULL_NAME = 0;
    public static final int NAME_RELATIVE_TO_CRL_ISSUER = 1;
    ASN1Encodable name;
    int type;

    public static DistributionPointName getInstance(ASN1TaggedObject obj, boolean explicit) {
        return getInstance(ASN1TaggedObject.getInstance(obj, true));
    }

    public static DistributionPointName getInstance(Object obj) {
        if (obj == null || (obj instanceof DistributionPointName)) {
            return (DistributionPointName) obj;
        }
        if (obj instanceof ASN1TaggedObject) {
            return new DistributionPointName((ASN1TaggedObject) obj);
        }
        throw new IllegalArgumentException("unknown object in factory: " + obj.getClass().getName());
    }

    public DistributionPointName(int type, ASN1Encodable name) {
        this.type = type;
        this.name = name;
    }

    public DistributionPointName(GeneralNames name) {
        this(0, name);
    }

    public int getType() {
        return this.type;
    }

    public ASN1Encodable getName() {
        return this.name;
    }

    public DistributionPointName(ASN1TaggedObject obj) {
        int tagNo = obj.getTagNo();
        this.type = tagNo;
        if (tagNo == 0) {
            this.name = GeneralNames.getInstance(obj, false);
        } else {
            this.name = ASN1Set.getInstance(obj, false);
        }
    }

    @Override // com.android.internal.org.bouncycastle.asn1.ASN1Object, com.android.internal.org.bouncycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        return new DERTaggedObject(false, this.type, this.name);
    }

    public String toString() {
        String sep = Strings.lineSeparator();
        StringBuffer buf = new StringBuffer();
        buf.append("DistributionPointName: [");
        buf.append(sep);
        if (this.type == 0) {
            appendObject(buf, sep, "fullName", this.name.toString());
        } else {
            appendObject(buf, sep, "nameRelativeToCRLIssuer", this.name.toString());
        }
        buf.append(NavigationBarInflaterView.SIZE_MOD_END);
        buf.append(sep);
        return buf.toString();
    }

    private void appendObject(StringBuffer buf, String sep, String name, String value) {
        buf.append("    ");
        buf.append(name);
        buf.append(":");
        buf.append(sep);
        buf.append("    ");
        buf.append("    ");
        buf.append(value);
        buf.append(sep);
    }
}
