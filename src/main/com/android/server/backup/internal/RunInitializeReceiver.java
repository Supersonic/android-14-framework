package com.android.server.backup.internal;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.ArraySet;
import android.util.Slog;
import com.android.server.backup.UserBackupManagerService;
/* loaded from: classes.dex */
public class RunInitializeReceiver extends BroadcastReceiver {
    public final UserBackupManagerService mUserBackupManagerService;

    public RunInitializeReceiver(UserBackupManagerService userBackupManagerService) {
        this.mUserBackupManagerService = userBackupManagerService;
    }

    @Override // android.content.BroadcastReceiver
    public void onReceive(Context context, Intent intent) {
        if ("android.app.backup.intent.INIT".equals(intent.getAction())) {
            synchronized (this.mUserBackupManagerService.getQueueLock()) {
                ArraySet<String> pendingInits = this.mUserBackupManagerService.getPendingInits();
                Slog.v("BackupManagerService", "Running a device init; " + pendingInits.size() + " pending");
                if (pendingInits.size() > 0) {
                    this.mUserBackupManagerService.clearPendingInits();
                    this.mUserBackupManagerService.initializeTransports((String[]) pendingInits.toArray(new String[pendingInits.size()]), null);
                }
            }
        }
    }
}
