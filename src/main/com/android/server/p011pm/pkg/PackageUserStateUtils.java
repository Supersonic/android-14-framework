package com.android.server.p011pm.pkg;

import android.content.pm.ComponentInfo;
import com.android.server.p011pm.pkg.component.ParsedMainComponent;
/* renamed from: com.android.server.pm.pkg.PackageUserStateUtils */
/* loaded from: classes2.dex */
public class PackageUserStateUtils {
    public static boolean reportIfDebug(boolean z, long j) {
        return z;
    }

    public static boolean isMatch(PackageUserState packageUserState, ComponentInfo componentInfo, long j) {
        return isMatch(packageUserState, componentInfo.applicationInfo.isSystemApp(), componentInfo.applicationInfo.enabled, componentInfo.enabled, componentInfo.directBootAware, componentInfo.name, j);
    }

    public static boolean isMatch(PackageUserState packageUserState, boolean z, boolean z2, ParsedMainComponent parsedMainComponent, long j) {
        return isMatch(packageUserState, z, z2, parsedMainComponent.isEnabled(), parsedMainComponent.isDirectBootAware(), parsedMainComponent.getName(), j);
    }

    public static boolean isMatch(PackageUserState packageUserState, boolean z, boolean z2, boolean z3, boolean z4, String str, long j) {
        boolean z5 = true;
        boolean z6 = (4202496 & j) != 0;
        if (!isAvailable(packageUserState, j) && (!z || !z6)) {
            return reportIfDebug(false, j);
        }
        if (isEnabled(packageUserState, z2, z3, str, j)) {
            if ((1048576 & j) != 0 && !z) {
                return reportIfDebug(false, j);
            }
            boolean z7 = ((262144 & j) == 0 || z4) ? false : true;
            boolean z8 = (524288 & j) != 0 && z4;
            if (!z7 && !z8) {
                z5 = false;
            }
            return reportIfDebug(z5, j);
        }
        return reportIfDebug(false, j);
    }

    public static boolean isAvailable(PackageUserState packageUserState, long j) {
        boolean z = (4194304 & j) != 0;
        boolean z2 = (j & 8192) != 0;
        if (z) {
            return true;
        }
        return packageUserState.isInstalled() && (!packageUserState.isHidden() || z2);
    }

    public static boolean isEnabled(PackageUserState packageUserState, ComponentInfo componentInfo, long j) {
        return isEnabled(packageUserState, componentInfo.applicationInfo.enabled, componentInfo.enabled, componentInfo.name, j);
    }

    /* JADX WARN: Code restructure failed: missing block: B:15:0x0022, code lost:
        if ((r11 & 32768) == 0) goto L22;
     */
    /* JADX WARN: Removed duplicated region for block: B:21:0x002e A[RETURN] */
    /* JADX WARN: Removed duplicated region for block: B:22:0x002f  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public static boolean isEnabled(PackageUserState packageUserState, boolean z, boolean z2, String str, long j) {
        if ((512 & j) != 0) {
            return true;
        }
        int enabledState = packageUserState.getEnabledState();
        if (enabledState != 0) {
            if (enabledState != 2 && enabledState != 3) {
                if (enabledState == 4) {
                }
                if (packageUserState.isComponentEnabled(str)) {
                    if (packageUserState.isComponentDisabled(str)) {
                        return false;
                    }
                    return z2;
                }
                return true;
            }
            return false;
        }
        if (!z) {
            return false;
        }
        if (packageUserState.isComponentEnabled(str)) {
        }
    }

    public static boolean isPackageEnabled(PackageUserState packageUserState, AndroidPackage androidPackage) {
        int enabledState = packageUserState.getEnabledState();
        if (enabledState != 1) {
            if (enabledState == 2 || enabledState == 3 || enabledState == 4) {
                return false;
            }
            return androidPackage.isEnabled();
        }
        return true;
    }
}
