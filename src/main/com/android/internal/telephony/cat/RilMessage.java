package com.android.internal.telephony.cat;

import android.compat.annotation.UnsupportedAppUsage;
/* JADX INFO: Access modifiers changed from: package-private */
/* compiled from: CatService.java */
/* loaded from: classes.dex */
public class RilMessage {
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    Object mData;
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    int mId;
    ResultCode mResCode;

    /* JADX INFO: Access modifiers changed from: package-private */
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    public RilMessage(int i, String str) {
        this.mId = i;
        this.mData = str;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public RilMessage(RilMessage rilMessage) {
        this.mId = rilMessage.mId;
        this.mData = rilMessage.mData;
        this.mResCode = rilMessage.mResCode;
    }
}
