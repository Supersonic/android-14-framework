package android.app;

import android.content.p001pm.IPackageManager;
import android.permission.IPermissionManager;
/* loaded from: classes.dex */
public class AppGlobals {
    public static Application getInitialApplication() {
        return ActivityThread.currentApplication();
    }

    public static String getInitialPackage() {
        return ActivityThread.currentPackageName();
    }

    public static IPackageManager getPackageManager() {
        return ActivityThread.getPackageManager();
    }

    public static IPermissionManager getPermissionManager() {
        return ActivityThread.getPermissionManager();
    }

    public static int getIntCoreSetting(String key, int defaultValue) {
        ActivityThread currentActivityThread = ActivityThread.currentActivityThread();
        if (currentActivityThread != null) {
            return currentActivityThread.getIntCoreSetting(key, defaultValue);
        }
        return defaultValue;
    }

    public static float getFloatCoreSetting(String key, float defaultValue) {
        ActivityThread currentActivityThread = ActivityThread.currentActivityThread();
        if (currentActivityThread != null) {
            return currentActivityThread.getFloatCoreSetting(key, defaultValue);
        }
        return defaultValue;
    }
}
