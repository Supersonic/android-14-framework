package com.android.server.tare;

import android.app.AppGlobals;
import android.content.Context;
import android.content.PermissionChecker;
import android.content.pm.ApplicationInfo;
import android.content.pm.InstallSourceInfo;
import android.content.pm.PackageInfo;
import android.os.RemoteException;
import com.android.internal.util.jobs.ArrayUtils;
/* loaded from: classes2.dex */
public class InstalledPackageInfo {
    public final boolean hasCode;
    public final String installerPackageName;
    public final boolean isSystemInstaller;
    public final String packageName;
    public final int uid;

    public InstalledPackageInfo(Context context, PackageInfo packageInfo) {
        InstallSourceInfo installSourceInfo;
        ApplicationInfo applicationInfo = packageInfo.applicationInfo;
        this.uid = applicationInfo == null ? -1 : applicationInfo.uid;
        String str = packageInfo.packageName;
        this.packageName = str;
        boolean z = true;
        this.hasCode = applicationInfo != null && applicationInfo.hasCode();
        this.isSystemInstaller = (applicationInfo == null || ArrayUtils.indexOf(packageInfo.requestedPermissions, "android.permission.INSTALL_PACKAGES") < 0 || PermissionChecker.checkPermissionForPreflight(context, "android.permission.INSTALL_PACKAGES", -1, applicationInfo.uid, str) != 0) ? false : z;
        try {
            installSourceInfo = AppGlobals.getPackageManager().getInstallSourceInfo(str);
        } catch (RemoteException unused) {
            installSourceInfo = null;
        }
        this.installerPackageName = installSourceInfo != null ? installSourceInfo.getInstallingPackageName() : null;
    }
}
