package android.companion.utils;

import android.p008os.Build;
import android.provider.DeviceConfig;
/* loaded from: classes.dex */
public final class FeatureUtils {
    private static final String NAMESPACE_COMPANION = "companion";
    private static final String PROPERTY_PERM_SYNC_ENABLED = "perm_sync_enabled";

    public static boolean isPermSyncEnabled() {
        return Build.isDebuggable() || DeviceConfig.getBoolean(NAMESPACE_COMPANION, PROPERTY_PERM_SYNC_ENABLED, false);
    }

    private FeatureUtils() {
    }
}
