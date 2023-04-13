package com.android.server.p011pm;

import android.os.Build;
import android.os.SystemProperties;
/* renamed from: com.android.server.pm.SharedUidMigration */
/* loaded from: classes2.dex */
public final class SharedUidMigration {
    public static boolean isDisabled() {
        return false;
    }

    public static int getCurrentStrategy() {
        int i;
        if (Build.IS_USERDEBUG && (i = SystemProperties.getInt("persist.debug.pm.shared_uid_migration_strategy", 1)) <= 2 && i >= 1) {
            return i;
        }
        return 1;
    }

    public static boolean applyStrategy(int i) {
        return !isDisabled() && getCurrentStrategy() >= i;
    }
}
