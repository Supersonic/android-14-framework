package com.android.server.hdmi;

import android.provider.DeviceConfig;
import java.util.concurrent.Executor;
/* loaded from: classes.dex */
public class DeviceConfigWrapper {
    public boolean getBoolean(String str, boolean z) {
        return DeviceConfig.getBoolean("hdmi_control", str, z);
    }

    public void addOnPropertiesChangedListener(Executor executor, DeviceConfig.OnPropertiesChangedListener onPropertiesChangedListener) {
        DeviceConfig.addOnPropertiesChangedListener("hdmi_control", executor, onPropertiesChangedListener);
    }
}
