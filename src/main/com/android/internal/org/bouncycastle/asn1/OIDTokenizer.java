package com.android.internal.org.bouncycastle.asn1;
/* loaded from: classes4.dex */
public class OIDTokenizer {
    private int index = 0;
    private String oid;

    public OIDTokenizer(String oid) {
        this.oid = oid;
    }

    public boolean hasMoreTokens() {
        return this.index != -1;
    }

    public String nextToken() {
        int i = this.index;
        if (i == -1) {
            return null;
        }
        int end = this.oid.indexOf(46, i);
        if (end == -1) {
            String token = this.oid.substring(this.index);
            this.index = -1;
            return token;
        }
        String token2 = this.oid.substring(this.index, end);
        this.index = end + 1;
        return token2;
    }
}
