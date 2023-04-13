package com.android.server.backup.internal;

import android.content.pm.IPackageDataObserver;
import com.android.server.backup.UserBackupManagerService;
/* loaded from: classes.dex */
public class ClearDataObserver extends IPackageDataObserver.Stub {
    public UserBackupManagerService backupManagerService;

    public ClearDataObserver(UserBackupManagerService userBackupManagerService) {
        this.backupManagerService = userBackupManagerService;
    }

    public void onRemoveCompleted(String str, boolean z) {
        synchronized (this.backupManagerService.getClearDataLock()) {
            this.backupManagerService.setClearingData(false);
            this.backupManagerService.getClearDataLock().notifyAll();
        }
    }
}
