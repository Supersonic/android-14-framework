package android.content.p001pm;

import android.provider.DeviceConfig;
import android.util.ArrayMap;
import android.util.Pair;
import android.util.Slog;
import com.android.internal.p028os.BackgroundThread;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
/* renamed from: android.content.pm.ConstrainDisplayApisConfig */
/* loaded from: classes.dex */
public final class ConstrainDisplayApisConfig {
    private static final String FLAG_ALWAYS_CONSTRAIN_DISPLAY_APIS = "always_constrain_display_apis";
    private static final String FLAG_NEVER_CONSTRAIN_DISPLAY_APIS = "never_constrain_display_apis";
    private static final String FLAG_NEVER_CONSTRAIN_DISPLAY_APIS_ALL_PACKAGES = "never_constrain_display_apis_all_packages";
    private static final String TAG = ConstrainDisplayApisConfig.class.getSimpleName();
    private ArrayMap<String, Pair<Long, Long>> mAlwaysConstrainConfigMap;
    private ArrayMap<String, Pair<Long, Long>> mNeverConstrainConfigMap;
    private boolean mNeverConstrainDisplayApisAllPackages;

    public ConstrainDisplayApisConfig() {
        updateCache();
        DeviceConfig.addOnPropertiesChangedListener("constrain_display_apis", BackgroundThread.getExecutor(), new DeviceConfig.OnPropertiesChangedListener() { // from class: android.content.pm.ConstrainDisplayApisConfig$$ExternalSyntheticLambda0
            public final void onPropertiesChanged(DeviceConfig.Properties properties) {
                ConstrainDisplayApisConfig.this.lambda$new$0(properties);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0(DeviceConfig.Properties properties) {
        updateCache();
    }

    public boolean getNeverConstrainDisplayApis(ApplicationInfo applicationInfo) {
        if (this.mNeverConstrainDisplayApisAllPackages) {
            return true;
        }
        return flagHasMatchingPackageEntry(this.mNeverConstrainConfigMap, applicationInfo);
    }

    public boolean getAlwaysConstrainDisplayApis(ApplicationInfo applicationInfo) {
        return flagHasMatchingPackageEntry(this.mAlwaysConstrainConfigMap, applicationInfo);
    }

    private void updateCache() {
        this.mNeverConstrainDisplayApisAllPackages = DeviceConfig.getBoolean("constrain_display_apis", FLAG_NEVER_CONSTRAIN_DISPLAY_APIS_ALL_PACKAGES, false);
        String neverConstrainConfigStr = DeviceConfig.getString("constrain_display_apis", FLAG_NEVER_CONSTRAIN_DISPLAY_APIS, "");
        this.mNeverConstrainConfigMap = buildConfigMap(neverConstrainConfigStr);
        String alwaysConstrainConfigStr = DeviceConfig.getString("constrain_display_apis", FLAG_ALWAYS_CONSTRAIN_DISPLAY_APIS, "");
        this.mAlwaysConstrainConfigMap = buildConfigMap(alwaysConstrainConfigStr);
    }

    private static ArrayMap<String, Pair<Long, Long>> buildConfigMap(String configStr) {
        ArrayMap<String, Pair<Long, Long>> configMap = new ArrayMap<>();
        if (configStr.isEmpty()) {
            return configMap;
        }
        String[] split = configStr.split(",");
        int length = split.length;
        int i = 0;
        int i2 = 0;
        while (i2 < length) {
            String packageEntryString = split[i2];
            List<String> packageAndVersions = Arrays.asList(packageEntryString.split(":", 3));
            if (packageAndVersions.size() != 3) {
                Slog.m90w(TAG, "Invalid package entry in flag 'never/always_constrain_display_apis': " + packageEntryString);
            } else {
                String packageName = packageAndVersions.get(i);
                String minVersionCodeStr = packageAndVersions.get(1);
                String maxVersionCodeStr = packageAndVersions.get(2);
                try {
                    long minVersion = minVersionCodeStr.isEmpty() ? Long.MIN_VALUE : Long.parseLong(minVersionCodeStr);
                    long maxVersion = maxVersionCodeStr.isEmpty() ? Long.MAX_VALUE : Long.parseLong(maxVersionCodeStr);
                    Pair<Long, Long> minMaxVersionCodes = new Pair<>(Long.valueOf(minVersion), Long.valueOf(maxVersion));
                    configMap.put(packageName, minMaxVersionCodes);
                } catch (NumberFormatException e) {
                    Slog.m90w(TAG, "Invalid APK version code in package entry: " + packageEntryString);
                }
            }
            i2++;
            i = 0;
        }
        return configMap;
    }

    private static boolean flagHasMatchingPackageEntry(Map<String, Pair<Long, Long>> configMap, ApplicationInfo applicationInfo) {
        if (!configMap.isEmpty() && configMap.containsKey(applicationInfo.packageName)) {
            return matchesApplicationInfo(configMap.get(applicationInfo.packageName), applicationInfo);
        }
        return false;
    }

    private static boolean matchesApplicationInfo(Pair<Long, Long> minMaxVersionCodes, ApplicationInfo applicationInfo) {
        return applicationInfo.longVersionCode >= minMaxVersionCodes.first.longValue() && applicationInfo.longVersionCode <= minMaxVersionCodes.second.longValue();
    }
}
