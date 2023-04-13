package com.android.server.p011pm.pkg;

import android.content.pm.ComponentInfo;
import android.util.SparseArray;
import com.android.server.p011pm.parsing.pkg.AndroidPackageInternal;
import com.android.server.p011pm.pkg.component.ParsedMainComponent;
/* renamed from: com.android.server.pm.pkg.PackageStateUtils */
/* loaded from: classes2.dex */
public class PackageStateUtils {
    public static boolean isMatch(PackageState packageState, long j) {
        if ((j & 1048576) != 0) {
            return packageState.isSystem();
        }
        return true;
    }

    public static int[] queryInstalledUsers(PackageStateInternal packageStateInternal, int[] iArr, boolean z) {
        int i = 0;
        for (int i2 : iArr) {
            if (packageStateInternal.getUserStateOrDefault(i2).isInstalled() == z) {
                i++;
            }
        }
        int[] iArr2 = new int[i];
        int i3 = 0;
        for (int i4 : iArr) {
            if (packageStateInternal.getUserStateOrDefault(i4).isInstalled() == z) {
                iArr2[i3] = i4;
                i3++;
            }
        }
        return iArr2;
    }

    public static boolean isEnabledAndMatches(PackageStateInternal packageStateInternal, ComponentInfo componentInfo, long j, int i) {
        if (packageStateInternal == null) {
            return false;
        }
        return PackageUserStateUtils.isMatch(packageStateInternal.getUserStateOrDefault(i), componentInfo, j);
    }

    public static boolean isEnabledAndMatches(PackageStateInternal packageStateInternal, ParsedMainComponent parsedMainComponent, long j, int i) {
        AndroidPackageInternal pkg;
        if (packageStateInternal == null || (pkg = packageStateInternal.getPkg()) == null) {
            return false;
        }
        return PackageUserStateUtils.isMatch(packageStateInternal.getUserStateOrDefault(i), packageStateInternal.isSystem(), pkg.isEnabled(), parsedMainComponent, j);
    }

    public static long getEarliestFirstInstallTime(SparseArray<? extends PackageUserStateInternal> sparseArray) {
        if (sparseArray == null || sparseArray.size() == 0) {
            return 0L;
        }
        long j = Long.MAX_VALUE;
        for (int i = 0; i < sparseArray.size(); i++) {
            long firstInstallTimeMillis = sparseArray.valueAt(i).getFirstInstallTimeMillis();
            if (firstInstallTimeMillis != 0 && firstInstallTimeMillis < j) {
                j = firstInstallTimeMillis;
            }
        }
        if (j == Long.MAX_VALUE) {
            return 0L;
        }
        return j;
    }
}
