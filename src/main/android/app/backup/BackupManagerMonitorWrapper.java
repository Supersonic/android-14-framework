package android.app.backup;

import android.app.backup.IBackupManagerMonitor;
import android.p008os.Bundle;
import android.p008os.RemoteException;
/* loaded from: classes.dex */
class BackupManagerMonitorWrapper extends IBackupManagerMonitor.Stub {
    private final BackupManagerMonitor mMonitor;

    /* JADX INFO: Access modifiers changed from: package-private */
    public BackupManagerMonitorWrapper(BackupManagerMonitor monitor) {
        this.mMonitor = monitor;
    }

    @Override // android.app.backup.IBackupManagerMonitor
    public void onEvent(Bundle event) throws RemoteException {
        this.mMonitor.onEvent(event);
    }
}
