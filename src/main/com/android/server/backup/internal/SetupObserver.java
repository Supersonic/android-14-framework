package com.android.server.backup.internal;

import android.content.Context;
import android.database.ContentObserver;
import android.os.Handler;
import com.android.server.backup.KeyValueBackupJob;
import com.android.server.backup.UserBackupManagerService;
/* loaded from: classes.dex */
public class SetupObserver extends ContentObserver {
    public final Context mContext;
    public final UserBackupManagerService mUserBackupManagerService;
    public final int mUserId;

    public SetupObserver(UserBackupManagerService userBackupManagerService, Handler handler) {
        super(handler);
        this.mUserBackupManagerService = userBackupManagerService;
        this.mContext = userBackupManagerService.getContext();
        this.mUserId = userBackupManagerService.getUserId();
    }

    @Override // android.database.ContentObserver
    public void onChange(boolean z) {
        boolean isSetupComplete = this.mUserBackupManagerService.isSetupComplete();
        boolean z2 = isSetupComplete || UserBackupManagerService.getSetupCompleteSettingForUser(this.mContext, this.mUserId);
        this.mUserBackupManagerService.setSetupComplete(z2);
        synchronized (this.mUserBackupManagerService.getQueueLock()) {
            if (z2 && !isSetupComplete) {
                if (this.mUserBackupManagerService.isEnabled()) {
                    KeyValueBackupJob.schedule(this.mUserBackupManagerService.getUserId(), this.mContext, this.mUserBackupManagerService);
                    this.mUserBackupManagerService.scheduleNextFullBackupJob(0L);
                }
            }
        }
    }
}
