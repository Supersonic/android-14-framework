package com.android.internal.org.bouncycastle.asn1;

import java.io.InputStream;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes4.dex */
public abstract class LimitedInputStream extends InputStream {
    protected final InputStream _in;
    private int _limit;

    /* JADX INFO: Access modifiers changed from: package-private */
    public LimitedInputStream(InputStream in, int limit) {
        this._in = in;
        this._limit = limit;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int getLimit() {
        return this._limit;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void setParentEofDetect(boolean on) {
        InputStream inputStream = this._in;
        if (inputStream instanceof IndefiniteLengthInputStream) {
            ((IndefiniteLengthInputStream) inputStream).setEofOn00(on);
        }
    }
}
