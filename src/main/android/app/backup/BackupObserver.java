package android.app.backup;

import android.annotation.SystemApi;
@SystemApi
/* loaded from: classes.dex */
public abstract class BackupObserver {
    public void onUpdate(String currentBackupPackage, BackupProgress backupProgress) {
    }

    public void onResult(String currentBackupPackage, int status) {
    }

    public void backupFinished(int status) {
    }
}
