package com.android.server.rollback;

import android.content.rollback.PackageRollbackInfo;
import android.os.storage.StorageManager;
import android.util.Slog;
import com.android.internal.annotations.VisibleForTesting;
import com.android.server.p011pm.ApexManager;
import com.android.server.p011pm.Installer;
import java.util.ArrayList;
import java.util.List;
@VisibleForTesting
/* loaded from: classes2.dex */
public class AppDataRollbackHelper {
    public final ApexManager mApexManager;
    public final Installer mInstaller;

    public AppDataRollbackHelper(Installer installer) {
        this.mInstaller = installer;
        this.mApexManager = ApexManager.getInstance();
    }

    @VisibleForTesting
    public AppDataRollbackHelper(Installer installer, ApexManager apexManager) {
        this.mInstaller = installer;
        this.mApexManager = apexManager;
    }

    public void snapshotAppData(int i, PackageRollbackInfo packageRollbackInfo, int[] iArr) {
        int i2;
        for (int i3 : iArr) {
            if (isUserCredentialLocked(i3)) {
                Slog.v("RollbackManager", "User: " + i3 + " isn't unlocked, skipping CE userdata backup.");
                packageRollbackInfo.addPendingBackup(i3);
                i2 = 1;
            } else {
                i2 = 3;
            }
            doSnapshot(packageRollbackInfo, i3, i, i2);
        }
    }

    public boolean restoreAppData(int i, PackageRollbackInfo packageRollbackInfo, int i2, int i3, String str) {
        int i4;
        List pendingBackups = packageRollbackInfo.getPendingBackups();
        ArrayList pendingRestores = packageRollbackInfo.getPendingRestores();
        boolean z = true;
        if (pendingBackups != null && pendingBackups.indexOf(Integer.valueOf(i2)) != -1) {
            pendingBackups.remove(pendingBackups.indexOf(Integer.valueOf(i2)));
        } else if (isUserCredentialLocked(i2)) {
            pendingRestores.add(new PackageRollbackInfo.RestoreInfo(i2, i3, str));
        } else {
            z = false;
            i4 = 3;
            doRestoreOrWipe(packageRollbackInfo, i2, i, i3, str, i4);
            return z;
        }
        i4 = 1;
        doRestoreOrWipe(packageRollbackInfo, i2, i, i3, str, i4);
        return z;
    }

    public final boolean doSnapshot(PackageRollbackInfo packageRollbackInfo, int i, int i2, int i3) {
        if (packageRollbackInfo.isApex()) {
            if ((i3 & 2) != 0) {
                return this.mApexManager.snapshotCeData(i, i2, packageRollbackInfo.getPackageName());
            }
            return true;
        }
        try {
            return this.mInstaller.snapshotAppData(packageRollbackInfo.getPackageName(), i, i2, i3);
        } catch (Installer.InstallerException e) {
            Slog.e("RollbackManager", "Unable to create app data snapshot for: " + packageRollbackInfo.getPackageName() + ", userId: " + i, e);
            return false;
        }
    }

    public final boolean doRestoreOrWipe(PackageRollbackInfo packageRollbackInfo, int i, int i2, int i3, String str, int i4) {
        if (packageRollbackInfo.isApex()) {
            if (packageRollbackInfo.getRollbackDataPolicy() == 0 && (i4 & 2) != 0) {
                this.mApexManager.restoreCeData(i, i2, packageRollbackInfo.getPackageName());
            }
        } else {
            try {
                int rollbackDataPolicy = packageRollbackInfo.getRollbackDataPolicy();
                if (rollbackDataPolicy == 0) {
                    this.mInstaller.restoreAppDataSnapshot(packageRollbackInfo.getPackageName(), i3, str, i, i2, i4);
                } else if (rollbackDataPolicy == 1) {
                    this.mInstaller.clearAppData(null, packageRollbackInfo.getPackageName(), i, i4, 0L);
                }
            } catch (Installer.InstallerException e) {
                Slog.e("RollbackManager", "Unable to restore/wipe app data: " + packageRollbackInfo.getPackageName() + " policy=" + packageRollbackInfo.getRollbackDataPolicy(), e);
                return false;
            }
        }
        return true;
    }

    public void destroyAppDataSnapshot(int i, PackageRollbackInfo packageRollbackInfo, int i2) {
        try {
            this.mInstaller.destroyAppDataSnapshot(packageRollbackInfo.getPackageName(), i2, i, 3);
        } catch (Installer.InstallerException e) {
            Slog.e("RollbackManager", "Unable to delete app data snapshot for " + packageRollbackInfo.getPackageName(), e);
        }
    }

    public void destroyApexDeSnapshots(int i) {
        this.mApexManager.destroyDeSnapshots(i);
    }

    public void destroyApexCeSnapshots(int i, int i2) {
        if (isUserCredentialLocked(i)) {
            return;
        }
        this.mApexManager.destroyCeSnapshots(i, i2);
    }

    public boolean commitPendingBackupAndRestoreForUser(int i, Rollback rollback) {
        boolean z;
        boolean z2 = false;
        for (PackageRollbackInfo packageRollbackInfo : rollback.info.getPackages()) {
            List pendingBackups = packageRollbackInfo.getPendingBackups();
            boolean z3 = true;
            if (pendingBackups == null || pendingBackups.indexOf(Integer.valueOf(i)) == -1) {
                z = false;
            } else {
                z2 = true;
                z = true;
            }
            PackageRollbackInfo.RestoreInfo restoreInfo = packageRollbackInfo.getRestoreInfo(i);
            if (restoreInfo != null) {
                z2 = true;
            } else {
                z3 = false;
            }
            if (z && z3) {
                packageRollbackInfo.removePendingBackup(i);
                packageRollbackInfo.removePendingRestoreInfo(i);
            } else {
                if (z) {
                    int indexOf = pendingBackups.indexOf(Integer.valueOf(i));
                    if (doSnapshot(packageRollbackInfo, i, rollback.info.getRollbackId(), 2)) {
                        pendingBackups.remove(indexOf);
                    }
                }
                if (z3 && doRestoreOrWipe(packageRollbackInfo, i, rollback.info.getRollbackId(), restoreInfo.appId, restoreInfo.seInfo, 2)) {
                    packageRollbackInfo.removeRestoreInfo(restoreInfo);
                }
            }
        }
        return z2;
    }

    @VisibleForTesting
    public boolean isUserCredentialLocked(int i) {
        return StorageManager.isFileEncrypted() && !StorageManager.isUserKeyUnlocked(i);
    }
}
