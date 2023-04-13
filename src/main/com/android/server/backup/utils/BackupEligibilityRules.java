package com.android.server.backup.utils;

import android.app.compat.CompatChanges;
import android.content.Context;
import android.content.p000pm.PackageManagerInternal;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.content.pm.SigningInfo;
import android.os.UserHandle;
import android.os.UserManager;
import android.util.ArraySet;
import android.util.Slog;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.util.ArrayUtils;
import com.android.server.backup.SetUtils;
import com.android.server.backup.transport.TransportConnection;
import com.android.server.p011pm.PackageManagerShellCommandDataLoader;
import com.google.android.collect.Sets;
import java.util.Set;
/* loaded from: classes.dex */
public class BackupEligibilityRules {
    public static final Set<String> systemPackagesAllowedForNonSystemUsers;
    public static final Set<String> systemPackagesAllowedForProfileUser;
    public final int mBackupDestination;
    public boolean mIsProfileUser;
    public final PackageManager mPackageManager;
    public final PackageManagerInternal mPackageManagerInternal;
    public final int mUserId;

    static {
        ArraySet newArraySet = Sets.newArraySet(new String[]{"@pm@", PackageManagerShellCommandDataLoader.PACKAGE});
        systemPackagesAllowedForProfileUser = newArraySet;
        systemPackagesAllowedForNonSystemUsers = SetUtils.union(newArraySet, Sets.newArraySet(new String[]{"com.android.wallpaperbackup", "com.android.providers.settings"}));
    }

    public BackupEligibilityRules(PackageManager packageManager, PackageManagerInternal packageManagerInternal, int i, Context context, int i2) {
        this.mIsProfileUser = false;
        this.mPackageManager = packageManager;
        this.mPackageManagerInternal = packageManagerInternal;
        this.mUserId = i;
        this.mBackupDestination = i2;
        this.mIsProfileUser = ((UserManager) context.getSystemService(UserManager.class)).isProfile();
    }

    @VisibleForTesting
    public boolean appIsEligibleForBackup(ApplicationInfo applicationInfo) {
        if (isAppBackupAllowed(applicationInfo)) {
            if (UserHandle.isCore(applicationInfo.uid)) {
                if (this.mUserId != 0) {
                    if (this.mIsProfileUser && !systemPackagesAllowedForProfileUser.contains(applicationInfo.packageName)) {
                        return false;
                    }
                    if (!this.mIsProfileUser && !systemPackagesAllowedForNonSystemUsers.contains(applicationInfo.packageName)) {
                        return false;
                    }
                }
                if (applicationInfo.backupAgentName == null) {
                    return false;
                }
            }
            if (applicationInfo.packageName.equals("com.android.sharedstoragebackup") || applicationInfo.isInstantApp()) {
                return false;
            }
            return !appIsDisabled(applicationInfo);
        }
        return false;
    }

    public boolean isAppBackupAllowed(ApplicationInfo applicationInfo) {
        int i = applicationInfo.flags;
        boolean z = (32768 & i) != 0;
        int i2 = this.mBackupDestination;
        if (i2 != 0) {
            if (i2 == 1) {
                return (!((i & 1) != 0) && CompatChanges.isChangeEnabled(183147249L, applicationInfo.packageName, UserHandle.of(this.mUserId))) || z;
            } else if (i2 == 2) {
                String str = applicationInfo.packageName;
                if (str == null) {
                    Slog.w("BackupManagerService", "Invalid ApplicationInfo object");
                    return false;
                } else if (CompatChanges.isChangeEnabled(171032338L, str, UserHandle.of(this.mUserId))) {
                    if (PackageManagerShellCommandDataLoader.PACKAGE.equals(str)) {
                        return true;
                    }
                    int i3 = applicationInfo.flags;
                    boolean z2 = (i3 & 8) != 0;
                    boolean z3 = (2 & i3) != 0;
                    if (UserHandle.isCore(applicationInfo.uid) || z2) {
                        try {
                            return this.mPackageManager.getPropertyAsUser("android.backup.ALLOW_ADB_BACKUP", str, null, this.mUserId).getBoolean();
                        } catch (PackageManager.NameNotFoundException unused) {
                            Slog.w("BackupManagerService", "Failed to read allowAdbBackup property for + " + str);
                            return z;
                        }
                    }
                    return z3;
                } else {
                    return z;
                }
            } else {
                Slog.w("BackupManagerService", "Unknown operation type:" + this.mBackupDestination);
                return false;
            }
        }
        return z;
    }

