package com.android.server.p014wm;

import android.app.compat.CompatChanges;
import android.content.pm.PackageManager;
import android.provider.DeviceConfig;
import com.android.internal.annotations.GuardedBy;
import java.util.HashSet;
import java.util.concurrent.Executor;
/* renamed from: com.android.server.wm.ActivitySecurityModelFeatureFlags */
/* loaded from: classes2.dex */
public class ActivitySecurityModelFeatureFlags {
    public static int sAsmRestrictionsEnabled;
    public static int sAsmToastsEnabled;
    public static final HashSet<String> sExcludedPackageNames = new HashSet<>();
    public static PackageManager sPm;

    @GuardedBy({"ActivityTaskManagerService.mGlobalLock"})
    public static void initialize(Executor executor, PackageManager packageManager) {
        updateFromDeviceConfig();
        DeviceConfig.addOnPropertiesChangedListener("window_manager", executor, new DeviceConfig.OnPropertiesChangedListener() { // from class: com.android.server.wm.ActivitySecurityModelFeatureFlags$$ExternalSyntheticLambda0
            public final void onPropertiesChanged(DeviceConfig.Properties properties) {
                ActivitySecurityModelFeatureFlags.updateFromDeviceConfig();
            }
        });
        sPm = packageManager;
    }

    @GuardedBy({"ActivityTaskManagerService.mGlobalLock"})
    public static boolean shouldShowToast(int i) {
        return flagEnabledForUid(sAsmToastsEnabled, i);
    }

    @GuardedBy({"ActivityTaskManagerService.mGlobalLock"})
    public static boolean shouldRestrictActivitySwitch(int i) {
        return flagEnabledForUid(sAsmRestrictionsEnabled, i);
    }

    public static boolean flagEnabledForUid(int i, int i2) {
        if (i == 2 || (i == 1 && CompatChanges.isChangeEnabled(230590090L, i2))) {
            String[] packagesForUid = sPm.getPackagesForUid(i2);
            if (packagesForUid == null) {
                return true;
            }
            for (String str : packagesForUid) {
                if (sExcludedPackageNames.contains(str)) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    public static void updateFromDeviceConfig() {
        sAsmToastsEnabled = DeviceConfig.getInt("window_manager", "ActivitySecurity__asm_toasts_enabled", 0);
        sAsmRestrictionsEnabled = DeviceConfig.getInt("window_manager", "ActivitySecurity__asm_restrictions_enabled", 0);
        String string = DeviceConfig.getString("window_manager", "ActivitySecurity__asm_exempted_packages", "");
        sExcludedPackageNames.clear();
        for (String str : string.split(",")) {
            String trim = str.trim();
            if (!trim.isEmpty()) {
                sExcludedPackageNames.add(trim);
            }
        }
    }
}
