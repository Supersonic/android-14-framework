package com.android.modules.utils.build;

import androidx.annotation.ChecksSdkIntAtLeast;
/* loaded from: classes.dex */
public final class SdkLevel {
    @ChecksSdkIntAtLeast(api = 31)
    public static boolean isAtLeastS() {
        return true;
    }

    @ChecksSdkIntAtLeast(api = 33)
    public static boolean isAtLeastT() {
        return true;
    }
}
