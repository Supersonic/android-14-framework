package com.android.internal.telephony;

import android.compat.annotation.UnsupportedAppUsage;
/* loaded from: classes.dex */
public class RadioCapability {
    public static final int RC_PHASE_APPLY = 2;
    public static final int RC_PHASE_CONFIGURED = 0;
    public static final int RC_PHASE_FINISH = 4;
    public static final int RC_PHASE_START = 1;
    public static final int RC_PHASE_UNSOL_RSP = 3;
    public static final int RC_STATUS_FAIL = 2;
    public static final int RC_STATUS_NONE = 0;
    public static final int RC_STATUS_SUCCESS = 1;
    private String mLogicalModemUuid;
    private int mPhase;
    private int mPhoneId;
    private int mRadioAccessFamily;
    private int mSession;
    private int mStatus;

    public int getVersion() {
        return 1;
    }

    public RadioCapability(int i, int i2, int i3, int i4, String str, int i5) {
        this.mPhoneId = i;
        this.mSession = i2;
        this.mPhase = i3;
        this.mRadioAccessFamily = i4;
        this.mLogicalModemUuid = str;
        this.mStatus = i5;
    }

    public int getPhoneId() {
        return this.mPhoneId;
    }

    public int getSession() {
        return this.mSession;
    }

    public int getPhase() {
        return this.mPhase;
    }

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    public int getRadioAccessFamily() {
        return this.mRadioAccessFamily;
    }

    public String getLogicalModemUuid() {
        return this.mLogicalModemUuid;
    }

    public int getStatus() {
        return this.mStatus;
    }

    public String toString() {
        return "{mPhoneId = " + this.mPhoneId + " mVersion=" + getVersion() + " mSession=" + getSession() + " mPhase=" + getPhase() + " mRadioAccessFamily=" + getRadioAccessFamily() + " mLogicModemId=" + getLogicalModemUuid() + " mStatus=" + getStatus() + "}";
    }
}
