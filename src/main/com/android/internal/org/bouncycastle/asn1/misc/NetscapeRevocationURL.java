package com.android.internal.org.bouncycastle.asn1.misc;

import com.android.internal.org.bouncycastle.asn1.DERIA5String;
/* loaded from: classes4.dex */
public class NetscapeRevocationURL extends DERIA5String {
    public NetscapeRevocationURL(DERIA5String str) {
        super(str.getString());
    }

    @Override // com.android.internal.org.bouncycastle.asn1.DERIA5String
    public String toString() {
        return "NetscapeRevocationURL: " + getString();
    }
}
