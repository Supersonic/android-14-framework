package com.android.server.notification;

import android.content.pm.IPackageManager;
import android.content.pm.PackageInfo;
import android.content.pm.ParceledListSlice;
import android.os.Binder;
import android.os.RemoteException;
import android.permission.IPermissionManager;
import android.util.ArrayMap;
import android.util.Pair;
import android.util.Slog;
import com.android.internal.util.ArrayUtils;
import com.android.server.p011pm.permission.PermissionManagerServiceInternal;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
/* loaded from: classes2.dex */
public final class PermissionHelper {
    public final IPackageManager mPackageManager;
    public final IPermissionManager mPermManager;
    public final PermissionManagerServiceInternal mPmi;

    public PermissionHelper(PermissionManagerServiceInternal permissionManagerServiceInternal, IPackageManager iPackageManager, IPermissionManager iPermissionManager) {
        this.mPmi = permissionManagerServiceInternal;
        this.mPackageManager = iPackageManager;
        this.mPermManager = iPermissionManager;
    }

    public boolean hasPermission(int i) {
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            return this.mPmi.checkUidPermission(i, "android.permission.POST_NOTIFICATIONS") == 0;
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public Set<Pair<Integer, String>> getAppsRequestingPermission(int i) {
        HashSet hashSet = new HashSet();
        for (PackageInfo packageInfo : getInstalledPackages(i)) {
            String[] strArr = packageInfo.requestedPermissions;
            if (strArr != null) {
                int length = strArr.length;
                int i2 = 0;
                while (true) {
                    if (i2 >= length) {
                        break;
                    } else if ("android.permission.POST_NOTIFICATIONS".equals(strArr[i2])) {
                        hashSet.add(new Pair(Integer.valueOf(packageInfo.applicationInfo.uid), packageInfo.packageName));
                        break;
                    } else {
                        i2++;
                    }
                }
            }
        }
        return hashSet;
    }

    public final List<PackageInfo> getInstalledPackages(int i) {
        ParceledListSlice parceledListSlice;
        try {
            parceledListSlice = this.mPackageManager.getInstalledPackages(4096L, i);
        } catch (RemoteException e) {
            Slog.d("PermissionHelper", "Could not reach system server", e);
            parceledListSlice = null;
        }
        if (parceledListSlice == null) {
            return Collections.emptyList();
        }
        return parceledListSlice.getList();
    }

    public Set<Pair<Integer, String>> getAppsGrantedPermission(int i) {
        ParceledListSlice parceledListSlice;
        HashSet hashSet = new HashSet();
        try {
            parceledListSlice = this.mPackageManager.getPackagesHoldingPermissions(new String[]{"android.permission.POST_NOTIFICATIONS"}, 0L, i);
        } catch (RemoteException e) {
            Slog.e("PermissionHelper", "Could not reach system server", e);
            parceledListSlice = null;
        }
        if (parceledListSlice == null) {
            return hashSet;
        }
        for (PackageInfo packageInfo : parceledListSlice.getList()) {
            hashSet.add(new Pair(Integer.valueOf(packageInfo.applicationInfo.uid), packageInfo.packageName));
        }
        return hashSet;
    }

    public ArrayMap<Pair<Integer, String>, Pair<Boolean, Boolean>> getNotificationPermissionValues(int i) {
        ArrayMap<Pair<Integer, String>, Pair<Boolean, Boolean>> arrayMap = new ArrayMap<>();
        Set<Pair<Integer, String>> appsRequestingPermission = getAppsRequestingPermission(i);
        Set<Pair<Integer, String>> appsGrantedPermission = getAppsGrantedPermission(i);
        for (Pair<Integer, String> pair : appsRequestingPermission) {
            arrayMap.put(pair, new Pair<>(Boolean.valueOf(appsGrantedPermission.contains(pair)), Boolean.valueOf(isPermissionUserSet((String) pair.second, i))));
        }
        return arrayMap;
    }

    /* JADX WARN: Removed duplicated region for block: B:28:0x0048 A[Catch: all -> 0x0064, RemoteException -> 0x0066, TryCatch #1 {RemoteException -> 0x0066, blocks: (B:3:0x0008, B:5:0x000e, B:7:0x0014, B:11:0x001d, B:17:0x002d, B:28:0x0048, B:29:0x0054, B:20:0x0037), top: B:41:0x0008, outer: #0 }] */
    /* JADX WARN: Removed duplicated region for block: B:29:0x0054 A[Catch: all -> 0x0064, RemoteException -> 0x0066, TRY_LEAVE, TryCatch #1 {RemoteException -> 0x0066, blocks: (B:3:0x0008, B:5:0x000e, B:7:0x0014, B:11:0x001d, B:17:0x002d, B:28:0x0048, B:29:0x0054, B:20:0x0037), top: B:41:0x0008, outer: #0 }] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public void setNotificationPermission(String str, int i, boolean z, boolean z2) {
        int i2;
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            try {
            } catch (RemoteException e) {
                Slog.e("PermissionHelper", "Could not reach system server", e);
            }
            if (packageRequestsNotificationPermission(str, i) && !isPermissionFixed(str, i) && (!isPermissionGrantedByDefaultOrRole(str, i) || z2)) {
                boolean z3 = this.mPmi.checkPermission(str, "android.permission.POST_NOTIFICATIONS", i) != -1;
                if (z && !z3) {
                    this.mPermManager.grantRuntimePermission(str, "android.permission.POST_NOTIFICATIONS", i);
                } else if (!z && z3) {
                    this.mPermManager.revokeRuntimePermission(str, "android.permission.POST_NOTIFICATIONS", i, "PermissionHelper");
                }
                if (!z2 && z) {
                    i2 = 3;
                    int i3 = i2;
                    if (!z2) {
                        this.mPermManager.updatePermissionFlags(str, "android.permission.POST_NOTIFICATIONS", i3, 1, true, i);
                    } else {
                        this.mPermManager.updatePermissionFlags(str, "android.permission.POST_NOTIFICATIONS", i3, 0, true, i);
                    }
                }
                i2 = 35;
                int i32 = i2;
                if (!z2) {
                }
            }
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public void setNotificationPermission(PackagePermission packagePermission) {
        String str;
        if (packagePermission == null || (str = packagePermission.packageName) == null || isPermissionFixed(str, packagePermission.userId)) {
            return;
        }
        setNotificationPermission(packagePermission.packageName, packagePermission.userId, packagePermission.granted, true);
    }

    public boolean isPermissionFixed(String str, int i) {
        long clearCallingIdentity = Binder.clearCallingIdentity();
        boolean z = false;
        try {
            int permissionFlags = this.mPermManager.getPermissionFlags(str, "android.permission.POST_NOTIFICATIONS", i);
            return ((permissionFlags & 16) == 0 && (permissionFlags & 4) == 0) ? true : true;
        } catch (RemoteException e) {
            Slog.e("PermissionHelper", "Could not reach system server", e);
            return false;
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public boolean isPermissionUserSet(String str, int i) {
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            return (this.mPermManager.getPermissionFlags(str, "android.permission.POST_NOTIFICATIONS", i) & 3) != 0;
        } catch (RemoteException e) {
            Slog.e("PermissionHelper", "Could not reach system server", e);
            return false;
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public boolean isPermissionGrantedByDefaultOrRole(String str, int i) {
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            return (this.mPermManager.getPermissionFlags(str, "android.permission.POST_NOTIFICATIONS", i) & 32800) != 0;
        } catch (RemoteException e) {
            Slog.e("PermissionHelper", "Could not reach system server", e);
            return false;
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public final boolean packageRequestsNotificationPermission(String str, int i) {
        try {
            PackageInfo packageInfo = this.mPackageManager.getPackageInfo(str, 4096L, i);
            if (packageInfo != null) {
                return ArrayUtils.contains(packageInfo.requestedPermissions, "android.permission.POST_NOTIFICATIONS");
            }
            return false;
        } catch (RemoteException e) {
            Slog.e("PermissionHelper", "Could not reach system server", e);
            return false;
        }
    }

    /* loaded from: classes2.dex */
    public static class PackagePermission {
        public final boolean granted;
        public final String packageName;
        public final int userId;
        public final boolean userModifiedSettings;

        public PackagePermission(String str, int i, boolean z, boolean z2) {
            this.packageName = str;
            this.userId = i;
            this.granted = z;
            this.userModifiedSettings = z2;
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }
            PackagePermission packagePermission = (PackagePermission) obj;
            return this.userId == packagePermission.userId && this.granted == packagePermission.granted && this.userModifiedSettings == packagePermission.userModifiedSettings && Objects.equals(this.packageName, packagePermission.packageName);
        }

        public int hashCode() {
            return Objects.hash(this.packageName, Integer.valueOf(this.userId), Boolean.valueOf(this.granted), Boolean.valueOf(this.userModifiedSettings));
        }

        public String toString() {
            return "PackagePermission{packageName='" + this.packageName + "', userId=" + this.userId + ", granted=" + this.granted + ", userSet=" + this.userModifiedSettings + '}';
        }
    }
}
