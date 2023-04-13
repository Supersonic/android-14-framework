package com.android.internal.telephony;

import android.compat.annotation.UnsupportedAppUsage;
/* loaded from: classes.dex */
public class SmsResponse {
    public static final int NO_ERROR_CODE = -1;
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    String mAckPdu;
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    public int mErrorCode;
    public long mMessageId;
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    int mMessageRef;

    @UnsupportedAppUsage
    public SmsResponse(int i, String str, int i2) {
        this(i, str, i2, 0L);
    }

    public SmsResponse(int i, String str, int i2, long j) {
        this.mMessageRef = i;
        this.mAckPdu = str;
        this.mErrorCode = i2;
        this.mMessageId = j;
    }

    public String toString() {
        return "{ mMessageRef = " + this.mMessageRef + ", mErrorCode = " + this.mErrorCode + ", mAckPdu = " + this.mAckPdu + ", " + SmsController.formatCrossStackMessageId(this.mMessageId) + "}";
    }
}
