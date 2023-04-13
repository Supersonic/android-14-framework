package com.android.server.compat.overrides;

import android.app.compat.PackageOverride;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.util.ArrayMap;
import android.util.ArraySet;
import android.util.KeyValueListParser;
import android.util.Pair;
import android.util.Slog;
import com.android.internal.util.jobs.XmlUtils;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.regex.Pattern;
import libcore.util.HexEncoding;
/* loaded from: classes.dex */
public final class AppCompatOverridesParser {
    public static final Pattern BOOLEAN_PATTERN = Pattern.compile("true|false", 2);
    public final PackageManager mPackageManager;

    public AppCompatOverridesParser(PackageManager packageManager) {
        this.mPackageManager = packageManager;
    }

    public Map<String, Set<Long>> parseRemoveOverrides(String str, Set<Long> set) {
        String[] split;
        if (str.isEmpty()) {
            return Collections.emptyMap();
        }
        ArrayMap arrayMap = new ArrayMap();
        if (str.equals("*")) {
            if (set.isEmpty()) {
                Slog.w("AppCompatOverridesParser", "Wildcard can't be used in 'remove_overrides' flag with an empty owned_change_ids' flag");
                return Collections.emptyMap();
            }
            for (ApplicationInfo applicationInfo : this.mPackageManager.getInstalledApplications(4194304)) {
                arrayMap.put(applicationInfo.packageName, set);
            }
            return arrayMap;
        }
        KeyValueListParser keyValueListParser = new KeyValueListParser(',');
        try {
            keyValueListParser.setString(str);
            for (int i = 0; i < keyValueListParser.size(); i++) {
                String keyAt = keyValueListParser.keyAt(i);
                String string = keyValueListParser.getString(keyAt, "");
                if (string.equals("*")) {
                    if (set.isEmpty()) {
                        Slog.w("AppCompatOverridesParser", "Wildcard can't be used in 'remove_overrides' flag with an empty owned_change_ids' flag");
                    } else {
                        arrayMap.put(keyAt, set);
                    }
                } else {
                    for (String str2 : string.split(XmlUtils.STRING_ARRAY_SEPARATOR)) {
                        try {
                            ((Set) arrayMap.computeIfAbsent(keyAt, new Function() { // from class: com.android.server.compat.overrides.AppCompatOverridesParser$$ExternalSyntheticLambda0
                                @Override // java.util.function.Function
                                public final Object apply(Object obj) {
                                    Set lambda$parseRemoveOverrides$0;
                                    lambda$parseRemoveOverrides$0 = AppCompatOverridesParser.lambda$parseRemoveOverrides$0((String) obj);
                                    return lambda$parseRemoveOverrides$0;
                                }
                            })).add(Long.valueOf(Long.parseLong(str2)));
                        } catch (NumberFormatException e) {
                            Slog.w("AppCompatOverridesParser", "Invalid change ID in 'remove_overrides' flag: " + str2, e);
                        }
                    }
                }
            }
            return arrayMap;
        } catch (IllegalArgumentException e2) {
            Slog.w("AppCompatOverridesParser", "Invalid format in 'remove_overrides' flag: " + str, e2);
            return Collections.emptyMap();
        }
    }

    public static /* synthetic */ Set lambda$parseRemoveOverrides$0(String str) {
        return new ArraySet();
    }

    public static Set<Long> parseOwnedChangeIds(String str) {
        String[] split;
        if (str.isEmpty()) {
            return Collections.emptySet();
        }
        ArraySet arraySet = new ArraySet();
        for (String str2 : str.split(",")) {
            try {
                arraySet.add(Long.valueOf(Long.parseLong(str2)));
            } catch (NumberFormatException e) {
                Slog.w("AppCompatOverridesParser", "Invalid change ID in 'owned_change_ids' flag: " + str2, e);
            }
        }
        return arraySet;
    }

