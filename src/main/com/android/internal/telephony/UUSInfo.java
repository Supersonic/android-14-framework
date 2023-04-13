package com.android.internal.telephony;

import android.compat.annotation.UnsupportedAppUsage;
/* loaded from: classes.dex */
public class UUSInfo {
    public static final int UUS_DCS_IA5c = 4;
    public static final int UUS_DCS_OSIHLP = 1;
    public static final int UUS_DCS_RMCF = 3;
    public static final int UUS_DCS_USP = 0;
    public static final int UUS_DCS_X244 = 2;
    public static final int UUS_TYPE1_IMPLICIT = 0;
    public static final int UUS_TYPE1_NOT_REQUIRED = 2;
    public static final int UUS_TYPE1_REQUIRED = 1;
    public static final int UUS_TYPE2_NOT_REQUIRED = 4;
    public static final int UUS_TYPE2_REQUIRED = 3;
    public static final int UUS_TYPE3_NOT_REQUIRED = 6;
    public static final int UUS_TYPE3_REQUIRED = 5;
    private byte[] mUusData;
    private int mUusDcs;
    private int mUusType;

    public UUSInfo() {
        this.mUusType = 0;
        this.mUusDcs = 4;
        this.mUusData = null;
    }

    public UUSInfo(int i, int i2, byte[] bArr) {
        this.mUusType = i;
        this.mUusDcs = i2;
        this.mUusData = bArr;
    }

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    public int getDcs() {
        return this.mUusDcs;
    }

    public void setDcs(int i) {
        this.mUusDcs = i;
    }

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    public int getType() {
        return this.mUusType;
    }

    public void setType(int i) {
        this.mUusType = i;
    }

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    public byte[] getUserData() {
        return this.mUusData;
    }

    public void setUserData(byte[] bArr) {
        this.mUusData = bArr;
    }
}
