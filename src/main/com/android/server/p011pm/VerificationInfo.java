package com.android.server.p011pm;

import android.net.Uri;
/* renamed from: com.android.server.pm.VerificationInfo */
/* loaded from: classes2.dex */
public final class VerificationInfo {
    public final int mInstallerUid;
    public final int mOriginatingUid;
    public final Uri mOriginatingUri;
    public final Uri mReferrer;

    public VerificationInfo(Uri uri, Uri uri2, int i, int i2) {
        this.mOriginatingUri = uri;
        this.mReferrer = uri2;
        this.mOriginatingUid = i;
        this.mInstallerUid = i2;
    }
}
