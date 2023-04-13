package com.android.server.policy;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.UserHandle;
import android.os.storage.StorageManagerInternal;
import android.provider.DeviceConfig;
import com.android.server.LocalServices;
import com.android.server.p011pm.pkg.AndroidPackage;
import java.util.Arrays;
import java.util.HashSet;
/* loaded from: classes2.dex */
public abstract class SoftRestrictedPermissionPolicy {
    public static final SoftRestrictedPermissionPolicy DUMMY_POLICY = new SoftRestrictedPermissionPolicy() { // from class: com.android.server.policy.SoftRestrictedPermissionPolicy.1
        @Override // com.android.server.policy.SoftRestrictedPermissionPolicy
        public boolean mayGrantPermission() {
            return true;
        }
    };
    public static final HashSet<String> sForcedScopedStorageAppWhitelist = new HashSet<>(Arrays.asList(getForcedScopedStorageAppWhitelist()));

    public int getExtraAppOpCode() {
        return -1;
    }

    public boolean mayAllowExtraAppOp() {
        return false;
    }

    public boolean mayDenyExtraAppOpIfGranted() {
        return false;
    }

    public abstract boolean mayGrantPermission();

    public static int getMinimumTargetSDK(Context context, ApplicationInfo applicationInfo, UserHandle userHandle) {
        PackageManager packageManager = context.getPackageManager();
        int i = applicationInfo.targetSdkVersion;
        String[] packagesForUid = packageManager.getPackagesForUid(applicationInfo.uid);
        if (packagesForUid != null) {
            for (String str : packagesForUid) {
                if (!str.equals(applicationInfo.packageName)) {
                    try {
                        i = Integer.min(i, packageManager.getApplicationInfoAsUser(str, 0, userHandle).targetSdkVersion);
                    } catch (PackageManager.NameNotFoundException unused) {
                    }
                }
            }
        }
        return i;
    }

    public static SoftRestrictedPermissionPolicy forPermission(Context context, ApplicationInfo applicationInfo, AndroidPackage androidPackage, UserHandle userHandle, String str) {
        final boolean z;
        final int i;
        final boolean z2;
        final boolean z3;
        final boolean z4;
        final boolean z5;
        final boolean z6;
        final boolean z7;
        final int i2;
        str.hashCode();
        final boolean z8 = false;
        if (!str.equals("android.permission.READ_EXTERNAL_STORAGE")) {
            if (str.equals("android.permission.WRITE_EXTERNAL_STORAGE")) {
                if (applicationInfo != null) {
                    boolean z9 = (context.getPackageManager().getPermissionFlags(str, applicationInfo.packageName, userHandle) & 14336) != 0;
                    i2 = getMinimumTargetSDK(context, applicationInfo, userHandle);
                    z8 = z9;
                } else {
                    i2 = 0;
                }
                return new SoftRestrictedPermissionPolicy() { // from class: com.android.server.policy.SoftRestrictedPermissionPolicy.3
                    @Override // com.android.server.policy.SoftRestrictedPermissionPolicy
                    public boolean mayGrantPermission() {
                        return z8 || i2 >= 29;
                    }
                };
            }
            return DUMMY_POLICY;
        }
        if (applicationInfo != null) {
            PackageManager packageManager = context.getPackageManager();
            StorageManagerInternal storageManagerInternal = (StorageManagerInternal) LocalServices.getService(StorageManagerInternal.class);
            int permissionFlags = packageManager.getPermissionFlags(str, applicationInfo.packageName, userHandle);
            boolean z10 = (permissionFlags & 14336) != 0;
            boolean hasLegacyExternalStorage = storageManagerInternal.hasLegacyExternalStorage(applicationInfo.uid);
            boolean hasUidRequestedLegacyExternalStorage = hasUidRequestedLegacyExternalStorage(applicationInfo.uid, context);
            boolean hasWriteMediaStorageGrantedForUid = hasWriteMediaStorageGrantedForUid(applicationInfo.uid, context);
            boolean hasPreserveLegacyExternalStorage = androidPackage.hasPreserveLegacyExternalStorage();
            i = getMinimumTargetSDK(context, applicationInfo, userHandle);
            z = z10;
            z2 = (permissionFlags & 16384) != 0;
            z3 = sForcedScopedStorageAppWhitelist.contains(applicationInfo.packageName);
            z5 = hasLegacyExternalStorage;
            z6 = hasUidRequestedLegacyExternalStorage;
            z4 = hasWriteMediaStorageGrantedForUid;
            z7 = hasPreserveLegacyExternalStorage;
        } else {
            z = false;
            i = 0;
            z2 = false;
            z3 = false;
            z4 = false;
            z5 = false;
            z6 = false;
            z7 = false;
        }
        return new SoftRestrictedPermissionPolicy() { // from class: com.android.server.policy.SoftRestrictedPermissionPolicy.2
            @Override // com.android.server.policy.SoftRestrictedPermissionPolicy
            public int getExtraAppOpCode() {
                return 87;
            }

            @Override // com.android.server.policy.SoftRestrictedPermissionPolicy
            public boolean mayGrantPermission() {
                return z || i >= 29;
            }

            @Override // com.android.server.policy.SoftRestrictedPermissionPolicy
            public boolean mayAllowExtraAppOp() {
                if (z2 || z3 || i >= 30) {
                    return false;
                }
                return z4 || z5 || z6;
            }

            @Override // com.android.server.policy.SoftRestrictedPermissionPolicy
            public boolean mayDenyExtraAppOpIfGranted() {
                if (i < 30) {
                    return !mayAllowExtraAppOp();
                }
                return z2 || z3 || !z7;
            }
        };
    }

    public static boolean hasUidRequestedLegacyExternalStorage(int i, Context context) {
        PackageManager packageManager = context.getPackageManager();
        String[] packagesForUid = packageManager.getPackagesForUid(i);
        if (packagesForUid == null) {
            return false;
        }
        UserHandle userHandleForUid = UserHandle.getUserHandleForUid(i);
        for (String str : packagesForUid) {
            if (packageManager.getApplicationInfoAsUser(str, 0, userHandleForUid).hasRequestedLegacyExternalStorage()) {
                return true;
            }
        }
        return false;
    }

    public static boolean hasWriteMediaStorageGrantedForUid(int i, Context context) {
        PackageManager packageManager = context.getPackageManager();
        String[] packagesForUid = packageManager.getPackagesForUid(i);
        if (packagesForUid == null) {
            return false;
        }
        for (String str : packagesForUid) {
            if (packageManager.checkPermission("android.permission.WRITE_MEDIA_STORAGE", str) == 0) {
                return true;
            }
        }
        return false;
    }

    public static String[] getForcedScopedStorageAppWhitelist() {
        String string = DeviceConfig.getString("storage_native_boot", "forced_scoped_storage_whitelist", "");
        return (string == null || string.equals("")) ? new String[0] : string.split(",");
    }
}
