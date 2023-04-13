package com.android.internal.telephony.cat;

import android.compat.annotation.UnsupportedAppUsage;
import com.android.telephony.Rlog;
/* loaded from: classes.dex */
public abstract class CatLog {
    @UnsupportedAppUsage
    /* renamed from: d */
    public static void m5d(Object obj, String str) {
        String name = obj.getClass().getName();
        Rlog.d("CAT", name.substring(name.lastIndexOf(46) + 1) + ": " + str);
    }

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    /* renamed from: d */
    public static void m4d(String str, String str2) {
        Rlog.d("CAT", str + ": " + str2);
    }

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    /* renamed from: e */
    public static void m3e(Object obj, String str) {
        String name = obj.getClass().getName();
        Rlog.e("CAT", name.substring(name.lastIndexOf(46) + 1) + ": " + str);
    }

    /* renamed from: e */
    public static void m2e(String str, String str2) {
        Rlog.e("CAT", str + ": " + str2);
    }
}
