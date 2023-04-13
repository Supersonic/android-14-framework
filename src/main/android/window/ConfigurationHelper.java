package android.window;

import android.app.ResourcesManager;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.p008os.IBinder;
/* loaded from: classes4.dex */
public class ConfigurationHelper {
    private ConfigurationHelper() {
    }

    public static void freeTextLayoutCachesIfNeeded(int configDiff) {
        if ((configDiff & 4) != 0) {
            Canvas.freeTextLayoutCaches();
        }
    }

    public static boolean shouldUpdateResources(IBinder token, Configuration config, Configuration newConfig, Configuration overrideConfig, boolean displayChanged, Boolean configChanged) {
        if (config == null || displayChanged || !ResourcesManager.getInstance().isSameResourcesOverrideConfig(token, overrideConfig) || shouldUpdateWindowMetricsBounds(config, newConfig) || isDisplayRotationChanged(config, newConfig)) {
            return true;
        }
        return configChanged == null ? config.diff(newConfig) != 0 : configChanged.booleanValue();
    }

    public static boolean isDifferentDisplay(int displayId, int newDisplayId) {
        return (newDisplayId == -1 || displayId == newDisplayId) ? false : true;
    }

    private static boolean shouldUpdateWindowMetricsBounds(Configuration currentConfig, Configuration newConfig) {
        Rect currentBounds = currentConfig.windowConfiguration.getBounds();
        Rect newBounds = newConfig.windowConfiguration.getBounds();
        Rect currentMaxBounds = currentConfig.windowConfiguration.getMaxBounds();
        Rect newMaxBounds = newConfig.windowConfiguration.getMaxBounds();
        return (currentBounds.equals(newBounds) && currentMaxBounds.equals(newMaxBounds)) ? false : true;
    }

    private static boolean isDisplayRotationChanged(Configuration config, Configuration newConfig) {
        int origRot = config.windowConfiguration.getDisplayRotation();
        int newRot = newConfig.windowConfiguration.getDisplayRotation();
        return (newRot == -1 || origRot == -1 || origRot == newRot) ? false : true;
    }
}
