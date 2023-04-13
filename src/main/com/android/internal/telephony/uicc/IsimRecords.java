package com.android.internal.telephony.uicc;

import android.compat.annotation.UnsupportedAppUsage;
/* loaded from: classes.dex */
public interface IsimRecords {
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    String getIsimDomain();

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    String getIsimImpi();

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    String[] getIsimImpu();

    String getIsimIst();

    String[] getIsimPcscf();
}
