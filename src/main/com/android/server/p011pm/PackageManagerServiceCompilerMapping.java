package com.android.server.p011pm;

import android.os.SystemProperties;
import dalvik.system.DexFile;
/* renamed from: com.android.server.pm.PackageManagerServiceCompilerMapping */
/* loaded from: classes2.dex */
public class PackageManagerServiceCompilerMapping {
    public static final int REASON_SHARED_INDEX;
    public static final String[] REASON_STRINGS;

    static {
        String[] strArr = {"first-boot", "boot-after-ota", "post-boot", "install", "install-fast", "install-bulk", "install-bulk-secondary", "install-bulk-downgraded", "install-bulk-secondary-downgraded", "bg-dexopt", "ab-ota", "inactive", "cmdline", "boot-after-mainline-update", "shared"};
        REASON_STRINGS = strArr;
        int length = strArr.length - 1;
        REASON_SHARED_INDEX = length;
        if (15 != strArr.length) {
            throw new IllegalStateException("REASON_STRINGS not correct");
        }
        if (!"shared".equals(strArr[length])) {
            throw new IllegalStateException("REASON_STRINGS not correct because of shared index");
        }
    }

    public static String getSystemPropertyName(int i) {
        if (i >= 0) {
            String[] strArr = REASON_STRINGS;
            if (i < strArr.length) {
                return "pm.dexopt." + strArr[i];
            }
        }
        throw new IllegalArgumentException("reason " + i + " invalid");
    }

    public static String getAndCheckValidity(int i) {
        String str = SystemProperties.get(getSystemPropertyName(i));
        if (str == null || str.isEmpty() || (!str.equals("skip") && !DexFile.isValidCompilerFilter(str))) {
            throw new IllegalStateException("Value \"" + str + "\" not valid (reason " + REASON_STRINGS[i] + ")");
        } else if (isFilterAllowedForReason(i, str)) {
            return str;
        } else {
            throw new IllegalStateException("Value \"" + str + "\" not allowed (reason " + REASON_STRINGS[i] + ")");
        }
    }

    public static boolean isFilterAllowedForReason(int i, String str) {
        return (i == REASON_SHARED_INDEX && DexFile.isProfileGuidedCompilerFilter(str)) ? false : true;
    }

    public static void checkProperties() {
        String systemPropertyName;
        IllegalStateException illegalStateException = null;
        for (int i = 0; i <= 14; i++) {
            try {
                systemPropertyName = getSystemPropertyName(i);
            } catch (Exception e) {
                if (illegalStateException == null) {
                    illegalStateException = new IllegalStateException("PMS compiler filter settings are bad.");
                }
                illegalStateException.addSuppressed(e);
            }
            if (systemPropertyName == null || systemPropertyName.isEmpty()) {
                throw new IllegalStateException("Reason system property name \"" + systemPropertyName + "\" for reason " + REASON_STRINGS[i]);
                break;
            }
            getAndCheckValidity(i);
        }
        if (illegalStateException != null) {
            throw illegalStateException;
        }
    }

    public static String getCompilerFilterForReason(int i) {
        return getAndCheckValidity(i);
    }

    public static String getDefaultCompilerFilter() {
        String str = SystemProperties.get("dalvik.vm.dex2oat-filter");
        return (str == null || str.isEmpty() || !DexFile.isValidCompilerFilter(str) || DexFile.isProfileGuidedCompilerFilter(str)) ? "speed" : str;
    }

    public static String getReasonName(int i) {
        if (i >= 0) {
            String[] strArr = REASON_STRINGS;
            if (i < strArr.length) {
                return strArr[i];
            }
        }
        throw new IllegalArgumentException("reason " + i + " invalid");
    }
}
