package com.android.internal.org.bouncycastle.asn1.pkcs;

import com.android.internal.org.bouncycastle.asn1.ASN1Object;
import com.android.internal.org.bouncycastle.asn1.ASN1Primitive;
import com.android.internal.org.bouncycastle.asn1.ASN1Sequence;
import com.android.internal.org.bouncycastle.asn1.BERSequence;
import com.android.internal.org.bouncycastle.asn1.DLSequence;
/* loaded from: classes4.dex */
public class AuthenticatedSafe extends ASN1Object {
    private ContentInfo[] info;
    private boolean isBer;

    private AuthenticatedSafe(ASN1Sequence seq) {
        this.isBer = true;
        this.info = new ContentInfo[seq.size()];
        int i = 0;
        while (true) {
            ContentInfo[] contentInfoArr = this.info;
            if (i != contentInfoArr.length) {
                contentInfoArr[i] = ContentInfo.getInstance(seq.getObjectAt(i));
                i++;
            } else {
                this.isBer = seq instanceof BERSequence;
                return;
            }
        }
    }

    public static AuthenticatedSafe getInstance(Object o) {
        if (o instanceof AuthenticatedSafe) {
            return (AuthenticatedSafe) o;
        }
        if (o != null) {
            return new AuthenticatedSafe(ASN1Sequence.getInstance(o));
        }
        return null;
    }

    public AuthenticatedSafe(ContentInfo[] info) {
        this.isBer = true;
        this.info = copy(info);
    }

    public ContentInfo[] getContentInfo() {
        return copy(this.info);
    }

    private ContentInfo[] copy(ContentInfo[] infos) {
        ContentInfo[] tmp = new ContentInfo[infos.length];
        System.arraycopy(infos, 0, tmp, 0, tmp.length);
        return tmp;
    }

    @Override // com.android.internal.org.bouncycastle.asn1.ASN1Object, com.android.internal.org.bouncycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        if (this.isBer) {
            return new BERSequence(this.info);
        }
        return new DLSequence(this.info);
    }
}
