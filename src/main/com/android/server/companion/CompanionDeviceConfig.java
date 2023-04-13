package com.android.server.companion;

import android.provider.DeviceConfig;
/* loaded from: classes.dex */
public class CompanionDeviceConfig {
    public static boolean isEnabled(String str) {
        return DeviceConfig.getBoolean("companion", str, true);
    }
}
