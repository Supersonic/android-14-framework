package android.app.compat;

import android.annotation.SystemApi;
import android.compat.Compatibility;
import android.p008os.RemoteException;
import android.p008os.UserHandle;
import android.util.ArrayMap;
import com.android.internal.compat.CompatibilityOverrideConfig;
import com.android.internal.compat.CompatibilityOverridesByPackageConfig;
import com.android.internal.compat.CompatibilityOverridesToRemoveByPackageConfig;
import com.android.internal.compat.CompatibilityOverridesToRemoveConfig;
import java.util.Map;
import java.util.Set;
@SystemApi
/* loaded from: classes.dex */
public final class CompatChanges {
    private static final ChangeIdStateCache QUERY_CACHE = new ChangeIdStateCache();

    private CompatChanges() {
    }

    public static boolean isChangeEnabled(long changeId) {
        return Compatibility.isChangeEnabled(changeId);
    }

    public static boolean isChangeEnabled(long changeId, String packageName, UserHandle user) {
        return QUERY_CACHE.query(ChangeIdStateQuery.byPackageName(changeId, packageName, user.getIdentifier())).booleanValue();
    }

    public static boolean isChangeEnabled(long changeId, int uid) {
        return QUERY_CACHE.query(ChangeIdStateQuery.byUid(changeId, uid)).booleanValue();
    }

    public static void putAllPackageOverrides(Map<String, Map<Long, PackageOverride>> packageNameToOverrides) {
        ArrayMap<String, CompatibilityOverrideConfig> packageNameToConfig = new ArrayMap<>();
        for (String packageName : packageNameToOverrides.keySet()) {
            packageNameToConfig.put(packageName, new CompatibilityOverrideConfig(packageNameToOverrides.get(packageName)));
        }
        CompatibilityOverridesByPackageConfig config = new CompatibilityOverridesByPackageConfig(packageNameToConfig);
        try {
            QUERY_CACHE.getPlatformCompatService().putAllOverridesOnReleaseBuilds(config);
        } catch (RemoteException e) {
            e.rethrowFromSystemServer();
        }
    }

    public static void putPackageOverrides(String packageName, Map<Long, PackageOverride> overrides) {
        CompatibilityOverrideConfig config = new CompatibilityOverrideConfig(overrides);
        try {
            QUERY_CACHE.getPlatformCompatService().putOverridesOnReleaseBuilds(config, packageName);
        } catch (RemoteException e) {
            e.rethrowFromSystemServer();
        }
    }

    public static void removeAllPackageOverrides(Map<String, Set<Long>> packageNameToOverridesToRemove) {
        ArrayMap<String, CompatibilityOverridesToRemoveConfig> packageNameToConfig = new ArrayMap<>();
        for (String packageName : packageNameToOverridesToRemove.keySet()) {
            packageNameToConfig.put(packageName, new CompatibilityOverridesToRemoveConfig(packageNameToOverridesToRemove.get(packageName)));
        }
        CompatibilityOverridesToRemoveByPackageConfig config = new CompatibilityOverridesToRemoveByPackageConfig(packageNameToConfig);
        try {
            QUERY_CACHE.getPlatformCompatService().removeAllOverridesOnReleaseBuilds(config);
        } catch (RemoteException e) {
            e.rethrowFromSystemServer();
        }
    }

    public static void removePackageOverrides(String packageName, Set<Long> overridesToRemove) {
        CompatibilityOverridesToRemoveConfig config = new CompatibilityOverridesToRemoveConfig(overridesToRemove);
        try {
            QUERY_CACHE.getPlatformCompatService().removeOverridesOnReleaseBuilds(config, packageName);
        } catch (RemoteException e) {
            e.rethrowFromSystemServer();
        }
    }
}