    public Map<Long, PackageOverride> parsePackageOverrides(String str, String str2, long j, Set<Long> set) {
        String[] split;
        if (str.isEmpty()) {
            return Collections.emptyMap();
        }
        PackageOverrideComparator packageOverrideComparator = new PackageOverrideComparator(j);
        ArrayMap arrayMap = new ArrayMap();
        Pair<String, String> extractSignatureFromConfig = extractSignatureFromConfig(str);
        if (extractSignatureFromConfig == null) {
            return Collections.emptyMap();
        }
        String str3 = (String) extractSignatureFromConfig.second;
        if (!verifySignature(str2, (String) extractSignatureFromConfig.first)) {
            return Collections.emptyMap();
        }
        for (String str4 : str3.split(",")) {
            List asList = Arrays.asList(str4.split(XmlUtils.STRING_ARRAY_SEPARATOR, 4));
            if (asList.size() != 4) {
                Slog.w("AppCompatOverridesParser", "Invalid change override entry: " + str4);
            } else {
                try {
                    long parseLong = Long.parseLong((String) asList.get(0));
                    if (!set.contains(Long.valueOf(parseLong))) {
                        String str5 = (String) asList.get(1);
                        String str6 = (String) asList.get(2);
                        String str7 = (String) asList.get(3);
                        if (BOOLEAN_PATTERN.matcher(str7).matches()) {
                            PackageOverride.Builder enabled = new PackageOverride.Builder().setEnabled(Boolean.parseBoolean(str7));
                            try {
                                if (!str5.isEmpty()) {
                                    enabled.setMinVersionCode(Long.parseLong(str5));
                                }
                                if (!str6.isEmpty()) {
                                    enabled.setMaxVersionCode(Long.parseLong(str6));
                                }
                                try {
                                    PackageOverride build = enabled.build();
                                    if (!arrayMap.containsKey(Long.valueOf(parseLong)) || packageOverrideComparator.compare(build, (PackageOverride) arrayMap.get(Long.valueOf(parseLong))) < 0) {
                                        arrayMap.put(Long.valueOf(parseLong), build);
                                    }
                                } catch (IllegalArgumentException e) {
                                    Slog.w("AppCompatOverridesParser", "Failed to build PackageOverride", e);
                                }
                            } catch (NumberFormatException e2) {
                                Slog.w("AppCompatOverridesParser", "Invalid min/max version code in override entry: " + str4, e2);
                            }
                        } else {
                            Slog.w("AppCompatOverridesParser", "Invalid enabled string in override entry: " + str4);
                        }
                    }
                } catch (NumberFormatException e3) {
                    Slog.w("AppCompatOverridesParser", "Invalid change ID in override entry: " + str4, e3);
                }
            }
        }
        return arrayMap;
    }

    public static Pair<String, String> extractSignatureFromConfig(String str) {
        List asList = Arrays.asList(str.split("~"));
        if (asList.size() == 1) {
            return Pair.create("", str);
        }
        if (asList.size() > 2) {
            Slog.w("AppCompatOverridesParser", "Only one signature per config is supported. Config: " + str);
            return null;
        }
        return Pair.create((String) asList.get(0), (String) asList.get(1));
    }

    /* JADX WARN: Code restructure failed: missing block: B:10:0x001a, code lost:
        android.util.Slog.w("AppCompatOverridesParser", r5 + " did not have expected signature: " + r6);
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public final boolean verifySignature(String str, String str2) {
        try {
            boolean z = true;
            if (!str2.isEmpty() && !this.mPackageManager.hasSigningCertificate(str, HexEncoding.decode(str2), 1)) {
                z = false;
            }
            return z;
        } catch (IllegalArgumentException e) {
            Slog.w("AppCompatOverridesParser", "Unable to verify signature " + str2 + " for " + str, e);
            return false;
        }
    }

    /* loaded from: classes.dex */
    public static final class PackageOverrideComparator implements Comparator<PackageOverride> {
        public final long mVersionCode;

        public PackageOverrideComparator(long j) {
            this.mVersionCode = j;
        }

        @Override // java.util.Comparator
        public int compare(PackageOverride packageOverride, PackageOverride packageOverride2) {
            boolean isVersionInRange = isVersionInRange(packageOverride, this.mVersionCode);
            if (isVersionInRange != isVersionInRange(packageOverride2, this.mVersionCode)) {
                return isVersionInRange ? -1 : 1;
            }
            boolean isVersionAfterRange = isVersionAfterRange(packageOverride, this.mVersionCode);
            if (isVersionAfterRange != isVersionAfterRange(packageOverride2, this.mVersionCode)) {
                return isVersionAfterRange ? -1 : 1;
            }
            return Long.compare(getVersionProximity(packageOverride, this.mVersionCode), getVersionProximity(packageOverride2, this.mVersionCode));
        }

        public static boolean isVersionInRange(PackageOverride packageOverride, long j) {
            return packageOverride.getMinVersionCode() <= j && j <= packageOverride.getMaxVersionCode();
        }

        public static boolean isVersionAfterRange(PackageOverride packageOverride, long j) {
            return packageOverride.getMaxVersionCode() < j;
        }

        public static boolean isVersionBeforeRange(PackageOverride packageOverride, long j) {
            return packageOverride.getMinVersionCode() > j;
        }

        public static long getVersionProximity(PackageOverride packageOverride, long j) {
            if (isVersionAfterRange(packageOverride, j)) {
                return j - packageOverride.getMaxVersionCode();
            }
            if (isVersionBeforeRange(packageOverride, j)) {
                return packageOverride.getMinVersionCode() - j;
            }
            return 0L;
        }
    }
}
