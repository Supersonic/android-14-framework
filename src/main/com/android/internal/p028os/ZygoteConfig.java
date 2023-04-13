package com.android.internal.p028os;

import android.media.MediaMetrics;
import android.p008os.SystemProperties;
/* renamed from: com.android.internal.os.ZygoteConfig */
/* loaded from: classes4.dex */
public class ZygoteConfig {
    public static final String PROPERTY_PREFIX_DEVICE_CONFIG = "persist.device_config";
    public static final String PROPERTY_PREFIX_SYSTEM = "dalvik.vm.";
    public static final String USAP_POOL_ENABLED = "usap_pool_enabled";
    public static final boolean USAP_POOL_ENABLED_DEFAULT = false;
    public static final String USAP_POOL_REFILL_DELAY_MS = "usap_pool_refill_delay_ms";
    public static final int USAP_POOL_REFILL_DELAY_MS_DEFAULT = 3000;
    public static final String USAP_POOL_REFILL_THRESHOLD = "usap_refill_threshold";
    public static final int USAP_POOL_REFILL_THRESHOLD_DEFAULT = 1;
    public static final String USAP_POOL_SIZE_MAX = "usap_pool_size_max";
    public static final int USAP_POOL_SIZE_MAX_DEFAULT = 3;
    public static final int USAP_POOL_SIZE_MAX_LIMIT = 100;
    public static final String USAP_POOL_SIZE_MIN = "usap_pool_size_min";
    public static final int USAP_POOL_SIZE_MIN_DEFAULT = 1;
    public static final int USAP_POOL_SIZE_MIN_LIMIT = 1;

    private static String getDeviceConfig(String name) {
        return SystemProperties.get(String.join(MediaMetrics.SEPARATOR, PROPERTY_PREFIX_DEVICE_CONFIG, "runtime_native", name));
    }

    public static int getInt(String name, int defaultValue) {
        String propString = getDeviceConfig(name);
        if (!propString.isEmpty()) {
            return Integer.parseInt(propString);
        }
        return SystemProperties.getInt(PROPERTY_PREFIX_SYSTEM + name, defaultValue);
    }

    public static boolean getBool(String name, boolean defaultValue) {
        String propString = getDeviceConfig(name);
        if (!propString.isEmpty()) {
            return Boolean.parseBoolean(propString);
        }
        return SystemProperties.getBoolean(PROPERTY_PREFIX_SYSTEM + name, defaultValue);
    }
}
