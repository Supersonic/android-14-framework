package com.android.internal.telephony.build;

import android.os.Build;
import androidx.annotation.ChecksSdkIntAtLeast;
import androidx.annotation.NonNull;
/* loaded from: classes.dex */
public final class SdkLevel {
    @ChecksSdkIntAtLeast(api = 30)
    public static boolean isAtLeastR() {
        return true;
    }

    @ChecksSdkIntAtLeast(api = 31)
    public static boolean isAtLeastS() {
        return true;
    }

    @ChecksSdkIntAtLeast(api = 32)
    public static boolean isAtLeastSv2() {
        return true;
    }

    @ChecksSdkIntAtLeast(api = 33)
    public static boolean isAtLeastT() {
        return true;
    }

    private SdkLevel() {
    }

    @ChecksSdkIntAtLeast(codename = "UpsideDownCake")
    public static boolean isAtLeastU() {
        return isAtLeastPreReleaseCodename("UpsideDownCake");
    }

    private static boolean isAtLeastPreReleaseCodename(@NonNull String str) {
        String str2 = Build.VERSION.CODENAME;
        return !"REL".equals(str2) && str2.compareTo(str) >= 0;
    }
}