    public boolean appIsRunningAndEligibleForBackupWithTransport(TransportConnection transportConnection, String str) {
        try {
            PackageInfo packageInfoAsUser = this.mPackageManager.getPackageInfoAsUser(str, 134217728, this.mUserId);
            ApplicationInfo applicationInfo = packageInfoAsUser.applicationInfo;
            if (appIsEligibleForBackup(applicationInfo) && !appIsStopped(applicationInfo) && !appIsDisabled(applicationInfo)) {
                if (transportConnection != null) {
                    try {
                        return transportConnection.connectOrThrow("AppBackupUtils.appIsRunningAndEligibleForBackupWithTransport").isAppEligibleForBackup(packageInfoAsUser, appGetsFullBackup(packageInfoAsUser));
                    } catch (Exception e) {
                        Slog.e("BackupManagerService", "Unable to ask about eligibility: " + e.getMessage());
                        return true;
                    }
                }
                return true;
            }
        } catch (PackageManager.NameNotFoundException unused) {
        }
        return false;
    }

    @VisibleForTesting
    public boolean appIsDisabled(ApplicationInfo applicationInfo) {
        int applicationEnabledState = this.mPackageManagerInternal.getApplicationEnabledState(applicationInfo.packageName, this.mUserId);
        if (applicationEnabledState != 0) {
            return applicationEnabledState == 2 || applicationEnabledState == 3 || applicationEnabledState == 4;
        }
        return !applicationInfo.enabled;
    }

    public boolean appIsStopped(ApplicationInfo applicationInfo) {
        return (applicationInfo.flags & 2097152) != 0;
    }

    @VisibleForTesting
    public boolean appGetsFullBackup(PackageInfo packageInfo) {
        ApplicationInfo applicationInfo = packageInfo.applicationInfo;
        return applicationInfo.backupAgentName == null || (applicationInfo.flags & 67108864) != 0;
    }

    public boolean appIsKeyValueOnly(PackageInfo packageInfo) {
        return !appGetsFullBackup(packageInfo);
    }

    public boolean signaturesMatch(Signature[] signatureArr, PackageInfo packageInfo) {
        boolean z;
        if (packageInfo == null || packageInfo.packageName == null) {
            return false;
        }
        if ((packageInfo.applicationInfo.flags & 1) != 0) {
            return true;
        }
        if (ArrayUtils.isEmpty(signatureArr)) {
            return false;
        }
        SigningInfo signingInfo = packageInfo.signingInfo;
        if (signingInfo == null) {
            Slog.w("BackupManagerService", "signingInfo is empty, app was either unsigned or the flag PackageManager#GET_SIGNING_CERTIFICATES was not specified");
            return false;
        }
        if (signatureArr.length == 1) {
            return this.mPackageManagerInternal.isDataRestoreSafe(signatureArr[0], packageInfo.packageName);
        }
        Signature[] apkContentsSigners = signingInfo.getApkContentsSigners();
        int length = apkContentsSigners.length;
        for (Signature signature : signatureArr) {
            int i = 0;
            while (true) {
                if (i >= length) {
                    z = false;
                    break;
                } else if (signature.equals(apkContentsSigners[i])) {
                    z = true;
                    break;
                } else {
                    i++;
                }
            }
            if (!z) {
                return false;
            }
        }
        return true;
    }

    public int getBackupDestination() {
        return this.mBackupDestination;
    }
}
