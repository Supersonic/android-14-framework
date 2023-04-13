package com.android.server.accessibility.magnification;

import android.provider.DeviceConfig;
import com.android.internal.annotations.VisibleForTesting;
import java.util.concurrent.Executor;
/* loaded from: classes.dex */
public class AlwaysOnMagnificationFeatureFlag {
    public static boolean isAlwaysOnMagnificationEnabled() {
        return DeviceConfig.getBoolean("window_manager", "AlwaysOnMagnifier__enable_always_on_magnifier", false);
    }

    @VisibleForTesting
    public static boolean setAlwaysOnMagnificationEnabled(boolean z) {
        return DeviceConfig.setProperty("window_manager", "AlwaysOnMagnifier__enable_always_on_magnifier", Boolean.toString(z), false);
    }

    public static DeviceConfig.OnPropertiesChangedListener addOnChangedListener(Executor executor, final Runnable runnable) {
        DeviceConfig.OnPropertiesChangedListener onPropertiesChangedListener = new DeviceConfig.OnPropertiesChangedListener() { // from class: com.android.server.accessibility.magnification.AlwaysOnMagnificationFeatureFlag$$ExternalSyntheticLambda0
            public final void onPropertiesChanged(DeviceConfig.Properties properties) {
                AlwaysOnMagnificationFeatureFlag.lambda$addOnChangedListener$0(runnable, properties);
            }
        };
        DeviceConfig.addOnPropertiesChangedListener("window_manager", executor, onPropertiesChangedListener);
        return onPropertiesChangedListener;
    }

    public static /* synthetic */ void lambda$addOnChangedListener$0(Runnable runnable, DeviceConfig.Properties properties) {
        if (properties.getKeyset().contains("AlwaysOnMagnifier__enable_always_on_magnifier")) {
            runnable.run();
        }
    }
}
